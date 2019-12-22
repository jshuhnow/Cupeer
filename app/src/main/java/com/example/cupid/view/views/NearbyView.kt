package com.example.cupid.view.views

interface NearbyView {
    fun launchWaitingPopup()
    fun launchRejectedPopup()
    fun proceedToNextStage()
    fun dismissPopups()
}