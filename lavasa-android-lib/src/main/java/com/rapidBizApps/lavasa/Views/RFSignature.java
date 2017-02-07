package com.rapidBizApps.lavasa.Views;

import android.app.Activity;
import android.content.Context;
import android.gesture.GestureOverlayView;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.media.ExifInterface;
import android.os.Environment;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.rapidBizApps.lavasa.Listeners.RFElementEventListener;
import com.rapidBizApps.lavasa.Models.RFElementModel;
import com.rapidBizApps.lavasa.R;
import com.rapidBizApps.lavasa.RFUtils;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.UUID;

/**
 * Created by cdara on 09-05-2016.
 */
public class RFSignature extends RFBaseElement {

    public static final String IMAGES_FOLDER_NAME = ".rapidbizapps";
    private final String TAG = RFSignature.class.getSimpleName();
    private LinearLayout mSignatureView;
    private TextView mSignatureLabel;
    private GestureOverlayView mSignature;
    private Button mRetry;
    private RFElementEventListener mListener;
    private RFElementModel mElementModel;
    private Context mContext;
    private ImageMeta signatureImage;

    public RFSignature(Context context, RFElementModel elementModel) {
        super(context, elementModel);
        mContext = context;
        mListener = getElementListeners();
        mElementModel = elementModel;
        init();
    }

    private void init() {

        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mSignatureView = (LinearLayout) inflater.inflate(R.layout.signature, null);
        mSignatureLabel = (TextView) mSignatureView.findViewById(R.id.signature_label);
        mSignature = (GestureOverlayView) mSignatureView.findViewById(R.id.signature);
        mRetry = (Button) mSignatureView.findViewById(R.id.signature_retry);

        mSignatureLabel.setText(mElementModel.getName());

        mSignatureLabel.setText(mElementModel.getName());

        mSignature.setDrawingCacheEnabled(true);
        mSignature.setClickable(false);

        mSignature.setOnTouchListener(new View.OnTouchListener() {

            public boolean onTouch(View v, MotionEvent event) {
                mSignatureView.requestDisallowInterceptTouchEvent(true);
                return false;
            }
        });

        mSignature.addOnGestureListener(new GestureOverlayView.OnGestureListener() {
            @Override
            public void onGestureStarted(GestureOverlayView overlay, MotionEvent event) {
                mListener.onBeginEditing(RFSignature.this);
            }

            @Override
            public void onGesture(GestureOverlayView overlay, MotionEvent event) {

            }

            @Override
            public void onGestureEnded(GestureOverlayView overlay, MotionEvent event) {
                Log.e("KAR", "onGestureEnded");
                if (mElementModel.getStyle() != null & mElementModel.getStyle().isEditability()) {
                    mListener.onChange(RFSignature.this, getData());
                }

            }

            @Override
            public void onGestureCancelled(GestureOverlayView overlay, MotionEvent event) {
            }
        });

        mRetry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSignature.cancelClearAnimation();
                mSignature.clear(true);
            }
        });

        if (mElementModel.getStyle() != null & mElementModel.getStyle().isEditability()) {

        } else {
            mRetry.setVisibility(View.INVISIBLE);
        }

        setUp(mSignatureView);

    }

    @Override
    public String getData() {
        Bitmap signatureBitmap = Bitmap.createBitmap(mSignature.getDrawingCache());
        if (signatureBitmap == null || mSignature.getGesture() == null || mSignature.getGesture().getLength() == 0) {
            Log.e("KAR", "bitmap null");
            return "";
        } else {
            final File root = new File(Environment.getExternalStorageDirectory() + File.separator + IMAGES_FOLDER_NAME + File.separator);
            root.mkdirs();
            String imageName = UUID.randomUUID().toString() + ".png";
            final File sdImageMainDirectory = new File(root, imageName);

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            signatureBitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
            FileOutputStream outFile = null;
            byte[] bystes = baos.toByteArray();
            try {
                outFile = new FileOutputStream(sdImageMainDirectory);
                outFile.write(bystes);
                outFile.close();
            } catch (Exception e) {
                e.printStackTrace();
                return "";
            }
            Log.e("KAR", "bitmap path ::" + sdImageMainDirectory.getAbsolutePath());


            String thumbnail64 = Base64.encodeToString(bystes, Base64.DEFAULT);

            JSONArray array = new JSONArray();
            ImageMeta meta = new ImageMeta();
            meta.thumbnail64 = thumbnail64;
            meta.fullImagePath = sdImageMainDirectory.getAbsolutePath().replace("\"", "");
            meta.thumbnailImagePath = sdImageMainDirectory.getAbsolutePath().replace("\"", "");
            array.put(meta.toJson());
            return array.toString();

        }
    }

    /*@Override
    protected void setData(String data) {
        if (data == null) {
            return;
        }
        try {
            JSONArray arrayOfImages = new JSONArray(data);



            Log.e(TAG, "setData: arrayOfImages::"+arrayOfImages.toString() );
            String imagePath = arrayOfImages.get(0).toString();
            imagePath.replace("\"", "");

            Bitmap signatureBitmap = getThumbNailImage(imagePath);

            if(signatureBitmap != null){
                BitmapDrawable ob = new BitmapDrawable(mContext.getResources(), signatureBitmap);
                mSignature.setBackground(ob);
            }

            // TODO: need to check whether we are going to show the previous signature or not.

        } catch (Exception e) {
            Log.e(TAG + " setData", e + "");
        }
    }*/

    @Override
    protected void setData(String data) {

        if (data == null) {
            return;
        }
        try {
            JSONArray arrayOfImages = new JSONArray(data);
            signatureImage = ImageMeta.fromJson(arrayOfImages.getJSONObject(0));

            byte[] decodedString = Base64.decode(signatureImage.thumbnail64, Base64.DEFAULT);
            Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);

            if (decodedByte != null) {
                BitmapDrawable ob = new BitmapDrawable(mContext.getResources(), decodedByte);
                mSignature.setBackground(ob);
            }

            if(signatureImage.awsPath != null && signatureImage.awsPath.length()>0) {
                Picasso.with(mContext).load(signatureImage.awsPath).transform(new Transformation() {
                    @Override
                    public Bitmap transform(Bitmap source) {
                        final BitmapDrawable ob = new BitmapDrawable(mContext.getResources(), source);
                        ((Activity) mContext).runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mSignature.setBackground(ob);
                            }
                        });
                        return source;
                    }

                    @Override
                    public String key() {
                        return "dsasdasdasd";
                    }
                }).into(new ImageView(mContext));
            }

        } catch (JSONException e) {
            Log.e(TAG + " setData", e.toString());
        }

    }


    public Bitmap getThumbNailImage(String imagePath) {

        if (imagePath == null) {
            return null;
        }

        Bitmap bm = null;
        imagePath = imagePath.replace("\"", "");

        int rotationAngle = 0;
        Matrix m = new Matrix();

        try {
            int orientation;
            ExifInterface exif = new ExifInterface(imagePath);
            orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 1);

            final BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = false;
            options.inPreferredConfig = Bitmap.Config.RGB_565;
            bm = BitmapFactory.decodeFile(imagePath, options);

            if ((orientation == ExifInterface.ORIENTATION_ROTATE_180)) {
                rotationAngle = 180;
            } else if (orientation == ExifInterface.ORIENTATION_ROTATE_90) {
                rotationAngle = 90;
            } else if (orientation == ExifInterface.ORIENTATION_ROTATE_270) {
                rotationAngle = 270;
            } else if (orientation == ExifInterface.ORIENTATION_UNDEFINED) {
                if (RFUtils.getDeviceName().contains("Sam")) {
                    rotationAngle = 90;
                } else {
                    rotationAngle = 0;
                }
            }

            m.postRotate(rotationAngle);

            int width, height;
            if (bm != null) {
                // Decode with inSampleSize
                BitmapFactory.Options o2 = new BitmapFactory.Options();
                if (bm != null) {
                    bm = BitmapFactory.decodeFile(imagePath, o2);
                    bm = Bitmap.createBitmap(bm, 0, 0, (int) bm.getWidth(), (int) (bm.getHeight()), m, true);
                }
            }
        } catch (IOException e) {
            Log.e(TAG + "RFCaptureImage", e + "");
        }

        return bm;
    }

    @Override
    public void setError(String error) {

    }

    public static class ImageMeta {

        public String fullImagePath;
        public String thumbnailImagePath;
        public String awsPath;
        public String thumbnail64;
        public String uuid;

        public static ImageMeta fromJson(JSONObject imageObject) {

            ImageMeta imageMeta = new ImageMeta();
            try {
                if (imageObject.has("fullPath"))
                    imageMeta.fullImagePath = imageObject.getString("fullPath");
                else
                    imageMeta.fullImagePath="";

                if (imageObject.has("thumbnail64"))
                    imageMeta.thumbnail64 = imageObject.getString("thumbnail64");

                if (imageObject.has("thumbnail"))
                    imageMeta.thumbnailImagePath = imageObject.getString("thumbnail");
                else
                    imageMeta.thumbnailImagePath="";

                if (imageObject.has("aws"))
                    imageMeta.awsPath = imageObject.getString("aws");
                else
                    imageMeta.awsPath="";
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return imageMeta;
        }

        public JSONObject toJson() {

            JSONObject imageObj = new JSONObject();

            try {
                imageObj.put("fullPath", fullImagePath);
                imageObj.put("aws", null);
                imageObj.put("thumbnail", fullImagePath);
                imageObj.put("thumbnail64", thumbnail64);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return imageObj;
        }
    }

}
