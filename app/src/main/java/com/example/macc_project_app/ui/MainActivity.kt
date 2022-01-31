package com.example.macc_project_app.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.example.macc_project_app.R
import com.example.macc_project_app.ui.googlesignin.LoginWithGoogleActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    fun switchNextActivity(view: View) {
        val intent = Intent(this, LoginWithGoogleActivity::class.java)
        startActivity(intent)
    }
}