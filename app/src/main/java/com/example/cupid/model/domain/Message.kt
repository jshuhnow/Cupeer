package com.example.cupid.model.domain

data class Message (
    val createdTime : Long = 0,
    val owner : Account,
    val payload : String,
    val readyBy : ArrayList<Account>
)