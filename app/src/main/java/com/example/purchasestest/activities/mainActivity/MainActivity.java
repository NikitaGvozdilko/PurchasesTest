package com.example.purchasestest.activities.mainActivity;

import android.Manifest;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.purchasestest.R;
import com.example.purchasestest.activities.mainActivity.di.DaggerMainActivityComponent;
import com.example.purchasestest.activities.mainActivity.di.MainActivityComponent;
import com.example.purchasestest.activities.mainActivity.di.MainActivityModule;
import com.example.purchasestest.activities.mainActivity.mvp.IPresenter;
import com.example.purchasestest.activities.mainActivity.mvp.IView;
import com.example.purchasestest.adapters.ProductsAdapter;
import com.example.purchasestest.database.model.Product;
import com.example.purchasestest.dialogs.ClearListDialog;
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
    private static final int PERM = 2;
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
        MainActivityComponent component = DaggerMainActivityComponent.builder().mainActivityModule(new MainActivityModule(this, this)).build();
        mAdapter = component.getAdapter();
        mPresenter = component.getPresenter();
        initViews();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mPresenter.stopLoading();
    }

    private void initViews() {
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0 && mode == ListMode.PRODUCTS) {
                    if (mFabAdd.isShown()) {
                        hideFabs();
                    }
                } else if (dy < 0 && mode == ListMode.PRODUCTS) {
                    if (!mFabAdd.isShown()) {
                        showFabs();
                    }
                }
            }
        });
        LayoutAnimationController animation = AnimationUtils.loadLayoutAnimation(this, R.anim.layout_animation_fall_down);
        mRecyclerView.setLayoutAnimation(animation);

        mPresenter.getProducts();

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
                if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(MainActivity.this,
                            new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,
                                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                    Manifest.permission.CAMERA
                            }, PERM);
                } else {
                    ClearListDialog dialog = new ClearListDialog(MainActivity.this, mPresenter, mAdapter.getProductsList());
                    dialog.show();
                }
            }
        });

        mSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked) {
                    mode = ListMode.HISTORY;
                    hideFabs();
                    mPresenter.getHistory();
                    mAdapter.hideNewProduct();
                    increaseTextSize(textHistory);
                    decreaseTextSize(textProducts);
                    textHistory.setTextColor(ContextCompat.getColor(MainActivity.this, R.color.colorAccent));
                    textProducts.setTextColor(ContextCompat.getColor(MainActivity.this, android.R.color.darker_gray));
                } else {
                    mode = ListMode.PRODUCTS;
                    showFabs();
                    mPresenter.getProducts();
                    decreaseTextSize(textHistory);
                    increaseTextSize(textProducts);
                    textHistory.setTextColor(ContextCompat.getColor(MainActivity.this, android.R.color.darker_gray));
                    textProducts.setTextColor(ContextCompat.getColor(MainActivity.this, R.color.colorAccent));
                }
            }

            private void decreaseTextSize(TextView textProducts) {
                ValueAnimator anim = ObjectAnimator.ofFloat(textProducts, "textSize", 22, 18);
                anim.setDuration(300);
                anim.start();
            }

            private void increaseTextSize(TextView textView) {
                ValueAnimator anim = ObjectAnimator.ofFloat(textView, "textSize", 18, 22);
                anim.setDuration(300);
                anim.start();
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERM:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED
                        && grantResults[1] == PackageManager.PERMISSION_GRANTED
                        && grantResults[2] == PackageManager.PERMISSION_GRANTED) {
                    ClearListDialog dialog = new ClearListDialog(MainActivity.this, mPresenter, mAdapter.getProductsList());
                    dialog.show();
                } else {
                    showError("You have no permissions");
                }
                break;
        }
    }

    @Override
    public void updateList(List<Product> products) {
        mAdapter.updateProducts(products, mode);
        mRecyclerView.scheduleLayoutAnimation();
    }

    @Override
    public void showError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_CANCELED || data == null) {
            return;
        }

        if (requestCode == GALLERY) {
            Uri contentURI = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), contentURI);
                String imagePath = Utils.saveImage(this, bitmap);
                mAdapter.setImage(imagePath);
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(MainActivity.this, "Failed!", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == CAMERA) {
            if (data.getExtras() == null) return;
            Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
            String imagePath = Utils.saveImage(this, thumbnail);
            mAdapter.setImage(imagePath);
        }
    }

    private void showFabs() {
        mFabClear.show();
        mFabAdd.show();
    }

    private void hideFabs() {
        mFabAdd.hide();
        mFabClear.hide();
    }
}
