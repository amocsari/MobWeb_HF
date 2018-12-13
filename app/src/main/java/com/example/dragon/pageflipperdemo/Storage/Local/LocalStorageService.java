package com.example.dragon.pageflipperdemo.Storage.Local;

import android.app.Activity;
import android.net.Uri;

import com.example.dragon.pageflipperdemo.Database.Local.LocalDatabaseService;
import com.example.dragon.pageflipperdemo.Database.Local.PdfDataEntity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * lokális adattárolásért felelős osztály
 */
public class LocalStorageService {
    private static LocalStorageService __instance;

    public static LocalStorageService getInstance(Activity context) {
        if (__instance == null) {
            __instance = new LocalStorageService(context);
        }
        return __instance;
    }

    private Activity applicationContext;

    private LocalStorageService(Activity context) {
        applicationContext = context;
    }

    /**
     * elmenti a uri-ban kapott file-t internal storage-ba, hogy majd meg lehessen onnan nyitni
     *
     * @param uri
     * @param entity
     * @throws IOException
     */
    public void saveToLocalStorage(Uri uri, final PdfDataEntity entity) throws IOException {
        InputStream inputStream = applicationContext.getContentResolver().openInputStream(uri);

        byte[] bytes = new byte[inputStream.available()];

        int offset = 0;
        int numread = 0;
        while (offset < bytes.length && (numread = inputStream.read(bytes, offset, bytes.length - offset)) >= 0) {
            offset += numread;
        }

        String filepath = applicationContext.getFilesDir() + "/" + entity.id+".pdf";
        File dst = new File(filepath);
        dst.createNewFile();

        FileOutputStream stream = new FileOutputStream(filepath);
        stream.write(bytes);
        stream.close();
        LocalDatabaseService.getInstance(applicationContext).addPdfData(entity);
    }

    /**
     * visszaad egy pdf-t id alapján
     * @param id
     * @return
     */
    public Uri getPdfById(int id) {
        File file = new File(applicationContext.getFilesDir() + "/" + id +".pdf");
        if(file.exists()){
            Uri uri = Uri.fromFile(file);
            return  uri;
        }

        return null;
    }
}
