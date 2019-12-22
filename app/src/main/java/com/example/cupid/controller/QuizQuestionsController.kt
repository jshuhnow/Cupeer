package com.example.cupid.controller

import android.content.Context
import android.os.Handler
import com.example.cupid.R
import com.example.cupid.model.DataAccessLayer
import com.example.cupid.model.domain.*
import com.example.cupid.view.MyConnectionService
import com.example.cupid.view.QuizQuestionsView
import com.example.cupid.view.utils.launchInstructionPopup

class QuizQuestionsController (
    private val model : DataAccessLayer
) : AbstractNearbyController() {
    private lateinit var view: QuizQuestionsView

    companion object {
        const val TAG = "QuizQuestionsController"
        const val STAGE = 1
    }

    fun bind(quizQuestionsView: QuizQuestionsView) {
        view = quizQuestionsView
    }

    fun reset() {
        mLock.lock()
        model.clearAnswers()
        mLock.unlock()

        view.showQuestions(model.getQuestions())

        if (model.inInstructionMode()) {
            launchInstructionPopup(
                view as Context,
                listOf(
                    (view as Context).resources.getString(R.string.demo_text_discovered1),
                    (view as Context).resources.getString(R.string.demo_text_discovered2)
                )
            )
        }
    }

    fun init() {

    }

    fun chooseAnswer(questionId: Int, answerId: Int) {
        model.updateUserAnswer(questionId, answerId)

        if (!model.inInstructionMode()) {
            mConnectionService.send(Answer(questionId, answerId))

            mLock.lock()
            if (model.getUserAnswers().size >= 3) {
                if (model.getPartnerAnswers().size >= 3) {
                    view.proceedToNextStage()
                } else {
                    view.launchWaitingPopup()
                }
            }
            mLock.unlock()
        }
    }

    private fun processAnswer(answer: Answer) {
        mLock.lock()
        model.updatePartnerAnswer(answer.questionId, answer.answerId)
        if (model.getPartnerAnswers().size >= 3) {
            if (model.getUserAnswers().size >=3) {
                view.proceedToNextStage()
            }
        }
        mLock.unlock()
    }


    override fun processNearbyPayload(nearbyPayload: NearbyPayload) {
        when(nearbyPayload.type) {
            "Answer" -> {
                val answer = nearbyPayload.obj as Answer
                processAnswer(answer)
            }
            else -> assert(false)
        }
    }

    override fun proceedToNextStage() {
        view.proceedToNextStage()
    }

    override fun rejectTheConnection() {
        if (model.inInstructionMode()) {
            return
        }
        mConnectionService.send(ReplyToken(false, STAGE))
        // Wait until the other party receive the ReplyToken
        Handler().postDelayed({
            mConnectionService.disconnect()
        }, 3000)
    }

    override fun waitForProceeding() {
        mConnectionService.send(ReplyToken(true, STAGE))
        view.launchWaitingPopup()
    }

    override fun registerNearbyPayloadListener() {
        super.registerNearbyPayloadListener(this)
    }

    override fun connectionRejected() {
        view.launchRejectedPopup()
    }

    override fun newPayloadReceived() {
        mConnectionService.conditionalPull()?.let { processNearbyPayload(it) }
    }

    override val mReceivingCondition: (NearbyPayload) -> Boolean
    get() = {
        when (it.type) {
            "Answer" -> true
            else -> false
        }
    }
}