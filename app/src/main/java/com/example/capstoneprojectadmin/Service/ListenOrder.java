package com.example.capstoneprojectadmin.Service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.example.capstoneprojectadmin.Common.Common;
import com.example.capstoneprojectadmin.Model.Order;
import com.example.capstoneprojectadmin.OrderStatus;
import com.example.capstoneprojectadmin.R;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ListenOrder extends Service implements ChildEventListener {

    FirebaseDatabase database;
    DatabaseReference orders;


    public ListenOrder() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    public void onCreate() {
        super.onCreate();
        database = FirebaseDatabase.getInstance("https://capstoneproject-c2dbe-default-rtdb.asia-southeast1.firebasedatabase.app");
        orders = database.getReference("Order");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        orders.addChildEventListener(this);
        return super.onStartCommand(intent, flags, startId);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
        Order order = snapshot.getValue(Order.class);
        showNotification(snapshot.getKey(),order);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
        Order order = snapshot.getValue(Order.class);
        showNotification(snapshot.getKey(),order);

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void showNotification(String key, Order order) {
        Intent intent = new Intent(this, OrderStatus.class);
        intent.putExtra("custID", order.getCustID());
        PendingIntent contentIntent = PendingIntent.getActivity(this,0,intent,PendingIntent.FLAG_UPDATE_CURRENT);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel =
                    new NotificationChannel("foodStatus", "foodStatus", NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);

        }

        Notification.Builder notification = new Notification.Builder(this, "foodStatus");



        orders.addListenerForSingleValueEvent(new ValueEventListener() {
            String text = "";
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child(key).child("notification").getValue().toString().equals("false"))
                    if (order.getStatus().equals("-1")) {
                        text = "Order #" + key + " has been cancelled by customer";
                        notification.setAutoCancel(true)
                                .setDefaults(Notification.DEFAULT_ALL)
                                .setWhen(System.currentTimeMillis())
                                .setTicker("INTI Restaurant")
                                .setContentInfo("Your order was updated")
                                .setContentText(text)
                                .setContentIntent(contentIntent)
                                .setContentInfo("Info")
                                .setSmallIcon(R.drawable.init_logo);

                        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                        notificationManager.notify(1, notification.build());
                    } else if (order.getStatus().equals("0")) {
                        text = "New Order #" + key + " has been placed";

                        notification.setAutoCancel(true)
                                .setDefaults(Notification.DEFAULT_ALL)
                                .setWhen(System.currentTimeMillis())
                                .setTicker("INTI Restaurant")
                                .setContentInfo("Your order was updated")
                                .setContentText(text)
                                .setContentIntent(contentIntent)
                                .setContentInfo("Info")
                                .setSmallIcon(R.drawable.init_logo);

                        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                        notificationManager.notify(1, notification.build());
                    }

                orders.child(key).child("notification").setValue("true");

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

//        if(order.getNotification().equals("false")) {
//
//            if (order.getStatus().equals("-1")) {
//                text = "Order #" + key + " has been cancelled by customer";
//                notification.setAutoCancel(true)
//                        .setDefaults(Notification.DEFAULT_ALL)
//                        .setWhen(System.currentTimeMillis())
//                        .setTicker("INTI Restaurant")
//                        .setContentInfo("Your order was updated")
//                        .setContentText(text)
//                        .setContentIntent(contentIntent)
//                        .setContentInfo("Info")
//                        .setSmallIcon(R.drawable.init_logo);
//
//                NotificationManager notificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
//                notificationManager.notify(1, notification.build());
//            } else if (order.getStatus().equals("0")) {
//                text = "New Order #" + key + " has been placed";
//
//                notification.setAutoCancel(true)
//                        .setDefaults(Notification.DEFAULT_ALL)
//                        .setWhen(System.currentTimeMillis())
//                        .setTicker("INTI Restaurant")
//                        .setContentInfo("Your order was updated")
//                        .setContentText(text)
//                        .setContentIntent(contentIntent)
//                        .setContentInfo("Info")
//                        .setSmallIcon(R.drawable.init_logo);
//
//                NotificationManager notificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
//                notificationManager.notify(1, notification.build());
//            }
//
//            orders.child(key).child("notification").setValue("true");
//        }
    }

    @Override
    public void onChildRemoved(@NonNull DataSnapshot snapshot) {

    }

    @Override
    public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

    }

    @Override
    public void onCancelled(@NonNull DatabaseError error) {

    }
}