package com.example.dragon.pageflipperdemo.Database.Firebase;

import com.example.dragon.pageflipperdemo.Database.Local.PdfDataEntity;

public class PdfDataModel {
    public int id;
    public String title;

    public PdfDataModel() {
    }

    public PdfDataModel(PdfDataEntity entity) {
        id = entity.id;
        title = entity.title;
    }
}
