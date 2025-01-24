package com.example.receptomat.entities

data class RecipePlan(
    val recipe_id: Int,
    val name: String,
    val time: String,
    val category_name: String,
    val plan_id: Int,
    val day_id: Int
)
