package com.lyzirving.modelviewer.model.draw;

import android.opengl.GLES20;

import com.lyzirving.modelviewer.model.data.Obj3d;
import com.lyzirving.modelviewer.util.AppContext;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class ObjFilter extends GLFilter {

    private static final String DEFAULT_MODEL_VERTEX_SHADER =
            "uniform mat4 uMVPMatrix;\n" +
            "attribute vec3 aPosition;\n" +
            "attribute vec2 aTexCoord; \n" +
            "varying vec2 vTexCoord; \n" +
            "void main() {\n" +
            "  gl_Position = uMVPMatrix * vec4(aPosition,1); \n" +
            "  vTexCoord = aTexCoord; \n" +
            "}";

    private static final String DEFAULT_MODEL_FRAGMENT_SHADER =
            "precision mediump float;\n" +
            "varying vec2 vTexCoord; \n" +
            "uniform sampler2D sTexture; \n" +
            "void main() \n" +
            "{\n" +
            "   vec4 finalColor = texture2D(sTexture, vTexCoord);\n" +
            "   gl_FragColor = finalColor;\n" +
            "}";

    private int mMvpHandler;

    private Obj3d mObj3d;

    private int mVertexCount;

    private int mTextureId;

    public ObjFilter(Obj3d obj3d) {
        super(DEFAULT_MODEL_VERTEX_SHADER, DEFAULT_MODEL_FRAGMENT_SHADER);
        mObj3d = obj3d;
        mTextureId = TextureUtil.NO_TEXTURE;
    }

    @Override
    protected void initShader(){
        mProgram = ShaderUtil.createProgram(mVertexShader, mFragShader);
        mPosHandle = GLES20.glGetAttribLocation(mProgram, "aPosition");
        mMvpHandler= GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");
        mTexCoordHandle= GLES20.glGetAttribLocation(mProgram, "aTexCoord");
        mTexSamplerHandler = GLES20.glGetUniformLocation(mProgram, "sTexture");
    }

    @Override
    protected void onInit() {
        initVertex();
        initTextureCoord();
        initTexture();
    }

    private void initVertex() {
        mVertex = mObj3d.getVertex();
        mVertexCount = mVertex.length / 3;
        //create buffer for vertex coordinate
        ByteBuffer vbb = ByteBuffer.allocateDirect(mVertex.length * 4);
        //set local system'byte-order as the byte-order
        vbb.order(ByteOrder.nativeOrder());
        mVertexBuffer = vbb.asFloatBuffer();
        //fill the data
        mVertexBuffer.put(mVertex);
        mVertexBuffer.position(0);
    }

    private void initTextureCoord() {
        mTexCoord = mObj3d.getTextureCoord();
        ByteBuffer vbb = ByteBuffer.allocateDirect(mTexCoord.length * 4);
        vbb.order(ByteOrder.nativeOrder());
        mTexCoordBuffer = vbb.asFloatBuffer();
        mTexCoordBuffer.put(mTexCoord);
        mTexCoordBuffer.position(0);
    }

    private void initTexture() {
        String name = mObj3d.getTextureName();
        if (name != null && !name.equals("")) {
            int id = AppContext.get().getIdFromName(name, "raw");
            mTextureId = TextureUtil.get().createTexture(id);
        }
    }

    public void draw(int textureId) {
        GLES20.glUseProgram(mProgram);

        GLES20.glVertexAttribPointer(mPosHandle, 3, GLES20.GL_FLOAT, false,
                3 * 4, mVertexBuffer);
        GLES20.glVertexAttribPointer(mTexCoordHandle, 2, GLES20.GL_FLOAT, false,
                2 * 4, mTexCoordBuffer);
        GLES20.glUniformMatrix4fv(mMvpHandler, 1, false, MatrixState.get().getFinalMatrix(), 0);

        GLES20.glEnableVertexAttribArray(mPosHandle);
        GLES20.glEnableVertexAttribArray(mTexCoordHandle);

        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTextureId);
        GLES20.glUniform1i(mTexSamplerHandler, 0);

        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, mVertexCount);

        GLES20.glDisableVertexAttribArray(mPosHandle);
        GLES20.glDisableVertexAttribArray(mTexCoordHandle);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);
    }

    @Override
    public void destroy() {
        if (mObj3d != null)
            mObj3d = null;
        if (mVertex != null)
            mVertex = null;
        if (mVertexBuffer != null) {
            mVertexBuffer.clear();
            mVertexBuffer = null;
        }
        if (mTextureId != TextureUtil.NO_TEXTURE) {
            GLES20.glDeleteTextures(1, new int[]{mTextureId}, 0);
            mTextureId = TextureUtil.NO_TEXTURE;
        }
    }

}
