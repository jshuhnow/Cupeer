package com.example.cupid.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import com.example.cupid.R
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.cupid.view.data.MessageUI
import kotlinx.android.synthetic.main.activity_chat.*


class ChatActivity : AppCompatActivity() {

    private var messageRecycler: RecyclerView? = null
    private var messageAdapter: MessageListAdapter? = null
    private var layoutManager : RecyclerView.LayoutManager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        layoutManager = LinearLayoutManager(this)
        (layoutManager as LinearLayoutManager).orientation = LinearLayoutManager.VERTICAL




        //TODO Dummy values
        var messages: ArrayList<MessageUI> = arrayListOf()


        messages.add(MessageUI (
            name = "",
            sentByMe = true,
            payload = "Test text"
        ))

        messages.add(MessageUI (
            name = "Bob",
            sentByMe = false,
            payload = "Test text2"
        ))

        messages.add(MessageUI (
            name = "",
            sentByMe = true,
            payload = "Test text3"
        ))


        messageRecycler = reyclerview_message_list
        messageRecycler!!.layoutManager = layoutManager
        messageAdapter = MessageListAdapter(this, messages)
        messageRecycler!!.adapter = messageAdapter


    }
}


