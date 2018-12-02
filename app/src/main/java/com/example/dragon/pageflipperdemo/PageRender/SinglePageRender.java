package com.example.dragon.pageflipperdemo.PageRender;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Handler;
import android.os.Message;

import com.eschao.android.widget.pageflip.Page;
import com.eschao.android.widget.pageflip.PageFlip;
import com.eschao.android.widget.pageflip.PageFlipState;
import com.example.dragon.pageflipperdemo.PdfHandling.LoadBitmapTask;

/**
 * egy oldalas lapozást megjelenítő renderer
 */
public class SinglePageRender extends PageRender {

    public SinglePageRender(Context context, PageFlip pageFlip, Handler handler, Integer pageNumber) {
        super(context, pageFlip, handler, pageNumber);
    }

    /**
     * oldalra  rajzoláskor hívódó eseménykezelő
     * kirajzolja az oldalra a választott képet
     */
    @Override
    public void onDrawFrame() {
        mPageFlip.deleteUnusedTextures();
        //TODO: megnézni, hogy ez így jó-e?
        Page page = mPageFlip.getFirstPage();

        if (mDrawCommand == DRAW_MOVING_FRAME || mDrawCommand == DRAW_ANIMATING_FRAME) {
            if (mPageFlip.getFlipState() == PageFlipState.FORWARD_FLIP) {
                if (!page.isSecondTextureSet()) {
                    drawPage(mPageNumber + 1);
                    page.setSecondTexture(mBitmap);
                }
            } else if (!page.isFirstTextureSet()) {
                mPageNumber = mPageNumber - 1;
                drawPage(mPageNumber);
                page.setFirstTexture(mBitmap);
            }
            mPageFlip.drawFlipFrame();
        } else if (mDrawCommand == DRAW_FULL_PAGE) {
            if (!page.isFirstTextureSet()) {
                drawPage(mPageNumber);
                page.setFirstTexture(mBitmap);
            }

            mPageFlip.drawPageFrame();
        }

        Message message = Message.obtain();
        message.what = MSG_ENDED_DRAWING_FRAME;
        message.arg1 = mDrawCommand;
        mHandler.sendMessage(message);
    }

    /**
     * felület megváltozásakor hívódó eseménykezelő
     * betölti a következő képet és beállítja a méreteit
     * @param width
     * @param height
     */
    @Override
    public void onSurfaceChanged(Integer width, Integer height) {
        if (mBackGround != null) {
            mBackGround.recycle();
        }

        if (mBitmap != null) {
            mBitmap.recycle();
        }

        Page page = mPageFlip.getFirstPage();
        //TODO: ránézni bitmap config-okra
        mBitmap = Bitmap.createBitmap((int) page.width(), (int) page.height(), Bitmap.Config.ARGB_8888);
        mCanvas.setBitmap(mBitmap);

        //TODO: maxCached-t átírni
        LoadBitmapTask.get(mContext).set(width, height, 1);
    }

    /**
     * rajzolás végeztével hívódó eseménykezelő
     * az animálásért felelős
     * @param what
     * @return
     */
    @Override
    public boolean onEndedDrawing(Integer what) {
        if (what == DRAW_ANIMATING_FRAME) {
            if (mPageFlip.animating()) {
                mDrawCommand = DRAW_ANIMATING_FRAME;
                return true;
            } else {
                final PageFlipState pageFlipState = mPageFlip.getFlipState();
                //TODO: megnézni, hogy az az if ág kell-e
                if (pageFlipState == PageFlipState.END_WITH_BACKWARD) {

                } else if (pageFlipState == PageFlipState.END_WITH_FORWARD) {
                    mPageFlip.getFirstPage().setFirstTextureWithSecond();
                    mPageNumber = mPageNumber + 1;
                }
                mDrawCommand = DRAW_FULL_PAGE;
                return true;
            }
        }
        return false;
    }

    /**
     * oldal kirajzolásáért felelős függvény
     * @param pageNumber
     */
    private void drawPage(Integer pageNumber) {
        final int width = mCanvas.getWidth();
        final int height = mCanvas.getHeight();
        Paint paint = new Paint();
        paint.setFilterBitmap(true);

        Bitmap background = LoadBitmapTask.get(mContext).getBitmap();
        Rect rect = new Rect(0, 0, width, height);
        mCanvas.drawBitmap(background, null, rect, paint);
        background.recycle();
        background = null;
    }

    /**
     * oldalszám alapján eldönti, hogy a lapozás lehetséges-e
     * @return
     */
    @Override
    public boolean canFlipForward() {
        return mPageNumber < mMaxPageNumber;
    }

    /**
     * oldalszám alapján eldönti, hogy a lapozás lehetséges-e
     * @return
     */
    @Override
    public boolean canFlipBackward() {
        if (mPageNumber > 1) {
            //TODO: megnézni, hogy ez így jó-e?
            mPageFlip.getFirstPage().setSecondTextureWithFirst();
            return true;
        }
        return false;
    }
}
