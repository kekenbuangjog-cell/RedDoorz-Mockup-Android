package com.example.reddoorz

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.firestore.FirebaseFirestore

import android.graphics.Color
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity

class SearchFragment : Fragment() {

    /* 
     * GLOBAL VARIABLES:
     * - db: Instance of FirebaseFirestore used to query the cloud database.
     * - rvProperties: The RecyclerView UI component that acts as a container for property items.
     * - propertyAdapter: The custom adapter (PropertyAdapter) that binds 'Property' data to UI views.
     * - propertyList: A mutable list that acts as the local data source (Source of Truth) for the adapter.
     */
    private lateinit var db: FirebaseFirestore
    private lateinit var rvProperties: RecyclerView
    private lateinit var propertyAdapter: PropertyAdapter
    private val propertyList = mutableListOf<Property>()
    private val filteredList = mutableListOf<Property>()

    private lateinit var llCityContainer: LinearLayout
    private lateinit var llRecommendedContainer: LinearLayout
    private lateinit var tvSearchPlaceholder: TextView

    // Search Result Launcher
    private val searchLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == AppCompatActivity.RESULT_OK) {
            val data = result.data
            val query = data?.getStringExtra("SEARCH_QUERY") ?: ""
            
            // Re-hydrate UI with the selected query
            if (query.isNotEmpty()) {
                tvSearchPlaceholder.text = query
                tvSearchPlaceholder.setTextColor(Color.parseColor("#000000"))
            } else {
                tvSearchPlaceholder.text = "Search hotels, apartments..."
                tvSearchPlaceholder.setTextColor(Color.parseColor("#888888"))
            }
            
            // Apply filtering across all relevant fields
            applyFilters(query)
        }
    }

    /**
     * onCreateView: Standard Fragment lifecycle method to initialize the UI.
     * 1. Inflates the XML layout (fragment_search) into a View object.
     * 2. Initializes the Firestore database instance.
     * 3. Sets up the 'Settings' button listener for navigation.
     * 4. Configures the UI for cities and properties.
     */
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_search, container, false)

        // Initialize Firebase Firestore connection
        db = FirebaseFirestore.getInstance()

        // Configure Settings Button
        val btnSettings = view.findViewById<LinearLayout>(R.id.btnSettings)
        btnSettings.setOnClickListener {
            startActivity(Intent(requireContext(), SettingsActivity::class.java))
        }

        // Configure Search Bar: Navigates to the new SearchInputActivity
        tvSearchPlaceholder = view.findViewById(R.id.tvSearchPlaceholder)
        val llSearchBar = view.findViewById<LinearLayout>(R.id.llSearchBar)
        llSearchBar.setOnClickListener {
            searchLauncher.launch(Intent(requireContext(), SearchInputActivity::class.java))
        }

        // Initialize Cities Container (Dynamic View Inflation)
        llCityContainer = view.findViewById(R.id.llCityContainer)
        
        // Initialize Recommended Properties Container
        llRecommendedContainer = view.findViewById(R.id.llRecommendedContainer)

        // Initialize Properties RecyclerView
        rvProperties = view.findViewById(R.id.rvProperties)
        rvProperties.layoutManager = LinearLayoutManager(requireContext())
        propertyAdapter = PropertyAdapter(filteredList)
        rvProperties.adapter = propertyAdapter
        
        // Execute Fetch: Just fetch properties, then extract cities from them
        fetchProperties()

        return view
    }

    /**
     * applyFilters: Filters the properties based on city, name, or street.
     */
    private fun applyFilters(query: String) {
        filteredList.clear()
        if (query.isEmpty()) {
            filteredList.addAll(propertyList)
        } else {
            val q = query.lowercase()
            for (property in propertyList) {
                if (property.prop_city.lowercase().contains(q) || 
                    property.prop_name.lowercase().contains(q) || 
                    property.prop_street.lowercase().contains(q)) {
                    filteredList.add(property)
                }
            }
        }
        propertyAdapter.notifyDataSetChanged()
    }

    /**
     * fetchProperties: Communicates with Firestore to retrieve property documents.
     * After fetching, it also extracts unique city names to populate the city list.
     */
    private fun fetchProperties() {
        db.collection("properties")
            .get() 
            .addOnSuccessListener { documents ->
                propertyList.clear()
                val uniqueCities = mutableSetOf<String>()
                val recommendedProperties = mutableListOf<Property>()
                
                for (document in documents) {
                    try {
                        val property = document.toObject(Property::class.java)
                        property?.let { 
                            propertyList.add(it)
                            
                            // Collect unique city names from properties (prop_city field)
                            if (it.prop_city.isNotEmpty()) {
                                uniqueCities.add(it.prop_city)
                            }

                            // Criteria for "Recommended": Rating >= 4.5
                            if (it.rating >= 4.5) {
                                recommendedProperties.add(it)
                            }
                        }
                    } catch (e: Exception) {
                        android.util.Log.e("Firestore", "Error parsing property: ${e.message}")
                    }
                }
                
                // Update Properties UI
                applyFilters("") // Show all by default

                // Update Cities UI
                updateCityList(uniqueCities)

                // Update Recommended Properties UI
                updateRecommendedList(recommendedProperties)
            }
            .addOnFailureListener { exception ->
                Toast.makeText(requireContext(), "Error fetching properties: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
    }

    /**
     * updateRecommendedList: Programmatically adds property views to the recommended section.
     */
    private fun updateRecommendedList(recommendedProperties: List<Property>) {
        // Clear existing dynamic views
        llRecommendedContainer.removeAllViews()

        val inflater = LayoutInflater.from(requireContext())

        for (property in recommendedProperties) {
            // Inflate the horizontal property card
            val propertyView = inflater.inflate(R.layout.item_property_recommended, llRecommendedContainer, false)
            
            // Bind components
            val ivImage: ImageView = propertyView.findViewById(R.id.ivRecPropertyImage)
            val tvRating: TextView = propertyView.findViewById(R.id.tvRecPropertyRating)
            val tvName: TextView = propertyView.findViewById(R.id.tvRecPropertyName)
            val tvPrice: TextView = propertyView.findViewById(R.id.tvRecPropertyPrice)
            
            // Set data
            tvRating.text = "${property.rating} / 5"
            tvName.text = property.prop_name
            tvPrice.text = "₱ ${String.format("%.2f", property.base_price)}"

            // Load Image
            if (property.image_urls.isNotEmpty()) {
                val context = requireContext()
                val imageName = property.image_urls[0]
                
                // Resolves the string name from Firestore into a local integer Resource ID
                val resId = context.resources.getIdentifier(imageName, "drawable", context.packageName)

                Glide.with(this)
                    .load(if (resId != 0) resId else android.R.drawable.ic_menu_gallery)
                    .placeholder(android.R.drawable.ic_menu_gallery)
                    .into(ivImage)
            }

            // Click listener to open detail activity
            propertyView.setOnClickListener {
                val intent = Intent(requireContext(), PropertyDetailActivity::class.java)
                intent.putExtra("PROP_NAME", property.prop_name)
                intent.putExtra("PROP_PRICE", property.base_price)
                intent.putExtra("PROP_CITY", property.prop_city)
                intent.putExtra("PROP_STREET", property.prop_street)
                intent.putExtra("PROP_RATING", property.rating)
                if (property.image_urls.isNotEmpty()) {
                    intent.putStringArrayListExtra("PROP_IMAGES", ArrayList(property.image_urls))
                }
                startActivity(intent)
            }

            // Add to container
            llRecommendedContainer.addView(propertyView)
        }
    }

    /**
     * updateCityList: Programmatically builds and adds city views to the screen.
     * RESPONSIBILITY: This method takes a list of unique city names, converts the XML layout
     * (item_city.xml) into actual UI components (Views) for each city, and attaches them 
     * horizontally next to the "Near me" button. This manual process bypasses the need 
     * for a RecyclerView, solving the horizontal scrolling conflict.
     *
     * @param uniqueCities A Set (a list with no duplicates) of city names extracted from Firestore.
     */
    private fun updateCityList(uniqueCities: Set<String>) {
        /*
         * STEP 1: CLEANUP
         * llCityContainer is the horizontal row holding the UI. 
         * Index 0 is the "Near me" button. Any index from 1 onwards are cities from previous fetches.
         * If there are more than 1 items (meaning old cities exist), we must remove them before adding new ones.
         */
        if (llCityContainer.childCount > 1) {
            // Removes all children starting from index 1 (leaving index 0: "Near me" intact).
            // 'childCount - 1' is the number of views to remove.
            llCityContainer.removeViews(1, llCityContainer.childCount - 1)
        }

        /*
         * STEP 2: PREPARE THE INFLATER
         * LayoutInflater is an Android tool that reads an XML layout file and 
         * translates it into actual Kotlin/Java View objects that can be displayed on screen.
         */
        val inflater = LayoutInflater.from(requireContext())

        /*
         * STEP 3: LOOP AND BUILD
         * We iterate through every unique city name we got from Firestore.
         */
        for (cityName in uniqueCities) {
            
            /*
             * STEP 3A: INFLATE THE VIEW (Build the box)
             * We tell the inflater to take 'item_city.xml' and turn it into a real View object (cityView).
             * Parameter 1: R.layout.item_city (The blueprint to build)
             * Parameter 2: llCityContainer (The parent it will eventually belong to, used for styling calculations)
             * Parameter 3: false (Don't attach it to the parent JUST yet, we need to add data to it first)
             */
            val cityView = inflater.inflate(R.layout.item_city, llCityContainer, false)
            
            /*
             * STEP 3B: FIND COMPONENTS (Look inside the box)
             * Now that we built the cityView, we need to find the specific ImageView and TextView inside it.
             */
            val ivCityImage: ImageView = cityView.findViewById(R.id.id_city_image)
            val tvCityName: TextView = cityView.findViewById(R.id.id_city_name)
            
            /*
             * STEP 3C: INJECT DATA (Put data into the components)
             * Set the text of the TextView to the current city name from our loop.
             */
            tvCityName.text = cityName
            
            /*
             * STEP 3D: LOAD IMAGE
             * Use Glide to load an image into the ImageView. 
             * Since we don't have unique city URLs yet, we pass 'null' which forces Glide 
             * to immediately load the fallback '.placeholder()' image (bg_city_box).
             */
            Glide.with(this)
                .load(null as String?) 
                .placeholder(R.drawable.bg_city_box)
                .into(ivCityImage)

            /*
             * STEP 4: ATTACH TO SCREEN
             * Finally, take the fully built and populated cityView and physically 
             * attach it to the end of the llCityContainer. It will appear horizontally 
             * after the "Near me" button (and any other cities previously added in the loop).
             */
            llCityContainer.addView(cityView)
        }
    }
}
