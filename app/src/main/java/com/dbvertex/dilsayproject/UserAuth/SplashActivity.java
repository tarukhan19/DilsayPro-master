package com.dbvertex.dilsayproject.UserAuth;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
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
import com.dbvertex.dilsayproject.Filter.FilterActivity;
import com.dbvertex.dilsayproject.MapsActivity;
import com.dbvertex.dilsayproject.R;
import com.dbvertex.dilsayproject.session.SessionManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class SplashActivity extends AppCompatActivity {
    ProgressDialog progressDialog;
    RequestQueue queue;
    TextView quoteTV;
    SessionManager session;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        progressDialog = new ProgressDialog(SplashActivity.this, R.style.CustomDialog);
        queue = Volley.newRequestQueue(SplashActivity.this);
        session = new SessionManager(this);
        quoteTV=findViewById(R.id.quoteTV);
        loadQuote();


    }

    private void loadQuote() {

        StringRequest postRequest = new StringRequest(Request.Method.GET, EndPoints.GETQUOTES,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("response", response);
                        try {

                            JSONObject obj = new JSONObject(response);
                            String quotes=obj.getString("quotes");
                            quoteTV.setText(quotes);


                            new Handler().postDelayed(new Runnable()
                            {
                                @Override
                                public void run() {
                                    if (session.isLoggedIn()) {

                                        startActivity(new Intent(SplashActivity.this, FilterActivity.class)
                                                .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                                        overridePendingTransition(R.anim.trans_left_in,
                                                R.anim.trans_left_out);

                                        finish();
                                    } else

                                    {
                                        if (session.getStep().get(SessionManager.KEY_STEP).equals("1"))
                                        {
                                            startActivity(new Intent(SplashActivity.this, UploadPicsActivity.class)
                                                    .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                                            overridePendingTransition(R.anim.trans_left_in,
                                                    R.anim.trans_left_out);
                                            finish();
                                        }

                                        else if (session.getStep().get(SessionManager.KEY_STEP).equalsIgnoreCase("2"))
                                        {
                                            Intent intent = new Intent(SplashActivity.this, LookingForActivity.class);
                                            intent .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                            startActivity(intent);
                                            overridePendingTransition(R.anim.trans_left_in,
                                                    R.anim.trans_left_out);
                                        }

                                        else if (session.getStep().get(SessionManager.KEY_STEP).equalsIgnoreCase("3"))
                                        {
                                            Intent intent = new Intent(SplashActivity.this, ChooseInterestActivity.class);
                                            intent .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                            startActivity(intent);
                                            overridePendingTransition(R.anim.trans_left_in,
                                                    R.anim.trans_left_out);
                                        }

                                        else if (session.getStep().get(SessionManager.KEY_STEP).equalsIgnoreCase("4"))
                                        {
                                            Intent intent = new Intent(SplashActivity.this, MyLocationActivity.class);
                                            intent .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                            startActivity(intent);
                                            overridePendingTransition(R.anim.trans_left_in,
                                                    R.anim.trans_left_out);
                                        }
                                        else
                                        {
                                            Intent intent = new Intent(SplashActivity.this, LoginActivity .class);
                                            intent .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                            startActivity(intent);
                                            overridePendingTransition(R.anim.trans_left_in,
                                                    R.anim.trans_left_out);

                                        }


                                    }


                                }
                            }, 5000);



                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                    }
                }
        ) {

        };
        int socketTimeout = 30000;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        postRequest.setRetryPolicy(policy);
        queue.add(postRequest);
    }
}
