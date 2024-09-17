package com.example.vastra;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class UserListActivity extends AppCompatActivity {

    private RecyclerView userRecyclerView;
    private UserAdapter userAdapter;
    private UserManager db;
    private List<User> userList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_list);

        userRecyclerView = findViewById(R.id.user_recycler_view);
        db = new UserManager(this);
        userList = new ArrayList<>();

        userRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Initialize adapter with empty user list
        userAdapter = new UserAdapter(this, userList);
        userRecyclerView.setAdapter(userAdapter);

        // Load users into RecyclerView
        loadUsers();
    }

    private void loadUsers() {
        userList.clear();
        List<User> users = db.getAllUsers();  // Fetch users as a list
        if (users != null) {
            userList.addAll(users);
            userAdapter.notifyDataSetChanged();
        }
    }
}
