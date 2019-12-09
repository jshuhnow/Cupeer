package com.example.cupid.model.observer

import com.example.cupid.model.domain.Message

interface MessageObserver : DomainObserver {
    fun messageReceived () {

    }
    fun messageSent() {

    }
}