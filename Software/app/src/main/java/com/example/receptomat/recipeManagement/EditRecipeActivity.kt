package com.example.receptomat.recipeManagement

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity
import com.example.receptomat.R
import com.example.receptomat.entities.Meal
import com.example.receptomat.entities.Recipe

class EditRecipeActivity : AppCompatActivity() {
    private lateinit var recipeNameEditText: EditText
    private lateinit var recipeImageView: ImageView
    private lateinit var ingredientsEditText: EditText
    private lateinit var instructionsEditText: EditText
    private lateinit var timeEditText: EditText
    private lateinit var saveButton: Button
    private lateinit var cancelButton: Button
    private lateinit var recipe: Recipe
    private lateinit var mealTypeSpinner: Spinner

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_recipe)

        recipeNameEditText = findViewById(R.id.et_recipe_name)
        recipeImageView = findViewById(R.id.recipeImageView)
        ingredientsEditText = findViewById(R.id.et_recipe_ingredients)
        instructionsEditText = findViewById(R.id.et_recipe_instructions)
        timeEditText = findViewById(R.id.et_recipe_preparation_time)
        saveButton = findViewById(R.id.btn_save)
        cancelButton = findViewById(R.id.btn_cancel)
        mealTypeSpinner = findViewById(R.id.spinner_meal_type)

        recipe = intent.getParcelableExtra("recipe") ?: return

        recipeNameEditText.setText(recipe.name)
        ingredientsEditText.setText(recipe.ingredients.joinToString("\n"))
        instructionsEditText.setText(recipe.instructions)

        timeEditText.setText(recipe.preparationTime.toString())

        val imageResId = if (recipe.image_path?.isNotEmpty() == true) {
            resources.getIdentifier(recipe.image_path, "drawable", packageName)
        } else {
            0
        }

        if (imageResId != 0) {
            recipeImageView.setImageResource(imageResId) // Set image to ImageView
        }

        val mealTypes = Meal.values().map { it.displayName }
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, mealTypes)
        mealTypeSpinner.adapter = adapter

        val selectedMealTypePosition = mealTypes.indexOfFirst { it == recipe.meal.displayName }
        mealTypeSpinner.setSelection(selectedMealTypePosition)

        saveButton.setOnClickListener {
            val newName = recipeNameEditText.text.toString()
            val newIngredients = ingredientsEditText.text.toString().split("\n")
            val newInstructions = instructionsEditText.text.toString()

            val preparationTimeText = timeEditText.text.toString()
            val newPreparationTime = preparationTimeText.toIntOrNull()

            if (newPreparationTime == null || newPreparationTime <= 0) {
                timeEditText.error = "Unesite ispravno vrijeme pripreme u minutama"
                return@setOnClickListener
            }

            val newMealType = Meal.fromDisplayName(mealTypeSpinner.selectedItem.toString())

            val updatedRecipe = recipe.copy(
                name = newName,
                ingredients = newIngredients,
                instructions = newInstructions,
                preparationTime = newPreparationTime,
                meal = newMealType
            )

            val resultIntent = Intent().apply {
                putExtra("UPDATED_RECIPE", updatedRecipe)
            }
            setResult(Activity.RESULT_OK, resultIntent)
            finish()
        }
        cancelButton.setOnClickListener {
            finish()
        }
    }
}