package com.example.cupid.view

import android.app.Activity
import android.content.Context
import android.util.Log
import com.example.cupid.R
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.cupid.model.domain.Question
import com.example.cupid.view.data.QuestionUI
import com.yuyakaido.android.cardstackview.CardStackView
import kotlinx.android.synthetic.main.item_question.view.*


class QuestionCardStackAdapter(
    private var questions: List<QuestionUI> = emptyList(),
    private var cardViewStack : CardStackView? =  null,
    private var context : Activity? = null
) : RecyclerView.Adapter<QuestionCardStackAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return ViewHolder(inflater.inflate(R.layout.item_question, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val question = questions[position]
        holder.question.text = question.questionText
        holder.answer1.text = question.choices[0]
        holder.answer2.text = question.choices[1]
        holder.answer3.text = question.choices[2]
        holder.answer4.text = question.choices[3]


        val bgColors = listOf(
            R.color.gradientEndActive,
            R.color.gradientMiddleActive,
            R.color.gradientStartActive)

        holder.itemView.layout_quiz_question.background = context!!.getDrawable(bgColors[position])

        // TODO get data out there
        holder.itemView.text_quiz_answer1.setOnClickListener { v -> cardViewStack!!.swipe() }
        holder.itemView.text_quiz_answer2.setOnClickListener { v -> cardViewStack!!.swipe() }
        holder.itemView.text_quiz_answer3.setOnClickListener { v -> cardViewStack!!.swipe() }
        holder.itemView.text_quiz_answer4.setOnClickListener { v -> cardViewStack!!.swipe() }
    }

    override fun getItemCount(): Int {
        return questions.size
    }

    fun setQuestions(questions: List<QuestionUI>) {
        this.questions = questions
    }

    fun getQuestions(): List<QuestionUI> {
        return questions
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val question: TextView = view.text_quiz_question
        val answer1 : TextView = view.text_quiz_answer1
        val answer2 : TextView = view.text_quiz_answer2
        val answer3 : TextView = view.text_quiz_answer3
        val answer4 : TextView = view.text_quiz_answer4
    }

}