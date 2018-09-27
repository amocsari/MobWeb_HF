package com.example.dragon.pageflipperdemo.PageRender;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.Handler;

import com.eschao.android.widget.pageflip.OnPageFlipListener;
import com.eschao.android.widget.pageflip.PageFlip;
import com.example.dragon.pageflipperdemo.Common.Keys;



//TODO: listener-t kiszervezni külön osztályba
//TODO: render-t kivezetni interfészbe (onDraw minden implementációnál van, mégsincs a superclass-ban)
public abstract class PageRender implements OnPageFlipListener {

    public static final int MSG_ENDED_DRAWING_FRAME = 1;
    public static final int DRAW_MOVING_FRAME = 0;
    public static final int DRAW_ANIMATING_FRAME = 1;
    public static final int DRAW_FULL_PAGE = 2;

    Context mContext;
    Integer mPageNumber;
    //TODO: (MOCK) maxpagenumber implementálása
    Integer mMaxPageNumber = 100;
    Handler mHandler;
    PageFlip mPageFlip;
    Canvas mCanvas;
    Bitmap mBitmap;
    Bitmap mBackGround;
    Integer mDrawCommand;

    public PageRender(Context context, PageFlip pageFlip, Handler handler, Integer pageNumber) {
        mContext = context;
        mPageFlip = pageFlip;
        mHandler = handler;
        mPageNumber = pageNumber;
        mCanvas = new Canvas();
        mPageFlip.setListener(this);
        mDrawCommand = DRAW_FULL_PAGE;
    }

    public Integer getMaxPageNumber() {
        return mMaxPageNumber;
    }

    public void setMaxPageNumber(Integer maxPageNumber){
        mMaxPageNumber = maxPageNumber;
    }

    public Integer getPageNumber() {
        return mPageNumber;
    }

    public void release() {
        if (mBitmap != null) {
            mBitmap.recycle();
            mBitmap = null;
        }

        mPageFlip.setListener(null);
        mCanvas = null;
        mBackGround = null;
    }

    public boolean onFingerMove(float x, float y) {
        mDrawCommand = DRAW_MOVING_FRAME;
        return true;
    }

    public boolean onFingerUp(float x, float y) {
        if (mPageFlip.isAnimating()) {
            mDrawCommand = DRAW_ANIMATING_FRAME;
            return true;
        }
        return false;
    }

    public abstract void onDrawFrame();

    public abstract void onSurfaceChanged(Integer width, Integer height);

    public abstract boolean onEndedDrawing(Integer what);
}
