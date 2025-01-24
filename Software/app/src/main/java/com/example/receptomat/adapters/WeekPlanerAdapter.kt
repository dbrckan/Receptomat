package com.example.receptomat.adapters

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.receptomat.weekMenu.MondayFragment
import com.example.receptomat.weekMenu.TuesdayFragment
import com.example.receptomat.weekMenu.WednesdayFragment
import com.example.receptomat.weekMenu.ThursdayFragment
import com.example.receptomat.weekMenu.FridayFragment
import com.example.receptomat.weekMenu.SaturdayFragment
import com.example.receptomat.weekMenu.SundayFragment

class WeekPlanerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {

    override fun getItemCount(): Int {
        return 7
    }

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> MondayFragment()
            1 -> TuesdayFragment()
            2 -> WednesdayFragment()
            3 -> ThursdayFragment()
            4 -> FridayFragment()
            5 -> SaturdayFragment()
            6 -> SundayFragment()
            else -> throw IllegalStateException("Invalid position")
        }
    }
}