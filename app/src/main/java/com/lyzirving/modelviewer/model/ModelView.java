package com.lyzirving.modelviewer.model;

import android.content.Context;
import android.graphics.PixelFormat;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;

import com.lyzirving.modelviewer.model.data.Obj3d;

public class ModelView extends GLSurfaceView implements ModelManager.ModelObserver {

    private ModelRenderer mRenderer;

    public ModelView(Context context) {
        this(context, null);
    }

    public ModelView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initGL();
        ModelManager.get().registerObserver(this);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (mRenderer != null)
            mRenderer.destroy();
        ModelManager.get().destroy();
    }

    @Override
    public void onModelAdd(Obj3d obj3d) {
        if (mRenderer != null) {
            mRenderer.onModelAdd(obj3d);
            requestRender();
        }
    }

    private void initGL() {
        setEGLContextClientVersion(2);
        setEGLConfigChooser(8,8,8,8,16,0);
        mRenderer = new ModelRenderer(this);
        setRenderer(mRenderer);
        setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
        getHolder().setFormat(PixelFormat.TRANSLUCENT);
    }

}
