package com.example.receptomat.entities

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.receptomat.R

class RecipeAdapter(private var recipes: List<Recipe>) : RecyclerView.Adapter<RecipeAdapter.RecipeViewHolder>() {

    inner class RecipeViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val recipeImage: ImageView = view.findViewById(R.id.ivPicture)
        private val recipeName: TextView = view.findViewById(R.id.tvNameRecipe)
        private val timeRecipe: TextView = view.findViewById(R.id.tvTimeRecipe)
        private val mealRecipe: TextView = view.findViewById(R.id.tvMeal)

        fun bind(recipe: Recipe) {
            recipeName.text = recipe.name
            timeRecipe.text = itemView.context.getString(R.string.preparation_time, recipe.preparationTime)
            mealRecipe.text = recipe.meal.displayName

            val imageResId = if (recipe.picture.isNullOrEmpty()) {
                R.drawable.nedostupno
            } else {
                itemView.context.resources.getIdentifier(recipe.picture, "drawable", itemView.context.packageName)
            }

            if (imageResId != 0) {
                recipeImage.setImageResource(imageResId)
            } else {
                recipeImage.setImageResource(0)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecipeViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recipe_list_item, parent, false)
        return RecipeViewHolder(view)
    }

    override fun getItemCount(): Int = recipes.size

    override fun onBindViewHolder(holder: RecipeViewHolder, position: Int) {
        val recipe = recipes[position]
        holder.bind(recipe)
    }

}