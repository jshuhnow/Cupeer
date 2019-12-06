package com.example.cupid.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.cupid.R
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.cupid.view.adapters.ChatMessageListAdapter
import com.example.cupid.view.data.MessageUI
import kotlinx.android.synthetic.main.activity_chat.*
import com.example.cupid.view.utils.returnToMain


//TODO deal with cancelation on partners side -> launchRejectedPopup
//TODO handle incoming chat messages -> update adapter and ui

class ChatActivity : AppCompatActivity() {

    private var messageRecycler: RecyclerView? = null
    private var messageAdapter: ChatMessageListAdapter? = null
    private var layoutManager : RecyclerView.LayoutManager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)


        //TODO Replace dummy values
        var messages: ArrayList<MessageUI> = arrayListOf()


        messages.add(MessageUI (
            name = "",
            iconId = -1,
            sentByMe = true,
            payload = "Test text"
        ))

        messages.add(MessageUI (
            name = "Bob",
            iconId = 4,
            sentByMe = false,
            payload = "Test text2"
        ))

        messages.add(MessageUI (
            name = "",
            iconId = -1,
            sentByMe = true,
            payload = "Test text3"
        ))


        /* RecyclerView configuration */

        messageRecycler = reyclerview_message_list

        layoutManager = LinearLayoutManager(this)
        (layoutManager as LinearLayoutManager).orientation = LinearLayoutManager.VERTICAL
        messageRecycler!!.layoutManager = layoutManager

        messageAdapter = ChatMessageListAdapter(this, messages)
        messageRecycler!!.adapter = messageAdapter


        setClickListeners()

    }

    private fun setClickListeners(){

        button_chat_found.setOnClickListener{
            // TODO? Wait for the other person to press "found" as well before closing the chat?
            returnToMain(this)
        }

        button_chat_close.setOnClickListener {
            // TODO send the other person the "rejection" message
            returnToMain(this)
        }

        button_chatbox_send.setOnClickListener{
            var payload = edittext_chatbox.text

            // TODO Send txt message here
        }
    }


}


