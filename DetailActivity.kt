package com.shilpa.kala

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Base64
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.shilpa.kala.R
import com.shilpa.kala.databinding.ActivityDetailBinding
import java.net.URLEncoder

class DetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val name = intent.getStringExtra("name") ?: ""
        val style = intent.getStringExtra("style") ?: ""
        val description = intent.getStringExtra("description") ?: ""
        val history = intent.getStringExtra("history") ?: ""
        val imageUrl = intent.getStringExtra("imageUrl") ?: ""
        val artistWhatsapp = intent.getStringExtra("whatsapp") ?: "910000000000"
        val productId = intent.getStringExtra("id") ?: "N/A"
        val artistName = intent.getStringExtra("artistName") ?: "Unknown Artist"
        val artistLocation = intent.getStringExtra("artistLocation") ?: ""
        val timelineImages = intent.getStringArrayListExtra("timelineImages") ?: arrayListOf<String>()

        binding.detailName.text = name
        
        binding.detailArtistInfo.text = getString(R.string.artist_info_format, artistName, artistLocation)

        binding.detailStyle.text = getString(R.string.style_label, style)
        binding.detailDescription.text = description
        binding.detailHistory.text = history

        // Decode Base64 Main Image
        try {
            val imageBytes = Base64.decode(imageUrl, Base64.DEFAULT)
            Glide.with(this)
                .asBitmap()
                .load(imageBytes)
                .placeholder(android.R.color.darker_gray)
                .into(binding.mainImage)
        } catch (e: Exception) {
            binding.mainImage.setImageResource(android.R.color.darker_gray)
        }

        // Work-in-Progress Timeline
        if (timelineImages.isNotEmpty()) {
            binding.timelineHeader.visibility = View.VISIBLE
            binding.timelineRecyclerView.visibility = View.VISIBLE
            binding.timelineRecyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
            binding.timelineRecyclerView.adapter = TimelineAdapter(timelineImages)
        } else {
            binding.timelineHeader.visibility = View.GONE
            binding.timelineRecyclerView.visibility = View.GONE
        }

        binding.btnEnquire.setOnClickListener {
            val message = "Hello, I am interested in your work: $name (ID: $productId). Can we discuss more?"
            try {
                val url = "https://api.whatsapp.com/send?phone=$artistWhatsapp&text=" + URLEncoder.encode(message, "UTF-8")
                val i = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                startActivity(i)
            } catch (e: Exception) {
                Toast.makeText(this, getString(R.string.whatsapp_not_installed), Toast.LENGTH_SHORT).show()
            }
        }
    }
}