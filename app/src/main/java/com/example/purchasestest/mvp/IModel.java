package com.example.purchasestest.mvp;

import com.example.purchasestest.database.model.Product;

import java.util.List;

import io.reactivex.Single;

public interface IModel {
    void addProduct(Product product);

    Single<List<Product>> getProducts();

    Single<List<Product>> getHistory();

    void updateProduct(Product product);
}
