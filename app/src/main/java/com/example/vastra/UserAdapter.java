package com.example.vastra;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {

    private List<User> userList;
    private Context context;

    public UserAdapter(Context context, List<User> userList) {
        this.context = context;
        this.userList = userList;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.item_user, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        User user = userList.get(position);
        holder.fullNameTextView.setText(user.getFullName());
        holder.usernameTextView.setText(user.getUsername());
        holder.emailTextView.setText(user.getEmail());
        holder.phoneTextView.setText(user.getPhone());
    }

    @Override
    public int getItemCount() {
        return userList != null ? userList.size() : 0;
    }

    public void setUserList(List<User> userList) {
        this.userList = userList;
        notifyDataSetChanged();
    }

    class UserViewHolder extends RecyclerView.ViewHolder {
        TextView fullNameTextView;
        TextView usernameTextView;
        TextView emailTextView;
        TextView phoneTextView;

        UserViewHolder(View itemView) {
            super(itemView);
            fullNameTextView = itemView.findViewById(R.id.full_name_text_view);
            usernameTextView = itemView.findViewById(R.id.username_text_view);
            emailTextView = itemView.findViewById(R.id.email_text_view);
            phoneTextView = itemView.findViewById(R.id.phone_text_view);
        }
    }
}
