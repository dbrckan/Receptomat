package com.example.receptomat.fragments

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.receptomat.R
import com.example.receptomat.entities.Recipe
import com.example.receptomat.entities.RecipeAdapter
import com.example.receptomat.helpers.MockDataLoader
import com.example.receptomat.recipeManagement.AddNewRecipeActivity
import com.example.receptomat.recipeManagement.DetailActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton

class HomeFragment : Fragment(R.layout.fragment_home) {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: RecipeAdapter
    private val recipes = mutableListOf<Recipe>()

    private lateinit var createRecipeResultLauncher: ActivityResultLauncher<Intent>

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
            }
        )
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(view.context)

        createRecipeResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val newRecipe = result.data?.getParcelableExtra<Recipe>("NEW_RECIPE")
                if (newRecipe != null) {
                    recipes.add(newRecipe)
                    adapter.updateRecipes(recipes)
                    adapter.notifyDataSetChanged()
                }
            }
        }

        val btnCreateRecipe = view.findViewById<FloatingActionButton>(R.id.fab_home_fragment_crate_recipe)
        btnCreateRecipe.setOnClickListener {
            val intent = Intent(requireContext(), AddNewRecipeActivity::class.java)
            createRecipeResultLauncher.launch(intent) // Launch the activity using the new method
        }
    }


}