package com.example.cupid.controller

import android.os.Parcelable
import android.util.Log
import com.example.cupid.R
import com.example.cupid.controller.NearbyController.Companion.TAG
import com.example.cupid.controller.util.ParcelableUtil
import com.example.cupid.model.DataAccessLayer
import com.example.cupid.model.domain.Account
import com.example.cupid.model.domain.Endpoint
import com.example.cupid.model.domain.NearbyPayload
import com.example.cupid.model.domain.ReplyToken

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
    private var mEndPoint : Endpoint? = null

    companion object {
        const val TAG = "MainController"
        const val STAGE = 0
    }

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
        model.updateUserAccount(1, "Alice")
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

    fun acceptTheConnection() {
        mConnectionService.send(ReplyToken(true, STAGE))
        val res = mConnectionService.pullNearbyPayload(this)
        if (res != null) {
            val replyToken = res.obj as ReplyToken
            processReplyToken(replyToken)
        } else {
            view.launchWaitingPopup()
        }
    }

    fun rejectTheConnection() {
        mConnectionService.send(ReplyToken(false, STAGE))
        mConnectionService.disconnect(mEndPoint!!)
    }

    fun processReplyToken(replyToken : ReplyToken) {
        if (replyToken.stage == 0) {
            if (replyToken.isAccepted) {
                view.proceedToNextStage()
            } else {
                // go back
            }
        } else {
            Log.d(TAG, "ReplyToken of unexpected stage")
        }
    }
    override fun newPartnerfound(endpoint: Endpoint) {
        mEndPoint = endpoint

        mConnectionService.send(model.getUserAccount()!!)

        val res = mConnectionService.pullNearbyPayload(this)
        if (res != null) {
            val account = res.obj as Account
            partnerInfoArrived(account.avatarId, account.name)
        }
    }


    override fun newElementArrived(nearbyPayload: NearbyPayload) {
        if (nearbyPayload.type == "Account") {
            val account = nearbyPayload.obj as Account
            partnerInfoArrived(account.avatarId, account.name)
        } else if (nearbyPayload.type == "ReplyToken") {
            val replyToken = nearbyPayload.obj as ReplyToken
            processReplyToken(replyToken)
        } else {
            Log.d(TAG, "NearbyPayload of unexpected type")
        }

    }

    fun partnerInfoArrived(avartarId : Int, name : String) {
        model.updatePartnerAccount(avartarId, name)
        view.partnerFound(avartarId, name)
    }


}