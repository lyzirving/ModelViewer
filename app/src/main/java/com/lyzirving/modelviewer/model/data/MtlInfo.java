package com.lyzirving.modelviewer.model.data;

public class MtlInfo {

    private String mName;
    private int mNs;
    private int mD;
    private int mIllum;
    private float[] mKd;
    private float[] mKs;
    private float[] mKa;
    private String mMapKd;

    public String getName() {
        return mName;
    }

    public void setName(String mName) {
        this.mName = mName;
    }

    public int getNs() {
        return mNs;
    }

    public void setNs(int mNs) {
        this.mNs = mNs;
    }

    public int getD() {
        return mD;
    }

    public void setD(int mD) {
        this.mD = mD;
    }

    public int getIllum() {
        return mIllum;
    }

    public void setIllum(int mIllum) {
        this.mIllum = mIllum;
    }

    public float[] getKd() {
        return mKd;
    }

    public void setKd(float[] mKd) {
        this.mKd = mKd;
    }

    public float[] getKs() {
        return mKs;
    }

    public void setKs(float[] mKs) {
        this.mKs = mKs;
    }

    public float[] getKa() {
        return mKa;
    }

    public void setKa(float[] mKa) {
        this.mKa = mKa;
    }

    public String getMapKd() {
        return mMapKd;
    }

    public void setMapKd(String mMapKd) {
        this.mMapKd = mMapKd;
    }

}
