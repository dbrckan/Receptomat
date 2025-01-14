package com.example.receptomat.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.receptomat.R
import com.example.receptomat.entities.Recipe

class FavoritesAdapter(
    private val favoriteRecipes: List<Recipe>,
    private val onRemoveClick: (Recipe) -> Unit
) : RecyclerView.Adapter<FavoritesAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val recipeName: TextView = view.findViewById(R.id.tvNameRecipe)
        val mealName: TextView = view.findViewById(R.id.tvMeal)
        val preparationTime: TextView = view.findViewById(R.id.tvTimeRecipe)
        val recipeImage: ImageView = view.findViewById(R.id.ivPicture)
        val removeButton: Button = view.findViewById(R.id.btnRemove)

        fun bind(recipe: Recipe) {
            recipeName.text = recipe.name
            mealName.text = recipe.meal?.displayName ?: itemView.context.getString(R.string.unknown_meal)
            preparationTime.text = itemView.context.getString(R.string.preparation_time, recipe.preparationTime)

            val imageResId = if (recipe.image_path.isNullOrEmpty()) {
                R.drawable.nedostupno
            } else{

                val resId = itemView.context.resources.getIdentifier(recipe.image_path, "drawable", itemView.context.packageName)
                if (resId != 0) resId else {
                    Log.e("FavoritesAdapter", "Invalid image path: ${recipe.image_path}")
                    R.drawable.nedostupno
                }
            }

            recipeImage.setImageResource(imageResId)

            removeButton.setOnClickListener {
                onRemoveClick(recipe)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recipe_favorites, parent, false)
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