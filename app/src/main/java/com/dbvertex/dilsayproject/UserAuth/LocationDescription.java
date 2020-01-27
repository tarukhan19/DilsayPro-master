package com.dbvertex.dilsayproject.UserAuth;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.databinding.DataBindingUtil;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.dbvertex.dilsayproject.Network.ConnectivityReceiver;
import com.dbvertex.dilsayproject.Network.MyApplication;
import com.dbvertex.dilsayproject.R;
import com.dbvertex.dilsayproject.databinding.ActivityLocationDescriptionBinding;
import com.dbvertex.dilsayproject.session.SessionManager;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LocationDescription extends AppCompatActivity implements
        ConnectivityReceiver.ConnectivityReceiverListener {

    ActivityLocationDescriptionBinding binding;
    ProgressDialog progressDialog;
    RequestQueue queue;
    SessionManager sessionManager;
    List<String> interestsentaraylist;
    String interestsentStr;
    boolean isConnected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_location_description);

        progressDialog = new ProgressDialog(LocationDescription.this, R.style.CustomDialog);
        queue = Volley.newRequestQueue(LocationDescription.this);
        sessionManager = new SessionManager(this);
        interestsentaraylist = new ArrayList<>();

        interestsentStr = sessionManager.getInterest().get(SessionManager.KEY_INTEREST);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_main);
        final LinearLayout back = toolbar.findViewById(R.id.back_LL);
        TextView toolbar_title = toolbar.findViewById(R.id.titleTV);
        toolbar_title.setText("My Location");

        if (!sessionManager.getLocation().get(SessionManager.KEY_LOCATION).isEmpty()) {
            binding.locationTV.setText(sessionManager.getLocation().get(SessionManager.KEY_LOCATION));
        }
        if (!sessionManager.getDescription().get(SessionManager.KEY_DESCRIPTION).isEmpty()) {
            binding.descriptionET.setText(sessionManager.getDescription().get(SessionManager.KEY_DESCRIPTION));
        }

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LocationDescription.this, MyLocationActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                overridePendingTransition(R.anim.trans_left_in,
                        R.anim.trans_left_out);
            }
        });

        binding.mylocationLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LocationDescription.this, MyLocationActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                overridePendingTransition(R.anim.trans_left_in,
                        R.anim.trans_left_out);
            }
        });

        binding.continueBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (binding.locationTV.getText().toString().isEmpty()) {
                    binding.locationTIL.setError("Select location");
                } else if (binding.descriptionET.getText().toString().isEmpty()) {
                    binding.descriptionTIL.setError("Enter description.");

                } else {

                    interestsentaraylist = Arrays.asList(interestsentStr.split("\\s*,\\s*"));
                    sessionManager.setDescription(binding.descriptionET.getText().toString());
                    if (isConnected) {
//                        ProfileTask task = new ProfileTask();
//                        task.execute();
                        submitLocationDesc();
                    } else {
                        showSnack(isConnected);
                    }
                }
            }
        });

    }


    private void submitLocationDesc()
    {
        progressDialog.setMessage("Please Wait..");
        progressDialog.setCancelable(true);
        progressDialog.show();

        StringRequest postRequest = new StringRequest(Request.Method.POST, EndPoints.UPLOAD_LOCDESC,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("response", response);
                        try {

                            progressDialog.dismiss();
                            JSONObject obj = new JSONObject(response);
                            int status = obj.getInt("status");
                            String message = obj.getString("message");
//{"status":200,"message":"success","data":{"id":11,"name":"Tarannum Khan","email":"tarukhan19@gmail.com",
// "mobile_no":"9522335636","dob":"1990-04-26","gender":"F","facebook_id":"2360644337504490"}}

                            if (status == 200 && message.equalsIgnoreCase("success"))
                            {

                              //  sessionManager.setStep("5");
                                Intent intent = new Intent(LocationDescription.this, PlansActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                                overridePendingTransition(R.anim.trans_left_in,
                                        R.anim.trans_left_out);




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
                params.put("latitude", sessionManager.getLocation().get(SessionManager.KEY_LATITUDE));
                params.put("longitude", sessionManager.getLocation().get(SessionManager.KEY_LONGITUDE));
                params.put("description", binding.descriptionET.getText().toString());
                params.put("location", sessionManager.getLocation().get(SessionManager.KEY_LOCATION));
                params.put("user_id", sessionManager.getUserId().get(SessionManager.KEY_USERID));

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
        Intent intent = new Intent(LocationDescription.this, MyLocationActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        overridePendingTransition(R.anim.trans_left_in,
                R.anim.trans_left_out);
    }


    @Override
    public void onStart() {
        isConnected = ConnectivityReceiver.isConnected();
        Log.e("onStart", isConnected + "");
        if (!isConnected) {
            showSnack(isConnected);
        }

        super.onStart();
    }

    // Showing the status in Snackbar
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

    @Override
    public void onResume() {
        super.onResume();

        // register connection status listener
        MyApplication.getInstance().setConnectivityListener(this);
    }

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        this.isConnected = isConnected;
        Log.e("onNetworkConnectionconn", isConnected + "");

        showSnack(isConnected);


    }
}
