package com.example.dragon.pageflipperdemo.Storage.Firebase;

import android.app.Activity;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.widget.TextView;

import com.example.dragon.pageflipperdemo.Database.Firebase.FirebaseDatabaseService;
import com.example.dragon.pageflipperdemo.Database.Firebase.PdfDataModel;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

/**
 * Firebase tárolásért felelős osztály
 */
public class FirebaseStorageService {

    /**
     * singleton scope
     */
    private static FirebaseStorageService __instance;

    public static FirebaseStorageService getInstance(Activity context) {
        if (__instance == null) {
            __instance = new FirebaseStorageService(context);
        }

        return __instance;
    }

    private FirebaseStorage mStorage;
    private StorageReference mStorageReference;

    private Activity applicationContext;

    private FirebaseStorageService(Activity context) {
        applicationContext = context;
        mStorage = FirebaseStorage.getInstance();
    }

    /**
     * beállítja a bucket-t az aktuális user-ére
     * @param uid
     * @return
     */
    public FirebaseStorageService configureReference(String uid) {
        mStorageReference = mStorage.getReferenceFromUrl("gs://pageflipperdemo.appspot.com").child(uid);
        return this;
    }

    /**
     * feltölt egy pdf-t a bucket-be
     * id.pdf néven
     * ha sikeres, elmenti az adatázisba is
     * @param uri
     * @param pdfDataModel
     */
    public void uploadPdf(Uri uri, final PdfDataModel pdfDataModel) {
        if (uri != null) {
            final StorageReference storageReference = this.mStorageReference.child(pdfDataModel.id + ".pdf");

            final UploadTask uploadTask = storageReference.putFile(uri);

            final Snackbar snackbar = Snackbar.make(applicationContext.findViewById(android.R.id.content), "Uploading: 0%", Snackbar.LENGTH_INDEFINITE);
            snackbar.setAction("Cancel", new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    uploadTask.cancel();
                }
            });
            snackbar.show();
            uploadTask
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            snackbar.dismiss();
                            FirebaseDatabaseService.getInstance(applicationContext).uploadToDatabase(pdfDataModel);
                            Snackbar.make(applicationContext.findViewById(android.R.id.content), "Upload Successful!", Snackbar.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            snackbar.dismiss();
                            Snackbar.make(applicationContext.findViewById(android.R.id.content), "Failed to upload!", Snackbar.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                            TextView textView = applicationContext.findViewById(android.support.design.R.id.snackbar_text);
                            textView.setText("Uploading: " + ((int) progress) + "%...");
                        }
                    });
        }
    }
}
