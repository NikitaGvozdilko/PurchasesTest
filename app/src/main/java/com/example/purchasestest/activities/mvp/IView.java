package com.example.purchasestest.activities.mvp;

import com.example.purchasestest.database.model.Product;

import java.util.List;

public interface IView {
    void updateList(List<Product> products);
    void showError(String message);
}
