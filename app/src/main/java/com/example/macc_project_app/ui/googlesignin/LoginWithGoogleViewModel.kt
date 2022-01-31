package com.example.macc_project_app.ui.googlesignin

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.macc_project_app.api.LoginWithGoogleApi
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginWithGoogleViewModel @Inject constructor(
    val loginWithGoogleApi: LoginWithGoogleApi
): ViewModel() {

    private val isAuthLiveData: MutableLiveData<Boolean> = MutableLiveData()

    fun sendToken(idToken: String, context: Context) {
        viewModelScope.launch {
            isAuthLiveData.value = loginWithGoogleApi.sendToken(idToken, context)
        }
    }

    fun IsAuthLiveData(): MutableLiveData<Boolean>  {
        return isAuthLiveData
    }
}