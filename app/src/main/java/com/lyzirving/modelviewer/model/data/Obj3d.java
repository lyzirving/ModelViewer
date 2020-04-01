package com.lyzirving.modelviewer.model.data;

import java.util.ArrayList;
import java.util.List;

public class Obj3d {

    private List<ObjGroup> mObjGroup;

    public Obj3d() {
        mObjGroup = new ArrayList<>();
    }

    public void addGroup(ObjGroup face) {
        mObjGroup.add(face);
    }

    public List<ObjGroup> getGroups() {
        return mObjGroup;
    }

}
