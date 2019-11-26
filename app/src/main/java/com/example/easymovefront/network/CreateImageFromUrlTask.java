package com.example.easymovefront.network;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.InputStream;
import java.net.URL;

import okhttp3.MediaType;

public class CreateImageFromUrlTask extends AsyncTask<String, Void, Drawable> {
    private Context mContext;

    public static int returnCode = -1;
    public static String mResponse;

    public CreateImageFromUrlTask (Context context){
        mContext = context;
    }

    @Override
    protected Drawable doInBackground(String... strings)
    {
        try {
            InputStream is = (InputStream) new URL(strings[0]).getContent();
            return Drawable.createFromStream(is, "src name");
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    protected void onPostExecute(Drawable result) {
        /*CharSequence text = String.valueOf(returnCode);
        int duration = Toast.LENGTH_LONG;

        Toast toast = Toast.makeText(mContext, text, duration);
        toast.show();*/
    }
}
