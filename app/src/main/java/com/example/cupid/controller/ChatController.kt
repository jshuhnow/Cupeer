package com.example.cupid.controller

import android.content.Context
import android.os.Handler
import android.util.Log
import com.example.cupid.R
import com.example.cupid.model.DataAccessLayer
import com.example.cupid.model.domain.*
import com.example.cupid.view.ChatView
import com.example.cupid.view.MyConnectionService
import com.example.cupid.view.utils.launchRejectedPopup


class ChatController(
    private val model : DataAccessLayer
) : AbstractNearbyController() {
    private lateinit var view : ChatView


    companion object {
        const val TAG = "ChatController"
        const val STAGE = 3
    }

    fun bind(chatView: ChatView) {
        view = chatView
    }

    fun init() {
        updateView()

        if(model.inInstructionMode()){

            var messageList = listOf((view as Context).resources.getString(R.string.demo_text_chat1),
                (view as Context).resources.getString(R.string.demo_text_chat2),
                (view as Context).resources.getString(R.string.demo_text_chat3),
                (view as Context).resources.getString(R.string.demo_text_chat4),
                (view as Context).resources.getString(R.string.demo_text_chat5),
                (view as Context).resources.getString(R.string.demo_text_chat6)
            )

            var messageDelays = listOf(2000,2700,4000,3000,4000,4000,0)

            val handler = Handler()
            handler.postDelayed(object : Runnable {

                private var messageIndex = 0
                override fun run() {

                    if(messageIndex < messageList.size){
                        addMessage(model.getPartnerAccount() as Account,messageList[messageIndex])

                        handler.postDelayed(this, messageDelays[++messageIndex].toLong())
                    }
                }
            }, messageDelays[0].toLong())
        }
    }

    private fun updateView() {
        view.renderMessages(
            model.getMessages(),
            model.getUserAccount() as Account
        )
    }

    fun addMessage(author: Account, payload: String){
        model.getMessages().add(
            Message(author, payload)
        )
        updateView()
    }

    fun sendMessage(message : Message) {
        mConnectionService.send(message)
        addMessage(message.owner, message.payload)
    }

    override fun processNearbyPayload(nearbyPayload : NearbyPayload) {
        when (nearbyPayload.type) {
            "Message" -> {
                val message = nearbyPayload.obj as Message
                addMessage(message.owner, message.payload)
            }
            "ReplyToken" -> {
                val replyToken = nearbyPayload.obj as ReplyToken
                if (replyToken.stage >= STAGE) {
                    if (!replyToken.isAccepted) {
                        view.launchRejectedPopup()
                        terminateTheConnection()
                    }
                } else {
                    Log.d(MainController.TAG, "ReplyToken of unexpected stage")
                }
            }
            else -> {
                Log.d(MainController.TAG, "NearbyPayload of unexpected type")
            }
        }
    }

    override val mReceivingCondition: (NearbyPayload) -> Boolean
        get() = {
            when(it.type) {
                "Message" -> true
                "ReplyToken" -> {
                    val replyToken = it.obj as ReplyToken
                    (replyToken.stage == STAGE) and (!replyToken.isAccepted)
                }
                else -> false
            }
        }

    // should not be called
    override fun proceedToNextStage() {
        return
    }

    fun terminateTheConnection() {
        mConnectionService.disconnect()
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
        mConnectionService.send(ReplyToken(true, STAGE))
        view.launchWaitingPopup()
    }

    override fun registerNearbyPayloadListener() {
        super.registerNearbyPayloadListener(this)
    }

    override fun connectionRejected() {
        view.launchRejectedPopup()
    }


    override fun newPayloadReceived() {
        mConnectionService.conditionalPull()?.let { processNearbyPayload(it) }
    }

}