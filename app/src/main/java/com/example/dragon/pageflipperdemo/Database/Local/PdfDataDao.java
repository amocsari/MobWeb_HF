package com.example.dragon.pageflipperdemo.Database.Local;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

/**
 * rekordot reprezentáló osztály
 */
@Dao
public interface PdfDataDao {
    @Insert
    void addPdfData(PdfDataEntity pdfDataEntity);

    @Query("select * from PdfDataEntity where uid like :uid")
    List<PdfDataEntity> getAllPdfDataForUser(String uid);

    @Query("select max(id) from PdfDataEntity")
    int getLatestId();

}
