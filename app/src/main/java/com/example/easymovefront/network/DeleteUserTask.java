package com.example.easymovefront.network;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import com.example.easymovefront.data.model.LoggedUser;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class DeleteUserTask  extends AsyncTask<String, Void, String> {

    private Context mContext;

    public String deleteUrl= "https://easymov.herokuapp.com/rest/usuari";
    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    public static int returnCode = -1;
    public static String mResponse;

    public DeleteUserTask(Context mContext) {
        this.mContext = mContext;
    }

    @Override
    protected String doInBackground(String... strings) {

        OkHttpClient client = new OkHttpClient();
        deleteUrl = deleteUrl + "/" + strings[0];

        String header = "Bearer " + LoggedUser.getInstance().getToken();

        Request request = new Request.Builder()
                .url(deleteUrl)
                .header("Authorization", header)
                .delete()
                .build();

        try (Response response = client.newCall(request).execute()) {

            if (response.isSuccessful()) {
                // Get back the response and convert it to a Book object
                returnCode = 1;
                mResponse = response.body().string();
            }
            else {
                returnCode = 0;
                mResponse = response.body().string();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return mResponse;
    }

    @Override
    protected void onPostExecute(String result) {
        CharSequence text = String.valueOf(returnCode);
        int duration = Toast.LENGTH_LONG;

        Toast toast = Toast.makeText(mContext, text, duration);
        toast.show();
    }
}
