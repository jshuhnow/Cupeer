package com.example.cupid.controller

import com.example.cupid.model.DataAccessLayer
import com.example.cupid.view.QuizQuestionsView

class QuizQuestionsController(
    private val model : DataAccessLayer
){
    private lateinit var view : QuizQuestionsView

    fun bind(quizQuestionsView: QuizQuestionsView) {
        view = quizQuestionsView
    }

    fun init() {
        view.showQuestions(model.getQuestions())
    }

    fun chooseAnswer(questionId : Int, answerId : Int) {
        // Access a model
    }
}