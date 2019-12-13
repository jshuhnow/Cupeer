package com.example.cupid.view.views

import com.example.cupid.model.domain.Question

interface QuizQuestionsView {
    fun showQuestions(questions : ArrayList<Question>?)
    fun proceedToNextStage()
    fun launchWaitingPopup()
}