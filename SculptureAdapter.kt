package com.shilpa.kala

import android.content.Intent
import android.net.Uri
import android.util.Base64
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.shilpa.kala.R
import com.shilpa.kala.databinding.ItemSculptureBinding
import java.net.URLEncoder

class SculptureAdapter(
    private val sculptures: List<Sculpture>,
    private val onItemClick: (Sculpture) -> Unit
) : RecyclerView.Adapter<SculptureAdapter.SculptureViewHolder>() {

    class SculptureViewHolder(val binding: ItemSculptureBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SculptureViewHolder {
        val binding = ItemSculptureBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SculptureViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SculptureViewHolder, position: Int) {
        val sculpture = sculptures[position]
        val context = holder.itemView.context
        
        holder.binding.sculptureName.text = sculpture.name
        holder.binding.sculptureArtist.text = context.getString(R.string.artist_label, sculpture.artistName)
        holder.binding.sculptureStyle.text = context.getString(R.string.style_label, sculpture.style)
        
        // Decode Base64 string to ByteArray for Glide
        try {
            val imageBytes = Base64.decode(sculpture.imageUrl, Base64.DEFAULT)
            Glide.with(context)
                .asBitmap()
                .load(imageBytes)
                .placeholder(android.R.color.darker_gray)
                .centerCrop()
                .into(holder.binding.sculptureImage)
        } catch (e: Exception) {
            holder.binding.sculptureImage.setImageResource(android.R.color.darker_gray)
        }

        holder.binding.btnViewDetails.setOnClickListener { onItemClick(sculpture) }
        holder.itemView.setOnClickListener { onItemClick(sculpture) }

        holder.binding.btnEnquire.setOnClickListener {
            val message = "Hello, I am interested in Sculpture ID: ${sculpture.id}"
            try {
                val url = "https://api.whatsapp.com/send?phone=${sculpture.artistWhatsapp}&text=" + URLEncoder.encode(message, "UTF-8")
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                context.startActivity(intent)
            } catch (e: Exception) {
                Toast.makeText(context, context.getString(R.string.whatsapp_not_installed), Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun getItemCount() = sculptures.size
}