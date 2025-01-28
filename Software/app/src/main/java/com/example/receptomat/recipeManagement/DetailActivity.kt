package com.example.receptomat.recipeManagement

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RatingBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.receptomat.R
import com.example.receptomat.adapters.ReviewsForRecipeAdapter
import com.example.receptomat.entities.Review
import database.AddReviewResponse
import database.ApiService
import database.GetRecipeItemsResponse
import database.RecipeItem
import database.RecipeRequest
import database.RetrofitClient
import database.ReviewRequest
import database.ReviewsResponse
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.Date
class DetailActivity : AppCompatActivity() {

    private lateinit var recipeNameTextView: TextView
    private lateinit var recipeMealTextView: TextView
    private lateinit var instructionsTextView: TextView
    private lateinit var preparationTimeTextView: TextView
    private lateinit var preferenceTextView: TextView
    private lateinit var recipeImageView: ImageView
    private lateinit var btnBack: ImageView
    private lateinit var recyclerViewReviews: RecyclerView
    private lateinit var reviewsAdapter: ReviewsForRecipeAdapter
    private lateinit var ratingBar: RatingBar
    private lateinit var commentTextView: TextView
    private lateinit var btnSubmitRating: Button
    private val reviewsList = mutableListOf<Review>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        recipeNameTextView = findViewById(R.id.tv_name_of_recipe)
        recipeMealTextView = findViewById(R.id.tv_category)
        instructionsTextView = findViewById(R.id.tv_instructions)
        preparationTimeTextView = findViewById(R.id.tv_preparationTime)
        recipeImageView = findViewById(R.id.imgv_recipe)
        preferenceTextView = findViewById(R.id.tv_preference_name)
        recyclerViewReviews = findViewById(R.id.recycler_view_reviews)
        btnBack = findViewById(R.id.btn_back)
        ratingBar = findViewById(R.id.ratingBar)
        commentTextView = findViewById(R.id.et_comment)
        btnSubmitRating = findViewById(R.id.btn_submit_rating)

        recyclerViewReviews.layoutManager = LinearLayoutManager(this)
        reviewsAdapter = ReviewsForRecipeAdapter(reviewsList)
        recyclerViewReviews.adapter = reviewsAdapter

        val btnShoppingCart: ImageView = findViewById(R.id.btn_shopping_cart)
        btnShoppingCart.setOnClickListener {
            val recipeId = intent.getIntExtra("RECIPE_ID", -1)
            if (recipeId != -1) {
                fetchItemsForCartAndOpenActivity(recipeId)
            } else {
                Toast.makeText(this, "Recipe ID not found!", Toast.LENGTH_SHORT).show()
            }
        }

        val recipeId = intent.getIntExtra("RECIPE_ID", -1)
        if (recipeId != -1) {
            fetchRecipeData(recipeId)
            fetchReviews(recipeId)
        }

        btnBack.setOnClickListener {
            onBackPressed()
        }

        btnSubmitRating.setOnClickListener {
            submitReview(recipeId)
        }


    }


    private fun getImageForCategory(categoryId: Int): Int {
        return when (categoryId) {
            1 -> R.drawable.breakfast
            2 -> R.drawable.lunch
            3 -> R.drawable.dinner
            4 -> R.drawable.dessert
            else -> R.drawable.nedostupno
        }
    }

    private fun fetchItemsForCartAndOpenActivity(recipeId: Int) {
        val apiService = RetrofitClient.instance.create(ApiService::class.java)
        apiService.getRecipeItems(recipeId).enqueue(object : Callback<GetRecipeItemsResponse> {
            override fun onResponse(call: Call<GetRecipeItemsResponse>, response: Response<GetRecipeItemsResponse>) {
                if (response.isSuccessful) {
                    Log.d("API Response", "Response: ${response.body()}")
                    val items = response.body()?.items ?: emptyList()
                    openEditShoppingListActivity(items)
                } else {
                    Log.e("API Error", "Failed to fetch items: ${response.message()}")
                }
            }



            override fun onFailure(call: Call<GetRecipeItemsResponse>, t: Throwable) {
                Log.e("API Error", "Network error: ${t.message}", t)
                Toast.makeText(this@DetailActivity, "Network error: ${t.message}", Toast.LENGTH_SHORT).show()
            }

        })
    }


    private fun openEditShoppingListActivity(items: List<RecipeItem>) {
        val itemNames = ArrayList(items.map { it.item_name })
        val intent = Intent(this, EditShoppingListActivity::class.java).apply {
            putExtra("is_new", true)
            putStringArrayListExtra("items", itemNames)
        }
        startActivity(intent)
    }


    private fun fetchRecipeData(recipeId: Int) {
        val apiService = RetrofitClient.instance.create(ApiService::class.java)
        CoroutineScope(Dispatchers.Main).launch {
            try {
                val response = apiService.getRecipeById(recipeId)
                if (response.isSuccessful) {
                    val recipeResponseAdd = response.body()
                    Log.d("RecipeResponse", "Recipe: ${recipeResponseAdd?.data}")
                    recipeResponseAdd?.data?.let {
                        populateUI(it)
                    } ?: run {
                        Log.d("EditRecipe", "No recipe data found.")
                    }
                } else {
                    Log.d("RecipeResponse", "Error loading recipe: ${response.message()}")
                    Toast.makeText(this@DetailActivity, "Error loading recipe", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(this@DetailActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun fetchReviews(recipeId: Int) {
        val apiService = RetrofitClient.instance.create(ApiService::class.java)
        val call = apiService.getReviewsForRecipeId(recipeId)
        call.enqueue(object : Callback<ReviewsResponse> {
            override fun onResponse(call: Call<ReviewsResponse>, response: Response<ReviewsResponse>) {
                if (response.isSuccessful) {
                    val reviewsResponse = response.body()
                    if (reviewsResponse != null && !reviewsResponse.reviews.isNullOrEmpty()) {
                        reviewsList.clear()
                        reviewsList.addAll(reviewsResponse.reviews.map { apiReview ->
                            Review(
                                review_id = apiReview.review_id,
                                username = apiReview.username,
                                rating = apiReview.rating,
                                date = apiReview.date,
                                recipe_id = apiReview.recipe_id,
                                comment = apiReview.comment
                            )
                        })
                        reviewsAdapter.notifyDataSetChanged()
                    } else {
                        Log.d("DetailActivity", "Nema recenzija za ovaj recept. + ${reviewsResponse?.recipes} + ${reviewsResponse?.reviews}")
                        Toast.makeText(this@DetailActivity, "Nema recenzija za ovaj recept", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this@DetailActivity, "Greška prilikom učitavanja recenzija: ${response.message()}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ReviewsResponse>, t: Throwable) {
                Toast.makeText(this@DetailActivity, "Greška: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun submitReview(recipeId: Int) {
        val sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE)
        val userId = sharedPreferences.getInt("user_id", -1)
        val comment = commentTextView.text.toString()
        val rating = ratingBar.rating.toInt()
        val date = SimpleDateFormat("yyyy-MM-dd").format(Date())

        if (userId != -1 && recipeId != -1 && comment.isNotEmpty() && rating > 0) {
            val reviewRequest = ReviewRequest(userId, recipeId, comment, rating, date)
            val apiService = RetrofitClient.instance.create(ApiService::class.java)
            val call = apiService.addReview(reviewRequest)
            Log.d("DetailActivity", "Request: $reviewRequest,user $userId, recipe $recipeId,")
            call.enqueue(object : Callback<AddReviewResponse> {
                override fun onResponse(call: Call<AddReviewResponse>, response: Response<AddReviewResponse>) {
                    if (response.isSuccessful) {
                        val dbResponse = response.body()
                        if (dbResponse?.success == true) {
                            Toast.makeText(this@DetailActivity, "Recenzija uspješno dodana", Toast.LENGTH_SHORT).show()
                            fetchReviews(recipeId)
                        } else {
                            Toast.makeText(this@DetailActivity, dbResponse?.error?: "Neuspješno dodavanje recenzije", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        val errorBody = response.errorBody()?.string()
                        Log.d("DetailActivity", "Response error: $errorBody")
                        Toast.makeText(this@DetailActivity, "Greška prilikom dodavanja recenzije: ${response.message()}", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<AddReviewResponse>, t: Throwable) {
                    Log.d("DetailActivity", "Greška prilikom dodavanja recenzije: ${t.message}")
                    t.printStackTrace()
                    Toast.makeText(this@DetailActivity, "Greška: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
        } else {
            Toast.makeText(this, "Molimo ispunite sve podatke za recenziju", Toast.LENGTH_SHORT).show()
        }
    }

    private fun populateUI(recipe: RecipeRequest) {

        recipeNameTextView.text = recipe.name
        instructionsTextView.text = recipe.description
        preparationTimeTextView.text = recipe.time.toString() + " minuta"
        preferenceTextView.text = recipe.preference_name
        recipeMealTextView.text = recipe.category_name

        val categoryId = recipe.category_id ?: -1
        Log.d("DetailActivity", "Kategorija ID $categoryId")
        val imageResId = getImageForCategory(categoryId)
        Log.d("DetailActivity", "Image resource ID: $imageResId")
        recipeImageView.setImageResource(imageResId)

        val ingredientsContainer = findViewById<LinearLayout>(R.id.ingredientsContainer)

        ingredientsContainer.removeAllViews()

        recipe.ingredients?.let {
            if (it.isNotEmpty()) {
                for (ingredient in it) {
                    val ingredientRow = LayoutInflater.from(this).inflate(R.layout.item_ingredient, ingredientsContainer, false)

                    val nameTextView = ingredientRow.findViewById<TextView>(R.id.tv_ingredient_name)
                    val quantityTextView = ingredientRow.findViewById<TextView>(R.id.tv_quantity)
                    val unitTextView = ingredientRow.findViewById<TextView>(R.id.tv_unit)

                    nameTextView.text = ingredient.item_name
                    quantityTextView.text = ingredient.quantity.toString()
                    unitTextView.text = ingredient.unit_name

                    ingredientsContainer.addView(ingredientRow)
                }
            }
        }
    }
}