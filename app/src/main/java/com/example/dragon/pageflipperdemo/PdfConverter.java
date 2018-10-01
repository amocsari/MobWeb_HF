package com.example.dragon.pageflipperdemo;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import org.apache.pdfbox.pdfparser.NonSequentialPDFParser;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.util.ImageIOUtil;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;


public class PdfConverter {

    NonSequentialPDFParser mPDFParser;
    Bitmap mBitmap;

    public void openPdf(InputStream inputStream) {
        try {
            mPDFParser = new NonSequentialPDFParser(inputStream);
        } catch (Exception e) {
            Log.e("PdfConverter", "Failed to open pdf");
            e.printStackTrace();
        }
    }

    public Bitmap getPage(int pageNumber) {
        PDPage page;
        try {
            page = mPDFParser.getPage(pageNumber);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            baos.flush();
            ImageIOUtil.writeImage(page.convertToImage(), "png", baos);
            byte[] imageInByte = baos.toByteArray();
            mBitmap = BitmapFactory.decodeByteArray(imageInByte, 0, imageInByte.length);

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        if (mBitmap == null) {
            return null;
        }

        return mBitmap;
    }
}
