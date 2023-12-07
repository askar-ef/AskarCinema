package com.example.askarcinema

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.askarcinema.databinding.ActivityLoginRegisterBinding
import com.google.android.material.tabs.TabLayoutMediator

class LoginRegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginRegisterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Setup ViewPager2 and TabLayout
        val adapter = LoginRegisterAdapter(this)
        binding.viewPagerLogreg.adapter = adapter

        // Use TabLayoutMediator to connect TabLayout with ViewPager2
        TabLayoutMediator(binding.tabLayoutLogreg, binding.viewPagerLogreg) { tab, position ->
            // You can customize tab labels if needed
            tab.text = when (position) {
                0 -> "Sign Up"
                1 -> "Login"
                2 -> "Upload"
                3 -> "Cinema"
                else -> "Invalid"
            }
        }.attach()
    }
}
