package com.example.macc_project_app.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.macc_project_app.R
import com.example.macc_project_app.ui.googlesignin.LoginWithGoogleActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    fun switchNextActivity() {
        val intent = Intent(this, LoginWithGoogleActivity::class.java)
        startActivity(intent)
    }
}