package com.rapidBizApps.lavasa.Listeners;

import java.util.List;

/**
 * Created by mlanka on 7/2/17.
 */

public interface LoadDataInterface {
    void loadDynamicData(String jsonFieldKey, FoundDataCallback callback);
}