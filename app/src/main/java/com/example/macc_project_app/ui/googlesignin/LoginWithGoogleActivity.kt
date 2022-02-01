package com.example.macc_project_app.ui.googlesignin

import android.app.Activity
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.macc_project_app.R
import com.example.macc_project_app.ui.nearbyrestaurant.NearbyRestaurantActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignIn.*
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginWithGoogleActivity : AppCompatActivity() {
    private val TAG: String = LoginWithGoogleActivity::class.java.simpleName
    private val mLoginWithGoogleViewModel : LoginWithGoogleViewModel by viewModels()
    private lateinit var mGoogleSignInClient: GoogleSignInClient
    private lateinit var signInButton : SignInButton

    private val mSignInActivity = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data: Intent? = result.data
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            val task: Task<GoogleSignInAccount> = getSignedInAccountFromIntent(data)
            handleSignInResult(task)
        }
        else {
            Log.d(TAG, result.toString())
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_google_sign_in)

        mLoginWithGoogleViewModel.isAuthLiveData().observe(this) {
            it?.let {
                if(it) {
                    val intent = Intent(this, NearbyRestaurantActivity::class.java)
                    startActivity(intent)
                }
                else {
                    Log.d(TAG, "authentication failed")
                }
            }
        }

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
        mGoogleSignInClient = getClient(applicationContext, gso)

        signInButton = this.findViewById(R.id.sign_in_button)
        signInButton.setOnClickListener {
            val signInIntent = mGoogleSignInClient.signInIntent
            mSignInActivity.launch(signInIntent)
        }

    }

    override fun onStart() {
        super.onStart()
        // Check for existing Google Sign In account, if the user is already signed in
        // the GoogleSignInAccount will be non-null.
        val account = getLastSignedInAccount(this)

        // TODO update UI to show that the token is being sent

        if(account != null) {
            if(account.isExpired) {
                refreshIdToken()
            }
            else {
                sendToken(account)
            }
        }
    }

    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val account  = completedTask.getResult(ApiException::class.java)
            sendToken(account)

        } catch (e: ApiException) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w(TAG, "signInResult:failed code=" + e.statusCode)
         }
    }

    private fun sendToken(account: GoogleSignInAccount) {
        val idToken = account.idToken
        if(idToken != null) {
            mLoginWithGoogleViewModel.sendToken(idToken, this)
        }
    }

    private fun refreshIdToken() {
        // This asynchronous branch will attempt to sign in the user silently and get a valid ID token.
        mGoogleSignInClient.silentSignIn()
            .addOnCompleteListener(
                this
            ) { task ->
                handleSignInResult(task)
            }
    }

}