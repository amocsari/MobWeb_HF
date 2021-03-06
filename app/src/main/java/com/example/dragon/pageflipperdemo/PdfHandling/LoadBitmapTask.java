package com.example.dragon.pageflipperdemo.PdfHandling;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.util.Log;

import com.example.dragon.pageflipperdemo.R;

import java.util.LinkedList;

/**
 * bitmap folyamatos beöltéséért felelős osztály
 */
public final class LoadBitmapTask implements Runnable {

    //TODO: külön memory barát verzió, ahol nem cache-eli be a teljes könyvet, hanem mindig kiolvassa az aktuálisat (lassabb lehet)
    //TODO: külön performance barát verzió, ahol be-cache-eli a teljes könyvet (memóriahasználata rosszabb lehet)


    //TODO: más konvencióval megcsinálni (vagy kivezetni egy factory-ba)
    private static LoadBitmapTask __object;

    private final static int BG_SIZE_SMALL = 0;
    private final static int BG_SIZE_MEDIUM = 1;
    private final static int BG_SIZE_LARGE = 2;

    //TODO: nem konstans
    final static int BG_COUNT = 10;

    private int mWidth;
    private int mBGSize;
    private int mQueueMaxSize;
    private boolean mIsLandScape;
    private boolean mStop;
    private Resources mResources;
    private Thread mThread;
    private LinkedList<Bitmap> mQueue;
    private Context mContext;

    /**
     * singleton scope
     * @param context
     * @return
     */
    public static LoadBitmapTask get(Context context) {
        if (__object == null) {
            __object = new LoadBitmapTask(context);
        }

        return __object;
    }

    private LoadBitmapTask(Context context) {
        //TODO: context csak a mock-hoz kell
        mContext = context;
        mResources = context.getResources();
        mBGSize = BG_SIZE_SMALL;
        mStop = false;
        mThread = null;
        mIsLandScape = false;

        //TODO: macCache-t átgondolni
        mQueueMaxSize = 1;
        mQueue = new LinkedList<Bitmap>();

        //TODO: init drawables
    }

    /**
     * visszaadja a következő bitmapt-t
     * @return
     */
    public Bitmap getBitmap() {
        Bitmap bitmap = null;
        synchronized (this) {
            if (mQueue.size() > 0) {
                bitmap = mQueue.pop();
            }

            notify();
        }

        if (bitmap == null) {
            bitmap = getNextBitmap();
        }

        return bitmap;
    }

    /**
     * betölti a következő bitmap-t
     * @return
     */
    private Bitmap getNextBitmap() {



        //TODO: (MOCK) implementálni
        Bitmap bitmap = BitmapFactory.decodeResource(mResources, R.drawable.sample);

        if (mIsLandScape) {
            Matrix matrix = new Matrix();
            matrix.postRotate(90);
            //TODO: filtert megnézni
            Bitmap leftBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
            bitmap.recycle();
            return leftBitmap;
        }
        return bitmap;
    }

    public boolean isRunning() {
        return mThread != null && mThread.isAlive();
    }

    /**
     * elindítja a töltő szálat
     */
    public synchronized void start() {
        if (mThread == null || !mThread.isAlive()) {
            mStop = false;
            mThread = new Thread(this);
            mThread.start();
        }
    }

    /**
     * leállítja a töltőszálat
     */
    public void stop() {
        synchronized (this) {
            mStop = true;
            notify();
        }

        for (int i = 0; i < 3 && mThread.isAlive(); i++) {
            //TODO: más logolás
            Log.d("LoadBitmapTask", "Waiting for thread to stop");
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
            }
        }

        if (mThread.isAlive()) {
            //TODO: más logolás
            Log.d("LoadBitMapTask", "Thread still alive after attempt to stop it");
        }
    }

    /**
     * képernyőméret alapján méret és orientáció megválasztása
     * @param w
     * @param h
     * @param maxCached
     */
    public void set(int w, int h, int maxCached) {
        mWidth = w;
        int newIndex = BG_SIZE_LARGE;

        if ((w <= 480 && h <= 8524) ||
                (w <= 854 && h <= 480)) {
            newIndex = BG_SIZE_SMALL;
        } else if ((w <= 800 && h <= 1280) ||
                (h <= 800 && w <= 1280)) {
            newIndex = BG_SIZE_MEDIUM;
        }

        mIsLandScape = w > h;

        if (maxCached != mQueueMaxSize) {
            mQueueMaxSize = maxCached;
        }

        if (newIndex != mBGSize) {
            mBGSize = newIndex;
            synchronized (this) {
                cleanQueue();
                notify();
            }
        }
    }

    /**
     * sor törélse
     */
    private void cleanQueue() {
        for (int i = 0; i < mQueue.size(); i++) {
            mQueue.get(i).recycle();
        }

        mQueue.clear();
    }

    /**
     * bitmap betöltéséért feleős futás
     */
    public void run() {
        //TODO: break helyett while(!mStop) és cleanQueue a cikluson kívül
        while (true) {
            synchronized (this) {
                if (mStop) {
                    cleanQueue();
                    break;
                }

                int size = mQueue.size();
                if (size < 1) {
                    for (int i = 0; i < mQueueMaxSize; i++) {
                        Log.d("LoadBitMapTask", "Load Queue " + i + " in background");
                        mQueue.push(getNextBitmap());
                    }
                }

                try {
                    wait();
                } catch (InterruptedException e) {
                }
            }
        }
    }
}
