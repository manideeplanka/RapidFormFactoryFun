package com.rapidBizApps.lavasa;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.rapidBizApps.lavasa.Constants.RFConstants;
import com.rapidBizApps.lavasa.Views.RFLocation;

/**
 * Created by cdara on 06-06-2016.
 */
public class RFMapActivity extends Activity implements OnMapReadyCallback {

    private static RFLocation mLocation;
    private Marker mMarker;
    private Button mOkay;
    private Button mCancel;
    private String mLatitude;
    private String mLongitue;

    public static void setRFLocation(RFLocation location) {
        mLocation = location;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.map_view);

        mOkay = (Button) findViewById(R.id.location_okay);
        mCancel = (Button) findViewById(R.id.location_cancel);

        mOkay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mLocation.setLatAndLng(mLatitude, mLongitue);
                finish();
            }
        });

        mCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMapAsync(this);
    }

    @Override
    public void onMapReady(final GoogleMap googleMap) {
        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        if (getIntent().getExtras() != null) {
            String latitude = getIntent().getExtras().getString(RFConstants.FC_LATITUDE);
            String longitude = getIntent().getExtras().getString(RFConstants.FC_LONGITUDE);

            try {
                LatLng latLng = new LatLng(Double.parseDouble(latitude), Double.parseDouble(longitude));
                setMarker(googleMap, latLng);
                googleMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));

            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }

        googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                setMarker(googleMap, latLng);
                mLatitude = latLng.latitude + "";
                mLongitue = latLng.longitude + "";
            }
        });
    }

    private void setMarker(GoogleMap googleMap, LatLng latLng) {
        googleMap.clear();
        mMarker = googleMap.addMarker(new MarkerOptions().position(latLng));
    }
}
