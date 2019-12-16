package com.example.cupid.controller

import android.content.Context
import android.util.Log
import com.example.cupid.R
import com.example.cupid.model.DataAccessLayer
import com.example.cupid.model.domain.*
import com.example.cupid.model.observer.QueueObserver
import com.example.cupid.view.MyConnectionService
import com.example.cupid.view.QuizQuestionsView
import com.example.cupid.view.utils.launchInstructionPopup

class QuizQuestionsController (
    private val model : DataAccessLayer
) : NearbyController {
    private lateinit var view : QuizQuestionsView
    private val mConnectionService: MyConnectionService = MyConnectionService.getInstance()

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
                proceedToNextStage()
            }
        }

    }

    override fun newElementArrived(nearbyPayload: NearbyPayload) {
        if (nearbyPayload.type == "Answer") {
            val answer = nearbyPayload.obj as Answer
            answerArrived(answer)
        } else if (nearbyPayload.type == "ReplyToken") {
            processReplyToken(nearbyPayload.obj as ReplyToken)
        }
    }

    fun answerArrived(answer : Answer) {
        model.updatePartnerAnswer(answer.questionId, answer.answerId)
    }
    override fun processReplyToken(replyToken : ReplyToken) {
        if (replyToken.stage <= STAGE) {
            if (replyToken.isAccepted) {
                view.proceedToNextStage()
            } else {
                // go back
            }
        } else {
            Log.d(MainController.TAG, "ReplyToken of unexpected stage")
        }
    }

    override fun rejectTheConnection() {
        mConnectionService.send(ReplyToken(false, STAGE))
        mConnectionService.myDisconnect()
    }

    override fun proceedToNextStage() {
        mConnectionService.send(ReplyToken(true, STAGE))
        for (i in 0 until 3) {
            val res = mConnectionService.pullNearbyPayload(this)
            if (res != null) {
                val answer = res.obj as Answer
                answerArrived(answer)
            }
        }
        val res = mConnectionService.pullNearbyPayload(this)
        if (res != null) {
            val replyToken = res.obj as ReplyToken
            processReplyToken(replyToken)
        } else {
            view.launchWaitingPopup()
        }
    }
}