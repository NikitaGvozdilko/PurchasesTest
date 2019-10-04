package com.example.purchasestest.database;

import android.content.Context;

import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.purchasestest.database.model.Product;

import java.util.List;

import io.reactivex.Flowable;
import io.reactivex.Single;

@androidx.room.Database(entities = {Product.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    abstract PurchaseDao purchaseDao();

    public static volatile AppDatabase appDatabase;
    private static final Object LOCK = new Object();

    public static AppDatabase getInstance(Context context) {
        synchronized (LOCK) {
            if (appDatabase == null)
                appDatabase = buildDatabase(context);
            return appDatabase;
        }
    }

    private static AppDatabase buildDatabase(Context context) {
        return Room.databaseBuilder(context,
                AppDatabase.class, "purchases.db")
            .allowMainThreadQueries()
                .build();
    }

    public Single<List<Product>> getProducts() {
        return purchaseDao().getProducts();
    }

    public Single<List<Product>> getHistory() {
        return purchaseDao().getHistory();
    }

    public void addProduct(Product product) {
        purchaseDao().addProduct(product);
    }



}
