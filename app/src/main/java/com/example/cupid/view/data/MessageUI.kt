package com.example.cupid.view.data

import com.example.cupid.model.domain.Account

data class MessageUI (
    val name : String,
    val sentByMe: Boolean,
    val payload : String
)