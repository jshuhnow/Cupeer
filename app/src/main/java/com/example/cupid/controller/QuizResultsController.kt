package com.example.cupid.controller

import android.content.Context
import android.os.Handler
import com.example.cupid.R
import com.example.cupid.model.DataAccessLayer
import com.example.cupid.model.domain.Account
import com.example.cupid.model.domain.NearbyPayload
import com.example.cupid.model.domain.ReplyToken
import com.example.cupid.view.MyConnectionService
import com.example.cupid.view.views.QuizResultsView
import com.example.cupid.view.utils.launchInstructionPopup

class QuizResultsController(
    private val model : DataAccessLayer
) : AbstractNearbyController() {
    private lateinit var view: QuizResultsView
    private var mHasAccepted = false

    companion object {
        const val TAG = "QuizResultsController"
        const val STAGE = 2
    }

    fun bind(quizQuestionsView: QuizResultsView) {
        view = quizQuestionsView
    }

    fun reset() {
        mHasAccepted = false

        view.renderAnswers(
            model.getQuestions(),
            model.getUserAccount() as Account,
            model.getUserAnswers(),
            model.getPartnerAccount() as Account,
            model.getPartnerAnswers()
        )

        if (model.inInstructionMode()) {
            launchInstructionPopup(
                view as Context,
                listOf(
                    (view as Context).resources.getString(R.string.demo_text_results1),
                    (view as Context).resources.getString(R.string.demo_text_results2)
                )
            )
        }
    }
    fun init() {

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

        mLock.lock()
        mHasAccepted = true
        mLock.unlock()

        mConnectionService.conditionalPull()?.let { processNearbyPayload(it) }
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

    override fun processNearbyPayload(nearbyPayload: NearbyPayload) {
        assert(nearbyPayload.type == "ReplyToken")
        val replyToken = nearbyPayload.obj as ReplyToken
        if (replyToken.isAccepted) {
            proceedToNextStage()
        } else {
            view.launchRejectedPopup()
        }
    }

    override val mReceivingCondition: (NearbyPayload) -> Boolean
        get() = {
            when (it.type) {

                "ReplyToken" -> {
                    val replyToken = (it.obj as ReplyToken)
                    mHasAccepted || !replyToken.isAccepted
                }
                else -> false
            }
        }
}