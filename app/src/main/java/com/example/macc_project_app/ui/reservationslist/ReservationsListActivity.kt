package com.example.macc_project_app.ui.reservationslist

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.macc_project_app.R
import com.example.macc_project_app.data.restaurant.ReservedRestaurant
import com.example.macc_project_app.ui.nearbyrestaurant.RESTAURANT_ID
import com.example.macc_project_app.ui.restaurantdetail.RestaurantDetailActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ReservationsListActivity : AppCompatActivity() {

    private val TAG: String = ReservationsListActivity::class.java.simpleName

    private val mReservationListViewModel : ReservationListViewModel by viewModels()

    private lateinit var mSwipeRefreshLayout: SwipeRefreshLayout
    private var mRefresh : Boolean = false

    private val reservationAdapter = ReservationAdapter {
        reservation -> adapterOnClick(reservation)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reservations_list)

        val recyclerView: RecyclerView = findViewById(R.id.reservationRecyclerView)
        recyclerView.adapter = reservationAdapter

        mReservationListViewModel.getReservationsLiveData().observe(this) {
            it?.let {
                Log.d(TAG, "ReservationViewModel changed to $it")
                reservationAdapter.submitList(it)

                mSwipeRefreshLayout.isRefreshing = false
            }
        }

        mSwipeRefreshLayout = findViewById(R.id.swiperefresh)

        mSwipeRefreshLayout.setOnRefreshListener {
            Log.i(TAG, "onRefresh called from SwipeRefreshLayout")

            // This method performs the actual data-refresh operation.
            // The method calls setRefreshing(false) when it's finished.
            initiateRefresh()
        }

        mReservationListViewModel.loadReservations(this)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_refresh -> {
                Log.i(TAG, "Refresh menu item selected")

                // We make sure that the SwipeRefreshLayout is displaying it's refreshing indicator
                if (!mSwipeRefreshLayout.isRefreshing) {
                    mSwipeRefreshLayout.isRefreshing = true
                }

                // Start our refresh background task
                initiateRefresh()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun initiateRefresh() {

        mRefresh = true

        reservationAdapter.submitList(null)

        Log.i(TAG, "initiateRefresh")

        mReservationListViewModel.loadReservations(this, mRefresh)
    }

    private fun adapterOnClick(reservation: ReservedRestaurant) {
        val intent = Intent(this, RestaurantDetailActivity()::class.java)
        val restaurant = reservation.restaurant
        Log.d(TAG, "View details of restaurant: $restaurant")
        intent.putExtra(RESTAURANT_ID, restaurant.id)
        startActivity(intent)
    }
}