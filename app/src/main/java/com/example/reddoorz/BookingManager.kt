package com.example.reddoorz

/**
 * Data class representing a single booking entry.
 * It encapsulates all the necessary details for a user's reservation.
 */
data class Booking(
    val hotelName: String,
    val dateRange: String,
    val status: String = "Confirmed",
    val price: String
)

/**
 * BookingManager is a Singleton object that acts as the "Source of Truth" for bookings.
 * Function: It provides a centralized, in-memory storage to manage booking data
 * across different screens (Activities and Fragments) without using a database for this demo.
 */
object BookingManager {
    // Private list to store bookings, ensuring it can only be modified through the addBooking method
    private val bookings = mutableListOf<Booking>()

    /**
     * Adds a new booking to the list.
     * This is called when a user clicks 'Book Now' in the PropertyDetailActivity.
     */
    fun addBooking(booking: Booking) {
        bookings.add(booking)
    }

    /**
     * Returns the full list of bookings.
     * This is used by the BookingsFragment to populate the RecyclerView.
     */
    fun getBookings(): List<Booking> {
        return bookings
    }
}
