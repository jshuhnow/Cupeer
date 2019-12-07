package com.example.cupid.model.observer

interface AccountObserver : DomainObserver {
    fun accountLoggedIn()
    fun accountUnknown()
}