package com.example.capstoneprojectadmin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.capstoneprojectadmin.Common.Common;
import com.example.capstoneprojectadmin.Model.Restaurant;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;


public class AdminManagement extends AppCompatActivity {

    Button updateRestaurantButton;
    Button addNewStaffButton;
    Button resetStaffPasswordButton;
    Button deleteStaffButton;
    MaterialEditText restaurantNameTxt;
    MaterialEditText restaurantSloganTxt;
    Spinner openingHourSpinner;
    Spinner openingMinuteSpinner;
    Spinner closingHourSpinner;
    Spinner closingMinuteSpinner;
    Spinner lastOrderHourSpinner;
    Spinner lastOrderMinuteSpinner;
    TextView openingAmPmLabel;
    TextView closingAmPmLabel;
    TextView lastOrderAmPmLabel;


    FirebaseDatabase database;
    DatabaseReference restaurantTable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_management);

        database = FirebaseDatabase.getInstance("https://capstoneproject-c2dbe-default-rtdb.asia-southeast1.firebasedatabase.app");
        restaurantTable = database.getReference("Restaurant");

        updateRestaurantButton = findViewById(R.id.updateRestaurantButton);
        addNewStaffButton = findViewById(R.id.addNewStaffButton);
        resetStaffPasswordButton = findViewById(R.id.resetStaffPasswordButton);
        deleteStaffButton = findViewById(R.id.deleteStaffButton);

        updateRestaurantButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(AdminManagement.this);
                alertDialog.setTitle("Update Restaurant Info");
                alertDialog.setMessage("Edit your restaurant information");

                LayoutInflater inflater = getLayoutInflater();
                View updateRestaurantLayout = inflater.inflate(R.layout.update_restaurant_alert_dialog,null);
                alertDialog.setIcon(R.drawable.ic_baseline_restaurant_24);
                alertDialog.setView(updateRestaurantLayout);

                restaurantNameTxt = updateRestaurantLayout.findViewById(R.id.restaurantNameTxt);
                restaurantSloganTxt = updateRestaurantLayout.findViewById(R.id.restaurantSloganTxt);
                restaurantNameTxt.setText(Common.currentRestaurant.getRestName());
                restaurantSloganTxt.setText(Common.currentRestaurant.getRestSlogan());

                openingHourSpinner = updateRestaurantLayout.findViewById(R.id.openingHourSpinner);
                openingMinuteSpinner = updateRestaurantLayout.findViewById(R.id.openingMinuteSpinner);
                closingHourSpinner = updateRestaurantLayout.findViewById(R.id.closingHourSpinner);
                closingMinuteSpinner = updateRestaurantLayout.findViewById(R.id.closingMinuteSpinner);
                lastOrderHourSpinner = updateRestaurantLayout.findViewById(R.id.lastOrderHourSpinner);
                lastOrderMinuteSpinner = updateRestaurantLayout.findViewById(R.id.lastOrderMinuteSpinner);
                openingAmPmLabel = updateRestaurantLayout.findViewById(R.id.openingAmPmLabel);
                closingAmPmLabel = updateRestaurantLayout.findViewById(R.id.closingAmPmLabel);
                lastOrderAmPmLabel = updateRestaurantLayout.findViewById(R.id.lastOrderAmPmLabel);

                String restOpening = Common.currentRestaurant.getRestOpening().
                                     substring(0, Common.currentRestaurant.getRestOpening().length() - 3);
                String restClosing = Common.currentRestaurant.getRestClosing().
                                     substring(0, Common.currentRestaurant.getRestClosing().length() - 3);
                String restLastOrder = Common.currentRestaurant.getRestLastOrderTime().
                                       substring(0, Common.currentRestaurant.getRestLastOrderTime().length() - 3);

                String[] openingHourMinute = restOpening.split(":");
                String openingHour = openingHourMinute[0];
                String openingMinute = openingHourMinute[1];

                String[] closingHourMinute = restClosing.split(":");
                String closingHour = closingHourMinute[0];
                String closingMinute = closingHourMinute[1];

                String[] lastOrderHourMinute = restLastOrder.split(":");
                String lastOrderHour = lastOrderHourMinute[0];
                String lastOrderMinute = lastOrderHourMinute[1];


                //Opening hour spinner
                List<String> hours = new ArrayList<>();
                for(int i = 0; i < 24; i++){
                    String hourString = String.valueOf(i);
                    if(hourString.length() == 1)
                        hourString = 0 + hourString;
                    hours.add(hourString);
                }

                List<String> minutes = new ArrayList<>();
                minutes.add("00");
                minutes.add("30");

                ArrayAdapter<String> hourSpinnerAdapter = new ArrayAdapter<>(AdminManagement.this,
                        android.R.layout.simple_list_item_1, hours);
                ArrayAdapter<String> minuteSpinnerAdapter = new ArrayAdapter<>(AdminManagement.this,
                        android.R.layout.simple_list_item_1, minutes);

                //Set default selected value
                openingHourSpinner.setAdapter(hourSpinnerAdapter);
                openingMinuteSpinner.setAdapter(minuteSpinnerAdapter);
                int openingHourSelection = hourSpinnerAdapter.getPosition(openingHour);
                int openingMinuteSelection = minuteSpinnerAdapter.getPosition(openingMinute);
                openingHourSpinner.setSelection(openingHourSelection);
                openingMinuteSpinner.setSelection(openingMinuteSelection);
                openingAmPmLabel.setText(Integer.parseInt(openingHour) >= 12 ? "PM" : "AM");


                closingHourSpinner.setAdapter(hourSpinnerAdapter);
                closingMinuteSpinner.setAdapter(minuteSpinnerAdapter);
                int closingHourSelection = hourSpinnerAdapter.getPosition(closingHour);
                int closingMinuteSelection = minuteSpinnerAdapter.getPosition(closingMinute);
                closingHourSpinner.setSelection(closingHourSelection);
                closingMinuteSpinner.setSelection(closingMinuteSelection);
                closingAmPmLabel.setText(Integer.parseInt(closingHour) >= 12 ? "PM" : "AM");

                lastOrderHourSpinner.setAdapter(hourSpinnerAdapter);
                lastOrderMinuteSpinner.setAdapter(minuteSpinnerAdapter);
                int lastOrderHourSelection = hourSpinnerAdapter.getPosition(lastOrderHour);
                int lastOrderMinuteSelection = minuteSpinnerAdapter.getPosition(lastOrderMinute);
                lastOrderHourSpinner.setSelection(lastOrderHourSelection);
                lastOrderMinuteSpinner.setSelection(lastOrderMinuteSelection);
                lastOrderAmPmLabel.setText(Integer.parseInt(lastOrderHour) >= 12 ? "PM" : "AM");

                openingHourSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                        String selectedHour = adapterView.getSelectedItem().toString();
                        openingAmPmLabel.setText(Integer.parseInt(selectedHour) >= 12 ? "PM" : "AM");
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> adapterView) {

                    }
                });

                closingHourSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                        String selectedHour = adapterView.getSelectedItem().toString();
                        closingAmPmLabel.setText(Integer.parseInt(selectedHour) >= 12 ? "PM" : "AM");
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> adapterView) {

                    }
                });

                lastOrderHourSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                        String selectedHour = adapterView.getSelectedItem().toString();
                        lastOrderAmPmLabel.setText(Integer.parseInt(selectedHour) >= 12 ? "PM" : "AM");
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> adapterView) {

                    }
                });


                alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        int selectedOpeningHour = Integer.parseInt(openingHourSpinner.getSelectedItem().toString());
                        int selectedOpeningMinute = Integer.parseInt(openingMinuteSpinner.getSelectedItem().toString());
                        int selectedClosingHour = Integer.parseInt(closingHourSpinner.getSelectedItem().toString());
                        int selectedClosingMinute = Integer.parseInt(closingMinuteSpinner.getSelectedItem().toString());
                        int selectedLastOrderHour = Integer.parseInt(lastOrderHourSpinner.getSelectedItem().toString());
                        int selectedLastOrderMinute = Integer.parseInt(lastOrderMinuteSpinner.getSelectedItem().toString());

                        if(restaurantNameTxt.getText().toString().isEmpty() || restaurantSloganTxt.getText().toString().isEmpty()){
                            Toast.makeText(AdminManagement.this, "All fields must not be empty!", Toast.LENGTH_SHORT).show();
                        } else if ((selectedOpeningHour > selectedClosingHour) || ((selectedOpeningHour >= selectedClosingHour) && (selectedOpeningMinute >= selectedClosingMinute))){
                            Toast.makeText(AdminManagement.this, "Opening Time must be earlier than Closing Time", Toast.LENGTH_SHORT).show();
                        } else if ((selectedOpeningHour > selectedLastOrderHour) || ((selectedOpeningHour >= selectedLastOrderHour) && (selectedOpeningMinute >= selectedLastOrderMinute))){
                            Toast.makeText(AdminManagement.this, "Opening Time must be earlier than Last Order Time", Toast.LENGTH_SHORT).show();
                        } else if ((selectedLastOrderHour > selectedClosingHour) || ((selectedLastOrderHour >= selectedClosingHour) && (selectedLastOrderMinute >= selectedClosingMinute))) {
                            Toast.makeText(AdminManagement.this, "Last Order Time must be earlier than Closing Time", Toast.LENGTH_SHORT).show();
                        } else {
                            restaurantTable.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    restaurantTable.child("restName").setValue(restaurantNameTxt.getText().toString());
                                    restaurantTable.child("restSlogan").setValue(restaurantSloganTxt.getText().toString());
                                    restaurantTable.child("restOpening").setValue(openingHourSpinner.getSelectedItem().toString() + ":" +
                                                                                  openingMinuteSpinner.getSelectedItem().toString() + " " +
                                                                                  openingAmPmLabel.getText().toString());
                                    restaurantTable.child("restClosing").setValue(closingHourSpinner.getSelectedItem().toString() + ":" +
                                                                                  closingMinuteSpinner.getSelectedItem().toString() + " " +
                                                                                  closingAmPmLabel.getText().toString());
                                    restaurantTable.child("restLastOrderTime").setValue(lastOrderHourSpinner.getSelectedItem().toString() + ":" +
                                                                                  lastOrderMinuteSpinner.getSelectedItem().toString() + " " +
                                                                                  lastOrderAmPmLabel.getText().toString());

                                    Common.currentRestaurant.setRestName(restaurantNameTxt.getText().toString());
                                    Common.currentRestaurant.setRestSlogan(restaurantSloganTxt.getText().toString());
                                    Common.currentRestaurant.setRestOpening(openingHourSpinner.getSelectedItem().toString() + ":" +
                                                                            openingMinuteSpinner.getSelectedItem().toString() + " " +
                                                                            openingAmPmLabel.getText().toString());
                                    Common.currentRestaurant.setRestClosing(closingHourSpinner.getSelectedItem().toString() + ":" +
                                                                            closingMinuteSpinner.getSelectedItem().toString() + " " +
                                                                            closingAmPmLabel.getText().toString());
                                    Common.currentRestaurant.setRestLastOrderTime(lastOrderHourSpinner.getSelectedItem().toString() + ":" +
                                                                                  lastOrderMinuteSpinner.getSelectedItem().toString() + " " +
                                                                                  lastOrderAmPmLabel.getText().toString());
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                        }
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
        });

        addNewStaffButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        resetStaffPasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        deleteStaffButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }
}