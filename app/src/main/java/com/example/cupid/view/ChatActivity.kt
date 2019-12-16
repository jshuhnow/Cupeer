package com.example.cupid.view

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.inputmethod.InputMethodManager
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
import com.example.cupid.view.utils.getAvatarFromId
import kotlinx.android.synthetic.main.activity_chat.*
import com.example.cupid.view.utils.returnToMain
import kotlinx.android.synthetic.main.activity_chat.view.*





//TODO deal with cancelation on partners side -> launchRejectedPopup

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

        // ease of access, refactor at some point
        constraintLayout.image_chat_heading_partner.setImageResource(getAvatarFromId(this,model.getPartnerAccount()!!.avatarId))
        constraintLayout.image_chat_heading_you.setImageResource(getAvatarFromId(this,model.getUserAccount()!!.avatarId))

        setClickListeners()

    }

    override fun renderMessages(msgs: ArrayList<Message>, user: Account){
        // TODO: the partner leaves the room either with or without the greenlight (#21)

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
        if(messages.size > 0){
            layoutManager!!.scrollToPosition(messages.size - 1)
        }

        messageRecycler!!.layoutManager = layoutManager

        messageAdapter = ChatMessageListAdapter(this, messages)
        messageRecycler!!.adapter = messageAdapter



    }

    private fun setClickListeners(){

        button_chat_found.setOnClickListener{
            controller.sendCue(true)
            controller.terminateTheConnection()
        }

        button_chat_close.setOnClickListener {
            controller.sendCue(false)
            controller.terminateTheConnection()
        }

        button_chatbox_send.setOnClickListener{
            var payload = edittext_chatbox.text.toString()

            while (payload.endsWith("\n")){
                payload = payload.dropLast(1)
            }

            if (payload != ""){
                controller.sendMessage(Message(model.getUserAccount()!!, payload))

                val inputManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager


                inputManager.hideSoftInputFromWindow(
                    currentFocus?.windowToken,
                    InputMethodManager.HIDE_NOT_ALWAYS
                )
            }



        }
    }

    override fun clearTextView() {
        edittext_chatbox.setText("")
    }


    override fun onBackPressed() {

    }

    override fun returnToMain() {
        returnToMain(this)
    }

    override fun launchRejectedPopup() {
        com.example.cupid.view.utils.launchRejectedPopup(this)
    }

}

