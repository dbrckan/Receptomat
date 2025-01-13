package com.example.receptomat.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.receptomat.R
import com.example.receptomat.entities.Review

class ReviewsAdapter(
    private val reviews: List<Review>,
    private val removeReviewCallback: (Review) -> Unit
) : RecyclerView.Adapter<ReviewsAdapter.ReviewViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReviewViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_review, parent, false)
        return ReviewViewHolder(view)
    }

    override fun onBindViewHolder(holder: ReviewViewHolder, position: Int) {
        val review = reviews[position]
        holder.bind(review)
        holder.removeButton.setOnClickListener {
            removeReviewCallback(review)
        }
    }

    override fun getItemCount(): Int = reviews.size

    class ReviewViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val comment: TextView = itemView.findViewById(R.id.comment)
        val rating: TextView = itemView.findViewById(R.id.rating)
        val date: TextView = itemView.findViewById(R.id.date)
        val removeButton: Button = itemView.findViewById(R.id.button_remove_review)

        fun bind(review: Review) {
            comment.text = review.comment
            rating.text = review.rating.toString()
            date.text = review.date
        }
    }
}