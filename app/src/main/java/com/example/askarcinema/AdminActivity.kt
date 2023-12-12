package com.example.askarcinema

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.askarcinema.databinding.ActivityAdminBinding
import com.google.android.material.tabs.TabLayoutMediator

class AdminActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAdminBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdminBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Setup ViewPager2 and TabLayout
        val adapter = AdminAdapter(this)
        binding.viewPagerAdmin.adapter = adapter

        // Use TabLayoutMediator to connect TabLayout with ViewPager2
        TabLayoutMediator(binding.tabLayoutAdmin, binding.viewPagerAdmin) { tab, position ->
            // You can customize tab labels if needed
            tab.text = when (position) {
                0 -> "Monitor"
                1 -> "Upload"
                else -> "Invalid"
            }
        }.attach()
    }
}