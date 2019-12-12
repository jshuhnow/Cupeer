package com.example.cupid.controller

import android.app.Activity
import android.content.Context
import android.os.Handler
import android.util.Log
import com.example.cupid.model.DataAccessLayer
import com.example.cupid.model.observer.QueueObserver
import com.example.cupid.view.ChatView
import com.example.cupid.view.MyConnectionService
import com.example.cupid.view.QuizResultsView
import androidx.core.os.HandlerCompat.postDelayed
import com.example.cupid.R
import com.example.cupid.model.domain.*
import com.example.cupid.view.utils.getAvatarFromId
import com.example.cupid.view.utils.returnToMain
import kotlinx.android.synthetic.main.activity_chat.*


class ChatController(
    private val model : DataAccessLayer
) : QueueObserver {
    private lateinit var view : ChatView
    private val mConnectionService: MyConnectionService = MyConnectionService.getInstance()

    companion object {
        const val TAG = "ChatController"
        const val STAGE = 3
    }

    fun bind(chatView: ChatView) {
        view = chatView
    }

    fun init() {
        updateView()
        fetchMessage()


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

            // TODO: the partner leaves the room either with or without the greenlight (#21)
        }
    }

    fun updateView() {

        view.renderMessages(
            model.getMessages() as ArrayList<Message>,
            model.getUserAccount() as Account
        )

        view.clearTextView()
    }

    // unless otherwise noted, the message type is MessageType.USER
    fun addMessage(author: Account, payload: String, type : MessageType = MessageType.USER){
        model.getMessages()!!.add(
            Message (0, author, payload, type, arrayListOf<Account>()))
        updateView()
    }

    fun sendMessage(message : Message) {
        mConnectionService.send(message)
        addMessage(message.owner, message.payload)
    }

    fun sendCue(isGreenLight : Boolean) {
        mConnectionService.send(Message(
            model.getUserAccount()!!,
            if (isGreenLight) SystemMessageString.GREENLIGHT else SystemMessageString.NO_GREENLIGHT,
            MessageType.SYSTEM
        ))
    }

    private fun processNearbyPayload(nearbyPayload : NearbyPayload) {
        when (nearbyPayload.type) {
            "Message" -> {
                val message = nearbyPayload.obj as Message
                addMessage(message.owner, message.payload, message.type)

                // One more
                fetchMessage()
            }
            "ReplyToken" -> {
                val replyToken = nearbyPayload.obj as ReplyToken
                processReplyToken(replyToken)
            }
            else -> {
                Log.d(MainController.TAG, "NearbyPayload of unexpected type")
            }
        }
    }

    fun terminateTheConnection() {
        mConnectionService.send(ReplyToken(false, STAGE))
        mConnectionService.myDisconnect()
    }

    private fun fetchMessage() {
        val nearbyPayload = mConnectionService.pullNearbyPayload(this) ?: return
        processNearbyPayload(nearbyPayload)
    }


    override fun newElementArrived(nearbyPayload: NearbyPayload) {
        processNearbyPayload(nearbyPayload)
    }

    fun processReplyToken(replyToken : ReplyToken) {
        if (replyToken.stage == MainController.STAGE) {
            if (replyToken.isAccepted == false) {
                terminateTheConnection()
            }
        } else {
            Log.d(MainController.TAG, "ReplyToken of unexpected stage")
        }
    }
}