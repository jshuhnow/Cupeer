package com.example.cupid.view

import com.example.cupid.model.domain.Answer
import com.example.cupid.model.domain.Question

interface QuizResultsView {
    fun showAnswers(questions : ArrayList<Question>?,
                    myAnswer : ArrayList<Answer>?,
                    partnerAnswer : ArrayList<Answer>?) {

    }
}