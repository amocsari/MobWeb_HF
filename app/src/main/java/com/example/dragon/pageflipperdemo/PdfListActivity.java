package com.example.dragon.pageflipperdemo;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dragon.pageflipperdemo.Authentication.AuthenticationService;
import com.example.dragon.pageflipperdemo.Database.Firebase.FirebaseDatabaseService;
import com.example.dragon.pageflipperdemo.Database.Firebase.PdfDataModel;
import com.example.dragon.pageflipperdemo.Database.Local.LocalDatabaseService;
import com.example.dragon.pageflipperdemo.Database.Local.PdfDataEntity;
import com.example.dragon.pageflipperdemo.Storage.Firebase.FirebaseStorageService;
import com.example.dragon.pageflipperdemo.Storage.Local.LocalStorageService;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * pdf lista megjelenítéséért felelős osztály
 */
public class PdfListActivity extends AppCompatActivity {
    private static final int IMPORT_REQUEST_CODE = 42;

    private ArrayList<PdfDataModel> pdfDataModelList;

    private DrawerLayout mDrawerLayout;
    private NavigationView mNavigationView;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;

    private TextView tvMsg;

    /**
     * beállítja a megfelelő authentikációs, adatábis és storage konfigurációkat
     * feltölti a listát
     * beállítja a menüt
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_system);

        AuthenticationService.getInstance(this)
                .setAuthListener(new FileSystemAuthChangeListener())
                .setResultCallback(new FileSystemResultCallback());

        mNavigationView = findViewById(R.id.nav_view);
        mDrawerLayout = findViewById(R.id.drawer_layout);

        tvMsg = findViewById(R.id.tv_filesystem_msg);

        final android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        ActionBarDrawerToggle mActionBarDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawerLayout.addDrawerListener(mActionBarDrawerToggle);
        mActionBarDrawerToggle.syncState();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("application/pdf");
                startActivityForResult(intent, IMPORT_REQUEST_CODE);
            }
        });

        mNavigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                mDrawerLayout.closeDrawers();
                int id = item.getItemId();
                switch (id) {
                    case R.id.miImport:
                        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                        intent.setType("application/pdf");
                        startActivityForResult(intent, IMPORT_REQUEST_CODE);
                        break;
                    case R.id.miLogin:
                        AuthenticationService.getInstance(PdfListActivity.this).signIn();
                        break;
                    case R.id.miLogout:
                        AuthenticationService.getInstance(PdfListActivity.this).signOut();
                        break;
                }
                return false;
            }
        });

        mRecyclerView = findViewById(R.id.rw_files);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        pdfDataModelList = new ArrayList<>();
        mAdapter = new PdfListAdapter(pdfDataModelList, this, mRecyclerView);
        mRecyclerView.setAdapter(mAdapter);

//        loadPdfs();
    }

    @Override
    public void onStart() {
        super.onStart();
        AuthenticationService.getInstance(this).addAuthListener();
    }

    @Override
    public void onStop() {
        super.onStop();
        AuthenticationService.getInstance(this).removeAuthListener();
    }

    /**
     *kezeli a választ váró intentekre érkezett válaszokat
     * @param requestCode
     * @param resultCode
     * @param resultData
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent resultData) {
        super.onActivityResult(requestCode, resultCode, resultData);

        // The ACTION_OPEN_DOCUMENT intent was sent with the request code
        // IMPORT_REQUEST_CODE. If the request code seen here doesn't match, it's the
        // response to some other intent, and the code below shouldn't run at all.

        if (requestCode == IMPORT_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            if (resultData != null) {
                addPdfToList(resultData.getData());
            }
        }

        if (requestCode == AuthenticationService.SIGN_IN_REQUEST_CODE) {
            AuthenticationService.getInstance(this).onAuthResult(resultData);
        }
    }

    /**
     * navigationdrawer nyitásáért és csukásáért felel
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mDrawerLayout.isDrawerOpen(mNavigationView)) {
            mDrawerLayout.closeDrawers();
        } else {
            mDrawerLayout.openDrawer(mNavigationView);
        }
        return false;
    }

    /**
     * új pdf beöltését végzi
     * @param uri
     */
    private void addPdfToList(Uri uri) {
        String uid = AuthenticationService.getInstance(this).getConnectedUid();
        if (uid == null) {
            Toast.makeText(this, "Nem támogatott művelet! Nincs bejelentkezve!", Toast.LENGTH_SHORT).show();
            return;
        }
        PdfDataEntity entity = new PdfDataEntity(LocalDatabaseService.getInstance(this).generateNextId());
        entity.uid = uid;

        PdfDataModel model = new PdfDataModel(entity);

        FirebaseStorageService.getInstance(this).uploadPdf(uri, model);

        try {
            LocalStorageService.getInstance(this).saveToLocalStorage(uri, entity);
        } catch (IOException e) {
            Toast.makeText(this, "Nem sikerült hozzáadni a pdf-t", Toast.LENGTH_SHORT).show();
        }

        pdfDataModelList.add(model);
        mAdapter.notifyDataSetChanged();
        if (pdfDataModelList.size() > 0) {
            tvMsg.setVisibility(View.GONE);
            mRecyclerView.setVisibility(View.VISIBLE);
        }
    }

    /**
     * authentikációs kijelentkezés callback
     */
    public class FileSystemResultCallback implements ResultCallback<Status> {
        @Override
        public void onResult(@NonNull Status status) {
            Menu menu = mNavigationView.getMenu();
            View header = mNavigationView.getHeaderView(0);
            MenuItem miLogin = menu.findItem(R.id.miLogin);
            MenuItem miLogout = menu.findItem(R.id.miLogout);
            MenuItem miImport = menu.findItem(R.id.miImport);
            TextView tvEmail = header.findViewById(R.id.tv_email);
            TextView tvUser = header.findViewById(R.id.tv_user);
            miLogin.setVisible(true);
            miLogout.setVisible(false);
            miImport.setVisible(false);
            tvMsg.setText(R.string.loginMsg);
            tvEmail.setText("");
            tvUser.setText("");
            loadPdfs();
        }
    }

    /**
     * autentikáció változását figyelő listener
     */
    private class FileSystemAuthChangeListener implements FirebaseAuth.AuthStateListener {

        @Override
        public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
            FirebaseUser user = firebaseAuth.getCurrentUser();
            if (user != null) {
                Menu menu = mNavigationView.getMenu();
                View header = mNavigationView.getHeaderView(0);
                MenuItem miLogin = menu.findItem(R.id.miLogin);
                MenuItem miLogout = menu.findItem(R.id.miLogout);
                MenuItem miImport = menu.findItem(R.id.miImport);
                TextView tvEmail = header.findViewById(R.id.tv_email);
                TextView tvUser = header.findViewById(R.id.tv_user);
                miLogin.setVisible(false);
                miLogout.setVisible(true);
                miImport.setVisible(true);

                String uid = user.getUid();

                FirebaseStorageService.getInstance(PdfListActivity.this).configureReference(uid);
                FirebaseDatabaseService.getInstance(PdfListActivity.this).configureReference(uid);
                loadPdfs();

                tvMsg.setText("A listában nincsenek elemek!");

                String displayName = user.getDisplayName();
                String email = user.getEmail();
                if (displayName != null) {
                    tvUser.setText(displayName);
                }
                if (email != null) {
                    tvEmail.setText(email);
                }
            }
        }
    }

    /**
     * pdf-eket tölti be
     */
    private void loadPdfs() {
        String uid = AuthenticationService.getInstance(this).getConnectedUid();
        List<PdfDataEntity> entities = LocalDatabaseService.getInstance(this).getAllPdfsForUser(uid);

        pdfDataModelList.clear();

        for (PdfDataEntity entity: entities) {
            pdfDataModelList.add(new PdfDataModel(entity));
        }

        mAdapter.notifyDataSetChanged();
        if(pdfDataModelList.size() == 0){
            mRecyclerView.setVisibility(View.GONE);
            tvMsg.setVisibility(View.VISIBLE);
        }
        else {
            mRecyclerView.setVisibility(View.VISIBLE);
            tvMsg.setVisibility(View.GONE);
        }
    }
}
