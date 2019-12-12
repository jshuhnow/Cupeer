package com.example.cupid.controller

import android.content.Context
import android.os.Handler
import android.os.Parcelable
import android.util.Log
import com.example.cupid.R

import com.example.cupid.controller.util.ParcelableUtil
import com.example.cupid.model.DataAccessLayer
import com.example.cupid.model.domain.*

import com.example.cupid.model.observer.NearbyNewPartnerFoundObserver
import com.example.cupid.model.observer.QueueObserver
import com.example.cupid.view.MainView
import com.example.cupid.view.MyConnectionService
import com.example.cupid.view.utils.launchInstructionPopup
import com.google.android.gms.nearby.connection.ConnectionsClient
import com.google.android.gms.nearby.connection.Payload

class MainController(private val model: DataAccessLayer)
    : NearbyNewPartnerFoundObserver, QueueObserver {
    private lateinit var view:MainView
    private var mDiscovering = false
    private val mConnectionService: MyConnectionService = MyConnectionService.getInstance()

    private var demoPopup = false

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
        mConnectionService
            .registerNearbyNewPartnerFoundObserver(this)
        mConnectionService.startAdvertising()
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

    fun isDiscovering() : Boolean{
        return mDiscovering
    }

    private fun dataSetup() {
        model.addQuestions(
            "You win a lottery! What do you do with the money?",
            arrayListOf(
                "Spend it now!",
                "Better save it.",
                "Give it away.",
                "Somehow lose it."
            )
        )
        model.addQuestions(
            "A test is coming up. How do you study for it?",
            arrayListOf(
                "Study hard. ",
                "At the last second. ",
                "Ignore it and play!",
                "Umm, what test?"
            )
        )
        model.addQuestions(
            "A human hand extends out of a toilet! What would you do?",
            arrayListOf(
                "Scream and run. ",
                "Close the lid without a word.",
                "Flush frantically.",
                "Shake hands with it."
            )
        )
        model.updateUserAccount(6, "Alice")
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
        model.setInstructionMode(false)
    }

    fun hitDiscoverButton() {
        mDiscovering = !mDiscovering
        view.updateClickListeners(mDiscovering)

        if (mDiscovering) {

            if(model.inInstructionMode()){
                if(!demoPopup){
                    demoPopup = true
                    Handler().postDelayed({
                        if(mDiscovering){
                            view.launchDiscoveredPopup(
                                model.getPartnerAccount()!!.avatarId, model.getPartnerAccount()!!.name)
                        }
                        demoPopup = false

                    }, 3000)
                }
                return
            }else{
                startAdvertising()
                startDiscovering()
            }

        } else {
            if(model.inInstructionMode()){return}
            stopAdvertising()
            stopDiscovering()
        }


    }

    fun acceptTheConnection() {

        if(model.inInstructionMode()){
            view.launchWaitingPopup()
            return
        }


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

        if(model.inInstructionMode()){
            return
        }

        mConnectionService.send(ReplyToken(false, STAGE))
        mConnectionService.myDisconnect()
    }

    fun processReplyToken(replyToken : ReplyToken) {
        if (replyToken.stage == STAGE) {
            if (replyToken.isAccepted) {
                view.proceedToNextStage()
            } else {
                // go back
            }
        } else {
            Log.d(TAG, "ReplyToken of unexpected stage")
        }
    }
    override fun newPartnerfound() {

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

    fun fillInstructionData(){
        model.updatePartnerAccount(11,"Cupee")
        model.updatePartnerAnswer(0, 0)
        model.updatePartnerAnswer(1, 0)
        model.updatePartnerAnswer(2, 3)
    }

    fun startInstructionDialog(){
        model.setInstructionMode(true)
        launchInstructionPopup(view as Context,
            listOf((view as Context).resources.getString(R.string.demo_text_greeting1),
                (view as Context).resources.getString(R.string.demo_text_greeting2),
                (view as Context).resources.getString(R.string.demo_text_greeting3),
                (view as Context).resources.getString(R.string.demo_text_greeting4)))
    }


}