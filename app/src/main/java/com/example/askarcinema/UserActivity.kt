package com.example.askarcinema

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.askarcinema.databinding.ActivityUserBinding
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

        // Use TabLayoutMediator to connect TabLayout with ViewPager2
        TabLayoutMediator(binding.tabLayoutUser, binding.viewPagerUser) { tab, position ->
            // You can customize tab labels if needed
            tab.text = when (position) {
                0 -> "Movies"
                1 -> "Profile"
                else -> "Invalid"
            }
        }.attach()
    }
}