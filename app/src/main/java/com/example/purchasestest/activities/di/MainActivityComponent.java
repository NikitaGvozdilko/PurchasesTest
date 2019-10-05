package com.example.purchasestest.activities.di;

import com.example.purchasestest.adapters.ProductsAdapter;
import com.example.purchasestest.activities.mvp.IPresenter;

import dagger.Component;

@SingleScope
@Component(modules = {MainActivityModule.class, PresenterModule.class})
public interface MainActivityComponent {
    public IPresenter getPresenter();
    public ProductsAdapter getAdapter();
}
