package com.lyzirving.modelviewer.util;

import android.content.Context;

public class AppContext {

    private static class AppContextWrapper {
        private static AppContext mInstance = new AppContext();
    }

    private Context mAppContext;

    public static AppContext get() {
        return AppContextWrapper.mInstance;
    }

    public void setAppContext(Context ctx) {
        mAppContext = ctx;
    }

    public Context getContext() {
        return mAppContext;
    }

    public int getIdFromName(String name, String type) {
        return mAppContext.getResources().getIdentifier(name, type, mAppContext.getPackageName());
    }

}
