package com.example.receptomat.recipeManagement

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity
import com.example.receptomat.R
import com.example.receptomat.entities.Meal
import com.example.receptomat.entities.Recipe
import java.util.Date

class AddNewRecipeActivity : AppCompatActivity() {
    private lateinit var ingredientContainer: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inflacija layouta
        setContentView(R.layout.activity_add_new_recipe)

        // Referenciranje kontrole
        ingredientContainer = findViewById(R.id.ingredientContainer)
        val spinnerMealType = findViewById<Spinner>(R.id.spinner_meal_type)
        val btnSave = findViewById<Button>(R.id.btn_save)
        val btnCancel = findViewById<Button>(R.id.btn_cancel)
        val btnAddIngredient = findViewById<Button>(R.id.btn_add_ingredient)

        // Popis tipova jela
        val mealTypes = Meal.values().map { it.displayName }

        val mealAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, mealTypes)
        mealAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerMealType.adapter = mealAdapter

        var selectedMeal: Meal = Meal.BREAKFAST

        spinnerMealType.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parentView: AdapterView<*>, view: View?, position: Int, id: Long) {
                selectedMeal = Meal.values()[position]
            }

            override fun onNothingSelected(parentView: AdapterView<*>) {}
        }

        // Funkcija za dodavanje novog reda za sastojke
        btnAddIngredient.setOnClickListener {
            addNewIngredientRow()
        }

        // Spremanje recepta
        btnSave.setOnClickListener {
            val name = findViewById<EditText>(R.id.et_recipe_name).text.toString()
            val ingredients = mutableListOf<String>()

            // Prikupljanje podataka o sastojcima
            for (i in 0 until ingredientContainer.childCount) {
                val ingredientLayout = ingredientContainer.getChildAt(i) as LinearLayout
                val etIngredientName = ingredientLayout.findViewById<EditText>(R.id.et_ingredient_name)
                val etIngredientQuantity = ingredientLayout.findViewById<EditText>(R.id.et_ingredient_quantity)
                val ingredientName = etIngredientName.text.toString()
                val ingredientQuantity = etIngredientQuantity.text.toString()
                ingredients.add("$ingredientName: $ingredientQuantity")
            }

            val instructions = findViewById<EditText>(R.id.et_recipe_instructions).text.toString()
            val preparationTime = findViewById<EditText>(R.id.et_recipe_preparation_time).text.toString().toIntOrNull() ?: 0
            val dateOfAddition = Date()

            // Kreiranje novog recepta
            val newRecipe = Recipe(
                recipe_id = (0..1000).random(),
                name = name,
                meal = selectedMeal,
                ingredients = ingredients,
                instructions = instructions,
                preparationTime = preparationTime,
                dateOfAddition = dateOfAddition
            )

            // Vraćanje rezultata aktivnosti
            val resultIntent = Intent().apply {
                putExtra("NEW_RECIPE", newRecipe)
            }
            setResult(Activity.RESULT_OK, resultIntent)
            finish()
        }

        // Odustajanje (završava aktivnost)
        btnCancel.setOnClickListener { finish() }
    }

    // Funkcija za dodavanje novog reda sastojka
    private fun addNewIngredientRow() {
        // Inflacija reda sastojka
        val ingredientLayout = layoutInflater.inflate(R.layout.ingredients_layout, null) as LinearLayout

        // Pronalaženje komponenti unutar infliranog layouta
        val etIngredientName = ingredientLayout.findViewById<EditText>(R.id.et_ingredient_name)
        val etIngredientQuantity = ingredientLayout.findViewById<EditText>(R.id.et_ingredient_quantity)
        val spinnerUnit = ingredientLayout.findViewById<Spinner>(R.id.spinner_unit)
        val btnRemoveIngredient = ingredientLayout.findViewById<Button>(R.id.btn_remove_ingredient)

        // Postavljanje adaptera za spinner (jedinice mjere)
        val units = arrayOf("g", "kg", "ml", "l")
        val unitAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, units)
        unitAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerUnit.adapter = unitAdapter

        // Postavljanje funkcionalnosti za uklanjanje reda
        btnRemoveIngredient.setOnClickListener {
            ingredientContainer.removeView(ingredientLayout)
        }

        // Dodavanje novog reda u LinearLayout
        ingredientContainer.addView(ingredientLayout)
    }
}
