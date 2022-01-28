package com.example.macc_project_app.ui.nearbyrestaurant

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.IntentSender
import android.content.pm.PackageManager
import android.location.Location
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import dagger.Provides
import dagger.hilt.android.qualifiers.ActivityContext
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ActivityRetainedScoped
import dagger.hilt.android.scopes.ActivityScoped
import java.util.*
import javax.inject.Inject

/**
 * The desired interval for location updates. Inexact. Updates may be more or less frequent.
 */
const val UPDATE_INTERVAL_IN_MILLISECONDS: Long = 100000

/**
 * The fastest rate for active location updates. Exact. Updates will never be more frequent
 * than this value.
 */
const val FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS = UPDATE_INTERVAL_IN_MILLISECONDS / 2

class LocationController (
    private val activity: AppCompatActivity,
    private val mLocationCallback: LocationCallback
){
    private val TAG: String = LocationController::class.java.simpleName

    private val mLocationRequest: LocationRequest = LocationRequest.create().apply {
        interval = UPDATE_INTERVAL_IN_MILLISECONDS
        fastestInterval= FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS
        priority = LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY
    }

    private val mFusedLocationClient = LocationServices.getFusedLocationProviderClient(activity)

    private val mSettingsClient = LocationServices.getSettingsClient(activity)

    private val mLocationSettingsRequest: LocationSettingsRequest by lazy {
        val builder = LocationSettingsRequest.Builder()
        builder.addLocationRequest(mLocationRequest)
        builder.build()
    }
    private var mRequestingLocationUpdates: Boolean = true

    fun locationPermissionsGranted(): Boolean {
        return ActivityCompat.checkSelfPermission(
            activity,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    fun requestLocationPermissions() {
        ActivityCompat.requestPermissions(
            activity,
            arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION),
            REQUEST_LOCATION
        )
    }

    @SuppressLint("MissingPermission")
    fun startLocationUpdates() {

        mRequestingLocationUpdates = true

        if(!locationPermissionsGranted()) {
            requestLocationPermissions()
            return
        }

        // TODO check network is turned on

        // Begin by checking if the device has the necessary location settings.
        mSettingsClient.checkLocationSettings(mLocationSettingsRequest)
            .addOnSuccessListener(activity) {
                Log.i(TAG, "All location settings are satisfied.")
                mFusedLocationClient.requestLocationUpdates(
                    mLocationRequest,
                    mLocationCallback, Looper.myLooper()!!
                )
            }
            .addOnFailureListener(activity) { e ->
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
                                activity,
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
                        Toast.makeText(activity, errorMessage, Toast.LENGTH_LONG)
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
    fun stopLocationUpdates() {
        if (!mRequestingLocationUpdates) {
            Log.d(TAG, "stopLocationUpdates: updates never requested, no-op.")
            return
        }

        mFusedLocationClient.removeLocationUpdates(mLocationCallback)
            .addOnCompleteListener(activity) {
                mRequestingLocationUpdates = false
            }
    }


}