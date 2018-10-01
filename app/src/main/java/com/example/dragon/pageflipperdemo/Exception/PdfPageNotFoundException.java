package com.example.dragon.pageflipperdemo.Exception;

public class PdfPageNotFoundException extends Exception {
    private Integer mPageNumber;
    private Exception mInnerException;

    public PdfPageNotFoundException(){}

    public PdfPageNotFoundException(Integer pageNumber){
        mPageNumber =  pageNumber;
    }

    public PdfPageNotFoundException(Integer pageNumber, Exception innerException){
        mPageNumber =  pageNumber;
        mInnerException = innerException;
    }

    public Integer getPageNumber() {
        return mPageNumber;
    }

    public Exception getInnerException() {
        return mInnerException;
    }
}
