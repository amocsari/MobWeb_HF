package com.example.dragon.pageflipperdemo.Database.Local;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

/**
 * az adatbázis elérési osztály
 */
@Database(entities = PdfDataEntity.class, version = 3, exportSchema = false)
public abstract class PdfDatabase extends RoomDatabase {
    /**
     * singleton scope
     */
    private static PdfDatabase __instance;

    public abstract PdfDataDao pdfDataDao();

    public static PdfDatabase getInstance(Context context){
        if(__instance == null){
            __instance = Room.databaseBuilder(context, PdfDatabase.class, "pdfdatabase")
                    .allowMainThreadQueries()
                    .fallbackToDestructiveMigration()
                    .build();
        }

        return __instance;
    }
}
