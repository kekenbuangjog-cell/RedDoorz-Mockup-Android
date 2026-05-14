package com.example.reddoorz

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

/**
 * PropertyAdapter: The "Bridge" between the Firestore data list and the RecyclerView UI.
 * RESPONSIBILITY: 
 * 1. Creates individual item views (using item_property.xml).
 * 2. Maps data from a 'Property' object to the UI components of a card.
 * 3. Handles the "Packing" and "Shipping" of data when a user clicks a card.
 */
class PropertyAdapter(private val properties: List<Property>) :
    RecyclerView.Adapter<PropertyAdapter.PropertyViewHolder>() {

    /**
     * PropertyViewHolder: A "Box" that holds references to the UI elements for a single list item.
     * This prevents the app from calling 'findViewById' repeatedly, which makes scrolling smooth.
     */
    class PropertyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        // Find and store references to the UI components inside 'item_property.xml'
        val ivImage: ImageView = view.findViewById(R.id.ivPropertyImage)
        val tvRating: TextView = view.findViewById(R.id.tvPropertyRating)
        val tvName: TextView = view.findViewById(R.id.tvPropertyName)
        val tvLocation: TextView = view.findViewById(R.id.tvPropertyLocation)
        val tvPrice: TextView = view.findViewById(R.id.tvPropertyPrice)
    }

    /**
     * onCreateViewHolder: Runs when the RecyclerView needs a new "empty card" on the screen.
     * It "inflates" (converts) the XML layout 'item_property.xml' into a real Kotlin View object.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PropertyViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_property, parent, false)
        return PropertyViewHolder(view)
    }

    /**
     * onBindViewHolder: The most important method for Data Flow.
     * It "binds" (plugs in) data from a specific Property object into a specific card on the screen.
     */
    override fun onBindViewHolder(holder: PropertyViewHolder, position: Int) {
        // Step 1: Identify the specific hotel object based on its position in the list.
        val property = properties[position]
        
        // Step 2: Inject text data into the ViewHolder's TextViews.
        holder.tvName.text = property.prop_name
        holder.tvRating.text = "${property.rating} / 5"
        holder.tvLocation.text = "${property.prop_city}, ${property.prop_street}"
        
        // Formats the Double price into a currency string (e.g., 1500.0 -> ₱ 1,500.00)
        holder.tvPrice.text = "₱ ${String.format("%.2f", property.base_price)}"

        // Step 3: Use Glide to handle local drawable resources.
        if (property.image_urls.isNotEmpty()) {
            val context = holder.itemView.context
            val imageName = property.image_urls[0]
            
            // Resolves the string name from Firestore into a local integer Resource ID
            val resId = context.resources.getIdentifier(imageName, "drawable", context.packageName)

            Glide.with(context)
                .load(if (resId != 0) resId else android.R.drawable.ic_menu_gallery)
                .placeholder(android.R.drawable.ic_menu_gallery)
                .into(holder.ivImage)
        }

        /*
         * Step 4: THE "PACKING & SHIPPING" LOGIC
         * Defines what happens when the user physically taps this specific card.
         */
        holder.itemView.setOnClickListener {
            // A. Create the Intent (The Delivery Box) pointing to the Detail Activity
            val intent = Intent(holder.itemView.context, PropertyDetailActivity::class.java)
            
            // B. PACKING: Use 'putExtra' to place data into the box with specific Labels (Keys)
            intent.putExtra("PROP_NAME", property.prop_name)
            intent.putExtra("PROP_PRICE", property.base_price)
            intent.putExtra("PROP_CITY", property.prop_city)
            intent.putExtra("PROP_STREET", property.prop_street)
            intent.putExtra("PROP_RATING", property.rating)
            
            // If the hotel has images, pack the full list into the intent
            if (property.image_urls.isNotEmpty()) {
                intent.putStringArrayListExtra("PROP_IMAGES", ArrayList(property.image_urls))
            }
            
            // C. SHIPPING: Start the next Activity and send the "Delivery Box" (intent) with it.
            holder.itemView.context.startActivity(intent)
        }
    }

    /**
     * getItemCount: Tells the RecyclerView how many total items are in our list.
     */
    override fun getItemCount() = properties.size
}

