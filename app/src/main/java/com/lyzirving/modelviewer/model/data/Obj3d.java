package com.lyzirving.modelviewer.model.data;

public class Obj3d {

    private float[] mVertex;
    private float[] mTextureCoord;
    private String mTextureName;

    public void setVertex(float[] vertex) {
        mVertex = vertex;
    }

    public void setTextureCoord(float[] texCoord) {
        mTextureCoord = texCoord;
    }

    public float[] getVertex() {
        return mVertex;
    }

    public float[] getTextureCoord() {
        return mTextureCoord;
    }

    public void setTextureName(String name) {
        mTextureName = name;
    }

    public String getTextureName() {
        return mTextureName;
    }

}
