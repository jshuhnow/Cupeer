package com.example.cupid.controller

import android.content.Context
import android.util.Log
import com.example.cupid.R
import com.example.cupid.model.DataAccessLayer
import com.example.cupid.model.domain.Account
import com.example.cupid.model.domain.NearbyPayload
import com.example.cupid.model.domain.ReplyToken
import com.example.cupid.model.observer.QueueObserver
import com.example.cupid.view.MyConnectionService
import com.example.cupid.view.views.QuizResultsView
import com.example.cupid.view.utils.launchInstructionPopup

class QuizResultsController(
    private val model : DataAccessLayer
) : QueueObserver {
    private lateinit var view : QuizResultsView
    private val mConnectionService: MyConnectionService = MyConnectionService.getInstance()

    companion object {
        const val TAG = "QuizResultsController"
        const val STAGE = 2
    }
    fun bind(quizQuestionsView: QuizResultsView) {
        view = quizQuestionsView
    }

    fun init() {
        view.renderAnswers(model.getQuestions(),
            model.getUserAccount() as Account,
            model.getUserAnswers(),
            model.getPartnerAccount() as Account,
            model.getPartnerAnswers()
        )

        if(model.inInstructionMode()){
            launchInstructionPopup(view as Context,
                listOf((view as Context).resources.getString(R.string.demo_text_results1),
                    (view as Context).resources.getString(R.string.demo_text_results2)))
        }

    }

    fun rejectTheConnection() {
        mConnectionService.send(ReplyToken(false, QuizQuestionsController.STAGE))
        mConnectionService.myDisconnect()
    }

    override fun newElementArrived(nearbyPayload: NearbyPayload) {
        if (nearbyPayload.type == "ReplyToken") {
            processReplyToken(nearbyPayload.obj as ReplyToken)
        }
    }

    fun processReplyToken(replyToken : ReplyToken) {
        if (replyToken.stage == STAGE) {
            if (replyToken.isAccepted) {
                view.proceedToNextStage()
            } else {
                // go back
            }
        } else {
            Log.d(MainController.TAG, "ReplyToken of unexpected stage")
        }
    }
    fun proceedToNextStage() {
        mConnectionService.send(ReplyToken(true, STAGE))

        val res = mConnectionService.pullNearbyPayload(this)
        if (res != null) {
            val replyToken = res.obj as ReplyToken
            processReplyToken(replyToken)
        } else {
            view.launchWaitingPopup()
        }
    }
}