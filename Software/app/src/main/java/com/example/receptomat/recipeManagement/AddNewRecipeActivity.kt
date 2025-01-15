package com.example.receptomat.recipeManagement

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.receptomat.R
import com.example.receptomat.entities.Category
import com.example.receptomat.entities.Preference
import com.example.receptomat.entities.RecipeDB
import com.example.receptomat.entities.Units
import database.ApiService
import database.MessageResponse
import database.RetrofitClient
import retrofit2.Response
import retrofit2.Call
import retrofit2.Callback

class AddNewRecipeActivity : AppCompatActivity() {

    private lateinit var ingredientContainer: LinearLayout
    private lateinit var categorySpinner: Spinner
    private lateinit var preferenceSpinner: Spinner
    private var selectedCategoryId: Int? = null
    private var selectedPreferenceId: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_new_recipe)

        ingredientContainer = findViewById(R.id.ingredientContainer)
        categorySpinner = findViewById(R.id.spinner_meal_type)
        preferenceSpinner = findViewById(R.id.spinner_preference)

        fetchCategories()
        fetchPreferences()
        setupAddIngredientButton()

        findViewById<Button>(R.id.btn_save).setOnClickListener {
            saveRecipe()
        }

        findViewById<Button>(R.id.btn_cancel).setOnClickListener {
            finish()
        }
    }

    private fun fetchCategories() {
        val apiService = RetrofitClient.instance.create(ApiService::class.java)
        apiService.getCategories().enqueue(object : Callback<List<Category>> {
            override fun onResponse(call: Call<List<Category>>, response: Response<List<Category>>) {
                if (response.isSuccessful) {
                    val categories = response.body() ?: emptyList()
                    if (categories.isEmpty()) {
                        Toast.makeText(this@AddNewRecipeActivity, "Nema dostupnih kategorija", Toast.LENGTH_SHORT).show()
                        return
                    }

                    val categoryNames = categories.map { it.name }
                    val adapter = ArrayAdapter(this@AddNewRecipeActivity, android.R.layout.simple_spinner_item, categoryNames)
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
                    Toast.makeText(this@AddNewRecipeActivity, "Greška pri dohvaćanju kategorija", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<Category>>, t: Throwable) {
                Toast.makeText(this@AddNewRecipeActivity, "Greška: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun fetchUnits(ingredientView: View) {
        val apiService = RetrofitClient.instance.create(ApiService::class.java)
        apiService.getUnits().enqueue(object : Callback<List<Units>> {
            override fun onResponse(call: Call<List<Units>>, response: Response<List<Units>>) {
                if (response.isSuccessful) {
                    val units = response.body() ?: emptyList()

                    val unitAdapter = object : ArrayAdapter<Units>(this@AddNewRecipeActivity, android.R.layout.simple_spinner_item, units) {
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
                    Toast.makeText(this@AddNewRecipeActivity, "Greška pri dohvaćanju mjernih jedinica", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<Units>>, t: Throwable) {
                Toast.makeText(this@AddNewRecipeActivity, "Greška: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
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

    private fun fetchPreferences() {
        val apiService = RetrofitClient.instance.create(ApiService::class.java)
        apiService.getPreferences().enqueue(object : Callback<List<Preference>> {
            override fun onResponse(call: Call<List<Preference>>, response: Response<List<Preference>>) {
                if (response.isSuccessful) {
                    val preferences = response.body() ?: emptyList()
                    if (preferences.isEmpty()) {
                        Toast.makeText(this@AddNewRecipeActivity, "Nema dostupnih preferencija", Toast.LENGTH_SHORT).show()
                        return
                    }

                    val preferenceNames = preferences.map { it.name }
                    val adapter = ArrayAdapter(this@AddNewRecipeActivity, android.R.layout.simple_spinner_item, preferenceNames)
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
                    Toast.makeText(this@AddNewRecipeActivity, "Greška pri dohvaćanju preferencija", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<Preference>>, t: Throwable) {
                Toast.makeText(this@AddNewRecipeActivity, "Greška: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun saveRecipe() {
        val name = findViewById<EditText>(R.id.et_recipe_name).text.toString().trim()
        val description = findViewById<EditText>(R.id.et_recipe_instructions).text.toString().trim()
        val timeString = findViewById<EditText>(R.id.et_recipe_preparation_time).text.toString().trim()
        val time = timeString.toIntOrNull()

        if (name.isEmpty() || description.isEmpty() || time == null || time <= 0) {
            Toast.makeText(this, "Molimo unesite sve podatke i valjani broj za vrijeme.", Toast.LENGTH_SHORT).show()
            return
        }

        val categoryId = selectedCategoryId
        val preferenceId = selectedPreferenceId
        if (categoryId == null || preferenceId == null) {
            Toast.makeText(this, "Molimo odaberite kategoriju i preferenciju.", Toast.LENGTH_SHORT).show()
            return
        }

        val sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE)
        val userId = sharedPreferences.getInt("user_id", -1)

        val recipeRequest = RecipeDB(
            recipe_id = null,
            name = name,
            description = description,
            time = time,
            user_id = userId,
            category_id = categoryId,
            preference_id = preferenceId
        )

        val apiService = RetrofitClient.instance.create(ApiService::class.java)
        apiService.addRecipe(
            name = recipeRequest.name,
            description = recipeRequest.description,
            time = recipeRequest.time,
            userId = recipeRequest.user_id,
            categoryId = recipeRequest.category_id,
            preferenceId = recipeRequest.preference_id
        ).enqueue(object : Callback<MessageResponse> {
            override fun onResponse(call: Call<MessageResponse>, response: Response<MessageResponse>) {
                if (response.isSuccessful && response.body()?.success == true) {
                    val message = response.body()?.message ?: "Recept spremljen!"
                    Toast.makeText(this@AddNewRecipeActivity, message, Toast.LENGTH_SHORT).show()

                    val recipeId = response.body()?.recipe_id ?: return

                    val intent = Intent().apply {
                        putExtra("NEW_RECIPE", recipeRequest)
                    }
                    setResult(RESULT_OK, intent)

                    saveIngredients(recipeId)

                    Log.d("AddRecipe", "Recipe ID3: $recipeId")

                    finish()
                } else {
                    val error = response.errorBody()?.string() ?: "Greška pri spremanju recepta"
                    Toast.makeText(this@AddNewRecipeActivity, "Greška pri spremanju recepta: $error", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<MessageResponse>, t: Throwable) {
                Toast.makeText(this@AddNewRecipeActivity, "Greška: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun saveIngredients(recipeId: Int) {
        for (i in 0 until ingredientContainer.childCount) {
            val ingredientView = ingredientContainer.getChildAt(i)
            val ingredientNameInput = ingredientView.findViewById<EditText>(R.id.et_ingredient_name)
            val quantityInput = ingredientView.findViewById<EditText>(R.id.et_ingredient_quantity)
            val unitSpinner = ingredientView.findViewById<Spinner>(R.id.spinner_unit)

            val ingredientName = ingredientNameInput.text.toString().trim()
            val quantityString = quantityInput.text.toString().trim()
            val quantity = quantityString.toFloatOrNull() ?: 0f
            val unit = unitSpinner.selectedItem as Units

            Log.d("AddRecipe", "Recipe ID2: $recipeId")
            Log.d("AddRecipe", "Ingredient to save: $ingredientName, Quantity: $quantity, Unit: $unit")

            if (ingredientName.isNotEmpty() && quantity > 0) {
                addItem(ingredientName, quantity, unit, recipeId)
            }
        }
    }

    private fun addItem(ingredientName: String, quantity: Float, unit: Units, recipeId: Int) {
        val apiService = RetrofitClient.instance.create(ApiService::class.java)
        apiService.addItem(ingredientName).enqueue(object : Callback<MessageResponse> {
            override fun onResponse(call: Call<MessageResponse>, response: Response<MessageResponse>) {
                if (response.isSuccessful && response.body()?.success == true) {
                    val itemId = response.body()?.item_id ?: return
                    Log.d("AddRecipe", "Item id: $itemId")
                    Log.d("AddRecipe", "Linking item to recipe: itemName=$ingredientName, itemId=$itemId, recipeId=$recipeId, quantity=$quantity, unitId=${unit.unit_id}")

                    linkItemToRecipe(recipeId, itemId, ingredientName, quantity, unit.unit_id)
                } else {
                    Log.e("AddRecipe", "Error response: ${response.errorBody()?.string()}")
                    Toast.makeText(this@AddNewRecipeActivity, "Greška pri dodavanju sastojka", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<MessageResponse>, t: Throwable) {
                Log.e("AddRecipe", "Failed to add item", t)
                Toast.makeText(this@AddNewRecipeActivity, "Greška: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun linkItemToRecipe(recipeId: Int, itemId: Int, itemName: String, quantity: Float, unitId: Int) {
        val apiService = RetrofitClient.instance.create(ApiService::class.java)
        apiService.linkItemToRecipe(recipeId, itemName, quantity, unitId).enqueue(object : Callback<MessageResponse> {
            override fun onResponse(call: Call<MessageResponse>, response: Response<MessageResponse>) {
                if (response.isSuccessful && response.body()?.success == true) {
                    Log.d("AddRecipe", "Item successfully linked to recipe")
                } else {
                    Log.e("AddRecipe", "Failed to link item to recipe: ${response.errorBody()?.string()}")
                    Toast.makeText(this@AddNewRecipeActivity, "Greška pri povezivanju sastojka s receptom", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<MessageResponse>, t: Throwable) {
                Log.e("AddRecipe", "Failed to link item to recipe: ${t.message ?: "Unknown error"}")
                Toast.makeText(this@AddNewRecipeActivity, "Greška pri povezivanju sastojka s receptom", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
