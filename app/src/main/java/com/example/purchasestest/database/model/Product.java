package com.example.purchasestest.database.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = Product.tableName)
public class Product {
    static final String tableName = "purchases";
    @PrimaryKey()
    public int id;
    private String text;
    private String imagePath;
    private boolean isPurchased;
    private boolean isHistory;
    private long time;

    public Product() {
    }

    public Product(String text) {
        this.text = text;
        this.isPurchased = false;
        this.isHistory = false;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public boolean isPurchased() {
        return isPurchased;
    }

    public void setPurchased(boolean purchased) {
        isPurchased = purchased;
    }

    public boolean isHistory() {
        return isHistory;
    }

    public void setHistory(boolean history) {
        isHistory = history;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }
}
