package com.example.binaryus

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        val firebaseAuth=FirebaseAuth.getInstance()
        var firebaseUser = firebaseAuth.currentUser

        Handler(Looper.getMainLooper()).postDelayed(Runnable{
            if ((firebaseUser==null)||(!firebaseUser!!.isEmailVerified)){
                startActivity(Intent(this,AuthActivity::class.java))
            }
            else
            {
                startActivity(Intent(this,MainPage::class.java))
            }
            finish()
        }, 2000)
    }
}