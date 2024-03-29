package com.example.cupid.view.views

import com.example.cupid.model.domain.Account
import com.example.cupid.model.domain.Answer
import com.example.cupid.model.domain.Question

interface QuizResultsView : NearbyView {
    fun renderAnswers(questions : ArrayList<Question>?,
                      myAccount: Account,
                      myAnswer : ArrayList<Answer>?,
                      partnerAccount: Account,
                      partnerAnswer : ArrayList<Answer>?) {

    }
}