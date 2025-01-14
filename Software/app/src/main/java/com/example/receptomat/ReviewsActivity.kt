package com.example.receptomat

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.receptomat.adapters.ReviewsAdapter
import com.example.receptomat.entities.Meal
import com.example.receptomat.entities.Recipe
import com.example.receptomat.entities.Review
import database.ApiService
import database.BasicResponse
import database.RetrofitClient
import com.example.receptomat.entities.ReviewsResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.Date

class ReviewsActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ReviewsAdapter
    private val userReviews = mutableListOf<Review>()
    private val recepies = mutableListOf<Recipe>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reviews)

        recyclerView = findViewById(R.id.recyclerViewReviews)
        recyclerView.layoutManager = LinearLayoutManager(this)

        adapter = ReviewsAdapter(userReviews, recepies) { review -> removeReview(review) }
        recyclerView.adapter = adapter

        val sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE)
        val userId = sharedPreferences.getInt("user_id", -1)
        if (userId != -1) {
            Log.d("ReviewsActivity", "Korisnički ID: $userId")
            loadReviewsForUser(userId)
        } else {
            Toast.makeText(this, "Korisnički ID je null", Toast.LENGTH_SHORT).show()
        }
    }

    private fun loadReviewsForUser(userId: Int) {
        val apiService = RetrofitClient.instance.create(ApiService::class.java)
        val call = apiService.getReviewsForRecipeUserId(userId)

        call.enqueue(object : Callback<ApiService.ReviewsResponse> {
            override fun onResponse(call: Call<ApiService.ReviewsResponse>, response: Response<ApiService.ReviewsResponse>) {
                if (response.isSuccessful) {
                    val reviewsResponse = response.body()
                    if (reviewsResponse != null && !reviewsResponse.reviews.isNullOrEmpty()) {
                        userReviews.clear()
                        userReviews.addAll(reviewsResponse.reviews.map { apiReview ->
                            Review(
                                review_id = apiReview.review_id,
                                comment = apiReview.comment,
                                rating = apiReview.rating,
                                date = apiReview.date,
                                recipe_id = apiReview.recipe_id
                            )
                        })

                        recepies.clear()
                        recepies.addAll(reviewsResponse.recipes.map { recipe ->
                            Recipe(
                                recipe_id = recipe.recipe_id,
                                name = recipe.name,
                                meal = recipe.meal?.let { Meal.fromDisplayName(it.displayName) } ?: Meal.BREAKFAST,
                                ingredients = emptyList(),
                                instructions = "",
                                preparationTime = recipe.preparationTime,
                                image_path = recipe.image_path,
                                dateOfAddition = Date()
                            )
                        })

                        adapter.notifyDataSetChanged()
                    } else {
                        Toast.makeText(this@ReviewsActivity, "Nema pronađenih recenzija", Toast.LENGTH_SHORT).show()
                        Log.d("ReviewsActivity", "Nema pronađenih recenzija")
                    }
                } else {
                    val errorMessage = response.errorBody()?.string()
                    Toast.makeText(this@ReviewsActivity, "Greška pri učitavanju recenzija: $errorMessage", Toast.LENGTH_SHORT).show()
                    Log.e("ReviewsActivity", "Greška pri učitavanju recenzija: $errorMessage")
                }
            }

            override fun onFailure(call: Call<ApiService.ReviewsResponse>, t: Throwable) {
                Toast.makeText(this@ReviewsActivity, "Greška pri učitavanju recenzija: ${t.message}", Toast.LENGTH_SHORT).show()
                Log.e("ReviewsActivity", "Greška pri učitavanju recenzija: ${t.message}")
                t.printStackTrace()
            }
        })
    }

    private fun removeReview(review: Review) {
        val apiService = RetrofitClient.instance.create(ApiService::class.java)
        val sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE)
        val userId = sharedPreferences.getInt("user_id", -1)

        if (userId != -1 && review.recipe_id != null) {
            val call = apiService.removeReview(userId, review.recipe_id.toInt())
            call.enqueue(object : Callback<BasicResponse> {
                override fun onResponse(call: Call<BasicResponse>, response: Response<BasicResponse>) {
                    if (response.isSuccessful) {
                        val basicResponse = response.body()
                        if (basicResponse?.success == true) {
                            userReviews.remove(review)
                            adapter.notifyDataSetChanged()
                            Toast.makeText(this@ReviewsActivity, "Recenzija uspješno uklonjena", Toast.LENGTH_SHORT).show()
                        } else {
                            val errorMessage = basicResponse?.error ?: "Neuspješno uklanjanje recenzije"
                            Toast.makeText(this@ReviewsActivity, errorMessage, Toast.LENGTH_SHORT).show()
                            Log.e("ReviewsActivity", "Greška pri uklanjanju recenzije: $errorMessage")
                        }
                    } else {
                        val errorMessage = response.errorBody()?.string() ?: "Neuspješno uklanjanje recenzije"
                        Toast.makeText(this@ReviewsActivity, errorMessage, Toast.LENGTH_SHORT).show()
                        Log.e("ReviewsActivity", "Greška pri uklanjanju recenzije: $errorMessage")
                    }
                }

                override fun onFailure(call: Call<BasicResponse>, t: Throwable) {
                    Toast.makeText(this@ReviewsActivity, "Neuspješno uklanjanje recenzije", Toast.LENGTH_SHORT).show()
                    Log.e("ReviewsActivity", "Greška pri uklanjanju recenzije: ${t.message}")
                }
            })
        } else {
            Toast.makeText(this@ReviewsActivity, "Korisnički ID ili ID recepta je null", Toast.LENGTH_SHORT).show()
            Log.e("ReviewsActivity", "Korisnički ID ili ID recepta je null")
        }
    }
}