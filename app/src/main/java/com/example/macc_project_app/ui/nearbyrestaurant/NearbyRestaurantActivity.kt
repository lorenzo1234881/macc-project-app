package com.example.macc_project_app.ui.nearbyrestaurant

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

import android.content.Intent
import android.location.Location
import android.util.Log

import android.view.Menu
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.macc_project_app.R
import com.example.macc_project_app.data.restaurant.Restaurant
import com.example.macc_project_app.ui.restaurantdetail.RestaurantDetailActivity
import com.google.android.gms.location.*
import dagger.hilt.android.AndroidEntryPoint
import java.text.DateFormat
import java.util.*

const val REQUEST_CHECK_SETTINGS = 0x1
const  val REQUEST_LOCATION = 0x2

// Keys for storing activity state in the Bundle.
const val KEY_LOCATION = "location"
const val KEY_LAST_UPDATED_TIME_STRING = "last-updated-time-string"

const val RESTAURANT_ID = "restaurant id"

@AndroidEntryPoint
class NearbyRestaurantActivity : AppCompatActivity() {

    /**
     * Creates a callback for receiving location events.
     */
    inner class NearbyRestaurantLocationCallback: LocationCallback() {

        override fun onLocationResult(locationResult: LocationResult) {
            super.onLocationResult(locationResult)
            mCurrentLocation = locationResult.lastLocation
            mLastUpdateTime = DateFormat.getTimeInstance().format(Date())

            val latitude = mCurrentLocation.latitude.toString()
            val longitude = mCurrentLocation.longitude.toString()

            Log.d(TAG, "longitude: $longitude, latitude: $latitude")

            mLocationController.stopLocationUpdates()

            mRestaurantListViewModel.loadRestaurants(latitude, longitude,this@NearbyRestaurantActivity, mRefresh)
        }
    }

    private val TAG: String = NearbyRestaurantActivity::class.java.simpleName

    private val mRestaurantListViewModel : RestaurantsListViewModel by viewModels()

    private lateinit var mSwipeRefreshLayout: SwipeRefreshLayout

    private var mLastUpdateTime: String? = ""

    private lateinit var mCurrentLocation: Location

    private var mRefresh : Boolean = false

    private val mLocationController : LocationController by lazy {
        LocationController(this@NearbyRestaurantActivity, NearbyRestaurantLocationCallback())
    }

    private val restaurantAdapter = RestaurantAdapter {
            restaurant -> adapterOnClick(restaurant)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_nearby_stores)

        // Update values using data stored in the Bundle.
        updateValuesFromBundle(savedInstanceState)

        val recyclerView: RecyclerView = findViewById(R.id.restaurantRecyclerView)
        recyclerView.adapter = restaurantAdapter

        mRestaurantListViewModel.getRestaurants().observe(this) {
            it?.let {
                Log.d(TAG, "RestaurantViewModel changed to $it")
                restaurantAdapter.submitList(it)
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

        mLocationController.startLocationUpdates()
    }

    /* Opens RestaurantDetailActivity when RecyclerView item is clicked. */
    private fun adapterOnClick(restaurant: Restaurant) {
        val intent = Intent(this, RestaurantDetailActivity()::class.java)
        Log.d(TAG, "View details of restaurant: $restaurant")
        intent.putExtra(RESTAURANT_ID, restaurant.id)
        startActivity(intent)
    }

    override fun onPause() {
        super.onPause()
        mLocationController.stopLocationUpdates()
    }

    override fun onStop() {
        super.onStop()
        mLocationController.stopLocationUpdates()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.nearby_stores_menu, menu)
        return true
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

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode == REQUEST_LOCATION) {
            Log.i("RequestPermission", "Received response for Location permission request.")
            mLocationController.startLocationUpdates()
        }
        else {
            Log.i("RequestPermission", "Location permission was not granted.")
        }
    }

    /**
     * Updates fields based on data stored in the bundle.
     *
     * @param savedInstanceState The activity state saved in the Bundle.
     */
    private fun updateValuesFromBundle(savedInstanceState: Bundle?) {
        if (savedInstanceState != null) {

            // Update the value of mCurrentLocation from the Bundle and update the UI to show the
            // correct latitude and longitude.
            if (savedInstanceState.keySet().contains(KEY_LOCATION)) {
                // Since KEY_LOCATION was found in the Bundle, we can be sure that mCurrentLocation
                // is not null.
                mCurrentLocation = savedInstanceState.getParcelable(KEY_LOCATION)!!
            }

            // Update the value of mLastUpdateTime from the Bundle and update the UI.
            if (savedInstanceState.keySet().contains(KEY_LAST_UPDATED_TIME_STRING)) {
                mLastUpdateTime =
                    savedInstanceState.getString(KEY_LAST_UPDATED_TIME_STRING)
            }
        }
    }

    private fun initiateRefresh() {

        mRefresh = true

        restaurantAdapter.submitList(null)

        Log.i(TAG, "initiateRefresh")

        mLocationController.startLocationUpdates()
    }

}