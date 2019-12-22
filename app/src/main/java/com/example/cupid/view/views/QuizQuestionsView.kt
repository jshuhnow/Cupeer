package com.example.cupid.view.views

import com.example.cupid.model.domain.Question

interface QuizQuestionsView : NearbyView {
    fun showQuestions(questions : ArrayList<Question>?)
}