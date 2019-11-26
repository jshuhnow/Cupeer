package com.example.cupid.model

import com.example.cupid.model.observer.DomainObserver

class DataAccessLayer {
    private val observers = mutableListOf<DomainObserver>()
    fun register(observer: DomainObserver) = observers.add(observer)

    fun unregister(observer: DomainObserver) = observers.remove(observer)

}