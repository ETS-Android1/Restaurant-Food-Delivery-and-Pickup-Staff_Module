package com.example.capstoneprojectadmin.ViewHolder;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.capstoneprojectadmin.Model.CartDetail;
import com.example.capstoneprojectadmin.R;

import java.util.ArrayList;
import java.util.List;

class MyViewHolder extends RecyclerView.ViewHolder {

    public TextView foodName;
    public TextView foodQuantity;
    public TextView foodPrice;

    public MyViewHolder(@NonNull View itemView) {
        super(itemView);
        foodName = itemView.findViewById(R.id.food_name);
        foodQuantity = itemView.findViewById(R.id.food_quantity);
        foodPrice = itemView.findViewById(R.id.food_price);

    }
}

public class OrderDetailAdapter extends RecyclerView.Adapter<MyViewHolder> {

    List<CartDetail> foods;

    List<String> foodName = new ArrayList<>();

    public OrderDetailAdapter(List<CartDetail> foods) {
        this.foods = foods;

        for(int i=0; i < foods.size(); i++) {
            if(!foodName.contains(foods.get(i).getFoodName()) && foodName.size() < 2) {
                if (foodName.size() == 1 && foods.size() > 2) {
                    foodName.add(foods.get(i).getFoodName() + " etc.");
                } else {
                    foodName.add(foods.get(i).getFoodName());
                }
            }
        }
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.order_detail_layout,parent,false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        CartDetail foodList = foods.get(position);
        //foodName.add(foodList.getFoodName());
        holder.foodName.setText    (String.format(foodList.getFoodName()));
        holder.foodQuantity.setText(String.format("%sx",foodList.getQuantity()));
        holder.foodPrice.setText   (String.format("RM %s",foodList.getFoodPrice()));

    }

    public List<String> getFoodsName() {
        return foodName;
    }

    @Override
    public int getItemCount() {
        return foods.size();
    }
}
