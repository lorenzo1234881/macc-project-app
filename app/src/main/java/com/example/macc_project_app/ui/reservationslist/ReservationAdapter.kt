package com.example.macc_project_app.ui.reservationslist

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
import com.example.macc_project_app.data.reservation.Reservation
import com.example.macc_project_app.data.restaurant.ReservedRestaurant

class ReservationAdapter(private val onClick: (ReservedRestaurant) -> Unit)  :
    ListAdapter<ReservedRestaurant, ReservationAdapter.ReservationViewHolder>(
        ReservedRestaurantDiffCallback
    ){

    class ReservationViewHolder (itemView: View, val onClick: (ReservedRestaurant) -> Unit) :
        RecyclerView.ViewHolder(itemView) {

        private val restaurantNameView: TextView = itemView.findViewById(R.id.restaurantNameView)
        private val restaurantImageView: NetworkImageView = itemView.findViewById(R.id.restaurantImageView)
        private val restaurantAddressView: TextView = itemView.findViewById(R.id.restaurantAddressView)
        private val numberSeatsView: TextView = itemView.findViewById(R.id.numberSeatsView)


        private var currentReservation: ReservedRestaurant? = null
        private val imageLoader = VolleySingleton.getInstance(itemView.context).imageLoader

        init {
            itemView.setOnClickListener {
                currentReservation?.let {
                    onClick(it)
                }
            }
        }

        /* Bind reservation name and image. */
        fun bind(reservation: ReservedRestaurant) {
            currentReservation = reservation

            restaurantNameView.text = reservation.restaurant.name
            restaurantAddressView.text = reservation.restaurant.address
            numberSeatsView.text = String.format("Number of seats reserved: %d", reservation.numberSeats)
            restaurantImageView.setImageUrl(reservation.restaurant.imageUrl, imageLoader)

        }
    }


    override fun onCreateViewHolder(
            parent: ViewGroup,
            viewType: Int
        ): ReservationViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.reservation_item, parent, false)

            return ReservationViewHolder(view, onClick)
        }

        override fun onBindViewHolder(holder: ReservationViewHolder, position: Int) {
            val reservation = getItem(position)
            holder.bind(reservation)
        }

}

object ReservedRestaurantDiffCallback : DiffUtil.ItemCallback<ReservedRestaurant>() {
    override fun areItemsTheSame(oldItem: ReservedRestaurant, newItem: ReservedRestaurant): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: ReservedRestaurant, newItem: ReservedRestaurant): Boolean {
        return oldItem.restaurant == newItem.restaurant
    }
}
