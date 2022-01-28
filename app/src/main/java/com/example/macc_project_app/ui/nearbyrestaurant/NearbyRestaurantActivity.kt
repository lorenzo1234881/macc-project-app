package com.example.macc_project_app.ui.nearbyrestaurant

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.content.pm.PackageManager

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.IntentSender
import android.location.Location
import android.util.Log

import android.widget.Toast

import android.os.Looper
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.activity.viewModels
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.macc_project_app.R
import com.example.macc_project_app.data.Restaurant
import com.example.macc_project_app.ui.restaurantdetail.RestaurantDetailActivity
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.location.LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY
import dagger.hilt.android.AndroidEntryPoint
import java.text.DateFormat
import java.util.*

/**
 * The desired interval for location updates. Inexact. Updates may be more or less frequent.
 */
const val UPDATE_INTERVAL_IN_MILLISECONDS: Long = 100000

/**
 * The fastest rate for active location updates. Exact. Updates will never be more frequent
 * than this value.
 */
const val FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS = UPDATE_INTERVAL_IN_MILLISECONDS / 2

const val REQUEST_CHECK_SETTINGS = 0x1
const  val REQUEST_LOCATION = 0x2

// Keys for storing activity state in the Bundle.
const val KEY_REQUESTING_LOCATION_UPDATES = "requesting-location-updates"
const val KEY_LOCATION = "location"
const val KEY_LAST_UPDATED_TIME_STRING = "last-updated-time-string"

const val RESTAURANT_ID = "restaurant id"

@AndroidEntryPoint
class NearbyRestaurantActivity : AppCompatActivity() {

    private val mRestaurantListViewModel : RestaurantsListViewModel by viewModels()

    private lateinit var mSwipeRefreshLayout: SwipeRefreshLayout
    private var mRefresh : Boolean = false

    private var mLastUpdateTime: String? = null
    private val TAG = NearbyRestaurantActivity::class.java.simpleName

    private lateinit var mCurrentLocation: Location
    private lateinit var mLocationCallback: LocationCallback
    private lateinit var mLocationRequest: LocationRequest
    private lateinit var mFusedLocationClient : FusedLocationProviderClient
    private lateinit var mSettingsClient : SettingsClient
    private lateinit var mLocationSettingsRequest: LocationSettingsRequest
    private var mRequestingLocationUpdates: Boolean = true

    private val restaurantAdapter = RestaurantAdapter { restaurant -> adapterOnClick(restaurant)}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_nearby_stores)

        mLastUpdateTime = ""

        // Update values using data stored in the Bundle.
        updateValuesFromBundle(savedInstanceState)

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        mSettingsClient = LocationServices.getSettingsClient(this)

        createLocationCallback()
        createLocationRequest()
        buildLocationSettingsRequest()

        val recyclerView: RecyclerView = findViewById(R.id.restaurantRecyclerView)
        recyclerView.adapter = restaurantAdapter

        mRestaurantListViewModel.getRestaurants().observe(this, {
            it?.let {
                Log.d(TAG, "RestaurantViewModel changed to $it")
                restaurantAdapter.submitList(it)
                mSwipeRefreshLayout.isRefreshing = false
            }
        })

        mSwipeRefreshLayout = findViewById(R.id.swiperefresh)

        mSwipeRefreshLayout.setOnRefreshListener {
            Log.i(TAG, "onRefresh called from SwipeRefreshLayout")


            // This method performs the actual data-refresh operation.
            // The method calls setRefreshing(false) when it's finished.
            initiateRefresh()
        }

    }

    /* Opens RestaurantDetailActivity when RecyclerView item is clicked. */
    private fun adapterOnClick(restaurant: Restaurant) {
        val intent = Intent(this, RestaurantDetailActivity()::class.java)
        Log.d(TAG, "View details of restaurant: $restaurant")
        intent.putExtra(RESTAURANT_ID, restaurant.id)
        startActivity(intent)
    }

    override fun onStart() {
        super.onStart()

        mRequestingLocationUpdates = true
        if (mRequestingLocationUpdates && locationPermissionsGranted()) {
            startLocationUpdates()
        } else if (!locationPermissionsGranted()) {
            requestLocationPermissions()
        }
    }

    override fun onPause() {
        super.onPause()
        stopLocationUpdates()
    }

    override fun onStop() {
        super.onStop()
        stopLocationUpdates()
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
            startLocationUpdates()
        }
        else {
            Log.i("RequestPermission", "Location permission was not granted.")
        }
    }

    private fun locationPermissionsGranted(): Boolean {
        return ActivityCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestLocationPermissions() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION),
            REQUEST_LOCATION
        )
    }

    /**
     * Updates fields based on data stored in the bundle.
     *
     * @param savedInstanceState The activity state saved in the Bundle.
     */
    private fun updateValuesFromBundle(savedInstanceState: Bundle?) {
        if (savedInstanceState != null) {
            if (savedInstanceState.keySet()
                    .contains(KEY_REQUESTING_LOCATION_UPDATES)
            ) {
                mRequestingLocationUpdates = savedInstanceState.getBoolean(
                    KEY_REQUESTING_LOCATION_UPDATES
                )
            }

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

    private fun createLocationRequest() {
        mLocationRequest = LocationRequest.create().apply {
            interval = UPDATE_INTERVAL_IN_MILLISECONDS
            fastestInterval= FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS
            priority = PRIORITY_BALANCED_POWER_ACCURACY
        }
    }

    /**
     * Creates a callback for receiving location events.
     */
    private fun createLocationCallback() {
        mLocationCallback = object : LocationCallback() {

            override fun onLocationResult(locationResult: LocationResult) {
                super.onLocationResult(locationResult)
                mCurrentLocation = locationResult.lastLocation
                mLastUpdateTime = DateFormat.getTimeInstance().format(Date())

                val latitude = mCurrentLocation.latitude.toString()
                val longitude = mCurrentLocation.longitude.toString()

                Log.d(TAG, "longitude: $longitude, latitude: $latitude")

                stopLocationUpdates()

                mRestaurantListViewModel.loadRestaurants(latitude, longitude,this@NearbyRestaurantActivity, mRefresh)
            }
        }
    }

    private fun buildLocationSettingsRequest() {
        val builder = LocationSettingsRequest.Builder()
        builder.addLocationRequest(mLocationRequest)
        mLocationSettingsRequest = builder.build()
    }


    @SuppressLint("MissingPermission")
    private fun startLocationUpdates() {

        // TODO check network is turned on

        // Begin by checking if the device has the necessary location settings.
        mSettingsClient.checkLocationSettings(mLocationSettingsRequest)
            .addOnSuccessListener(this) {
                Log.i(TAG, "All location settings are satisfied.")
                mFusedLocationClient.requestLocationUpdates(
                    mLocationRequest,
                    mLocationCallback, Looper.myLooper()!!
                )
            }
            .addOnFailureListener(this) { e ->
                when ((e as ApiException).statusCode) {
                    LocationSettingsStatusCodes.RESOLUTION_REQUIRED -> {
                        Log.i(
                            TAG,
                            "Location settings are not satisfied. Attempting to upgrade " +
                                    "location settings "
                        )
                        try {
                            // Show the dialog by calling startResolutionForResult(), and check the
                            // result in onActivityResult().
                            val rae = e as ResolvableApiException
                            rae.startResolutionForResult(
                                this,
                                REQUEST_CHECK_SETTINGS
                            )

                            // TODO try again startLocationUpdates()

                        } catch (sie: IntentSender.SendIntentException) {
                            Log.i(TAG, "PendingIntent unable to execute request.")
                        }
                    }
                    LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE -> {
                        val errorMessage = "Location settings are inadequate, and cannot be " +
                                "fixed here. Fix in Settings."
                        Log.e(TAG, errorMessage)
                        Toast.makeText(this, errorMessage, Toast.LENGTH_LONG)
                            .show()
                        mRequestingLocationUpdates = false
                    }
                }
            }
    }

    /**
     * Removes location updates from the FusedLocationApi.
     * Should be called when activity in a paused or stopped state.
     */
    private fun stopLocationUpdates() {
        if (!mRequestingLocationUpdates) {
            Log.d(TAG, "stopLocationUpdates: updates never requested, no-op.")
            return
        }

        mFusedLocationClient.removeLocationUpdates(mLocationCallback)
            .addOnCompleteListener(this) {
                mRequestingLocationUpdates = false
            }
    }

    private fun initiateRefresh() {

        mRefresh = true

        restaurantAdapter.submitList(null)

        Log.i(TAG, "initiateRefresh")
        mRequestingLocationUpdates = true
        if (mRequestingLocationUpdates && locationPermissionsGranted()) {
            startLocationUpdates()
        } else if (!locationPermissionsGranted()) {
            requestLocationPermissions()
        }
    }
}