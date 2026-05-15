package com.shilpa.kala

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.shilpa.kala.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private val sculpturesList = mutableListOf<Sculpture>()
    private lateinit var adapter: SculptureAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.title = getString(R.string.app_name)

        setupRecyclerView()

        binding.fabAdd.setOnClickListener {
            if (auth.currentUser == null) {
                startActivity(Intent(this, LoginActivity::class.java))
            } else {
                startActivity(Intent(this, AddSculptureActivity::class.java))
            }
        }

        fetchSculpturesFromFirebase()
    }

    private fun setupRecyclerView() {
        adapter = SculptureAdapter(sculpturesList) { sculpture ->
            val intent = Intent(this, DetailActivity::class.java).apply {
                putExtra("id", sculpture.id)
                putExtra("name", sculpture.name)
                putExtra("style", sculpture.style)
                putExtra("description", sculpture.description)
                putExtra("history", sculpture.history)
                putExtra("imageUrl", sculpture.imageUrl)
                putExtra("whatsapp", sculpture.artistWhatsapp)
                putExtra("artistName", sculpture.artistName)
                putExtra("artistLocation", sculpture.artistLocation)
                putStringArrayListExtra("timelineImages", ArrayList(sculpture.timelineImages))
            }
            startActivity(intent)
        }
        // Premium Gallery: 2 Columns Grid
        binding.galleryRecyclerView.layoutManager = GridLayoutManager(this, 2)
        binding.galleryRecyclerView.adapter = adapter
    }

    private fun fetchSculpturesFromFirebase() {
        binding.progressBar.visibility = View.VISIBLE
        db.collection("sculptures")
            .get()
            .addOnSuccessListener { documents ->
                binding.progressBar.visibility = View.GONE
                sculpturesList.clear()
                for (document in documents) {
                    val sculpture = document.toObject(Sculpture::class.java)
                    sculpturesList.add(sculpture)
                }
                adapter.notifyDataSetChanged()
                
                if (sculpturesList.isEmpty()) {
                    Toast.makeText(this, "Welcome! No creations posted yet.", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { exception ->
                binding.progressBar.visibility = View.GONE
                Toast.makeText(this, "Error: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_logout -> {
                auth.signOut()
                Toast.makeText(this, "Logged out successfully", Toast.LENGTH_SHORT).show()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onResume() {
        super.onResume()
        fetchSculpturesFromFirebase()
    }
}