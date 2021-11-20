package com.example.capstoneprojectadmin;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.capstoneprojectadmin.Model.Food;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class FoodDetail extends AppCompatActivity {

    TextView foodName;
    TextView foodPrice;
    TextView foodDesc;
    ImageView foodImage;

    CollapsingToolbarLayout collapsingToolbarLayout;

    String foodID = "";

    FirebaseDatabase database;
    DatabaseReference food;

    Food currentFood;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_detail);

        //Firebase
        database = FirebaseDatabase.getInstance("https://capstoneproject-c2dbe-default-rtdb.asia-southeast1.firebasedatabase.app");
        food = database.getReference("Food");


        foodDesc = findViewById(R.id.foodDesc);
        foodName = findViewById(R.id.foodName);
        foodPrice = findViewById(R.id.foodPrice);
        foodImage = findViewById(R.id.foodImage);
        collapsingToolbarLayout = findViewById(R.id.collapsing);
        collapsingToolbarLayout.setExpandedTitleTextAppearance(R.style.ExpandedAppbar);
        collapsingToolbarLayout.setCollapsedTitleTextAppearance(R.style.CollapsedAppbar);

        //Get foodID from previous Activity Intent
        if(getIntent() != null) {
            foodID = getIntent().getStringExtra("Food ID");
        }

        if(!foodID.isEmpty()) {
            getDetailFood(foodID);
        }
    }

    public void getDetailFood(String foodID) {
        food.child(foodID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                currentFood = snapshot.getValue(Food.class);

                Glide.with(getBaseContext()).load(currentFood.getFoodImageURL()).into(foodImage);

                collapsingToolbarLayout.setTitle(currentFood.getFoodName());

                foodPrice.setText(currentFood.getFoodPrice());

                foodName.setText(currentFood.getFoodName());

                foodDesc.setText(currentFood.getFoodDesc());
                            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
