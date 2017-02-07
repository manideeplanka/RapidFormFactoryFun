package com.rapidBizApps.lavasa.Listeners;

import org.json.JSONException;
import org.json.JSONObject;

public interface FindDataInterface {
    JSONObject findDataForKey(String key) throws JSONException;
}
