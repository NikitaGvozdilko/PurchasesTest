package com.example.purchasestest.adapters;

import android.net.Uri;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.recyclerview.widget.RecyclerView;

import com.example.purchasestest.R;
import com.example.purchasestest.database.model.Product;
import com.example.purchasestest.utils.AppPref;
import com.example.purchasestest.utils.ListMode;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ProductsAdapter extends RecyclerView.Adapter {
    private final AppPref appPref;
    private List<Product> productsList = new ArrayList<>();
    private MutableLiveData<Product> newProduct;
    private MutableLiveData<Product> updateProduct;
    private boolean addNewProduct = false;
    private View.OnClickListener onClickListener;
    private String newProductImagePath;
    private ListMode listMode = ListMode.PRODUCTS;

    public ProductsAdapter(MutableLiveData<Product> newProduct, MutableLiveData<Product> updateProduct, AppPref appPref, View.OnClickListener onClickListener) {
        this.newProduct = newProduct;
        this.updateProduct = updateProduct;
        this.appPref = appPref;
        this.onClickListener = onClickListener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        switch (viewType) {
            case 1:
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_add_product, parent, false);
                return new NewProductViewHolder(view);
            default:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_product, parent, false);
                return new ViewHolder(view);
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (position == productsList.size()) return 1;
        return 0;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ViewHolder)
            ((ViewHolder) holder).bind(productsList.get(position));
        else ((NewProductViewHolder) holder).bind();

    }

    @Override
    public int getItemCount() {
        if (addNewProduct)
            return productsList.size() + 1;
        return productsList.size();
    }

    public void updateAddProductMode() {
        addNewProduct = !addNewProduct;
        notifyDataSetChanged();
    }

    public void hideNewProduct() {
        addNewProduct = false;
        notifyDataSetChanged();
    }

    public void updateProducts(List<Product> list, ListMode mode) {
        this.productsList = list;
        this.listMode = mode;
        notifyDataSetChanged();
    }

    public void setImage(String imagePath) {
        newProductImagePath = imagePath;
        notifyDataSetChanged();
    }

    public List<Product> getProductsList() {
        return productsList;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.textTitle)
        TextView textTitle;

        @BindView(R.id.checkBox)
        CheckBox checkBox;

        View itemView;

        @BindView(R.id.imageProductPhoto)
        ImageView imageProductPhoto;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            this.itemView = itemView;
        }

        void bind(Product product) {
            if (listMode == ListMode.HISTORY) {
                checkBox.setVisibility(View.GONE);
            } else {
                View.OnClickListener onClickListener = new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        product.setPurchased(!product.isPurchased());
                        updateProduct.postValue(product);
                        checkBox.setChecked(product.isPurchased());
                    }
                };
                checkBox.setVisibility(View.VISIBLE);
                checkBox.setChecked(product.isPurchased());
                checkBox.setOnClickListener(onClickListener);
                itemView.setOnClickListener(onClickListener);
                if (!TextUtils.isEmpty(product.getImagePath())) {
                    imageProductPhoto.setImageURI(Uri.parse(product.getImagePath()));
                    textTitle.setVisibility(View.GONE);
                    imageProductPhoto.setVisibility(View.VISIBLE);
                } else {
                    textTitle.setVisibility(View.VISIBLE);
                    imageProductPhoto.setVisibility(View.GONE);
                }
            }

            textTitle.setText(product.getText());
        }
    }

    class NewProductViewHolder extends RecyclerView.ViewHolder {
        EditText editText;
        View imageSave;
        ImageView imagePhoto;
        ImageView imageProductPhoto;

        NewProductViewHolder(@NonNull View itemView) {
            super(itemView);
            editText = itemView.findViewById(R.id.editProduct);
            imageSave = itemView.findViewById(R.id.imageSaveProduct);
            imagePhoto = itemView.findViewById(R.id.imagePhoto);
            imageProductPhoto = itemView.findViewById(R.id.imageProductPhoto);
        }

        void bind() {
            imageSave.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (!editText.getText().toString().isEmpty() || !TextUtils.isEmpty(newProductImagePath)) {
                        Product product = new Product();
                        product.setId(appPref.getId());
                        product.setImagePath(newProductImagePath);
                        product.setText(editText.getText().toString());
                        Date currentTime = new Date();
                        product.setTime(currentTime.getTime());
                        productsList.add(product);
                        notifyDataSetChanged();
                        newProduct.postValue(product);
                        editText.getText().clear();
                        newProductImagePath = "";
                    }
                }
            });

            if (!TextUtils.isEmpty(newProductImagePath)) {
                imageProductPhoto.setImageURI(Uri.parse(newProductImagePath));
                editText.setVisibility(View.GONE);
                imageProductPhoto.setVisibility(View.VISIBLE);
            } else {
                editText.setVisibility(View.VISIBLE);
                imageProductPhoto.setVisibility(View.GONE);
            }

            imagePhoto.setOnClickListener(onClickListener);
        }
    }
}
