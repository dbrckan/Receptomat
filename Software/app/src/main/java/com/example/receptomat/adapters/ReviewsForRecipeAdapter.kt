package com.example.receptomat.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RatingBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.receptomat.R
import com.example.receptomat.entities.Review

class ReviewsForRecipeAdapter(
    private val reviews: List<Review>
) : RecyclerView.Adapter<ReviewsForRecipeAdapter.ReviewViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReviewViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_recipe_review_and_comment, parent, false)
        return ReviewViewHolder(view)
    }

    override fun onBindViewHolder(holder: ReviewViewHolder, position: Int) {
        val review = reviews[position]
        holder.bind(review)
    }

    override fun getItemCount(): Int = reviews.size

    class ReviewViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val username: TextView = itemView.findViewById(R.id.tv_username)
        private val ratingBar: RatingBar = itemView.findViewById(R.id.ratingBar)
        private val comment: TextView = itemView.findViewById(R.id.tv_comment)

        fun bind(review: Review) {
            username.text =  "Recenzirao:\n ${review.username}"
            ratingBar.rating = review.rating.toFloat()
            comment.text = review.comment
        }
    }
}