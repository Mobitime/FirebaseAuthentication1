package com.example.firebaseauthentication1

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class EmailAdapter(private val emails: List<Email>) : 
    RecyclerView.Adapter<EmailAdapter.EmailViewHolder>() {

    class EmailViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val senderTextView: TextView = itemView.findViewById(R.id.tvSender)
        val subjectTextView: TextView = itemView.findViewById(R.id.tvSubject)
        val previewTextView: TextView = itemView.findViewById(R.id.tvPreview)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EmailViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_email, parent, false)
        return EmailViewHolder(view)
    }

    override fun onBindViewHolder(holder: EmailViewHolder, position: Int) {
        val email = emails[position]
        holder.senderTextView.text = email.sender
        holder.subjectTextView.text = email.subject
        holder.previewTextView.text = email.preview
    }

    override fun getItemCount() = emails.size
}