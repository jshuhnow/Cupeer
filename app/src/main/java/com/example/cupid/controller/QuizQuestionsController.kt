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
    private lateinit var view : QuizQuestionsView

    override val mConnectionService: MyConnectionService = MyConnectionService.getInstance()

    companion object {
        const val TAG = "QuizQuestionsController"
        const val STAGE = 1
    }

    fun bind(quizQuestionsView: QuizQuestionsView) {
        view = quizQuestionsView
    }

    fun init() {
        view.showQuestions(model.getQuestions())

        if(model.inInstructionMode()){
            launchInstructionPopup(view as Context,
                listOf((view as Context).resources.getString(R.string.demo_text_discovered1),
                    (view as Context).resources.getString(R.string.demo_text_discovered2)))
        }

    }

    fun chooseAnswer(questionId : Int, answerId : Int) {
        model.updateUserAnswer(questionId, answerId)

        if (!model.inInstructionMode()){
            mConnectionService.send(Answer(questionId, answerId))
            if (model.getUserAnswers().size >= 3) {
                view.launchWaitingPopup()
            }
        }

    }

    private fun answerArrived(answer : Answer) {
        model.updatePartnerAnswer(answer.questionId, answer.answerId)
    }


    override fun processNearbyPayload(nearbyPayload: NearbyPayload) {
        if (nearbyPayload.type == "Answer") {
            val answer = nearbyPayload.obj as Answer
            answerArrived(answer)
        } else if (nearbyPayload.type == "ReplyToken") {
            val replyToken = nearbyPayload.obj as ReplyToken

            // only accepts isAccepted
            assert(replyToken.isAccepted)
            view.launchWaitingPopup()
        }
    }

    override fun proceedToNextStage() {
        TODO("NOT IMPLEMENTED")
    }

    override fun rejectTheConnection() {
        if(model.inInstructionMode()){
            return
        }
        mConnectionService.send(ReplyToken(false, MainController.STAGE))
        // Wait until the other party receive the ReplyToken
        Handler().postDelayed({
            mConnectionService.myDisconnect()
        }, 3000)
    }

    override fun waitForProceeding() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun connectionRejected() {
        view.launchRejectedPopup()
    }

    override val mReceivingCondition: (NearbyPayload) -> Boolean
        get() = {
            when(it.type) {
                "ReplyToken" -> (it.obj as ReplyToken).stage == STAGE
                else -> false
            }
        }

    override fun newPayloadReceived() {
        mConnectionService.conditionalPull()?.let() { processNearbyPayload(it) }
    }

    override fun haltPayloadReceived() {
        view.launchRejectedPopup()
    }

}