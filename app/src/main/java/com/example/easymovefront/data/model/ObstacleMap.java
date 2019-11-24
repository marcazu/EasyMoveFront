package com.example.easymovefront.data.model;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.maps.model.Marker;

import org.json.JSONObject;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class ObstacleMap {
    private static final ObstacleMap ourInstance = new ObstacleMap();

    private Map<Marker, JSONObject> mObstacleMap;

    public static ObstacleMap getInstance() {
        return ourInstance;
    };

    private ObstacleMap() {
        mObstacleMap = new HashMap<>();
    }

    public void addMarker(Marker m, JSONObject obj) {
        mObstacleMap.put(m, obj);
    }

    public Map<Marker, JSONObject> getMap() {
        return mObstacleMap;
    }
}
