package com.example.capstoneprojectadmin;

import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Menu;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.example.capstoneprojectadmin.Common.Common;
import com.example.capstoneprojectadmin.Model.FoodCategory;
import com.example.capstoneprojectadmin.Service.ListenOrder;
import com.example.capstoneprojectadmin.ViewHolder.MenuViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.capstoneprojectadmin.databinding.ActivityHomeBinding;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.squareup.picasso.Picasso;

import java.util.UUID;

import Interface.ItemClickListener;
import info.hoang8f.widget.FButton;

public class Home extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private ActivityHomeBinding binding;

    TextView txtFullName;

    //Firebase
    FirebaseDatabase database;
    DatabaseReference foodCategory;
    FirebaseStorage storage;
    StorageReference storageReference;
    FirebaseRecyclerAdapter<FoodCategory, MenuViewHolder> adapter;

    //View
    RecyclerView recyclerMenu;
    RecyclerView.LayoutManager layoutManager;

    //Add New Menu Layout
    MaterialEditText edtName;
    MaterialEditText edtURL;
    ImageView imagePreview;
    FButton selectButton;

    FoodCategory newCategory;

    Uri saveUri;

    DrawerLayout drawer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //Init Firebase
        database = FirebaseDatabase.getInstance();
        foodCategory = database.getReference("FoodCategory");
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference("images/");

        //Set the visibility of Admin Management Menu
        NavigationView nv = (NavigationView) findViewById(R.id.nav_view);
        Menu menu = nv.getMenu();
        MenuItem adminMng = menu.findItem(R.id.admin_mng);

        if (Common.currentAdmin.getSuperAdmin().toString().equals("true"))
            adminMng.setVisible(true);


        setSupportActionBar(binding.appBarHome.toolbar);
        binding.appBarHome.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialog();
            }
        });
        drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow)
                .setOpenableLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_home);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();

                if(id == R.id.nav_menu){

                }
                else if(id == R.id.nav_orders){
                    Intent orderIntent = new Intent(Home.this,OrderStatus.class);
                    startActivity(orderIntent);
                }
                else if(id == R.id.admin_mng){
                    Intent adminManagement = new Intent(Home.this,AdminManagement.class);
                    startActivity(adminManagement);
                }
                else if(id == R.id.nav_profile){
                    Intent profile = new Intent(Home.this, Profile.class);
                    startActivity(profile);
                }
                else if(id == R.id.nav_ratings) {
                    Intent ratingList = new Intent(Home.this, RatingList.class);
                    startActivity(ratingList);
                }
                else if(id == R.id.nav_log_out){
                    //Logout
                    Intent logIn = new Intent(Home.this,Login.class);
                    logIn.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(logIn);
                }
                return false;
            }
        });


        //Set Name for user
        View headerView = navigationView.getHeaderView(0);
        txtFullName = headerView.findViewById(R.id.txtFullName);
        txtFullName.setText(Common.currentAdmin.getName());

        //Load menu
        recyclerMenu = findViewById(R.id.recycler_menu);
        recyclerMenu.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerMenu.setLayoutManager(layoutManager);

        loadMenu();

        //Register Service
        Intent service = new Intent(Home.this, ListenOrder.class);
        startService(service);
    }

    private void showDialog() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(Home.this);
        alertDialog.setTitle("Add new Category");
        alertDialog.setMessage("Enter the category Name and URL image");

        LayoutInflater inflater = this.getLayoutInflater();
        View add_menu_layout = inflater.inflate(R.layout.add_new_menu_layout,null);

        edtName = add_menu_layout.findViewById(R.id.edtName);
        imagePreview = add_menu_layout.findViewById(R.id.image_preview);
        selectButton = add_menu_layout.findViewById(R.id.selectButton);

        selectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseImage(); //Let user select image from gallery and save uri of this image
            }
        });

        alertDialog.setView(add_menu_layout);
        alertDialog.setIcon(R.drawable.ic_baseline_restaurant_24);

        //Set button
        alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener(){

            @Override
            public void onClick(DialogInterface dialog, int i) {
                dialog.dismiss();
                if(saveUri!=null){
                    if (edtName.getText().toString().isEmpty()) {
                        Toast.makeText(Home.this, "Category name must not be empty!", Toast.LENGTH_SHORT).show();
                        saveUri = null;
                    } else {
                        ProgressDialog mDialog = new ProgressDialog(Home.this);
                        mDialog.setMessage("Uploading...");
                        mDialog.show();

                        String imageName = UUID.randomUUID().toString();
                        StorageReference imageFolder = storageReference.child("images/"+imageName);
                        imageFolder.putFile(saveUri)
                                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                        mDialog.dismiss();;
                                        imageFolder.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                            @Override
                                            public void onSuccess(Uri uri) {
                                                if (edtName.getText().toString().isEmpty()){
                                                    Toast.makeText(Home.this, "Category name must not be empty!", Toast.LENGTH_SHORT).show();
                                                    saveUri = null;
                                                } else {
                                                    //set value for newCategory if image upload and we can get download link
                                                    newCategory = new FoodCategory(edtName.getText().toString(),uri.toString());
                                                    foodCategory.push().setValue(newCategory);
                                                    Snackbar.make(drawer, "New category " + newCategory.getFoodCatName() + " was added", Snackbar.LENGTH_SHORT).show();
                                                    saveUri = null;
                                                }
                                            }
                                        });
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        mDialog.dismiss();
                                        Toast.makeText(Home.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                })
                                .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {

                                        double progress = (100 * snapshot.getBytesTransferred()/snapshot.getTotalByteCount());

                                        mDialog.setMessage("Uploaded " + progress+"%");
                                    }
                                });
                    }
                } else {
                    Toast.makeText(Home.this,"No image was selected!", Toast.LENGTH_SHORT).show();
                }

            }
        });
        alertDialog.setNegativeButton("NO", new DialogInterface.OnClickListener(){

            @Override
            public void onClick(DialogInterface dialog, int i) {
                dialog.dismiss();
            }
        });


        alertDialog.show();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == Common.PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null){
            saveUri = data.getData();
            imagePreview.setImageURI(saveUri);
        }
    }

    private void chooseImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,"Select Picture"),Common.PICK_IMAGE_REQUEST);
    }

    private void loadMenu() {
        adapter = new FirebaseRecyclerAdapter<FoodCategory, MenuViewHolder>(FoodCategory.class, R.layout.menu_item, MenuViewHolder.class,foodCategory) {
            @Override
            protected void populateViewHolder(MenuViewHolder menuViewHolder, FoodCategory foodCategory, int i) {
                menuViewHolder.txtMenuName.setText(foodCategory.getFoodCatName());
                Glide.with(getBaseContext()).load(foodCategory.getFoodCatImageURL()).into(menuViewHolder.imageView);
                FoodCategory clickItem = foodCategory;
                menuViewHolder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {
                        //send category id and start new activity
                        Intent foodList = new Intent (Home.this,FoodList.class);
                        foodList.putExtra("Food Category ID",adapter.getRef(position).getKey());
                        startActivity(foodList);
                    }
                });
            }
        };

        //Refresh data if have data changed
        adapter.notifyDataSetChanged();
        recyclerMenu.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_home);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    //Update / Delete


    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {

        if(item.getTitle().equals(Common.UPDATE)){
            showUpdateDialog(adapter.getRef(item.getOrder()).getKey(),adapter.getItem(item.getOrder()));
        }
        else if(item.getTitle().equals(Common.DELETE)){
            deleteCategory(adapter.getRef(item.getOrder()).getKey());
        }



        return super.onContextItemSelected(item);
    }

    private void deleteCategory(String key) {
        foodCategory.child(key).removeValue();
        Toast.makeText(this,"Item deleted!",Toast.LENGTH_SHORT).show();
    }

    private void showUpdateDialog(String key, FoodCategory item) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(Home.this);
        alertDialog.setTitle("Update Category");
        alertDialog.setMessage("Enter the category Name and URL image");

        LayoutInflater inflater = this.getLayoutInflater();
        View add_menu_layout = inflater.inflate(R.layout.add_new_menu_layout,null);

        edtName = add_menu_layout.findViewById(R.id.edtName);
        selectButton = add_menu_layout.findViewById(R.id.selectButton);
        imagePreview = add_menu_layout.findViewById(R.id.image_preview);


        //Set default name
        edtName.setText(item.getFoodCatName());

        //set default image
        Glide.with(getBaseContext()).load(item.getFoodCatImageURL()).into(imagePreview);

        selectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseImage(); //Let user select image from gallery and save uri of this image
            }
        });


        alertDialog.setView(add_menu_layout);
        alertDialog.setIcon(R.drawable.ic_baseline_restaurant_24);

        //Set button
        alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener(){

            @Override
            public void onClick(DialogInterface dialog, int i) {
                dialog.dismiss();
                if(saveUri!=null){
                    if (edtName.getText().toString().isEmpty()) {
                        Toast.makeText(Home.this, "Category name must not be empty!", Toast.LENGTH_SHORT).show();
                    } else {
                        ProgressDialog mDialog = new ProgressDialog(Home.this);
                        mDialog.setMessage("Uploading...");
                        mDialog.show();

                        String imageName = UUID.randomUUID().toString();
                        StorageReference imageFolder = storageReference.child("images/"+imageName);
                        imageFolder.putFile(saveUri)
                                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                        mDialog.dismiss();;
                                        imageFolder.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                            @Override
                                            public void onSuccess(Uri uri) {
                                                item.setFoodCatName(edtName.getText().toString());
                                                item.setFoodImageURL(uri.toString());
                                                foodCategory.child(key).setValue(item);
                                                Snackbar.make(drawer, "Category updated successfully!", Snackbar.LENGTH_SHORT).show();
                                                saveUri = null;
                                            }
                                        });
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        mDialog.dismiss();
                                        Toast.makeText(Home.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                })
                                .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {

                                        double progress = (100 * snapshot.getBytesTransferred()/snapshot.getTotalByteCount());

                                        mDialog.setMessage("Uploaded " + progress+"%");
                                    }
                                });
                    }
                } else {
                    if(edtName.getText().toString().trim().equals(item.getFoodCatName())){
                        Toast.makeText(Home.this,"No changes were made", Toast.LENGTH_SHORT).show();
                    } else if (edtName.getText().toString().isEmpty()){
                        Toast.makeText(Home.this,"Category name must not be empty!", Toast.LENGTH_SHORT).show();
                    } else {
                        item.setFoodCatName(edtName.getText().toString());
                        foodCategory.child(key).setValue(item);
                        Snackbar.make(drawer, "Category updated successfully!", Snackbar.LENGTH_SHORT).show();
                    }
                }


            }
        });
        alertDialog.setNegativeButton("NO", new DialogInterface.OnClickListener(){

            @Override
            public void onClick(DialogInterface dialog, int i) {
                dialog.dismiss();
            }
        });


        alertDialog.show();
    }
    private void changeImage(FoodCategory item) {
        if(saveUri!=null){
            ProgressDialog mDialog = new ProgressDialog(this);
            mDialog.setMessage("Uploading...");
            mDialog.show();

            String imageName = UUID.randomUUID().toString();
            StorageReference imageFolder = storageReference.child("images/"+imageName);
            imageFolder.putFile(saveUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            mDialog.dismiss();;
                            Toast.makeText(Home.this, "Uploaded !", Toast.LENGTH_SHORT).show();
                            imageFolder.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    //set value for newCategory if image upload and we can get download link
                                    item.setFoodImageURL(uri.toString());

                                }
                            });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            mDialog.dismiss();
                            Toast.makeText(Home.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {

                            long progress = (100 * snapshot.getBytesTransferred()/snapshot.getTotalByteCount());
                            mDialog.setMessage("Uploaded " + progress+"%");
                        }
                    });
        }
    }

    //Refresh customer name after applying changes in Profile page
    @Override
    protected void onRestart() {
        super.onRestart();
        txtFullName.setText(Common.currentAdmin.getName());
    }
}

