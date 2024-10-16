package com.example.ecommerce_nawaz;

public class CartItem {
    private String name;
    private String image;
    private double price;
    private int quantity;

    public CartItem(String name, String image, double price) {
        this.name = name;
        this.image = image;
        this.price = price;
        this.quantity = 1; // default quantity is 1 when first added
    }

    // Getters and Setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getTotalAmount() {
        return this.price * this.quantity;
    }
}
