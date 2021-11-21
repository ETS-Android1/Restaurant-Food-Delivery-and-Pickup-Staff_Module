package com.example.capstoneprojectadmin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Spinner;

import com.example.capstoneprojectadmin.Common.Common;
import Interface.ItemClickListener;

import com.example.capstoneprojectadmin.Model.Order;
import com.example.capstoneprojectadmin.ViewHolder.OrderViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.jaredrummler.materialspinner.MaterialSpinner;

public class OrderStatus extends AppCompatActivity {

    Button allFilterButton;
    Button activeFilterButton;
    Button completedFilterButton;
    Button cancelledFilterButton;

    public RecyclerView recyclerView;

    FirebaseRecyclerAdapter<Order,OrderViewHolder> adapter;

    FirebaseDatabase database;
    DatabaseReference orders;

    Spinner spinner;

    String selectedFilter = "Order";

    Query collectionReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_status);

        //Firebase
        database = FirebaseDatabase.getInstance("https://capstoneproject-c2dbe-default-rtdb.asia-southeast1.firebasedatabase.app");
        orders = database.getReference("Order");

        recyclerView = findViewById(R.id.listOrders);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        loadOrders("Order");

        adapter.notifyDataSetChanged();

        allFilterButton = findViewById(R.id.allFilterButton);
        activeFilterButton = findViewById(R.id.activeFilterButton);
        completedFilterButton = findViewById(R.id.completedFilterButton);
        cancelledFilterButton = findViewById(R.id.cancelledFilterButton);

        allFilterButton.setBackgroundColor(Color.parseColor("#15BCFF"));
    }

    private void loadOrders(String filterSelected) {
        Query readQuery;

        if(filterSelected.equals("Order")) {
            adapter = new FirebaseRecyclerAdapter<Order, OrderViewHolder>(Order.class, R.layout.order_layout, OrderViewHolder.class, orders){

                @Override
                protected void populateViewHolder(OrderViewHolder orderViewHolder, Order order, int i) {
                    orderViewHolder.txtOrderId.setText(String.format("Order # ")+ adapter.getRef(i).getKey());
                    orderViewHolder.txtOrderStatus.setText(convertCodeToStatus(order.getStatus()));
                    orderViewHolder.txtOrderPrice.setText(order.getOrderPrice());
                    orderViewHolder.txtOrderType.setText(String.format("Order Type: ") + order.getOrderType());
                    orderViewHolder.txtOrderDate.setText(Common.getDate(Long.parseLong(adapter.getRef(i).getKey())));

                    if(!order.getScheduledTime().isEmpty()){
                        if (order.getStatus().equals("0")){
                            orderViewHolder.txtScheduledTime.setText("Scheduled for " + order.getScheduledTime());
                            orderViewHolder.txtScheduledTime.setVisibility(View.VISIBLE);
                        }
                        else
                            orderViewHolder.txtScheduledTime.setVisibility(View.INVISIBLE);
                    }
                    else
                        orderViewHolder.txtScheduledTime.setVisibility(View.INVISIBLE);


                    //Set text color of order status
                    if(convertCodeToStatus(order.getStatus()).equals("Placed")) {
                        orderViewHolder.txtOrderStatus.setTextColor(Color.parseColor("#29B438"));
                    }
                    else if ((convertCodeToStatus(order.getStatus()).equals("Delivering")) || (convertCodeToStatus(order.getStatus()).equals("Ready to Pickup"))) {
                        orderViewHolder.txtOrderStatus.setTextColor(Color.parseColor("#FF7800"));
                    }
                    else if (convertCodeToStatus(order.getStatus()).equals("Preparing")) {
                        orderViewHolder.txtOrderStatus.setTextColor(Color.parseColor("#EAA825"));
                    }
                    else if (convertCodeToStatus(order.getStatus()).equals("Completed")) {
                        orderViewHolder.txtOrderStatus.setTextColor(Color.parseColor("#EE1010"));
                    }
                    else {
                        orderViewHolder.txtOrderStatus.setTextColor(Color.parseColor("#080808"));
                    }

                    orderViewHolder.setItemClickListener(new ItemClickListener() {
                        @Override
                        public void onClick(View view, int position, boolean isLongClick) {
                            Intent orderDetail = new Intent(OrderStatus.this, OrderDetail.class);
                            Common.currentOrder = order;
                            orderDetail.putExtra("OrderId",adapter.getRef(position).getKey());
                            startActivity(orderDetail);
                        }
                    });
                }
            };
        } else {
            readQuery = orders.orderByChild("adminFilter").equalTo(filterSelected);
            adapter = new FirebaseRecyclerAdapter<Order, OrderViewHolder>(
                    Order.class,
                    R.layout.order_layout,
                    OrderViewHolder.class,
                    readQuery
            ) {
                @Override
                protected void populateViewHolder(OrderViewHolder orderViewHolder, Order order, int i) {
                    orderViewHolder.txtOrderId.setText(String.format("Order # ")+ adapter.getRef(i).getKey());
                    orderViewHolder.txtOrderStatus.setText(convertCodeToStatus(order.getStatus()));
                    orderViewHolder.txtOrderPrice.setText(order.getOrderPrice());
                    orderViewHolder.txtOrderType.setText(String.format("Order Type: ") + order.getOrderType());
                    orderViewHolder.txtOrderDate.setText(Common.getDate(Long.parseLong(adapter.getRef(i).getKey())));

                    if(!order.getScheduledTime().isEmpty()){
                        if (order.getStatus().equals("0")){
                            orderViewHolder.txtScheduledTime.setText("Scheduled for " + order.getScheduledTime());
                            orderViewHolder.txtScheduledTime.setVisibility(View.VISIBLE);
                        }
                        else
                            orderViewHolder.txtScheduledTime.setVisibility(View.INVISIBLE);
                    }
                    else
                        orderViewHolder.txtScheduledTime.setVisibility(View.INVISIBLE);

                    //Set text color of order status
                    if(convertCodeToStatus(order.getStatus()).equals("Placed")) {
                        orderViewHolder.txtOrderStatus.setTextColor(Color.parseColor("#29B438"));
                    }
                    else if ((convertCodeToStatus(order.getStatus()).equals("Delivering")) || (convertCodeToStatus(order.getStatus()).equals("Ready to Pickup"))) {
                        orderViewHolder.txtOrderStatus.setTextColor(Color.parseColor("#FF7800"));
                    }
                    else if (convertCodeToStatus(order.getStatus()).equals("Preparing")) {
                        orderViewHolder.txtOrderStatus.setTextColor(Color.parseColor("#EAA825"));
                    }
                    else if (convertCodeToStatus(order.getStatus()).equals("Completed")) {
                        orderViewHolder.txtOrderStatus.setTextColor(Color.parseColor("#EE1010"));
                    }
                    else {
                        orderViewHolder.txtOrderStatus.setTextColor(Color.parseColor("#080808"));
                    }

                    orderViewHolder.setItemClickListener(new ItemClickListener() {
                        @Override
                        public void onClick(View view, int position, boolean isLongClick) {
                            Intent orderDetail = new Intent(OrderStatus.this, OrderDetail.class);
                            Common.currentOrder = order;
                            orderDetail.putExtra("OrderId",adapter.getRef(position).getKey());
                            startActivity(orderDetail);
                        }
                    });

                }
            };
        }
        recyclerView.setAdapter(adapter);
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
            return "Cancelled by Customer";
        else
            return "Cancelled by Restaurant";
    }


    public void allFilterTapped(View view) {
        selectedFilter = "Order";
        allFilterButton.setBackgroundColor(Color.parseColor("#15BCFF"));
        activeFilterButton.setBackgroundColor(Color.parseColor("#125DDF"));
        completedFilterButton.setBackgroundColor(Color.parseColor("#125DDF"));
        cancelledFilterButton.setBackgroundColor(Color.parseColor("#125DDF"));

        loadOrders(selectedFilter);
    }

    public void activeFilterTapped(View view) {
        selectedFilter = "0";
        allFilterButton.setBackgroundColor(Color.parseColor("#125DDF"));
        activeFilterButton.setBackgroundColor(Color.parseColor("#15BCFF"));
        completedFilterButton.setBackgroundColor(Color.parseColor("#125DDF"));
        cancelledFilterButton.setBackgroundColor(Color.parseColor("#125DDF"));
        loadOrders(selectedFilter);
    }

    public void completedFilterTapped(View view) {
        selectedFilter = "4";
        allFilterButton.setBackgroundColor(Color.parseColor("#125DDF"));
        activeFilterButton.setBackgroundColor(Color.parseColor("#125DDF"));
        completedFilterButton.setBackgroundColor(Color.parseColor("#15BCFF"));
        cancelledFilterButton.setBackgroundColor(Color.parseColor("#125DDF"));
        loadOrders(selectedFilter);
    }

    public void cancelledFilterTapped(View view) {
        selectedFilter = "-1";
        allFilterButton.setBackgroundColor(Color.parseColor("#125DDF"));
        activeFilterButton.setBackgroundColor(Color.parseColor("#125DDF"));
        completedFilterButton.setBackgroundColor(Color.parseColor("#125DDF"));
        cancelledFilterButton.setBackgroundColor(Color.parseColor("#15BCFF"));
        loadOrders(selectedFilter);
    }

}