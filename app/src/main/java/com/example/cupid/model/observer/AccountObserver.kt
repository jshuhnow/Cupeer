package com.example.cupid.model.observer

interface AccountObserver : DomainObserver {
    fun userAccountUpdated()
}