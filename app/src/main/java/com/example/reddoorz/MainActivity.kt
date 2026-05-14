package com.example.reddoorz

import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment

class MainActivity : AppCompatActivity() {

    // UI elements for the bottom navigation bar
    private lateinit var navSearch: LinearLayout
    private lateinit var navHelp: LinearLayout
    private lateinit var navBookings: LinearLayout

    private lateinit var ivSearch: ImageView
    private lateinit var ivHelp: ImageView
    private lateinit var ivBookings: ImageView

    private lateinit var tvSearch: TextView
    private lateinit var tvHelp: TextView
    private lateinit var tvBookings: TextView

    // Fragment instances to be reused (Fragment Caching)
    private val searchFragment = SearchFragment()
    private val helpFragment = HelpFragment()
    private val bookingsFragment = BookingsFragment()
    private var activeFragment: Fragment = searchFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Enables edge-to-edge display
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        // Handles window insets
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Initialize UI elements
        navSearch = findViewById(R.id.navSearch)
        navHelp = findViewById(R.id.navHelp)
        navBookings = findViewById(R.id.navBookings)

        ivSearch = findViewById(R.id.ivSearch)
        ivHelp = findViewById(R.id.ivHelp)
        ivBookings = findViewById(R.id.ivBookings)

        tvSearch = findViewById(R.id.tvSearch)
        tvHelp = findViewById(R.id.tvHelp)
        tvBookings = findViewById(R.id.tvBookings)

        /*
         * FRAGMENT INITIALIZATION:
         * We add all fragments to the manager at the start, but hide the ones
         * that shouldn't be visible yet. This keeps their data in memory.
         */
        supportFragmentManager.beginTransaction().apply {
            add(R.id.fragment_container, bookingsFragment, "bookings").hide(bookingsFragment)
            add(R.id.fragment_container, helpFragment, "help").hide(helpFragment)
            add(R.id.fragment_container, searchFragment, "search") // Search is visible by default
        }.commit()

        // Click listeners for bottom navigation buttons
        navSearch.setOnClickListener {
            switchFragment(searchFragment, "search")
            updateNavUI("search")
        }

        navHelp.setOnClickListener {
            switchFragment(helpFragment, "help")
            updateNavUI("help")
        }

        navBookings.setOnClickListener {
            switchFragment(bookingsFragment, "bookings")
            updateNavUI("bookings")
        }
    }

    /**
     * switchFragment: Uses show/hide instead of replace.
     * This ensures that the fragment's state (data, scroll position, etc.) is preserved.
     */
    private fun switchFragment(fragment: Fragment, tag: String) {
        if (fragment == activeFragment) return

        supportFragmentManager.beginTransaction()
            .hide(activeFragment)
            .show(fragment)
            .commit()
        
        activeFragment = fragment
    }

    /**
     * Updates the visual appearance of the bottom navigation bar.
     * Highlights the icon and text of the selected tab and dims the others.
     */
    private fun updateNavUI(selectedTab: String) {
        val activeColor = Color.parseColor("#E52D27") // Red brand color
        val inactiveColor = Color.parseColor("#888888") // Grey color

        // Reset all icons and text to the inactive color (Grey)
        ivSearch.imageTintList = ColorStateList.valueOf(inactiveColor)
        ivHelp.imageTintList = ColorStateList.valueOf(inactiveColor)
        ivBookings.imageTintList = ColorStateList.valueOf(inactiveColor)

        tvSearch.setTextColor(inactiveColor)
        tvHelp.setTextColor(inactiveColor)
        tvBookings.setTextColor(inactiveColor)

        tvSearch.setTypeface(null, Typeface.NORMAL)
        tvHelp.setTypeface(null, Typeface.NORMAL)
        tvBookings.setTypeface(null, Typeface.NORMAL)

        // Highlight the selected tab with the active color (Red) and make text bold
        when (selectedTab) {
            "search" -> {
                ivSearch.imageTintList = ColorStateList.valueOf(activeColor)
                tvSearch.setTextColor(activeColor)
                tvSearch.setTypeface(null, Typeface.BOLD)
            }
            "help" -> {
                ivHelp.imageTintList = ColorStateList.valueOf(activeColor)
                tvHelp.setTextColor(activeColor)
                tvHelp.setTypeface(null, Typeface.BOLD)
            }
            "bookings" -> {
                ivBookings.imageTintList = ColorStateList.valueOf(activeColor)
                tvBookings.setTextColor(activeColor)
                tvBookings.setTypeface(null, Typeface.BOLD)
            }
        }
    }
}