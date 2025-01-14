package com.example.receptomat.entities

data class Ingredient(
    val item_name: String,
    val quantity: Float,
    val unit_id: Int,
    val unit_name: String?
)
