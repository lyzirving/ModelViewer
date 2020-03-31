package com.lyzirving.modelviewer.util;

import android.app.Application;

import com.lyzirving.modelviewer.model.ModelLoader;

public class AppApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        ModelLoader.get().setResources(getResources());
        AppContext.get().setAppContext(getApplicationContext());
    }

}
