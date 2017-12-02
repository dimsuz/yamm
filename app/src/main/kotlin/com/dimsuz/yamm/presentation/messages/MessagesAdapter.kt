package com.dimsuz.yamm.presentation.messages

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.dimsuz.yamm.R
import com.dimsuz.yamm.domain.models.Post

class MessagesAdapter : RecyclerView.Adapter<MessagesAdapter.ViewHolder>() {
  private var data: List<Post> = emptyList()

  fun setData(data: List<Post>) {
    this.data = data
    notifyDataSetChanged()
  }

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
    val inflater = LayoutInflater.from(parent.context)
    val view = inflater.inflate(R.layout.item_message, parent, false)
    return ViewHolder(view)
  }

  override fun onBindViewHolder(holder: ViewHolder, position: Int) {
    val item = data[holder.adapterPosition]
    holder.messageTextView.text = item.message
  }

  override fun getItemCount() = data.size

  class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val messageTextView: TextView = itemView.findViewById(R.id.message_text)
  }
}