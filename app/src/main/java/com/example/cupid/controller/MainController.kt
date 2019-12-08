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

    fun dataSetup() {
        model.addQuestions(
            "You win a lottery! What do you do with the money?",
            arrayListOf(
                "Spend it now!",
                "Better save it.",
                "Give it away.",
                ""
            )
        )
        model.addQuestions(
            "Q2",
            arrayListOf(
                "A",
                "B",
                "C",
                "D"
            )
        )
        model.addQuestions(
            "Q3",
            arrayListOf(
                "A",
                "B",
                "C",
                "D"
            )
        )


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