package com.example.receptomat.recipeManagement

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity
import com.example.receptomat.R
import com.example.receptomat.entities.Meal
import com.example.receptomat.entities.Recipe
import java.util.Date

class AddNewRecipeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_new_recipe)

        val spinnerMealType = findViewById<Spinner>(R.id.spinner_meal_type)

        val mealTypes = Meal.values().map { it.displayName }

        val mealAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, mealTypes)
        mealAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerMealType.adapter = mealAdapter

        var selectedMeal: Meal = Meal.BREAKFAST

        spinnerMealType.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parentView: AdapterView<*>, view: View?, position: Int, id: Long) {
                selectedMeal = Meal.values()[position]
            }

            override fun onNothingSelected(parentView: AdapterView<*>) {
            }
        }

        val btnSave = findViewById<Button>(R.id.btn_save)
        btnSave.setOnClickListener {
            val name = findViewById<EditText>(R.id.et_recipe_name).text.toString()
            val ingredients = findViewById<EditText>(R.id.et_recipe_ingredients).text.toString().split(",")
            val instructions = findViewById<EditText>(R.id.et_recipe_instructions).text.toString()
            val preparationTime = findViewById<EditText>(R.id.et_recipe_preparation_time).text.toString().toIntOrNull() ?: 0
            val dateOfAddition = Date()

            val newRecipe = Recipe(
                recipe_id = (0..1000).random(),
                name = name,
                meal = selectedMeal,
                ingredients = ingredients,
                instructions = instructions,
                preparationTime = preparationTime,
                dateOfAddition = dateOfAddition
            )

            val resultIntent = Intent().apply {
                putExtra("NEW_RECIPE", newRecipe)
            }
            setResult(Activity.RESULT_OK, resultIntent)
            finish()
        }

        val btnCancel = findViewById<Button>(R.id.btn_cancel)
        btnCancel.setOnClickListener { finish() }
    }
}