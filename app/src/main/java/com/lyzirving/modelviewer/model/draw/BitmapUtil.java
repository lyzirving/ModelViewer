package com.lyzirving.modelviewer.model.draw;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;

public class BitmapUtil {

    private static class BitmapUtilWrapper {
        private static BitmapUtil mInstance = new BitmapUtil();
    }

    public static BitmapUtil get() {
        return BitmapUtilWrapper.mInstance;
    }

    public Bitmap getBitmapWithColor(int color, int width, int height) {
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
        Canvas c = new Canvas(bitmap);
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(color);
        c.drawRect(0, 0, width, height, paint);
        return bitmap;
    }

}
