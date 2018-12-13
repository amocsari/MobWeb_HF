package com.example.dragon.pageflipperdemo.PdfHandling;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.util.Log;

import com.example.dragon.pageflipperdemo.Exception.PdfNotOpenException;
import com.example.dragon.pageflipperdemo.Exception.PdfPageNotFoundException;
import com.sun.pdfview.PDFFile;
import com.sun.pdfview.PDFImage;
import com.sun.pdfview.PDFPage;
import com.sun.pdfview.PDFPaint;

import net.sf.andpdf.nio.ByteBuffer;

import java.io.IOException;
import java.io.InputStream;

/**
 * pdf-k megnyitásáért felelős osztály
 */
public class PdfHandler {

    /**
     * singleton scope
     */
    private static PdfHandler __object;

    private final String TAG = "PfdHandler";

    private Context mContext;
    private PDFFile mPDFFile;
    private PDFPage mPDFPage;
    private Integer mViewSize;

    public static PdfHandler get(Context context){
        //TODO: helyettesíteni tejes képernyő szélességével
        return get(context, 1);
    }

    public static PdfHandler get(Context context, int viewSize) {
        if (__object == null) {
            __object = new PdfHandler(context);
        }

        __object.mViewSize = viewSize;

        return __object;
    }

    private PdfHandler(Context context) {
        //TODO: context csak a mock-hoz kell
        mContext = context;

        PDFImage.sShowImages = true;

        //TODO: battery saving stuff
        PDFPaint.s_doAntiAlias = true;
    }

    public Integer getViewSize() {
        return mViewSize;
    }

    public void setViewSize(Integer mViewSize) {
        this.mViewSize = mViewSize;
    }

    /**
     * megnyit egy pdf-t (egyelőre resource alapján)
     * @param uri
     * @throws IOException
     */
    public void openPdf(Uri uri) throws IOException {
        InputStream inputStream = mContext.getContentResolver().openInputStream(uri);

        byte[] bytes = new byte[inputStream.available()];

        int offset = 0;
        int numread = 0;
        while (offset < bytes.length && (numread = inputStream.read(bytes, offset, bytes.length - offset)) >= 0) {
            offset += numread;
        }

        ByteBuffer byteBuffer = ByteBuffer.NEW(bytes);
        mPDFFile = new PDFFile(byteBuffer);
    }

    /**
     * a kapott oldalra lapoz
     * @param pageNo
     * @throws PdfNotOpenException
     * @throws PdfPageNotFoundException
     */
    @SuppressLint("StaticFieldLeak")
    public void activatePage(final int pageNo) throws PdfNotOpenException, PdfPageNotFoundException {
        if (mPDFFile == null) {
            //TODO: logging
            Log.e(TAG, "Pdf not open when activatePage was called");
            throw new PdfNotOpenException();
        }

        try {
            mPDFPage = mPDFFile.getPage(pageNo, true);
        } catch (Exception e) {
            //TODO: logging
            Log.e(TAG, "Cannot get page from PDF");
            throw new PdfPageNotFoundException(pageNo, e);
        }
        if (mPDFPage == null) {
            //TODO: logging
            Log.e(TAG, "Page not open after getPage");
            throw new PdfPageNotFoundException(pageNo);
        }

//        try {
//
//            new AsyncTask<Void, Void, Void>() {
//
//                @Override
//                protected Void doInBackground(Void... voids) {
//                    try {
//
//                        if (mPDFFile == null) {
//                            //TODO: logging
//                            Log.e(TAG, "Pdf not open when activatePage was called");
//                            throw new PdfNotOpenException();
//                        }
//
//                        try {
//                            mPDFPage = mPDFFile.getPage(pageNo, true);
//                        } catch (Exception e) {
//                            //TODO: logging
//                            Log.e(TAG, "Cannot get page from PDF");
//                            throw new PdfPageNotFoundException(pageNo, e);
//                        }
//                        if (mPDFPage == null) {
//                            //TODO: logging
//                            Log.e(TAG, "Page not open after getPage");
//                            throw new PdfPageNotFoundException(pageNo);
//                        }
//                    } catch (Exception ignored) {
//
//                    }
//                    return null;
//                }
//            }.execute();
//
//
//        } catch (Exception e) {
//        }
    }

    /**
     * visszaadja az az aktuális oldalt bitmap formátumban
     * @return
     * @throws PdfNotOpenException
     * @throws PdfPageNotFoundException
     */
    @SuppressLint("StaticFieldLeak")
    public Bitmap getPageAsBitmap() throws PdfNotOpenException, PdfPageNotFoundException {

        if (mPDFFile == null) {
            throw new PdfNotOpenException();
        }
        if (mPDFPage == null) {
            throw new PdfPageNotFoundException();
        }

        final float scale = mViewSize / mPDFPage.getWidth() * 0.95f;
        Bitmap bitmap = mPDFPage.getImage((int) (mPDFPage.getWidth() * scale), (int) (mPDFPage.getHeight() * scale), null);
        return bitmap;

//        try {
//            return new AsyncTask<Void, Void, Bitmap>(){
//
//                @Override
//                protected Bitmap doInBackground(Void... voids) {
//
//                    if (mPDFFile == null) {
//                        return null;
//                        //TODO: exception handling
////                        throw new PdfNotOpenException();
//                    }
//                    if (mPDFPage == null) {
//                        return null;
//                        //TODO: exception handling
////                        throw new PdfPageNotFoundException();
//                    }
//
//                    final float scale = mViewSize / mPDFPage.getWidth() * 0.95f;
//
//                    Bitmap bitmap = mPDFPage.getImage((int) (mPDFPage.getWidth() * scale), (int) (mPDFPage.getHeight() * scale), null, true, true);
//                    return bitmap;
//                }
//            }.execute().get();
//        } catch (InterruptedException | ExecutionException e) {
//            e.printStackTrace();
//        }
//        return null;
    }
}
