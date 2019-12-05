package com.example.cupid.view

import android.text.format.DateUtils.formatDateTime
import android.content.Context
import android.util.Log
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.example.cupid.R
import com.example.cupid.view.data.MessageUI


class MessageListAdapter(
    private val mContext: Context,
    private val mMessageList: List<MessageUI>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val SENT = 1
    private val RECEIVED = 2

    override fun getItemCount(): Int {
        Log.d("Test","kjhjh")
        return mMessageList.size
    }


    override fun getItemViewType(position: Int): Int {
        val message = mMessageList[position]
        return if (message.sentByMe) SENT else RECEIVED
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view: View

        if(viewType == SENT) {
            view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_chat_message_sent, parent, false)
            return SentMessageHolder(view)
        } else {
            view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_chat_message_received, parent, false)
            return ReceivedMessageHolder(view)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val message = mMessageList[position]

        when (holder.itemViewType) {
            SENT -> (holder as SentMessageHolder).bind(message)
            RECEIVED -> (holder as ReceivedMessageHolder).bind(message)
        }
    }

    private inner class SentMessageHolder internal constructor(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        internal var messageText: TextView
        internal var timeText: TextView

        init {

            messageText = itemView.findViewById(R.id.text_message_body)
            timeText = itemView.findViewById(R.id.text_message_time)
        }

        internal fun bind(message: MessageUI) {
            messageText.text = message.payload
        }
    }

    private inner class ReceivedMessageHolder internal constructor(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        var messageText: TextView
        var timeText: TextView
        var nameText: TextView
        var profileImage: ImageView

        init {
            messageText = itemView.findViewById(R.id.text_message_body)
            timeText = itemView.findViewById(R.id.text_message_time)
            nameText = itemView.findViewById(R.id.text_message_name)
            profileImage = itemView.findViewById(R.id.image_message_profile) as ImageView
        }

        internal fun bind(message: MessageUI) {
            messageText.text = message.payload
            nameText.text = message.name
        }
    }
}