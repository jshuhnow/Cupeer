package com.example.cupid.view.views

interface MainView : NearbyView {
    fun updateUserInfo(avatarId: Int, name : String)
    fun updateGradientAnimation()
    fun launchDiscoveredPopup(partnerAvatarId: Int, partnerName : String)

    fun updateClickListeners(isSearching : Boolean )
    fun checkPermissions() : Boolean

    fun partnerFound(avatarId: Int, name: String)

}