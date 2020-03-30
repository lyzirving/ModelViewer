package com.lyzirving.modelviewer.model;

import android.util.Log;

import com.lyzirving.modelviewer.model.data.Obj3d;

import java.util.ArrayList;
import java.util.List;

public class ModelManager {

    private List<Obj3d> mObjs;

    private ModelObserver mObserver;

    private static class ModelManagerWrapper {
        private static ModelManager mInstance = new ModelManager();
    }

    private ModelManager() {
        mObjs = new ArrayList<>();
    }

    public static ModelManager get() {
        return ModelManagerWrapper.mInstance;
    }

    public void appendData(Obj3d obj3d) {
        mObjs.add(obj3d);
        if (mObserver != null)
            mObserver.onModelAdd(mObjs.get(mObjs.size() - 1));
    }

    public void registerObserver(ModelObserver o) {
        mObserver = o;
    }

    public void destroy() {
        if (mObjs != null && mObjs.size() > 0) {
            Log.d("test", "destroy " + mObjs.size() + " models");
            mObjs.clear();
        }
        if (mObserver != null)
            mObserver = null;
    }

    public interface ModelObserver {
        void onModelAdd(Obj3d obj3d);
    }

}
