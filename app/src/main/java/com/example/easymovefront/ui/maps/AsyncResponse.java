package com.example.easymovefront.ui.maps;

import org.json.JSONObject;

/**
 * Interface used to handle responses from secondary threads
 */
public interface AsyncResponse {

    void processFinish(String output);

    void processFinish(JSONObject output);
}
