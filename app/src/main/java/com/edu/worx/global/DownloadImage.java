package com.edu.worx.global;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.widget.ImageView;

import java.io.InputStream;

public class DownloadImage extends AsyncTask<String, Void, Bitmap> {

    ImageView imgView;
    DownloadImage (ImageView img_view) {
        imgView = img_view;
    }

    @Override
    protected Bitmap doInBackground(String... URLs) {
        Bitmap bitmap = null;
        try {
            // Download Image from URL
            InputStream input = new java.net.URL(URLs[0]).openStream();
            // Decode Bitmap
            bitmap = BitmapFactory.decodeStream(input);
            imgView.setImageBitmap(bitmap);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bitmap;
    }
}
