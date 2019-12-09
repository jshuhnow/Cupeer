package com.example.cupid.model.domain

data class Question (
    val questionId : Int = 0,
    val questionText : String,
    val choices : ArrayList<String>
)