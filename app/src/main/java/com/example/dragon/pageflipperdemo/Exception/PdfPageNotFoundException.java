package com.example.dragon.pageflipperdemo.Exception;

/**
 * pdfoldalra sikertelen lapozáskor dobódó kivétel
 */
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
