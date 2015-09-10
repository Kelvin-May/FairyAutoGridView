package com.k.utils;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.view.View;

/**
 * Created by Kelvin on 15/9/8.
 */
public class ViewUtil {

    public static Bitmap convertViewToBitmap(View view, int bitmapWidth, int bitmapHeight) {
        return convertViewToBitmap(view, bitmapWidth, bitmapHeight, 1.0f);
    }

    public static Bitmap convertViewToBitmap(View view, int bitmapWidth, int bitmapHeight, float scale) {
        Bitmap bitmap = Bitmap.createBitmap(bitmapWidth, bitmapHeight, Bitmap.Config.ARGB_8888);
        view.draw(new Canvas(bitmap));
        if (scale != 1.0f) {
            Matrix matrix = new Matrix();
            matrix.postScale(scale, scale);
            bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        }
        return bitmap;
    }
}
