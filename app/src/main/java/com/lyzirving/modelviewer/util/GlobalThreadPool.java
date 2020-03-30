package com.lyzirving.modelviewer.util;

import com.lyzirving.modelviewer.model.ModelLoader;
import com.lyzirving.modelviewer.model.ModelManager;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class GlobalThreadPool {

    private ExecutorService mExecutor;

    private static class GlobalThreadPoolWrapper {
        private static GlobalThreadPool mInstance = new GlobalThreadPool();
    }

    private GlobalThreadPool() {
        mExecutor = Executors.newCachedThreadPool();
    }

    public static GlobalThreadPool get() {
        return GlobalThreadPoolWrapper.mInstance;
    }

    public void doInbackground(Runnable r) {
        mExecutor.submit(r);
    }

    public void runLoadObjTask(final int id) {
        Runnable r = new Runnable() {
            @Override
            public void run() {
                ModelManager.get().appendData( ModelLoader.get().loadFromAssets(id));
            }
        };
        doInbackground(r);
    }

}
