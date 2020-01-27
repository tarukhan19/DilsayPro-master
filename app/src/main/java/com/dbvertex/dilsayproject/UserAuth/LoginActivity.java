package com.dbvertex.dilsayproject.UserAuth;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.dbvertex.dilsayproject.EndPoints;
import com.dbvertex.dilsayproject.Fcm.Config;
import com.dbvertex.dilsayproject.Network.ConnectivityReceiver;
import com.dbvertex.dilsayproject.Network.MyApplication;
import com.dbvertex.dilsayproject.R;
import com.dbvertex.dilsayproject.databinding.ActivityLoginBinding;
import com.dbvertex.dilsayproject.session.SessionManager;
import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import org.json.JSONException;
import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener,
        ConnectivityReceiver.ConnectivityReceiverListener {
    ActivityLoginBinding loginBinding;
    CallbackManager callbackManager;
    private AccessTokenTracker accessTokenTracker;
    ProgressDialog progressDialog;
    RequestQueue queue;
    String fname, mail, fid, profilePic;
    boolean isConnected;
    String regId;

    SessionManager sessionManager;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        loginBinding = DataBindingUtil.setContentView(this, R.layout.activity_login);

        progressDialog = new ProgressDialog(LoginActivity.this, R.style.CustomDialog);
        queue = Volley.newRequestQueue(LoginActivity.this);
        sessionManager = new SessionManager(this);

        loginBinding.signinLL.setOnClickListener(this);
        callbackManager = CallbackManager.Factory.create();
        loginBinding.loginButton.setReadPermissions("email");
        loginBinding.loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                AccessToken accessToken = loginResult.getAccessToken();
                getUserDetails(accessToken);

            }

            @Override
            public void onCancel() {
                // App code
            }

            @Override
            public void onError(FacebookException exception) {
                // App code
            }
        });
        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener( LoginActivity.this,
                new OnSuccessListener<InstanceIdResult>() {
                    @Override
                    public void onSuccess(InstanceIdResult instanceIdResult) {
                        regId = instanceIdResult.getToken();
                        Log.e("Token",regId);
                    }
                });
        accessTokenTracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken, AccessToken currentAccessToken) {
                // currentAccessToken is null if the user is logged out
//                if (currentAccessToken != null) {
//                    getUserDetails(currentAccessToken);
//
//                } else {
//
//
//                }
            }
        };

       // getHshKey();
    }


        public void getHshKey()
    {
        try {
            PackageInfo info = getPackageManager().getPackageInfo(
                    "com.dbvertex.dilsayproject",
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.e("KeyHash", "KeyHash:"+ Base64.encodeToString(md.digest(),
                        Base64.DEFAULT));
                Toast.makeText(getApplicationContext(), Base64.encodeToString(md.digest(),
                        Base64.DEFAULT), Toast.LENGTH_LONG).show();
            }
        } catch (PackageManager.NameNotFoundException e) {

        } catch (NoSuchAlgorithmException e) {

        }
    }

    protected void getUserDetails(AccessToken accessToken) {

        GraphRequest data_request = GraphRequest.newMeRequest(
                accessToken, new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(
                            JSONObject object,
                            GraphResponse response) {

                        try {
                            Log.e("tttttt", object.getString("email"));
                            fname = object.getString("name");
                            mail = object.getString("email");
                            fid = object.getString("id");
                            profilePic = "https://graph.facebook.com/" + fid + "/picture?type=large";
                            sessionManager.setFacebookData(mail,fname,fid,profilePic);
                            LoginManager.getInstance().logOut();

                            login();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }

                });


        Bundle permission_param = new Bundle();
        permission_param.putString("fields", "id,name,email,picture.width(120).height(120)");
        data_request.setParameters(permission_param);
        data_request.executeAsync();

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);

    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.signinLL:
                if (isConnected)
                loginBinding.loginButton.performClick();
                else
                    showSnack(isConnected);

        }

    }

    @Override
    public void onStart() {
        super.onStart();

        isConnected = ConnectivityReceiver.isConnected();
        Log.e("onStart",isConnected+"");
        if (!isConnected)
        {
            showSnack(isConnected);
        }

        accessTokenTracker.startTracking();

        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        if (accessToken != null) {
            //   getUserDetails(accessToken);
        }

    }


    protected void onResume() {
        super.onResume();
        // Logs 'install' and 'app activate' App Events.
        AppEventsLogger.activateApp(this);
        MyApplication.getInstance().setConnectivityListener(this);

    }

    @Override
    protected void onPause() {
        super.onPause();
        // Logs 'app deactivate' App Event.
        AppEventsLogger.deactivateApp(this);
    }

    public void onDestroy() {
        super.onDestroy();
        // We stop the tracking before destroying the activity
        accessTokenTracker.stopTracking();
    }


    private void login() {

        progressDialog.setMessage("Please Wait..");
        progressDialog.setCancelable(true);
        progressDialog.show();

        StringRequest postRequest = new StringRequest(Request.Method.POST, EndPoints.LOGIN,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("response", response);
                        try {

                            progressDialog.dismiss();
                            JSONObject obj = new JSONObject(response);
                            int status = obj.getInt("status");
                            String message = obj.getString("message");
                            if (status==1 && message.equalsIgnoreCase("User Not Found!"))
                            {
                                Intent intent = new Intent(LoginActivity.this, SignupActivity.class);
                                startActivity(intent);
                                overridePendingTransition(R.anim.trans_left_in,
                                        R.anim.trans_left_out);
                            }
                            else if (status==200 && message.equalsIgnoreCase("success"))
                            {
                                JSONObject data= obj.getJSONObject("data");
                                String otp_verified=data.getString("otp_verified");
                                // {"status":200,"message":"success","data":{"otp_verified":0,"mobile_no":"7697087433","country_code":"+91"}}
                                if (otp_verified.equalsIgnoreCase("0"))
                                {
                                    String mobile_no=data.getString("mobile_no");
                                    String country_code=data.getString("country_code");

                                    Intent intent = new Intent(LoginActivity.this, OtpActivity.class);
                                    intent.putExtra("mobileno", mobile_no);
                                    intent.putExtra("countrycode", country_code);
                                    sessionManager.setMobileNumber(mobile_no,country_code);
                                    startActivity(intent);
                                    overridePendingTransition(R.anim.trans_left_in,
                                            R.anim.trans_left_out);
                                }

                                else if (otp_verified.equalsIgnoreCase("1"))
                                {


                                    String user_id=data.getString("id");
                                    String profile_status=data.getString("profile_status");

                               //     sessionManager.setStep(profile_status);
                                    sessionManager.setUserId(user_id);
                                    //{"status":200,"message":"success","data":{"otp_verified":0,"mobile_no":"9522335636","country_code":"+91","id":"139"}}
                                    if (profile_status.equalsIgnoreCase("1"))
                                    {
                                        Intent intent = new Intent(LoginActivity.this, UploadPicsActivity.class);
                                        startActivity(intent);
                                        overridePendingTransition(R.anim.trans_left_in,
                                                R.anim.trans_left_out);

                                    }
                                    else if (profile_status.equalsIgnoreCase("2"))
                                    {
                                        Intent intent = new Intent(LoginActivity.this, LookingForActivity.class);
                                        startActivity(intent);
                                        overridePendingTransition(R.anim.trans_left_in,
                                                R.anim.trans_left_out);
                                    }

                                    else if (profile_status.equalsIgnoreCase("3"))
                                    {
                                        Intent intent = new Intent(LoginActivity.this, ChooseInterestActivity.class);
                                        startActivity(intent);
                                        overridePendingTransition(R.anim.trans_left_in,
                                                R.anim.trans_left_out);
                                    }

                                    else if (profile_status.equalsIgnoreCase("4"))
                                    {
                                        Intent intent = new Intent(LoginActivity.this, MyLocationActivity.class);
                                        startActivity(intent);
                                        overridePendingTransition(R.anim.trans_left_in,
                                                R.anim.trans_left_out);
                                    }
                                    else
                                    {
                                        Intent intent = new Intent(LoginActivity.this, HomePageActivity.class);
                                        startActivity(intent);
                                        sessionManager.setLoginSession();

                                        overridePendingTransition(R.anim.trans_left_in,
                                                R.anim.trans_left_out);

                                    }


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
                params.put("fb_id", fid);
                params.put("device_type","android");
                params.put("device_id",regId);
                Log.e("params", params.toString());
                return params;
            }
        };
        int socketTimeout = 30000;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        postRequest.setRetryPolicy(policy);
        queue.add(postRequest);
    }

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        this.isConnected=isConnected;
        Log.e("onNetworkConnectionconn",isConnected+"");

        showSnack(isConnected);

    }

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
                .make(findViewById(R.id.ll), message, Snackbar.LENGTH_LONG);
        View sbView = snackbar.getView();
        TextView textView = (TextView) sbView.findViewById(R.id.snackbar_text);
        textView.setTextColor(color);
        snackbar.show();
    }


}
