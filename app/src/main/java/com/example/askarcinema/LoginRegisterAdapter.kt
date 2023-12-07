package com.example.askarcinema

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2


class LoginRegisterAdapter(fragmentActivity: FragmentActivity) : FragmentStateAdapter(fragmentActivity) {

    override fun getItemCount(): Int {
        return 5
    }

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> SignUpFragment()
            1 -> LoginFragment()
            2 -> UploadFragment()
            3 -> HomeFragment()
            4 -> MonitorFragment()
            else -> throw IllegalArgumentException("Invalid position")
        }
    }
}