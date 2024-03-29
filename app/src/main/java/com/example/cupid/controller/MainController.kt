package com.example.cupid.controller

import android.content.Context
import android.os.Handler
import android.util.Log
import com.example.cupid.R

import com.example.cupid.model.DataAccessLayer
import com.example.cupid.model.QuestionBankModule
import com.example.cupid.model.domain.*
import com.example.cupid.model.observer.NearbyEndpointListener

import com.example.cupid.view.views.MainView
import com.example.cupid.view.MyConnectionService
import com.example.cupid.view.utils.launchInstructionPopup

import com.google.android.gms.nearby.connection.ConnectionsClient

class MainController(private val model: DataAccessLayer)
    : AbstractNearbyController(), NearbyEndpointListener  {
    private lateinit var view : MainView
    private var mLocalIsSearching = false
    private var mHasAccepted = false

    // called only once
    fun init() {
        view.checkPermissions()

        dataSetup()
        view.updateGradientAnimation()

        // onStart() in MainActivity should start with the lock acquired
        mConnectionService.acquireLock()
    }

    // called whenever coming back to Main
    fun reset() {
        updateUserInfo()
        view.updateClickListeners(false)
        model.setInstructionMode(false)
        mConnectionService.setNearbyEndpointListener(this)
        mLocalIsSearching = false
        mHasAccepted = false

        for (i in 0..10) {
            var isReceived = false
            mConnectionService.conditionalPull()?.let {
                var res = processNearbyPayload(it)
                if (res != null) {
                    isReceived =true
                }

            }
            if (!isReceived) break
        }
    }

    fun startBackgroundThreads() {
        mConnectionService.searching()
    }

    // should be called with the lock acquired
    override fun registerNearbyPayloadListener() {
        super.registerNearbyPayloadListener(this)
    }

    fun isSearching() : Boolean {
        return mLocalIsSearching
    }


    override fun proceedToNextStage() {
        view.proceedToNextStage()
    }

    override fun rejectTheConnection() {
        if (model.inInstructionMode()) {
            return
        }
        mConnectionService.send(ReplyToken(false, STAGE))
        // Wait until the other party receive the ReplyToken
        Handler().postDelayed({
            mConnectionService.disconnect()
        }, 3000)
    }

    override fun waitForProceeding() {
        view.launchWaitingPopup()
        mHasAccepted = true
        mConnectionService.conditionalPull()?.let { processNearbyPayload(it) }

        if(model.inInstructionMode()){
            return
        }

        mConnectionService.send(ReplyToken(true, STAGE))
    }

    override fun connectionRejected() {
        view.launchRejectedPopup()
    }
    private fun processReplyToken(replyToken : ReplyToken) {
        if (replyToken.stage == STAGE) {
            if (replyToken.isAccepted) {
                view.proceedToNextStage()
            } else {
                view.launchRejectedPopup()
            }
        } else {
            Log.d(TAG, "ReplyToken of unexpected stage")
        }
    }

    override fun processNearbyPayload(nearbyPayload: NearbyPayload) {
        when (nearbyPayload.type) {
            "Account" -> {
                val account = nearbyPayload.obj as Account
                partnerInfoArrived(account.avatarId, account.name)
            }
            "ReplyToken" -> {
                val replyToken = nearbyPayload.obj as ReplyToken
                processReplyToken(replyToken)
            }
            else -> {
                Log.d(TAG, "NearbyPayload of unexpected type")
            }
        }
    }

    override fun newPayloadReceived() {
        mConnectionService.conditionalPull()?.let { processNearbyPayload(it) }
    }

    override val mReceivingCondition: (NearbyPayload) -> Boolean
        get() = {
            when (it.type) {
                "Account" -> true
                "ReplyToken" -> {
                    val replyToken = it.obj as ReplyToken
                    mHasAccepted || !replyToken.isAccepted

                }
                else -> false
            }
        }

    private var mDemoPopup = false

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

    var questionBank  = QuestionBankModule.questionBank

    private fun dataSetup() {
        for (i in 0..2){
            model.addQuestions(
                questionBank[i].first,
                questionBank[i].second
            )
        }

        model.updateUserAccount(6, "Alice")
    }

    fun updateUserInfo() {
        view.updateUserInfo(model.getUserAccount()!!.avatarId, model.getUserAccount()!!.name)
    }

    fun hitDiscoverButton() {
        mLocalIsSearching = !mLocalIsSearching
        view.updateClickListeners(mLocalIsSearching)

        if (mLocalIsSearching) {
            if(model.inInstructionMode()){
                fillDummyPartnerData()
                if(!mDemoPopup){
                    mDemoPopup = true
                    Handler().postDelayed({
                        if(mLocalIsSearching){
                            view.launchDiscoveredPopup(
                                model.getPartnerAccount()!!.avatarId, model.getPartnerAccount()!!.name)
                        }
                        mDemoPopup = false

                    }, 3000)
                }
                return
            }else{
                mConnectionService.startSearching()
            }

        } else {
            if(model.inInstructionMode()){return}
            mConnectionService.stopSearching()
        }
    }

    private fun partnerInfoArrived(avatarId : Int, name : String) {
        model.updatePartnerAccount(avatarId, name)
        view.partnerFound(avatarId, name)
    }


    fun startInstructionDialog(){
        model.setInstructionMode(true)
        launchInstructionPopup(view as Context,
            listOf((view as Context).resources.getString(R.string.demo_text_greeting1),
                (view as Context).resources.getString(R.string.demo_text_greeting2),
                (view as Context).resources.getString(R.string.demo_text_greeting3),
                (view as Context).resources.getString(R.string.demo_text_greeting4)))
    }

    override fun onEndpointConnected() {
        mConnectionService.stopSearching()
        mConnectionService.send(model.getUserAccount()!!)
    }

    override fun onEndpointDisconnected(endpoint: Endpoint?) {
        view.dismissPopups()
    }

    private fun fillDummyPartnerData() {
        model.updatePartnerAccount(11,"Cupee")
    }
}