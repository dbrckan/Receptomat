package com.example.receptomat.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.receptomat.R
import com.example.receptomat.entities.RecipeDB
import com.example.receptomat.adapters.FavoritesAdapter
import com.example.receptomat.recipeManagement.DetailActivity
import database.ApiService
import database.BasicResponse
import database.FavoriteRecipesResponse
import database.RetrofitClient
import android.content.Context.MODE_PRIVATE
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FavoriteRecipesFragment : Fragment() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: FavoritesAdapter
    private val favoriteRecipes = mutableListOf<RecipeDB>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_favorite_recipes, container, false)
        recyclerView = view.findViewById(R.id.recyclerViewFavorites)
        recyclerView.layoutManager = LinearLayoutManager(context)

        adapter = FavoritesAdapter(
            favoriteRecipes,
            onRemoveClick = { recipe -> removeFavoriteRecipe(recipe) },
            onItemClick = { recipe -> navigateToDetail(recipe) }
        )
        recyclerView.adapter = adapter

        return view
    }

    override fun onResume() {
        super.onResume()
        val sharedPreferences = requireActivity().getSharedPreferences("user_prefs", MODE_PRIVATE)
        val userId = sharedPreferences.getInt("user_id", -1)
        if (userId != -1) {
            Log.d("FavoriteRecipesFragment", "Korisnički ID: $userId")
            loadFavoriteRecipes(userId)
        } else {
            Toast.makeText(context, "Korisnički ID je null", Toast.LENGTH_SHORT).show()
        }
    }

    fun loadFavoriteRecipes(userId: Int) {
        val apiService = RetrofitClient.instance.create(ApiService::class.java)
        val call = apiService.getFavoriteRecipesForPerson(userId)

        call.enqueue(object : Callback<FavoriteRecipesResponse> {
            override fun onResponse(call: Call<FavoriteRecipesResponse>, response: Response<FavoriteRecipesResponse>) {
                if (response.isSuccessful) {
                    val recipesResponse = response.body()
                    if (recipesResponse != null && !recipesResponse.recipes.isNullOrEmpty()) {
                        favoriteRecipes.clear()
                        favoriteRecipes.addAll(recipesResponse.recipes.map { recipe ->
                            RecipeDB(
                                recipe_id = recipe.recipe_id,
                                name = recipe.name,
                                description = recipe.description ?: "",
                                time = recipe.time ?: 1,
                                user_id = recipe.user_id,
                                category_id = recipe.category_id ?: 1,
                                preference_id = recipe.preference_id,
                                image_path = recipe.image_path
                            )
                        })
                        Log.d("FavoriteRecipesFragment", "Učitani recepti: $favoriteRecipes")
                        adapter.notifyDataSetChanged()
                    } else {
                        Toast.makeText(context, "Nema pronađenih recepata", Toast.LENGTH_SHORT).show()
                        Log.d("FavoriteRecipesFragment", "Nema pronađenih recepata")
                    }
                } else {
                    val errorMessage = response.errorBody()?.string()
                    Toast.makeText(context, "Greška pri učitavanju omiljenih recepata: $errorMessage", Toast.LENGTH_SHORT).show()
                    Log.e("FavoriteRecipesFragment", "Greška pri učitavanju omiljenih recepata: $errorMessage")
                }
            }

            override fun onFailure(call: Call<FavoriteRecipesResponse>, t: Throwable) {
                Toast.makeText(context, "Greška pri učitavanju omiljenih recepata: ${t.message}", Toast.LENGTH_SHORT).show()
                Log.e("FavoriteRecipesFragment", "Greška pri učitavanju omiljenih recepata: ${t.message}")
                t.printStackTrace()
            }
        })
    }

    private fun removeFavoriteRecipe(recipe: RecipeDB) {
        val apiService = RetrofitClient.instance.create(ApiService::class.java)
        val sharedPreferences = requireActivity().getSharedPreferences("user_prefs", MODE_PRIVATE)
        val userId = sharedPreferences.getInt("user_id", -1)

        if (userId != -1 && recipe.recipe_id != null) {
            val call = apiService.removeFavoriteRecipe(userId, recipe.recipe_id)
            call.enqueue(object : Callback<BasicResponse> {
                override fun onResponse(call: Call<BasicResponse>, response: Response<BasicResponse>) {
                    if (response.isSuccessful) {
                        val basicResponse = response.body()
                        if (basicResponse?.success == true) {
                            favoriteRecipes.remove(recipe)
                            adapter.notifyDataSetChanged()
                            Toast.makeText(context, "Recept je uklonjen iz favorita", Toast.LENGTH_SHORT).show()
                        } else {
                            val errorMessage = basicResponse?.error ?: "Neuspješno uklanjanje recepta iz favorita"
                            Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
                            Log.e("FavoriteRecipesFragment", "Greška pri uklanjanju recepta iz favorita: $errorMessage")
                        }
                    } else {
                        val errorMessage = response.errorBody()?.string() ?: "Neuspješno uklanjanje recepta iz favorita"
                        Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
                        Log.e("FavoriteRecipesFragment", "Greška pri uklanjanju recepta iz favorita: $errorMessage")
                    }
                }

                override fun onFailure(call: Call<BasicResponse>, t: Throwable) {
                    Toast.makeText(context, "Neuspješno uklanjanje recepta iz favorita", Toast.LENGTH_SHORT).show()
                    Log.e("FavoriteRecipesFragment", "Greška pri uklanjanju recepta iz favorita: ${t.message}")
                }
            })
        } else {
            Toast.makeText(context, "Korisnički ID ili ID recepta je null", Toast.LENGTH_SHORT).show()
            Log.e("FavoriteRecipesFragment", "Korisnički ID ili ID recepta je null")
        }
    }

    private fun navigateToDetail(recipe: RecipeDB) {
        val intent = Intent(context, DetailActivity::class.java)
        intent.putExtra("RECIPE_ID", recipe.recipe_id)
        startActivity(intent)
    }
}