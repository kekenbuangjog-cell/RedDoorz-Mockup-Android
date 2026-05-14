package com.example.reddoorz

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class BookingsFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_bookings, container, false)
        
        val rvBookings = view.findViewById<RecyclerView>(R.id.rvBookings)
        val tvNoBookings = view.findViewById<TextView>(R.id.tvNoBookings)

        val bookings = BookingManager.getBookings()

        if (bookings.isNotEmpty()) {
            tvNoBookings.visibility = View.GONE
            rvBookings.visibility = View.VISIBLE
            
            rvBookings.layoutManager = LinearLayoutManager(requireContext())
            rvBookings.adapter = BookingsAdapter(bookings)
        } else {
            tvNoBookings.visibility = View.VISIBLE
            rvBookings.visibility = View.GONE
        }

        return view
    }
}