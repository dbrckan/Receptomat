package com.example.receptomat.entities

data class Review(
    val review_id: String,
    val comment: String,
    val username : String,
    val rating: String,
    val date: String,
    val recipe_id: String
)
