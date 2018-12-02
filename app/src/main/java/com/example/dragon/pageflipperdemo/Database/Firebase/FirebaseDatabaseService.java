package com.example.dragon.pageflipperdemo.Database.Firebase;

import android.app.Activity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * firebase adatbázis eléréséért felelős osztály
 */
public class FirebaseDatabaseService {
    /**
     * singleton scope
     */
    private static FirebaseDatabaseService __instance;

    public static FirebaseDatabaseService getInstance(Activity context) {
        if (__instance == null) {
            __instance = new FirebaseDatabaseService(context);
            if(mDatabase == null){
                mDatabase = FirebaseDatabase.getInstance();
                FirebaseDatabase.getInstance().setPersistenceEnabled(true);
            }
        }
        return __instance;
    }

    private static FirebaseDatabase mDatabase;
    public static DatabaseReference mDatabaseReference;

    private Activity applicationContext;

    private FirebaseDatabaseService(Activity context) {
        applicationContext = context;
    }

    /**
     * beálltja az adatbázis referenciát a választott felhasználó kulcsára
     * @param uid
     */
    public void configureReference(String uid) {
        mDatabaseReference = mDatabase.getReferenceFromUrl("https://pageflipperdemo.firebaseio.com/").child(uid);
    }

    /**
     * feltölt egy új elemet az adatbázisba
     * @param pdfDataModel
     */
    public void uploadToDatabase(PdfDataModel pdfDataModel) {
        mDatabaseReference.child(pdfDataModel.id + "").setValue(pdfDataModel);
    }
}
