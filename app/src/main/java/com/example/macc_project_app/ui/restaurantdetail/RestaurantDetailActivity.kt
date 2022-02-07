package com.example.macc_project_app.ui.restaurantdetail

import android.app.DatePickerDialog
import android.app.Dialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.text.format.DateFormat
import android.util.Log
import android.widget.*
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.toolbox.NetworkImageView
import com.example.macc_project_app.R
import com.example.macc_project_app.api.VolleySingleton
import com.example.macc_project_app.data.reservation.Reservation
import com.example.macc_project_app.ui.nearbyrestaurant.RESTAURANT_ID
import dagger.hilt.android.AndroidEntryPoint
import java.util.*


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

    private lateinit var mReservation : Reservation


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_restaurant_detail)

        reserveButton.isEnabled = false
        cancelButton.isEnabled = false

        val bundle: Bundle? = intent.extras
        if (bundle != null) {
            mCurrentRestaurantId = bundle.getLong(RESTAURANT_ID)
            mReservation = Reservation(mCurrentRestaurantId!!)
        }

        reserveButton.setOnClickListener {
            showDateAndTimeDialogs {
                showNumberOfSeatsDialog {
                    // Once user set all reservation properties, call api
                    mRestaurantDetailViewModel.makeReservation(mReservation, this)
                }
            }
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

    private fun showDateAndTimeDialogs(executeNext: () -> Unit) {
        val calendar: Calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)
        val datePickerDialog =
            DatePickerDialog(
                this,
                { view, year, month, dayOfMonth ->
                    mReservation.year = year
                    mReservation.month = month
                    mReservation.dayOfMonth = dayOfMonth

                    val c = Calendar.getInstance()
                    val timePickerDialog = TimePickerDialog(
                        this@RestaurantDetailActivity,
                        { timePicker: TimePicker, hourOfDay: Int, minute: Int ->
                            mReservation.hour = hourOfDay
                            mReservation.minute = minute
                            executeNext()
                        },
                        c[Calendar.HOUR],
                        c[Calendar.MINUTE],
                        DateFormat.is24HourFormat(this@RestaurantDetailActivity)
                    )
                    timePickerDialog.show()
                },
                year,
                month,
                day)
        datePickerDialog.show()
    }

    private fun showNumberOfSeatsDialog(executeNext : () -> Unit) {
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
            mReservation.numberSeats = np.value
            executeNext()
        }
        d.show()
    }

}