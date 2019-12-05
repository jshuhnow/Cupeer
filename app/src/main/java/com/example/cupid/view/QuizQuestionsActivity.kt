package com.example.cupid.view

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.example.cupid.R
import com.example.cupid.view.data.QuestionUI
import com.yuyakaido.android.cardstackview.*
import kotlinx.android.synthetic.main.activity_quiz_questions.*
import kotlinx.android.synthetic.main.dialog_waiting.*

class QuizQuestionsActivity : AppCompatActivity(), CardStackListener {

    private var questionCardStackAdapter : QuestionCardStackAdapter? = null
    private var layoutManager : CardStackLayoutManager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_quiz_questions)

        val cardStackView = quiz_card_stack_view
        layoutManager = CardStackLayoutManager(this,this)

        window.decorView.systemUiVisibility= View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN

        layoutManager!!.setSwipeableMethod(SwipeableMethod.Automatic)
        layoutManager!!.setStackFrom(StackFrom.Top)
        layoutManager!!.setSwipeThreshold(1.0f)
        cardStackView.layoutManager = layoutManager


        // dummy data

        val questions: ArrayList<QuestionUI> = arrayListOf()

        questions.add(QuestionUI (
            questionText = "You win a lottery! What do you do with the money?",
            choices = arrayListOf(
                "A",
                "B",
                "C",
                "D")
        ))

        questions.add(QuestionUI (
            questionText = "Question2",
            choices = arrayListOf(
                "Spend it now!",
                "Better save it.",
                "Give it away.",
                "")
        ))

        questions.add(QuestionUI (
            questionText = "Question3",
            choices = arrayListOf("A","B","C","D")
        ))

        questionCardStackAdapter = QuestionCardStackAdapter(questions, cardStackView, this)
        cardStackView.adapter = questionCardStackAdapter


    }


    override fun onCardDisappeared(view: View, position: Int) {
        if(layoutManager!!.topPosition == 2){
            launchWaitingPopup(view)
        }
    }

    private fun launchWaitingPopup(v: View){

        val waitingDialog = Dialog(this)
        waitingDialog.setContentView(R.layout.dialog_waiting)


        waitingDialog.button_waiting_close.setOnClickListener{
            waitingDialog.dismiss()

            /*normally just dismiss, this is for testing purposes -> REMOVE EXIT BUTTON*/

            val myIntent = Intent(this, QuizResultsActivity::class.java)
            //myIntent.putExtra("key", value) //Optional parameters
            this.startActivity(myIntent)
            /**/
        }


        waitingDialog.window!!.attributes.windowAnimations = R.style.DialogAnimation
        waitingDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        waitingDialog.show()
    }

    override fun onCardDragging(direction: Direction, ratio: Float) {}

    override fun onCardSwiped(direction: Direction) {}

    override fun onCardRewound() {}

    override fun onCardCanceled() {}

    override fun onCardAppeared(view: View, position: Int) {}
}
