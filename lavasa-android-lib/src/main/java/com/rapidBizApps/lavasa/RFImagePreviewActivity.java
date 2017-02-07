package com.rapidBizApps.lavasa;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;

/**
 * Created by cdara on 12-05-2016.
 */
public class RFImagePreviewActivity extends Activity {

    private final String TAG = RFImagePreviewActivity.class.getSimpleName();
    private String mImagePath;
    private ImageView mImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.image_preview);

        Intent receivedIntent = getIntent();
        mImageView = (ImageView) findViewById(R.id.image);

        if (receivedIntent.getExtras() != null) {

            Log.e(TAG, "onCreate: bundle ::"+receivedIntent.getExtras() );
            mImagePath = receivedIntent.getExtras().getString("imagePath");



            if (receivedIntent.getExtras().getString("fullImagePath") != null) {
                Log.e(TAG, "onCreate: path"+mImagePath);
                if ((new File(receivedIntent.getExtras().getString("fullImagePath"))).exists()) {
                    mImagePath = receivedIntent.getExtras().getString("fullImagePath");
                    mImageView.setImageBitmap(getImage(mImagePath));
                }
            }else{
                Log.e(TAG, "onCreate: else path"+mImagePath);
            }



            if(receivedIntent.getExtras().getString("awsUrl") != null){
                Log.e(TAG, "onCreate: awsurl ::"+receivedIntent.getExtras().getString("awsUrl"));
                if(receivedIntent.getExtras().getString("awsUrl").contains("http")){
                    loadImageFromURL(receivedIntent.getExtras().getString("awsUrl") , mImageView);
                }
            }else{
                Log.e(TAG, "onCreate: else awsurl ::");
            }
        }




    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    public Bitmap getImage(String imagePath) {

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
            }

            m.postRotate(rotationAngle);

            int width_tmp, height_tmp;
            if (bm != null) {
                final int REQUIRED_SIZE = getDensity(RFImagePreviewActivity.this);

                width_tmp = options.outWidth;
                height_tmp = options.outHeight;
                int scale = 1;
                while (true) {
                    if (width_tmp / 2 < REQUIRED_SIZE || height_tmp / 2 < REQUIRED_SIZE) {
                        break;
                    }
                    width_tmp /= 2;
                    height_tmp /= 2;
                    scale *= 2;
                }

                // Decode with inSampleSize
                BitmapFactory.Options o2 = new BitmapFactory.Options();
                o2.inSampleSize = scale;

                bm = BitmapFactory.decodeFile(imagePath, o2);
                bm = Bitmap.createBitmap(bm, 0, 0, (int) bm.getWidth(), (int) (bm.getHeight()), m, true);
            }
        } catch (IOException e) {
            Log.e(TAG + "RFCaptureImage", e + "");
        }

        return bm;
    }

    private static int getDensity(Context ctx) {
        DisplayMetrics metrics = ctx.getResources().getDisplayMetrics();
        switch (metrics.densityDpi) {
            case 560:
                return 200;
            case 480:
                return 300;
            case 320:
                return 700;
            case 240:
                return 700;
            case 160:
                return 700;
            default:
                return 700;
        }


    }

    public boolean loadImageFromURL(final String fileUrl, final ImageView iv) {

        Picasso.with(this).load(fileUrl).into(iv);
        return false;
    }

}
