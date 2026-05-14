package com.example.reddoorz

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

/**
 * ImageSliderAdapter: Displays a list of images in a ViewPager2.
 */
class ImageSliderAdapter(private val images: List<String>) :
    RecyclerView.Adapter<ImageSliderAdapter.SliderViewHolder>() {

    class SliderViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val ivImage: ImageView = view.findViewById(R.id.ivSliderImage)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SliderViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_image_slider, parent, false)
        return SliderViewHolder(view)
    }

    override fun onBindViewHolder(holder: SliderViewHolder, position: Int) {
        val imageName = images[position]
        val context = holder.itemView.context
        
        // Resolve drawable resource
        val resId = context.resources.getIdentifier(imageName, "drawable", context.packageName)

        Glide.with(context)
            .load(if (resId != 0) resId else android.R.drawable.ic_menu_gallery)
            .placeholder(android.R.drawable.ic_menu_gallery)
            .into(holder.ivImage)
    }

    override fun getItemCount() = images.size
}
