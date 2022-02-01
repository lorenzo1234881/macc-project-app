package com.example.macc_project_app.ui.googlesignin

import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.macc_project_app.api.LoginWithGoogleApi
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
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

    fun isAuthLiveData(): MutableLiveData<Boolean>  {
        return isAuthLiveData
    }
}