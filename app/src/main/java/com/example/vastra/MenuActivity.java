package com.example.vastra;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

public class MenuActivity extends AppCompatActivity {

    private UserManager db;
    private GridLayout productGrid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product); // Ensure this layout file exists

        db = new UserManager(this);
        productGrid = findViewById(R.id.product_grid);

        loadProducts();
    }

    private void loadProducts() {
        List<Product> products = db.getAllProducts();
        if (products != null) {
            for (Product product : products) {
                String name = product.getName();
                String description = product.getDescription();
                byte[] imageBytes = product.getImage();

                // Convert byte array to Bitmap
                Bitmap bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);

                // Create a new view for each product
                View productView = LayoutInflater.from(this).inflate(R.layout.product_item, productGrid, false);
                ImageView imageView = productView.findViewById(R.id.product_image);
                TextView nameView = productView.findViewById(R.id.product_name);
                TextView descriptionView = productView.findViewById(R.id.product_description);

                imageView.setImageBitmap(bitmap);
                nameView.setText(name);
                descriptionView.setText(description);

                productGrid.addView(productView);
            }
        }
    }
}
