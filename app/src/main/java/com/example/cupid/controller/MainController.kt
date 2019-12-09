package com.example.cupid.controller

import com.example.cupid.model.DataAccessLayer
import com.example.cupid.model.domain.Question
import com.example.cupid.view.MainView
import com.example.cupid.view.MyConnectionService
import com.google.android.gms.nearby.connection.ConnectionsClient

class MainController(private val model: DataAccessLayer) {
    private lateinit var view:MainView
    private var mDiscovering = false
    private val mConnectionService: MyConnectionService = MyConnectionService.getInstance()


    fun bind(mainView : MainView) {
        view = mainView
    }

    fun registerClient(connectionsClient: ConnectionsClient) {
        MyConnectionService.getInstance()
            .setConnectionsClient(connectionsClient)
    }

    fun startAdvertising() {
        mConnectionService.startAdvertising()
    }

    fun stopAdvertising() {
        mConnectionService.stopAdvertising()
    }

    fun startDiscovering() {
        mConnectionService.startDiscovering()
    }

    fun stopDiscovering() {
        mConnectionService.stopDiscovering()
    }

    private fun dataSetup() {
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
        model.updateUserAccount(0, "Alice")
    }
    fun updateUserInfo() {
        view.updateUserInfo(model.getUserAccount()!!.avatarId, model.getUserAccount()!!.name)
    }

    fun init() {
        view.checkPermissions()

        dataSetup()
        updateUserInfo()
        view.updateGradientAnimation()
        view.updateClickListeners(mDiscovering)
    }

    fun hitDiscoverButton() {
        mDiscovering = !mDiscovering
        view.updateClickListeners(mDiscovering)

        if (mDiscovering) {
            startAdvertising()
            startDiscovering()
        } else {
            stopAdvertising()
            stopDiscovering()
        }
    }

    fun clientDiscovered(partnerAvartardId : Int, partnerName : String) {
        view.launchDiscoveredPopup(partnerAvartardId, partnerName)
    }
}