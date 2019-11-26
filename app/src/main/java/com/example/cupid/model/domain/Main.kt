package com.example.cupid.model.domain

data class Main (
    val status: MainStatus = MainStatus.INIT,
    val user : Account,
    val questionList : ArrayList<Question>, // shared by all the users
    val session : Session,
    val chatList : ArrayList<Chat>,
    val pastUsers : ArrayList<Account> // used to avoid reconnecting to the same user
)