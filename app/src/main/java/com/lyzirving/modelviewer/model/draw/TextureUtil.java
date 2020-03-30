package com.lyzirving.modelviewer.model.draw;

import android.graphics.Bitmap;
import android.opengl.GLES20;
import android.opengl.GLUtils;

public class TextureUtil {
    public static final int NO_TEXTURE = -1;

    private static class TextureUtilWrapper {
        private static TextureUtil mInstance = new TextureUtil();
    }

    public static TextureUtil get() {
        return TextureUtilWrapper.mInstance;
    }

    public int loadTexture(Bitmap img, boolean recycle) {
        int textures[] = new int[1];
        GLES20.glGenTextures(1, textures, 0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textures[0]);
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,
                GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,
                GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,
                GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,
                GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);

        GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, img, 0);
        if (recycle) {
            img.recycle();
        }
        return textures[0];
    }

}
