package com.example.cupid.controller

import android.content.Context
import android.os.Handler
import android.util.Log
import com.example.cupid.R
import com.example.cupid.model.DataAccessLayer
import com.example.cupid.model.domain.*
import com.example.cupid.view.ChatView
import com.example.cupid.view.MyConnectionService


class ChatController(
    private val model : DataAccessLayer
) : AbstractNearbyController() {
    private lateinit var view : ChatView
    override val mConnectionService: MyConnectionService = MyConnectionService.getInstance()
    override fun connectionRejected() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

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
                if (replyToken.stage <= MainController.STAGE) {
                    if (!replyToken.isAccepted) {
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

    fun terminateTheConnection() {
        mConnectionService.send(ReplyToken(false, STAGE))

        Handler().postDelayed(
            { mConnectionService.myDisconnect() },
            3000
        )
    }

    override fun proceedToNextStage() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun rejectTheConnection() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun waitForProceeding() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override val mReceivingCondition: (NearbyPayload) -> Boolean
        get() = TODO("not implemented") //To change initializer of created properties use File | Settings | File Templates.

    override fun newPayloadReceived() {

    }

    override fun haltPayloadReceived() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}