package com.example.purchasestest;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.TypedValue;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.purchasestest.adapters.ProductsAdapter;
import com.example.purchasestest.database.AppDatabase;
import com.example.purchasestest.database.model.Product;
import com.example.purchasestest.dialogs.ClearListDialog;
import com.example.purchasestest.dialogs.SelectPictureDialog;
import com.example.purchasestest.mvp.IModel;
import com.example.purchasestest.mvp.IPresenter;
import com.example.purchasestest.mvp.IView;
import com.example.purchasestest.mvp.Model;
import com.example.purchasestest.mvp.Presenter;
import com.example.purchasestest.utils.AppPref;
import com.example.purchasestest.utils.ListMode;
import com.example.purchasestest.utils.Utils;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.IOException;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements IView {
    private static final int GALLERY = 0;
    private static final int CAMERA = 1;
    @BindView(R.id.recycler)
    RecyclerView mRecyclerView;

    @BindView(R.id.switchPurchases)
    Switch mSwitch;

    @BindView(R.id.fabAdd)
    FloatingActionButton mFabAdd;

    @BindView(R.id.fabClear)
    FloatingActionButton mFabClear;

    @BindView(R.id.textCurrentProducts)
    TextView textProducts;

    @BindView(R.id.textHistory)
    TextView textHistory;

    private ListMode mode = ListMode.PRODUCTS;

    private ProductsAdapter mAdapter;
    private IPresenter mPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        mFabAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPresenter.add();
                mAdapter.updateAddProductMode();
                mRecyclerView.smoothScrollToPosition(mAdapter.getItemCount());
            }
        });

        mFabClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ClearListDialog dialog = new ClearListDialog(MainActivity.this, mPresenter, mAdapter.getProductsList());
                dialog.show();
            }
        });

        MutableLiveData<Product> newProductLD = new MutableLiveData<>();
        newProductLD.observe(this, new Observer<Product>() {
            @Override
            public void onChanged(Product product) {
                mPresenter.addProduct(product);
            }
        });
        MutableLiveData<Product> updateProductLD = new MutableLiveData<>();
        updateProductLD.observe(this, new Observer<Product>() {
            @Override
            public void onChanged(Product product) {
                mPresenter.updateProduct(product);
            }
        });
        AppPref appPref = new AppPref(this);
        mAdapter = new ProductsAdapter(newProductLD, updateProductLD, appPref, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SelectPictureDialog dialog = new SelectPictureDialog(MainActivity.this, MainActivity.this);
                dialog.show();
            }
        });

        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0 && mode == ListMode.PRODUCTS) {
                    if (mFabAdd.isShown()) {
                        mFabClear.hide();
                        mFabAdd.hide();
                    }
                } else if (dy < 0 && mode == ListMode.PRODUCTS) {
                    if (!mFabAdd.isShown()) {
                        mFabClear.show();
                        mFabAdd.show();
                    }
                }
            }
        });

        IModel model = new Model(AppDatabase.getInstance(this));
        mPresenter = new Presenter(model);
        mPresenter.attachView(this);
        mPresenter.getProducts();

        mSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked) {
                    mode = ListMode.HISTORY;
                    mFabAdd.hide();
                    mFabClear.hide();
                    mPresenter.getHistory();
                    mAdapter.hideNewProduct();
                    textHistory.setTextSize(TypedValue.COMPLEX_UNIT_SP, 22);
                    textProducts.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
                    textHistory.setTextColor(ContextCompat.getColor(MainActivity.this, R.color.colorAccent));
                    textProducts.setTextColor(ContextCompat.getColor(MainActivity.this, android.R.color.darker_gray));
                } else {
                    mode = ListMode.PRODUCTS;
                    mFabAdd.show();
                    mFabClear.show();
                    mPresenter.getProducts();
                    textHistory.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
                    textProducts.setTextSize(TypedValue.COMPLEX_UNIT_SP, 22);
                    textHistory.setTextColor(ContextCompat.getColor(MainActivity.this, android.R.color.darker_gray));
                    textProducts.setTextColor(ContextCompat.getColor(MainActivity.this, R.color.colorAccent));
                }
            }
        });
    }

    @Override
    public void updateList(List<Product> products) {
        mAdapter.updateProducts(products, mode);
    }

    @Override
    public void showError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_CANCELED) {
            return;
        }

        if (requestCode == GALLERY) {
            if (data != null) {
                Uri contentURI = data.getData();
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), contentURI);
                    String imagePath = Utils.saveImage(this, bitmap);
                    mAdapter.setImage(imagePath);
                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(MainActivity.this, "Failed!", Toast.LENGTH_SHORT).show();
                }
            }
        } else if (requestCode == CAMERA) {
            if (data.getExtras() != null) {
                Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
                String imagePath = Utils.saveImage(this, thumbnail);
                mAdapter.setImage(imagePath);
            }
        }
    }
}
