package com.rapidBizApps.lavasa.Views;

import android.app.DatePickerDialog;
import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.widget.DatePicker;

import com.rapidBizApps.lavasa.Listeners.RFElementEventListener;
import com.rapidBizApps.lavasa.Models.RFElementModel;
import com.rba.ui.MaterialEditText;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by kkalluri on 2/4/2016.
 */

public class RFDatePicker extends RFBaseElement {

    private MaterialEditText mMaterialEditText;
    private RFElementModel mElementModel;
    private RFElementEventListener mListener;
    private Context mContext;
    private SimpleDateFormat mDateFormat;
    private DatePickerDialog mDatePicker;

    public RFDatePicker(Context context, RFElementModel elementModel) {
        super(context, elementModel);
        mListener = getElementListeners();
        setElementModel(elementModel);
        setRFEditText(new MaterialEditText(context));
        mContext = context;
        init();
    }


    // All the initializations for the date picker will be present in the init() method
    // setUp() will be called in the init method
    private void init() {

        mMaterialEditText.setFocusable(false);
        mMaterialEditText.setClickable(true);

        mMaterialEditText.setSingleLine();

        mMaterialEditText.setHint(mElementModel.getName());

        mMaterialEditText.setErrorColor(Color.RED);

        final Calendar calendar = Calendar.getInstance();

        mDateFormat = new SimpleDateFormat("dd-MM-yyyy");
        final DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, monthOfYear);
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                mMaterialEditText.setText(mDateFormat.format(calendar.getTime()));
                mListener.onChange(RFDatePicker.this, getData());
            }

        };
        mDatePicker = new DatePickerDialog(mContext, dateSetListener, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));


        mMaterialEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mDatePicker.show();
            }
        });

        setUp(mMaterialEditText);

        mMaterialEditText.setFloatingLabelText(null);
        mMaterialEditText.setFloatingLabel(MaterialEditText.FLOATING_LABEL_HIGHLIGHT);
        mMaterialEditText.setFloatingLabelAnimating(true);

    }

    @Override
    public String getData() {
        return mMaterialEditText.getText().toString();
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
            mMaterialEditText.setText(data);
            Date tempDate = mDateFormat.parse(data);
            Calendar cal = dateToCalendar(tempDate);
            mDatePicker.updateDate(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DATE));
        } catch (ParseException e) {
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

    private Calendar dateToCalendar(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal;
    }
}
