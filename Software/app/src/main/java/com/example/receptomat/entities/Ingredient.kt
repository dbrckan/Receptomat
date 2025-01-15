package com.example.receptomat.entities

data class Ingredient(
    val item_name: String,
    val quantity: Double,
    val unit_id: Int,
    val unit_name: String?
)
