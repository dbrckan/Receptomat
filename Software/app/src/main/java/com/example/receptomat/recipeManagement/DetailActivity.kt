package com.example.receptomat.recipeManagement

import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.receptomat.R
import com.example.receptomat.entities.Recipe
import java.text.SimpleDateFormat
import java.util.Locale

class DetailActivity : AppCompatActivity() {
    private lateinit var recipeNameTextView: TextView
    private lateinit var recipeMealTextView: TextView
    private lateinit var ingredientsTextView: TextView
    private lateinit var instructionsTextView: TextView
    private lateinit var preparationTimeTextView: TextView
    private lateinit var dateTextView: TextView
    private lateinit var recipeImageView: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        recipeNameTextView = findViewById(R.id.tv_name_of_recipe)
        recipeMealTextView = findViewById(R.id.tv_meal)
        ingredientsTextView = findViewById(R.id.tv_ingredients)
        instructionsTextView = findViewById(R.id.tv_instructions)
        preparationTimeTextView = findViewById(R.id.tv_preparationTime)
        dateTextView = findViewById(R.id.tv_date)
        recipeImageView = findViewById(R.id.imgv_recipe)

        val selectedRecipe = intent.getParcelableExtra<Recipe>("RECIPE_DATA")

        if (selectedRecipe != null) {
            recipeNameTextView.text = selectedRecipe.name
            recipeMealTextView.text = selectedRecipe.meal.displayName
            ingredientsTextView.text =
                selectedRecipe.ingredients.joinToString("\n") // Sastojci odvojeni novim redom
            instructionsTextView.text = selectedRecipe.instructions
            preparationTimeTextView.text =
                getString(R.string.preparation_time, selectedRecipe.preparationTime)
            val sdf = SimpleDateFormat("dd.MM.yyyy", Locale("hr", "HR"))
            val formattedDate = sdf.format(selectedRecipe.dateOfAddition)
            dateTextView.text = getString(R.string.date_added, formattedDate)

            if (!selectedRecipe.picture.isNullOrEmpty()) {
                val imageResId =
                    resources.getIdentifier(selectedRecipe.picture, "drawable", packageName)
                if (imageResId != 0) {
                    recipeImageView.setImageResource(imageResId)
                } else {
                    val unavailableImageResId =
                        resources.getIdentifier("nedostupno", "drawable", packageName)
                    if (unavailableImageResId != 0) {
                        recipeImageView.setImageResource(unavailableImageResId)
                    } else {
                        recipeImageView.setImageResource(0)
                    }
                }
            } else {
                val unavailableImageResId =
                    resources.getIdentifier("nedostupno", "drawable", packageName)
                if (unavailableImageResId != 0) {
                    recipeImageView.setImageResource(unavailableImageResId)
                } else {
                    recipeImageView.setImageResource(0)
                }
            }
        }
    }
}