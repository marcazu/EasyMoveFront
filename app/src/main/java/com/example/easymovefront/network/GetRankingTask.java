package com.example.easymovefront.network;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import com.example.easymovefront.data.model.LoggedUser;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class GetRankingTask extends AsyncTask<String, Void, String> {
    private Context mContext;

    public String getUrl= "https://easymov.herokuapp.com/rest/ranking";
    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    public static int returnCode = -1;
    public static String mResponse = "";

    public GetRankingTask (Context context){
        mContext = context;
    }

    @Override
    protected String doInBackground(String... strings) {
        JSONObject json = new JSONObject();


        String postBody = json.toString();

        OkHttpClient client = new OkHttpClient();

        RequestBody body = RequestBody.create(JSON, postBody);

        String header = "Bearer " + LoggedUser.getInstance().getToken();

        Request request = new Request.Builder()
                .url(getUrl)
                .header("Authorization", header)
                .get()
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
