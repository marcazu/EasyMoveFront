package com.example.easymovefront.data.model;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;

public class LoggedUser {
    private static final LoggedUser ourInstance = new LoggedUser();

    private GoogleSignInAccount mUserAccount;

    private GoogleSignInClient mGoogleSignInClient;

    private String id;

    public void setmUserAccount(GoogleSignInAccount acc) { this.mUserAccount = acc; }

    public void setId(String id) { this.id = id; }

    public void setmGoogleSignInClient(GoogleSignInClient client) { this.mGoogleSignInClient = client;}

    public GoogleSignInAccount getmUserAccount() { return this.mUserAccount; }

    public GoogleSignInClient getmGoogleSignInClient() { return this.mGoogleSignInClient;}

    public String getId() { return this.id; }

    public static LoggedUser getInstance() {
        return ourInstance;
    }

    private LoggedUser() {
    }
}
