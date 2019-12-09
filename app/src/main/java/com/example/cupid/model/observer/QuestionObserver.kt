package com.example.cupid.model.observer

import com.example.cupid.model.domain.Question

interface QuestionObserver : DomainObserver {
    fun setQuestions(questions: ArrayList<Question>?)

}