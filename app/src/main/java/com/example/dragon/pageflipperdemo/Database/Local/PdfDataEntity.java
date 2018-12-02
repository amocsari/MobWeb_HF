package com.example.dragon.pageflipperdemo.Database.Local;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

import com.example.dragon.pageflipperdemo.Database.Firebase.PdfDataModel;

/**
 * entitást reprezentáló osztály
 */
@Entity
public class PdfDataEntity {
    @PrimaryKey
    public final int id;
    public String title;
    public String uid;

    public PdfDataEntity(int id){
        this.id = id;
        title = "Book_#" + id + ".pdf";
    }

    public PdfDataEntity(PdfDataModel model){
        id = model.id;
        title = model.title;
    }
}
