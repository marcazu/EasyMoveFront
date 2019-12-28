package com.example.easymovefront.network;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.example.easymovefront.data.model.LoggedUser;

import org.json.JSONObject;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class LikeObstacleTask extends AsyncTask<String, Void, Integer> {

    private Context mContext;

    public String postUrl= "https://easymov.herokuapp.com/rest/obstacle/";
    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    public static int returnCode = -1;
    public static String mResponse;

    public LikeObstacleTask (Context context){
        mContext = context;
    }

    @Override
    protected Integer doInBackground(String... strings)
    {
        try {
            postUrl = postUrl + strings[0] + "/" + strings[1] + "/" + strings[2];

            OkHttpClient client = new OkHttpClient();

            RequestBody body = RequestBody.create(JSON, "");

            String header = "Bearer " + LoggedUser.getInstance().getToken();

            Request request = new Request.Builder()
                    .url(postUrl)
                    .header("Authorization", header)
                    .put(body)
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
            }

        }

        catch (Exception e) {
            returnCode = -1;
        }

        return returnCode;
    }

    @Override
    protected void onPostExecute(Integer result) {
        CharSequence text = result.toString();
        int duration = Toast.LENGTH_LONG;

        Toast toast = Toast.makeText(mContext, text, duration);
        toast.show();
    }
}
