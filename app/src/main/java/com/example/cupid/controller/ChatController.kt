package com.example.cupid.controller

import android.util.Log
import com.example.cupid.model.DataAccessLayer
import com.example.cupid.model.domain.Account
import com.example.cupid.model.domain.Message
import com.example.cupid.model.domain.NearbyPayload
import com.example.cupid.model.domain.ReplyToken
import com.example.cupid.model.observer.QueueObserver
import com.example.cupid.view.ChatView
import com.example.cupid.view.MyConnectionService
import com.example.cupid.view.QuizResultsView

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
    }

    fun updateView() {
        view.renderMessages(
            model.getMessages() as ArrayList<Message>,
            model.getUserAccount() as Account
        )
        view.clearTextView()
    }
    fun addMessage(author: Account, payload: String){
        model.getMessages()!!.add(
            Message (0, author, payload, arrayListOf<Account>()))
        updateView()
    }

    fun sendMessage(message : Message) {
        mConnectionService.send(message)
        addMessage(message.owner, message.payload)
    }

    fun rejectTheConnection() {
        mConnectionService.send(ReplyToken(false, STAGE))
        mConnectionService.myDisconnect()
        //TODO view.goback()
    }

    fun fetchMessage() {
        val res = mConnectionService.pullNearbyPayload(this)
        if (res == null) {
            return
        }

        if (res.type == "Message") {
            val message = res.obj as Message
            addMessage(message.owner, message.payload)

            // One more
            fetchMessage()
        } else if (res.type == "ReplyToken") {
            val replyToken = res.obj as ReplyToken
            processReplyToken(replyToken)
        } else {
            Log.d(MainController.TAG, "NearbyPayload of unexpected type")
        }
    }

    override fun newElementArrived(nearbyPayload: NearbyPayload) {
        if (nearbyPayload.type == "Message") {
            val message = nearbyPayload.obj as Message
            addMessage(message.owner, message.payload)

            // One more
            fetchMessage()
        } else if (nearbyPayload.type == "ReplyToken") {
            val replyToken = nearbyPayload.obj as ReplyToken
            processReplyToken(replyToken)
        } else {
            Log.d(MainController.TAG, "NearbyPayload of unexpected type")
        }

    }

    fun processReplyToken(replyToken : ReplyToken) {
        if (replyToken.stage == MainController.STAGE) {
            if (replyToken.isAccepted == false) {
                rejectTheConnection()
            }
        } else {
            Log.d(MainController.TAG, "ReplyToken of unexpected stage")
        }
    }

}