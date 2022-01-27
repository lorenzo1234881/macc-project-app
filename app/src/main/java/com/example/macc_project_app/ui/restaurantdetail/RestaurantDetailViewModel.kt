package com.example.macc_project_app.ui.restaurantdetail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.macc_project_app.data.Restaurant
import com.example.macc_project_app.domain.GetNearbyRestaurantUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RestaurantDetailViewModel @Inject constructor(
    val getNearbyRestaurantUseCase: GetNearbyRestaurantUseCase
) : ViewModel() {

    private val restaurantLiveData: MutableLiveData<Restaurant> = MutableLiveData()

    fun getRestaurantLiveData(): LiveData<Restaurant> {
        return restaurantLiveData
    }

    fun getRestaurant(restaurantId: Long) {
        viewModelScope.launch {
            restaurantLiveData.value = getNearbyRestaurantUseCase.invoke(restaurantId)
        }
    }

}