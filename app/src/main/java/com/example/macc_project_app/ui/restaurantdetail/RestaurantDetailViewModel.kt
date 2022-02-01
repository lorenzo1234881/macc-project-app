package com.example.macc_project_app.ui.restaurantdetail

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.macc_project_app.api.MakeReservationApi
import com.example.macc_project_app.data.restaurant.Restaurant
import com.example.macc_project_app.domain.GetNearbyRestaurantUseCase
import com.example.macc_project_app.domain.GetReservedRestaurantUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RestaurantDetailViewModel @Inject constructor(
    val getNearbyRestaurantUseCase: GetNearbyRestaurantUseCase,
    val makeReservationApi: MakeReservationApi,
    val getReservedRestaurantUseCase: GetReservedRestaurantUseCase
) : ViewModel() {


    private val restaurantLiveData: MutableLiveData<Restaurant> = MutableLiveData()
    private val reservationStateLiveData: MutableLiveData<Boolean> = MutableLiveData()

    fun getRestaurantLiveData(): LiveData<Restaurant> {
        return restaurantLiveData
    }

    fun getReservationStateLiveData(): LiveData<Boolean> {
        return reservationStateLiveData
    }

    fun getRestaurant(restaurantId: Long) {
        viewModelScope.launch {
            restaurantLiveData.value = getNearbyRestaurantUseCase(restaurantId)
        }
    }

    fun makeReservation(restaurantId:Long, numberSeats: Int, context:Context) {
        viewModelScope.launch {
            reservationStateLiveData.value = makeReservationApi.sendReservation(restaurantId, numberSeats, context)
        }
    }

    fun existsReservation(restaurantId: Long) {
        viewModelScope.launch {
            reservationStateLiveData.value = getReservedRestaurantUseCase(restaurantId)
        }

    }

}