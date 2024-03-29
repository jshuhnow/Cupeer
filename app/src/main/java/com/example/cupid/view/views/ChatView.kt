package com.example.cupid.view.views

import com.example.cupid.model.domain.Account
import com.example.cupid.model.domain.Message

interface ChatView : NearbyView {
    fun renderMessages(msgs: ArrayList<Message>, user: Account){}
    fun clearTextView()
    fun returnToMain()

}