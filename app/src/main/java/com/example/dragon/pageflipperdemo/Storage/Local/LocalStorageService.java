package com.example.dragon.pageflipperdemo.Storage.Local;

import android.app.Activity;
import android.net.Uri;

import com.example.dragon.pageflipperdemo.Database.Local.LocalDatabaseService;
import com.example.dragon.pageflipperdemo.Database.Local.PdfDataEntity;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

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
     * @param uri
     * @param entity
     * @throws IOException
     */
    public void saveToLocalStorage(Uri uri, final PdfDataEntity entity) throws IOException {
        File dst = new File(applicationContext.getFilesDir() + "/" + entity.id+".pdf");
        dst.createNewFile();
        File src = new File(uri.toString());
        InputStream is = applicationContext.getContentResolver().openInputStream(uri);

        OutputStream os=new FileOutputStream(dst);
        byte[] buff=new byte[1024];
        int len;
        while((len=is.read(buff))>0){
            os.write(buff,0,len);
        }
        is.close();
        os.close();

        LocalDatabaseService.getInstance(applicationContext).addPdfData(entity);
    }

    /**
     * visszaad egy pdf-t id alapján
     * @param id
     * @return
     */
//    public File getPdfById(int id) {
//        File directory = new File(applicationContext.getFilesDir().toURI());
//        File[] files = directory.listFiles();
//
//        String filename = id + ".pdf";
//        return new File(applicationContext.getFilesDir() + "/" + filename);
//    }
}
