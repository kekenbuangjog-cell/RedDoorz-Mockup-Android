package com.example.reddoorz

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

/**
 * Data class representing a search suggestion (City or Hotel).
 */
data class Suggestion(
    val title: String,
    val subtitle: String,
    val type: String // "CITY" or "PROPERTY"
)

/**
 * SearchSuggestionAdapter: Displays real-time search results in SearchInputActivity.
 */
class SearchSuggestionAdapter(
    private var suggestions: List<Suggestion>,
    private val onItemClick: (Suggestion) -> Unit
) : RecyclerView.Adapter<SearchSuggestionAdapter.SuggestionViewHolder>() {

    class SuggestionViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val ivIcon: ImageView = view.findViewById(R.id.ivSuggestionIcon)
        val tvTitle: TextView = view.findViewById(R.id.tvSuggestionTitle)
        val tvSubtitle: TextView = view.findViewById(R.id.tvSuggestionSubtitle)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SuggestionViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_search_suggestion, parent, false)
        return SuggestionViewHolder(view)
    }

    override fun onBindViewHolder(holder: SuggestionViewHolder, position: Int) {
        val suggestion = suggestions[position]
        holder.tvTitle.text = suggestion.title
        holder.tvSubtitle.text = suggestion.subtitle

        // Differentiate icon based on type
        if (suggestion.type == "CITY" || suggestion.type == "LOCALITY") {
            holder.ivIcon.setImageResource(R.drawable.ic_location_near)
        } else {
            holder.ivIcon.setImageResource(R.drawable.ic_reddoorz_door)
        }

        holder.itemView.setOnClickListener {
            onItemClick(suggestion)
        }
    }

    override fun getItemCount() = suggestions.size

    fun updateList(newList: List<Suggestion>) {
        suggestions = newList
        notifyDataSetChanged()
    }
}
