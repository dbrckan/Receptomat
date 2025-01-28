package com.example.receptomat.entities

import android.app.AlertDialog
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
    val onAddToMenuClick: (RecipeDB, Int) -> Unit,
    private val categories: List<Category>,
    private val loggedInUserId: Int,
    private val userPreferenceId: Int
) : RecyclerView.Adapter<RecipeAdapter.RecipeViewHolder>() {

    inner class RecipeViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val recipeImage: ImageView = view.findViewById(R.id.ivPicture)
        private val recipeName: TextView = view.findViewById(R.id.tvNameRecipe)
        private val timeRecipe: TextView = view.findViewById(R.id.tvTimeRecipe)
        private val ivOverflowMenu: ImageView = itemView.findViewById(R.id.ivOverflowMenu)
        private val categoryRecipe: TextView = view.findViewById(R.id.tvCategoryRecipe)
        private val cardView: androidx.cardview.widget.CardView = view.findViewById(R.id.cardView)

        fun bind(recipe: RecipeDB) {
            recipeName.text = recipe.name
            timeRecipe.text = itemView.context.getString(R.string.preparation_time, recipe.time)

            val category = categories.find { it.category_id == recipe.category_id }
            categoryRecipe.text = category?.name ?: "Nepoznato"

            val categoryImage = getImageForCategory(recipe.category_id)
            recipeImage.setImageResource(categoryImage)

            ivOverflowMenu.setOnClickListener {
                showPopupMenu(it, recipe)
            }

            itemView.setOnClickListener {
                val intent = Intent(itemView.context, DetailActivity::class.java)
                intent.putExtra("RECIPE_ID", recipe.recipe_id ?: -1)
                itemView.context.startActivity(intent)
            }

            if (recipe.preference_id == userPreferenceId) {
                cardView.setCardBackgroundColor(itemView.context.getColor(R.color.highlight_color))
            } else if (recipe.user_id == loggedInUserId) {
                cardView.setCardBackgroundColor(itemView.context.getColor(R.color.card_color2))
            } else {
                cardView.setCardBackgroundColor(itemView.context.getColor(R.color.card_color))
            }
        }
    }

    private fun getImageForCategory(categoryId: Int): Int {
        return when (categoryId) {
            1 -> R.drawable.breakfast
            2 -> R.drawable.lunch
            3 -> R.drawable.dinner
            4 -> R.drawable.dessert
            else -> R.drawable.nedostupno
        }
    }

    private fun showPopupMenu(view: View, recipe: RecipeDB) {
        val popupMenu = PopupMenu(view.context, view)
        popupMenu.menuInflater.inflate(R.menu.menu_recipe, popupMenu.menu)

        val isRecipeOwner = recipe.user_id == loggedInUserId
        popupMenu.menu.findItem(R.id.action_delete).isVisible = isRecipeOwner
        popupMenu.menu.findItem(R.id.action_edit).isVisible = isRecipeOwner

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
                    onFavoriteClick(recipe)
                    true
                }
                R.id.action_add_to_menu -> {
                    showAddToMenuDialog(view, recipe)
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

    private fun showAddToMenuDialog(view: View, recipe: RecipeDB) {
        val days = arrayOf("Ponedjeljak", "Utorak", "Srijeda", "Četvrtak", "Petak", "Subota", "Nedjelja")

        val dayMapping = mapOf(
            "Ponedjeljak" to 1,
            "Utorak" to 2,
            "Srijeda" to 3,
            "Četvrtak" to 4,
            "Petak" to 5,
            "Subota" to 6,
            "Nedjelja" to 7
        )

        val builder = AlertDialog.Builder(view.context)
        builder.setTitle("Odaberite dan za dodavanje recepta")
        builder.setItems(days) { _, which ->
            val selectedDay = days[which]
            val dayId = dayMapping[selectedDay] ?: 1
            onAddToMenuClick(recipe, dayId)
        }
        builder.show()
    }
}