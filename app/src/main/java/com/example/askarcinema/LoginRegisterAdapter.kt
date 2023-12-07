package com.example.askarcinema

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2


class LoginRegisterAdapter(fragmentActivity: FragmentActivity) : FragmentStateAdapter(fragmentActivity) {

    override fun getItemCount(): Int {
        return 4
    }

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> SignUpFragment()
            1 -> LoginFragment()
            2 -> UploadFragment()
            3 -> HomeFragment()
            else -> throw IllegalArgumentException("Invalid position")
        }
    }
}