package com.lyzirving.modelviewer.model.draw;

import android.opengl.GLES20;

import com.lyzirving.modelviewer.model.data.MtlInfo;
import com.lyzirving.modelviewer.model.data.Obj3d;
import com.lyzirving.modelviewer.model.data.ObjGroup;
import com.lyzirving.modelviewer.util.AppContext;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.List;
import java.util.StringTokenizer;

public class ObjFilter extends GLFilter {

    /**
     * the light model contains ambient light, diffuse light and specular light;
     *
     * note that when we calculate the intersected angle between normal and light vector, we have to
     * set the dot value minus;
     *
     */
    private static final String DEFAULT_MODEL_VERTEX_SHADER =
            "uniform mat4 uMVPMatrix;\n" +
            "uniform mat4 uMMatrix; \n" +
            "uniform vec3 uLightLocation; \n" +
            "uniform vec3 aAmbient; \n" +
            "uniform vec3 uDiffuse; \n" +
            "attribute vec3 aPosition;\n" +
            "attribute vec2 aTexCoord; \n" +
            "attribute vec3 aNormal; \n" +
            "varying vec2 vTexCoord; \n" +
            "varying vec4 vAmbient; \n" +
            "varying vec4 vDiffuse; \n" +
            "void main() {\n" +
            "  gl_Position = uMVPMatrix * vec4(aPosition,1); \n" +
            "  vTexCoord = aTexCoord; \n" +
            "  \n" +
            "  vec3 transformedNormal = normalize((uMMatrix * vec4(aNormal, 0.0)).xyz); \n" +
            "  vec3 lightVector= normalize(uLightLocation - (uMMatrix * vec4(aPosition,1)).xyz); \n" +
            "  float diffuseFactor = max(0.1, -dot(transformedNormal, lightVector)); \n" +
            "  vDiffuse = vec4((uDiffuse * diffuseFactor).xyz, 1); \n" +
            "  \n" +
            "  vAmbient = vec4(aAmbient, 1); \n" +
            "}";

    private static final String DEFAULT_MODEL_FRAGMENT_SHADER =
            "precision mediump float;\n" +
            "varying vec2 vTexCoord; \n" +
            "uniform sampler2D sTexture; \n" +
            "varying vec4 vAmbient; \n" +
            "varying vec4 vDiffuse; \n" +
            "void main() \n" +
            "{\n" +
            "  vec4 finalColor = texture2D(sTexture, vTexCoord);\n" +
            "  gl_FragColor = finalColor * (vDiffuse + vAmbient); \n" +
            "}";

    private int mMvpHandler;
    private int mMMatrixHandler;
    private int mLightLocationHandler;
    private int mNormalHandler;
    private int mAmbientHandler;
    private int mDiffuseHandler;

    private Obj3d mObj3d;

    private FloatBuffer[] mVertexBufferArray;
    private FloatBuffer[] mTexCoordBufferArray;
    private FloatBuffer[] mNormalBufferArray;
    private float[][] mAmbientArray;
    private float[][] mDiffuseArray;
    private int[] mVertexNumArray;
    private int[] mTexIds;

    public ObjFilter(Obj3d obj3d) {
        super(DEFAULT_MODEL_VERTEX_SHADER, DEFAULT_MODEL_FRAGMENT_SHADER);
        mObj3d = obj3d;
    }

    @Override
    protected void initShader(){
        mProgram = ShaderUtil.createProgram(mVertexShader, mFragShader);
        mPosHandle = GLES20.glGetAttribLocation(mProgram, "aPosition");
        mNormalHandler = GLES20.glGetAttribLocation(mProgram, "aNormal");
        mAmbientHandler = GLES20.glGetUniformLocation(mProgram, "aAmbient");
        mDiffuseHandler = GLES20.glGetUniformLocation(mProgram, "uDiffuse");
        mMvpHandler = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");
        mMMatrixHandler = GLES20.glGetUniformLocation(mProgram, "uMMatrix");
        mLightLocationHandler = GLES20.glGetUniformLocation(mProgram, "uLightLocation");
        mTexCoordHandle= GLES20.glGetAttribLocation(mProgram, "aTexCoord");
        mTexSamplerHandler = GLES20.glGetUniformLocation(mProgram, "sTexture");
    }

    @Override
    protected void onInit() {
        initVertex();
        initTexture();
        initTextureCoord();
        initNormal();
        initLightAffect();
    }

    private void initVertex() {
        List<ObjGroup> groups = mObj3d.getGroups();
        mVertexNumArray = new int[groups.size()];
        mVertexBufferArray = new FloatBuffer[groups.size()];

        ObjGroup tmp;
        ByteBuffer vbb;
        FloatBuffer vertexBuffer;
        for (int i = 0; i < groups.size(); i++) {
            tmp = groups.get(i);
            vbb = ByteBuffer.allocateDirect(tmp.getVertex().length * 4);
            vbb.order(ByteOrder.nativeOrder());
            vertexBuffer = vbb.asFloatBuffer();
            vertexBuffer.put(tmp.getVertex());
            vertexBuffer.position(0);

            mVertexBufferArray[i] = vertexBuffer;
            mVertexNumArray[i] = tmp.getVertex().length / 3;
        }
    }

    private void initTextureCoord() {
        List<ObjGroup> groups = mObj3d.getGroups();
        mTexCoordBufferArray = new FloatBuffer[groups.size()];

        ObjGroup tmp;
        ByteBuffer vbb;
        FloatBuffer texCoordBuffer;
        for (int i = 0; i < groups.size(); i++) {
            tmp = groups.get(i);
            vbb = ByteBuffer.allocateDirect(tmp.getTexCoord().length * 4);
            vbb.order(ByteOrder.nativeOrder());
            texCoordBuffer = vbb.asFloatBuffer();
            texCoordBuffer.put(tmp.getTexCoord());
            texCoordBuffer.position(0);

            mTexCoordBufferArray[i] = texCoordBuffer;
        }
    }

    private void initNormal() {
        List<ObjGroup> groups = mObj3d.getGroups();
        mNormalBufferArray = new FloatBuffer[groups.size()];

        ObjGroup tmp;
        ByteBuffer vbb;
        FloatBuffer normalBuffer;
        for (int i = 0; i < groups.size(); i++) {
            tmp = groups.get(i);
            vbb = ByteBuffer.allocateDirect(tmp.getVertexNormal().length * 4);
            vbb.order(ByteOrder.nativeOrder());
            normalBuffer = vbb.asFloatBuffer();
            normalBuffer.put(tmp.getVertexNormal());
            normalBuffer.position(0);

            mNormalBufferArray[i] = normalBuffer;
        }
    }

    private void initLightAffect() {
        List<ObjGroup> groups = mObj3d.getGroups();
        mAmbientArray = new float[groups.size()][3];
        mDiffuseArray = new float[groups.size()][3];

        ObjGroup tmp;
        float[] tmpVal;
        for (int i = 0; i < groups.size(); i++) {
            tmp = groups.get(i);
            tmpVal = tmp.getMtlInfo().getKa();//ambient color
            mAmbientArray[i] = tmpVal;

            tmpVal = tmp.getMtlInfo().getKd();//diffuse color
            mDiffuseArray[i] = tmpVal;
        }
    }

    private void initTexture() {
        List<ObjGroup> groups = mObj3d.getGroups();
        mTexIds = new int[groups.size()];

        ObjGroup tmp;
        MtlInfo info;
        int id;
        for (int i = 0; i < groups.size(); i++) {
            tmp = groups.get(i);
            info = tmp.getMtlInfo();
            String texName = info.getMapKd();
            if (texName != null && !texName.equals("")) {
                if (texName.contains(".")) {
                    StringTokenizer st = new StringTokenizer(texName, ".");
                    texName = st.nextToken();
                }
                id = AppContext.get().getIdFromName(texName, "raw");
                mTexIds[i] = TextureUtil.get().createTexture(id);
            }
        }
    }

    public void draw(int textureId) {
        GLES20.glUseProgram(mProgram);

        GLES20.glUniformMatrix4fv(mMvpHandler, 1, false, MatrixState.get().getFinalMatrix(), 0);
        GLES20.glUniformMatrix4fv(mMMatrixHandler, 1, false, MatrixState.get().getMMatrix(), 0);
        GLES20.glUniform3f(mLightLocationHandler,
                MatrixState.get().getLightLocation()[0],
                MatrixState.get().getLightLocation()[1],
                MatrixState.get().getLightLocation()[2]);

        for (int i = 0; i < mObj3d.getGroups().size(); i++) {

            GLES20.glVertexAttribPointer(mPosHandle, 3, GLES20.GL_FLOAT,
                    false, 3 * 4, mVertexBufferArray[i]);
            GLES20.glVertexAttribPointer(mTexCoordHandle, 2, GLES20.GL_FLOAT, false,
                    2 * 4, mTexCoordBufferArray[i]);
            GLES20.glVertexAttribPointer(mNormalHandler, 3, GLES20.GL_FLOAT,
                    false, 3 * 4, mNormalBufferArray[i]);
            GLES20.glUniform3f(mAmbientHandler, mAmbientArray[i][0], mAmbientArray[i][1], mAmbientArray[i][2]);
            GLES20.glUniform3f(mDiffuseHandler, mDiffuseArray[i][0], mDiffuseArray[i][1], mDiffuseArray[i][2]);

            GLES20.glEnableVertexAttribArray(mPosHandle);
            GLES20.glEnableVertexAttribArray(mTexCoordHandle);
            GLES20.glEnableVertexAttribArray(mNormalHandler);

            GLES20.glActiveTexture(GLES20.GL_TEXTURE0 + i);
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTexIds[i]);
            GLES20.glUniform1i(mTexSamplerHandler, i);

            GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, mVertexNumArray[i]);
        }

        GLES20.glDisableVertexAttribArray(mPosHandle);
        GLES20.glDisableVertexAttribArray(mNormalHandler);
        GLES20.glDisableVertexAttribArray(mTexCoordHandle);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);
    }

    @Override
    public void destroy() {
        if (mObj3d != null)
            mObj3d = null;
        for (FloatBuffer tmpVBuffer : mVertexBufferArray)
            tmpVBuffer.clear();
        for (FloatBuffer tmpTBuffer : mTexCoordBufferArray)
            tmpTBuffer.clear();
        for (FloatBuffer tmpNBuffer : mNormalBufferArray)
            tmpNBuffer.clear();
        if (mTexIds != null && mTexIds.length > 0) {
            for (int id : mTexIds)
                GLES20.glDeleteTextures(1, new int[]{id}, 0);
        }
    }

}
