package com.example.animalmate

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RatingBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions

class ReviewAdapter(options : FirebaseRecyclerOptions<ReviewInfo>)
    : FirebaseRecyclerAdapter<ReviewInfo, ReviewAdapter.ViewHolder>(options) {
    inner class ViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView){
        var reviewid : TextView
        var reviewcontent : TextView
        var reviewrating : RatingBar
        init{
            reviewid = itemView.findViewById(R.id.review_ID)
            reviewcontent = itemView.findViewById(R.id.review_content)
            reviewrating = itemView.findViewById(R.id.review_rating)

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.row_review, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int, model: ReviewInfo) {
        holder.reviewid.text = model.ID
        holder.reviewcontent.text = model.content
        holder.reviewrating.rating = model.rating
    }
}