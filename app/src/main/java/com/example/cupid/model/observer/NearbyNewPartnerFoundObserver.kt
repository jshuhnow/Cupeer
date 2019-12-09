package com.example.cupid.model.observer

interface NearbyNewPartnerFoundObserver {
    fun found()
    fun partnerInfoArrived(avartarId : Int, name : String)
}