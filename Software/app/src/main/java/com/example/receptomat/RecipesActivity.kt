package com.example.receptomat

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.viewpager2.widget.ViewPager2
import com.example.receptomat.adapters.MainPagerAdapter
import com.example.receptomat.fragments.CartFragment
import com.example.receptomat.fragments.FavoriteRecipesFragment
import com.example.receptomat.fragments.HomeFragment
import com.example.receptomat.fragments.ProfileFragment
import com.example.receptomat.fragments.WeekPlanerFragment
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class RecipesActivity : AppCompatActivity() {
    lateinit var tabLayout: TabLayout
    lateinit var viewPager2: ViewPager2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_recipes)


        tabLayout = findViewById(R.id.tabs)
        viewPager2 = findViewById(R.id.viewpager)

        val mainPagerAdapter = MainPagerAdapter(supportFragmentManager, lifecycle)

        mainPagerAdapter.addFragment(
            MainPagerAdapter.FragmentItem(
                R.drawable.ic_baseline_home_24,
                HomeFragment::class
            )
        )
        mainPagerAdapter.addFragment(
            MainPagerAdapter.FragmentItem(
                R.drawable.ic_baseline_fastfood_24,
                WeekPlanerFragment::class
            )
        )
        mainPagerAdapter.addFragment(
            MainPagerAdapter.FragmentItem(
                R.drawable.ic_baseline_favorite_24,
                FavoriteRecipesFragment::class
            )
        )
        mainPagerAdapter.addFragment(
            MainPagerAdapter.FragmentItem(
                R.drawable.ic_baseline_shopping_cart_24,
                CartFragment::class
            )
        )
        mainPagerAdapter.addFragment(
            MainPagerAdapter.FragmentItem(
                R.drawable.ic_baseline_account_circle_24,
                ProfileFragment::class
            )
        )

        viewPager2.adapter = mainPagerAdapter

        TabLayoutMediator(tabLayout, viewPager2) { tab, position ->
            tab.setIcon(mainPagerAdapter.fragmentItems[position].iconRes)
        }.attach()

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.recipes)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

    }
}