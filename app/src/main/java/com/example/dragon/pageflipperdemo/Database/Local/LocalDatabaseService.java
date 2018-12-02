package com.example.dragon.pageflipperdemo.Database.Local;

import android.app.Activity;

import java.util.List;

/**
 * lokális adattárolásért felelős osztály
 */
public class LocalDatabaseService {
    /**
     * singleton scopr
     */
    private static LocalDatabaseService __instance;

    public static LocalDatabaseService getInstance(Activity context) {
        if (__instance == null) {
            __instance = new LocalDatabaseService(context);
        }
        return __instance;
    }

    private Activity applicationContext;

    private LocalDatabaseService(Activity context) {
        applicationContext = context;
    }

    /**
     * hozzáad egy új elemet az adatbázishoz
     * @param pdfDataEntity
     */
    public void addPdfData(PdfDataEntity pdfDataEntity) {
        PdfDatabase.getInstance(applicationContext).pdfDataDao().addPdfData(pdfDataEntity);
    }

    /**
     * visszaadja az összes a felhasználóhoz tartozó rekordot
     * @param uid
     * @return
     */
    public List<PdfDataEntity> getAllPdfsForUser(String uid) {
        return PdfDatabase.getInstance(applicationContext).pdfDataDao().getAllPdfDataForUser(uid);
    }

    /**
     * az utolsó rekord alapján visszadja az új kulcs értékét inkrementális növeléssel
     * @return
     */
    public int generateNextId() {
        return PdfDatabase.getInstance(applicationContext).pdfDataDao().getLatestId() + 1;
    }
}
