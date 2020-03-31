package com.lyzirving.modelviewer.model.draw;

import android.opengl.GLES20;

import java.nio.FloatBuffer;
import java.util.LinkedList;

public class GLFilter {

    protected final String BASIC_VERTEX_SHADER =
            "attribute vec3 aPosition; \n" +
            "attribute vec2 aTexCoord; \n" +
            "varying vec2 vTexCoord; \n" +
            "void main() {\n" +
            "  gl_Position = vec4(aPosition,1); \n" +
            "  vTexCoord = aTexCoord; \n" +
            "}";

    protected final String BASIC_FRAGMENT_SHADER =
            "precision mediump float; \n" +
            "varying vec2 vTexCoord; \n" +
            "uniform sampler2D sTexture; \n" +
            "void main() { \n" +
            "   gl_FragColor = texture2D(sTexture, vTexCoord); \n" +
            "}";

    protected String mVertexShader, mFragShader;

    protected float[] mVertex;
    protected float[] mTexCoord;

    protected FloatBuffer mVertexBuffer;
    protected FloatBuffer mTexCoordBuffer;

    protected int mProgram;
    protected int mPosHandle;
    protected int mTexCoordHandle;
    protected int mTexSamplerHandler;

    protected final LinkedList<Runnable> mRunPreDraw;

    protected boolean mIsInit;

    public GLFilter() {
        mVertexShader = BASIC_VERTEX_SHADER;
        mFragShader = BASIC_FRAGMENT_SHADER;
        mRunPreDraw = new LinkedList<>();
    }

    public GLFilter(String vertexShader, String fragShader) {
        if (vertexShader != null && !vertexShader.equals(""))
            mVertexShader = vertexShader;
        else
            mVertexShader = BASIC_VERTEX_SHADER;
        if (fragShader != null && !fragShader.equals(""))
            mFragShader = fragShader;
        else
            mFragShader = BASIC_VERTEX_SHADER;
        mRunPreDraw = new LinkedList<>();
    }

    /**
     * don't override this method
     * do what you need in onInit()
     */
    public final void init() {
        if (!mIsInit) {
            initShader();
            onInit();
            mIsInit = true;
        }
    }

    protected void initShader(){
        mProgram = ShaderUtil.createProgram(mVertexShader, mFragShader);
        //get vertex coordinate's handler
        mPosHandle = GLES20.glGetAttribLocation(mProgram, "aPosition");
        //get texure coordinate's handler
        mTexCoordHandle= GLES20.glGetAttribLocation(mProgram, "aTexCoord");
        //get sampler's handler
        mTexSamplerHandler = GLES20.glGetUniformLocation(mProgram, "sTexture");
    }

    protected void onInit() {

    }

    public void draw(int textureId, FloatBuffer vertexBuffer, FloatBuffer textureBuffer) {
        GLES20.glUseProgram(mProgram);

        vertexBuffer.position(0);
        textureBuffer.position(0);

        //transfer vertex coordinate data
        GLES20.glVertexAttribPointer(mPosHandle, 3, GLES20.GL_FLOAT, false,
                3*4, vertexBuffer);
        //transfer texture coordinate data
        GLES20.glVertexAttribPointer(mTexCoordHandle, 2, GLES20.GL_FLOAT, false,
                2*4, textureBuffer);

        GLES20.glEnableVertexAttribArray(mPosHandle);
        GLES20.glEnableVertexAttribArray(mTexCoordHandle);

        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId);
        GLES20.glUniform1i(mTexSamplerHandler, 0);

        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, 3);

        GLES20.glDisableVertexAttribArray(mPosHandle);
        GLES20.glDisableVertexAttribArray(mTexCoordHandle);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);
    }

    public void destroy() {}

}
