package com.example.purchasestest.activities.di;

import com.example.purchasestest.database.AppDatabase;
import com.example.purchasestest.activities.mvp.IModel;
import com.example.purchasestest.activities.mvp.IPresenter;
import com.example.purchasestest.activities.mvp.IView;
import com.example.purchasestest.activities.mvp.Model;
import com.example.purchasestest.activities.mvp.Presenter;

import dagger.Module;
import dagger.Provides;

@Module
public class PresenterModule {
    @SingleScope
    @Provides
    public IModel provideModel(AppDatabase appDatabase) {
        return new Model(appDatabase);
    }

    @SingleScope
    @Provides
    public IPresenter providePresenter(IModel model, IView view) {
        IPresenter mPresenter = new Presenter(model);
        mPresenter.attachView(view);
        return mPresenter;
    }
}
