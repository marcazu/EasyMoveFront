package com.example.easymovefront.network;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Environment;
import android.widget.Toast;

import com.example.easymovefront.data.model.CurrentBitmap;
import com.example.easymovefront.data.model.LoggedUser;
import com.example.easymovefront.ui.maps.AsyncResponse;
import com.example.easymovefront.ui.maps.MapsActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class CreateMarkerTask extends AsyncTask<String, Void, JSONObject>
{
    private Context mContext;

    public String postUrl= "https://easymov.herokuapp.com/rest/obstacle";
    public String imageUrl ="http://sharefy.tk/api/publication";
    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    public static int returnCode = -1;
    public static String mResponse;

    public AsyncResponse asyncResponse = null;

    public CreateMarkerTask (Context context){
        mContext = context;
    }

    @Override
    protected JSONObject doInBackground(String... strings)
    {
        JSONObject json = null;
        try {
            Bitmap pic = CurrentBitmap.getInstance().getBitMap();
            File file = new File(Environment.getExternalStorageDirectory() + "/myimage.png");
            FileOutputStream stream = new FileOutputStream(file);
            pic.compress(Bitmap.CompressFormat.PNG, 100, stream);
            stream.close();
            String idFoto = uploadFoto(file);
            if (!idFoto.equals("0")) {

                String fotoUrl = getFotoFromBack(idFoto);

                json = new JSONObject();
                json.put("descripcio", strings[0]);
                json.put("foto", fotoUrl);
                json.put("idUsuariCreador", Integer.parseInt(strings[1]));
                json.put("latitud", Double.valueOf(strings[2]));
                json.put("longitud", Double.valueOf(strings[3]));
                json.put("nom", strings[4]);

                String postBody = json.toString();

                OkHttpClient client = new OkHttpClient();

                RequestBody body = RequestBody.create(JSON, postBody);

                String header = "Bearer " + LoggedUser.getInstance().getToken();

                Request request = new Request.Builder()
                        .url(postUrl)
                        .header("Authorization", header)
                        .post(body)
                        .build();

                try (Response response = client.newCall(request).execute()) {

                    if (response.isSuccessful()) {
                        // Get back the response and convert it to a Book object
                        returnCode = 1;
                        mResponse = response.body().string();
                        json.put("id", Integer.parseInt(mResponse));

                    } else {
                        returnCode = 0;
                        mResponse = response.body().string();
                    }
                }

            }
            else returnCode = -1;
        }

        catch (Exception e) {
            returnCode = -1;
        }

        return json;
    }

    private String getFotoFromBack(String idFoto) throws IOException, JSONException {
        JSONObject json = null;

        String getUrl = imageUrl + "/" + idFoto;

        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(getUrl)
                .get()
                .build();
        Response response = client.newCall(request).execute();

        json = new JSONObject(response.body().string());
        String baseUrl = "http://sharefy.tk";
        JSONObject value = json.getJSONObject("value");
        baseUrl += value.getString("video_path");
        return baseUrl;
    }

    private String uploadFoto(File file) throws IOException, JSONException {
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("video", "obstaclePicture.png", RequestBody.create(MediaType.parse("image/png"), file))
                .addFormDataPart("id_user", "55")
                .addFormDataPart("text", "my_image")
                .build();

        Request request = new Request.Builder()
                .url(imageUrl)
                .post(requestBody)
                .build();

        OkHttpClient client = new OkHttpClient();
        Response response = client.newCall(request).execute();
        if (response.isSuccessful()) {
            JSONObject json = new JSONObject(response.body().string());
            return json.getString("value");
        }
        else return "0";
    }


    @Override
    protected void onPostExecute(JSONObject result) {
        asyncResponse.processFinish(result);
        CharSequence text = String.valueOf(returnCode);
        int duration = Toast.LENGTH_LONG;

        Toast toast = Toast.makeText(mContext, text, duration);
        toast.show();
    }
}