package com.lyzirving.modelviewer.model.draw;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import android.opengl.Matrix;

/**
 *  in default case, the camera is located at (0, 0, 0) in the world coordinate system;
 *  in default case, the model is located at (0, 0, -1) in the world coordinate system;
 *  in default case, the up vector is (0, 1, 0);
 */
public class MatrixState {

    private static class MatrixStateWrapper {
        private static MatrixState mInstance = new MatrixState();
    }

    public static MatrixState get() {
        return MatrixStateWrapper.mInstance;
    }

    private float[] mProjMatrix;
    private float[] mVMatrix;
    private float[] mCurrMatrix;//default location of model in world coordinate system is (0, 0, -1);
    private float[] mMVPMatrix;
    private float[] mLightLocation;
    private FloatBuffer mCameraFB;

    private float[][] mStack;
    private int mStackTop;
    private float[] mCameraLocation;//default location in world coordinate system is (0, 0, 0);

    private ByteBuffer llbb;

    private MatrixState() {
        reset();
    }

    public void reset() {
        /**
         * init the default location of camera in world coordinate system
         */
        mCameraLocation = new float[3];
        mCameraLocation[0] = 0;
        mCameraLocation[1] = 0;
        mCameraLocation[2] = 0;

        llbb = ByteBuffer.allocateDirect(3 * 4);
        mLightLocation = new float[3];
        mStackTop = -1;
        mStack = new float[10][16];
        mCurrMatrix = new float[16];
        mVMatrix = new float[16];
        mProjMatrix = new float[16];
        mMVPMatrix = new float[16];
        Matrix.setIdentityM(mCurrMatrix, 0);
        Matrix.setIdentityM(mVMatrix, 0);
        Matrix.setIdentityM(mProjMatrix, 0);
        Matrix.setIdentityM(mMVPMatrix, 0);
    }

    public void pushMatrix() {
        mStackTop++;
        for (int i = 0; i < 16; i++) {
            mStack[mStackTop][i] = mCurrMatrix[i];
        }
    }

    public void popMatrix() {
        for (int i = 0; i < 16; i++) {
            mCurrMatrix[i] = mStack[mStackTop][i];
        }
        mStackTop--;
    }

    public void translate(float x, float y, float z) {
        Matrix.translateM(mCurrMatrix, 0, x, y, z);
    }

    public void rotate(float angle, float x, float y, float z) {
        Matrix.rotateM(mCurrMatrix, 0, angle, x, y, z);
    }

    public void scale(float x, float y, float z) {
        Matrix.scaleM(mCurrMatrix, 0, x, y, z);
    }

    public void matrix(float[] self) {
        float[] result = new float[16];
        Matrix.multiplyMM(result, 0, mCurrMatrix, 0, self, 0);
        mCurrMatrix = result;
    }

    public void setCamera(float cx, float cy, float cz,
                                 float tx, float ty, float tz,
                                 float upx, float upy, float upz) {
        Matrix.setLookAtM(mVMatrix, 0,
                cx, cy, cz,
                tx, ty, tz,
                upx, upy, upz);

        mCameraLocation[0] = cx;
        mCameraLocation[1] = cy;
        mCameraLocation[2] = cz;

        llbb.clear();
        llbb.order(ByteOrder.nativeOrder());
        mCameraFB = llbb.asFloatBuffer();
        mCameraFB.put(mCameraLocation);
        mCameraFB.position(0);
    }

    public void setProjectFrustum(float left, float right, float bottom, float top,
                                         float near, float far) {
        Matrix.frustumM(mProjMatrix, 0, left, right, bottom, top, near, far);
    }

    public void setProjectOrtho(float left, float right, float bottom, float top,
                                       float near, float far) {
        Matrix.orthoM(mProjMatrix, 0, left, right, bottom, top, near, far);
    }

    public float[] getFinalMatrix() {
        Matrix.multiplyMM(mMVPMatrix, 0, mVMatrix, 0, mCurrMatrix, 0);
        Matrix.multiplyMM(mMVPMatrix, 0, mProjMatrix, 0, mMVPMatrix, 0);
        return mMVPMatrix;
    }

    public float[] getMMatrix() {
        return mCurrMatrix;
    }

    public float[] getProjMatrix() {
        return mProjMatrix;
    }

    public float[] getCaMatrix() {
        return mVMatrix;
    }

    public float[] getCameraLocation() {
        return mCameraLocation;
    }

    /**
     * @param x, x > 0, means the light source is on the right side when you look up to the screen;
     *           in the code, we should set it minus;
     * @param y, y > 0, means the light source is on the top side when you look up to the screen;
     *           in the code, we should set it minus;
     * @param z, z > 0, means the light source is front of the screen when you look up to the screen;
     *           we dont't have to set it mimus;
     */
    public void setLightLocation(float x, float y, float z) {
        if (Math.abs(x) > 1 || Math.abs(y) > 1 || Math.abs(z) > 1) {
            float tmp = (float) Math.sqrt(x * x + y * y + z * z);
            x = x / tmp;
            y = y / tmp;
            z = z / tmp;
        }
        mLightLocation[0] = -x;
        mLightLocation[1] = -y;
        mLightLocation[2] = z;
    }

    public float[] getLightLocation() {
        return mLightLocation;
    }

}
