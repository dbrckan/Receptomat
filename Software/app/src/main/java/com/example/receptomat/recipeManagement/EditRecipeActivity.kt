package com.example.receptomat.recipeManagement

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
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
    private lateinit var ingredientContainer: LinearLayout
    private lateinit var addIngredientButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_recipe)

        // Find views
        recipeNameEditText = findViewById(R.id.et_recipe_name)
        recipeImageView = findViewById(R.id.recipeImageView)
        instructionsEditText = findViewById(R.id.et_recipe_instructions)
        timeEditText = findViewById(R.id.et_recipe_preparation_time)
        saveButton = findViewById(R.id.btn_save)
        cancelButton = findViewById(R.id.btn_cancel)
        mealTypeSpinner = findViewById(R.id.spinner_meal_type)
        ingredientContainer = findViewById(R.id.ingredientContainer)
        addIngredientButton = findViewById(R.id.btn_add_ingredient)

        // Get recipe data
        recipe = intent.getParcelableExtra("recipe") ?: return

        // Populate views with existing recipe data
        recipeNameEditText.setText(recipe.name)
        instructionsEditText.setText(recipe.instructions)
        timeEditText.setText(recipe.preparationTime.toString())

        // Set meal type in spinner
        val mealTypes = Meal.values().map { it.displayName }
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, mealTypes)
        mealTypeSpinner.adapter = adapter
        val selectedMealTypePosition = mealTypes.indexOfFirst { it == recipe.meal.displayName }
        mealTypeSpinner.setSelection(selectedMealTypePosition)

        // Set recipe ingredients
        recipe.ingredients.forEach { ingredient ->
            addIngredientRow(ingredient)
        }

        // Add new ingredient row
        addIngredientButton.setOnClickListener {
            addIngredientRow()
        }

        // Save updated recipe
        saveButton.setOnClickListener {
            val newName = recipeNameEditText.text.toString()
            val newIngredients = mutableListOf<String>()

            // Collect ingredients from input fields
            for (i in 0 until ingredientContainer.childCount) {
                val ingredientLayout = ingredientContainer.getChildAt(i) as LinearLayout
                val etIngredientName = ingredientLayout.findViewById<EditText>(R.id.et_ingredient_name)
                val etIngredientQuantity = ingredientLayout.findViewById<EditText>(R.id.et_ingredient_quantity)
                val ingredientName = etIngredientName.text.toString()
                val ingredientQuantity = etIngredientQuantity.text.toString()
                newIngredients.add("$ingredientName: $ingredientQuantity")
            }

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

            // Return updated recipe
            val resultIntent = Intent().apply {
                putExtra("UPDATED_RECIPE", updatedRecipe)
            }
            setResult(Activity.RESULT_OK, resultIntent)
            finish()
        }

        // Cancel editing
        cancelButton.setOnClickListener {
            finish()
        }
    }

    // Function to add a new ingredient row
    private fun addIngredientRow(existingIngredient: String? = null) {
        // Inflate the ingredient layout
        val ingredientLayout = layoutInflater.inflate(R.layout.ingredients_layout, null) as LinearLayout

        // Find views in the inflated layout
        val etIngredientName = ingredientLayout.findViewById<EditText>(R.id.et_ingredient_name)
        val etIngredientQuantity = ingredientLayout.findViewById<EditText>(R.id.et_ingredient_quantity)
        val spinnerUnit = ingredientLayout.findViewById<Spinner>(R.id.spinner_unit)
        val btnRemoveIngredient = ingredientLayout.findViewById<Button>(R.id.btn_remove_ingredient)

        // Set unit spinner
        val units = arrayOf("g", "kg", "ml", "l")
        val unitAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, units)
        unitAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerUnit.adapter = unitAdapter

        // Set existing ingredient data if provided
        existingIngredient?.let {
            val parts = it.split(": ")
            if (parts.size == 2) {
                etIngredientName.setText(parts[0])
                etIngredientQuantity.setText(parts[1])
            }
        }

        // Remove ingredient row
        btnRemoveIngredient.setOnClickListener {
            ingredientContainer.removeView(ingredientLayout)
        }

        // Add ingredient row to container
        ingredientContainer.addView(ingredientLayout)
    }
}
