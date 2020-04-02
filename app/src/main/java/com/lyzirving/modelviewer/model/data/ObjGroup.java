package com.lyzirving.modelviewer.model.data;

import java.util.ArrayList;
import java.util.List;

public class ObjGroup {

    private String mMtlName;
    private float[] mVertex;
    private float[] mTexCoord;
    private float[] mVertexNormal;
    private List<Integer> mVertexIndex;
    private List<Integer> mTexCoordIndex;
    private List<Integer> mVertexNormalIndex;
    private MtlInfo mMtlInfo;

    public ObjGroup() {
        mVertexIndex = new ArrayList<>();
        mTexCoordIndex = new ArrayList<>();
        mVertexNormalIndex = new ArrayList<>();
    }

    public String getMtlName() {
        return mMtlName;
    }

    public void setMtlName(String mtlName) {
        mMtlName = new String(mtlName);
    }

    public float[] getVertex() {
        return mVertex;
    }

    public void setVertex(float[] vertex) {
        mVertex = new float[vertex.length];
        System.arraycopy(vertex, 0, mVertex, 0, vertex.length);
    }

    public float[] getTexCoord() {
        return mTexCoord;
    }

    public void setTexCoord(float[] texCoord) {
        mTexCoord = new float[texCoord.length];
        System.arraycopy(texCoord, 0, mTexCoord, 0, texCoord.length);
    }

    public void setVertexNormal(float[] normals) {
        mVertexNormal = new float[normals.length];
        System.arraycopy(normals, 0, mVertexNormal, 0, normals.length);
    }

    public float[] getVertexNormal() { return mVertexNormal; }

    public void addVertexIndex(int index) {
        mVertexIndex.add(index);
    }

    public void addTexCoordIndex(int index) {
        mTexCoordIndex.add(index);
    }

    public void addVertexNormalIndex(int index) {
        mVertexNormalIndex.add(index);
    }

    public List<Integer> getVertexIndex() {
        return mVertexIndex;
    }

    public List<Integer> getTexCoordIndex() {
        return mTexCoordIndex;
    }

    public List<Integer> getVertexNormalIndex() { return mVertexNormalIndex; }

    public void setMtlInfo(MtlInfo info) {
        mMtlInfo = info;
    }

    public MtlInfo getMtlInfo() {
        return mMtlInfo;
    }

}
