package com.example.vastra;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity {

    private UserManager userManager;
    private TextView usernameTextView;
    private Button logoutButton;
    private boolean isLoggedIn = false;
    private String currentUsername = "Guest";
    private RecyclerView productRecyclerView;
    private ProductAdapter productAdapter;
    private List<Product> productList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        userManager = new UserManager(this);
        usernameTextView = findViewById(R.id.username_text_view);
        logoutButton = findViewById(R.id.logout_button);
        productRecyclerView = findViewById(R.id.product_grid);

        updateUsernameText();
        checkLoginStatus();
        setupNavigation();
        setupRecyclerView();
        loadProducts(); // Load products from the database and set them in the adapter

        Log.d("MainActivity", "Number of products: " + productList.size());
    }

    private void checkLoginStatus() {
        if (isLoggedIn) {
            usernameTextView.setText("Welcome, " + currentUsername);
            logoutButton.setText(R.string.logout_title);
        } else {
            usernameTextView.setText("Guest");
            logoutButton.setText(R.string.login_title);
        }
    }

    public void onLogoutLoginClick(View view) {
        if (isLoggedIn) {
            logout();
        } else {
            showLoginDialog();
        }
    }

    private void showLoginDialog() {
        final Dialog loginDialog = new Dialog(this);
        loginDialog.setContentView(R.layout.dialog_login);

        final EditText usernameEditText = loginDialog.findViewById(R.id.username_edit_text);
        final EditText passwordEditText = loginDialog.findViewById(R.id.password_edit_text);
        Button loginButton = loginDialog.findViewById(R.id.login_button);
        Button registerButton = loginDialog.findViewById(R.id.register_button);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = usernameEditText.getText().toString();
                String password = passwordEditText.getText().toString();

                String loginResult = userManager.validateLogin(username, password);
                if (loginResult != null) {
                    login(username);
                    loginDialog.dismiss();

                    if (loginResult.equals("admin")) {
                        Intent intent = new Intent(MainActivity.this, AdminActivity.class);
                        startActivity(intent);
                    }
                } else {
                    Toast.makeText(MainActivity.this, R.string.login_error, Toast.LENGTH_SHORT).show();
                }
            }
        });

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, RegisterActivity.class);
                startActivity(intent);
                loginDialog.dismiss();
            }
        });

        loginDialog.show();
    }

    private void login(String username) {
        isLoggedIn = true;
        currentUsername = username;
        updateUsernameText();
    }

    private void logout() {
        isLoggedIn = false;
        currentUsername = "Guest";
        updateUsernameText();
    }

    private void updateUsernameText() {
        if (isLoggedIn) {
            usernameTextView.setText("Welcome, " + currentUsername);
        } else {
            usernameTextView.setText("Guest");
        }
    }

    private void setupNavigation() {
        Spinner navigationSpinner = findViewById(R.id.navigation_spinner);

        if (navigationSpinner != null) {
            navigationSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    switch (position) {
                        case 0: // Home
                            break;
                        case 1: // Products
                            startActivity(new Intent(MainActivity.this, ProductActivity.class));
                            break;
                        case 2: // Login
                            showLoginDialog();
                            break;
                        case 3: // About us
                            // About us page logic
                            break;
                        default:
                            Log.w("MainActivity", "Unexpected position: " + position);
                            break;
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                }
            });
        } else {
            Log.e("MainActivity", "Navigation spinner is null. Check your layout file and ID.");
        }
    }

    private void setupRecyclerView() {
        productRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        productAdapter = new ProductAdapter(this, productList);
        productRecyclerView.setAdapter(productAdapter);

        productAdapter.setOnItemClickListener(new ProductAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Product product) {
                showProductDetailsDialog(product);
            }
        });
    }

    private void loadProducts() {
        productList = fetchProductsFromDatabase();

        if (productList == null) {
            productList = new ArrayList<>();
            Log.e("MainActivity", "Product list is null, initializing with an empty list.");
        }

        if (productAdapter != null) {
            productAdapter.setProducts(productList);
        } else {
            Log.e("MainActivity", "ProductAdapter is null. Make sure it's initialized.");
        }
    }

    public void onMenuClick(View view) {
        Toast.makeText(this, "Menu clicked!", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(MainActivity.this, MenuActivity.class);
        startActivity(intent);
    }

    private List<Product> fetchProductsFromDatabase() {
        return userManager.getAllProducts(); // Fetch all products from the database using UserManager
    }

    private void showProductDetailsDialog(Product selectedProduct) {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_product);

        ImageView selectedProductImage = dialog.findViewById(R.id.selected_product_image);
        TextView selectedProductName = dialog.findViewById(R.id.selected_product_name);
        TextView selectedProductDescription = dialog.findViewById(R.id.selected_product_description);
        TextView selectedProductPrice = dialog.findViewById(R.id.selected_product_price);
        RecyclerView similarProductsRecycler = dialog.findViewById(R.id.similar_products_recycler);

        // Set selected product details
        if (selectedProduct.getImage() != null) {
            Bitmap bitmap = BitmapFactory.decodeByteArray(selectedProduct.getImage(), 0, selectedProduct.getImage().length);
            selectedProductImage.setImageBitmap(bitmap);
        } else {
            selectedProductImage.setImageResource(R.drawable.product_image); // Default placeholder
        }

        selectedProductName.setText(selectedProduct.getName());
        selectedProductDescription.setText(selectedProduct.getDescription());
        selectedProductPrice.setText(String.format("Rs. %.2f", selectedProduct.getPrice()));

        // Set up RecyclerView for similar products
        similarProductsRecycler.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        ProductAdapter similarProductAdapter = new ProductAdapter(this, fetchSimilarProducts(selectedProduct));
        similarProductsRecycler.setAdapter(similarProductAdapter);

        dialog.show();
    }

    private List<Product> fetchSimilarProducts(Product selectedProduct) {
        String productName = selectedProduct.getName().toLowerCase();
        List<Product> allProducts = userManager.getAllProducts();
        List<Product> similarProducts = new ArrayList<>();

        for (Product product : allProducts) {
            if (!product.equals(selectedProduct) && product.getName().toLowerCase().contains(productName)) {
                similarProducts.add(product);
            }
        }

        // Limit to 4 similar products
        return similarProducts.size() > 4 ? similarProducts.subList(0, 4) : similarProducts;
    }
}
