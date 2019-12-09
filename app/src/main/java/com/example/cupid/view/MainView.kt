package com.example.cupid.view

interface MainView {
    fun updateUserInfo(avatarId: Int, name : String)
    fun updateGradientAnimation()
    fun launchDiscoveredPopup(partnerAvatarId: Int, partnerName : String)
    fun updateClickListeners(mDiscovering : Boolean )
    fun checkPermissions() : Boolean
    fun launchWaitingPopup()
}