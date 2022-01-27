package com.example.macc_project_app.ui.restaurantdetail

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.activity.viewModels
import com.android.volley.toolbox.NetworkImageView
import com.example.macc_project_app.R
import com.example.macc_project_app.api.VolleySingleton
import com.example.macc_project_app.ui.nearbyrestaurant.RESTAURANT_ID
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class RestaurantDetailActivity : AppCompatActivity() {

    private val TAG = RestaurantDetailActivity::class.java.simpleName
    private val mRestaurantDetailViewModel: RestaurantDetailViewModel by viewModels()
    private val imageLoader = VolleySingleton.getInstance(this).imageLoader

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_restaurant_detail)

        var currentRestaurantId : Long? = null

        val restaurantName: TextView = findViewById(R.id.restaurantName)
        val restaurantImage: NetworkImageView = findViewById(R.id.restaurantImageView)
        val restaurantDescription: TextView = findViewById(R.id.restaurantDescription)

        val bundle: Bundle? = intent.extras
        if (bundle != null) {
            currentRestaurantId = bundle.getLong(RESTAURANT_ID)
        }

        mRestaurantDetailViewModel.getRestaurantLiveData().observe(this, {
            it?.let {
                Log.d(TAG, "mRestaurantDetailViewModel changed to $it")

                restaurantName.text = it.name
                restaurantDescription.text = it.description
                restaurantImage.setImageUrl(it.imageUrl, imageLoader)
            }
        })

        currentRestaurantId?.let {
            mRestaurantDetailViewModel.getRestaurant(it)
        }
    }
}