package com.example.vastra;

public class Product {
    private int id;
    private String name;
    private String description;
    private byte[] image;
    private double price;

    public Product(int id, String name, String description, byte[] image, double price) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.image = image;
        this.price = price;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public byte[] getImage() {
        return image;
    }

    public double getPrice() {
        return price;
    }
}
