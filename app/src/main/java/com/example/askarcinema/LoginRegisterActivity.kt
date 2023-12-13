package com.example.askarcinema

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.askarcinema.databinding.ActivityLoginRegisterBinding
import com.google.android.material.tabs.TabLayoutMediator

class LoginRegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginRegisterBinding
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoginRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)

        // Check login status
        val isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false)

        if (isLoggedIn) {
            // User is already logged in, navigate to the main activity
            val intent = Intent(this, UserActivity::class.java)
            startActivity(intent)
            finish() // Finish the login activity to prevent going back
        } else {
            // Setup ViewPager2 and TabLayout
            val adapter = LoginRegisterAdapter(this)
            binding.viewPagerLogreg.adapter = adapter

            // Use TabLayoutMediator to connect TabLayout with ViewPager2
            TabLayoutMediator(binding.tabLayoutLogreg, binding.viewPagerLogreg) { tab, position ->
                // You can customize tab labels if needed
                tab.text = when (position) {
                    0 -> "Sign Up"
                    1 -> "Login"
                    else -> "Invalid"
                }
            }.attach()
        }
    }
}
