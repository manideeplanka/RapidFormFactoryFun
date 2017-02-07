package com.rapidBizApps.lavasa.Views;

import android.content.Context;
import android.graphics.Color;
import android.view.View;

import com.rapidBizApps.lavasa.Listeners.RFElementEventListener;
import com.rapidBizApps.lavasa.Listeners.RFSpinnerListener;
import com.rapidBizApps.lavasa.Models.RFElementModel;
import com.rba.ui.MaterialEditText;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * Created by kkalluri on 2/3/2016.
 */
public class RFMultiSelect extends RFBaseElement {

    private RFMultiSelectSpinner mMultiSelectSpinner;
    private MaterialEditText mMaterialEditText;
    private RFElementModel mElementModel;
    private RFElementEventListener mListener;
    private Context mContext;
    private List<String> mSelectedIDsList;

    public RFMultiSelect(Context context, RFElementModel elementModel) {
        super(context, elementModel);
        mListener = getElementListeners();
        setElementModel(elementModel);

        mContext = context;
        setRFMultiSelect(new MaterialEditText(mContext));

        init();
    }


    // All the initializations for the multi select spinner will be present in the init() method
    // setUp() will be called in the init method
    private void init() {
        mMaterialEditText.setFocusable(false);
        mMaterialEditText.setClickable(true);

        mMaterialEditText.setHint(mElementModel.getName());

        mMaterialEditText.setErrorColor(Color.RED);

        mMultiSelectSpinner = new RFMultiSelectSpinner(mContext, new RFSpinnerListener() {
            @Override
            public void onDoneClick(List<String> list) {
                mSelectedIDsList = list;
                mMaterialEditText.setText(mMultiSelectSpinner.buildSelectedItemString());
                mListener.onChange(RFMultiSelect.this, getData());
            }

            @Override
            public void onDoneClick(int selectedPosition) {

            }
        });

        mMaterialEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMultiSelectSpinner.performClick();
            }
        });

        LinkedHashMap<String, String> dataMap = mElementModel.getJsonData();

        List<String> keyList = new ArrayList<>(dataMap.keySet());
        List<String> valueList = new ArrayList<>(dataMap.values());

        mMultiSelectSpinner.setItems(valueList, keyList);

        setUp(mMaterialEditText);

        mMaterialEditText.setFloatingLabelText(null);
        mMaterialEditText.setFloatingLabel(MaterialEditText.FLOATING_LABEL_HIGHLIGHT);
        mMaterialEditText.setFloatingLabelAnimating(true);

    }

    private void setRFMultiSelect(MaterialEditText materialEditText) {
        mMaterialEditText = materialEditText;
    }

    @Override
    public String getData() {
        JSONArray jsonArray = new JSONArray(mSelectedIDsList);
        return jsonArray.toString();
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
            JSONArray jsonArray = new JSONArray(data);
            mSelectedIDsList = convertJsonArrayToList(jsonArray);
            mMultiSelectSpinner.setSelection(mSelectedIDsList);
            mMaterialEditText.setText(mMultiSelectSpinner.buildSelectedItemString());

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

    // This will converts the json array to list
    private List convertJsonArrayToList(JSONArray jsonArray) {

        List<String> list = new ArrayList<String>();
        if (jsonArray != null) {
            int len = jsonArray.length();
            for (int i = 0; i < len; i++) {
                try {
                    list.add(jsonArray.get(i).toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        return list;
    }
}
