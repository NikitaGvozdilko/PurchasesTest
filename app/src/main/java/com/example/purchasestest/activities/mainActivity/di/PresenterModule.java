package com.example.purchasestest.activities.mainActivity.di;

import com.example.purchasestest.database.AppDatabase;
import com.example.purchasestest.activities.mainActivity.mvp.IModel;
import com.example.purchasestest.activities.mainActivity.mvp.IPresenter;
import com.example.purchasestest.activities.mainActivity.mvp.IView;
import com.example.purchasestest.activities.mainActivity.mvp.Model;
import com.example.purchasestest.activities.mainActivity.mvp.Presenter;

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
