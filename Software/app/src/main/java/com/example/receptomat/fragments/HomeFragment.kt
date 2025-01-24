package com.example.receptomat.fragments

import android.app.Activity
import android.app.AlertDialog
import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.receptomat.R
import com.example.receptomat.entities.Category
import com.example.receptomat.entities.RecipeAdapter
import com.example.receptomat.entities.RecipeDB
import com.example.receptomat.recipeManagement.AddNewRecipeActivity
import com.example.receptomat.recipeManagement.DetailActivity
import com.example.receptomat.recipeManagement.EditRecipeActivity
import database.ApiService
import database.AddFavoriteRecipeRequest
import database.BasicResponse
import database.MessageResponse
import database.PlanResponse
import database.RetrofitClient
import database.UserPreferenceResponse
import retrofit2.Response
import retrofit2.Call
import retrofit2.Callback

class HomeFragment : Fragment(R.layout.fragment_home) {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: RecipeAdapter
    private val recipes = mutableListOf<RecipeDB>()
    private val categories = mutableListOf<Category>()
    private var loggedInUserId: Int = -1

    private lateinit var createRecipeLauncher: ActivityResultLauncher<Intent>
    private lateinit var editRecipeLauncher: ActivityResultLauncher<Intent>

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val sharedPreferences = requireContext().getSharedPreferences("user_prefs", Activity.MODE_PRIVATE)
        val loggedInUserId = sharedPreferences.getInt("user_id", -1)

        if (loggedInUserId != -1) {
            fetchUserPreference(loggedInUserId) { preferenceId ->
                fetchRecipesWithPreference(preferenceId)
            }
        }

        recyclerView = view.findViewById(R.id.rv_recipes)
        adapter = RecipeAdapter(
            recipes,

            onItemClick = { selectedRecipe ->
                val intent = Intent(requireContext(), DetailActivity::class.java).apply {
                    putExtra("RECIPE_DATA", selectedRecipe)
                }
                startActivity(intent)
            },
            onDeleteClick = { recipe ->
                showDeleteConfirmationDialog(recipe)
            },
            onEditClick = { recipe ->
                editRecipe(recipe)
                true
            },
            onFavoriteClick = { recipe ->
                addToFavorites(recipe)
                true
            },
            onAddToMenuClick = { recipe, dayId ->
                val sharedPreferences = requireActivity().getSharedPreferences("user_prefs", MODE_PRIVATE)
                val userId = sharedPreferences.getInt("user_id", -1)

                if (userId != -1) {
                    getPlanIdForUser(userId) { planId ->
                        val recipeId = recipe.recipe_id ?: 0
                        if (recipeId != 0) {
                            addRecipeToMealPlan(recipeId, planId, dayId)
                        } else {
                            Toast.makeText(requireContext(), "Recept nema validan ID", Toast.LENGTH_SHORT).show()
                        }
                    }
                } else {
                    Toast.makeText(requireContext(), "Korisnički ID nije pronađen", Toast.LENGTH_SHORT).show()
                }
            },
            categories,
            loggedInUserId,
            userPreferenceId = -1
        )
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(view.context)

        createRecipeLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val newRecipe = result.data?.getParcelableExtra<RecipeDB>("NEW_RECIPE")
                if (newRecipe != null) {
                    recipes.add(newRecipe)
                    adapter.notifyItemInserted(recipes.size - 1)
                    fetchRecipes()
                }
            }
        }

        editRecipeLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val updatedRecipe = result.data?.getParcelableExtra<RecipeDB>("UPDATED_RECIPE")
                if (updatedRecipe != null) {
                    val index = recipes.indexOfFirst { it.recipe_id == updatedRecipe.recipe_id }
                    if (index != -1) {
                        recipes[index] = updatedRecipe
                        adapter.notifyItemChanged(index)
                    }
                    fetchRecipes()
                }
            }
        }

        val btnCreateRecipe = view.findViewById<ImageButton>(R.id.ib_home_fragment_crate_recipe)
        btnCreateRecipe.setOnClickListener {
            val intent = Intent(requireContext(), AddNewRecipeActivity::class.java)
            createRecipeLauncher.launch(intent)
        }

        val searchView = view.findViewById<androidx.appcompat.widget.SearchView>(R.id.sv_recipe_search)
        searchView.requestFocus()
        searchView.setOnQueryTextListener(object : androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let {
                    searchRecipes(it)
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                newText?.let {
                    searchRecipes(it)
                }
                return true
            }
        })

        fetchRecipes()
        fetchCategoriesAndRecipes()
    }

    override fun onResume() {
        super.onResume()
        fetchRecipes()
    }

    private fun fetchCategoriesAndRecipes() {
        val apiService = RetrofitClient.instance.create(ApiService::class.java)

        val categoriesCall = apiService.getCategories()
        categoriesCall.enqueue(object : Callback<List<Category>> {
            override fun onResponse(call: Call<List<Category>>, response: Response<List<Category>>) {
                if (response.isSuccessful) {
                    categories.clear()
                    response.body()?.let { categoriesList ->
                        categories.addAll(categoriesList)
                        fetchRecipes()
                    }
                } else {
                    Toast.makeText(requireContext(), "Pogreška pri učitavanju kategorija", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<Category>>, t: Throwable) {
                Toast.makeText(requireContext(), "Pogreška pri povezivanju s serverom", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun fetchRecipes() {
        val apiService = RetrofitClient.instance.create(ApiService::class.java)
        val call = apiService.getRecipes()

        call.enqueue(object : Callback<List<RecipeDB>> {
            override fun onResponse(call: Call<List<RecipeDB>>, response: Response<List<RecipeDB>>) {
                if (response.isSuccessful) {
                    response.body()?.let { recipeDBList ->
                        recipes.clear()
                        recipes.addAll(recipeDBList)
                        adapter.notifyDataSetChanged()
                    }
                } else {
                    Toast.makeText(requireContext(), "Pogreška pri učitavanju recepata", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<RecipeDB>>, t: Throwable) {
                Toast.makeText(requireContext(), "Pogreška pri povezivanju s serverom", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun showDeleteConfirmationDialog(recipe: RecipeDB) {
        val context = requireContext()
        val alertDialog = AlertDialog.Builder(context)
            .setMessage("Jeste li sigurni da želite obrisati recept?")
            .setPositiveButton("Obriši") { _, _ ->
                deleteRecipe(recipe)
            }
            .setNegativeButton("Odustani", null)
            .create()

        alertDialog.setOnShowListener {
            alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(
                ContextCompat.getColor(context, R.color.button_start_page_color)
            )
            alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(
                ContextCompat.getColor(context, R.color.button_start_page_color)
            )
        }

        alertDialog.show()
    }

    private fun deleteRecipe(recipe: RecipeDB) {
        val recipeId = recipe.recipe_id

        if (recipeId != null) {
            val apiService = RetrofitClient.instance.create(ApiService::class.java)
            val call = apiService.deleteRecipe(recipeId)

            call.enqueue(object : Callback<MessageResponse> {
                override fun onResponse(call: Call<MessageResponse>, response: Response<MessageResponse>) {
                    if (response.isSuccessful) {
                        val responseBody = response.body()
                        if (responseBody?.success == true) {
                            // Ukloni recept iz liste i ažuriraj UI
                            val position = recipes.indexOf(recipe)
                            recipes.removeAt(position)
                            adapter.notifyItemRemoved(position)
                            Toast.makeText(requireContext(), responseBody.message, Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(requireContext(), "Pogreška pri brisanju recepta", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(requireContext(), "Pogreška pri povezivanju s serverom", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<MessageResponse>, t: Throwable) {
                    Toast.makeText(requireContext(), "Pogreška pri povezivanju s serverom", Toast.LENGTH_SHORT).show()
                }
            })
        } else {
            Toast.makeText(requireContext(), "ID recepta nije dostupan", Toast.LENGTH_SHORT).show()
        }
    }

    private fun editRecipe(recipe: RecipeDB) {
        val intent = Intent(requireContext(), EditRecipeActivity::class.java)
        intent.putExtra("recipe_id", recipe.recipe_id)
        editRecipeLauncher.launch(intent)
    }

    private fun addToFavorites(recipe: RecipeDB) {
        val apiService = RetrofitClient.instance.create(ApiService::class.java)
        val sharedPreferences = requireActivity().getSharedPreferences("user_prefs", MODE_PRIVATE)
        val userId = sharedPreferences.getInt("user_id", -1)
        if (userId != -1) {
            if (recipe.recipe_id != null) {
                val request = AddFavoriteRecipeRequest(userId, recipe.recipe_id)
                val call = apiService.addFavoriteRecipe(request)
                call.enqueue(object : Callback<BasicResponse> {
                    override fun onResponse(
                        call: Call<BasicResponse>,
                        response: Response<BasicResponse>
                    ) {
                        if (response.isSuccessful) {
                            val basicResponse = response.body()
                            if (basicResponse?.success == true) {
                                Toast.makeText(
                                    context,
                                    "Recept dodan u favorite",
                                    Toast.LENGTH_SHORT
                                ).show()
                            } else {
                                val errorMessage = basicResponse?.error
                                    ?: "Neuspješno dodavanje recepta u favorite"
                                Toast.makeText(context, "Greška: $errorMessage", Toast.LENGTH_SHORT)
                                    .show()
                                Log.e(
                                    "HomeFragment",
                                    "Greška prilikom dodavanja recepta: $errorMessage"
                                )
                            }
                        } else {
                            val errorMessage = response.errorBody()?.string()
                                ?: "Neuspješno dodavanje recepta u favorite"
                            Toast.makeText(context, "Greška: $errorMessage", Toast.LENGTH_SHORT)
                                .show()
                            Log.e(
                                "HomeFragment",
                                "Greška prilikom dodavanja recepta: $errorMessage"
                            )
                        }
                    }

                    override fun onFailure(call: Call<BasicResponse>, t: Throwable) {
                        Toast.makeText(
                            context,
                            "Error adding recipe to favorites: ${t.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                        Log.e("HomeFragment", "Error adding recipe to favorites: ${t.message}")
                        t.printStackTrace()
                    }
                })
            } else {
                Toast.makeText(context, "User ID is null", Toast.LENGTH_SHORT).show()
            }
        }
    }

 private fun searchRecipes(query: String) {
        val apiService = RetrofitClient.instance.create(ApiService::class.java)
        val call = apiService.searchRecipesByName(query)

        call.enqueue(object : Callback<List<RecipeDB>> {
            override fun onResponse(call: Call<List<RecipeDB>>, response: Response<List<RecipeDB>>) {
                if (response.isSuccessful) {
                    response.body()?.let { searchResults ->
                        recipes.clear()
                        recipes.addAll(searchResults)
                        adapter.notifyDataSetChanged()
                    }
                } else {
                    Toast.makeText(requireContext(), "Pogreška pri pretrazi recepata", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<RecipeDB>>, t: Throwable) {
                Toast.makeText(requireContext(), "Pogreška pri povezivanju sa serverom", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun fetchUserPreference(loggedInUserId: Int, callback: (Int?) -> Unit) {
        val apiService = RetrofitClient.instance.create(ApiService::class.java)
        val call = apiService.getUserPreference(loggedInUserId)

        call.enqueue(object : Callback<UserPreferenceResponse> {
            override fun onResponse(
                call: Call<UserPreferenceResponse>,
                response: Response<UserPreferenceResponse>
            ) {
                if (response.isSuccessful) {
                    val preferenceId = response.body()?.preference_id
                    callback(preferenceId)
                } else {
                    Toast.makeText(requireContext(), "Pogreška pri dohvaćanju preferencije", Toast.LENGTH_SHORT).show()
                    callback(null)
                }
            }

            override fun onFailure(call: Call<UserPreferenceResponse>, t: Throwable) {
                Toast.makeText(requireContext(), "Pogreška pri povezivanju s serverom", Toast.LENGTH_SHORT).show()
                callback(null)
            }
        })
    }

    private fun fetchRecipesWithPreference(userPreferenceId: Int?) {
        val apiService = RetrofitClient.instance.create(ApiService::class.java)
        val call = apiService.getRecipes()

        call.enqueue(object : Callback<List<RecipeDB>> {
            override fun onResponse(call: Call<List<RecipeDB>>, response: Response<List<RecipeDB>>) {
                if (response.isSuccessful) {
                    response.body()?.let { recipeDBList ->
                        recipes.clear()
                        recipes.addAll(recipeDBList)


                        adapter = RecipeAdapter(
                            recipes,
                            onItemClick = { selectedRecipe ->
                                val intent = Intent(requireContext(), DetailActivity::class.java).apply {
                                    putExtra("RECIPE_DATA", selectedRecipe)
                                }
                                startActivity(intent)
                            },
                            onDeleteClick = { recipe ->
                                showDeleteConfirmationDialog(recipe)
                            },
                            onEditClick = { recipe ->
                                editRecipe(recipe)
                                true
                            },
                            onFavoriteClick = { recipe ->
                                addToFavorites(recipe)
                                true
                            },
                            onAddToMenuClick = { recipe, dayId ->
                                val sharedPreferences = requireActivity().getSharedPreferences("user_prefs", MODE_PRIVATE)
                                val userId = sharedPreferences.getInt("user_id", -1)

                                if (userId != -1) {
                                    getPlanIdForUser(userId) { planId ->
                                        val recipeId = recipe.recipe_id ?: 0
                                        if (recipeId != 0) {
                                            addRecipeToMealPlan(recipeId, planId, dayId)
                                        } else {
                                            Toast.makeText(requireContext(), "Recept nema validan ID", Toast.LENGTH_SHORT).show()
                                        }
                                    }
                                } else {
                                    Toast.makeText(requireContext(), "Korisnički ID nije pronađen", Toast.LENGTH_SHORT).show()
                                }
                            },
                            categories,
                            loggedInUserId,
                            userPreferenceId ?: -1
                        )

                        recyclerView.adapter = adapter
                    }
                } else {
                    Toast.makeText(requireContext(), "Pogreška pri učitavanju recepata", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<RecipeDB>>, t: Throwable) {
                Toast.makeText(requireContext(), "Pogreška pri povezivanju s serverom", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun addRecipeToMealPlan(recipeId: Int, planId: Int, dayId: Int) {
        val apiService = RetrofitClient.instance.create(ApiService::class.java)
        val call = apiService.addRecipeToPlan(recipeId, planId, dayId)

        call.enqueue(object : Callback<MessageResponse> {
            override fun onResponse(call: Call<MessageResponse>, response: Response<MessageResponse>) {
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody != null && responseBody.success) {
                        Toast.makeText(requireContext(), "Recept uspješno dodan u plan", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(requireContext(), responseBody?.message ?: "Neuspješno dodavanje recepta", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    val error = response.errorBody()?.string() ?: "Neuspješan odgovor"
                    Toast.makeText(requireContext(), "Greška u odgovoru od servera", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<MessageResponse>, t: Throwable) {
                Toast.makeText(requireContext(), "Povezivanje sa serverom nije uspjelo", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun getPlanIdForUser(userId: Int, callback: (planId: Int) -> Unit) {
        val apiService = RetrofitClient.instance.create(ApiService::class.java)

        apiService.getPlanIdByUserId(userId).enqueue(object : Callback<PlanResponse> {
            override fun onResponse(call: Call<PlanResponse>, response: Response<PlanResponse>) {
                if (response.isSuccessful && response.body()?.plan_id != null) {
                    val planId = response.body()?.plan_id ?: 1
                    callback(planId)
                } else {
                    Toast.makeText(requireContext(), "Greška pri dohvaćanju plana", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<PlanResponse>, t: Throwable) {
                Log.e("MondayFragment", "Greška pri povezivanju s API-jem", t)
                Toast.makeText(requireContext(), "Greška pri povezivanju: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }


}
