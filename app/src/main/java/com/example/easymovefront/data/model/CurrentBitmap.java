package com.example.easymovefront.data.model;

import android.graphics.Bitmap;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;

public class CurrentBitmap {
    private static final CurrentBitmap ourInstance = new CurrentBitmap();

    private Bitmap bmp;

    public static CurrentBitmap getInstance() {
        return ourInstance;
    }

    public Bitmap getBitMap() { return bmp; }

    public void setBitMap(Bitmap bit) { bmp = bit; }

    private CurrentBitmap() {
    }
}
