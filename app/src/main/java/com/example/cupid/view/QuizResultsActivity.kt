package com.example.cupid.view

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.cupid.R
import com.example.cupid.controller.QuizResultsController
import com.example.cupid.model.DataAccessLayer
import com.example.cupid.model.ModelModule
import com.example.cupid.model.domain.Account
import com.example.cupid.model.domain.Answer
import com.example.cupid.model.domain.Question
import com.example.cupid.view.adapters.ResultListAdapter
import com.example.cupid.view.data.ResultUI

import com.example.cupid.view.utils.returnToMain
import kotlinx.android.synthetic.main.activity_quiz_results.*
import kotlinx.android.synthetic.main.dialog_waiting.*
import com.example.cupid.controller.ControllerModule.quizResultsController
import com.example.cupid.view.views.QuizResultsView

class QuizResultsActivity : AppCompatActivity(), QuizResultsView {

    private var resultListAdapter : ResultListAdapter? = null
    private var layoutManager : RecyclerView.LayoutManager? = null
    private var resultRecyclerView : RecyclerView? = null

    private var model : DataAccessLayer = ModelModule.dataAccessLayer
    private var controller : QuizResultsController = quizResultsController()

    private var waitingDialog : Dialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_quiz_results)
        window.decorView.systemUiVisibility= View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN

        setClickListeners()

        controller.bind(this)
        controller.init()

    }

    override fun renderAnswers(questions : ArrayList<Question>?,
                               myAccount: Account,
                               myAnswer : ArrayList<Answer>?,
                               partnerAccount: Account,
                               partnerAnswer : ArrayList<Answer>?) {

        val results: ArrayList<ResultUI> = arrayListOf()

        questions!!.forEachIndexed { i, question ->
            results.add(
                ResultUI (
                    question_text = question.questionText,
                    answerYou = question.choices[myAnswer!![i].answerId],
                    answerPartner = question.choices[partnerAnswer!![i].answerId],
                    nameYou = myAccount.name,
                    namePartner = partnerAccount.name,
                    iconIdPartner = partnerAccount.avatarId,
                    iconIdYou = myAccount.avatarId
                )
            )
        }


        /* RecyclerView configuration */
        resultRecyclerView = result_recycler_view

        layoutManager = LinearLayoutManager(this)
        (layoutManager as LinearLayoutManager).orientation = LinearLayoutManager.VERTICAL
        resultRecyclerView!!.layoutManager = layoutManager

        resultListAdapter = ResultListAdapter(results, this)
        resultRecyclerView!!.adapter = resultListAdapter

    }

    private fun setClickListeners(){
        button_result_connect.setOnClickListener{
            controller.waitForProceeding()
        }

        button_result_cancel.setOnClickListener{
            if(!model.inInstructionMode()) {
                controller.rejectTheConnection()
            }
            returnToMain(this)
        }
    }


    override fun launchWaitingPopup(){

        waitingDialog = Dialog(this)
        waitingDialog!!.setContentView(R.layout.dialog_waiting)
        waitingDialog!!.setCancelable(false)
        if(model.inInstructionMode()){

            Handler().postDelayed({
                proceedToNextStage()

            }, 1500)

        }else{
            waitingDialog!!.button_waiting_close.setOnClickListener{
                waitingDialog!!.dismiss()
                controller.rejectTheConnection()
                returnToMain(this)
            }
        }



        waitingDialog!!.window!!.attributes.windowAnimations = R.style.DialogAnimation
        waitingDialog!!.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        waitingDialog!!.show()
    }

    override fun launchRejectedPopup() {
        com.example.cupid.view.utils.launchRejectedPopup(this)
    }

    override fun proceedToNextStage() {
        dismissPopups()
        val myIntent = Intent(this, ChatActivity::class.java)
        this.startActivity(myIntent)

    }

    override fun dismissPopups() {
        waitingDialog?.dismiss()
    }

    override fun onBackPressed() {

    }

    override fun onResume() {
        super.onResume()
        controller.registerNearbyPayloadListener()
        controller.reset()
    }

    override fun onPause() {
        super.onPause()
        controller.releaseNearbyPayloadListener()
    }

    override fun onDestroy() {
        super.onDestroy()
        dismissPopups()
    }
}
