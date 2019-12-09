package com.example.cupid.view

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
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
import com.example.cupid.view.utils.launchRejectedPopup
import com.example.cupid.view.utils.returnToMain
import kotlinx.android.synthetic.main.activity_quiz_results.*
import kotlinx.android.synthetic.main.dialog_waiting.*

class QuizResultsActivity : AppCompatActivity(), QuizResultsView {

    private var resultListAdapter : ResultListAdapter? = null
    private var layoutManager : RecyclerView.LayoutManager? = null
    private var resultRecyclerView : RecyclerView? = null

    private var model : DataAccessLayer = ModelModule.dataAccessLayer
    private var controller : QuizResultsController = QuizResultsController(model)

    private var waitingDialog : Dialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_quiz_results)
        window.decorView.systemUiVisibility= View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN

        controller.bind(this)
        controller.init()

        setClickListeners()
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
            proceedToNextStage()
        }

        button_result_cancel.setOnClickListener{
            //TODO send cancel message to other person
            controller.rejectTheConnection()
            returnToMain(this)
        }
    }


    override fun launchWaitingPopup(){

        waitingDialog = Dialog(this)
        waitingDialog!!.setContentView(R.layout.dialog_waiting)


        waitingDialog!!.button_waiting_close.setOnClickListener{
            waitingDialog!!.dismiss()
            controller.rejectTheConnection()
        }


        waitingDialog!!.window!!.attributes.windowAnimations = R.style.DialogAnimation
        waitingDialog!!.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        waitingDialog!!.show()
    }

    override fun proceedToNextStage() {
        if (waitingDialog != null) {
            waitingDialog!!.dismiss()
        }
        val myIntent = Intent(this, ChatActivity::class.java)
        this.startActivity(myIntent)

    }
}
