package com.example.macc_project_app.ui.googlesignin

import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.common.SignInButton
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import androidx.activity.result.contract.ActivityResultContracts
import com.example.macc_project_app.R
import com.google.android.gms.auth.api.signin.GoogleSignIn.*
import com.google.android.gms.tasks.Task
import android.util.Log
import com.example.macc_project_app.ui.nearbyrestaurant.NearbyRestaurantActivity
import com.google.android.gms.common.api.ApiException

class LoginWithGoogleActivity : AppCompatActivity() {
    private val TAG: String = LoginWithGoogleActivity::class.java.simpleName
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

        val ai: ApplicationInfo = applicationContext.packageManager
            .getApplicationInfo(applicationContext.packageName, PackageManager.GET_META_DATA)
        val client_server_id = ai.metaData["client_server_id"] as String

        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(client_server_id)
            .requestEmail()
            .build()

        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = getClient(this, gso)
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
        val account: GoogleSignInAccount? = getLastSignedInAccount(this)
        updateUI(account)
    }

    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val account: GoogleSignInAccount = completedTask.getResult(ApiException::class.java)

            // Signed in successfully, show authenticated UI.
            updateUI(account)
        } catch (e: ApiException) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w(TAG, "signInResult:failed code=" + e.statusCode)
            updateUI(null)
        }
    }

    private fun updateUI(account: GoogleSignInAccount?) {
        if(account != null) {
            Log.d(TAG, "Pass to next activity")
            val intent = Intent(this, NearbyRestaurantActivity::class.java)
            startActivity(intent)
        }
    }

}