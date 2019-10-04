package com.example.purchasestest.mvp;

import com.example.purchasestest.database.model.Product;

import java.util.List;

import io.reactivex.Flowable;

public interface IPresenter {
    void attachView(IView view);
    void add();
    void addProduct(Product product);
    void getProducts();
    void getHistory();
    void updateProduct(Product product);
    void removeSelected(List<Product> productsList);
    void removeAll(List<Product> productsList);
}
