package com.example.dragon.pageflipperdemo.Exception;

/**
 * pdf sikertelen megnyitásakor dobódó kivétel
 */
public class PdfNotOpenException extends Exception {
    private Exception mInnerException;


    public PdfNotOpenException(){
        mInnerException = null;
    }

}
