package com.example.vastra;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {

    private Context context;
    private List<Product> productList;
    private OnItemClickListener onItemClickListener;

    public ProductAdapter(Context context, List<Product> productList) {
        this.context = context;
        this.productList = productList != null ? productList : new ArrayList<>();
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_product, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Product product = productList.get(position);
        holder.productName.setText(product.getName());
        holder.productDescription.setText(product.getDescription());
        holder.productPrice.setText(String.format("Rs. %.2f", product.getPrice()));

        // Convert byte[] to Bitmap for product image
        if (product.getImage() != null && product.getImage().length > 0) {
            Bitmap bitmap = BitmapFactory.decodeByteArray(product.getImage(), 0, product.getImage().length);
            holder.productImage.setImageBitmap(bitmap);
        } else {
            holder.productImage.setImageResource(R.drawable.product_image); // Default placeholder
        }

        // Set click listener to show product details dialog
        holder.itemView.setOnClickListener(v -> {
            if (onItemClickListener != null) {
                onItemClickListener.onItemClick(product);
            } else {
                showProductDetailsDialog(product);  // If no external click listener is set, show details dialog
            }
        });
    }

    @Override
    public int getItemCount() {
        return productList != null ? productList.size() : 0;
    }

    public void setProducts(List<Product> products) {
        this.productList = products != null ? products : new ArrayList<>();
        notifyDataSetChanged();
    }

    private void showProductDetailsDialog(Product selectedProduct) {
        Dialog dialog = new Dialog(context);
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
        LinearLayoutManager layoutManager = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
        similarProductsRecycler.setLayoutManager(layoutManager);

        ProductAdapter similarProductAdapter = new ProductAdapter(context, fetchSimilarProducts(selectedProduct));
        similarProductsRecycler.setAdapter(similarProductAdapter);

        dialog.show();
    }

    // Method to fetch similar products based on selected product's name
    private List<Product> fetchSimilarProducts(Product selectedProduct) {
        String productName = selectedProduct.getName().toLowerCase();
        List<Product> allProducts = new UserManager(context).getAllProducts();  // Assuming you have access to UserManager here
        List<Product> similarProducts = new ArrayList<>();

        for (Product product : allProducts) {
            if (!product.equals(selectedProduct) && product.getName().toLowerCase().contains(productName)) {
                similarProducts.add(product);
            }
        }

        // Limit to 4 similar products
        return similarProducts.size() > 4 ? similarProducts.subList(0, 4) : similarProducts;
    }

    // Interface to handle click events externally
    public interface OnItemClickListener {
        void onItemClick(Product product);
    }

    static class ProductViewHolder extends RecyclerView.ViewHolder {
        TextView productName, productDescription, productPrice;
        ImageView productImage;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            productName = itemView.findViewById(R.id.product_name);
            productDescription = itemView.findViewById(R.id.product_description);
            productPrice = itemView.findViewById(R.id.product_price);
            productImage = itemView.findViewById(R.id.product_image);
        }
    }
}
