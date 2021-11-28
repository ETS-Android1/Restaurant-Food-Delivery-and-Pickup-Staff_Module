package com.example.capstoneprojectadmin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.Toast;

import com.example.capstoneprojectadmin.Common.Common;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rengwuxian.materialedittext.MaterialEditText;

import com.example.capstoneprojectadmin.Model.Admin;

import javax.annotation.Nonnull;

public class Login extends AppCompatActivity {



    MaterialEditText usernameText, passwordText;
    Button loginButton;

    FirebaseDatabase  database;
    DatabaseReference adminTable;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        requestWindowFeature(Window.FEATURE_NO_TITLE);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        usernameText = findViewById(R.id.usernameText);
        passwordText = findViewById(R.id.passwordText);
        loginButton = findViewById(R.id.loginButton);

        database = FirebaseDatabase.getInstance("https://capstoneproject-c2dbe-default-rtdb.asia-southeast1.firebasedatabase.app/");
        adminTable = database.getReference("Admin");


        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(usernameText.getText().toString().isEmpty() || passwordText.getText().toString().isEmpty()) {
                    Toast.makeText(Login.this, "Both fields must not be empty!", Toast.LENGTH_SHORT).show();
                } else {
                    loginAdmin(usernameText.getText().toString(), passwordText.getText().toString());
                }
            }
        });
    }

    private void loginAdmin(String username, String password) {

        final String localUsername = username;
        final String localPassword = password;
        adminTable.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(dataSnapshot.child(username).exists()){

                    Admin admin = dataSnapshot.child(username).getValue(Admin.class);
                    admin.setUsername(localUsername);
                    admin.setAdminPassword(dataSnapshot.child(username).child("adminPassword").getValue().toString());
                    admin.setName(dataSnapshot.child(username).child("adminName").getValue().toString());
                    admin.setAdminTelNo(dataSnapshot.child(username).child("adminTelNo").getValue().toString());
                    admin.setSuperAdmin(dataSnapshot.child(username).child("superAdmin").getValue().toString());

                    if(admin.getAdminPassword().equals(password)){
                        Toast.makeText(Login.this, "Login successful!", Toast.LENGTH_SHORT).show();
                        Intent home = new Intent(Login.this,Home.class);
                        Common.currentAdmin = admin;
                        startActivity(home);
                        finish();
                    }
                    else
                        Toast.makeText(Login.this, "Wrong password !", Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(Login.this, "Please login with Staff account!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@Nonnull DatabaseError error) {

            }
        });
    }
}