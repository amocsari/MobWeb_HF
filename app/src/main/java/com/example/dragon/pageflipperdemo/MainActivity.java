package com.example.dragon.pageflipperdemo;

import android.app.Activity;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.view.View;

import com.example.dragon.pageflipperdemo.PdfHandling.LoadBitmapTask;

/**
 * lapozást megjelenítő activity
 * kezeli a különböző eseményeket
 */
public class MainActivity extends Activity implements OnGestureListener {

    PageFlipView mPageFlipView;
    GestureDetector mGestureDetector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mPageFlipView = new PageFlipView(this);
        setContentView(mPageFlipView);
        mGestureDetector = new GestureDetector(this, this);

        mPageFlipView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_FULLSCREEN |
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE |
                View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION |
                View.SYSTEM_UI_FLAG_IMMERSIVE |
                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
    }

    @Override
    protected void onResume() {
        super.onResume();

        LoadBitmapTask.get(this).start();
        mPageFlipView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();

        mPageFlipView.onPause();
        LoadBitmapTask.get(this).stop();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_UP) {
            mPageFlipView.onFingerUp(event.getX(), event.getY());
            return true;
        }

        return mGestureDetector.onTouchEvent(event);
    }

    @Override
    public boolean onDown(MotionEvent e) {
        mPageFlipView.onFingerDown(e.getX(), e.getY());
        return true;
    }

    @Override
    public void onShowPress(MotionEvent e) {
    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        mPageFlipView.onFingerMove(e2.getX(), e2.getY());
        return true;
    }

    @Override
    public void onLongPress(MotionEvent e) {
    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        return false;
    }
}
