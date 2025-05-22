/*
    Utilitário para carregar e exibir imagens codificadas em Base64 dentro de ImageViews de forma assíncrona.
    Isso previne travamento da UI ao decodificar imagens pesadas.
*/

package com.example.healthpet3.utils;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.widget.ImageView;

import com.example.healthpet3.R;

public class ImageUtils {
    public static void setBase64Image(ImageView imageView, String base64Image) {
        setBase64Image(imageView, base64Image, false);
    }

    public static void setBase64Image(ImageView imageView, String base64Image, boolean isTutorImage) {
        if (base64Image != null && !base64Image.isEmpty()) {
            try {
                byte[] decodedBytes = Base64.decode(base64Image, Base64.DEFAULT);
                Bitmap bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
                imageView.setImageBitmap(bitmap);
            } catch (Exception e) {
                e.printStackTrace();
                imageView.setImageResource(isTutorImage ? R.drawable.tutoricon2 : R.drawable.cachorrogato);
            }
        } else {
            imageView.setImageResource(isTutorImage ? R.drawable.tutoricon2 : R.drawable.cachorrogato);
        }
    }
}