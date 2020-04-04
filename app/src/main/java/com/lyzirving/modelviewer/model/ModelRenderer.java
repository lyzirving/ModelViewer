package com.lyzirving.modelviewer.model;

import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.util.Log;

import com.lyzirving.modelviewer.model.data.Obj3d;
import com.lyzirving.modelviewer.model.draw.GLFilter;
import com.lyzirving.modelviewer.model.draw.ObjFilter;
import com.lyzirving.modelviewer.model.draw.TextureUtil;
import com.lyzirving.modelviewer.util.GlobalThreadPool;
import com.lyzirving.modelviewer.model.draw.MatrixState;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class ModelRenderer implements GLSurfaceView.Renderer, ModelManager.ModelObserver {
    private static final int INVALID_SIZE = -1;

    private int mViewWidth = INVALID_SIZE, mViewHeight = INVALID_SIZE;

    private GLSurfaceView mGLSurfaceView;

    private GLFilter mContent;
    private float mRotation;

    private ReentrantLock mLock;
    private Condition mNotEmpty;
    private Queue<Runnable> mRunPreDraw;

    public ModelRenderer(GLSurfaceView view) {
        super();
        mRunPreDraw = new LinkedList<>();
        mLock = new ReentrantLock();
        mNotEmpty = mLock.newCondition();
        mGLSurfaceView = view;
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        GLES20.glEnable(GLES20.GL_DEPTH_TEST);
        //white background
        GLES20.glClearColor(1f,1f,1f, 1f);
        GlobalThreadPool.get().runLoadObjTask("pikachu.obj");
        MatrixState.get().setLightLocation(3, 0, 3);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        boolean isViewChanged = (width != mViewWidth || height != mViewHeight);
        if (isViewChanged) {
            mViewWidth = width;
            mViewHeight = height;
        }
        //let the window fill the whole view
        GLES20.glViewport(0, 0, width, height);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        /**
         * clear the color, so the view can display the color wo set by glClearColor()
         * clear the buffered depth
         */
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);

        if (mContent == null) {
            mLock.lock();
            try {//waiting in rendering thread won't bother.
                Log.d("test", "onDrawFrame: wait until we get model");
                mNotEmpty.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                mLock.unlock();
            }
        }

        runPreDraw();

        MatrixState.get().pushMatrix();

        MatrixState.get().translate(0, -0.5f, 0);
        MatrixState.get().rotate(mRotation, 0, 1, 0);

        if (mContent instanceof ObjFilter)
            ((ObjFilter) mContent).draw(TextureUtil.NO_TEXTURE);

        MatrixState.get().popMatrix();

        mRotation += 1;
        if (mRotation >= 360)
            mRotation = 0;
        mGLSurfaceView.requestRender();
    }

    @Override
    public void onModelAdd(Obj3d obj3d) {
        if (mContent == null) {
            mLock.lock();
            try {
                mContent = new ObjFilter(obj3d);
                mRunPreDraw.add(new Runnable() {
                    @Override
                    public void run() {
                        mContent.init();
                    }
                });
                mNotEmpty.signalAll();
                Log.d("test", "onModelAdd: content init for the first time");
            }  finally {
                mLock.unlock();
            }
        } else {
            mContent = new ObjFilter(obj3d);
            mRunPreDraw.add(new Runnable() {
                @Override
                public void run() {
                    mContent.init();
                }
            });
        }
    }

    public void destroy() {
        if (mContent != null)
            mContent.destroy();
        if (mGLSurfaceView != null)
            mGLSurfaceView = null;
    }

    private void runPreDraw() {
        if (mRunPreDraw != null && mRunPreDraw.size() > 0) {
            synchronized (mRunPreDraw) {
                Runnable task;
                while ((task = mRunPreDraw.poll()) != null) {
                    task.run();
                }
            }
        }
    }

}
