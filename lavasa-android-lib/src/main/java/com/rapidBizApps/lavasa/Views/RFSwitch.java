package com.rapidBizApps.lavasa.Views;

import android.content.Context;
import android.widget.CompoundButton;
import android.widget.Switch;

import com.rapidBizApps.lavasa.Listeners.RFElementEventListener;
import com.rapidBizApps.lavasa.Models.RFElementModel;

/**
 * Created by kkalluri on 2/4/2016.
 */
public class RFSwitch extends RFBaseElement {
    private Switch mSwitch;
    private RFElementModel mElementModel;
    private RFElementEventListener mListener;
    private Context mContext;
    private boolean mIsBoolean;

    public RFSwitch(Context context, RFElementModel elementModel) {
        super(context, elementModel);
        mContext = context;
        mListener = getElementListeners();
        setElementModel(elementModel);
        setRFSwitch(new Switch(mContext));

        init();
    }

    // All the initializations for the switch will be present in the init() method
    // setUp() will be called in the init method
    private void init() {
        setUp(mSwitch);
        mSwitch.setText(mElementModel.getName());
        mListener.onChange(RFSwitch.this, getData());
        mSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mListener.onChange(RFSwitch.this, getData());
                mIsBoolean = isChecked;
            }
        });


    }


    @Override
    public String getData() {
        return mSwitch.isChecked() + "";
    }

    @Override
    protected void setData(String data) {
        mSwitch.setChecked(Boolean.parseBoolean(data));
    }

    @Override
    public void setError(String error) {

    }

    private void setElementModel(RFElementModel elementModel) {
        mElementModel = elementModel;
    }

    private void setRFSwitch(Switch rfSwitch) {
        mSwitch = rfSwitch;
    }

}
