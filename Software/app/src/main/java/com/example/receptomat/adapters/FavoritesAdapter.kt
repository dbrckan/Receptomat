package com.example.receptomat.adapters

import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.receptomat.R
import com.example.receptomat.entities.Category
import com.example.receptomat.entities.RecipeDB
import com.example.receptomat.recipeManagement.DetailActivity
import database.ApiService
import database.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FavoritesAdapter(
    private val favoriteRecipes: List<RecipeDB>,
    private val onRemoveClick: (RecipeDB) -> Unit,
    private val onItemClick: (RecipeDB) -> Unit
) : RecyclerView.Adapter<FavoritesAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val recipeName: TextView = view.findViewById(R.id.tvNameRecipe)
        val mealName: TextView = view.findViewById(R.id.tvMeal)
        val preparationTime: TextView = view.findViewById(R.id.tvTimeRecipe)
        val recipeImage: ImageView = view.findViewById(R.id.ivPicture)
        val removeButton: Button = view.findViewById(R.id.btnRemove)

        fun bind(recipe: RecipeDB) {
            recipeName.text = recipe.name
            preparationTime.text = itemView.context.getString(R.string.preparation_time, recipe.time)

            val imageResId = if (recipe.image_path.isNullOrEmpty()) {
                R.drawable.nedostupno
            } else {
                val resId = itemView.context.resources.getIdentifier(
                    recipe.image_path,
                    "drawable",
                    itemView.context.packageName
                )
                if (resId != 0) resId else {
                    Log.e("FavoritesAdapter", "Invalid image path: ${recipe.image_path}")
                    R.drawable.nedostupno
                }
            }

            recipeImage.setImageResource(imageResId)

            fetchCategoryName(recipe.category_id) { categoryName ->
                mealName.text = categoryName ?: itemView.context.getString(R.string.unknown_meal)
            }

            itemView.setOnClickListener {
                onItemClick(recipe)
            }

            removeButton.setOnClickListener {
                onRemoveClick(recipe)
            }
        }
    }

    private fun fetchCategoryName(categoryId: Int, callback: (String?) -> Unit) {
        val apiService = RetrofitClient.instance.create(ApiService::class.java)
        val call = apiService.getCategories()

        call.enqueue(object : Callback<List<Category>> {
            override fun onResponse(call: Call<List<Category>>, response: Response<List<Category>>) {
                if (response.isSuccessful) {
                    val categories = response.body()
                    val category = categories?.find { it.category_id == categoryId }
                    callback(category?.name)
                } else {
                    Log.e("FavoritesAdapter", "Failed to fetch categories: ${response.errorBody()?.string()}")
                    callback(null)
                }
            }

            override fun onFailure(call: Call<List<Category>>, t: Throwable) {
                Log.e("FavoritesAdapter", "Error fetching categories", t)
                callback(null)
            }
        })
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.recipe_favorites, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val recipe = favoriteRecipes[position]
        holder.bind(recipe)
    }

    override fun getItemCount(): Int {
        return favoriteRecipes.size
    }
}