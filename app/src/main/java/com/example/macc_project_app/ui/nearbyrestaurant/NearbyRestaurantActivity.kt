package com.example.macc_project_app.ui.nearbyrestaurant

import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.macc_project_app.R
import com.example.macc_project_app.data.restaurant.Restaurant
import com.example.macc_project_app.ui.googlesignin.LoginWithGoogleActivity
import com.example.macc_project_app.ui.reservationslist.ReservationsListActivity
import com.example.macc_project_app.ui.restaurantdetail.RestaurantDetailActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationResult
import com.google.android.material.navigation.NavigationView
import dagger.hilt.android.AndroidEntryPoint
import java.text.DateFormat
import java.util.*


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

            mRestaurantListViewModel.loadRestaurants(latitude, longitude,this@NearbyRestaurantActivity, mRefresh)
        }
    }

    private val TAG: String = NearbyRestaurantActivity::class.java.simpleName

    private val mRestaurantListViewModel : RestaurantsListViewModel by viewModels()

    private lateinit var mSwipeRefreshLayout: SwipeRefreshLayout
    private var mRefresh : Boolean = false

    private var mLastUpdateTime: String? = ""
    private lateinit var mCurrentLocation: Location
    private val mLocationController : LocationController by lazy {
        LocationController(this@NearbyRestaurantActivity, NearbyRestaurantLocationCallback())
    }

    private val restaurantAdapter = RestaurantAdapter {
            restaurant -> adapterOnClick(restaurant)
    }

    var drawerLayout: DrawerLayout? = null
    var actionBarDrawerToggle: ActionBarDrawerToggle? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_nearby_stores)

        val recyclerView: RecyclerView = findViewById(R.id.restaurantRecyclerView)
        recyclerView.adapter = restaurantAdapter

        mRestaurantListViewModel.getRestaurants().observe(this) {
            it?.let {
                Log.d(TAG, "restaurantListLiveData changed to $it")
                restaurantAdapter.submitList(it)
                mSwipeRefreshLayout.isRefreshing = false
            }
        }

        mRestaurantListViewModel.getAuthLiveData().observe(this) {
            it?.let {
                Log.d(TAG, "authLiveData changed to $it")

                val googleSignInClient = GoogleSignIn.getClient(applicationContext, GoogleSignInOptions.DEFAULT_SIGN_IN)

                signOut(googleSignInClient)
                revokeAccess(googleSignInClient)

                // pass to LoginWithGoogleActivity after sending logout
                val intent = Intent(this, LoginWithGoogleActivity::class.java)
                startActivity(intent)
            }
        }

        mSwipeRefreshLayout = findViewById(R.id.swiperefresh)

        mSwipeRefreshLayout.setOnRefreshListener {
            Log.d(TAG, "onRefresh called from SwipeRefreshLayout")

            // This method performs the actual data-refresh operation.
            // The method calls setRefreshing(false) when it's finished.
            initiateRefresh()
        }

        // drawer layout instance to toggle the menu icon to open
        // drawer and back button to close drawer
        drawerLayout = findViewById(R.id.my_drawer_layout)
        actionBarDrawerToggle =
            ActionBarDrawerToggle(this, drawerLayout, R.string.nav_open, R.string.nav_close)


        // drawer layout instance to toggle the menu icon to open
        // drawer and back button to close drawer
        drawerLayout = findViewById(R.id.my_drawer_layout)
        actionBarDrawerToggle =
            ActionBarDrawerToggle(this, drawerLayout, R.string.nav_open, R.string.nav_close)

        val navigationView: NavigationView = findViewById(R.id.my_navigation_view)

        navigationView.setNavigationItemSelectedListener {
            return@setNavigationItemSelectedListener when (it.itemId) {
                R.id.nav_reservations -> {
                    val intent = Intent(this, ReservationsListActivity::class.java)
                    startActivity(intent)
                    true
                }
                R.id.nav_logout -> {
                    Log.d(TAG, "logout")
                    mRestaurantListViewModel.logout(this)
                    true
                }
                else -> {
                    false
                }
            }
        }

        // pass the Open and Close toggle for the drawer layout listener
        // to toggle the button
        drawerLayout?.addDrawerListener(actionBarDrawerToggle!!)
        actionBarDrawerToggle!!.syncState()

        // to make the Navigation drawer icon always appear on the action bar
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

    }

    /* Opens RestaurantDetailActivity when RecyclerView item is clicked. */
    private fun adapterOnClick(restaurant: Restaurant) {
        val intent = Intent(this, RestaurantDetailActivity()::class.java)
        Log.d(TAG, "View details of restaurant: $restaurant")
        intent.putExtra(RESTAURANT_ID, restaurant.id)
        startActivity(intent)
    }

    override fun onResume() {
        super.onResume()
        mLocationController.startLocationUpdates()
    }

    override fun onPause() {
        super.onPause()
        mLocationController.stopLocationUpdates()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.nearby_stores_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (actionBarDrawerToggle?.onOptionsItemSelected(item) == true) {
            return true
        }
        else {
            when (item.itemId) {
                R.id.menu_refresh -> {
                    Log.d(TAG, "Refresh menu item selected")

                    // We make sure that the SwipeRefreshLayout is displaying it's refreshing indicator
                    if (!mSwipeRefreshLayout.isRefreshing) {
                        mSwipeRefreshLayout.isRefreshing = true
                    }

                    // Start our refresh background task
                    initiateRefresh()
                    return true
                }
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
        when (requestCode) {
            mLocationController.REQUEST_LOCATION -> {
                Log.d("RequestPermission", "Received response for Location permission request.")
                for (grantResult in grantResults) {
                    if (grantResult == PackageManager.PERMISSION_DENIED) {
                        Log.d("RequestPermission", "Permission denied")
                    }
                }
            }
            mLocationController.REQUEST_CHECK_SETTINGS -> {
                Log.d("RequestPermission", "Check settings.")
            }
            else -> {
                Log.d("RequestPermission", "Location permission was not granted.")
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            mLocationController.REQUEST_CHECK_SETTINGS -> {
                Log.d(TAG, "Check settings")
            }
        }
    }

    private fun initiateRefresh() {

        mRefresh = true

        restaurantAdapter.submitList(null)

        Log.d(TAG, "initiateRefresh")

        mLocationController.startLocationUpdates()
    }

    private fun signOut(googleSignInClient: GoogleSignInClient) {
        googleSignInClient.signOut()
            .addOnCompleteListener(this) {
                Log.d(TAG, "Sign Out")
            }
    }

    private fun revokeAccess(googleSignInClient: GoogleSignInClient) {
        googleSignInClient.revokeAccess()
            .addOnCompleteListener(this) {
                Log.d(TAG, "Revoke Access")
            }
    }

}