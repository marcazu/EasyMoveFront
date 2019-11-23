package com.example.easymovefront.network;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.widget.Toast;

import com.example.easymovefront.data.model.LoggedUser;

import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.URL;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class CreateMarkerTask extends AsyncTask<String, Void, JSONObject>
{
    private Context mContext;

    public String postUrl= "https://easymov.herokuapp.com/obstacle";
    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    public static int returnCode = -1;
    public static String mResponse;

    public CreateMarkerTask (Context context){
        mContext = context;
    }

    @Override
    protected JSONObject doInBackground(String... strings)
    {
        JSONObject json = null;
        try {
            json = new JSONObject();
            json.put("descripcio", strings[0]);
            json.put("foto", strings[1]);
            json.put("idUsuariCreador", Integer.parseInt(strings[2]));
            json.put("latitud", Double.valueOf(strings[3]));
            json.put("longitud", Double.valueOf(strings[4]));
            json.put("nom", strings[5]);

            String postBody = json.toString();

            OkHttpClient client = new OkHttpClient();

            RequestBody body = RequestBody.create(JSON, postBody);

            Request request = new Request.Builder()
                    .url(postUrl)
                    .post(body)
                    .build();

            try (Response response = client.newCall(request).execute()) {

                if (response.isSuccessful()) {
                    // Get back the response and convert it to a Book object
                    returnCode = 1;
                    mResponse = response.body().string();
                    json.put("id", Integer.parseInt(mResponse));

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

        return json;
    }

    @Override
    protected void onPostExecute(JSONObject result) {
        /*CharSequence text = result.toString();
        int duration = Toast.LENGTH_LONG;

        Toast toast = Toast.makeText(mContext, text, duration);
        toast.show();*/
    }
}