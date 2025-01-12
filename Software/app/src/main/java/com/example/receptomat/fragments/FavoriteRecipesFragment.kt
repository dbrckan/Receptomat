// FavoriteRecipesFragment.kt
package com.example.receptomat.fragments

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
import com.example.receptomat.adapters.FavoritesAdapter
import com.example.receptomat.entities.Recipe
import database.ApiService
import database.FavoriteRecipesResponse
import database.RetrofitClient
import android.content.Context.MODE_PRIVATE

class FavoriteRecipesFragment : Fragment() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: FavoritesAdapter
    private val favoriteRecipes = mutableListOf<Recipe>()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_favorite_recipes, container, false)
        recyclerView = view.findViewById(R.id.recyclerViewFavorites)
        recyclerView.layoutManager = LinearLayoutManager(context)
        adapter = FavoritesAdapter(favoriteRecipes)
        recyclerView.adapter = adapter

        val sharedPreferences = requireActivity().getSharedPreferences("user_prefs", MODE_PRIVATE)
        val userId = sharedPreferences.getInt("user_id", -1)
        if (userId != -1) {
            Log.d("FavoriteRecipesFragment", "User ID: $userId")
            loadFavoriteRecipes(userId)
        } else {
            Toast.makeText(context, "User ID is null", Toast.LENGTH_SHORT).show()
        }

        return view
    }

    private fun loadFavoriteRecipes(userId: Int) {
        Log.d("FavoriteRecipesFragment", "Loading favorite recipes for user ID: $userId")
        val apiService = RetrofitClient.instance.create(ApiService::class.java)
        val call = apiService.getFavoriteRecipesForPerson(userId)

        call.enqueue(object : retrofit2.Callback<FavoriteRecipesResponse> {
            override fun onResponse(call: retrofit2.Call<FavoriteRecipesResponse>, response: retrofit2.Response<FavoriteRecipesResponse>) {
                if (response.isSuccessful) {
                    val recipesResponse = response.body()
                    if (recipesResponse != null) {
                        Log.d("FavoriteRecipesFragment", "Recipes received: ${recipesResponse.recipes.size}")
                        favoriteRecipes.clear()
                        favoriteRecipes.addAll(recipesResponse.recipes)
                        adapter.notifyDataSetChanged()
                    } else {
                        Toast.makeText(context, "No recipes found", Toast.LENGTH_SHORT).show()
                        Log.d("FavoriteRecipesFragment", "No recipes found")
                    }
                } else {
                    val errorMessage = response.errorBody()?.string()
                    Toast.makeText(context, "Error loading favorite recipes: $errorMessage", Toast.LENGTH_SHORT).show()
                    Log.e("FavoriteRecipesFragment", "Error loading favorite recipes: $errorMessage")
                }
            }

            override fun onFailure(call: retrofit2.Call<FavoriteRecipesResponse>, t: Throwable) {
                Toast.makeText(context, "Error loading favorite recipes: ${t.message}", Toast.LENGTH_SHORT).show()
                Log.e("FavoriteRecipesFragment", "Error loading favorite recipes: ${t.message}")
                t.printStackTrace()
            }
        })
    }
}