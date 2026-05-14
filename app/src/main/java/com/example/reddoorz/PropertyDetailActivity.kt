package com.example.reddoorz

import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2

/**
 * PropertyDetailActivity displays the full details of a specific hotel property.
 */
class PropertyDetailActivity : AppCompatActivity() {

    private lateinit var vpImageSlider: ViewPager2
    private lateinit var llDotIndicator: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_property_detail)

        // Unpack Intent Data
        val hotelName = intent.getStringExtra("PROP_NAME") ?: "Hotel Name"
        val hotelPriceValue = intent.getDoubleExtra("PROP_PRICE", 0.0)
        val hotelPrice = "₱ ${String.format("%.2f", hotelPriceValue)}"
        val hotelCity = intent.getStringExtra("PROP_CITY") ?: "City"
        val hotelStreet = intent.getStringExtra("PROP_STREET") ?: "Street"
        val hotelRating = intent.getDoubleExtra("PROP_RATING", 0.0)
        val imageList = intent.getStringArrayListExtra("PROP_IMAGES") ?: arrayListOf()

        // Initialize UI Elements
        val tvName: TextView = findViewById(R.id.tvDetailName)
        val tvBottomPrice: TextView = findViewById(R.id.tvBottomPrice)
        val tvRating: TextView = findViewById(R.id.tvDetailRating)
        val tvCity: TextView = findViewById(R.id.tvDetailCity)
        val tvStreet: TextView = findViewById(R.id.tvDetailStreet)
        val btnBack: ImageView = findViewById(R.id.btnBack)
        val btnBookNow: Button = findViewById(R.id.btnBookNow)
        
        vpImageSlider = findViewById(R.id.vpImageSlider)
        llDotIndicator = findViewById(R.id.llDotIndicator)

        // Bind Data
        tvName.text = hotelName
        tvBottomPrice.text = hotelPrice
        tvRating.text = "$hotelRating / 5"
        tvCity.text = hotelCity
        tvStreet.text = hotelStreet

        // Setup Image Slider
        if (imageList.isNotEmpty()) {
            val adapter = ImageSliderAdapter(imageList)
            vpImageSlider.adapter = adapter
            
            setupDotIndicators(imageList.size)
            
            vpImageSlider.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)
                    updateDotIndicators(position)
                }
            })
        }

        // Click Listeners
        btnBack.setOnClickListener {
            finish()
        }

        btnBookNow.setOnClickListener {
            val booking = Booking(
                hotelName = hotelName,
                dateRange = "25 Apr - 26 Apr 2026", 
                price = hotelPrice
            )
            BookingManager.addBooking(booking)
            Toast.makeText(this, "Successfully Booked $hotelName!", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    /**
     * setupDotIndicators: Dynamically creates dots based on image count.
     */
    private fun setupDotIndicators(count: Int) {
        llDotIndicator.removeAllViews()
        val dots = arrayOfNulls<TextView>(count)
        val params = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        params.setMargins(8, 0, 8, 0)

        for (i in 0 until count) {
            dots[i] = TextView(this)
            dots[i]?.text = "•"
            dots[i]?.textSize = 30f
            dots[i]?.setTextColor(android.graphics.Color.parseColor("#88FFFFFF")) // Inactive dot (Semi-transparent white)
            llDotIndicator.addView(dots[i], params)
        }
    }

    /**
     * updateDotIndicators: Updates the color of the dots to show current selection.
     */
    private fun updateDotIndicators(position: Int) {
        for (i in 0 until llDotIndicator.childCount) {
            val dot = llDotIndicator.getChildAt(i) as TextView
            if (i == position) {
                dot.setTextColor(android.graphics.Color.WHITE) // Active dot (Solid white)
                dot.scaleX = 1.2f
                dot.scaleY = 1.2f
            } else {
                dot.setTextColor(android.graphics.Color.parseColor("#88FFFFFF")) // Inactive dot
                dot.scaleX = 1.0f
                dot.scaleY = 1.0f
            }
        }
    }
}
