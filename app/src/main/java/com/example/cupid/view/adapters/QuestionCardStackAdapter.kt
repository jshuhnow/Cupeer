package com.example.cupid.view.adapters

import android.app.Activity
import android.graphics.Color
import com.example.cupid.R
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.cupid.controller.QuizQuestionsController
import com.example.cupid.view.data.QuestionUI
import com.yuyakaido.android.cardstackview.CardStackView
import kotlinx.android.synthetic.main.item_question.view.*


class QuestionCardStackAdapter(
    private var questions: List<QuestionUI> = emptyList(),
    private var cardViewStack : CardStackView? =  null,
    private var context : Activity? = null,
    private var controller : QuizQuestionsController
) : RecyclerView.Adapter<QuestionCardStackAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return ViewHolder(
            inflater.inflate(
                R.layout.item_question,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val question = questions[position]
        holder.question.text = question.questionText

        val questionViews = arrayListOf(
            holder.answer1,
            holder.answer2,
            holder.answer3,
            holder.answer4
        )

        val bgColors = listOf(
            R.color.gradientEndActive,
            R.color.gradientMiddleActive,
            R.color.gradientStartActive)

        holder.itemView.layout_quiz_question.background = context!!.getDrawable(bgColors[position])


        questionViews.forEachIndexed { i, element ->
            renderAnswerOption(element,question, position, i)
        }

    }

    private fun renderAnswerOption(textView : TextView,question: QuestionUI, position: Int, answerIndex : Int){

        textView.text = question.choices[answerIndex]

        if (question.choices[answerIndex] == ""){
            textView.setBackgroundColor(Color.TRANSPARENT)
            return
        }

        textView.setOnClickListener {
            controller.chooseAnswer(position, answerIndex)
            cardViewStack!!.swipe()
        }

    }

    override fun getItemCount(): Int {
        return questions.size
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val question: TextView = view.text_quiz_question
        val answer1 : TextView = view.text_quiz_answer1
        val answer2 : TextView = view.text_quiz_answer2
        val answer3 : TextView = view.text_quiz_answer3
        val answer4 : TextView = view.text_quiz_answer4
    }

}