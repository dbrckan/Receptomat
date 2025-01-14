package com.example.receptomat.entities

import android.content.Intent
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.receptomat.R
import com.example.receptomat.recipeManagement.DetailActivity
import kotlin.Unit

class RecipeAdapter(
    private var recipes: List<RecipeDB>,
    private val onItemClick: (RecipeDB) -> Unit,
    private val onDeleteClick: (RecipeDB) -> Unit,
    private val onEditClick: (RecipeDB) -> Unit,
    private val onFavoriteClick: (RecipeDB) -> Unit,
    private val categories: List<Category>
) : RecyclerView.Adapter<RecipeAdapter.RecipeViewHolder>() {

    inner class RecipeViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val recipeImage: ImageView = view.findViewById(R.id.ivPicture)
        private val recipeName: TextView = view.findViewById(R.id.tvNameRecipe)
        private val timeRecipe: TextView = view.findViewById(R.id.tvTimeRecipe)
        private val ivOverflowMenu: ImageView = itemView.findViewById(R.id.ivOverflowMenu)
        private val categoryRecipe: TextView = view.findViewById(R.id.tvCategoryRecipe) // Ovdje se koristi TextView za prikaz kategorije

        fun bind(recipe: RecipeDB) {
            recipeName.text = recipe.name
            timeRecipe.text = itemView.context.getString(R.string.preparation_time, recipe.time)

            val category = categories.find { it.category_id == recipe.category_id }
            categoryRecipe.text = category?.name ?: "Nepoznato"

            ivOverflowMenu.setOnClickListener {
                showPopupMenu(it, recipe)
            }

            itemView.setOnClickListener {
                val intent = Intent(itemView.context, DetailActivity::class.java)
                intent.putExtra("RECIPE_ID", recipe.recipe_id ?: -1)
                itemView.context.startActivity(intent)
            }
        }
    }

    private fun showPopupMenu(view: View, recipe: RecipeDB) {
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
                R.id.action_favorite -> {
                    onFavoriteClick(recipe)
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
}