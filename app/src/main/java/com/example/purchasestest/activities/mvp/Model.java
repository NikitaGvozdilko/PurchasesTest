package com.example.purchasestest.activities.mvp;

import com.example.purchasestest.database.AppDatabase;
import com.example.purchasestest.database.model.Product;

import java.util.List;

import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class Model implements IModel {
    private AppDatabase database;

    public Model(AppDatabase database) {
        this.database = database;
    }

    @Override
    public void addProduct(Product product) {
        database.addProduct(product);
    }

    @Override
    public Single<List<Product>> getProducts() {
        return database.getProducts()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Single<List<Product>> getHistory() {
        return database.getHistory()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public void updateProduct(Product product) {
        database.addProduct(product);
    }
}
