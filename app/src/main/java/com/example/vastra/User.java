package com.example.vastra;
public class User {
    private int id;
    private String fullName;
    private String username;
    private String email;
    private String phone;

    public User(int id, String fullName, String username, String email, String phone) {
        this.id = id;
        this.fullName = fullName;
        this.username = username;
        this.email = email;
        this.phone = phone;
    }

    public int getId() {
        return id;
    }

    public String getFullName() {
        return fullName;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }
}
