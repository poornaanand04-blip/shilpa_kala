package com.shilpa.kala

import android.util.Base64
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.shilpa.kala.databinding.ItemTimelineBinding

class TimelineAdapter(private val images: List<String>) : RecyclerView.Adapter<TimelineAdapter.TimelineViewHolder>() {

    class TimelineViewHolder(val binding: ItemTimelineBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TimelineViewHolder {
        val binding = ItemTimelineBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TimelineViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TimelineViewHolder, position: Int) {
        val base64String = images[position]
        try {
            val imageBytes = Base64.decode(base64String, Base64.DEFAULT)
            Glide.with(holder.itemView.context)
                .asBitmap()
                .load(imageBytes)
                .centerCrop()
                .placeholder(android.R.color.darker_gray)
                .into(holder.binding.timelineImage)
        } catch (e: Exception) {
            holder.binding.timelineImage.setImageResource(android.R.color.darker_gray)
        }
    }

    override fun getItemCount() = images.size
}