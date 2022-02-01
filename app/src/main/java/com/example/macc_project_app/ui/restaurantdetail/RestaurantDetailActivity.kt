package com.example.macc_project_app.ui.restaurantdetail

import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.NumberPicker
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
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

    private val restaurantName: TextView by lazy { findViewById(R.id.restaurantName) }
    private val restaurantImage: NetworkImageView by lazy { findViewById(R.id.restaurantImageView) }
    private val restaurantDescription: TextView by lazy { findViewById(R.id.restaurantDescription) }
    private val reserveButton: Button by lazy { findViewById(R.id.reserveTable) }
    private val cancelButton: Button by lazy {findViewById(R.id.cancelReservation)}

    private var mCurrentRestaurantId : Long? = null
    private var mNumberOfSeats = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_restaurant_detail)

        reserveButton.isEnabled = false
        cancelButton.isEnabled = false

        val bundle: Bundle? = intent.extras
        if (bundle != null) {
            mCurrentRestaurantId = bundle.getLong(RESTAURANT_ID)
        }

        reserveButton.setOnClickListener {
            show()
        }

        cancelButton.setOnClickListener {
            mRestaurantDetailViewModel.cancelReservation(mCurrentRestaurantId, this)
        }

        mRestaurantDetailViewModel.getRestaurantLiveData().observe(this) {
            it?.let {
                Log.d(TAG, "restaurantListLiveData changed to $it")

                restaurantName.text = it.name
                restaurantDescription.text = it.description
                restaurantImage.setImageUrl(it.imageUrl, imageLoader)
            }
        }

        mRestaurantDetailViewModel.getReservationStateLiveData().observe(this) {
            it?.let {
                Log.d(TAG, "reservationStateLiveData changed to $it")
                if(it) {
                    reserveButton.setText(R.string.update_reservation)
                    reserveButton.isEnabled = true
                    cancelButton.isEnabled = true
                }
                else {
                    reserveButton.setText(R.string.reserve_table)
                    reserveButton.isEnabled = true
                    cancelButton.isEnabled = false
                }
            }
        }

        mCurrentRestaurantId?.let {
            mRestaurantDetailViewModel.getRestaurant(it)
            mRestaurantDetailViewModel.existsReservation(it)
        }

    }

    fun show() {
        val d = Dialog(this@RestaurantDetailActivity)
        d.setTitle("NumberPicker")
        d.setContentView(R.layout.number_seats_dialog)
        val setButton = d.findViewById(R.id.setButton) as Button
        val np = d.findViewById(R.id.numberSeatsPicker) as NumberPicker
        np.maxValue = 100
        np.minValue = 1
        np.wrapSelectorWheel = false
        setButton.setOnClickListener {
            d.dismiss()
            mNumberOfSeats = np.value
            mRestaurantDetailViewModel.makeReservation(mCurrentRestaurantId!!,  mNumberOfSeats, this)
        }
        d.show()
    }

}