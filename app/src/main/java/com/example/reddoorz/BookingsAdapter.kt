package com.example.reddoorz

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

/**
 * BookingsAdapter acts as a bridge between the data (Booking list) and the UI (RecyclerView).
 * Function: It creates view items and binds data to them, efficiently recycling 
 * views to save memory and ensure smooth scrolling.
 */
class BookingsAdapter(private val bookings: List<Booking>) :
    RecyclerView.Adapter<BookingsAdapter.BookingViewHolder>() {

    /**
     * ViewHolder describes an item view and metadata about its place within the RecyclerView.
     * Function: It holds references to the UI components for a single row item.
     */
    class BookingViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvHotelName: TextView = view.findViewById(R.id.tvHotelName)
        val tvDateRange: TextView = view.findViewById(R.id.tvDateRange)
        val tvPrice: TextView = view.findViewById(R.id.tvPrice)
        val tvStatus: TextView = view.findViewById(R.id.tvStatus)
    }

    /**
     * Called when RecyclerView needs a new ViewHolder to represent an item.
     * Function: Inflates the XML layout (item_booking.xml) for a single row.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookingViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_booking, parent, false)
        return BookingViewHolder(view)
    }

    /**
     * Called by RecyclerView to display the data at the specified position.
     * Function: Maps data from a Booking object to the UI elements in the ViewHolder.
     */
    override fun onBindViewHolder(holder: BookingViewHolder, position: Int) {
        val booking = bookings[position]
        holder.tvHotelName.text = booking.hotelName
        holder.tvDateRange.text = booking.dateRange
        holder.tvPrice.text = booking.price
        holder.tvStatus.text = booking.status
    }

    /**
     * Returns the total number of items in the data set held by the adapter.
     */
    override fun getItemCount() = bookings.size
}
