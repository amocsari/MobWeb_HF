package com.example.dragon.pageflipperdemo.PdfHandling;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

import com.example.dragon.pageflipperdemo.Exception.PdfNotOpenException;
import com.example.dragon.pageflipperdemo.Exception.PdfPageNotFoundException;
import com.sun.pdfview.PDFFile;
import com.sun.pdfview.PDFPage;

import net.sf.andpdf.nio.ByteBuffer;

import java.io.IOException;
import java.io.InputStream;

public class PdfHandler {

    private static PdfHandler __object;

    private final String TAG = "PfdHandler";

    private Context mContext;
    private PDFFile mPDFFile;
    private PDFPage mPDFPage;

    public static PdfHandler get(Context context) {
        if (__object == null) {
            __object = new PdfHandler(context);
        }

        return __object;
    }

    private PdfHandler(Context context) {
        mContext = context;
    }

    public void openPdf(int resourceId) throws IOException {
        InputStream inputStream = mContext.getResources().openRawResource(resourceId);
        byte[] bytes = new byte[inputStream.available()];
        ByteBuffer byteBuffer = ByteBuffer.NEW(bytes);

        mPDFFile = new PDFFile(byteBuffer);
    }

    public void activatePage(int pageNo) throws PdfNotOpenException, PdfPageNotFoundException {
        if(mPDFFile ==  null){
            //TODO: logging
            Log.e(TAG, "Pdf not open when activatePage was called");
            throw new PdfNotOpenException();
        }

        try {
            mPDFPage = mPDFFile.getPage(pageNo);
        }catch (Exception e){
            //TODO: logging
            Log.e(TAG, "Cannot get page from PDF");
            throw new PdfPageNotFoundException(pageNo,e);
        }
        if (mPDFPage == null){
            //TODO: logging
            Log.e(TAG, "Page not open after getPage");
            throw new PdfPageNotFoundException(pageNo);
        }
    }

    public Bitmap getPageAsBitmap() throws PdfNotOpenException, PdfPageNotFoundException {
        if (mPDFFile == null) {
            throw new PdfNotOpenException();
        }
        if(mPDFPage == null){
            throw new PdfPageNotFoundException();
        }

        return mPDFPage.getImage((int)mPDFPage.getWidth(), (int)mPDFPage.getHeight(), null);
    }
}
