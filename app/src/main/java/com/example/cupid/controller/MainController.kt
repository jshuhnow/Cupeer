package com.example.cupid.controller

import android.os.Parcelable
import com.example.cupid.controller.util.ParcelableUtil
import com.example.cupid.model.DataAccessLayer
import com.example.cupid.model.domain.Account
import com.example.cupid.model.domain.NearbyPayload

import com.example.cupid.model.observer.NearbyNewPartnerFoundObserver
import com.example.cupid.model.observer.QueueObserver
import com.example.cupid.view.MainView
import com.example.cupid.view.MyConnectionService
import com.google.android.gms.nearby.connection.ConnectionsClient
import com.google.android.gms.nearby.connection.Payload

class MainController(private val model: DataAccessLayer)
    : NearbyNewPartnerFoundObserver, QueueObserver {
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
        mConnectionService
            .registerNearbyNewPartnerFoundObserver(this)

    }

    fun stopAdvertising() {
        mConnectionService.stopAdvertising()
        mConnectionService
            .unregisterNearbyNewPartnerFoundObserver()
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

    override fun newPartnerfound() {
        mConnectionService.send(model.getUserAccount()!!)
        val res = mConnectionService.pullNearbyPayload(this)
        if (res != null) {
            val account = res as Account
            partnerInfoArrived(account.avatarId, account.name)
        }
    }

    override fun newElementArrived(nearbyPayload: NearbyPayload) {
        val account = nearbyPayload.obj as Account
        partnerInfoArrived(account.avatarId, account.name)
    }

    fun partnerInfoArrived(avartarId : Int, name : String) {
        model.updatePartnerAccount(avartarId, name)
    }


}