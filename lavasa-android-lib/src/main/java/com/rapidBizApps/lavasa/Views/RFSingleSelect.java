package com.rapidBizApps.lavasa.Views;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.View;

import com.rapidBizApps.lavasa.Listeners.FindDataInterface;
import com.rapidBizApps.lavasa.Listeners.FoundDataCallback;
import com.rapidBizApps.lavasa.Listeners.LoadDataInterface;
import com.rapidBizApps.lavasa.Listeners.RFElementEventListener;
import com.rapidBizApps.lavasa.Listeners.RFSpinnerListener;
import com.rapidBizApps.lavasa.Models.RFElementModel;
import com.rba.ui.MaterialEditText;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * Created by kkalluri on 2/4/2016.
 */
public class RFSingleSelect extends RFBaseElement {
    private MaterialEditText mMaterialEditText;
    private RFElementModel mElementModel;
    private RFElementEventListener mListener;
    private Context mContext;
    private int mSelectedPosition;
    private List<String> mKeyList, mValueList;
    private RFSingleSelectSpinner mSpinner;
    private LoadDataInterface loadDataInterface;
    LinkedHashMap<String, String> fetchedData = new LinkedHashMap<>();
    LinkedHashMap<String, String> dataMap;

    private static final String TAG = "RFSingleSelect";

    public RFSingleSelect(Context context, RFElementModel elementModel) {
        super(context, elementModel);
        mListener = getElementListeners();
        setElementModel(elementModel);
        setRFEditText(new MaterialEditText(context));
        mContext = context;
        loadDataInterface = (LoadDataInterface) mContext;
        init();
    }

    // All the initializations for the single selection will be present in the init() method
    // setUp() will be called in the init method
    private void init() {
        mMaterialEditText.setFocusable(false);
        mMaterialEditText.setClickable(true);

        mMaterialEditText.setSingleLine();

        mMaterialEditText.setHint(mElementModel.getName());

        mMaterialEditText.setErrorColor(Color.RED);

        mSpinner = new RFSingleSelectSpinner(mContext, new RFSpinnerListener() {
            @Override
            public void onDoneClick(List<String> list) {

            }

            @Override
            public void onDoneClick(int selectedPosition) {
                String selectedString = mValueList.get(selectedPosition);
                mSelectedPosition = selectedPosition;
                mMaterialEditText.setText(selectedString);
                mListener.onChange(RFSingleSelect.this, getData());
            }
        });

        dataMap = mElementModel.getJsonData();

        if (dataMap != null) {
            mKeyList = new ArrayList<>(dataMap.keySet());
            mValueList = new ArrayList<>(dataMap.values());
        } else {
            mKeyList = new ArrayList<>();
            mValueList = new ArrayList<>();
        }

        mSpinner.setItem(mValueList);

        mMaterialEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //check if the data is not already set. data to be loaded dynamically
                if (mElementModel.getJsonData() == null) {
                    loadDataInterface.loadDynamicData(mElementModel.getId(), new FoundDataCallback() {
                        @Override
                        public void callback(List<String> list) {
                            //forming the LinkedHashMap
                            for (int i = 0; i < list.size(); i++) {
                                fetchedData.put(i + "", list.get(i));
                            }


                            mElementModel.setJsonData(fetchedData);
                            dataMap = fetchedData;

                            mKeyList = new ArrayList<>(dataMap.keySet());
                            mValueList = new ArrayList<>(dataMap.values());

                            mSpinner.setItem(mValueList);
                            mSpinner.performClick();
                        }
                    });
                } else { //data not already set
                    mSpinner.performClick();
                }
            }
        });

        setUp(mMaterialEditText);

        mMaterialEditText.setFloatingLabelText(null);
        mMaterialEditText.setFloatingLabel(MaterialEditText.FLOATING_LABEL_HIGHLIGHT);
        mMaterialEditText.setFloatingLabelAnimating(true);
    }


    @Override
    public String getData() {

        JSONObject object = new JSONObject();
        try {
            object.put(mKeyList.get(mSelectedPosition), mValueList.get(mSelectedPosition));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return object.toString();
    }

    @Override
    protected void setData(String data) {

        mMaterialEditText.setFloatingLabelText(null);
        mMaterialEditText.setFloatingLabel(MaterialEditText.FLOATING_LABEL_HIGHLIGHT);
        mMaterialEditText.setFloatingLabelAnimating(true);

        if (data == null) {
            return;
        }

        try {
            JSONObject dataJsonObject = new JSONObject(data);
            JSONArray dataKeys = dataJsonObject.names();

            if (dataKeys == null || dataKeys.length() == 0) {
                return;
            }

            String selectedString = dataKeys.get(0).toString();
            mSelectedPosition = mKeyList.indexOf(selectedString);
            mSpinner.setSelection(mSelectedPosition);
            mMaterialEditText.setText(mValueList.get(mSelectedPosition));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void setError(String error) {
        if (mMaterialEditText != null) {
            mMaterialEditText.setError(error);
        }
    }

    private void setElementModel(RFElementModel elementModel) {
        mElementModel = elementModel;
    }

    private void setRFEditText(MaterialEditText materialEditText) {
        mMaterialEditText = materialEditText;
    }

}
