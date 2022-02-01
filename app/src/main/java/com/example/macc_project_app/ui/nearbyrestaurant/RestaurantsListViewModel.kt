package com.example.macc_project_app.ui.nearbyrestaurant

import android.content.Context
import androidx.lifecycle.*
import com.example.macc_project_app.data.restaurant.Restaurant
import com.example.macc_project_app.domain.GetNearbyRestaurantUseCase
import com.example.macc_project_app.domain.GetReservedRestaurantUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RestaurantsListViewModel @Inject constructor(
    val getNearbyRestaurantUseCase: GetNearbyRestaurantUseCase,
    val getReservedRestaurantUseCase: GetReservedRestaurantUseCase
    ) : ViewModel() {

    private val restaurantsLiveData: MutableLiveData<List<Restaurant>> = MutableLiveData()

    fun getRestaurants(): LiveData<List<Restaurant>> {
        return restaurantsLiveData
    }

    fun loadRestaurants(latitude: String,
                        longitude: String,
                        context: Context,
                        refresh:Boolean=false) {
        viewModelScope.launch {
            val restaurants = getNearbyRestaurantUseCase.invoke(latitude, longitude, context, refresh)
            // fetch reservation immediately so when pass to RestaurantDetailActivity
            // user knows whether can make, update or cancel its reservation
            getReservedRestaurantUseCase(context, refresh)
            restaurantsLiveData.value = restaurants
        }
    }

}