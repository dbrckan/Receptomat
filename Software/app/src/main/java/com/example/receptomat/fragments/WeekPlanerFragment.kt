package com.example.receptomat.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.viewpager2.widget.ViewPager2
import com.example.receptomat.R
import com.example.receptomat.adapters.WeekPlanerAdapter

class WeekPlanerFragment : Fragment(R.layout.fragment_week_planer) {
    private var selectedButton: TextView? = null
    private lateinit var btnMonday: TextView
    private lateinit var btnTuesday: TextView
    private lateinit var btnWednesday: TextView
    private lateinit var btnThursday: TextView
    private lateinit var btnFriday: TextView
    private lateinit var btnSaturday: TextView
    private lateinit var btnSunday: TextView
    private lateinit var viewPager: ViewPager2

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewPager = view.findViewById(R.id.viewpager)
        btnMonday = view.findViewById(R.id.btn_monday)
        btnTuesday = view.findViewById(R.id.btn_tuesday)
        btnWednesday = view.findViewById(R.id.btn_wednesday)
        btnThursday = view.findViewById(R.id.btn_thursday)
        btnFriday = view.findViewById(R.id.btn_friday)
        btnSaturday = view.findViewById(R.id.btn_saturday)
        btnSunday = view.findViewById(R.id.btn_sunday)

        viewPager.adapter = WeekPlanerAdapter(this)

        btnMonday.setOnClickListener { selectButton(btnMonday, 0) }
        btnTuesday.setOnClickListener { selectButton(btnTuesday, 1) }
        btnWednesday.setOnClickListener { selectButton(btnWednesday, 2) }
        btnThursday.setOnClickListener { selectButton(btnThursday, 3) }
        btnFriday.setOnClickListener { selectButton(btnFriday, 4) }
        btnSaturday.setOnClickListener { selectButton(btnSaturday, 5) }
        btnSunday.setOnClickListener { selectButton(btnSunday, 6) }
    }

    private fun selectButton(selected: TextView, position: Int) {
        selectedButton?.isSelected = false

        selected.isSelected = true

        viewPager.currentItem = position

        selectedButton = selected
    }
}