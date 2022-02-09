package com.example.macc_project_app.ui.openstreetmap

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.macc_project_app.data.restaurant.Restaurant
import com.example.macc_project_app.domain.GetNearbyRestaurantUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import org.osmdroid.api.IMapController
import org.osmdroid.config.Configuration
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.overlay.Marker
import javax.inject.Inject


@HiltViewModel
class OsmViewModel @Inject constructor(
    val getNearbyRestaurantUseCase: GetNearbyRestaurantUseCase
) : ViewModel() {

    val restaurantLiveData: MutableLiveData<Restaurant> = MutableLiveData()

    fun getRestaurant(restaurantId: Long) {
        viewModelScope.launch {
            restaurantLiveData.value = getNearbyRestaurantUseCase(restaurantId)
        }
    }

    fun loadMap(mapController: IMapController, startPoint:GeoPoint) {
        viewModelScope.launch {
            Configuration.getInstance().userAgentValue = "macc-project/1.0"
            mapController.setZoom(18)
            mapController.setCenter(startPoint)
        }
    }
}