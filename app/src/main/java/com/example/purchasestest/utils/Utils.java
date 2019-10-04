package com.example.purchasestest.utils;

import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Bitmap;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;

public class Utils {
    public static String saveImage(Context context, Bitmap thumbnail) {
        ContextWrapper cw = new ContextWrapper(context);
        File directory = cw.getDir("Photo", Context.MODE_PRIVATE);
        if (!directory.exists()) {
            directory.mkdir();
        }

        File mypath;
        int i = 0;
        do {
            mypath = new File(directory, "thumbnail" + i + ".png");
            i++;
        } while (mypath.exists());

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(mypath);
            thumbnail.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.close();
        } catch (Exception e) {
            Log.e("SAVE_IMAGE", e.getMessage(), e);
        }

        return mypath.getAbsolutePath();
    }
}
