package com.example.cupid.view

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.example.cupid.R
import com.example.cupid.controller.QuizQuestionsController
import com.example.cupid.model.ModelModule
import com.example.cupid.model.domain.Question
import com.example.cupid.view.adapters.QuestionCardStackAdapter
import com.example.cupid.view.data.QuestionUI
import com.yuyakaido.android.cardstackview.*
import kotlinx.android.synthetic.main.activity_quiz_questions.*
import kotlinx.android.synthetic.main.dialog_waiting.*

class QuizQuestionsActivity :
    AppCompatActivity(),
    CardStackListener,
    QuizQuestionsView
{
    private val model = ModelModule.dataAccessLayer
    private val controller = QuizQuestionsController(model)

    private var questionCardStackAdapter : QuestionCardStackAdapter? = null
    private var layoutManager : CardStackLayoutManager? = null
    private var cardStackView : CardStackView? = null

    private val mQuestions: ArrayList<QuestionUI> = arrayListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_quiz_questions)
        window.decorView.systemUiVisibility= View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN

        controller.bind(this)
        controller.init()
    }

    override fun onCardDisappeared(view: View, position: Int) {
        if(layoutManager!!.topPosition == (mQuestions.size-1)){
            launchWaitingPopup()
        }
    }

    private fun launchWaitingPopup(){
        with( Dialog(this)) {
            setContentView(R.layout.dialog_waiting)
            button_waiting_close.setOnClickListener{
                this.dismiss()

                /*TODO normally just dismiss, this is for testing purposes*/

                val myIntent = Intent(
                    this@QuizQuestionsActivity,
                    QuizResultsActivity::class.java
                )

                //myIntent.putExtra("key", value) //Optional parameters
                this@QuizQuestionsActivity.startActivity(myIntent)
                /**/
            }
            window!!.attributes.windowAnimations = R.style.DialogAnimation
            window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            show()
        }
    }

    override fun onCardDragging(direction: Direction, ratio: Float) {}

    override fun onCardSwiped(direction: Direction) {}

    override fun onCardRewound() {}

    override fun onCardCanceled() {}

    override fun onCardAppeared(view: View, position: Int) {}

    override fun showQuestions(questions : ArrayList<Question>?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        mQuestions.add(QuestionUI (
            questionText = "You win a lottery! What do you do with the money?",
            choices = arrayListOf(
                "Spend it now!",
                "Better save it.",
                "Give it away.",
                "")
        ))

        mQuestions.add(QuestionUI (
            questionText = "Question2",
            choices = arrayListOf(
                "A",
                "B",
                "C",
                "D")
        ))

        mQuestions.add(QuestionUI (
            questionText = "Question3",
            choices = arrayListOf("A","B","C","D")
        ))

        // TODO replace dummy data
        /* RecyclerView configuration */
        cardStackView = quiz_card_stack_view

        layoutManager = CardStackLayoutManager(this,this)
        layoutManager!!.setSwipeableMethod(SwipeableMethod.Automatic)
        layoutManager!!.setStackFrom(StackFrom.Top)
        layoutManager!!.setSwipeThreshold(1.0f)
        cardStackView!!.layoutManager = layoutManager

        questionCardStackAdapter =
            QuestionCardStackAdapter(mQuestions, cardStackView, this)
        cardStackView!!.adapter = questionCardStackAdapter
    }
}
