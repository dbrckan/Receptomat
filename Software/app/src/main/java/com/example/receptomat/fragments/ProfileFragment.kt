package com.example.receptomat.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import com.example.receptomat.R
import com.example.receptomat.ReviewsActivity

class ProfileFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_profile, container, false)
        val buttonViewReviews = view.findViewById<Button>(R.id.button_view_reviews)
        buttonViewReviews.setOnClickListener {
            val intent = Intent(activity, ReviewsActivity::class.java)
            startActivity(intent)
        }

        return view
    }
}