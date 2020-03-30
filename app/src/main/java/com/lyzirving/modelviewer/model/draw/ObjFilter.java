package com.lyzirving.modelviewer.model.draw;

import android.opengl.GLES20;

import com.lyzirving.modelviewer.model.data.Obj3d;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class ObjFilter extends GLFilter {

    private static final String DEFAULT_MODEL_FRAGMENT_SHADER =
            "precision mediump float;\n" +
            "void main()                         \n" +
            "{\n" +
            "   vec4 finalColor=vec4(0.9,0.9,0.9,1.0);\n" +
            "   gl_FragColor = finalColor;\n" +
            "}";

    private Obj3d mObj3d;

    private int mVertexCount;

    public ObjFilter(Obj3d obj3d) {
        super(null, DEFAULT_MODEL_FRAGMENT_SHADER);
        mObj3d = obj3d;
    }

    @Override
    protected void onInit() {
        super.onInit();
        initVertex();
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

    public void draw(int textureId) {
        GLES20.glUseProgram(mProgram);

        //transfer vertex coordinate data
        GLES20.glVertexAttribPointer(mPosHandle, 3, GLES20.GL_FLOAT, false,
                3 * 4, mVertexBuffer);

        GLES20.glEnableVertexAttribArray(mPosHandle);

        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, mVertexCount);

        GLES20.glDisableVertexAttribArray(mPosHandle);
    }

}
