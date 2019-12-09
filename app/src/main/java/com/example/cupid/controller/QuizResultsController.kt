package com.example.cupid.controller

import com.example.cupid.model.DataAccessLayer
import com.example.cupid.model.domain.Account
import com.example.cupid.view.QuizResultsView

class QuizResultsController(
    private val model : DataAccessLayer
){
    private lateinit var view : QuizResultsView

    fun bind(quizQuestionsView: QuizResultsView) {
        view = quizQuestionsView
    }

    fun init() {
        view.renderAnswers(model.getQuestions(),
            model.getUserAccount() as Account,
            model.getUserAnswers(),
            model.getPartnerAccount() as Account,
            model.getPartnerAnswers()
        )
    }
}