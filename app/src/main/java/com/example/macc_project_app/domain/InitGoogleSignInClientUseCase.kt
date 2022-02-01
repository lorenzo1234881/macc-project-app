package com.example.macc_project_app.domain

import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions

class InitGoogleSignInClientUseCase {
    companion object {
        operator fun invoke(applicationContext: Context) : GoogleSignInClient {
            val ai: ApplicationInfo = applicationContext.packageManager
                .getApplicationInfo(applicationContext.packageName, PackageManager.GET_META_DATA)
            val clientServerId = ai.metaData["client_server_id"] as String

            // Configure sign-in to request the user's ID, email address, and basic
            // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
            val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(clientServerId)
                .requestEmail()
                .build()

            // Build a GoogleSignInClient with the options specified by gso.
            return GoogleSignIn.getClient(applicationContext, gso)
        }
    }
}