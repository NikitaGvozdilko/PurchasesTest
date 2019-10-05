package com.example.purchasestest.activities.mainActivity.di;

import com.example.purchasestest.adapters.ProductsAdapter;
import com.example.purchasestest.activities.mainActivity.mvp.IPresenter;

import dagger.Component;

@SingleScope
@Component(modules = {MainActivityModule.class, PresenterModule.class})
public interface MainActivityComponent {
    public IPresenter getPresenter();
    public ProductsAdapter getAdapter();
}
