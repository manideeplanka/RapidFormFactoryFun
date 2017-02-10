package com.mlanka.rapidformfactoryfun;

import android.location.Location;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.LinearLayout;

import com.rapidBizApps.lavasa.Controller.RFBuilder;
import com.rapidBizApps.lavasa.Listeners.FoundDynamicDataCallback;
import com.rapidBizApps.lavasa.Listeners.RFFormListener;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
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
            public void loadDynamicData(String jsonFieldKey, @Nullable String filter, FoundDynamicDataCallback callback) {
                List<String> items = new ArrayList<>();
                switch (jsonFieldKey) {
                    case "sports_page_select_sport":
                        items.add("Tennis");
                        items.add("Football");
                        break;

                    case "sports_page_select_school":
                        items.add("Hogwarts");
                        items.add("Ilvermony");
                        break;

                    case "sports_page_select_player":
                        if (filter != null) {
                            if (filter.equalsIgnoreCase("Tennis")) {
                                items.add("Roger Federer");
                                items.add("Rafael Nadal");
                            } else if (filter.equalsIgnoreCase("Football")) {
                                items.add("Lionel Messi");
                                items.add("Christiano Ronaldo");
                            }
                        }
                        break;

                    case "sports_page_select_slam":
                        if (filter != null) {
                            if (filter.equalsIgnoreCase("Roger Federer")) {
                                items.add("Wimbledon");
                                items.add("Australian Open");
                            } else if (filter.equalsIgnoreCase("Football")) {
                                items.add("French Open");
                                items.add("US Open");
                            }
                        }
                        break;

                    case "sports_page_select_year":
                        if (filter != null) {
                            if (filter.equalsIgnoreCase("Wimbledon")) {
                                items.add("2012");
                                items.add("2015");
                            } else if (filter.equalsIgnoreCase("US Open")) {
                                items.add("2013");
                                items.add("2014");
                            }
                        }
                        break;
                    default:
                }
                callback.callback(items);
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
}
