package com.example.cupid.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.cupid.R
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.cupid.controller.ChatController
import com.example.cupid.controller.QuizQuestionsController
import com.example.cupid.model.ModelModule
import com.example.cupid.model.domain.Account
import com.example.cupid.model.domain.Message
import com.example.cupid.view.adapters.ChatMessageListAdapter
import com.example.cupid.view.data.MessageUI
import kotlinx.android.synthetic.main.activity_chat.*
import com.example.cupid.view.utils.returnToMain


//TODO deal with cancelation on partners side -> launchRejectedPopup

//TODO handle incoming chat messages -> addMessage(author: Account, payload: String)

class ChatActivity : AppCompatActivity(), ChatView {

    private var messageRecycler: RecyclerView? = null
    private var messageAdapter: ChatMessageListAdapter? = null
    private var layoutManager : RecyclerView.LayoutManager? = null

    private val model = ModelModule.dataAccessLayer
    private val controller = ChatController(model)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)


        controller.bind(this)
        controller.init()


        setClickListeners()

    }

    override fun renderMessages(msgs: ArrayList<Message>, user: Account){

        var messages: ArrayList<MessageUI> = arrayListOf()

        for (msg in msgs) {
            messages.add(MessageUI (
                name = msg.owner.name,
                iconId = msg.owner.avatarId,
                sentByMe = msg.owner == user, // may not work in kotlin
                payload = msg.payload
            ))
        }

        /* RecyclerView configuration */

        messageRecycler = reyclerview_message_list

        layoutManager = LinearLayoutManager(this)
        (layoutManager as LinearLayoutManager).orientation = LinearLayoutManager.VERTICAL
        messageRecycler!!.layoutManager = layoutManager

        messageAdapter = ChatMessageListAdapter(this, messages)
        messageRecycler!!.adapter = messageAdapter



    }

    private fun setClickListeners(){

        button_chat_found.setOnClickListener{
            returnToMain(this)
        }

        button_chat_close.setOnClickListener {
            // TODO send the other person the "rejection" message
            returnToMain(this)
        }

        button_chatbox_send.setOnClickListener{
            var payload = edittext_chatbox.text.toString()
            controller.addMessage(model.getUserAccount() as Account, payload)

            // TODO Send txt message to partner here
        }
    }


}

