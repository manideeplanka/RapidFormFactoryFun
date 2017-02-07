package com.rapidBizApps.lavasa.Views;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import com.rapidBizApps.lavasa.Listeners.RFElementEventListener;
import com.rapidBizApps.lavasa.Models.RFElementModel;
import com.rapidBizApps.lavasa.R;
import com.rapidBizApps.lavasa.RFUtils;
import com.rba.ui.MaterialEditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Created by cdara on 01-03-2016.
 */
public class RFCreditCard extends RFBaseElement {

    private final String CARD_NUMBER = "cardNumber";
    private final String MONTH = "month";
    private final String YEAR = "year";
    private final String CVV = "cvv";
    private LinearLayout mCreditCardView;
    private MaterialEditText mCardNumber;
    private MaterialEditText mCVV;
    private MaterialEditText mMonth;
    private MaterialEditText mYear;
    private Context mContext;
    private RFElementEventListener mListener;
    private RFElementModel mElementModel;
    private HashMap<Pattern, Integer> mCreditCardPatterns;

    public RFCreditCard(Context context, RFElementModel elementModel) {
        super(context, elementModel);
        mContext = context;
        mElementModel = elementModel;
        mListener = getElementListeners();

        addCreditCardPatterns();
        init();
    }

    private void addCreditCardPatterns() {
        mCreditCardPatterns = new HashMap<>();

        Pattern visaPattern = Pattern.compile("^4[0-9]{12}(?:[0-9]{3})?$");
        mCreditCardPatterns.put(visaPattern, R.drawable.visa);

        Pattern masterCardPatter = Pattern.compile("^5[1-5][0-9]{14}$");
        mCreditCardPatterns.put(masterCardPatter, R.drawable.mastercard);

        Pattern americanExpress = Pattern.compile("^3[47][0-9]{13}$");
        mCreditCardPatterns.put(americanExpress, R.drawable.amex);

        Pattern dinnersClub = Pattern.compile("^3(?:0[0-5]|[68][0-9])[0-9]{11}$");
        mCreditCardPatterns.put(dinnersClub, R.drawable.dinners_club);

        Pattern discover = Pattern.compile("^6(?:011|5[0-9]{2})[0-9]{12}$ ");
        mCreditCardPatterns.put(discover, R.drawable.discover);

        Pattern jcb = Pattern.compile("^(?:2131|1800|35\\d{3})\\d{11}$");
        mCreditCardPatterns.put(jcb, R.drawable.jcb);
    }

    // All the initializations for the credit card will be present in the init() method
    // setUp() will be called in the init method
    public void init() {
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        mCreditCardView = (LinearLayout) inflater.inflate(R.layout.credit_card, null);

        mCardNumber = (MaterialEditText) mCreditCardView.findViewById(R.id.credit_card_number);
        mCVV = (MaterialEditText) mCreditCardView.findViewById(R.id.cvc);
        mMonth = (MaterialEditText) mCreditCardView.findViewById(R.id.expiry_month);
        mYear = (MaterialEditText) mCreditCardView.findViewById(R.id.expiry_year);

        mCardNumber.setHint(mElementModel.getName());

        // TODO : Need to fix the issue with the compound drawables.
        mCardNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                Iterator patternIterator = mCreditCardPatterns.entrySet().iterator();
                boolean isPatternMatched = false;
                while (patternIterator.hasNext()) {
                    Map.Entry<Pattern, Integer> currentItem = (Map.Entry) patternIterator.next();
                    Pattern currentPattern = currentItem.getKey();

                    if (currentPattern.matcher(mCardNumber.getText().toString()).matches()) {
                        mCardNumber.setCompoundDrawablesWithIntrinsicBounds(null, null, mContext.getResources().getDrawable(currentItem.getValue()), null);
                        isPatternMatched = true;
                        break;
                    }
                }

                if (!isPatternMatched) {
                    mCardNumber.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
                }

                mListener.onChange(RFCreditCard.this, getData());
            }
        });

        mCVV.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                mListener.onChange(RFCreditCard.this, getData());
            }
        });

        mMonth.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                mListener.onChange(RFCreditCard.this, getData());
            }
        });

        mYear.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                mListener.onChange(RFCreditCard.this, getData());
            }
        });

        mMonth.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus && mMonth.getText().toString() != null) {

                    try {
                        int month = Integer.parseInt(mMonth.getText().toString());
                        if (month < 1 || month > 12) {
                            mMonth.setError(RFUtils.getString(mContext, R.string.valid_month));
                        } else {
                            mMonth.setError(null);
                        }

                    } catch (NumberFormatException e) {

                    }

                }
            }
        });

        setUp(mCreditCardView);
    }

    @Override
    public void setError(String error) {

    }

    @Override
    public String getData() {
        JSONObject dataJson = new JSONObject();

        try {

            dataJson.put(CARD_NUMBER, mCardNumber.getText().toString());
            dataJson.put(CVV, mCVV.getText().toString());
            dataJson.put(MONTH, mMonth.getText().toString());
            dataJson.put(YEAR, mYear.getText().toString());

        } catch (JSONException e) {
            return null;
        }

        return dataJson.toString();
    }

    @Override
    protected void setData(String data) {

        if (data == null) {
            return;
        }

        try {
            JSONObject dataJson = new JSONObject(data);

            if (dataJson.has(CARD_NUMBER))
                mCardNumber.setText(dataJson.getString(CARD_NUMBER));

            if (dataJson.has(CVV))
                mCVV.setText(dataJson.getString(CVV));

            if (dataJson.has(MONTH))
                mMonth.setText(dataJson.getString(MONTH));

            if (dataJson.has(YEAR))
                mYear.setText(dataJson.getString(YEAR));

        } catch (JSONException e) {

        }
    }
}
