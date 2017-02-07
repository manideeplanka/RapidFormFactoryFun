package com.rapidBizApps.lavasa.Views;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.rapidBizApps.lavasa.Listeners.RFElementEventListener;
import com.rapidBizApps.lavasa.Models.RFElementModel;
import com.rapidBizApps.lavasa.R;

import java.io.ByteArrayOutputStream;

/**
 * Created by cdara on 09-05-2016.
 */
public class RFImageView extends RFBaseElement {

    private final String TAG = RFImageView.class.getSimpleName();
    private RFElementModel mElementModel;
    private Context mContext;
    private LinearLayout mImageView;
    private ImageView mImage;
    private TextView mImageLabel;
    private RFElementEventListener mListener;

    public RFImageView(Context context, RFElementModel elementModel) {
        super(context, elementModel);
        mContext = context;
        mListener = getElementListeners();
        mElementModel = elementModel;
        init();
    }

    public void init() {
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mImageView = (LinearLayout) inflater.inflate(R.layout.image, null);
        mImage = (ImageView) mImageView.findViewById(R.id.image);
        mImageLabel = (TextView) mImageView.findViewById(R.id.image_label);

        mImageLabel.setText(mElementModel.getId());

        setUp(mImageView);
    }

    @Override
    public String getData() {
        if (mImage.getDrawable() == null) {
            return null;
        }

        Bitmap imageBitmap = ((BitmapDrawable) mImage.getDrawable()).getBitmap();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] b = baos.toByteArray();
        return Base64.encodeToString(b, Base64.DEFAULT);
    }

    @Override
    protected void setData(String data) {
        try {

            byte[] encodeByte = Base64.decode(data, Base64.DEFAULT);
            Bitmap imageBitmap = BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
            mImage.setImageBitmap(imageBitmap);

        } catch (Exception e) {
            Log.e(TAG + " setData", e + "");
        }
    }

    @Override
    public void setError(String error) {

    }
}
