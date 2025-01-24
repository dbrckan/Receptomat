package com.example.receptomat.weekMenu

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatActivity.MODE_PRIVATE
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.receptomat.R
import com.example.receptomat.adapters.RecipeWeekAdapter
import com.example.receptomat.entities.RecipePlan
import com.example.receptomat.recipeManagement.DetailActivity
import database.ApiService
import database.PlanResponse
import database.RecipePlanResponse
import database.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class SaturdayFragment : Fragment(R.layout.fragment_saturday) {
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: RecipeWeekAdapter
    private lateinit var recipeApi: ApiService
    private val recipesList = mutableListOf<RecipePlan>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_monday, container, false)

        recyclerView = view.findViewById(R.id.rv_recipes)
        recyclerView.layoutManager = LinearLayoutManager(context)

        Log.d("MondayFragment", "monday fragment")

        recipeApi = RetrofitClient.instance.create(ApiService::class.java)

        val sharedPreferences = activity?.getSharedPreferences("user_prefs", AppCompatActivity.MODE_PRIVATE)
        val userId = sharedPreferences?.getInt("user_id", -1)

        if (userId != null && userId != -1) {
            getPlanIdForUser(userId)
        } else {
            Log.d("MondayFragment", "user_id nije pronađen")
        }

        return view
    }

    override fun onResume() {
        super.onResume()

        val sharedPreferences = requireActivity().getSharedPreferences("user_prefs", MODE_PRIVATE)
        val userId = sharedPreferences.getInt("user_id", -1)

        if (userId != -1) {
            getPlanIdForUser(userId)
        } else {
            Toast.makeText(context, "Korisnički ID je null", Toast.LENGTH_SHORT).show()
        }
    }

    private fun loadRecipes(dayId: Int, planId: Int) {
        recipeApi.getRecipesByDay(dayId, planId).enqueue(object : Callback<RecipePlanResponse> {
            override fun onResponse(call: Call<RecipePlanResponse>, response: Response<RecipePlanResponse>) {
                if (response.isSuccessful && response.body()?.success == true) {
                    val recipes = response.body()?.data?.map {
                        RecipePlan(
                            recipe_id = it.recipe_id,
                            name = it.name,
                            time = it.time,
                            category_name = it.category_name,
                            plan_id = it.plan_id,
                            day_id = it.day_id
                        )
                    } ?: emptyList()

                    recipesList.clear()
                    recipesList.addAll(recipes)

                    adapter = RecipeWeekAdapter(
                        recipes,
                        { selectedRecipe ->
                            val intent = Intent(requireContext(), DetailActivity::class.java)
                            intent.putExtra("RECIPE_ID", selectedRecipe.recipe_id)
                            startActivity(intent)
                        },
                        { recipeId, planId, dayId, position ->
                            deleteRecipeFromMealPlan(recipeId, planId, dayId, position)
                        }
                    )
                    recyclerView.adapter = adapter

                } else {
                    Toast.makeText(context, "Greška: ${response.body()?.message}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<RecipePlanResponse>, t: Throwable) {
                Log.e("MondayFragment", "Neuspješno povezivanje: ${t.message}", t)
                Toast.makeText(context, "Neuspješno povezivanje: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun getPlanIdForUser(userId: Int) {
        val apiService = RetrofitClient.instance.create(ApiService::class.java)

        apiService.getPlanIdByUserId(userId).enqueue(object : Callback<PlanResponse> {
            override fun onResponse(call: Call<PlanResponse>, response: Response<PlanResponse>) {
                if (response.isSuccessful && response.body()?.plan_id != null) {
                    val planId = response.body()?.plan_id ?: 1
                    loadRecipes(6, planId)
                } else {
                    Toast.makeText(context, "Greška pri dohvaćanju plana", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<PlanResponse>, t: Throwable) {
                Log.e("MondayFragment", "Greška pri povezivanju s API-jem", t)
                Toast.makeText(context, "Greška pri povezivanju: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun deleteRecipeFromMealPlan(recipeId: Int, planId: Int, dayId: Int, position: Int) {
        val apiService = RetrofitClient.instance.create(ApiService::class.java)
        val call = apiService.removeRecipeFromPlan(recipeId, planId, dayId)

        Log.d("DeleteRecipe", "Pozivam API za brisanje recepta: recipeId=$recipeId, planId=$planId, dayId=$dayId")

        call.enqueue(object : Callback<RecipePlanResponse> {
            override fun onResponse(call: Call<RecipePlanResponse>, response: Response<RecipePlanResponse>) {
                if (response.isSuccessful && response.body()?.success == true) {
                    Toast.makeText(context, "Recept uspješno obrisan", Toast.LENGTH_SHORT).show()

                    if (position >= 0 && position < recipesList.size) {
                        recipesList.removeAt(position)
                        adapter.notifyItemRemoved(position)

                        val sharedPreferences = requireActivity().getSharedPreferences("user_prefs", MODE_PRIVATE)
                        val userId = sharedPreferences.getInt("user_id", -1)

                        if (userId != -1) {
                            getPlanIdForUser(userId)
                        }
                    } else {
                        Log.e("DeleteRecipe", "Pogrešan indeks: $position, veličina liste: ${recipesList.size}")
                    }
                } else {
                    Toast.makeText(context, response.body()?.message ?: "Greška pri brisanju recepta", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<RecipePlanResponse>, t: Throwable) {
                Log.e("DeleteRecipe", "Greška pri povezivanju s API-jem: ${t.message}")
                Toast.makeText(context, "Greška pri povezivanju s API-jem", Toast.LENGTH_SHORT).show()
            }
        })
    }
}