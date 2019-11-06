package com.example.easymovefront.data.model;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;

public class LoggedUser {
    private static final LoggedUser ourInstance = new LoggedUser();

    private GoogleSignInAccount mUserAccount;

    private int id;

    public void setmUserAccount(GoogleSignInAccount acc) { this.mUserAccount = acc; }

    public void setId(int id) { this.id = id; }

    public GoogleSignInAccount getmUserAccount() { return this.mUserAccount; }

    public int getId() { return this.id; }

    public static LoggedUser getInstance() {
        return ourInstance;
    }

    private LoggedUser() {
    }
}
