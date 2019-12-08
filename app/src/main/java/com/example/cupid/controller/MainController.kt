package com.example.cupid.controller

import com.example.cupid.model.DataAccessLayer
import com.example.cupid.model.domain.Question
import com.example.cupid.view.MainView

class MainController(private val model: DataAccessLayer) {
    private lateinit var view:MainView
    private var mDiscovering = false

    fun bind(mainView : MainView) {
        view = mainView
    }

    fun init() {
        view.checkPermissions()
        view.updateGradientAnimation()
        view.updateClickListeners(mDiscovering)

        mDiscovering = true
    }

    fun clientDiscovered(partnerAvartardId : Int, partnerName : String) {
        view.launchDiscoveredPopup(partnerAvartardId, partnerName)
    }
}