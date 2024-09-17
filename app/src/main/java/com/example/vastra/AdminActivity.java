package com.example.vastra;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.List;

public class AdminActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;

    private UserManager db;
    private EditText descriptionEditText;
    private EditText priceEditText;
    private EditText nameEditText;
    private ImageView selectedImageView;
    private Bitmap selectedImageBitmap;
    private RecyclerView productRecyclerView;
    private RecyclerView userRecyclerView;
    private ProductAdapter productAdapter;
    private UserAdapter userAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_page); // Use admin_page.xml

        db = new UserManager(this);
        descriptionEditText = findViewById(R.id.description_edit_text);
        priceEditText = findViewById(R.id.price_edit_text);
        nameEditText = findViewById(R.id.name_edit_text);
        selectedImageView = findViewById(R.id.selected_image_view);
        Button selectImageButton = findViewById(R.id.select_image_button);
        Button addProductButton = findViewById(R.id.add_product_button);
        Button productDetailsButton = findViewById(R.id.product_details_button);
        Button userDetailsButton = findViewById(R.id.user_details_button);
        Button logoutButton = findViewById(R.id.logout_button);
        TextView adminTitle = findViewById(R.id.admin_title);
        productRecyclerView = findViewById(R.id.product_recycler_view);
        userRecyclerView = findViewById(R.id.user_recycler_view); // The RecyclerView for User Details

        // Set admin title
        adminTitle.setText("Admin");

        // Initialize RecyclerViews
        productRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        productAdapter = new ProductAdapter(this, db.getAllProducts());
        productRecyclerView.setAdapter(productAdapter);

        userRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        userAdapter = new UserAdapter(this, db.getAllUsers()); // Pass Context and List<User> to UserAdapter
        userRecyclerView.setAdapter(userAdapter);
        userRecyclerView.setVisibility(View.GONE); // Initially hidden

        // Open image picker
        selectImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openImagePicker();
            }
        });

        // Handle Add Product button click
        addProductButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addProduct();
            }
        });

        // Handle Product Details button click
        productDetailsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                productRecyclerView.setVisibility(View.VISIBLE);
                userRecyclerView.setVisibility(View.GONE);
            }
        });

        // Handle User Details button click
        userDetailsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (userRecyclerView.getVisibility() == View.GONE) {
                    userRecyclerView.setVisibility(View.VISIBLE); // Show RecyclerView
                    productRecyclerView.setVisibility(View.GONE);
                } else {
                    userRecyclerView.setVisibility(View.GONE); // Optionally hide it again if clicked again
                }
            }
        });

        // Handle Logout button click
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logout();
            }
        });
    }

    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri imageUri = data.getData();
            try {
                InputStream inputStream = getContentResolver().openInputStream(imageUri);
                selectedImageBitmap = BitmapFactory.decodeStream(inputStream);
                selectedImageView.setImageBitmap(selectedImageBitmap);
                selectedImageView.setVisibility(View.VISIBLE);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private byte[] bitmapToByteArray(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        return stream.toByteArray();
    }

    private void clearForm() {
        nameEditText.setText("");
        descriptionEditText.setText("");
        priceEditText.setText("");
        selectedImageView.setImageBitmap(null);
        selectedImageView.setVisibility(View.GONE);
    }

    private void addProduct() {
        String name = nameEditText.getText().toString();
        String description = descriptionEditText.getText().toString();
        String priceText = priceEditText.getText().toString();

        if (selectedImageBitmap != null && !description.isEmpty() && !priceText.isEmpty() && !name.isEmpty()) {
            byte[] imageBytes = bitmapToByteArray(selectedImageBitmap);
            double price = Double.parseDouble(priceText);

            // Set this based on your logic or form input

            boolean success = db.addProduct(name, description, imageBytes, price);
            if (success) {
                Toast.makeText(AdminActivity.this, "Product Added", Toast.LENGTH_SHORT).show();
                clearForm();
                loadProducts();
            } else {
                Toast.makeText(AdminActivity.this, "Error Adding Product", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(AdminActivity.this, "Please fill all fields and select an image", Toast.LENGTH_SHORT).show();
        }
    }

    private void loadProducts() {
        List<Product> products = db.getAllProducts();
        productAdapter.setProducts(products);
        productRecyclerView.setVisibility(View.VISIBLE);
    }

    private void logout() {
        // Implement your logout logic here
        Toast.makeText(this, "Logged out successfully", Toast.LENGTH_SHORT).show();
        finish();
    }
}
