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
import com.example.receptomat.entities.Recipe
import com.example.receptomat.entities.RecipeAdapter
import com.example.receptomat.helpers.MockDataLoader
import com.example.receptomat.recipeManagement.AddNewRecipeActivity
import com.example.receptomat.recipeManagement.DetailActivity
import com.example.receptomat.recipeManagement.EditRecipeActivity
import database.ApiService
import database.BasicResponse
import database.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HomeFragment : Fragment(R.layout.fragment_home) {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: RecipeAdapter
    private val recipes = mutableListOf<Recipe>()

    private lateinit var createRecipeLauncher: ActivityResultLauncher<Intent>
    private lateinit var editRecipeLauncher: ActivityResultLauncher<Intent>

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recipes.addAll(MockDataLoader.getDemoData())

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
            },
            onFavoriteClick = { recipe ->
                addToFavorites(recipe)
            },
        )
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(view.context)

        createRecipeLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val newRecipe = result.data?.getParcelableExtra<Recipe>("NEW_RECIPE")
                if (newRecipe != null) {
                    recipes.add(newRecipe)
                    adapter.updateRecipes(recipes)
                    adapter.notifyDataSetChanged()
                }
            }
        }

        editRecipeLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val updatedRecipe = result.data?.getParcelableExtra<Recipe>("UPDATED_RECIPE")
                if (updatedRecipe != null) {
                    val index = recipes.indexOfFirst { it.recipe_id == updatedRecipe.recipe_id }
                    if (index != -1) {
                        recipes[index] = updatedRecipe
                        adapter.notifyItemChanged(index)
                    }
                }
            }
        }

        val btnCreateRecipe = view.findViewById<ImageButton>(R.id.ib_home_fragment_crate_recipe)
        btnCreateRecipe.setOnClickListener {
            val intent = Intent(requireContext(), AddNewRecipeActivity::class.java)
            createRecipeLauncher.launch(intent)
        }
    }

    private fun showDeleteConfirmationDialog(recipe: Recipe) {
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

    private fun deleteRecipe(recipe: Recipe) {
        recipes.remove(recipe)
        adapter.updateRecipes(recipes)
        Toast.makeText(requireContext(), "Recept je obrisan.", Toast.LENGTH_SHORT).show()
    }

    private fun editRecipe(recipe: Recipe) {
        val intent = Intent(requireContext(), EditRecipeActivity::class.java)
        intent.putExtra("recipe", recipe)
        editRecipeLauncher.launch(intent)
    }

    private fun addToFavorites(recipe: Recipe) {
        val apiService = RetrofitClient.instance.create(ApiService::class.java)
        val sharedPreferences = requireActivity().getSharedPreferences("user_prefs", MODE_PRIVATE)
        val userId = sharedPreferences.getInt("user_id", -1)
        if (userId != -1) {
            val request = ApiService.AddFavoriteRecipeRequest(userId, recipe.recipe_id)
            val call = apiService.addFavoriteRecipe(request)
            call.enqueue(object : Callback<BasicResponse> {
                override fun onResponse(call: Call<BasicResponse>, response: Response<BasicResponse>) {
                    if (response.isSuccessful) {
                        val basicResponse = response.body()
                        if (basicResponse?.success == true) {
                            Toast.makeText(context, "Recept dodan u favorite", Toast.LENGTH_SHORT).show()
                        } else {
                            val errorMessage = basicResponse?.error ?: "Neuspješno dodavanje recepta u favorite"
                            Toast.makeText(context, "Greška: $errorMessage", Toast.LENGTH_SHORT).show()
                            Log.e("HomeFragment", "Greška prilikom dodavanja recepta: $errorMessage")
                        }
                    } else {
                        val errorMessage = response.errorBody()?.string() ?: "Neuspješno dodavanje recepta u favorite"
                        Toast.makeText(context, "Greška: $errorMessage", Toast.LENGTH_SHORT).show()
                        Log.e("HomeFragment", "Greška prilikom dodavanja recepta: $errorMessage")
                    }
                }

                override fun onFailure(call: Call<BasicResponse>, t: Throwable) {
                    Toast.makeText(context, "Error adding recipe to favorites: ${t.message}", Toast.LENGTH_SHORT).show()
                    Log.e("HomeFragment", "Error adding recipe to favorites: ${t.message}")
                    t.printStackTrace()
                }
            })
        } else {
            Toast.makeText(context, "User ID is null", Toast.LENGTH_SHORT).show()
        }
    }
}