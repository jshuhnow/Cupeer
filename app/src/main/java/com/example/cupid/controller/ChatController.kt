package com.example.cupid.controller

import com.example.cupid.model.DataAccessLayer
import com.example.cupid.model.domain.Account
import com.example.cupid.model.domain.Message
import com.example.cupid.view.ChatView
import com.example.cupid.view.QuizResultsView

class ChatController(
    private val model : DataAccessLayer
){
    private lateinit var view : ChatView

    fun bind(chatView: ChatView) {
        view = chatView
    }

    fun init() {
        view.renderMessages(
            model.getMessages() as ArrayList<Message>,
            model.getUserAccount() as Account
        )
    }

    fun addMessage(author: Account, payload: String){

        model.getMessages()!!.add(
            Message (0, author, payload, arrayListOf<Account>()))

        view.renderMessages(
            model.getMessages() as ArrayList<Message>,
            model.getUserAccount() as Account
        )
    }
}