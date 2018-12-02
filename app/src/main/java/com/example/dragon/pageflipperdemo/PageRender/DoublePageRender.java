//package com.example.dragon.pageflipperdemo.PageRender;
//
//import android.content.Context;
//import android.graphics.Bitmap;
//import android.graphics.Paint;
//import android.graphics.Rect;
//import android.os.Handler;
//import android.os.Message;
//
//import com.eschao.android.widget.pageflip.Page;
//import com.eschao.android.widget.pageflip.PageFlip;
//import com.eschao.android.widget.pageflip.PageFlipState;
//import com.example.dragon.pageflipperdemo.PdfHandling.LoadBitmapTask;
//
//
//public class DoublePageRender extends PageRender {
//
//    public DoublePageRender(Context context, PageFlip pageFlip, Handler handler, int pageNumber) {
//        super(context, pageFlip, handler, pageNumber);
//    }
//
//    @Override
//    public void onDrawFrame() {
//        mPageFlip.deleteUnusedTextures();
//
//        final Page firstPage = mPageFlip.getFirstPage();
//        final Page secondPage = mPageFlip.getSecondPage();
//
//        if (!firstPage.isFirstTextureSet()) {
//            drawPage(firstPage.isLeftPage() ? mPageNumber : mPageNumber + 1);
//            firstPage.setFirstTexture(mBitmap);
//        }
//
//        if (secondPage.isFirstTextureSet()) {
//            drawPage(secondPage.isLeftPage() ? mPageNumber : mPageNumber + 1);
//            secondPage.setFirstTexture(mBitmap);
//        }
//
//        if (mDrawCommand == DRAW_MOVING_FRAME || mDrawCommand == DRAW_ANIMATING_FRAME) {
//            if (!firstPage.isBackTextureSet()) {
//                drawPage(firstPage.isLeftPage() ? mPageNumber - 1 : mPageNumber + 2);
//                firstPage.setBackTexture(mBitmap);
//            }
//
//            if (!firstPage.isSecondTextureSet()) {
//                drawPage(firstPage.isLeftPage() ? mPageNumber - 2 : mPageNumber + 3);
//                firstPage.setSecondTexture(mBitmap);
//            }
//
//            mPageFlip.drawFlipFrame();
//        } else if (mDrawCommand == DRAW_FULL_PAGE) {
//            mPageFlip.drawPageFrame();
//        }
//
//        Message message = Message.obtain();
//        message.what = MSG_ENDED_DRAWING_FRAME;
//        message.arg1 = mDrawCommand;
//        mHandler.sendMessage(message);
//    }
//
//    @Override
//    public void onSurfaceChanged(Integer width, Integer height) {
//        if (mBackGround != null) {
//            mBackGround.recycle();
//        }
//
//        if (mBitmap != null) {
//            mBitmap.recycle();
//        }
//
//        Page page = mPageFlip.getFirstPage();
//        int pageWidth = (int) page.width();
//        int pageHeight = (int) page.height();
//
//        mBitmap = Bitmap.createBitmap(pageWidth, pageHeight, Bitmap.Config.ARGB_8888);
//        mCanvas.setBitmap(mBitmap);
//        //TODO: maxCache-d átértékelni
//        LoadBitmapTask.get(mContext).set(pageWidth, pageHeight, 2);
//    }
//
//    @Override
//    public boolean onEndedDrawing(Integer what) {
//        if (what == DRAW_ANIMATING_FRAME) {
//            if (mPageFlip.animating()) {
//                mDrawCommand = DRAW_ANIMATING_FRAME;
//                return true;
//            } else {
//                if (mPageFlip.getFlipState() == PageFlipState.END_WITH_FORWARD) {
//                    final Page firstPage = mPageFlip.getFirstPage();
//                    final Page secondPage = mPageFlip.getSecondPage();
//                    secondPage.swapTexturesWithPage(firstPage);
//
//                    if (firstPage.isLeftPage()) {
//                        mPageNumber = mPageNumber - 2;
//                    } else {
//                        mPageNumber = mPageNumber + 2;
//                    }
//                }
//
//                mDrawCommand = DRAW_FULL_PAGE;
//                return true;
//            }
//        }
//        return false;
//    }
//
//    @Override
//    public boolean canFlipForward() {
//        final Page page = mPageFlip.getFirstPage();
//        if (page.isLeftPage()) {
//            return mPageNumber > 1;
//        }
//
//        return (mPageNumber + 2 <= mMaxPageNumber);
//    }
//
//    @Override
//    public boolean canFlipBackward() {
//        return false;
//    }
//
//    private void drawPage(Integer pageNumber) {
//        final int pageWidth = mCanvas.getWidth();
//        final int pageHeight = mCanvas.getHeight();
//        Paint paint = new Paint();
//        paint.setFilterBitmap(true);
//
//        Bitmap background = LoadBitmapTask.get(mContext).getBitmap();
//        Rect rect = new Rect(0, 0, pageWidth, pageHeight);
//        //TODO: landscape-t kitalálni értelmesebben
//        if (pageWidth > pageHeight) {
//            mCanvas.rotate(90);
//            mCanvas.drawBitmap(background, null, rect, paint);
//            mCanvas.rotate(-90);
//        } else {
//            mCanvas.drawBitmap(background, null, paint);
//        }
//
//        background.recycle();
//        background = null;
//    }
//}
