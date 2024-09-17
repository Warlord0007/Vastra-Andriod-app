package com.example.vastra;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class LoginActivity extends Activity {

    private EditText usernameEditText;
    private EditText passwordEditText;
    private UserManager userManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Inflate the dialog_login layout
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_login, null);

        // Create an AlertDialog to show the login UI
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialogView)
                .setTitle(R.string.login_title) // Add a title if needed
                .setPositiveButton(R.string.login, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        performLogin();
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

        AlertDialog dialog = builder.create();
        dialog.show();

        // Initialize UI components
        usernameEditText = dialogView.findViewById(R.id.username_edit_text);
        passwordEditText = dialogView.findViewById(R.id.password_edit_text);
        Button loginButton = dialogView.findViewById(R.id.login_button);
        Button registerButton = dialogView.findViewById(R.id.register_button);

        // Initialize UserManager
        userManager = new UserManager(this);

        // Set click listeners
        loginButton.setOnClickListener(v -> performLogin());
        registerButton.setOnClickListener(v -> openRegisterActivity());
    }

    private void performLogin() {
        String username = usernameEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        if (username.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, R.string.login_error, Toast.LENGTH_SHORT).show();
        } else {
            // Validate user credentials with special case for admin
            String loginResult = userManager.validateLogin(username, password);
            if (loginResult != null) {
                if (loginResult.equals("admin")) {
                    Toast.makeText(this, "Admin Login Successful", Toast.LENGTH_SHORT).show();
                    // Redirect to AdminActivity
                    Intent intent = new Intent(this, AdminActivity.class);
                    startActivity(intent);
                    finish(); // Finish this activity
                } else {
                    Toast.makeText(this, R.string.login_success, Toast.LENGTH_SHORT).show();
                    // Redirect to main activity or home screen
                    Intent intent = new Intent(this, MainActivity.class);
                    startActivity(intent);
                    finish(); // Finish this activity
                }
            } else {
                Toast.makeText(this, R.string.login_error, Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void openRegisterActivity() {
        // Open RegisterActivity
        startActivity(new Intent(this, RegisterActivity.class));
    }
}
