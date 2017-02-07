package com.rapidBizApps.lavasa.Views;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.support.v4.content.FileProvider;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.rapidBizApps.lavasa.Listeners.RFElementEventListener;
import com.rapidBizApps.lavasa.Models.RFElementModel;
import com.rapidBizApps.lavasa.R;
import com.rapidBizApps.lavasa.RFImagePreviewActivity;
import com.rapidBizApps.lavasa.RFImagesActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.UUID;

import static com.rapidBizApps.lavasa.RFImagesActivity.IMAGES_FOLDER_NAME;

/**
 * Created by cdara on 10-05-2016.
 */
public class RFCaptureImage extends RFBaseElement {

    private static String saperator = "#.#.#.#.#.#.#.#.#.#.#.#.#.#.#.#.#.#.#.#.#.#.#.#.#.#.";
    private final String TAG = RFCaptureImage.class.getSimpleName();
    private RFElementModel mElementModel;
    private LinearLayout mCaptureImageView;
    private TextView mCaptureImageLabel;
    private LinearLayout mAddImage;
    private LinearLayout mImagesList;
    private Context mContext;
    private LayoutInflater mInflater;
    private RFElementEventListener mListener;
    private ArrayList<ImageMeta> fieldImages = new ArrayList<>();

    public RFCaptureImage(Context context, RFElementModel elementModel) {
        super(context, elementModel);
        mContext = context;
        mElementModel = elementModel;
        mListener = getElementListeners();
        init();
    }

    private void init() {
        mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        mCaptureImageView = (LinearLayout) mInflater.inflate(R.layout.capture_image, null);
        mCaptureImageLabel = (TextView) mCaptureImageView.findViewById(R.id.capture_image_label);
        mAddImage = (LinearLayout) mCaptureImageView.findViewById(R.id.add_image);
        mImagesList = (LinearLayout) mCaptureImageView.findViewById(R.id.list_of_images);

        mCaptureImageLabel.setText(mElementModel.getName());

        mAddImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mImagesList.getChildCount() <= 4)
                    addImageClick();
                else
                    Toast.makeText(mContext, "You can't add more than five images.", Toast.LENGTH_SHORT).show();
            }
        });

        setUp(mCaptureImageView);
    }

    @Override
    public String getData() {
        if (mImagesList.getChildCount() == 0) {
            return null;
        }

        JSONArray arrayOfImages = new JSONArray();

        for (int i = 0; i < fieldImages.size(); i++) {
            //arrayOfImages.put(mImagesList.getChildAt(i).getTaggit sta ());
            JSONObject imageObj = new JSONObject();
            arrayOfImages.put(fieldImages.get(i).toJson());
        }
        return arrayOfImages.toString();
    }

    @Override
    protected void setData(String data) {

        if (data == null) {
            return;
        }
        try {
            JSONArray arrayOfImages = new JSONArray(data);
            for (int i = 0; i < arrayOfImages.length(); i++) {
                fieldImages.add(ImageMeta.fromJson(arrayOfImages.getJSONObject(i)));
            }
            reRenderImageList();
        } catch (JSONException e) {
            Log.e(TAG + " setData", e.toString());
        }

    }

    @Override
    public void setError(String error) {

    }


    private void addImageClick() {
        RFImagesActivity.setCaptureImage(this);
        Intent intent = new Intent(((Activity) mContext), RFImagesActivity.class);
        ((Activity) mContext).startActivity(intent);

    }

    public void addImageToView(String imagePath) {  // this will be called from RFImageActivity

        ImageMeta newImageMeta = new ImageMeta();
        newImageMeta.fullImagePath = imagePath;
        String thumbnail = getThumbNailImage(imagePath);
        String[] tokens = thumbnail.split(saperator);
        newImageMeta.thumbnailImagePath = tokens[0];
        newImageMeta.thumbnail64 = tokens[1];
        newImageMeta.awsPath = null;
        fieldImages.add(newImageMeta);
        reRenderImageList();

    }

    private void reRenderImageList() {

        mImagesList.removeAllViews();
        for (final ImageMeta m : fieldImages) {
            RelativeLayout imageLayout = (RelativeLayout) mInflater.inflate(R.layout.captured_image, null);
            m.fullImagePath.replace("\"", "");
            imageLayout.setTag(m.fullImagePath);
            ImageView imageView = (ImageView) imageLayout.findViewById(R.id.captured_image);
            ImageView cancelView = (ImageView) imageLayout.findViewById(R.id.cancel_captured_image);

            if (new File(m.thumbnailImagePath).exists()) {//just in case path is passed instead of base 64 string

                Uri photoUri;
                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
                    photoUri = FileProvider.getUriForFile(mContext, mContext.getApplicationContext().getPackageName() + ".provider", new File(m.thumbnailImagePath));
                } else {
                    photoUri = Uri.fromFile(new File(m.thumbnailImagePath));

                }

                imageView.setImageURI(photoUri);
            } else {
                byte[] decodedString = Base64.decode(m.thumbnail64, Base64.DEFAULT);
                Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                imageView.setImageBitmap(decodedByte);
            }


            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent imagePreview = new Intent(((Activity) (mContext)), RFImagePreviewActivity.class);

                    imagePreview.putExtra("imagePath", m.thumbnailImagePath);
                    imagePreview.putExtra("fullImagePath", m.fullImagePath);
                    imagePreview.putExtra("awsUrl", m.awsPath);
                    ((Activity) (mContext)).startActivity(imagePreview);
                }
            });

            cancelView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mElementModel.getStyle() != null & mElementModel.getStyle().isEditability()) {

                        int i = mImagesList.indexOfChild((View) v.getParent());
                        fieldImages.remove(i);
                        reRenderImageList();
                    }
                }
            });

            mImagesList.addView(imageLayout);
        }
        mListener.onChange(RFCaptureImage.this, getData());

    }


    public String getThumbNailImage(String imagePath) {

        Bitmap thumbNail = null;
        imagePath = imagePath.replace("\"", "");

        int rotationAngle = 0;
        Matrix m = new Matrix();

        try {
            Log.e(TAG, "getThumbNailImage: path ::"+imagePath );
            int orientation;
            ExifInterface exif = new ExifInterface(imagePath);
            orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 1);

            final BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = false;
            options.inPreferredConfig = Bitmap.Config.RGB_565;
            thumbNail = BitmapFactory.decodeFile(imagePath, options);
            Log.e("BITMAP CREATION ", "getThumbNailImage: orientation::" + orientation);
            if ((orientation == ExifInterface.ORIENTATION_ROTATE_180)) {
                rotationAngle = 180;
            } else if (orientation == ExifInterface.ORIENTATION_ROTATE_90) {
                rotationAngle = 90;
            } else if (orientation == ExifInterface.ORIENTATION_ROTATE_270) {
                rotationAngle = 270;
            }

            m.postRotate(rotationAngle);

            int widthTemp = options.outWidth, heightTemp = options.outHeight;

            int scale = 1;
            final int REQUIRED_SIZE = 50;
            while (true) {
                if (widthTemp / 2 < REQUIRED_SIZE || heightTemp / 2 < REQUIRED_SIZE) {
                    break;
                }
                heightTemp /= 2;
                widthTemp /= 2;
                scale *= 2;
            }

            // Decode with inSampleSize
            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize = scale;

            if (thumbNail != null) {
                thumbNail = BitmapFactory.decodeFile(imagePath, o2);
                thumbNail = Bitmap.createBitmap(thumbNail, 0, 0, (int) thumbNail.getWidth(), (int) (thumbNail.getHeight()), m, true);
            }

        } catch (Exception e) {
            Log.e("BITMAP CREATION", e + "");
        }

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        thumbNail.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] bystes = baos.toByteArray();

        String thumbnail64 = Base64.encodeToString(bystes, Base64.DEFAULT);

        String outputFilePath = Environment.getExternalStorageDirectory() + File.separator + IMAGES_FOLDER_NAME + File.separator + "thumbnail" + File.separator;
        File f = new File(outputFilePath);
        f.mkdirs();

        String filename= UUID.randomUUID() + ".png";

        File outputFile = new File(f,filename);
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(outputFile);
            thumbNail.compress(Bitmap.CompressFormat.PNG, 100, out); // bmp is your Bitmap instance
        } catch (Exception e) {
            e.printStackTrace();
            outputFile = new File(imagePath); //just in case of error in case  error
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        thumbNail.recycle();
        return outputFilePath+filename + saperator + thumbnail64;
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
                imageObj.put("thumbnail", thumbnailImagePath);
                imageObj.put("aws", null);
                imageObj.put("thumbnail64", thumbnail64);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return imageObj;
        }
    }
}
