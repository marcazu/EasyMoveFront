package com.example.easymovefront.data.model;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;

/**
 * This class keeps track of the current user logged in: its ID in backend, its access token, aswell as
 * his Google account
 */
public class LoggedUser {
    private static final LoggedUser ourInstance = new LoggedUser();

    private GoogleSignInAccount mUserAccount;

    private GoogleSignInClient mGoogleSignInClient;

    private String id;

    private String token;

    public void setmUserAccount(GoogleSignInAccount acc) { this.mUserAccount = acc; }

    public void setId(String id) { this.id = id; }

    public void setToken (String token) { this.token = token; }

    public void setmGoogleSignInClient(GoogleSignInClient client) { this.mGoogleSignInClient = client;}

    public GoogleSignInAccount getmUserAccount() { return this.mUserAccount; }

    public GoogleSignInClient getmGoogleSignInClient() { return this.mGoogleSignInClient;}

    public String getId() { return this.id; }

    public String getToken() { return this.token; }

    public static LoggedUser getInstance() {
        return ourInstance;
    }

    private LoggedUser() {
    }
}
