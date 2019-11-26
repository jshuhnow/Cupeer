package com.example.cupid.model.domain

enum class MainStatus(val status: Int) {
    INIT(0),
    SEARCHING(1),
    WAITING(2), //for other
    CHATTING(3),
    IDLE(4), // no timeout
}