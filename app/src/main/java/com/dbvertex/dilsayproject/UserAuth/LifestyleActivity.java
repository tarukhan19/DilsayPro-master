package com.dbvertex.dilsayproject.UserAuth;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.databinding.DataBindingUtil;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
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
import com.dbvertex.dilsayproject.databinding.ActivityLifestyleBinding;
import com.dbvertex.dilsayproject.session.SessionManager;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class LifestyleActivity extends AppCompatActivity implements
        ConnectivityReceiver.ConnectivityReceiverListener {
    ActivityLifestyleBinding binding;
    String foodprefrence = "", smokeprefrence = "", drinkprefrence = "";
    SessionManager sessionManager;
    boolean isConnected;
    ProgressDialog progressDialog;
    RequestQueue queue;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_lifestyle);
        sessionManager = new SessionManager(this);
        progressDialog = new ProgressDialog(LifestyleActivity.this, R.style.CustomDialog);
        queue = Volley.newRequestQueue(LifestyleActivity.this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_main);
        LinearLayout back = toolbar.findViewById(R.id.back_LL);
        TextView toolbar_title = toolbar.findViewById(R.id.titleTV);
        toolbar_title.setText("Lifestyle");


        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LifestyleActivity.this, ReligionActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                overridePendingTransition(R.anim.trans_left_in,
                        R.anim.trans_left_out);
            }
        });


        if (!sessionManager.getLifestyle().get(SessionManager.KEY_FOODPREF).isEmpty())
        {
            if (sessionManager.getLifestyle().get(SessionManager.KEY_FOODPREF).equalsIgnoreCase("veg"))
            {
                binding.vegLL.setBackground(getResources().getDrawable(R.drawable.gradientcurverectangle2));
                binding.nonvegLL.setBackground(getResources().getDrawable(R.drawable.greycurverectangle1));
                binding.vegTV.setTextColor(getResources().getColor(R.color.white));
                binding.nonvegTV.setTextColor(getResources().getColor(R.color.colorPrimary));
                foodprefrence = "veg";

            }

            else
            {

                binding.nonvegLL.setBackground(getResources().getDrawable(R.drawable.gradientcurverectangle1));
                binding.vegLL.setBackground(getResources().getDrawable(R.drawable.greycurverectangle2));
                binding.nonvegTV.setTextColor(getResources().getColor(R.color.white));
                binding.vegTV.setTextColor(getResources().getColor(R.color.colorPrimary));
                foodprefrence = "nonveg";


            }
        }



        if (!sessionManager.getLifestyle().get(SessionManager.KEY_SMOKE).isEmpty())
        {
            if (sessionManager.getLifestyle().get(SessionManager.KEY_SMOKE).equalsIgnoreCase("smoke"))
            {
                binding.smokingLL.setBackground(getResources().getDrawable(R.drawable.gradientcurverectangle2));
                binding.nonsmokeLL.setBackground(getResources().getDrawable(R.drawable.greycurverectangle));
                binding.smokeocassionalLL.setBackground(getResources().getDrawable(R.drawable.greycurverectangle1));

                binding.smokeTV.setTextColor(getResources().getColor(R.color.white));
                binding.nonsmokeTV.setTextColor(getResources().getColor(R.color.colorPrimary));
                binding.smokeocassionalTV.setTextColor(getResources().getColor(R.color.colorPrimary));

                smokeprefrence = "smoke";

            }

            else if (sessionManager.getLifestyle().get(SessionManager.KEY_SMOKE).equalsIgnoreCase("nonsmoke"))
            {

                binding.nonsmokeLL.setBackground(getResources().getDrawable(R.drawable.gradientcurverectangle));
                binding.smokingLL.setBackground(getResources().getDrawable(R.drawable.greycurverectangle2));
                binding.smokeocassionalLL.setBackground(getResources().getDrawable(R.drawable.greycurverectangle1));

                binding.nonsmokeTV.setTextColor(getResources().getColor(R.color.white));
                binding.smokeTV.setTextColor(getResources().getColor(R.color.colorPrimary));
                binding.smokeocassionalTV.setTextColor(getResources().getColor(R.color.colorPrimary));

                smokeprefrence = "nonsmoke";


            }

            else
            {
                binding.smokeocassionalLL.setBackground(getResources().getDrawable(R.drawable.gradientcurverectangle1));
                binding.smokingLL.setBackground(getResources().getDrawable(R.drawable.greycurverectangle2));
                binding.nonsmokeLL.setBackground(getResources().getDrawable(R.drawable.greycurverectangle));

                binding.smokeocassionalTV.setTextColor(getResources().getColor(R.color.white));
                binding.smokeTV.setTextColor(getResources().getColor(R.color.colorPrimary));
                binding.nonsmokeTV.setTextColor(getResources().getColor(R.color.colorPrimary));

                smokeprefrence = "smokeocassionally";
            }
        }



        if (!sessionManager.getLifestyle().get(SessionManager.KEY_DRINK).isEmpty())
        {
            if (sessionManager.getLifestyle().get(SessionManager.KEY_DRINK).equalsIgnoreCase("drink"))
            {
                binding.drinkLL.setBackground(getResources().getDrawable(R.drawable.gradientcurverectangle2));
                binding.nondrinkLL.setBackground(getResources().getDrawable(R.drawable.greycurverectangle));
                binding.drinkocassionalLL.setBackground(getResources().getDrawable(R.drawable.greycurverectangle1));

                binding.drinkTV.setTextColor(getResources().getColor(R.color.white));
                binding.nondrinkTV.setTextColor(getResources().getColor(R.color.colorPrimary));
                binding.drinkocassionalTV.setTextColor(getResources().getColor(R.color.colorPrimary));

                drinkprefrence = "drink";

            }

            else if (sessionManager.getLifestyle().get(SessionManager.KEY_DRINK).equalsIgnoreCase("nondrink"))
            {

                binding.nondrinkLL.setBackground(getResources().getDrawable(R.drawable.gradientcurverectangle));
                binding.drinkLL.setBackground(getResources().getDrawable(R.drawable.greycurverectangle2));
                binding.drinkocassionalLL.setBackground(getResources().getDrawable(R.drawable.greycurverectangle1));

                binding.nondrinkTV.setTextColor(getResources().getColor(R.color.white));
                binding.drinkTV.setTextColor(getResources().getColor(R.color.colorPrimary));
                binding.drinkocassionalTV.setTextColor(getResources().getColor(R.color.colorPrimary));

                drinkprefrence = "nondrink";


            }

            else
            {
                binding.drinkocassionalLL.setBackground(getResources().getDrawable(R.drawable.gradientcurverectangle1));
                binding.drinkLL.setBackground(getResources().getDrawable(R.drawable.greycurverectangle2));
                binding.nondrinkLL.setBackground(getResources().getDrawable(R.drawable.greycurverectangle));

                binding.drinkocassionalTV.setTextColor(getResources().getColor(R.color.white));
                binding.drinkTV.setTextColor(getResources().getColor(R.color.colorPrimary));
                binding.nondrinkTV.setTextColor(getResources().getColor(R.color.colorPrimary));

                drinkprefrence = "drinkocassionally";
            }
        }







        binding.vegLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (foodprefrence.equalsIgnoreCase("nonveg") || foodprefrence.equalsIgnoreCase("")) {
                    binding.vegLL.setBackground(getResources().getDrawable(R.drawable.gradientcurverectangle2));
                    binding.vegTV.setTextColor(getResources().getColor(R.color.white));
                    binding.nonvegLL.setBackground(getResources().getDrawable(R.drawable.greycurverectangle1));
                    binding.nonvegTV.setTextColor(getResources().getColor(R.color.colorPrimary));
                    foodprefrence = "veg";
                }


            }
        });

        binding.nonvegLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.nonvegLL.setBackground(getResources().getDrawable(R.drawable.gradientcurverectangle1));
                binding.nonvegTV.setTextColor(getResources().getColor(R.color.white));
                binding.vegLL.setBackground(getResources().getDrawable(R.drawable.greycurverectangle2));
                binding.vegTV.setTextColor(getResources().getColor(R.color.colorPrimary));
                foodprefrence = "nonveg";
            }
        });

        binding.smokingLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.smokingLL.setBackground(getResources().getDrawable(R.drawable.gradientcurverectangle2));
                binding.smokeTV.setTextColor(getResources().getColor(R.color.white));
                binding.smokeocassionalLL.setBackground(getResources().getDrawable(R.drawable.greycurverectangle1));
                binding.smokeocassionalTV.setTextColor(getResources().getColor(R.color.colorPrimary));
                binding.nonsmokeLL.setBackground(getResources().getDrawable(R.drawable.greycurverectangle));
                binding.nonsmokeTV.setTextColor(getResources().getColor(R.color.colorPrimary));
                smokeprefrence = "smoke";
            }
        });

        binding.nonsmokeLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.smokingLL.setBackground(getResources().getDrawable(R.drawable.greycurverectangle2));
                binding.smokeTV.setTextColor(getResources().getColor(R.color.colorPrimary));
                binding.smokeocassionalLL.setBackground(getResources().getDrawable(R.drawable.greycurverectangle1));
                binding.smokeocassionalTV.setTextColor(getResources().getColor(R.color.colorPrimary));
                binding.nonsmokeLL.setBackground(getResources().getDrawable(R.drawable.gradientcurverectangle));
                binding.nonsmokeTV.setTextColor(getResources().getColor(R.color.white));
                smokeprefrence = "nonsmoke";

            }
        });

        binding.smokeocassionalLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.smokingLL.setBackground(getResources().getDrawable(R.drawable.greycurverectangle2));
                binding.smokeTV.setTextColor(getResources().getColor(R.color.colorPrimary));
                binding.smokeocassionalLL.setBackground(getResources().getDrawable(R.drawable.gradientcurverectangle1));
                binding.smokeocassionalTV.setTextColor(getResources().getColor(R.color.white));
                binding.nonsmokeLL.setBackground(getResources().getDrawable(R.drawable.greycurverectangle));
                binding.nonsmokeTV.setTextColor(getResources().getColor(R.color.colorPrimary));
                smokeprefrence = "smokeocassionally";

            }
        });


        binding.drinkLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.drinkLL.setBackground(getResources().getDrawable(R.drawable.gradientcurverectangle2));
                binding.drinkTV.setTextColor(getResources().getColor(R.color.white));
                binding.drinkocassionalLL.setBackground(getResources().getDrawable(R.drawable.greycurverectangle1));
                binding.drinkocassionalTV.setTextColor(getResources().getColor(R.color.colorPrimary));
                binding.nondrinkLL.setBackground(getResources().getDrawable(R.drawable.greycurverectangle));
                binding.nondrinkTV.setTextColor(getResources().getColor(R.color.colorPrimary));
                drinkprefrence = "drink";
            }
        });

        binding.nondrinkLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.drinkLL.setBackground(getResources().getDrawable(R.drawable.greycurverectangle2));
                binding.drinkTV.setTextColor(getResources().getColor(R.color.colorPrimary));
                binding.drinkocassionalLL.setBackground(getResources().getDrawable(R.drawable.greycurverectangle1));
                binding.drinkocassionalTV.setTextColor(getResources().getColor(R.color.colorPrimary));
                binding.nondrinkLL.setBackground(getResources().getDrawable(R.drawable.gradientcurverectangle));
                binding.nondrinkTV.setTextColor(getResources().getColor(R.color.white));
                drinkprefrence = "nondrink";

            }
        });

        binding.drinkocassionalLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.drinkLL.setBackground(getResources().getDrawable(R.drawable.greycurverectangle2));
                binding.drinkTV.setTextColor(getResources().getColor(R.color.colorPrimary));
                binding.drinkocassionalLL.setBackground(getResources().getDrawable(R.drawable.gradientcurverectangle1));
                binding.drinkocassionalTV.setTextColor(getResources().getColor(R.color.white));
                binding.nondrinkLL.setBackground(getResources().getDrawable(R.drawable.greycurverectangle));
                binding.nondrinkTV.setTextColor(getResources().getColor(R.color.colorPrimary));
                drinkprefrence = "drinkocassionally";

            }
        });

        binding.nextLL.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                if (foodprefrence.isEmpty()) {
                    Toast.makeText(LifestyleActivity.this, "Select food prefrence", Toast.LENGTH_SHORT).show();
                } else if (smokeprefrence.isEmpty()) {
                    Toast.makeText(LifestyleActivity.this, "Select smoke prefrence", Toast.LENGTH_SHORT).show();
                } else if (drinkprefrence.isEmpty()) {
                    Toast.makeText(LifestyleActivity.this, "Select drink prefrence", Toast.LENGTH_SHORT).show();
                } else {
                    if (isConnected)
                    {
                        sessionManager.setLifestyle(foodprefrence, smokeprefrence, drinkprefrence);
                        Intent intent = new Intent(LifestyleActivity.this, ChooseInterestActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        overridePendingTransition(R.anim.trans_left_in,
                                R.anim.trans_left_out);
                    } else {
                        showSnack(isConnected);
                    }

                }

            }
        });

        profileTask();

    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(LifestyleActivity.this, ReligionActivity.class);
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
    public void onNetworkConnectionChanged(boolean isConnected)
    {
        this.isConnected = isConnected;
        Log.e("onNetworkConnectionconn", isConnected + "");

        showSnack(isConnected);


    }



    private void profileTask()
    {

        progressDialog.setMessage("Please Wait..");
        progressDialog.setCancelable(true);
        progressDialog.show();

        StringRequest postRequest = new StringRequest(Request.Method.POST, EndPoints.LIFESTYLE_LIST,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("response", response);
                        try {

                            progressDialog.dismiss();
                            JSONObject obj = new JSONObject(response);
                            Log.e("response", response);
                            //{"user_id":"122","intrests[]":"Biker,Adventure Junkie,Sky Diving"}
                            int status = obj.getInt("status");
                            String message = obj.getString("message");

                            if (status == 200 && message.equals("success"))
                            {
                                JSONObject jsonObject=obj.getJSONObject("data");
                                String foodpref=jsonObject.getString("food_prefrence");
                                String smokepref=jsonObject.getString("smoke");
                                String drinkpref=jsonObject.getString("drinking");

                                sessionManager.setLifestyle(foodpref, smokepref, drinkpref);


                            } else {
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
                params.put("user_id", sessionManager.getUserId().get(SessionManager.KEY_USERID));
                params.put("religion", sessionManager.getReligion().get(SessionManager.KEY_RELIGION));

                Log.e("params", params.toString());
                return params;
            }
        };
        int socketTimeout = 30000;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        postRequest.setRetryPolicy(policy);
        queue.add(postRequest);
    }


}
