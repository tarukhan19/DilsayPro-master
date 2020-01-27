package com.dbvertex.dilsayproject.Chatting;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.databinding.DataBindingUtil;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.dbvertex.dilsayproject.Adapter.ChatInboxAdapter;
import com.dbvertex.dilsayproject.DeleteActivity;
import com.dbvertex.dilsayproject.EndPoints;
import com.dbvertex.dilsayproject.HideKeyboard;
import com.dbvertex.dilsayproject.ImageLoadingUtils;
import com.dbvertex.dilsayproject.Model.ChatDTO;
import com.dbvertex.dilsayproject.Network.ConnectivityReceiver;
import com.dbvertex.dilsayproject.Network.MyApplication;
import com.dbvertex.dilsayproject.ProfileDetailActivity;
import com.dbvertex.dilsayproject.R;
import com.dbvertex.dilsayproject.UserAuth.HomePageActivity;
import com.dbvertex.dilsayproject.UserAuth.LoginActivity;
import com.dbvertex.dilsayproject.VollMultiprtReq;
import com.dbvertex.dilsayproject.VolleyMultipartRequest;
import com.dbvertex.dilsayproject.databinding.ActivityChatInboxBinding;
import com.dbvertex.dilsayproject.session.SessionManager;
import com.google.android.material.snackbar.Snackbar;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.READ_CONTACTS;
import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class ChatInboxActivity extends AppCompatActivity implements ConnectivityReceiver.ConnectivityReceiverListener  {
ActivityChatInboxBinding binding;

    static private String  recieverId,receivername,from;
    static SessionManager sessionManager;
    ProgressDialog progressDialog;
    RequestQueue queue;
    private ListView chatLV;
    boolean isConnected;
    static private ArrayList<ChatDTO> chatList;
    Intent intent;
    static private ChatInboxAdapter chatADP;
    static RequestQueue rQueue;
    Bitmap scaledBitmap;

    String msg;

    ImageLoadingUtils utils;
    public static final int PERMISSION_REQUEST = 100;

    Uri picUri;
    private ArrayList<String> permissionsToRequest;
    private ArrayList<String> permissionsRejected = new ArrayList<>();
    private ArrayList<String> permissions = new ArrayList<>();

    private final static int ALL_PERMISSIONS_RESULT = 107;
    private final static int IMAGE_RESULT = 200;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_chat_inbox);
        isConnected = ConnectivityReceiver.isConnected();

        utils = new ImageLoadingUtils(this);
        permissions.add(Manifest.permission.CAMERA);
        permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        permissions.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        permissionsToRequest = findUnAskedPermissions(permissions);

        if (android.os.Build.VERSION.SDK_INT >= 21)
        {
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(this.getResources().getColor(R.color.colorPrimary));
        }
        intent=getIntent();
        recieverId=intent.getStringExtra("userid");
        receivername=intent.getStringExtra("username");
        from=intent.getStringExtra("from");

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_main);
        LinearLayout back=toolbar.findViewById(R.id.back_LL);
        TextView toolbar_title=toolbar.findViewById(R.id.titleTV);
        progressDialog = new ProgressDialog(ChatInboxActivity.this, R.style.CustomDialog);
        queue = Volley.newRequestQueue(ChatInboxActivity.this);
        sessionManager = new SessionManager(ChatInboxActivity.this);

        queue = Volley.newRequestQueue(ChatInboxActivity.this);


        back.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
            if (from.equalsIgnoreCase("chatfrag"))
            {
                Intent intent=new Intent(ChatInboxActivity.this, HomePageActivity.class);
                intent.putExtra("from",from);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                overridePendingTransition(R.anim.trans_left_in,
                        R.anim.trans_left_out);
            }
            else
            {
                Intent intent=new Intent(ChatInboxActivity.this, ProfileDetailActivity.class);
                intent.putExtra("from",from);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                overridePendingTransition(R.anim.trans_left_in,
                        R.anim.trans_left_out);
            }
            }
        });
        toolbar_title.setText(receivername);

        chatLV = (ListView) findViewById(R.id.chatLV);


        // ----Set autoscroll of listview when a new message arrives----//

        chatList = new ArrayList<>();
        chatADP = new ChatInboxAdapter(chatList, this,recieverId);
        chatLV.setTranscriptMode(ListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
        chatLV.setStackFromBottom(true);
        chatLV.setAdapter(chatADP);

        binding.sendll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                msg = binding.commentET.getText().toString().trim();

                HideKeyboard.hideKeyboard(ChatInboxActivity.this);
                if (!isConnected)
                {
                    showSnack(isConnected);
                }
                else
                {   if (!msg.isEmpty())
                {

                    binding.progressBarCyclic.setVisibility(View.VISIBLE);
                    sendChatTask();
                }
                }

            }
        });

        binding.uploadpicIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(getPickImageChooserIntent(), IMAGE_RESULT);
            }
        });
        binding.crossIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                binding.imageRl.setVisibility(View.INVISIBLE);
                binding.chatsendIV.setImageBitmap(null);
                scaledBitmap=null;

            }
        });
        //checkConnection();
        mayRequestPermissions();
        loadChatTask();
    }

    private void sendChatTask()
    {


        VollMultiprtReq volleyMultipartRequest = new VollMultiprtReq(Request.Method.POST,
                EndPoints.CHAT_SEND,
                new Response.Listener<NetworkResponse>() {
                    @Override
                    public void onResponse(NetworkResponse response) {
                        rQueue.getCache().clear();

                        Log.e("resp", new String(response.data));
                        //{"id":3,"status":"User successfully registered.","Userid":141,
                        // "Usercode":"ASDAR1","Username":"Taru khan","Email":"tarukhan19@gmail.com","Mobileno":"6265697963"}
                        //{"id":1,"status":"Success"}

                        try {
                            JSONObject obj = new JSONObject(new String(response.data));
                            int status = obj.getInt("status");
                            String message = obj.getString("message");

                            if (status == 200 && message.equalsIgnoreCase("success")) {
                                binding.progressBarCyclic.setVisibility(View.INVISIBLE);
                                binding.commentET.setText("");
                                binding.imageRl.setVisibility(View.INVISIBLE);
                                binding.chatsendIV.setImageBitmap(null);
                                scaledBitmap=null;
                                loadChatTask();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        binding.progressBarCyclic.setVisibility(View.INVISIBLE);
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {

                Map<String, String> ob = new HashMap<>();
                ob.put("sender_id", sessionManager.getUserId().get(SessionManager.KEY_USERID));
                ob.put("receiver_id", recieverId);
                ob.put("message", toBase64(binding.commentET.getText().toString()));
                Log.e("params", ob.toString());
                return ob;
            }

            @Override
            protected Map<String, DataPart> getByteData() {
                Map<String, DataPart> params = new HashMap<>();
                long imagename = System.currentTimeMillis();

                if (scaledBitmap != null) {
                    params.put("image", new DataPart(imagename + "profilepic.png", getFileDataFromDrawable(scaledBitmap)));

                }

                Log.e("params", params.toString());

                return params;
            }
        };
        //adding the request to volley
        volleyMultipartRequest.setRetryPolicy(new DefaultRetryPolicy(
                0,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        rQueue = Volley.newRequestQueue(ChatInboxActivity.this);
        rQueue.add(volleyMultipartRequest);
    }

    public byte[] getFileDataFromDrawable(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 80, byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }
    public static String toBase64(String message) {
        byte[] data;
        try {
            data = message.getBytes("UTF-8");
            String base64Sms = Base64.encodeToString(data, Base64.DEFAULT);
            return base64Sms;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return null;
    }


    // Showing the status in Snackbar
    private void showSnack(boolean isConnected) {
        String message;
        int color;

        Log.e("showSnackisConnected",isConnected+"");
        if (isConnected) {
            message = "Good! Connected to Internet";
            color = Color.WHITE;

        } else {
            message = "Sorry! Not connected to internet";
            color = Color.RED;

        }

        Snackbar snackbar = Snackbar
                .make(findViewById(R.id.ll), message, Snackbar.LENGTH_INDEFINITE).setAction("Retry", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        startActivity(intent);
                        overridePendingTransition(0,0);
                    }
                });

        View sbView = snackbar.getView();
        TextView textView = (TextView) sbView.findViewById(R.id.snackbar_text);
        textView.setTextColor(color);
        snackbar.show();
    }
    @Override
    public void onResume() {
        super.onResume();

        // register connection status listener
        MyApplication.getInstance().setConnectivityListener(this);
    }
    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        this.isConnected=isConnected;
        Log.e("onNetworkConnectionconn",isConnected+"");

        showSnack(isConnected);



    }


    public Intent getPickImageChooserIntent()
    {

        Uri outputFileUri = getCaptureImageOutputUri();

        List<Intent> allIntents = new ArrayList<>();
        PackageManager packageManager = getPackageManager();

        Intent captureIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        List<ResolveInfo> listCam = packageManager.queryIntentActivities(captureIntent, 0);
        for (ResolveInfo res : listCam) {
            Intent intent = new Intent(captureIntent);
            intent.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
            intent.setPackage(res.activityInfo.packageName);
            if (outputFileUri != null) {
                intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
            }
            allIntents.add(intent);
        }

        Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        List<ResolveInfo> listGallery = packageManager.queryIntentActivities(galleryIntent, 0);
        for (ResolveInfo res : listGallery) {
            Intent intent = new Intent(galleryIntent);
            intent.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
            intent.setPackage(res.activityInfo.packageName);
            allIntents.add(intent);
        }

        Intent mainIntent = allIntents.get(allIntents.size() - 1);
        for (Intent intent : allIntents) {
            if (intent.getComponent().getClassName().equals("com.android.documentsui.DocumentsActivity")) {
                mainIntent = intent;
                break;
            }
        }
        allIntents.remove(mainIntent);

        Intent chooserIntent = Intent.createChooser(mainIntent, "Select source");
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, allIntents.toArray(new Parcelable[allIntents.size()]));

        return chooserIntent;
    }


    private Uri getCaptureImageOutputUri() {
        Uri outputFileUri = null;
        File getImage = getExternalFilesDir("");
        if (getImage != null) {
            outputFileUri = Uri.fromFile(new File(getImage.getPath(), "profile.png"));
        }
        return outputFileUri;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {


        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {


            if (requestCode == IMAGE_RESULT) {

                String filePath = getImageFilePath(data);
                if (filePath != null) {

                    new ImageCompressionAsyncTask().execute(filePath);


                }
            }

        }

    }


    private String getImageFromFilePath(Intent data)
    {
        boolean isCamera = data == null || data.getData() == null;
        if (isCamera) return getCaptureImageOutputUri().getPath();
        else return getPathFromURI(data.getData());

    }

    public String getImageFilePath(Intent data) {
        return getImageFromFilePath(data);
    }

    private String getPathFromURI(Uri contentUri) {
        String[] proj = {MediaStore.Audio.Media.DATA};
        Cursor cursor = getContentResolver().query(contentUri, proj, null,
                null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable("pic_uri", picUri);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        picUri = savedInstanceState.getParcelable("pic_uri");
    }

    private ArrayList<String> findUnAskedPermissions(ArrayList<String> wanted) {
        ArrayList<String> result = new ArrayList<String>();

        for (String perm : wanted) {
            if (!hasPermission(perm)) {
                result.add(perm);
            }
        }

        return result;
    }

    private boolean hasPermission(String permission) {
        if (canMakeSmores()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                return (checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED);
            }
        }
        return true;
    }

    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(ChatInboxActivity.this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }

    private boolean canMakeSmores() {
        return (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1);
    }

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

        switch (requestCode) {

            case ALL_PERMISSIONS_RESULT:
                for (String perms : permissionsToRequest) {
                    if (!hasPermission(perms)) {
                        permissionsRejected.add(perms);
                    }
                }

                if (permissionsRejected.size() > 0) {


                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (shouldShowRequestPermissionRationale(permissionsRejected.get(0))) {
                            showMessageOKCancel("These permissions are mandatory for the application. Please allow access.",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {


                                                requestPermissions(permissionsRejected.toArray(new String[permissionsRejected.size()]), ALL_PERMISSIONS_RESULT);
                                            }
                                        }
                                    });
                            return;
                        }
                    }

                }

                break;
        }

    }


    class ImageCompressionAsyncTask extends AsyncTask<String, Void, String>
    {

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


        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            binding.imageRl.setVisibility(View.VISIBLE);
            binding.chatsendIV.setImageBitmap(scaledBitmap);


        }

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
            android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this).setTitle(R.string.permission_rationale);
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

    private void loadChatTask()
    {
        progressDialog.setMessage("Please Wait..");
        progressDialog.setCancelable(true);
        progressDialog.show();

        StringRequest postRequest = new StringRequest(Request.Method.POST, EndPoints.CHAT_LOAD,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("CHAT_LOADresponse", response);
                        try {

                            progressDialog.dismiss();
                            JSONObject obj = new JSONObject(response);
                            int status = obj.getInt("Status");
                            String message = obj.getString("Message");
//{"status":200,"message":"success","data":{"id":11,"name":"Tarannum Khan","email":"tarukhan19@gmail.com",
// "mobile_no":"9522335636","dob":"1990-04-26","gender":"F","facebook_id":"2360644337504490"}}

                            if (status == 200 && message.equalsIgnoreCase("success"))
                            {

                                chatList.clear();
                                JSONArray data = obj.getJSONArray("data");
                                for (int x = 0; x < data.length(); x++)
                                {
                                    JSONObject chatObj = data.getJSONObject(x);

                                    String startTime=chatObj.getString("Message_date");


                                    StringTokenizer tk = new StringTokenizer(startTime);
                                    String dte = tk.nextToken();
                                    String time = tk.nextToken();

                                    String inputPattern = "dd-MMM-yyyy";
                                    String outputPattern = "ddMMMyy";
                                    SimpleDateFormat inputFormat = new SimpleDateFormat(inputPattern);
                                    SimpleDateFormat outputFormat = new SimpleDateFormat(outputPattern);

                                    Date date = null;

                                    try {
                                        date = inputFormat.parse(dte);

                                    } catch (ParseException e) {
                                        e.printStackTrace();
                                    }

                                    SimpleDateFormat sdf = new SimpleDateFormat("hh:mm:ss");
                                    SimpleDateFormat sdfs = new SimpleDateFormat("hh:mm a");
                                    Date dt = null;
                                    try {
                                        dt = sdf.parse(time);
                                        Log.e("Time Display: " , sdfs.format(dt)); // <-- I got result here
                                    } catch (ParseException e) {
                                        // TODO Auto-generated catch block
                                        e.printStackTrace();
                                    }

                                    ChatDTO chat = new ChatDTO();
                                    chat.setMsg(chatObj.getString("Message"));
                                    chat.setSendBy(chatObj.getString("sender_id"));
                                    chat.setrecieveBy(chatObj.getString("receiver_id"));
                                    chat.setTime(outputFormat.format(date)+" "+sdfs.format(dt));
                                    chat.setImage(chatObj.getString("image"));

                                    chatList.add(chat);
                                    chatADP.notifyDataSetChanged();
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
                        error.printStackTrace();
                        progressDialog.dismiss();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("sender_id", sessionManager.getUserId().get(SessionManager.KEY_USERID));
                params.put("receiver_id", recieverId);
                Log.e("params", params.toString());

                //user_id,latitude, longitude ,location,description
                return params;
            }
        };
        int socketTimeout = 30000;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        postRequest.setRetryPolicy(policy);
        queue.add(postRequest);
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (from.equalsIgnoreCase("chatfrag"))
        {
            Intent intent=new Intent(ChatInboxActivity.this, HomePageActivity.class);
            intent.putExtra("from",from);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            overridePendingTransition(R.anim.trans_left_in,
                    R.anim.trans_left_out);
        }
        else
        {
            Intent intent=new Intent(ChatInboxActivity.this, ProfileDetailActivity.class);
            intent.putExtra("from",from);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            overridePendingTransition(R.anim.trans_left_in,
                    R.anim.trans_left_out);
        }
    }
}
