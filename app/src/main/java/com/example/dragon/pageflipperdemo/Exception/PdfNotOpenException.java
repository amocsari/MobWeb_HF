package com.example.dragon.pageflipperdemo.Exception;

public class PdfNotOpenException extends Exception {
    private Exception mInnerException;

    public PdfNotOpenException(Exception innerException){
        mInnerException = innerException;
    }

    public PdfNotOpenException(){
        mInnerException = null;
    }

    public Exception getInnerException() {
        return mInnerException;
    }
}
