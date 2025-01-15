package com.example.receptomat.recipeManagement

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.receptomat.R
import database.ApiService
import database.RecipeRequest
import database.RetrofitClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class DetailActivity : AppCompatActivity() {

    private lateinit var recipeNameTextView: TextView
    private lateinit var recipeMealTextView: TextView
    private lateinit var instructionsTextView: TextView
    private lateinit var preparationTimeTextView: TextView
    private lateinit var preferenceTextView: TextView
    private lateinit var recipeImageView: ImageView
    private lateinit var btnBack: ImageView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        recipeNameTextView = findViewById(R.id.tv_name_of_recipe)
        recipeMealTextView = findViewById(R.id.tv_category)
        instructionsTextView = findViewById(R.id.tv_instructions)
        preparationTimeTextView = findViewById(R.id.tv_preparationTime)
        recipeImageView = findViewById(R.id.imgv_recipe)
        preferenceTextView = findViewById(R.id.tv_preference_name)

        val recipeId = intent.getIntExtra("RECIPE_ID", -1)
        if (recipeId != -1) {
            fetchRecipeData(recipeId)
        }

        btnBack = findViewById(R.id.btn_back)

        btnBack.setOnClickListener {
            onBackPressed()
        }
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

    private fun populateUI(recipe: RecipeRequest) {
        if (recipe == null) {
            Log.d("DetailActivity", "Recipe data is null.")
            return
        }

        recipeNameTextView.text = recipe.name
        instructionsTextView.text = recipe.description
        preparationTimeTextView.text = recipe.time.toString() + " minuta"
        preferenceTextView.text = recipe.preference_name
        recipeMealTextView.text = recipe.category_name

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

        recipeImageView.setImageResource(R.drawable.nedostupno)
    }

}
