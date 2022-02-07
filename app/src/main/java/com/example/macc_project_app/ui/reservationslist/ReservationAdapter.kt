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
        private val reservationTimeView: TextView = itemView.findViewById(R.id.reservationTimeView)
        private val reservationDateView: TextView = itemView.findViewById(R.id.reservationDateView)


        private var currentReservedRestaurant: ReservedRestaurant? = null
        private val imageLoader = VolleySingleton.getInstance(itemView.context).imageLoader

        init {
            itemView.setOnClickListener {
                currentReservedRestaurant?.let {
                    onClick(it)
                }
            }
        }

        /* Bind reservation name and image. */
        fun bind(reservedRestaurant: ReservedRestaurant) {
            currentReservedRestaurant = reservedRestaurant

            val reservation = reservedRestaurant.reservation

            restaurantNameView.text = reservedRestaurant.restaurant.name
            restaurantAddressView.text = reservedRestaurant.restaurant.address
            numberSeatsView.text = String.format("Number of seats reserved: %d", reservation.numberSeats)
            reservationTimeView.text = String.format("Time of reservation: %d:%d", reservation.hour, reservation.minute)
            reservationDateView.text = String.format("Date of reservation: %d/%d/%d", reservation.dayOfMonth, reservation.month, reservation.year)


            restaurantImageView.setImageUrl(reservedRestaurant.restaurant.imageUrl, imageLoader)

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
            val reservedRestaurant = getItem(position)
            holder.bind(reservedRestaurant)
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
