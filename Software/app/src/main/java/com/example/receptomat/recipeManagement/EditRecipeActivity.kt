package com.example.receptomat.recipeManagement

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.receptomat.R
import com.example.receptomat.entities.Category
import com.example.receptomat.entities.Ingredient
import com.example.receptomat.entities.Preference
import com.example.receptomat.entities.Units
import database.ApiService
import database.IngredientsRequest
import database.RecipeRequest
import database.RetrofitClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class EditRecipeActivity : AppCompatActivity() {

    private lateinit var recipeNameEditText: EditText
    private lateinit var instructionsEditText: EditText
    private lateinit var timeEditText: EditText
    private var categoriesList: List<Category> = emptyList()
    private var preferencesList: List<Preference> = emptyList()
    private lateinit var ingredientContainer: LinearLayout
    private lateinit var categorySpinner: Spinner
    private lateinit var preferenceSpinner: Spinner
    private var selectedCategoryId: Int? = null
    private var selectedPreferenceId: Int? = null



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_new_recipe)

        recipeNameEditText = findViewById(R.id.et_recipe_name)
        instructionsEditText = findViewById(R.id.et_recipe_instructions)
        timeEditText = findViewById(R.id.et_recipe_preparation_time)
        ingredientContainer = findViewById(R.id.ingredientContainer)
        categorySpinner = findViewById(R.id.spinner_meal_type)
        preferenceSpinner = findViewById(R.id.spinner_preference)

        setupAddIngredientButton()

        val recipeId = intent.getIntExtra("recipe_id", -1)
        Log.d("EditRecipe", "Recipe id ${recipeId}")

        if (recipeId != -1) {
            loadRecipe(recipeId)
        }

        fetchCategories()
        fetchPreferences()

        findViewById<Button>(R.id.btn_save).setOnClickListener {
            saveRecipe(recipeId)
            finish()
        }

        findViewById<Button>(R.id.btn_cancel).setOnClickListener {
            finish()
        }
    }

    private fun addIngredientLayout() {
        val inflater = getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val ingredientView = inflater.inflate(R.layout.ingredients_layout, null)
        ingredientContainer.addView(ingredientView)

        val btnRemoveIngredient = ingredientView.findViewById<Button>(R.id.btn_remove_ingredient)
        btnRemoveIngredient.setOnClickListener {
            ingredientContainer.removeView(ingredientView)
        }

        fetchUnits(ingredientView)
    }

    private fun setupAddIngredientButton() {
        findViewById<Button>(R.id.btn_add_ingredient).setOnClickListener {
            addIngredientLayout()
        }
    }

    private fun saveRecipe(recipeId: Int) {
        val name = recipeNameEditText.text.toString().trim()
        val instructions = instructionsEditText.text.toString().trim()
        val time = timeEditText.text.toString().trim()

        if (name.isEmpty() || instructions.isEmpty() || time.isEmpty()) {
            Toast.makeText(this, "Molimo unesite sve potrebne podatke.", Toast.LENGTH_SHORT).show()
            return
        }

        val sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE)
        val userId = sharedPreferences.getInt("user_id", -1)

        val updatedRecipe = RecipeRequest(
            recipe_id = recipeId,
            name = name,
            description = instructions,
            time = time.toInt(),
            user_id = userId,
            category_id = selectedCategoryId ?: 0,
            category_name = getCategoryById(selectedCategoryId ?: 0)?.name,
            preference_id = selectedPreferenceId ?: 0,
            preference_name = getPreferenceById(selectedPreferenceId ?: 0)?.name,
            ingredients = collectIngredients()
        )

        val apiService = RetrofitClient.instance.create(ApiService::class.java)

        CoroutineScope(Dispatchers.Main).launch {
            try {
                val response = apiService.updateRecipe(updatedRecipe)
                if (response.isSuccessful) {
                    Toast.makeText(this@EditRecipeActivity, "Recept uspješno spremljen!", Toast.LENGTH_SHORT).show()
                    updateIngredients(recipeId)
                    setResult(Activity.RESULT_OK, intent)
                    finish()
                } else {
                    Toast.makeText(this@EditRecipeActivity, "Greška pri spremanju recepta.", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(this@EditRecipeActivity, "Greška: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun collectIngredients(): List<Ingredient> {
        val ingredientsList = mutableListOf<Ingredient>()
        for (i in 0 until ingredientContainer.childCount) {
            val ingredientView = ingredientContainer.getChildAt(i)
            val nameEditText = ingredientView.findViewById<EditText>(R.id.et_ingredient_name)
            val quantityEditText = ingredientView.findViewById<EditText>(R.id.et_ingredient_quantity)
            val unitSpinner = ingredientView.findViewById<Spinner>(R.id.spinner_unit)

            val ingredientName = nameEditText.text.toString().trim()
            val ingredientQuantity = quantityEditText.text.toString().trim().toDoubleOrNull() ?: 0.0
            val selectedUnit = unitSpinner.selectedItem as? Units

            if (ingredientName.isNotEmpty() && ingredientQuantity != 0.0 && selectedUnit != null) {
                val ingredient = Ingredient(
                    item_name = ingredientName,
                    quantity = ingredientQuantity,
                    unit_id = selectedUnit.unit_id,
                    unit_name = selectedUnit.unit_name
                )
                ingredientsList.add(ingredient)
            }
        }
        return ingredientsList
    }

    private fun updateIngredients(recipeId: Int) {
        val ingredients = collectIngredients()
        if (ingredients.isEmpty()) {
            Toast.makeText(this, "Nema sastojaka za ažuriranje", Toast.LENGTH_SHORT).show()
            return
        }
        val ingredientsRequest = IngredientsRequest(
            recipe_id = recipeId,
            ingredients = ingredients.map {
                Ingredient(
                    item_name = it.item_name,
                    quantity = it.quantity,
                    unit_id = it.unit_id,
                    unit_name = it.unit_name
                )
            }
        )

        val apiService = RetrofitClient.instance.create(ApiService::class.java)

        CoroutineScope(Dispatchers.Main).launch {
            try {
                val response = apiService.updateIngredients(ingredientsRequest)
                if (response.isSuccessful) {
                    Toast.makeText(this@EditRecipeActivity, "Sastojci uspješno ažurirani", Toast.LENGTH_SHORT).show()
                    setResult(Activity.RESULT_OK, intent)
                    finish()
                } else {
                    Toast.makeText(this@EditRecipeActivity, "Greška pri ažuriranju sastojaka", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(this@EditRecipeActivity, "Greška: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }


    private fun loadRecipe(recipeId: Int) {
        val apiService = RetrofitClient.instance.create(ApiService::class.java)
        CoroutineScope(Dispatchers.Main).launch {
            try {
                val response = apiService.getRecipeById(recipeId)
                if (response.isSuccessful) {
                    val recipeResponse = response.body()
                    Log.d("RecipeResponse", "Recipe: ${recipeResponse?.data}")
                    recipeResponse?.data?.let {
                        populateUI(it)
                    } ?: run {
                        Log.d("EditRecipe", "No recipe data found.")
                    }
                } else {
                    Log.d("RecipeResponse", "Error loading recipe: ${response.message()}")
                    Toast.makeText(this@EditRecipeActivity, "Error loading recipe", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(this@EditRecipeActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun populateUI(recipeRequest: RecipeRequest?) {
        if (recipeRequest == null) {
            Log.d("EditRecipe", "Recipe data is null.")
            return
        }

        recipeNameEditText.setText(recipeRequest.name)
        instructionsEditText.setText(recipeRequest.description)
        timeEditText.setText(recipeRequest.time.toString())

        recipeRequest.ingredients?.let {
            Log.d("EditRecipe", "Ingredients in 'let' block: $it")
            if (it.isNotEmpty()) {
                it.forEach { ingredient ->
                    addIngredientField(ingredient)
                }
            } else {
                Log.d("EditRecipe", "No ingredients available.")
            }
        }

        val selectedCategory = getCategoryById(recipeRequest.category_id)
        selectedCategory?.let {
            val categoryAdapter = categorySpinner.adapter as ArrayAdapter<Category>
            val categoryPosition = categoryAdapter.getPosition(it)
            categorySpinner.setSelection(categoryPosition)
        }

        val selectedPreference = getPreferenceById(recipeRequest.preference_id)
        selectedPreference?.let {
            val preferenceAdapter = preferenceSpinner.adapter as ArrayAdapter<Preference>
            val preferencePosition = preferenceAdapter.getPosition(it)
            preferenceSpinner.setSelection(preferencePosition)
        }
    }



    private fun addIngredientField(ingredient: Ingredient) {
        Log.d("EditRecipe", "Adding ingredient: ${ingredient.item_name}, quantity: ${ingredient.quantity}, unit: ${ingredient.unit_id}")

        val inflater = LayoutInflater.from(this)
        val view = inflater.inflate(R.layout.ingredients_layout, null)

        val nameEditText = view.findViewById<EditText>(R.id.et_ingredient_name)
        val quantityEditText = view.findViewById<EditText>(R.id.et_ingredient_quantity)
        val unitSpinner = view.findViewById<Spinner>(R.id.spinner_unit)

        nameEditText.setText(ingredient.item_name)
        quantityEditText.setText(ingredient.quantity.toString())

        if (unitSpinner.adapter == null || unitSpinner.adapter.count == 0) {
            Log.d("UnitSpinner", "Unit spinner je null")
            fetchUnits(view)
        } else {
            val unitAdapter = unitSpinner.adapter as? ArrayAdapter<Unit>
            unitAdapter?.let {
                Log.d("UnitSpinner", "Unit spinner je null2222222")
                val selectedUnit = it.getItem(ingredient.unit_id)
                val unitPosition = it.getPosition(selectedUnit)
                unitSpinner.setSelection(unitPosition)
            }
        }

        ingredientContainer.addView(view)
    }

    private fun getCategoryById(categoryId: Int): Category? {
        return categoriesList.find { it.category_id == categoryId }
    }

    private fun getPreferenceById(preferenceId: Int): Preference? {
        return preferencesList.find { it.preference_id == preferenceId }
    }


    private fun fetchCategories() {
        val apiService = RetrofitClient.instance.create(ApiService::class.java)
        apiService.getCategories().enqueue(object : Callback<List<Category>> {
            override fun onResponse(call: Call<List<Category>>, response: Response<List<Category>>) {
                if (response.isSuccessful) {
                    val categories = response.body() ?: emptyList()
                    if (categories.isEmpty()) {
                        Toast.makeText(this@EditRecipeActivity, "Nema dostupnih kategorija", Toast.LENGTH_SHORT).show()
                        return
                    }

                    val categoryNames = categories.map { it.name }
                    val adapter = ArrayAdapter(this@EditRecipeActivity, android.R.layout.simple_spinner_item, categoryNames)
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    categorySpinner.adapter = adapter


                    categorySpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                        override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                            selectedCategoryId = categories[position].category_id
                        }

                        override fun onNothingSelected(parent: AdapterView<*>) {
                            selectedCategoryId = null
                        }
                    }
                } else {
                    Toast.makeText(this@EditRecipeActivity, "Greška pri dohvaćanju kategorija", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<Category>>, t: Throwable) {
                Toast.makeText(this@EditRecipeActivity, "Greška: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun fetchUnits(ingredientView: View) {
        val apiService = RetrofitClient.instance.create(ApiService::class.java)
        apiService.getUnits().enqueue(object : Callback<List<Units>> {
            override fun onResponse(call: Call<List<Units>>, response: Response<List<Units>>) {
                if (response.isSuccessful) {
                    val units = response.body() ?: emptyList()

                    val unitAdapter = object : ArrayAdapter<Units>(this@EditRecipeActivity, android.R.layout.simple_spinner_item, units) {
                        override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
                            val view = super.getDropDownView(position, convertView, parent)
                            (view as TextView).text = getItem(position)?.unit_name ?: ""
                            return view
                        }

                        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                            val view = super.getView(position, convertView, parent)
                            (view as TextView).text = getItem(position)?.unit_name ?: ""
                            return view
                        }
                    }

                    unitAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

                    val spinnerUnit = ingredientView.findViewById<Spinner>(R.id.spinner_unit)
                    spinnerUnit.adapter = unitAdapter
                } else {
                    Toast.makeText(this@EditRecipeActivity, "Greška pri dohvaćanju mjernih jedinica", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<Units>>, t: Throwable) {
                Toast.makeText(this@EditRecipeActivity, "Greška: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun fetchPreferences() {
        val apiService = RetrofitClient.instance.create(ApiService::class.java)
        apiService.getPreferences().enqueue(object : Callback<List<Preference>> {
            override fun onResponse(call: Call<List<Preference>>, response: Response<List<Preference>>) {
                if (response.isSuccessful) {
                    val preferences = response.body() ?: emptyList()
                    if (preferences.isEmpty()) {
                        Toast.makeText(this@EditRecipeActivity, "Nema dostupnih preferencija", Toast.LENGTH_SHORT).show()
                        return
                    }

                    val preferenceNames = preferences.map { it.name }
                    val adapter = ArrayAdapter(this@EditRecipeActivity, android.R.layout.simple_spinner_item, preferenceNames)
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    preferenceSpinner.adapter = adapter

                    preferenceSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                        override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                            selectedPreferenceId = preferences[position].preference_id
                        }

                        override fun onNothingSelected(parent: AdapterView<*>) {
                            selectedPreferenceId = null
                        }
                    }
                } else {
                    Toast.makeText(this@EditRecipeActivity, "Greška pri dohvaćanju preferencija", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<Preference>>, t: Throwable) {
                Toast.makeText(this@EditRecipeActivity, "Greška: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

}
