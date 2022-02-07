package com.example.macc_project_app.ui.reservationslist

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.macc_project_app.data.restaurant.ReservedRestaurant
import com.example.macc_project_app.domain.GetReservedRestaurantUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ReservationListViewModel @Inject constructor(
    private val getReservedRestaurantUseCase: GetReservedRestaurantUseCase
): ViewModel() {
    private val reservationsListLiveData : MutableLiveData<List<ReservedRestaurant>> = MutableLiveData()

    fun loadReservations(context: Context, refresh:Boolean=false) {
        viewModelScope.launch {
            reservationsListLiveData.value = getReservedRestaurantUseCase(context, refresh)
        }
    }

    fun getReservationsLiveData(): MutableLiveData<List<ReservedRestaurant>> {
        return reservationsListLiveData
    }

}