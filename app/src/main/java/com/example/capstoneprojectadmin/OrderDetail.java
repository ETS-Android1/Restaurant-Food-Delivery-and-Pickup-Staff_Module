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
import java.util.Calendar;
import java.util.TimeZone;

public class OrderDetail extends AppCompatActivity{

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
    String nextStatusCode = "";
    RecyclerView foodList;
    RecyclerView.LayoutManager layoutManager;
    Button updateOrderButton;
    Button cancelOrderButton;
    boolean scheduledOrderIsActive = true;

    Calendar timeNow;
    double currentHour;
    double currentMinute;

    FirebaseDatabase database;
    DatabaseReference orders;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detail);

        database = FirebaseDatabase.getInstance("https://capstoneproject-c2dbe-default-rtdb.asia-southeast1.firebasedatabase.app");
        orders = database.getReference("Order");



        orderId = findViewById(R.id.order_id);
        orderPhone = findViewById(R.id.order_phone);
        orderAddress = findViewById(R.id.order_address);
        orderTime = findViewById(R.id.order_time);
        orderTotal = findViewById(R.id.order_price);
        orderRequest = findViewById(R.id.order_request);
        orderStatus = findViewById(R.id.order_status);
        statusImage = findViewById(R.id.status_image);
        orderSchedule = findViewById(R.id.order_schedule);
        updateOrderButton = findViewById(R.id.updateOrderButton);
        cancelOrderButton = findViewById(R.id.cancelOrderButton);
        foodList = findViewById(R.id.foodList);
        foodList.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        foodList.setLayoutManager(layoutManager);

        if(getIntent() != null) {
            orderIdValue = getIntent().getStringExtra("OrderId");
        }


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


        updateOrderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                timeNow = Calendar.getInstance(TimeZone.getTimeZone("Asia/Kuala_Lumpur"));
                currentHour = timeNow.get(Calendar.HOUR_OF_DAY);
                currentMinute = timeNow.get(Calendar.MINUTE);

                if(!Common.currentOrder.getScheduledTime().isEmpty()) {
                    String scheduledTime = Common.currentOrder.getScheduledTime().substring(0, Common.currentOrder.getScheduledTime().length() - 3);
                    String[] scheduledHourMinute = scheduledTime.split(":");
                    int scheduledHour = Integer.parseInt(scheduledHourMinute[0]);
                    int scheduledMinute = Integer.parseInt(scheduledHourMinute[1]);

                    if(currentHour < scheduledHour || ((currentHour == scheduledHour) && (currentMinute < scheduledMinute))){
                        Toast.makeText(OrderDetail.this, "Order has not reached scheduled time", Toast.LENGTH_SHORT).show();
                        scheduledOrderIsActive = false;
                    } else {
                        scheduledOrderIsActive = true;
                    }
                }
                if (scheduledOrderIsActive == true) {
                    if(Common.currentOrder.getStatus().equals("-1") || Common.currentOrder.getStatus().equals("-2") ) {
                        Toast.makeText(OrderDetail.this, "Order is already cancelled", Toast.LENGTH_SHORT).show();
                    }else if(Common.currentOrder.getStatus().equals("4")) {
                        Toast.makeText(OrderDetail.this, "Order is already completed", Toast.LENGTH_SHORT).show();
                    }else{
                        String currentOrderType = Common.currentOrder.getOrderType();
                        String currentStatus = convertCodeToStatus(Common.currentOrder.getStatus());
                        if(currentStatus.equals("Placed"))
                            nextStatusCode = "1";
                        else if (currentStatus.equals("Preparing") && currentOrderType.equals("Delivery"))
                            nextStatusCode = "2";
                        else if (currentStatus.equals("Preparing") && currentOrderType.equals("Self-Collect"))
                            nextStatusCode = "3";
                        else if (currentStatus.equals("Delivering") || currentStatus.equals("Ready to Pickup"))
                            nextStatusCode = "4";

                        AlertDialog.Builder alertDialog = new AlertDialog.Builder(OrderDetail.this);
                        alertDialog.setTitle("Update Confirmation!");
                        alertDialog.setMessage("Are you sure to update this order from:\n\n" + currentStatus + " -> " +
                                convertCodeToStatus(nextStatusCode) + " ?");
                        alertDialog.setIcon(R.drawable.ic_baseline_warning_24);

                        alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                orders.child(orderIdValue).child("status").setValue(nextStatusCode);
                                if (nextStatusCode.equals("4")) {
                                    orders.child(orderIdValue).child("custIDStatusFilter").setValue(Common.currentOrder.getCustID() + "4");
                                    orders.child(orderIdValue).child("adminFilter").setValue("4");
                                }

                                Common.currentOrder.setStatus(nextStatusCode);

                                Toast.makeText(OrderDetail.this,"Order updated!", Toast.LENGTH_SHORT).show();

                                //refresh activity
                                finish();
                                overridePendingTransition(0, 0);
                                startActivity(getIntent());
                                overridePendingTransition(0, 0);
                            }
                        });
                        alertDialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        });
                        alertDialog.show();
                    }
                }
            }
        });

        cancelOrderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Display Toast message if order status is not "Placed"
                if(Common.currentOrder.getStatus().equals("-1") || Common.currentOrder.getStatus().equals("-2") ) {
                    Toast.makeText(OrderDetail.this, "Order is already cancelled", Toast.LENGTH_SHORT).show();
                }
                else if(Common.currentOrder.getStatus().equals("1")) {
                    Toast.makeText(OrderDetail.this, "Unable to cancel preparing orders", Toast.LENGTH_SHORT).show();
                }
                else if((Common.currentOrder.getStatus().equals("2")) || (Common.currentOrder.getStatus().equals("3"))){
                    Toast.makeText(OrderDetail.this, "Unable to cancel prepared orders", Toast.LENGTH_SHORT).show();
                }
                else if(Common.currentOrder.getStatus().equals("4")) {
                    Toast.makeText(OrderDetail.this, "Unable to cancel completed orders", Toast.LENGTH_SHORT).show();
                }
                else {
                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(OrderDetail.this);
                    alertDialog.setTitle("Cancel Confirmation!");
                    alertDialog.setMessage("Are you sure to cancel this order?\nThis action cannot be undone");
                    alertDialog.setIcon(R.drawable.ic_baseline_warning_24);

                    alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            orders.child(orderIdValue).child("status").setValue("-2");
                            orders.child(orderIdValue).child("custIDStatusFilter").setValue(Common.currentOrder.getCustID() + "-1");
                            orders.child(orderIdValue).child("adminFilter").setValue("-1");
                            Common.currentOrder.setStatus("-2");
                            Toast.makeText(OrderDetail.this,"Order cancelled", Toast.LENGTH_SHORT).show();

                            //refresh activity
                            finish();
                            overridePendingTransition(0, 0);
                            startActivity(getIntent());
                            overridePendingTransition(0, 0);
                        }
                    });
                    alertDialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    });
                    alertDialog.show();
                }
            }
        });
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
}