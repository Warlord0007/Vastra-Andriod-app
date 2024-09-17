package com.example.vastra;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class RegisterActivity extends AppCompatActivity {

    private EditText fullNameEditText, usernameEditText, emailEditText, phoneEditText, passwordEditText, rePasswordEditText;
    private Button registerButton;
    private UserManager userManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Initialize UserManager
        userManager = new UserManager(this);

        // Initialize UI components
        fullNameEditText = findViewById(R.id.full_name);
        usernameEditText = findViewById(R.id.username);
        emailEditText = findViewById(R.id.email);
        phoneEditText = findViewById(R.id.phone);
        passwordEditText = findViewById(R.id.password);
        rePasswordEditText = findViewById(R.id.repassword);
        registerButton = findViewById(R.id.register_button);

        // Set OnClickListener for register button
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerUser();
            }
        });
    }

    private void registerUser() {
        String fullName = fullNameEditText.getText().toString().trim();
        String username = usernameEditText.getText().toString().trim();
        String email = emailEditText.getText().toString().trim();
        String phone = phoneEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();
        String rePassword = rePasswordEditText.getText().toString().trim();

        if (fullName.isEmpty() || username.isEmpty() || email.isEmpty() || phone.isEmpty() || password.isEmpty() || rePassword.isEmpty()) {
            Toast.makeText(RegisterActivity.this, R.string.fill_all_fields, Toast.LENGTH_SHORT).show();
        } else if (!password.equals(rePassword)) {
            Toast.makeText(RegisterActivity.this, R.string.password_mismatch, Toast.LENGTH_SHORT).show();
        } else {
            boolean isRegistered = userManager.registerUser(fullName, username, email, phone, password);
            if (isRegistered) {
                Toast.makeText(RegisterActivity.this, R.string.register_success, Toast.LENGTH_SHORT).show();
                // Navigate to login screen or finish the activity
                finish(); // Optionally, navigate to login screen
            } else {
                Toast.makeText(RegisterActivity.this, R.string.register_error, Toast.LENGTH_SHORT).show();
            }
        }
    }
}
