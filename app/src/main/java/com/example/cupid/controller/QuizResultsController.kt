package com.example.cupid.controller

import com.example.cupid.model.DataAccessLayer
import com.example.cupid.view.QuizResultsView

class QuizResultsController(
    private val model : DataAccessLayer
){
    private lateinit var view : QuizResultsView

    fun bind(quizQuestionsView: QuizResultsView) {
        view = quizQuestionsView
    }

    fun init() {
        view.showAnswers(model.getQuestions(),
            model.getUserAccount()!!.answers,
            model.getPartnerAccount()!!.answers
        )
    }

    fun chooseAnswer(questionId : Int, answerId : Int) {
        // Access a model
        model.updateUserAnswer(questionId, answerId)
    }
}