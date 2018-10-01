package com.example.dragon.pageflipperdemo;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.opengl.GLSurfaceView;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.util.AttributeSet;

import com.eschao.android.widget.pageflip.PageFlip;
import com.eschao.android.widget.pageflip.PageFlipException;
import com.example.dragon.pageflipperdemo.Common.Constants;
import com.example.dragon.pageflipperdemo.Common.Keys;
import com.example.dragon.pageflipperdemo.PageRender.DoublePageRender;
import com.example.dragon.pageflipperdemo.PageRender.PageRender;
import com.example.dragon.pageflipperdemo.PageRender.SinglePageRender;

import java.util.concurrent.locks.ReentrantLock;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

//TODO: kiszervezni a renderer-t külön osztályba
public class PageFlipView extends GLSurfaceView implements GLSurfaceView.Renderer {
    public static final String KEY_PREF_DURATION = "duration";
    public static final String KEY_PREF_MESH_PIXELS = "mesh_pixels";
    public static final String KEY_PREF_PAGE_MODE = "page_mode";


    private Integer mPageNumber;
    private Integer mAnimationDuration;
    private PageFlip mPageFlip;
    private ReentrantLock mReentrantLock;
    private Handler mHandler;
    private PageRender mPageRender;

    //TODO: menü-ben használandó
    public Integer getAnimationDuration() {
        return mAnimationDuration;
    }

    //TODO: menü-ben használandó
    public void setAnimationDuration(Integer animationDuration) {
        mAnimationDuration = animationDuration;
    }

    public PageFlipView(Context context) {
        super(context);
        initializeContext(context);
    }

    public PageFlipView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initializeContext(context);
    }

    private void initializeContext(Context context) {
        mHandler = createHandler();

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        mAnimationDuration = sharedPreferences.getInt(KEY_PREF_DURATION, Constants.DEFAULT_DURATION);
        int pixelsOfMesh = sharedPreferences.getInt(KEY_PREF_MESH_PIXELS, Constants.DEFAULT_MESH_PIXELS);
        boolean isAuto = sharedPreferences.getBoolean(KEY_PREF_PAGE_MODE, Constants.DEFAULT_PAGE_MODE);

        mPageFlip = new PageFlip(context);

        //TODO: change from default parameters
        mPageFlip.setSemiPerimeterRatio(0.8f)
                .setShadowWidthOfFoldEdges(5, 60, 0.3f)
                .setShadowWidthOfFoldBase(5, 80, 0.4f)
                .setPixelsOfMesh(pixelsOfMesh)
                .enableAutoPage(isAuto);

        //TODO: comment if allowed
        //sets the version of OpenGL
        setEGLContextClientVersion(2);

        mReentrantLock = new ReentrantLock();
        mPageNumber = 1;

        mPageRender = new SinglePageRender(context, mPageFlip, mHandler, mPageNumber);

        //sets the renderer and starts the thread associated with the view
        setRenderer(this);

        //TODO: implementálni RENDERMODE_CONTINUOUSLY-t is
        //sets the rendering mode to RENDERMODE_WHEN_DIRTY (more battery life) or to RENDERMODE_CONTINUOUSLY (more frequent updates)
        setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
    }

    //TODO: kivenni a comment-ezést, vagy törölni attól függően, hogy kell-e később
    //    public boolean isAutoPageEnabled() {
    //        return mPageFlip.isAutoPageEnabled();
    //    }

    //TODO: ha pixels of Mesh valid use-case akkor kikommentelni
    //    public int getPixelsOfMesh() {
    //        return mPageFlip.getPixelsOfMesh();
    //    }

    //TODO: ha támogatott a "csak single page view"-ból "double page view"-ba váltás akkor implementálni az enableAutoPage metódust

    public void onFingerDown(float x, float y) {
        if (!mPageFlip.isAnimating() && mPageFlip.getFirstPage() != null) {
            mPageFlip.onFingerDown(x, y);
        }
    }

    public void onFingerMove(float x, float y) {
        //TODO: megnézni, hogy kell-e az if ág
        if (mPageFlip.isAnimating()) {

        } else if (mPageFlip.canAnimate(x, y)) {
            onFingerUp(x, y);
        } else if (mPageFlip.onFingerMove(x, y)) {
            try {
                mReentrantLock.lock();
                mPageRender.onFingerMove(x, y);
                requestRender();
            } finally {
                mReentrantLock.unlock();
            }
        }

    }

    public void onFingerUp(float x, float y) {
        if (!mPageFlip.isAnimating()) {
            mPageFlip.onFingerUp(x, y, mAnimationDuration);
            try {
                mReentrantLock.lock();
                mPageRender.onFingerUp(x, y);
                requestRender();
            } finally {
                mReentrantLock.unlock();
            }
        }
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        try {
            mPageFlip.onSurfaceCreated();
        } catch (PageFlipException e) {
            //TODO: ha logoló osztály implementálva van, akkor valami értelmesebb logolás
            e.printStackTrace();
        }
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        try {
            mPageFlip.onSurfaceChanged(width, height);
            //TODO: kitalálni, hogy ezt miért kell egy külön változóban és miért nem elég az mPageNumber
            int pageNumber = mPageRender.getPageNumber();
            //TODO: width>height helyett megnézni, hogy lehet-e azt ellenőrizni, hogy landscape-e
            if (mPageFlip.getSecondPage() != null && width > height) {
                if (!(mPageRender instanceof DoublePageRender)) {
                    mPageRender.release();
                    mPageRender = new DoublePageRender(getContext(), mPageFlip, mHandler, pageNumber);
                }
            } else if (!(mPageRender instanceof SinglePageRender)) {
                mPageRender.release();
                mPageRender = new SinglePageRender(getContext(), mPageFlip, mHandler, pageNumber);
            }

            mPageRender.onSurfaceChanged(width, height);
        } catch (PageFlipException e) {
            //TODO: ha logoló osztály implementálva van, akkor valami értelmesebb logolás
            e.printStackTrace();
        }
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        try {
            mReentrantLock.lock();
            if (mPageRender != null) {
                mPageRender.onDrawFrame();
            }
        } finally {
            mReentrantLock.unlock();
        }
    }


    //TODO: megoldani a suppress-elt warningot
    // https://stackoverflow.com/questions/11407943/this-handler-class-should-be-static-or-leaks-might-occur-incominghandler
    @SuppressLint("HandlerLeak")
    private Handler createHandler() {
        return new Handler() {
            public void handleMessage(Message message) {
                if (message.what == PageRender.MSG_ENDED_DRAWING_FRAME) {
                    try {
                        mReentrantLock.lock();

                        if (mPageRender != null && mPageRender.onEndedDrawing(message.arg1)) {
                            requestRender();
                        }
                    } finally {
                        mReentrantLock.unlock();
                    }
                }
            }
        };
    }
}
