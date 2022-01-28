package com.example.macc_project_app.ui.nearbyrestaurant

import android.app.Application
import android.content.Context
import androidx.lifecycle.*
import com.example.macc_project_app.data.Restaurant
import com.example.macc_project_app.domain.GetNearbyRestaurantUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RestaurantsListViewModel @Inject constructor(
    val getNearbyRestaurantUseCase: GetNearbyRestaurantUseCase
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
            restaurantsLiveData.value = getNearbyRestaurantUseCase.invoke(latitude, longitude, context, refresh)
        }
    }
}