package com.example.pathfinder.network

// In your ChatAdapter.kt (or wherever you bind the message to a TextView)

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.pathfinder.R // Make sure you have the correct R import
import com.example.pathfinder.model.ChatMessage
import io.noties.markwon.Markwon // <-- Import Markwon

class ChatAdapter(private val messages: List<ChatMessage>) : RecyclerView.Adapter<ChatAdapter.MessageViewHolder>() {

    // You need an instance of Markwon. You can create it once.
    private lateinit var markwon: Markwon

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        // Lazily initialize Markwon here using the context
        if (!::markwon.isInitialized) {
            markwon = Markwon.create(parent.context)
        }

        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_chat_message, parent, false)
        return MessageViewHolder(view)
    }

    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
        val message = messages[position]
        holder.bind(message)
    }

    override fun getItemCount() = messages.size

    inner class MessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // Assuming your layout file has a TextView with the id 'messageTextView'
        private val messageTextView: TextView = itemView.findViewById(R.id.messageTextView)

        fun bind(chatMessage: ChatMessage) {
            // This is the magic line!
            // Instead of just setting text, use Markwon to set the Markdown.
            markwon.setMarkdown(messageTextView, chatMessage.message)
        }
    }
}