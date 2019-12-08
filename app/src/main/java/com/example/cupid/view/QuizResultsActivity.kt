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
import com.example.cupid.view.adapters.ResultListAdapter
import com.example.cupid.view.data.ResultUI
import com.example.cupid.view.utils.launchRejectedPopup
import com.example.cupid.view.utils.returnToMain
import kotlinx.android.synthetic.main.activity_quiz_results.*
import kotlinx.android.synthetic.main.dialog_waiting.*

class QuizResultsActivity : AppCompatActivity() {

    private var resultListAdapter : ResultListAdapter? = null
    private var layoutManager : RecyclerView.LayoutManager? = null
    private var resultRecyclerView : RecyclerView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_quiz_results)
        window.decorView.systemUiVisibility= View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN

        // Todo get data from repository
        // Todo change the data into the UI format shown below

        val results: ArrayList<ResultUI> = arrayListOf()

        results.add(
            ResultUI (
                question_text = "Question Text",
                answerYou = "AAAAA",
                answerPartner = "BBBBB",
                nameYou = "You",
                namePartner = "Bob",
                iconIdPartner = 2,
                iconIdYou = 3
            )
        )

        results.add(
            ResultUI (
                question_text = "Question Text",
                answerYou = "AAAAA",
                answerPartner = "BBBBB",
                nameYou = "You",
                namePartner = "Bob",
                iconIdPartner = 2,
                iconIdYou = 3
            )
        )

        results.add(
            ResultUI (
                question_text = "Question Text",
                answerYou = "AAAAA",
                answerPartner = "BBBBB",
                nameYou = "You",
                namePartner = "Bob",
                iconIdPartner = 2,
                iconIdYou = 3
            )
        )

        /* RecyclerView configuration */
        resultRecyclerView = result_recycler_view

        layoutManager = LinearLayoutManager(this)
        (layoutManager as LinearLayoutManager).orientation = LinearLayoutManager.VERTICAL
        resultRecyclerView!!.layoutManager = layoutManager

        resultListAdapter = ResultListAdapter(results, this)
        resultRecyclerView!!.adapter = resultListAdapter

        setClickListeners()
    }

    private fun setClickListeners(){
        button_result_connect.setOnClickListener{
            launchWaitingPopup(it)
        }

        button_result_cancel.setOnClickListener{
            //TODO send cancel message to other person

            returnToMain(this)
        }
    }


    private fun launchWaitingPopup(v: View){

        val waitingDialog = Dialog(this)
        waitingDialog.setContentView(R.layout.dialog_waiting)


        waitingDialog.button_waiting_close.setOnClickListener{
            waitingDialog.dismiss()

            /*TODO normally just dismiss, this is for testing purposes -> move this */

            val myIntent = Intent(this, ChatActivity::class.java)
            //myIntent.putExtra("key", value) //Optional parameters
            this.startActivity(myIntent)
            /**/
        }


        waitingDialog.window!!.attributes.windowAnimations = R.style.DialogAnimation
        waitingDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        waitingDialog.show()
    }
}
