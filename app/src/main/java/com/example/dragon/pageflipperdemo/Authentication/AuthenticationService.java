package com.example.dragon.pageflipperdemo.Authentication;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.Toast;

import com.example.dragon.pageflipperdemo.R;
import com.firebase.ui.auth.data.model.User;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

/**
 * Google authentikációért felelős osztály
 */
public class AuthenticationService {
    public static final int SIGN_IN_REQUEST_CODE = 53;

    /**
     * Singleton scope
     */
    private static AuthenticationService __instance;

    public static AuthenticationService getInstance(FragmentActivity context) {
        if (__instance == null) {
            __instance = new AuthenticationService(context);
        }

        return __instance;
    }

    private GoogleApiClient mGoogleApiClient;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FragmentActivity applicationContext;
    private ResultCallback<Status> resultCallback;

    /**
     * Konstruktor
     * @param context
     */
    private AuthenticationService(FragmentActivity context) {
        applicationContext = context;
        GoogleSignInOptions options = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(applicationContext.getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(applicationContext).enableAutoManage(context, new GoogleApiClient.OnConnectionFailedListener() {
                @Override
                public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                    Snackbar.make(applicationContext.findViewById(android.R.id.content), "Connection Failed!", Snackbar.LENGTH_SHORT).show();
                }
            }).addApi(Auth.GOOGLE_SIGN_IN_API, options).build();
        }
        mAuth = FirebaseAuth.getInstance();
    }

    /**
     * Beálljtja az authentikáció állapotának megváltozását figyelő liszenert
     * @param authStateListener
     * @return
     */
    public AuthenticationService setAuthListener(FirebaseAuth.AuthStateListener authStateListener) {
        mAuthListener = authStateListener;
        return this;
    }

    /**
     * beállítja a kijelentkezéskori callback függvényt
     * @param resultCallback
     * @return
     */
    public AuthenticationService setResultCallback(ResultCallback<Status> resultCallback) {
        this.resultCallback = resultCallback;
        return this;
    }

    /**
     * bejelenetkezés művelet
     */
    public void signIn() {
        mGoogleApiClient.clearDefaultAccountAndReconnect();
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        applicationContext.startActivityForResult(signInIntent, SIGN_IN_REQUEST_CODE);
    }


    /**
     * bejelentkezés művelet segédfüggvénye
     * @param account
     */
    private void firebaseAuthWithGoogle(GoogleSignInAccount account) {
        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        mAuth.signInWithCredential(credential).addOnCompleteListener(applicationContext, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (!task.isSuccessful()) {
                    Snackbar.make(applicationContext.findViewById(android.R.id.content), "Login Failed!", Snackbar.LENGTH_SHORT).show();
                }
            }
        });
    }

    /**
     * Kijelentkezés művelet
     */
    public void signOut() {
        FirebaseAuth.getInstance().signOut();
        mGoogleApiClient.connect();
        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(resultCallback);
    }

    public void removeAuthListener() {
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    public void addAuthListener() {
        mAuth.addAuthStateListener(mAuthListener);
    }

    /**
     * bejelentkezés kérelem után hívódok
     * @param resultData
     */
    public void onAuthResult(Intent resultData) {
        GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(resultData);
        if (result.isSuccess()) {
            GoogleSignInAccount account = result.getSignInAccount();
            firebaseAuthWithGoogle(account);
        } else {
            Snackbar.make(applicationContext.findViewById(android.R.id.content), "Login Failed!", Snackbar.LENGTH_SHORT).setAction("Try again!", new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    signIn();
                }
            }).show();
        }
    }

    /**
     * megadja a bejeletnekzett felhasználó uid-ját
     * @return
     */
    public String getConnectedUid() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user != null){
            return user.getUid();
        }
        return null;
    }
}
