package com.example.cupid.model.domain

data class Session (
    val questions : ArrayList<Question>,
    val answers : ArrayList<Answer>,
    val chat : Chat,
    val status : SessionStatus = SessionStatus.EMBRYO,
    val timeout : Long
)