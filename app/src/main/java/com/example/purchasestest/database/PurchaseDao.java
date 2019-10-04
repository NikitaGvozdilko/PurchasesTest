package com.example.purchasestest.database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.purchasestest.database.model.Product;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.Observer;
import io.reactivex.Single;

@Dao
public interface PurchaseDao {
    @Query("SELECT * FROM purchases WHERE isHistory = 0")
    Single<List<Product>> getProducts();

    @Query("SELECT * FROM purchases WHERE isHistory = 1 ORDER BY time desc")
    Single<List<Product>> getHistory();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void addProduct(Product product);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void updateProduct(Product product);
}
