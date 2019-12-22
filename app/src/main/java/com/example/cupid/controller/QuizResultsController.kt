package com.example.cupid.controller

import android.content.Context
import com.example.cupid.R
import com.example.cupid.model.DataAccessLayer
import com.example.cupid.model.domain.Account
import com.example.cupid.model.domain.NearbyPayload
import com.example.cupid.model.domain.ReplyToken
import com.example.cupid.view.MyConnectionService
import com.example.cupid.view.QuizResultsView
import com.example.cupid.view.utils.launchInstructionPopup

class QuizResultsController(
    private val model : DataAccessLayer
) : AbstractNearbyController() {
    private lateinit var view : QuizResultsView
    override val mConnectionService: MyConnectionService = MyConnectionService.getInstance()

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

    override fun rejectTheConnection() {
        mConnectionService.send(ReplyToken(false, QuizQuestionsController.STAGE))
        mConnectionService.myDisconnect()
    }

    override fun waitForProceeding() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun connectionRejected() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun processNearbyPayload(nearbyPayload: NearbyPayload) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override val mReceivingCondition: (NearbyPayload) -> Boolean
        get() = TODO("not implemented") //To change initializer of created properties use File | Settings | File Templates.

    override fun newPayloadReceived() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun haltPayloadReceived() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }


    override fun proceedToNextStage() {
        mConnectionService.send(ReplyToken(true, STAGE))
        view.launchWaitingPopup()
    }
}