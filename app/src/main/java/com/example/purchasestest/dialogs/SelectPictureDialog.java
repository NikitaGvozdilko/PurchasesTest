package com.example.purchasestest.dialogs;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;

import androidx.appcompat.app.AlertDialog;

import com.example.purchasestest.activities.mainActivity.mvp.IView;

public class SelectPictureDialog {
    private static final int GALLERY = 0;
    private static final int CAMERA = 1;
    private AlertDialog alertDialog;

    public SelectPictureDialog(Activity activity, IView view) {
        AlertDialog.Builder pictureDialog = new AlertDialog.Builder(activity);
        pictureDialog.setTitle("Select Action");
        String[] pictureDialogItems = {
                "Select photo from gallery",
                "Capture photo from camera"};
        pictureDialog.setItems(pictureDialogItems,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                choosePhotoFromGallary(activity);
                                break;
                            case 1:
                                takePhotoFromCamera(activity, view);
                                break;
                        }
                    }
                });
        alertDialog = pictureDialog.create();
    }

    public void show() {
        alertDialog.show();
    }

    private void choosePhotoFromGallary(Activity activity) {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        activity.startActivityForResult(galleryIntent, GALLERY);
    }

    private void takePhotoFromCamera(Activity activity, IView view) {
        Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        if (intent.getPackage() != null)
            activity.startActivityForResult(intent, CAMERA);
        else
            view.showError("No camera available");
    }
}
