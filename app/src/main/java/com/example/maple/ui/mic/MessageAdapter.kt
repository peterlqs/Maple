package com.example.maple.ui.mic

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.maple.R
import com.example.maple.data.Message
import com.google.firebase.auth.FirebaseAuth

class MessageAdapter(val context: Context, private val messageList: ArrayList<Message>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    // This will be explain later
    private val receive = 1
    private val sent = 2

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == 1) {
            // If from bot then inflate the chat box from the left
            val view: View =
                LayoutInflater.from(context).inflate(R.layout.row_receive, parent, false)
            // Tell the Receive class to put the right text in
            ReceiveViewHolder(view)
        } else {
            // If from user then inflate that chat bot from the right side
            val view: View = LayoutInflater.from(context).inflate(R.layout.row_send, parent, false)
            // Tell the Receive class to put the right text in
            SentViewHolder(view)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        // Get the message
        val currentMessage = messageList[position]
        // If the message is from the user then set on the sent xml
        if (holder.javaClass == SentViewHolder::class.java) {
            val viewHolder = holder as SentViewHolder
            holder.sentMessage.text = currentMessage.message
        } else {
            // If the message is from the bot then set on the receive xml
            val viewHolder = holder as ReceiveViewHolder
            holder.receiveMessage.text = currentMessage.message
        }
    }

    // Determine if from bot or user
    override fun getItemViewType(position: Int): Int {
        // Get current message
        val currentMessage = messageList[position]

        // If the id from message is from user then return sent( identify as user) else from bot
        return if (FirebaseAuth.getInstance().currentUser?.uid.equals(currentMessage.user)) {
            sent
        } else receive
    }

    // Size of list
    override fun getItemCount(): Int {
        return messageList.size
    }

    // The below two class to find the text view on the layouts
    class SentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val sentMessage: TextView = itemView.findViewById(R.id.txtSend)
    }

    class ReceiveViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val receiveMessage: TextView = itemView.findViewById(R.id.txtReceive)
    }
}