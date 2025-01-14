package com.example.receptomat.entities

data class Review(
    val review_id: String,
    val comment: String,
    val rating: String,
    val date: String,
    val recipe_id: String
)

data class ReviewsResponse(
    val success: Boolean,
    val reviews: List<Review>,
    val recipes: List<Recipe>
)