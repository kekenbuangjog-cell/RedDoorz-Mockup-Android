package com.example.reddoorz

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore

/**
 * SearchInputActivity: Real-time autocomplete search for hotel properties and cities.
 */
class SearchInputActivity : AppCompatActivity() {

    private lateinit var etSearchInput: EditText
    private lateinit var btnClearSearch: ImageView
    private lateinit var rvSearchResults: RecyclerView
    private lateinit var suggestionAdapter: SearchSuggestionAdapter

    private lateinit var db: FirebaseFirestore
    private var allSuggestions = mutableListOf<Suggestion>()
    private var filteredSuggestions = mutableListOf<Suggestion>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_search_input)

        db = FirebaseFirestore.getInstance()

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(android.R.id.content)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Initialize UI
        etSearchInput = findViewById(R.id.etSearchInput)
        btnClearSearch = findViewById(R.id.btnClearSearch)
        rvSearchResults = findViewById(R.id.rvSearchResults)

        findViewById<ImageView>(R.id.btnSearchBack).setOnClickListener { finish() }

        btnClearSearch.setOnClickListener {
            etSearchInput.text.clear()
        }

        // Setup RecyclerView
        rvSearchResults.layoutManager = LinearLayoutManager(this)
        suggestionAdapter = SearchSuggestionAdapter(filteredSuggestions) { suggestion ->
            returnSearchResult(suggestion.title)
        }
        rvSearchResults.adapter = suggestionAdapter

        // Fetch all data for local filtering (efficient for smaller datasets)
        fetchSearchData()

        // Real-time filtering logic
        etSearchInput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val query = s.toString().trim()
                btnClearSearch.visibility = if (query.isNotEmpty()) View.VISIBLE else View.GONE
                filterSuggestions(query)
            }
            override fun afterTextChanged(s: Editable?) {}
        })
    }

    /**
     * fetchSearchData: Retrieves all properties from Firestore to build the autocomplete list.
     */
    private fun fetchSearchData() {
        db.collection("properties").get().addOnSuccessListener { documents ->
            val suggestions = mutableListOf<Suggestion>()
            val uniqueCities = mutableSetOf<String>()
            val uniqueStreets = mutableSetOf<String>()

            for (document in documents) {
                try {
                    val prop = document.toObject(Property::class.java)
                    if (prop != null) {
                        // Add city as a suggestion
                        if (prop.prop_city.isNotEmpty() && uniqueCities.add(prop.prop_city)) {
                            suggestions.add(Suggestion(prop.prop_city, "City", "CITY"))
                        }
                        // Add street as a suggestion (Locality)
                        if (prop.prop_street.isNotEmpty() && uniqueStreets.add(prop.prop_street)) {
                            suggestions.add(Suggestion(prop.prop_street, prop.prop_city, "LOCALITY"))
                        }
                        // Add specific hotel as a suggestion
                        suggestions.add(Suggestion(prop.prop_name, "${prop.prop_city}, ${prop.prop_street}", "PROPERTY"))
                    }
                } catch (e: Exception) {
                    android.util.Log.e("Firestore", "Error parsing property: ${e.message}")
                }
            }

            allSuggestions.clear()
            allSuggestions.addAll(suggestions)
        }
    }

    /**
     * filterSuggestions: Filters the master list against user input.
     * Logic: Matches are found in title or subtitle. Results are sorted so that 
     * items starting with the query appear first (Progressive search).
     */
    private fun filterSuggestions(query: String) {
        if (query.isEmpty()) {
            filteredSuggestions.clear()
        } else {
            val q = query.lowercase()
            filteredSuggestions = allSuggestions.filter {
                it.title.lowercase().contains(q) || it.subtitle.lowercase().contains(q)
            }.sortedByDescending { 
                it.title.lowercase().startsWith(q) 
            }.toMutableList()
        }
        suggestionAdapter.updateList(filteredSuggestions)
    }

    /**
     * returnSearchResult: Navigates to the SearchResultsActivity with the selected query.
     */
    private fun returnSearchResult(query: String) {
        val intent = Intent(this, SearchResultsActivity::class.java)
        intent.putExtra("SEARCH_QUERY", query)
        startActivity(intent)
    }
}
