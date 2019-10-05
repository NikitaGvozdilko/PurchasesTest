package com.example.purchasestest.activities.di;

import android.app.Activity;
import android.view.View;

import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.example.purchasestest.adapters.ProductsAdapter;
import com.example.purchasestest.database.AppDatabase;
import com.example.purchasestest.database.model.Product;
import com.example.purchasestest.dialogs.SelectPictureDialog;
import com.example.purchasestest.activities.mvp.IPresenter;
import com.example.purchasestest.activities.mvp.IView;
import com.example.purchasestest.utils.AppPref;

import dagger.Module;
import dagger.Provides;

@Module(includes = PresenterModule.class)
public class MainActivityModule {
    private Activity activity;
    private IView mView;

    public MainActivityModule(Activity activity, IView view) {
        this.activity = activity;
        this.mView = view;
    }

    @SingleScope
    @Provides
    public AppDatabase provideDatabase(Activity context) {
        return AppDatabase.getInstance(context);
    }

    /*@SingleScope
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
    }*/

    @SingleScope
    @Provides
    public Activity getActivity() {
        return activity;
    }

    @SingleScope
    @Provides
    public IView getView() {
        return mView;
    }

    @SingleScope
    @Provides
    public RequestManager provideGlide(Activity activity) {
        return Glide.with(activity);
    }

    @Provides
    public ProductsAdapter provideProductsAdapter(Activity activity, RequestManager glide, IPresenter presenter, IView iView) {
        MutableLiveData<Product> newProductLD = new MutableLiveData<>();
        newProductLD.observe((LifecycleOwner) activity, new Observer<Product>() {
            @Override
            public void onChanged(Product product) {
                presenter.addProduct(product);
            }
        });
        MutableLiveData<Product> updateProductLD = new MutableLiveData<>();
        updateProductLD.observe((LifecycleOwner) activity, new Observer<Product>() {
            @Override
            public void onChanged(Product product) {
                presenter.updateProduct(product);
            }
        });
        AppPref appPref = new AppPref(activity);
        ProductsAdapter adapter = new ProductsAdapter(newProductLD, updateProductLD, appPref, glide, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SelectPictureDialog dialog = new SelectPictureDialog(activity, iView);
                dialog.show();
            }
        });
        return adapter;
    }
}
