package com.example.macc_project_app.ui.nearbyrestaurant

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.toolbox.NetworkImageView
import com.example.macc_project_app.R
import com.example.macc_project_app.api.VolleySingleton
import com.example.macc_project_app.data.Restaurant
import java.text.DecimalFormat

class RestaurantAdapter(private val onClick: (Restaurant) -> Unit) :
    ListAdapter<Restaurant, RestaurantAdapter.RestaurantViewHolder>(
    RestaurantDiffCallback
){
    class RestaurantViewHolder (itemView: View, val onClick: (Restaurant) -> Unit) :
        RecyclerView.ViewHolder(itemView) {

        private val restaurantNameView: TextView = itemView.findViewById(R.id.restaurantNameView)
        private val restaurantImageView: NetworkImageView = itemView.findViewById(R.id.restaurantImageView)
        private val restaurantAddressView: TextView = itemView.findViewById(R.id.restaurantAddressView)
        private val restaurantDistanceView: TextView = itemView.findViewById(R.id.restaurantDistanceView)

        private var currentRestaurant: Restaurant? = null
        private val imageLoader = VolleySingleton.getInstance(itemView.context).imageLoader

        init {
            itemView.setOnClickListener {
                currentRestaurant?.let {
                    onClick(it)
                }
            }
        }

        /* Bind restaurant name and image. */
        fun bind(restaurant: Restaurant) {
            currentRestaurant = restaurant

            restaurantNameView.text = restaurant.name
            restaurantAddressView.text = restaurant.address
            restaurantDistanceView.text = String.format("%.3f miles away", restaurant.distance)
            restaurantImageView.setImageUrl(restaurant.imageUrl, imageLoader)
        }
    }

    /* Creates and inflates view and return RestaurantViewHolder */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RestaurantViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.restaurant_item, parent, false)

        return RestaurantViewHolder(view, onClick)
    }

    /* Associate ViewHolder with correspondent data */
    override fun onBindViewHolder(holder: RestaurantViewHolder, position: Int) {
        val restaurant = getItem(position)
        holder.bind(restaurant)
    }
}

object RestaurantDiffCallback : DiffUtil.ItemCallback<Restaurant>() {
    override fun areItemsTheSame(oldItem: Restaurant, newItem: Restaurant): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: Restaurant, newItem: Restaurant): Boolean {
        return oldItem.name == newItem.name
    }
}
