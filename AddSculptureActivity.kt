package com.shilpa.kala

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.util.Base64
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.shilpa.kala.databinding.ActivityAddSculptureBinding
import java.io.ByteArrayOutputStream
import java.util.*

class AddSculptureActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddSculptureBinding
    private var mainImageUri: Uri? = null
    private val timelineUris = mutableListOf<Uri>()
    private lateinit var timelineAdapter: UploadTimelineAdapter
    
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    private val pickMainImage = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            mainImageUri = it
            binding.ivSelectedImage.setImageURI(it)
        }
    }

    private val pickTimelineImage = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            timelineUris.add(it)
            timelineAdapter.notifyItemInserted(timelineUris.size - 1)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddSculptureBinding.inflate(layoutInflater)
        setContentView(binding.root)

        timelineAdapter = UploadTimelineAdapter(timelineUris)
        binding.rvTimelineUpload.adapter = timelineAdapter

        binding.cardImage.setOnClickListener { pickMainImage.launch("image/*") }
        binding.btnAddTimeline.setOnClickListener { pickTimelineImage.launch("image/*") }

        binding.btnUpload.setOnClickListener { validateAndUpload() }
    }

    private fun validateAndUpload() {
        val name = binding.etName.text.toString().trim()
        val style = binding.etStyle.text.toString().trim()
        val location = binding.etLocation.text.toString().trim()
        val description = binding.etDescription.text.toString().trim()
        val history = binding.etHistory.text.toString().trim()
        val whatsapp = binding.etWhatsapp.text.toString().trim()

        if (mainImageUri == null) {
            Toast.makeText(this, "Main image is required", Toast.LENGTH_SHORT).show()
            return
        }
        if (name.isEmpty() || style.isEmpty() || location.isEmpty() || whatsapp.isEmpty()) {
            Toast.makeText(this, "Fill in required fields", Toast.LENGTH_SHORT).show()
            return
        }

        startUploadProcess(name, style, location, description, history, whatsapp)
    }

    private fun startUploadProcess(name: String, style: String, location: String, desc: String, history: String, whatsapp: String) {
        binding.progressBar.visibility = View.VISIBLE
        binding.btnUpload.isEnabled = false

        Thread {
            try {
                // 1. Convert Main Image to Base64
                val mainImageBase64 = uriToBase64(mainImageUri!!)

                // 2. Convert Timeline Images to Base64
                val timelineBase64List = timelineUris.mapNotNull { uriToBase64(it) }

                runOnUiThread {
                    if (mainImageBase64 != null) {
                        saveSculptureToFirestore(name, style, location, desc, history, whatsapp, mainImageBase64, timelineBase64List)
                    } else {
                        binding.progressBar.visibility = View.GONE
                        binding.btnUpload.isEnabled = true
                        Toast.makeText(this, "Failed to process image", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                runOnUiThread {
                    binding.progressBar.visibility = View.GONE
                    binding.btnUpload.isEnabled = true
                    Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }.start()
    }

    private fun uriToBase64(uri: Uri): String? {
        return try {
            val inputStream = contentResolver.openInputStream(uri)
            val bitmap = BitmapFactory.decodeStream(inputStream)
            inputStream?.close()

            val outputStream = ByteArrayOutputStream()
            // Compress to keep Firestore document under 1MB limit
            bitmap.compress(Bitmap.CompressFormat.JPEG, 60, outputStream)
            val bytes = outputStream.toByteArray()
            Base64.encodeToString(bytes, Base64.DEFAULT)
        } catch (e: Exception) {
            null
        }
    }

    private fun saveSculptureToFirestore(name: String, style: String, location: String, desc: String, history: String, whatsapp: String, mainBase64: String, timelineBase64: List<String>) {
        val sculpture = Sculpture(
            id = UUID.randomUUID().toString(),
            name = name,
            style = style,
            artistLocation = location,
            description = desc,
            history = history,
            imageUrl = mainBase64,
            artistWhatsapp = whatsapp,
            artistName = auth.currentUser?.displayName ?: "Artist",
            artistUid = auth.currentUser?.uid ?: "",
            timelineImages = timelineBase64
        )

        db.collection("sculptures").document(sculpture.id).set(sculpture)
            .addOnSuccessListener {
                binding.progressBar.visibility = View.GONE
                Toast.makeText(this, "Art posted successfully!", Toast.LENGTH_SHORT).show()
                finish()
            }.addOnFailureListener {
                binding.progressBar.visibility = View.GONE
                binding.btnUpload.isEnabled = true
                Toast.makeText(this, "Firestore error: ${it.message}", Toast.LENGTH_SHORT).show()
            }
    }
}