package com.example.purchasestest.utils;

import com.example.purchasestest.database.model.Product;

import java.util.ArrayList;
import java.util.List;

public class ProductsFactory {
    public static List<Product> getProducts() {
        List<Product> list = new ArrayList<>();
        list.add(new Product("Shoes"));
        list.add(new Product("Potato"));
        list.add(new Product("Bike"));
        list.add(new Product("Cap"));
        list.add(new Product("Carrot"));
        list.add(new Product("Jeans"));
        list.add(new Product("Sweater"));

        return list;
    }

}
