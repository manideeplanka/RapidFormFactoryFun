package com.rapidBizApps.lavasa;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.location.Location;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.rapidBizApps.lavasa.Views.RFCaptureImage;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;
import java.util.UUID;


public class RFImagesActivity extends Activity implements GoogleApiClient.ConnectionCallbacks,LocationListener {

    public static final String IMAGES_FOLDER_NAME = ".rapidbizapps";
    private static RFCaptureImage mCaptureImage;
    private final int GALLERY = 2;
    private final int CAMERA = 1;
    private LinearLayout mCamera;
    private LinearLayout mGallery;
    private RelativeLayout mCancel;
    private String mImagePath;
    String imageName;
    private int STORAGE_PERMISSION = 100;
    private int LOCATION_PERMISSION = 200;

    private static long INTERVAL = 1000 * 3;
    private static long FASTEST_INTERVAL = 500 * 1;
    LocationRequest mLocationRequest;
    GoogleApiClient mGoogleApiClient;
    Location mCurrentLocation;
    private static final String TAG = "RFImagesActivity";

    private static Bitmap imgBitmap = null;
    private static String imgFilePath = null;

    public static void setCaptureImage(RFCaptureImage mCaptureImage) {
        RFImagesActivity.mCaptureImage = mCaptureImage;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_image);
        init();
        if (ActivityCompat.checkSelfPermission(this,Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(RFImagesActivity.this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},STORAGE_PERMISSION);
        }else {
            checkLocationPermission();
        }


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode == STORAGE_PERMISSION){
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                checkLocationPermission();
            }else{
                finish();
            }
        }else if(requestCode == LOCATION_PERMISSION){
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                initializeViews();
            }else{
                finish();
            }
        }
    }

    private void checkLocationPermission() {
        if (ActivityCompat.checkSelfPermission(RFImagesActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(RFImagesActivity.this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},LOCATION_PERMISSION);
        }else{
            initializeViews();
        }
    }

    private void initializeViews() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API).addConnectionCallbacks(this)
                .build();
        createLocationRequest();
        mGoogleApiClient.connect();
        //cameraClick();

    }



    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    private void init() {
        mCamera = (LinearLayout) findViewById(R.id.camera);
        mCamera.setVisibility(View.VISIBLE);
        mGallery = (LinearLayout) findViewById(R.id.gallery);
        mGallery.setVisibility(View.VISIBLE);
        mCancel = (RelativeLayout) findViewById(R.id.cancel) ;
        mCancel.setVisibility(View.VISIBLE);

        // I didn't implement OnClickListener because we cannot use ids in switch statement in libraries like projects @chandana
        mCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cameraClick();
            }
        });

        mGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                galleryClick();
            }
        });

        mCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void cameraClick() {
        File rootDirectory = new File(Environment.getExternalStorageDirectory() + File.separator + IMAGES_FOLDER_NAME + File.separator);
        rootDirectory.mkdirs();

        imageName = UUID.randomUUID().toString() + ".png";
        File imageFile = new File(rootDirectory, imageName);
        imgFilePath = imageFile.toString();

        Uri imageUri;
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
            imageUri = FileProvider.getUriForFile(this, getApplicationContext().getPackageName() + ".provider", imageFile);
        } else {
            imageUri = Uri.fromFile(imageFile);

        }
        mImagePath = imageUri.getEncodedPath();

        Intent captureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        captureIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        startActivityForResult(captureIntent, CAMERA);
    }

    private void galleryClick() {
        Intent galleryIntent = new Intent();
        galleryIntent.setType("image/*");
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);

        startActivityForResult(galleryIntent, GALLERY);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if(requestCode == CAMERA  && resultCode == RESULT_OK){
            Log.d(TAG, "onActivityResult: inside ");

            imgBitmap = BitmapFactory.decodeFile(imgFilePath);
            imgBitmap = scaleDownBitmap(imgBitmap,200,this);
            imgBitmap = imageOrientation(imgBitmap,imgFilePath);
            drawOnCanvas(imgBitmap);

        }

        if(requestCode == GALLERY && data != null ) {
            Uri selectedImage = data.getData();
            try {
                imgBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);  // Getting the Image Bitmap ..
            } catch (IOException e) {
                e.printStackTrace();
            }


            /* Getting the path of the Image File.. */

            String path = null;
            Log.d(TAG, "onActivityResult: bitmap : "+imgBitmap);
            Log.d(TAG, "onActivityResult: uri : "+imgBitmap);
            if (selectedImage.getPath().contains(".jpeg") || selectedImage.getPath().contains(".jpg") || selectedImage.getPath().contains(".png")) {
                Log.d(TAG, "onActivityResult: one");
                path = selectedImage.getPath();
            } else if (getRealPathFromURI(selectedImage) != null) {
                Log.d(TAG, "onActivityResult: two");
                path = getRealPathFromURI(selectedImage);
            } else if (selectedImage.toString().contains("content://com.android.providers")) {
                Log.d(TAG, "onActivityResult: three");

                path = getRealPathFromURI_API19(this, selectedImage);
                Log.d(TAG, "onActivityResult: path 1 : " + path);
            }else if ("com.google.android.apps.docs.storage".equals(selectedImage.getAuthority())) {
                Log.d(TAG, "onActivityResult: five");

                try {
                    imgBitmap  = BitmapFactory.decodeStream(getContentResolver().openInputStream(selectedImage));
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            } else if (selectedImage.toString().contains("content://com.android.externalstorage.documents/document/")) {
                Log.d(TAG, "onActivityResult: four");

                String wholeID = null;

                if (Build.VERSION.SDK_INT >= 19) {
                    wholeID = DocumentsContract.getDocumentId(selectedImage);
                }

                // Split at colon, use second item in the array
                String remainingPath = wholeID.split(":")[1];

                String storageLocation = selectedImage.toString().substring(selectedImage.toString().lastIndexOf("/") + 1, selectedImage.toString().indexOf("%"));

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    if (Objects.equals(storageLocation, "primary"))
                        path = "/storage/emulated/0/" + remainingPath;
                    else
                        path = "/storage/" + storageLocation + '/' + remainingPath;
                }

                Log.d(TAG, "onActivityResult: new Path: " + path);

            }


            imgBitmap = scaleDownBitmap(imgBitmap, 200, this);

            Log.d(TAG, "onActivityResult: newimage bitmap 1" + imgBitmap);

            if(path != null)
                imgBitmap = imageOrientation(imgBitmap, path);

            drawOnCanvas(imgBitmap);

        }

        mGoogleApiClient.disconnect();
        finish();
    }

    private void drawOnCanvas(Bitmap imgBitmap) {
        Bitmap  alteredBitmap = Bitmap.createBitmap( imgBitmap.getWidth(), imgBitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(alteredBitmap);
        Paint paint = new Paint();
        canvas.drawBitmap(imgBitmap, 0, 0, paint);

        paint.setColor(Color.WHITE);
        int textsize = imgBitmap.getWidth()/20;
        paint.setTextSize(textsize);
        paint.setTypeface(Typeface.create(Typeface.DEFAULT_BOLD, Typeface.BOLD));

        SimpleDateFormat currentDate = new SimpleDateFormat("dd/MM/yyyy");
        SimpleDateFormat currentTime = new SimpleDateFormat("hh:mm");
        Date todayDate = new Date();
        String date = currentDate.format(todayDate);
        String time = currentTime.format(todayDate);

        String location ="";

        if(mCurrentLocation!=null){
            location = mCurrentLocation.getLatitude()+" , "+mCurrentLocation.getLongitude();
        }
        canvas.drawText(" Location:"+location, 0, canvas.getHeight() - 100, paint);
        canvas.drawText(" Date:"+date+" , time: "+time, 0, 100, paint);

        final File root = new File(Environment.getExternalStorageDirectory() + File.separator + IMAGES_FOLDER_NAME + File.separator);
        root.mkdirs();

        imageName = UUID.randomUUID() + ".png";

        final File sdImageMainDirectory = new File(root, imageName);

        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        alteredBitmap.compress(Bitmap.CompressFormat.JPEG, 20, bytes);
        FileOutputStream outFile = null;
        try {
            outFile = new FileOutputStream(sdImageMainDirectory);
            outFile.write(bytes.toByteArray());
            outFile.close();
            alteredBitmap.recycle();
            mImagePath = sdImageMainDirectory.getAbsolutePath();
            mCaptureImage.addImageToView(mImagePath);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /** Checking the Orientation of the image and sending the appropriate rotated Bitmap..
     *
     * @param bitmap - Bitmap of the image which is to be checked for it's orientation
     * @param path - Path of that image , where it is stored in the device
     * @return - Returns the appropriate Rotated Image bitmap
     */
    private static Bitmap imageOrientation(Bitmap bitmap, String path){

        ExifInterface ei = null;
        try {
            ei = new ExifInterface(path);
        } catch (IOException e) {
            e.printStackTrace();
        }
        int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                ExifInterface.ORIENTATION_UNDEFINED);

        switch(orientation) {
            case ExifInterface.ORIENTATION_ROTATE_90:
                Log.d(TAG, "onActivityResult: 90");
                bitmap = rotateImage(bitmap, 90);
                break;
            case ExifInterface.ORIENTATION_ROTATE_180:
                Log.d(TAG, "onActivityResult: 180");
                bitmap = rotateImage(bitmap, 180);
                break;
            case ExifInterface.ORIENTATION_ROTATE_270:
                Log.d(TAG, "onActivityResult: 270");
                bitmap = rotateImage(bitmap, 270);
                break;
            case ExifInterface.ORIENTATION_NORMAL:
            default:
                Log.d(TAG, "onActivityResult: default");
                break;
        }
        return bitmap;
    }


    /** Scaling the size of the Bitmap..
     *
     * @param photo- Bitmap of the image to be scaled down
     * @param newHeight- Height to which it should be scaled
     * @param context- Context
     * @return - Returns a scaled bitmap with the desired height
     */
    private static Bitmap scaleDownBitmap(Bitmap photo, int newHeight, Context context) {

        final float densityMultiplier = context.getResources().getDisplayMetrics().density;

        int h= (int) (newHeight*densityMultiplier);
        int w= (int) (h * photo.getWidth()/((double) photo.getHeight()));

        photo=Bitmap.createScaledBitmap(photo, w, h, true);

        return photo;
    }


    /** Rotating the bitmap to an angle that makes the resultant bitmap appear straight..
     *
     * @param source - Image bitmap to be oriented
     * @param angle - Orientation angle of the image
     * @return - Returns correctly oriented image.
     */
    private static Bitmap rotateImage(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix,   true);
    }

    /** This gives the path of the Images where they are being stored..
     *
     * @param contentUri - Image URI for which the path is to be found..
     * @return - Path of the Image
     */
    private String getRealPathFromURI(Uri contentUri)    {
        String[] proj = { MediaStore.Audio.Media.DATA };
        Cursor cursor = managedQuery(contentUri, proj, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }

    /** This gives the path of the Images where they are being stored in devices having API level above 19..
     *
     * @param context - Context
     * @param uri - Image URI for which the path is to be found..
     * @return - Path of the Image
     */
    private static String getRealPathFromURI_API19(Context context, Uri uri){
        String filePath = "";
        String wholeID = null;

        if (Build.VERSION.SDK_INT >= 19) {
            wholeID = DocumentsContract.getDocumentId(uri);
        }

        // Split at colon, use second item in the array
        String id = wholeID.split(":")[1];
        String[] column = { MediaStore.Images.Media.DATA };

        // where id is equal to
        String sel = MediaStore.Images.Media._ID + "=?";

        Cursor cursor = context.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                column, sel, new String[]{ id }, null);


        int columnIndex = cursor.getColumnIndex(column[0]);


        if (cursor.moveToFirst()) {
            filePath = cursor.getString(columnIndex);
        }
        cursor.close();
        return filePath;
    }

    @Override
    public void onConnected(Bundle bundle) {
        startLocationUpdates();
    }

    protected void startLocationUpdates() {
        PendingResult<Status> pendingResult = LocationServices.FusedLocationApi.requestLocationUpdates( mGoogleApiClient, mLocationRequest, this);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onLocationChanged(Location location) {
        Log.e("RFImage","onlocationchanged");
        mCurrentLocation = location;
    }
}
