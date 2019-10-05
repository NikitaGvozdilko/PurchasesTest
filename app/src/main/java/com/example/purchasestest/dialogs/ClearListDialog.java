package com.example.purchasestest.dialogs;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;

import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;

import com.example.purchasestest.database.model.Product;
import com.example.purchasestest.activities.mvp.IPresenter;

import java.util.List;

public class ClearListDialog {
    private AlertDialog alertDialog;

    public ClearListDialog(Context context, IPresenter presenter, List<Product> productsList) {
        AlertDialog.Builder removeDialog = new AlertDialog.Builder(context);
        removeDialog.setTitle("Select Action");
        String[] pictureDialogItems = {
                "Remove selected",
                "Remove all"};
        removeDialog.setItems(pictureDialogItems,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                presenter.removeSelected(productsList);
                                break;
                            case 1:
                                presenter.removeAll(productsList);
                                break;
                        }
                    }
                });
        alertDialog = removeDialog.create();
    }

    public void show() {
        alertDialog.show();
    }
}
