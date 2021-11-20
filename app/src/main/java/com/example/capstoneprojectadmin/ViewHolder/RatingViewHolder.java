package com.example.capstoneprojectadmin.ViewHolder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.capstoneprojectadmin.R;

public class RatingViewHolder extends RecyclerView.ViewHolder{
    public TextView ratingId;
    public TextView ratingCustId;
    public TextView ratingComment;
    public TextView ratingFood;
    public ImageView ratingStars;


    public RatingViewHolder(@NonNull View itemView) {
        super(itemView);
        ratingCustId = itemView.findViewById(R.id.rating_custId);
        ratingComment = itemView.findViewById(R.id.rating_comment);
        ratingStars = itemView.findViewById(R.id.rating_image);
        ratingFood = itemView.findViewById(R.id.rating_food);
    }
}
