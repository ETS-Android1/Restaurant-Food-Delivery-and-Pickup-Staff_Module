package com.example.capstoneprojectadmin;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.capstoneprojectadmin.Common.Common;
import com.example.capstoneprojectadmin.Model.Food;
import com.example.capstoneprojectadmin.Model.FoodCategory;
import com.example.capstoneprojectadmin.ViewHolder.FoodViewHolder;
import com.example.capstoneprojectadmin.ViewHolder.MenuViewHolder;
import com.example.capstoneprojectadmin.databinding.ActivityFoodListBinding;
import com.example.capstoneprojectadmin.databinding.ActivityHomeBinding;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.FirebaseError;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.mancj.materialsearchbar.MaterialSearchBar;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import Interface.ItemClickListener;
import info.hoang8f.widget.FButton;

public class FoodList extends AppCompatActivity {
    

    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;

    RelativeLayout rootLayout;

    //Firebase
    FirebaseDatabase database;
    DatabaseReference foodList;
    FirebaseStorage storage;
    StorageReference storageReference;

    String foodCatID = "";

    FirebaseRecyclerAdapter<Food, FoodViewHolder> adapter;

    //Add New Food
    MaterialEditText edtName, edtDesc, edtPrice;
    FButton selectButton;
    ImageView imagePreview;

    Food newFood;

    Uri saveUri;


    //Search Functionality
    FirebaseRecyclerAdapter<Food, FoodViewHolder> searchAdapter;
    java.util.List<String> suggestList = new ArrayList<>();
    MaterialSearchBar materialSearchBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_list);

        //Firebase
        database = FirebaseDatabase.getInstance();
        foodList = database.getReference("Food");
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        //Init
        recyclerView = findViewById(R.id.recycler_food);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        rootLayout = (RelativeLayout) findViewById(R.id.rootLayout);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAddFoodDialog();
            }
        });


        if(getIntent() != null)
            foodCatID = getIntent().getStringExtra("Food Category ID");

        if(!foodCatID.isEmpty())
            loadFoodList(foodCatID);

        //Search
        materialSearchBar = (MaterialSearchBar)findViewById(R.id.search_bar);
        materialSearchBar.setHint("Search Food");
        loadSuggest(); //Write function to load suggest from firebase
        materialSearchBar.setLastSuggestions(suggestList);
        materialSearchBar.setCardViewElevation(10);

        materialSearchBar.addTextChangeListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                //When user type their text, we will change suggest list
                List<String> suggest = new ArrayList<String>();
                for(String search:suggestList){ //loop in suggest list
                    if(search.toLowerCase().contains(materialSearchBar.getText().toLowerCase()))
                        suggest.add(search);
                }
                materialSearchBar.setLastSuggestions(suggest);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        materialSearchBar.setOnSearchActionListener(new MaterialSearchBar.OnSearchActionListener() {
            @Override
            public void onSearchStateChanged(boolean enabled) {
                //When search bar is close
                //Restore the original adapter
                if(!enabled)
                    recyclerView.setAdapter(adapter);
            }

            @Override
            public void onSearchConfirmed(CharSequence text) {
                //When finish searching
                //Show result of search adapter
                startSearch(text);
            }

            @Override
            public void onButtonClicked(int buttonCode) {

            }
        });
    }

    private void showAddFoodDialog() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(FoodList.this);
        alertDialog.setTitle("Add new food");
        alertDialog.setMessage("Enter the food Name, description and upload the URL image");

        LayoutInflater inflater = this.getLayoutInflater();
        View add_menu_layout = inflater.inflate(R.layout.add_new_food_layout,null);
        alertDialog.setIcon(R.drawable.ic_baseline_restaurant_24);
        alertDialog.setView(add_menu_layout);

        edtName = add_menu_layout.findViewById(R.id.edtName);
        edtDesc = add_menu_layout.findViewById(R.id.edtDesc);
        edtPrice = add_menu_layout.findViewById(R.id.edtPrice);
        imagePreview = add_menu_layout.findViewById(R.id.image_preview);

        selectButton = add_menu_layout.findViewById(R.id.selectButton);

        selectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseImage(); //Let user select image from gallery and save uri of this image
            }
        });

        //Set button
        alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener(){

            @Override
            public void onClick(DialogInterface dialog, int i) {
                dialog.dismiss();
                if(saveUri!=null){
                    if (edtName.getText().toString().isEmpty() || edtDesc.getText().toString().isEmpty() ||
                        edtPrice.getText().toString().isEmpty()) {
                        Toast.makeText(FoodList.this, "All fields must not be empty!", Toast.LENGTH_SHORT).show();
                        saveUri = null;
                    } else {
                        ProgressDialog mDialog = new ProgressDialog(FoodList.this);
                        mDialog.setMessage("Uploading...");
                        mDialog.show();

                        String imageName = UUID.randomUUID().toString();
                        StorageReference imageFolder = storageReference.child("images/"+imageName);
                        imageFolder.putFile(saveUri)
                                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                        mDialog.dismiss();;
                                        Toast.makeText(FoodList.this, "Uploaded !", Toast.LENGTH_SHORT).show();
                                        imageFolder.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                            @Override
                                            public void onSuccess(Uri uri) {
                                                //set value for newCategory if image upload and we can get download link
                                                newFood = new Food();
                                                newFood.setFoodName(edtName.getText().toString());
                                                newFood.setFoodDesc(edtDesc.getText().toString());
                                                newFood.setFoodPrice(edtPrice.getText().toString());
                                                newFood.setFoodCatID(foodCatID);
                                                newFood.setFoodImageURL(uri.toString());
                                                foodList.push().setValue(newFood);
                                                Snackbar.make(rootLayout, "New food " + newFood.getFoodName() + " was added", Snackbar.LENGTH_SHORT).show();
                                                saveUri = null;
                                            }
                                        });
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        mDialog.dismiss();
                                        Toast.makeText(FoodList.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
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
                } else {
                    Toast.makeText(FoodList.this,"No image was selected!", Toast.LENGTH_SHORT).show();
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

    private void chooseImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,"Select Picture"), Common.PICK_IMAGE_REQUEST);
    }


    private void startSearch(CharSequence text) {
        searchAdapter = new FirebaseRecyclerAdapter<Food, FoodViewHolder>(
                Food.class,
                R.layout.food_item,
                FoodViewHolder.class,
                foodList.orderByChild("foodName").equalTo(text.toString()) //compare name
        ) {
            @Override
            protected void populateViewHolder(FoodViewHolder foodViewHolder, Food food, int position) {
                foodViewHolder.food_name.setText(food.getFoodName());
                Glide.with(getBaseContext()).load(food.getFoodImageURL()).into(foodViewHolder.food_image);
                foodViewHolder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {
                        //Start new Activity
                        Intent foodDetail = new Intent(FoodList.this, FoodDetail.class);
                        //Send food ID to new Activity
                        foodDetail.putExtra("Food ID", searchAdapter.getRef(position).getKey());
                        startActivity(foodDetail);
                    }
                });
            }
        };

        recyclerView.setAdapter(searchAdapter);
    }

    private void loadSuggest() {
        foodList.orderByChild("foodCatID").equalTo(foodCatID)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                            Food item = postSnapshot.getValue(Food.class);
                            suggestList.add(item.getFoodName()); //Add Name of food to suggest list
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void loadFoodList(String foodCatID) {
        adapter = new FirebaseRecyclerAdapter<Food, FoodViewHolder>(Food.class, R.layout.food_item, FoodViewHolder.class,
                foodList.orderByChild("foodCatID").equalTo(foodCatID)) { //Similar to SQL statement, SELECT * FROM Food WHERE foodCatID = xxx

            @Override
            protected void populateViewHolder(FoodViewHolder foodViewHolder, Food food, int i) {
                foodViewHolder.food_name.setText(food.getFoodName());
                Glide.with(getBaseContext())
                        .load(food.getFoodImageURL())
                        .into(foodViewHolder.food_image);
                Food selectedFood = food;
                foodViewHolder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {
                        //Start new Activity
                        Intent foodDetail = new Intent(FoodList.this, FoodDetail.class);
                        //Send food ID to new Activity
                        foodDetail.putExtra("Food ID", adapter.getRef(position).getKey());
                        startActivity(foodDetail);
                    }
                });
            }
        };

        //Set adapter
        adapter.notifyDataSetChanged();
        recyclerView.setAdapter(adapter);
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

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        if(item.getTitle().equals(Common.UPDATE)){
            showUpdateFoodDialog(adapter.getRef(item.getOrder()).getKey(),adapter.getItem(item.getOrder()));
        }
        else if(item.getTitle().equals(Common.DELETE)){
            deleteFood(adapter.getRef(item.getOrder()).getKey());
        }

        return super.onContextItemSelected(item);
    }

    private void deleteFood(String key) {
        foodList.child(key).removeValue();
    }


    private void showUpdateFoodDialog(String key, Food item) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(FoodList.this);
        alertDialog.setTitle("Edit food");
        alertDialog.setMessage("Enter the food Name, description and upload the URL image");

        LayoutInflater inflater = this.getLayoutInflater();
        View add_menu_layout = inflater.inflate(R.layout.add_new_food_layout,null);

        edtName = add_menu_layout.findViewById(R.id.edtName);
        edtDesc = add_menu_layout.findViewById(R.id.edtDesc);
        edtPrice = add_menu_layout.findViewById(R.id.edtPrice);
        imagePreview = add_menu_layout.findViewById(R.id.image_preview);

        //Set default value for view
        edtName.setText(item.getFoodName());
        edtDesc.setText(item.getFoodDesc());
        edtPrice.setText(item.getFoodPrice());
        Glide.with(getBaseContext()).load(item.getFoodImageURL()).into(imagePreview);

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
        alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int i) {
                dialog.dismiss();
                if (saveUri != null) {
                    if (edtName.getText().toString().isEmpty() || edtDesc.getText().toString().isEmpty() ||
                            edtPrice.getText().toString().isEmpty()) {
                        Toast.makeText(FoodList.this, "All fields must not be empty!", Toast.LENGTH_SHORT).show();
                        saveUri = null;
                    } else {
                        ProgressDialog mDialog = new ProgressDialog(FoodList.this);
                        mDialog.setMessage("Uploading...");
                        mDialog.show();

                        String imageName = UUID.randomUUID().toString();
                        StorageReference imageFolder = storageReference.child("images/"+imageName);
                        imageFolder.putFile(saveUri)
                                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                        mDialog.dismiss();;
                                        Toast.makeText(FoodList.this, "Uploaded !", Toast.LENGTH_SHORT).show();
                                        imageFolder.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                            @Override
                                            public void onSuccess(Uri uri) {
                                                double foodPrice = Double.parseDouble(edtPrice.getText().toString());

                                                //set value for newCategory if image upload and we can get download link
                                                item.setFoodName(edtName.getText().toString());
                                                item.setFoodDesc(edtDesc.getText().toString());
                                                item.setFoodPrice(String.format("%.2f",foodPrice));
                                                //newFood.setFoodCatID(foodCatID);
                                                item.setFoodImageURL(uri.toString());
                                                foodList.child(key).setValue(item);
                                                //foodList.push().setValue(newFood);
                                                Snackbar.make(rootLayout, "Food updated successfully!", Snackbar.LENGTH_SHORT).show();
                                                saveUri = null;
                                            }
                                        });
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        mDialog.dismiss();
                                        Toast.makeText(FoodList.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
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

                } else {
                    if(edtName.getText().toString().trim().equals(item.getFoodName()) &&
                        edtDesc.getText().toString().trim().equals(item.getFoodDesc()) &&
                         edtPrice.getText().toString().trim().equals(item.getFoodPrice())){
                        Toast.makeText(FoodList.this,"No changes were made", Toast.LENGTH_SHORT).show();

                    } else if (edtName.getText().toString().isEmpty() || edtDesc.getText().toString().isEmpty() ||
                            edtPrice.getText().toString().isEmpty()) {
                        Toast.makeText(FoodList.this, "All fields must not be empty!", Toast.LENGTH_SHORT).show();
                    } else {
                        double foodPrice = Double.parseDouble(edtPrice.getText().toString());

                        item.setFoodName(edtName.getText().toString());
                        item.setFoodDesc(edtDesc.getText().toString());
                        item.setFoodPrice(String.format("%.2f",foodPrice));
                        foodList.child(key).setValue(item);
                        Snackbar.make(rootLayout, "Food updated successfully!", Snackbar.LENGTH_SHORT).show();
                    }
                }
            }
        }).setNegativeButton("NO", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int i) {
                dialog.dismiss();
            }
        });


        alertDialog.show();
    }

    private void changeImage(Food item) {
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
                            Toast.makeText(FoodList.this, "Uploaded !", Toast.LENGTH_SHORT).show();
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
                            Toast.makeText(FoodList.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {

                            double progress = (100.0 * snapshot.getBytesTransferred()/snapshot.getTotalByteCount());
                            mDialog.setMessage("Uploaded " + progress+"%");
                        }
                    });
        }
    }
}