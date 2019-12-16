package com.example.cupid.view

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.view.animation.AccelerateInterpolator
import com.example.cupid.R
import com.example.cupid.controller.QuizQuestionsController
import com.example.cupid.model.ModelModule
import com.example.cupid.model.domain.Question
import com.example.cupid.view.adapters.QuestionCardStackAdapter
import com.example.cupid.view.data.QuestionUI
import com.yuyakaido.android.cardstackview.*
import kotlinx.android.synthetic.main.activity_quiz_questions.*
import kotlinx.android.synthetic.main.dialog_waiting.*
import com.example.cupid.controller.ControllerModule.quizQuestionsController

class QuizQuestionsActivity :
    AppCompatActivity(),
    CardStackListener,
    QuizQuestionsView
{
    private val model = ModelModule.dataAccessLayer
    private val controller = quizQuestionsController()

    private var questionCardStackAdapter : QuestionCardStackAdapter? = null
    private var layoutManager : CardStackLayoutManager? = null
    private var cardStackView : CardStackView? = null

    private val mQuestions: ArrayList<QuestionUI> = arrayListOf()
    private var waitingDialog : Dialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_quiz_questions)
        window.decorView.systemUiVisibility= View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN

        controller.bind(this)
        controller.init()

    }

    override fun onCardDisappeared(view: View, position: Int) {
        if(layoutManager!!.topPosition == (mQuestions.size-1)){
            if(model.inInstructionMode()){
                launchWaitingPopup()
            }

        }
    }

    override fun launchWaitingPopup(){
        waitingDialog = Dialog(this)
        with(waitingDialog!!) {
            setContentView(R.layout.dialog_waiting)
            this.setCancelable(false)
            if(model.inInstructionMode()){
                Handler().postDelayed({
                    proceedToNextStage()

                }, 1500)

            }else{
                button_waiting_close.setOnClickListener{
                    this.dismiss()
                    controller.rejectTheConnection()
                }
            }

            window!!.attributes.windowAnimations = R.style.DialogAnimation
            window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            show()
        }
    }

    override fun launchRejectedPopup() {
        com.example.cupid.view.utils.launchRejectedPopup(this)
    }

    override fun onCardDragging(direction: Direction, ratio: Float) {}

    override fun onCardSwiped(direction: Direction) {}

    override fun onCardRewound() {}

    override fun onCardCanceled() {}

    override fun onCardAppeared(view: View, position: Int) {}

    override fun showQuestions(questions : ArrayList<Question>?) {
        mQuestions.clear()
        for (question in questions!!) {
            mQuestions.add(QuestionUI(
                questionText = question.questionText,
                choices = question.choices
            ))
        }

        /* RecyclerView configuration */
        cardStackView = quiz_card_stack_view

        layoutManager = CardStackLayoutManager(this,this)
        layoutManager!!.setSwipeableMethod(SwipeableMethod.Automatic)
        layoutManager!!.setStackFrom(StackFrom.Top)
        layoutManager!!.setSwipeThreshold(1.0f)

        val setting = SwipeAnimationSetting.Builder()
            .setDirection(Direction.Right)
            .setDuration(Duration.Slow.duration) // Duration.Normal.duration
            .setInterpolator(AccelerateInterpolator())
            .build()

        layoutManager!!.setSwipeAnimationSetting(setting)
        cardStackView!!.layoutManager = layoutManager

        questionCardStackAdapter = QuestionCardStackAdapter(mQuestions, cardStackView, this, controller)
        cardStackView!!.adapter = questionCardStackAdapter
    }

    override fun proceedToNextStage() {
        if (waitingDialog != null)
            waitingDialog!!.dismiss()
        val myIntent = Intent(
            this@QuizQuestionsActivity,
            QuizResultsActivity::class.java
        )

        this@QuizQuestionsActivity.startActivity(myIntent)
    }

    override fun onBackPressed() {

    }
}
