package com.example.receptomat.fragments

import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Switch
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.receptomat.R
import com.example.receptomat.ReviewsActivity
import database.ApiService
import database.BasicResponse
import database.RetrofitClient
import database.UpdateNotificationsRequest
import database.UserProfileResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ProfileFragment : Fragment() {

    private lateinit var usernameTextView: TextView
    private lateinit var emailTextView: TextView
    private lateinit var notificationSwitch: Switch

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_profile, container, false)


        usernameTextView = view.findViewById(R.id.usernameTextView)
        emailTextView = view.findViewById(R.id.emailTextView)
        notificationSwitch = view.findViewById(R.id.notificationSwitch)

        val buttonViewReviews = view.findViewById<Button>(R.id.button_view_reviews)
        buttonViewReviews.setOnClickListener {
            val intent = Intent(activity, ReviewsActivity::class.java)
            startActivity(intent)
        }




        fetchUserProfile()


        notificationSwitch.setOnCheckedChangeListener { _, isChecked ->
            val sharedPreferences = requireActivity().getSharedPreferences("user_prefs", MODE_PRIVATE)
            val userId = sharedPreferences.getInt("user_id", -1)
            if (userId != -1) {
                updateNotifications(userId, isChecked)
            } else {
                Toast.makeText(requireContext(), "Invalid user ID", Toast.LENGTH_SHORT).show()
            }
        }

        return view
    }

    private fun fetchUserProfile() {
        val apiService = RetrofitClient.instance.create(ApiService::class.java)
        val sharedPreferences = requireActivity().getSharedPreferences("user_prefs", MODE_PRIVATE)
        val userId = sharedPreferences.getInt("user_id", -1)

        if (userId == -1) {
            Toast.makeText(requireContext(), "Invalid user ID", Toast.LENGTH_SHORT).show()
            return
        }

        apiService.getUserProfile(userId).enqueue(object : Callback<UserProfileResponse> {
            override fun onResponse(call: Call<UserProfileResponse>, response: Response<UserProfileResponse>) {
                if (response.isSuccessful && response.body()?.success == true) {
                    val user = response.body()!!
                    usernameTextView.text = "Username: ${user.username}"
                    emailTextView.text = "Email: ${user.email}"
                    notificationSwitch.isChecked = user.notifications == 1
                } else {
                    Toast.makeText(requireContext(), "Error fetching user profile", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<UserProfileResponse>, t: Throwable) {
                Toast.makeText(requireContext(), "Network error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun updateNotifications(userId: Int, notifications: Boolean) {
        val apiService = RetrofitClient.instance.create(ApiService::class.java)
        val request = UpdateNotificationsRequest(user_id = userId, notifications = if (notifications) 1 else 0)

        apiService.updateNotifications(request).enqueue(object : Callback<BasicResponse> {
            override fun onResponse(call: Call<BasicResponse>, response: Response<BasicResponse>) {
                if (response.isSuccessful && response.body()?.success == true) {
                    Toast.makeText(requireContext(), "Notifications updated successfully", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(requireContext(), "Error updating notifications: ${response.body()?.error}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<BasicResponse>, t: Throwable) {
                Toast.makeText(requireContext(), "Network error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
