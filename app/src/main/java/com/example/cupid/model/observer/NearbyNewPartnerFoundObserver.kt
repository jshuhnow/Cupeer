package com.example.cupid.model.observer

interface NearbyNewPartnerFoundObserver {
    fun found(avartarId : Int, name: String)
}