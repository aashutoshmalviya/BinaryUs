package com.example.binaryus

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager
import com.example.binaryus.fragments.*
import com.google.android.material.bottomappbar.BottomAppBar
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.tabs.TabLayout
import com.google.firebase.auth.FirebaseAuth

@Suppress("DEPRECATION")
class MainPage : AppCompatActivity() {
    lateinit var logout: ImageButton
    lateinit var bottomNavigationView: BottomNavigationView
    lateinit var toolbar: androidx.appcompat.widget.Toolbar
    lateinit var toolbar_tv: TextView
    lateinit var addpostbt:FloatingActionButton
    lateinit var prevfrag:Fragment

    private lateinit var bottomAppBar: BottomAppBar
    private lateinit var floatingActionButton: FloatingActionButton
    private lateinit var cancelPost:ImageButton
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_page)

        iniit()
        navigate()
        clicklistener()
    }

    private fun navigate() {
        setCurrentFragment(Home())
        bottomNavigationView.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.home -> {
                    logout.visibility= GONE
                    toolbar_tv.text="BinaryUs"
                    setCurrentFragment(Home())
                }
                R.id.Search-> {
                    logout.visibility= GONE
                    setCurrentFragment(Search())
                }
                R.id.Notification -> {
                    logout.visibility= GONE
                    setCurrentFragment(Notification())
                }
                R.id.Profile-> {
                    logout.visibility= VISIBLE
                    toolbar_tv.text="Profile"
                    setCurrentFragment(Profile())
                }

            }
            true
        }
    }

    private fun setCurrentFragment(fragment: Fragment) =
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.flFragment, fragment)
            commit()
        }

    private fun clicklistener() {
        addpostbt.setOnClickListener{
            cancelPost.visibility= VISIBLE
            floatingActionButton.visibility= GONE
            bottomAppBar.visibility= GONE
            toolbar_tv.text="Add Post"
            logout.visibility= GONE
            setCurrentFragment(Add())
        }
        logout!!.setOnClickListener {
            val firebaseAuth = FirebaseAuth.getInstance()
            firebaseAuth.signOut()
            startActivity(Intent(this, AuthActivity::class.java))
            finish()
        }
        cancelPost.setOnClickListener {
            cancelPost.visibility= GONE
            floatingActionButton.visibility= VISIBLE
            bottomAppBar.visibility= VISIBLE
            toolbar_tv.text="Binary Us"
            setCurrentFragment(Home())
        }
    }



    private fun iniit() {
        logout = findViewById(R.id.logoutbutton)
        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        toolbar_tv = findViewById(R.id.toolbar_tv)
        bottomNavigationView = findViewById(R.id.bottomNavigationView)
        addpostbt=findViewById(R.id.addpostbt)
        floatingActionButton=findViewById(R.id.addpostbt)
        bottomAppBar=findViewById(R.id.bottomAppBar)
        cancelPost=findViewById(R.id.cancelPost)
    }
}