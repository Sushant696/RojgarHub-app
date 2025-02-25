package com.example.rojgarhub.ui.activity

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.example.rojgarhub.ui.fragment.Profile
import com.example.rojgarhub.R
import com.example.rojgarhub.databinding.ActivityNavigationBinding
import com.example.rojgarhub.ui.fragment.Home
import com.example.rojgarhub.ui.fragment.Jobs
import com.example.rojgarhub.ui.fragment.MyApplicationsFragment

class NavigationActivity : AppCompatActivity() {
    lateinit var navigationBinding: ActivityNavigationBinding

    private fun replaceFragment(fragment: Fragment) {
        val fragmentManager: FragmentManager = supportFragmentManager
        val fragmentTransaction: FragmentTransaction = fragmentManager.beginTransaction()

        fragmentTransaction.replace(R.id.frameLayout, fragment)
        fragmentTransaction.commit()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        navigationBinding = ActivityNavigationBinding.inflate(layoutInflater)

        setContentView(navigationBinding.root)

        replaceFragment(Home())
        navigationBinding.buttomNavigation.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.menuHome -> replaceFragment(Home())
                R.id.menuJobs -> replaceFragment(Jobs())
                R.id.menuApplications -> replaceFragment(MyApplicationsFragment())
                R.id.menuProfile -> replaceFragment(Profile())
                else -> {}
            }
            true
        }
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0)
            insets
        }
    }
}