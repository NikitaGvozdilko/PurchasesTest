package com.example.purchasestest.activities.mainActivity.mvp;

import com.example.purchasestest.database.model.Product;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;

public class Presenter implements IPresenter {
    private IView view;
    private IModel model;
    private CompositeDisposable compositeDisposable = new CompositeDisposable();

    public Presenter(IModel model) {
        this.model = model;
    }

    @Override
    public void attachView(IView view) {
        this.view = view;
    }

    @Override
    public void add() {
        //model.addProduct(new Product("Unknown"));
    }

    @Override
    public void addProduct(Product product) {
        model.addProduct(product);
    }

    @Override
    public void getProducts() {
        compositeDisposable.add(model.getProducts().subscribe(
                new Consumer<List<Product>>() {
                    @Override
                    public void accept(List<Product> products) {
                        view.updateList(products);
                    }
                }
        ));
    }

    @Override
    public void getHistory() {
       compositeDisposable.add(model.getHistory().subscribe(
                new Consumer<List<Product>>() {
                    @Override
                    public void accept(List<Product> products) {
                        view.updateList(products);
                    }
                }
        ));
    }

    @Override
    public void updateProduct(Product product) {
        model.updateProduct(product);
    }

    @Override
    public void removeSelected(List<Product> productsList) {
        List<Product> productsResult = new ArrayList<>(productsList);
        Observable.fromIterable(productsList)
                .filter(Product::isPurchased)
                .map(product -> {
                    productsResult.remove(product);
                    product.setPurchased(false);
                    product.setHistory(true);
                    Date time = new Date();
                    product.setTime(time.getTime());
                    model.updateProduct(product);
                    return product;
                })
                .toList()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DisposableSingleObserver<List<Product>>() {
                    @Override
                    public void onSuccess(List<Product> products) {
                        view.updateList(productsResult);
                    }

                    @Override
                    public void onError(Throwable e) {
                        view.showError(e.getMessage());
                    }
                });
    }

    @Override
    public void removeAll(List<Product> productsList) {
        Observable.fromIterable(productsList)
                .map(product -> {
                    product.setPurchased(false);
                    product.setHistory(true);
                    Date time = new Date();
                    product.setTime(time.getTime());
                    model.updateProduct(product);
                    return product;
                })
                .toList()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DisposableSingleObserver<List<Product>>() {
                    @Override
                    public void onSuccess(List<Product> products) {
                        view.updateList(new ArrayList<Product>());
                    }

                    @Override
                    public void onError(Throwable e) {
                        view.showError(e.getMessage());
                    }
                });
    }

    @Override
    public void stopLoading() {
        compositeDisposable.dispose();
    }

}
