package com.example.askarcinema

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.example.askarcinema.databinding.ActivityUserBinding
import com.example.askarcinema.roomDatabase.MovieDao
import com.example.askarcinema.roomDatabase.MovieRoomDatabase
import com.google.android.material.tabs.TabLayoutMediator

class UserActivity : AppCompatActivity() {
    private lateinit var binding: ActivityUserBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Setup ViewPager2 and TabLayout
        val adapter = UserAdapter(this)
        binding.viewPagerUser.adapter = adapter

        binding.bottomNavbar.setOnItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_home -> binding.viewPagerUser.setCurrentItem(0, true)
                R.id.nav_profile -> binding.viewPagerUser.setCurrentItem(1, true)
                else -> return@setOnItemSelectedListener false
            }
            true
        }
    }
}