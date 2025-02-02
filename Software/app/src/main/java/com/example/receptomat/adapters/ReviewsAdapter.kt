package com.example.receptomat.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.receptomat.R
import com.example.receptomat.entities.Category
import com.example.receptomat.entities.RecipeDB
import com.example.receptomat.entities.Review
import database.ApiService
import database.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ReviewsAdapter(
    private val reviews: List<Review>,
    private val recipes: List<RecipeDB>,
    private val removeReviewCallback: (Review) -> Unit
) : RecyclerView.Adapter<ReviewsAdapter.ReviewViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReviewViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_review, parent, false)
        return ReviewViewHolder(view)
    }

    override fun onBindViewHolder(holder: ReviewViewHolder, position: Int) {
        val review = reviews[position]
        val recipe = recipes.find { it.recipe_id == review.recipe_id.toInt() }
        holder.bind(review, recipe)
        holder.removeButton.setOnClickListener {
            removeReviewCallback(review)
        }
    }

    override fun getItemCount(): Int = reviews.size

    class ReviewViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val comment: TextView = itemView.findViewById(R.id.comment)
        val rating: TextView = itemView.findViewById(R.id.rating)
        val date: TextView = itemView.findViewById(R.id.date)
        val recipeName: TextView = itemView.findViewById(R.id.recipe_name)
        val recipeMeal: TextView = itemView.findViewById(R.id.recipe_meal)
        val preparationTime: TextView = itemView.findViewById(R.id.preparation_time)
        val removeButton: Button = itemView.findViewById(R.id.button_remove_review)

        fun bind(review: Review, recipe: RecipeDB?) {
            comment.text = review.comment
            rating.text = review.rating.toString()
            date.text = review.date
            recipeName.text = recipe?.name ?: "Unknown"
            recipeMeal.text = recipe?.description ?: "Unknown"
            preparationTime.text = recipe?.let { itemView.context.getString(R.string.preparation_time, it.time) } ?: "Unknown"

            recipe?.let {
                fetchCategoryName(it.category_id) { categoryName ->
                    recipeMeal.text = categoryName ?: itemView.context.getString(R.string.unknown_meal)
                }
            }
        }

        private fun fetchCategoryName(categoryId: Int, callback: (String?) -> Unit) {
            val apiService = RetrofitClient.instance.create(ApiService::class.java)
            val call = apiService.getCategories()

            call.enqueue(object : Callback<List<Category>> {
                override fun onResponse(call: Call<List<Category>>, response: Response<List<Category>>) {
                    if (response.isSuccessful) {
                        val categories = response.body()
                        Log.d("ReviewsAdapter", "Fetched categories: $categories")
                        val category = categories?.find { it.category_id == categoryId }
                        if (category == null) {
                            Log.e("ReviewsAdapter", "Category ID $categoryId not found in fetched categories")
                        }
                        callback(category?.name)
                    } else {
                        Log.e("ReviewsAdapter", "Failed to fetch categories: ${response.errorBody()?.string()}")
                        callback(null)
                    }
                }

                override fun onFailure(call: Call<List<Category>>, t: Throwable) {
                    Log.e("ReviewsAdapter", "Error fetching categories", t)
                    callback(null)
                }
            })
        }
    }
}
