package com.rapidBizApps.lavasa.Views;

import android.app.TimePickerDialog;
import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.widget.TimePicker;

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
public class RFTimePicker extends RFBaseElement {


    private MaterialEditText mMaterialEditText;
    private RFElementModel mElementModel;
    private RFElementEventListener mListener;
    private Context mContext;
    private TimePickerDialog mTimePicker;
    private SimpleDateFormat _24HourSDF;

    public RFTimePicker(Context context, RFElementModel elementModel) {
        super(context, elementModel);
        mListener = getElementListeners();
        setElementModel(elementModel);
        setRFEditText(new MaterialEditText(context));
        mContext = context;
        init();
    }


    // All the initializations for the time picker will be present in the init() method
    // setUp() will be called in the init method
    private void init() {
        mMaterialEditText.setFocusable(false);
        mMaterialEditText.setClickable(true);

        mMaterialEditText.setSingleLine();

        mMaterialEditText.setHint(mElementModel.getName());

        mMaterialEditText.setErrorColor(Color.RED);

        final Calendar calendar = Calendar.getInstance();
        final SimpleDateFormat _12HourSDF = new SimpleDateFormat("hh:mm a");

        _24HourSDF = new SimpleDateFormat("HH:mm");
        final TimePickerDialog.OnTimeSetListener timeSetListener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

                String time = "" + hourOfDay + ":" + minute;

                mMaterialEditText.setText(time);
                mListener.onChange(RFTimePicker.this, getData());

            }
        };
        mTimePicker = new TimePickerDialog(mContext, timeSetListener, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), false);

        mMaterialEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                mTimePicker.show();
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
            Date tempDate = _24HourSDF.parse(data);
            Calendar tempCal = dateToCalendar(tempDate);
            mTimePicker.updateTime(tempCal.get(Calendar.HOUR_OF_DAY), tempCal.get(Calendar.MINUTE));
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
