package com.dbvertex.dilsayproject.UserAuth;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.databinding.DataBindingUtil;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.dbvertex.dilsayproject.EndPoints;
import com.dbvertex.dilsayproject.ImageLoadingUtils;
import com.dbvertex.dilsayproject.Model.ReligionDTO;
import com.dbvertex.dilsayproject.Network.ConnectivityReceiver;
import com.dbvertex.dilsayproject.Network.MyApplication;
import com.dbvertex.dilsayproject.R;
import com.dbvertex.dilsayproject.VolleyMultipartRequest;
import com.dbvertex.dilsayproject.databinding.ActivityUploadPicsBinding;
import com.dbvertex.dilsayproject.session.SessionManager;
import com.google.android.material.snackbar.Snackbar;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.DexterError;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.PermissionRequestErrorListener;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.READ_CONTACTS;
import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class UploadPicsActivity extends AppCompatActivity implements
        ConnectivityReceiver.ConnectivityReceiverListener {
    ActivityUploadPicsBinding binding;
    ProgressDialog progressDialog;
    RequestQueue queue;
    SessionManager sessionManager;
    private ArrayList<Bitmap> photoBMList;
    private final int REQUEST_CODE_FROM_GALLERY = 01;
    private final int REQUEST_CODE_CLICK_IMAGE = 02;
    Bitmap scaledBitmap = null;
    private int STORAGE_REQUEST = 99, SELECT_FILE = 1;
    ImageLoadingUtils utils;
    Uri imageUri;
    String from;
    public static final int PERMISSION_REQUEST = 100;
    boolean isConnected;
    byte[] profilePicbyte = null;
    private RequestQueue rQueue;
    Intent intent;

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_upload_pics);
        progressDialog = new ProgressDialog(UploadPicsActivity.this, R.style.CustomDialog);
        queue = Volley.newRequestQueue(UploadPicsActivity.this);
        sessionManager = new SessionManager(this);
        utils = new ImageLoadingUtils(UploadPicsActivity.this);
        photoBMList = new ArrayList<>();
        intent=getIntent();


        Toolbar toolbar_main = findViewById(R.id.toolbar_main);
        TextView titleTV = (TextView) toolbar_main.findViewById(R.id.titleTV);
        LinearLayout back_LL = toolbar_main.findViewById(R.id.back_LL);
        titleTV.setText("Add Pictures");

        back_LL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (intent.hasExtra("from"))
                {
                    Intent intent = new Intent(UploadPicsActivity.this, HomePageActivity.class);
                    intent.putExtra("from","MyProfileFragment");
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP );
                    startActivity(intent);
                    overridePendingTransition(R.anim.trans_left_in,
                            R.anim.trans_left_out);
                }
                else
                {
                    finish();
                }

            }
        });


        if (!sessionManager.getFacebookData().get(SessionManager.KEY_FACEBOOKDP).isEmpty()) {
            Glide.with(getApplicationContext()).load(sessionManager.getFacebookData().get(SessionManager.KEY_FACEBOOKDP))
                    .thumbnail(0.5f).centerCrop()
                    .diskCacheStrategy(DiskCacheStrategy.ALL).placeholder(R.drawable.defaultpic)
                    .into(binding.im1);
        }

        binding.im2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                from = "im2";
                opengalcamDialog();
            }
        });

        binding.im3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                from = "im3";
                opengalcamDialog();
            }
        });

        binding.im4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                from = "im4";
                opengalcamDialog();
            }
        });

        binding.im5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                from = "im5";
                opengalcamDialog();
            }
        });

        binding.im6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                from = "im6";
                opengalcamDialog();
            }
        });


        binding.continueBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (isConnected) {
                    uploadImage();
                } else {
                    showSnack(isConnected);
                }


            }
        });


        openPhotoGuidelinesDialog();


    }

    private void openPhotoGuidelinesDialog() {


        final Dialog dialog = new Dialog(UploadPicsActivity.this, R.style.CustomDialog);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.item_photoguidelines);
        dialog.setCanceledOnTouchOutside(false);
        dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        Button continueBtn = dialog.findViewById(R.id.continueBtn);

        TextView titleTV = dialog.findViewById(R.id.titleTV);
        LinearLayout back_LL = dialog.findViewById(R.id.back_LL);
        titleTV.setText("Photo Guidelines");


        back_LL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                mayRequestPermissions();

                loadImage();
            }
        });

        continueBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog.dismiss();
                mayRequestPermissions();
                loadImage();

            }
        });


        dialog.show();
    }


    private void opengalcamDialog() {
        final Dialog dialog = new Dialog(UploadPicsActivity.this, R.style.CustomDialog);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.item_gallerycamera);
        dialog.setCanceledOnTouchOutside(false);
        dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        TextView cameraLBL = (TextView) dialog.findViewById(R.id.cameraLBL);
        TextView gallLBL = (TextView) dialog.findViewById(R.id.gallLBL);
        ImageView crossIV = (ImageView) dialog.findViewById(R.id.crossIV);

        crossIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.cancel();
            }
        });

        cameraLBL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);


                ContentValues values = new ContentValues();
                values.put(MediaStore.Images.Media.TITLE, "New Picture");
                values.put(MediaStore.Images.Media.DESCRIPTION, "From your Camera");
                imageUri = getContentResolver().insert(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                startActivityForResult(intent, REQUEST_CODE_CLICK_IMAGE);
                dialog.cancel();
            }
        });

        gallLBL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(i, REQUEST_CODE_FROM_GALLERY);
                dialog.cancel();
            }

        });
        dialog.show();
    }


    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case REQUEST_CODE_FROM_GALLERY:
                    new ImageCompressionAsyncTask().execute(data.getDataString());

                    break;
                case REQUEST_CODE_CLICK_IMAGE:
                    try {

                        String imageurl = getRealPathFromURI(imageUri);
                        new ImageCompressionAsyncTask().execute(imageurl);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    break;
            }
        }
    }

    public String getRealPathFromURI(Uri contentUri) {
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = getContentResolver().query(contentUri, proj, null, null, null);
        int column_index = cursor
                .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }

    class ImageCompressionAsyncTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            String filePath = compressImage(params[0]);
            return filePath;
        }

        public String compressImage(String imageUri) {

            String filePath = getRealPathFromURI(imageUri);


            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            Bitmap bmp = BitmapFactory.decodeFile(filePath, options);

            int actualHeight = options.outHeight;
            int actualWidth = options.outWidth;
            float maxHeight = 816.0f;
            float maxWidth = 612.0f;
            float imgRatio = actualWidth / actualHeight;
            float maxRatio = maxWidth / maxHeight;

            if (actualHeight > maxHeight || actualWidth > maxWidth) {
                if (imgRatio < maxRatio) {
                    imgRatio = maxHeight / actualHeight;
                    actualWidth = (int) (imgRatio * actualWidth);
                    actualHeight = (int) maxHeight;
                } else if (imgRatio > maxRatio) {
                    imgRatio = maxWidth / actualWidth;
                    actualHeight = (int) (imgRatio * actualHeight);
                    actualWidth = (int) maxWidth;
                } else {
                    actualHeight = (int) maxHeight;
                    actualWidth = (int) maxWidth;

                }
            }

            options.inSampleSize = utils.calculateInSampleSize(options, actualWidth, actualHeight);
            options.inJustDecodeBounds = false;
            options.inDither = false;
            options.inPurgeable = true;
            options.inInputShareable = true;
            options.inTempStorage = new byte[16 * 1024];

            try {
                bmp = BitmapFactory.decodeFile(filePath, options);
            } catch (OutOfMemoryError exception) {
                exception.printStackTrace();

            }
            try {
                scaledBitmap = Bitmap.createBitmap(actualWidth, actualHeight, Bitmap.Config.ARGB_8888);
            } catch (OutOfMemoryError exception) {
                exception.printStackTrace();
            }

            float ratioX = actualWidth / (float) options.outWidth;
            float ratioY = actualHeight / (float) options.outHeight;
            float middleX = actualWidth / 2.0f;
            float middleY = actualHeight / 2.0f;

            Matrix scaleMatrix = new Matrix();
            scaleMatrix.setScale(ratioX, ratioY, middleX, middleY);

            Canvas canvas = new Canvas(scaledBitmap);
            canvas.setMatrix(scaleMatrix);
            canvas.drawBitmap(bmp, middleX - bmp.getWidth() / 2, middleY - bmp.getHeight() / 2, new Paint(Paint.FILTER_BITMAP_FLAG));


            ExifInterface exif;
            try {
                exif = new ExifInterface(filePath);

                int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 0);
                Log.d("EXIF", "Exif: " + orientation);
                Matrix matrix = new Matrix();
                if (orientation == 6) {
                    matrix.postRotate(90);
                    Log.d("EXIF", "Exif: " + orientation);
                } else if (orientation == 3) {
                    matrix.postRotate(180);
                    Log.d("EXIF", "Exif: " + orientation);
                } else if (orientation == 8) {
                    matrix.postRotate(270);
                    Log.d("EXIF", "Exif: " + orientation);
                }
                scaledBitmap = Bitmap.createBitmap(scaledBitmap, 0, 0, scaledBitmap.getWidth(), scaledBitmap.getHeight(), matrix, true);
            } catch (IOException e) {
                e.printStackTrace();
            }
            FileOutputStream out = null;
            String filename = getFilename();
            try {
                out = new FileOutputStream(filename);
                scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

            return filename;

        }

        private String getRealPathFromURI(String contentURI) {
            Uri contentUri = Uri.parse(contentURI);
            Cursor cursor = getContentResolver().query(contentUri, null, null, null, null);
            if (cursor == null) {

                return contentUri.getPath();
            } else {
                cursor.moveToFirst();
                int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
                return cursor.getString(idx);
            }
        }

        public String getFilename() {
            File file = new File(Environment.getExternalStorageDirectory().getPath(), "MyFolder/Images");
            if (!file.exists()) {
                file.mkdirs();
            }
            String uriSting = (file.getAbsolutePath() + "/" + System.currentTimeMillis() + ".jpg");
            return uriSting;

        }


        @SuppressLint("WrongThread")
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            if (from.equalsIgnoreCase("im2")) {

                try {
                    Bitmap i = photoBMList.get(0);
                    if (i != null) {
                        photoBMList.remove(0);
                    }
                } catch (IndexOutOfBoundsException e) {

                }
                Glide.with(getApplicationContext()).asBitmap().load(scaledBitmap)
                        .thumbnail(0.5f).centerCrop()
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(binding.im2);

            } else if (from.equalsIgnoreCase("im3")) {
                try {
                    Bitmap i = photoBMList.get(1);
                    if (i != null) {
                        photoBMList.remove(1);
                    }
                } catch (IndexOutOfBoundsException e) {

                }


                Glide.with(getApplicationContext()).asBitmap().load(scaledBitmap)
                        .thumbnail(0.5f).centerCrop()
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(binding.im3);

            } else if (from.equalsIgnoreCase("im4")) {
                try {
                    Bitmap i = photoBMList.get(2);
                    if (i != null) {
                        photoBMList.remove(2);
                    }
                } catch (IndexOutOfBoundsException e) {

                }
                Glide.with(getApplicationContext()).asBitmap().load(scaledBitmap)
                        .thumbnail(0.5f).centerCrop()
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(binding.im4);

            } else if (from.equalsIgnoreCase("im5")) {
                try {
                    Bitmap i = photoBMList.get(3);
                    if (i != null) {
                        photoBMList.remove(3);
                    }
                } catch (IndexOutOfBoundsException e) {

                }

                Glide.with(getApplicationContext()).asBitmap().load(scaledBitmap)
                        .thumbnail(0.5f).centerCrop()
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(binding.im5);


            } else if (from.equalsIgnoreCase("im6")) {
                try {
                    Bitmap i = photoBMList.get(4);
                    if (i != null) {
                        photoBMList.remove(4);
                    }
                } catch (IndexOutOfBoundsException e) {

                }


                Glide.with(getApplicationContext()).asBitmap().load(scaledBitmap)
                        .thumbnail(0.5f).centerCrop()
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(binding.im6);


            }


            photoBMList.add(scaledBitmap);
            Log.e("imgaelist", photoBMList + "");


        }

    }




    private byte[][] getBitmapArray(ArrayList<Bitmap> photoBMList) {
        byte data[][] = new byte[photoBMList.size()][];
        int index = 0;
        for (Bitmap bm : photoBMList) {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bm.compress(Bitmap.CompressFormat.PNG, 100, stream);
            byte[] byteArray = stream.toByteArray();
            data[index] = byteArray;
            index++;
        }
        return data;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (intent.hasExtra("from"))
        {
            Intent intent = new Intent(UploadPicsActivity.this, HomePageActivity.class);
            intent.putExtra("from","MyProfileFragment");
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP );
            startActivity(intent);
            overridePendingTransition(R.anim.trans_left_in,
                    R.anim.trans_left_out);
        }
        else
        {
            finish();
        }
    }


    public static String getStringFromBitmap(Bitmap bm) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] byteArray = stream.toByteArray();

        return android.util.Base64.encodeToString(byteArray, android.util.Base64.NO_WRAP);
    }

    private boolean mayRequestPermissions() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }
        if (checkSelfPermission(CAMERA) + checkSelfPermission(READ_EXTERNAL_STORAGE) +
                checkSelfPermission(WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        if (shouldShowRequestPermissionRationale(READ_CONTACTS)) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this).setTitle(R.string.permission_rationale);
            builder.setPositiveButton(android.R.string.ok, null);
            builder.setMessage("Please confirm access to files & folders");
            builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @TargetApi(Build.VERSION_CODES.M)
                @Override
                public void onDismiss(DialogInterface dialog) {
                    requestPermissions(new String[]{READ_EXTERNAL_STORAGE,
                                    WRITE_EXTERNAL_STORAGE, CAMERA},
                            PERMISSION_REQUEST);
                }
            });
            builder.show();
        } else {
            requestPermissions(new String[]{READ_EXTERNAL_STORAGE,
                            WRITE_EXTERNAL_STORAGE, CAMERA},
                    PERMISSION_REQUEST);
        }
        return false;
    }


    @Override
    public void onStart() {
        super.onStart();

        isConnected = ConnectivityReceiver.isConnected();
        Log.e("onStart", isConnected + "");
        if (!isConnected) {
            showSnack(isConnected);
        }


    }


    protected void onResume() {
        super.onResume();
        // Logs 'install' and 'app activate' App Events.
        MyApplication.getInstance().setConnectivityListener(this);

    }

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        this.isConnected = isConnected;
        Log.e("onNetworkConnectionconn", isConnected + "");

        showSnack(isConnected);

    }

    private void showSnack(boolean isConnected) {
        String message;
        int color;

        Log.e("showSnackisConnected", isConnected + "");
        if (isConnected) {
            message = "Good! Connected to Internet";
            color = Color.WHITE;

        } else {
            message = "Sorry! Not connected to internet";
            color = Color.RED;

        }

        Snackbar snackbar = Snackbar
                .make(findViewById(R.id.ll), message, Snackbar.LENGTH_LONG);
        View sbView = snackbar.getView();
        TextView textView = (TextView) sbView.findViewById(R.id.snackbar_text);
        textView.setTextColor(color);
        snackbar.show();
    }

    private void loadImage() {
        progressDialog.setMessage("Please Wait..");
        progressDialog.setCancelable(true);
        progressDialog.show();

        StringRequest postRequest = new StringRequest(Request.Method.POST,
                "https://dilsay.app/webservice/Register/get_user_image",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("response", response);
                        JSONObject object = null;
                        try {
                            progressDialog.dismiss();
                            object = new JSONObject(response);
                            String status = object.getString("status");
                            String message = object.getString("message");
                            if (status.equalsIgnoreCase("200") &&
                                    message.equalsIgnoreCase("success")) {
                                JSONObject obj = object.getJSONObject("data");
                                JSONArray profile_image = obj.getJSONArray("profile_image");
                                int size = profile_image.length();

                                if (size == 1) {
                                    String img1 = profile_image.getString(0);
                                    Glide.with(getApplicationContext()).asBitmap().load(img1)
                                            .thumbnail(0.5f).centerCrop()
                                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                                            .into(binding.im2);
                                    from = "im2";
                                    new DownloadImageTask().execute(img1);

                                } else if (size == 2) {

                                    String img1 = profile_image.getString(0);
                                    String img2 = profile_image.getString(1);
                                    from = "im3";
                                    Glide.with(getApplicationContext()).asBitmap().load(img1)
                                            .thumbnail(0.5f).centerCrop()
                                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                                            .into(binding.im2);
                                    new DownloadImageTask().execute(img1);
                                    Glide.with(getApplicationContext()).asBitmap().load(img2)
                                            .thumbnail(0.5f).centerCrop()
                                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                                            .into(binding.im3);
                                    new DownloadImageTask().execute(img2);

                                } else if (size == 3) {

                                    String img1 = profile_image.getString(0);
                                    String img2 = profile_image.getString(1);
                                    String img3 = profile_image.getString(2);
                                    from = "im4";
                                    Glide.with(getApplicationContext()).asBitmap().load(img1)
                                            .thumbnail(0.5f).centerCrop()
                                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                                            .into(binding.im2);
                                    new DownloadImageTask().execute(img1);
                                    Glide.with(getApplicationContext()).asBitmap().load(img2)
                                            .thumbnail(0.5f).centerCrop()
                                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                                            .into(binding.im3);
                                    new DownloadImageTask().execute(img2);
                                    Glide.with(getApplicationContext()).asBitmap().load(img3)
                                            .thumbnail(0.5f).centerCrop()
                                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                                            .into(binding.im4);
                                    new DownloadImageTask().execute(img3);


                                } else if (size == 4) {

                                    String img1 = profile_image.getString(0);
                                    String img2 = profile_image.getString(1);
                                    String img3 = profile_image.getString(2);
                                    String img4 = profile_image.getString(3);
                                    from = "im5";
                                    Glide.with(getApplicationContext()).asBitmap().load(img1)
                                            .thumbnail(0.5f).centerCrop()
                                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                                            .into(binding.im2);
                                    new DownloadImageTask().execute(img1);
                                    Glide.with(getApplicationContext()).asBitmap().load(img2)
                                            .thumbnail(0.5f).centerCrop()
                                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                                            .into(binding.im3);
                                    new DownloadImageTask().execute(img2);
                                    Glide.with(getApplicationContext()).asBitmap().load(img3)
                                            .thumbnail(0.5f).centerCrop()
                                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                                            .into(binding.im4);
                                    new DownloadImageTask().execute(img3);

                                    Glide.with(getApplicationContext()).asBitmap().load(img4)
                                            .thumbnail(0.5f).centerCrop()
                                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                                            .into(binding.im5);
                                    new DownloadImageTask().execute(img4);


                                } else if (size == 5) {

                                    String img1 = profile_image.getString(0);
                                    String img2 = profile_image.getString(1);
                                    String img3 = profile_image.getString(2);
                                    String img4 = profile_image.getString(3);
                                    String img5 = profile_image.getString(4);
                                    from = "im6";
                                    Glide.with(getApplicationContext()).asBitmap().load(img1)
                                            .thumbnail(0.5f).centerCrop()
                                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                                            .into(binding.im2);
                                    new DownloadImageTask().execute(img1);
                                    Glide.with(getApplicationContext()).asBitmap().load(img2)
                                            .thumbnail(0.5f).centerCrop()
                                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                                            .into(binding.im3);
                                    new DownloadImageTask().execute(img2);
                                    Glide.with(getApplicationContext()).asBitmap().load(img3)
                                            .thumbnail(0.5f).centerCrop()
                                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                                            .into(binding.im4);

                                    new DownloadImageTask().execute(img3);
                                    Glide.with(getApplicationContext()).asBitmap().load(img4)
                                            .thumbnail(0.5f).centerCrop()
                                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                                            .into(binding.im5);
                                    new DownloadImageTask().execute(img4);
                                    Glide.with(getApplicationContext()).asBitmap().load(img5)
                                            .thumbnail(0.5f).centerCrop()
                                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                                            .into(binding.im6);
                                    new DownloadImageTask().execute(img5);


                                }
                            }

                        } catch (JSONException e) {
                            progressDialog.dismiss();

                            e.printStackTrace();
                        }


                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        progressDialog.dismiss();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("user_id", sessionManager.getUserId().get(SessionManager.KEY_USERID));
                Log.e("params", params.toString());
                return params;
            }
        };
        int socketTimeout = 30000;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        postRequest.setRetryPolicy(policy);
        queue.add(postRequest);
    }


    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        public DownloadImageTask() {
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap bmp = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                bmp = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return bmp;
        }

        protected void onPostExecute(Bitmap result) {
            photoBMList.add(result);
        }
    }


    private void uploadImage()
    {
        progressDialog.setMessage("loading..");
        progressDialog.setCancelable(false);
        progressDialog.show();
        VolleyMultipartRequest volleyMultipartRequest = new VolleyMultipartRequest(Request.Method.POST, EndPoints.UPLOAD_PROFILE_IMAGE,
                new Response.Listener<NetworkResponse>() {
                    @Override
                    public void onResponse(NetworkResponse response) {
                        progressDialog.dismiss();
                        Log.e("ressssssoo", new String(response.data));
                        rQueue.getCache().clear();
                        try {
                            JSONObject obj = new JSONObject(new String(response.data));
                            int status = obj.getInt("status");
                            String message = obj.getString("message");

                            if (status == 200 && message.equalsIgnoreCase("success")) {

                                if (intent.hasExtra("from"))
                                {
                                    Intent intent = new Intent(UploadPicsActivity.this, HomePageActivity.class);
                                    intent.putExtra("from","MyProfileFragment");
                                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP );
                                    startActivity(intent);
                                    overridePendingTransition(R.anim.trans_left_in,
                                            R.anim.trans_left_out);
                                }
                                else
                                {
                                    Intent in = new Intent(UploadPicsActivity.this, LookingForActivity.class);
                                    in.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(in);
                                    overridePendingTransition(R.anim.trans_left_in,
                                            R.anim.trans_left_out);
                                }


                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressDialog.dismiss();

                        Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }) {

            /*
             * If you want to add more parameters with the image
             * you can do it here
             * here we have only one parameter with the image
             * which is tags
             * */
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> ob = new HashMap<>();
                ob.put("user_id", sessionManager.getUserId().get(SessionManager.KEY_USERID));
                ob.put("fb_image", sessionManager.getFacebookData().get(SessionManager.KEY_FACEBOOKDP));
                return ob;
            }

            /*
             *pass files using below method
             * */
            @Override
            protected Map<String, DataPart> getByteData() {

                Map<String, DataPart> params = new HashMap<>();
//                Log.e("size",photoBMList.size()+"");
//                for (int i=0; i<photoBMList.size();i++)
//                {
//                    long imagename = System.currentTimeMillis();
//                    Log.e("imagename",imagename+"");

                params.put("profile_image[]", new DataPart(getBitmapArray(photoBMList)));
//                    Log.e("params",params+"");
//
//                }

                return params;
            }
        };


        volleyMultipartRequest.setRetryPolicy(new DefaultRetryPolicy(
                0,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        rQueue = Volley.newRequestQueue(UploadPicsActivity.this);
        rQueue.add(volleyMultipartRequest);
    }


    public byte[] getFileDataFromDrawable(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 80, byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }

    private void requestMultiplePermissions() {
        Dexter.withActivity(this)
                .withPermissions(

                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        // check if all permissions are granted
                        if (report.areAllPermissionsGranted()) {
                            Toast.makeText(getApplicationContext(), "All permissions are granted by user!", Toast.LENGTH_SHORT).show();
                        }

                        // check for permanent denial of any permission
                        if (report.isAnyPermissionPermanentlyDenied()) {
                            // show alert dialog navigating to Settings

                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).
                withErrorListener(new PermissionRequestErrorListener() {
                    @Override
                    public void onError(DexterError error) {
                        Toast.makeText(getApplicationContext(), "Some Error! ", Toast.LENGTH_SHORT).show();
                    }
                })
                .onSameThread()
                .check();
    }



}
