package com.example.easymovefront.ui.maps;

import org.json.JSONObject;

public interface AsyncResponse {

    void processFinish(String output);

    void processFinish(JSONObject output);
}
