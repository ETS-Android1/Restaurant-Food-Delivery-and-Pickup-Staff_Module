package com.example.capstoneprojectadmin;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

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
                loginAdmin(usernameText.getText().toString(), passwordText.getText().toString());

            }
        });
    }

    private void loginAdmin(String username, String password) {
        ProgressDialog mDialog = new ProgressDialog(Login.this);
        mDialog.setMessage ("Please waiting...");
        mDialog.show();

        final String localUsername = username;
        final String localPassword = password;
        adminTable.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if(dataSnapshot.child(localUsername).exists()){
                    mDialog.dismiss();
                    Admin admin = dataSnapshot.child(localUsername).getValue(Admin.class);
                    admin.setUsername(localUsername);

                    if(admin.getPassword().equals(localPassword)){
                        Toast.makeText(Login.this, "Login successful!", Toast.LENGTH_SHORT).show();
                    }
                    else
                        Toast.makeText(Login.this, "Wrong password !", Toast.LENGTH_SHORT).show();
                }
                else
                    mDialog.dismiss();
                    Toast.makeText(Login.this, "Please login with Staff account!", Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onCancelled(@Nonnull DatabaseError error) {

            }
        });
    }
}