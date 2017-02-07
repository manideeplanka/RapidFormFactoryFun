package com.mlanka.rapidformfactoryfun;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import com.rapidBizApps.lavasa.Controller.RFBuilder;
import com.rapidBizApps.lavasa.Listeners.FindDataInterface;
import com.rapidBizApps.lavasa.Listeners.FoundDataCallback;
import com.rapidBizApps.lavasa.Listeners.LoadDataInterface;
import com.rapidBizApps.lavasa.Listeners.RFFormListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements FindDataInterface,LoadDataInterface {
    private LinearLayout mFormLayout;
    private RFBuilder builder;
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mFormLayout = (LinearLayout) findViewById(R.id.mine);

        initializeRFBuilder();
    }

    private void initializeRFBuilder() {
        builder = new RFBuilder();
        builder.registerContext(MainActivity.this);

        builder.generateFormWithFileName("mlanka.json", mFormLayout, new RFFormListener() {
            @Override
            public void onBeforeFormRender() {
                Log.d(TAG, "onBeforeFormRender: ");
            }

            @Override
            public void onPageChange() {
                Log.d(TAG, "onPageChange: ");
            }

            @Override
            public void onParseError(String error) {
                Log.e(TAG, "onParseError: " + error);
            }

            @Override
            public void onLoad() {
                Log.d(TAG, "onLoad: ");
            }

            @Override
            public void onChange(String fieldJsonResponse) {
                Log.d(TAG, "onChange: " + fieldJsonResponse); //called every time there is a change in the form's data
            }
        });
    }

    @Override
    public JSONObject findDataForKey(String key) throws JSONException {
        String extrasString = null;

        if (key.equalsIgnoreCase("Football")) {
            extrasString = "{\n" +
                    "  \"sports_page_select_player\": [\n" +
                    "    {\n" +
                    "      \"0\": \"Lionel Messi\"\n" +
                    "    }\n" +
                    "  ]\n" +
                    "}\n";

        } else if (key.equalsIgnoreCase("Tennis")) {
            extrasString = "{\n" +
                    "  \"sports_page_select_player\": [\n" +
                    "    {\n" +
                    "      \"0\": \"Roger Federer\"\n" +
                    "    }\n" +
                    "  ]\n" +
                    "}\n";

        } else if (key.equalsIgnoreCase("Roger Federer")) {
            extrasString = "{\n" +
                    "  \"sports_page_set_score\": [\n" +
                    "    {\n" +
                    "      \"0\": \"DEUCE\"\n" +
                    "    }\n" +
                    "  ]\n" +
                    "}\n";
        } else if (key.equalsIgnoreCase("Lionel Messi")) {
            extrasString = "{\n" +
                    "  \"sports_page_set_score\": [\n" +
                    "    {\n" +
                    "      \"0\": \"PENALTY\"\n" +
                    "    }\n" +
                    "  ]\n" +
                    "}\n";
        }
        return new JSONObject(extrasString);
    }

    @Override
    public void loadDynamicData(String jsonFieldKey, FoundDataCallback callback) {
        // TODO: 7/2/17 use jsonFieldKey to fetch dat from a data source
        List<String> items = new ArrayList<>();
        items.add("Tennis");
        items.add("Football");
        callback.callback(items);
    }
}
