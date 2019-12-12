package com.example.cupid.view

import com.example.cupid.model.domain.Account
import com.example.cupid.model.domain.Message

interface ChatView {
    fun renderMessages(msgs: ArrayList<Message>, user: Account){}
    fun clearTextView()
    fun returnToMain()
}