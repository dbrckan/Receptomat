package com.example.receptomat.adapters

import android.app.AlertDialog
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.receptomat.R
import com.example.receptomat.entities.RecipePlan

class RecipeWeekAdapter(private val recipes: List<RecipePlan>,
                        private val onRecipeClick: (RecipePlan) -> Unit,
                        private val onDeleteClick: (Int, Int, Int, Int) -> Unit
) : RecyclerView.Adapter<RecipeWeekAdapter.RecipeViewHolder>() {

    inner class RecipeViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val name: TextView = view.findViewById(R.id.tvNameRecipe)
        val time: TextView = view.findViewById(R.id.tvTimeRecipe)
        val category: TextView = view.findViewById(R.id.tvCategoryRecipe)
        private val recipeImage: ImageView = view.findViewById(R.id.ivPicture)

        init {
            itemView.setOnClickListener {
                val recipe = recipes[adapterPosition]
                onRecipeClick(recipe)
            }
            recipeImage.setImageResource(R.drawable.nedostupno)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecipeViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.recipe_week_planner, parent, false)
        return RecipeViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecipeViewHolder, position: Int) {
        val recipe = recipes[position]
        holder.name.text = recipe.name
        holder.time.text = recipe.time + " minuta"
        holder.category.text = recipe.category_name


        holder.itemView.findViewById<Button>(R.id.btn_remove_ingredient).setOnClickListener {
            val context = holder.itemView.context
            val recipe = recipes[position]

            val alertDialog = android.app.AlertDialog.Builder(context)
                .setTitle("Potvrdi brisanje")
                .setMessage("Jeste li sigurni da želite obrisati ovaj recept?")
                .setPositiveButton("Obriši") { dialog, _ ->
                    dialog.dismiss()
                    onDeleteClick(recipe.recipe_id, recipe.plan_id, recipe.day_id, position)
                }
                .setNegativeButton("Odustani") { dialog, _ ->
                    dialog.dismiss()
                }
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

    }

    override fun getItemCount(): Int = recipes.size

}