package com.example.reddoorz

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore

/**
 * SearchResultsActivity: Displays a live-filtering list of hotels.
 */
class SearchResultsActivity : AppCompatActivity() {

    private lateinit var db: FirebaseFirestore
    private lateinit var rvResults: RecyclerView
    private lateinit var propertyAdapter: PropertyAdapter
    private lateinit var etResultsSearch: EditText
    private lateinit var btnClearResultsSearch: ImageView

    private val allPropertiesList = mutableListOf<Property>()
    private val filteredList = mutableListOf<Property>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_search_results)

        db = FirebaseFirestore.getInstance()

        // Handle Window Insets for edge-to-edge
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(android.R.id.content)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Initialize UI
        etResultsSearch = findViewById(R.id.etResultsSearch)
        btnClearResultsSearch = findViewById(R.id.btnClearResultsSearch)
        rvResults = findViewById(R.id.rvSearchResultsList)

        // Get the initial search query from Intent
        val initialQuery = intent.getStringExtra("SEARCH_QUERY") ?: ""
        etResultsSearch.setText(initialQuery)

        // Setup Back Button
        findViewById<ImageView>(R.id.btnResultsBack).setOnClickListener {
            finish()
        }

        // Setup Clear Button
        btnClearResultsSearch.setOnClickListener {
            etResultsSearch.text.clear()
        }

        // Setup RecyclerView
        rvResults.layoutManager = LinearLayoutManager(this)
        propertyAdapter = PropertyAdapter(filteredList)
        rvResults.adapter = propertyAdapter

        // Real-time filtering logic
        etResultsSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                filterProperties(s.toString().trim())
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        // Initial data fetch
        fetchAllProperties()
    }

    /**
     * fetchAllProperties: Retrieves all properties from Firestore to enable local live filtering.
     */
    private fun fetchAllProperties() {
        db.collection("properties")
            .get()
            .addOnSuccessListener { documents ->
                allPropertiesList.clear()
                for (document in documents) {
                    try {
                        val property = document.toObject(Property::class.java)
                        property?.let { allPropertiesList.add(it) }
                    } catch (e: Exception) {
                        android.util.Log.e("Firestore", "Error parsing property: ${e.message}")
                    }
                }
                
                // Once data is loaded, apply the filter based on current text
                filterProperties(etResultsSearch.text.toString().trim())
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error fetching data: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    /**
     * filterProperties: Filters the master list and updates the UI in real-time.
     */
    private fun filterProperties(query: String) {
        // Toggle Clear button visibility
        btnClearResultsSearch.visibility = if (query.isNotEmpty()) View.VISIBLE else View.GONE

        filteredList.clear()
        if (query.isEmpty()) {
            // Show all if query is empty
            filteredList.addAll(allPropertiesList)
        } else {
            val q = query.lowercase()
            for (property in allPropertiesList) {
                if (property.prop_city.lowercase().contains(q) ||
                    property.prop_name.lowercase().contains(q) ||
                    property.prop_street.lowercase().contains(q)) {
                    filteredList.add(property)
                }
            }
        }
        
        propertyAdapter.notifyDataSetChanged()
    }
}
