package com.shilpa.kala

import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.shilpa.kala.databinding.ItemUploadTimelineBinding

class UploadTimelineAdapter(private val uris: List<Uri>) : RecyclerView.Adapter<UploadTimelineAdapter.ViewHolder>() {

    class ViewHolder(val binding: ItemUploadTimelineBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemUploadTimelineBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.ivTimelineItem.setImageURI(uris[position])
    }

    override fun getItemCount() = uris.size
}