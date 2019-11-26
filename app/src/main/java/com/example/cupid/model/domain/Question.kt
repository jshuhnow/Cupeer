package com.example.cupid.model.domain

data class Question (
    val questionId : Int = 0,
    val choices : ArrayList<String>
)