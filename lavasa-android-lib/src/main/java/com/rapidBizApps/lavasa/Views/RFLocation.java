package com.rapidBizApps.lavasa.Views;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.rapidBizApps.lavasa.Constants.RFConstants;
import com.rapidBizApps.lavasa.Listeners.RFElementEventListener;
import com.rapidBizApps.lavasa.Models.RFElementModel;
import com.rapidBizApps.lavasa.R;
import com.rapidBizApps.lavasa.RFMapActivity;
import com.rba.ui.MaterialEditText;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by cdara on 06-06-2016.
 */
public class RFLocation extends RFBaseElement {

    private RFElementModel mElementModel;
    private Context mContext;
    private RFElementEventListener mListeners;
    private LinearLayout mLocationView;
    private MaterialEditText mLatitude;
    private MaterialEditText mLongitude;
    private TextView mLocationLabel;
    private ImageView mLocationIcon;


    public RFLocation(Context context, RFElementModel elementModel) {
        super(context, elementModel);
        mContext = context;
        mElementModel = elementModel;
        mListeners = getElementListeners();

        init();
    }

    private void init() {
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mLocationView = (LinearLayout) inflater.inflate(R.layout.location_view, null);

        mLocationLabel = (TextView) mLocationView.findViewById(R.id.location_label);
        mLatitude = (MaterialEditText) mLocationView.findViewById(R.id.latitude);
        mLongitude = (MaterialEditText) mLocationView.findViewById(R.id.longitude);
        mLocationIcon = (ImageView) mLocationView.findViewById(R.id.location);


        mLocationLabel.setText(mElementModel.getName());


        mLatitude.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                mListeners.onChange(RFLocation.this, getData());
            }
        });

        mLongitude.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                mListeners.onChange(RFLocation.this, getData());
            }
        });


        mLocationIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mapIntent = new Intent((Activity) mContext, RFMapActivity.class);

                mapIntent.putExtra(RFConstants.FC_LATITUDE, mLatitude.getText().toString());
                mapIntent.putExtra(RFConstants.FC_LONGITUDE, mLongitude.getText().toString());

                RFMapActivity.setRFLocation(RFLocation.this);
                ((Activity) mContext).startActivity(mapIntent);
            }
        });


        setUp(mLocationView);

    }

    @Override
    public String getData() {
        JSONObject jsonObject = new JSONObject();

        try {
            jsonObject.put(RFConstants.FC_LATITUDE, mLatitude.getText().toString());
            jsonObject.put(RFConstants.FC_LONGITUDE, mLongitude.getText().toString());
        } catch (JSONException e) {

        }

        return jsonObject.toString();
    }

    @Override
    protected void setData(String data) {
        try {
            JSONObject jsonObject = new JSONObject(data);

            if (!jsonObject.isNull(RFConstants.FC_LATITUDE)) {
                mLatitude.setText(jsonObject.getString(RFConstants.FC_LATITUDE));
            }

            if (!jsonObject.isNull(RFConstants.FC_LONGITUDE)) {
                mLongitude.setText(jsonObject.getString(RFConstants.FC_LONGITUDE));
            }

        } catch (JSONException e) {

        }
    }

    @Override
    public void setError(String error) {

    }

    public void setLatAndLng(String latitude, String longitude) {

        mLatitude.setText(latitude);
        mLongitude.setText(longitude);

    }
}
