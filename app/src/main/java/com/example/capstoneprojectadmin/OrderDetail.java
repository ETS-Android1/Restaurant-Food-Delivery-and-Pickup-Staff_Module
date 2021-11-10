package com.example.capstoneprojectadmin;

import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.capstoneprojectadmin.Common.Common;
import com.example.capstoneprojectadmin.Model.Rating;
import com.example.capstoneprojectadmin.ViewHolder.OrderDetailAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Arrays;

public class OrderDetail extends AppCompatActivity{

    Button cancelOrderButton;
    Button rateOrderButton;

    TextView orderId;
    TextView orderPhone;
    TextView orderAddress;
    TextView orderTime;
    TextView orderTotal;
    TextView orderRequest;
    TextView orderStatus;
    ImageView statusImage;
    TextView orderSchedule;
    String orderIdValue = "";
    String orderedFoods = "";
    RecyclerView foodList;
    RecyclerView.LayoutManager layoutManager;

    FirebaseDatabase database;
    DatabaseReference orders;
    DatabaseReference ratings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detail);

        database = FirebaseDatabase.getInstance("https://capstoneproject-c2dbe-default-rtdb.asia-southeast1.firebasedatabase.app");
        orders = database.getReference("Order");
        ratings = database.getReference("Rating");

        orderId = findViewById(R.id.order_id);
        orderPhone = findViewById(R.id.order_phone);
        orderAddress = findViewById(R.id.order_address);
        orderTime = findViewById(R.id.order_time);
        orderTotal = findViewById(R.id.order_price);
        orderRequest = findViewById(R.id.order_request);
        orderStatus = findViewById(R.id.order_status);
        cancelOrderButton = findViewById(R.id.cancelOrderButton);
        rateOrderButton = findViewById(R.id.rateButton);
        statusImage = findViewById(R.id.status_image);
        orderSchedule = findViewById(R.id.order_schedule);
        foodList = findViewById(R.id.foodList);
        foodList.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        foodList.setLayoutManager(layoutManager);

        if(getIntent() != null) {
            orderIdValue = getIntent().getStringExtra("OrderId");
        }

        ratings.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.child(orderIdValue).exists()) {
                    rateOrderButton.setText("Order Rated");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        OrderDetailAdapter adapter = new OrderDetailAdapter(Common.currentOrder.getFoods());
        adapter.notifyDataSetChanged();
        foodList.setAdapter(adapter);

        orderId.setText(String.format("Order # ") + orderIdValue);
        orderPhone.setText(Common.currentOrder.getOrderTelNo());
        orderTime.setText(Common.getDate(Long.parseLong(orderIdValue)));
        orderAddress.setText(Common.currentOrder.getOrderAddress());
        if(Common.currentOrder.getScheduledTime().equals("")) {
            orderSchedule.setText("Now");
        } else {
            orderSchedule.setText(Common.currentOrder.getScheduledTime());
        }
        orderTotal.setText(Common.currentOrder.getOrderPrice());
        if (Common.currentOrder.getOrderRequest().equals("")) {
            orderRequest.setText("None");
        } else {
            orderRequest.setText(Common.currentOrder.getOrderRequest());
        }

        //Set Image displayed for different order status
        orderStatus.setText(convertCodeToStatus(Common.currentOrder.getStatus()));
        if(convertCodeToStatus(Common.currentOrder.getStatus()).equals("Placed")) {
            statusImage.setImageResource(R.drawable.placedimage_trans);
        }
        else if (convertCodeToStatus(Common.currentOrder.getStatus()).equals("Preparing")) {
            statusImage.setImageResource(R.drawable.preparingimage_trans);
        }
        else if (convertCodeToStatus(Common.currentOrder.getStatus()).equals("Delivering")) {
            statusImage.setImageResource(R.drawable.deliveringimage_trans);
        }
        else if (convertCodeToStatus(Common.currentOrder.getStatus()).equals("Ready to Pickup")) {
            statusImage.setImageResource(R.drawable.readytopickupimage_trans);
        }
        else if (convertCodeToStatus(Common.currentOrder.getStatus()).equals("Completed")) {
            statusImage.setImageResource(R.drawable.completed_v2);
        }
        else {
            statusImage.setImageResource(R.drawable.cancelledimage_trans);
        }

        orderedFoods = TextUtils.join(", ", adapter.getFoodsName());
    }

    private String convertCodeToStatus(String status) {
        if(status.equals("0"))
            return "Placed";
        else if(status.equals("1"))
            return "Preparing";
        else if(status.equals("2"))
            return "Delivering";
        else if(status.equals("3"))
            return "Ready to Pickup";
        else if(status.equals("4"))
            return "Completed";
        else if(status.equals("-1"))
            return "Cancelled by You";
        else
            return "Cancelled by Restaurant";
    }
}