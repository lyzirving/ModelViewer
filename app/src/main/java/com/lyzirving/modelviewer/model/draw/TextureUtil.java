package com.lyzirving.modelviewer.model.draw;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLUtils;

import com.lyzirving.modelviewer.util.AppContext;

import java.io.IOException;
import java.io.InputStream;

public class TextureUtil {
    public static final int NO_TEXTURE = -1;

    private static class TextureUtilWrapper {
        private static TextureUtil mInstance = new TextureUtil();
    }

    public static TextureUtil get() {
        return TextureUtilWrapper.mInstance;
    }

    public int createTexture(int drawableId) {
        //store the texture id
        int[] textures = new int[1];
        GLES20.glGenTextures(1, textures, 0);
        int textureId = textures[0];
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId);
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST);
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);

        Bitmap bitmapTmp;
        InputStream is = AppContext.get().getContext().getResources().openRawResource(drawableId);
        bitmapTmp = BitmapFactory.decodeStream(is);
        try {
            is.close();
        } catch (IOException e) {
            return NO_TEXTURE;
        }
        GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmapTmp, 0);
        bitmapTmp.recycle();
        return textureId;
    }

}
