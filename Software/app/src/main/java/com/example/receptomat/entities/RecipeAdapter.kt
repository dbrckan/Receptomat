package com.example.receptomat.entities

import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.receptomat.R

class RecipeAdapter(private var recipes: List<Recipe>, private val onItemClick: (Recipe) -> Unit, private val onDeleteClick: (Recipe) -> Unit, private val onEditClick: (Recipe) -> Unit) : RecyclerView.Adapter<RecipeAdapter.RecipeViewHolder>() {

    inner class RecipeViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val recipeImage: ImageView = view.findViewById(R.id.ivPicture)
        private val recipeName: TextView = view.findViewById(R.id.tvNameRecipe)
        private val timeRecipe: TextView = view.findViewById(R.id.tvTimeRecipe)
        private val mealRecipe: TextView = view.findViewById(R.id.tvMeal)
        private val ivOverflowMenu: ImageView = itemView.findViewById(R.id.ivOverflowMenu)

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
            itemView.setOnClickListener {
                onItemClick(recipe)
            }
            ivOverflowMenu.setOnClickListener {
                showPopupMenu(it, recipe)
            }
        }
    }
    private fun showPopupMenu(view: View, recipe: Recipe) {
        val popupMenu = PopupMenu(view.context, view)
        popupMenu.menuInflater.inflate(R.menu.menu_recipe, popupMenu.menu)

        popupMenu.setOnMenuItemClickListener { menuItem: MenuItem ->
            when (menuItem.itemId) {
                R.id.action_delete -> {
                    onDeleteClick(recipe)
                    true
                }
                R.id.action_edit -> {
                    onEditClick(recipe)
                    true
                }
                R.id.action_favorite -> {
                    true
                }
                R.id.action_add_to_menu -> {
                    true
                }
                else -> false
            }
        }
        popupMenu.show()
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
    fun updateRecipes(newRecipes: List<Recipe>) {
        recipes = newRecipes
        notifyDataSetChanged()
    }
}