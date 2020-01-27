package com.dbvertex.dilsayproject.UserAuth;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.databinding.DataBindingUtil;

import android.app.ProgressDialog;
import android.content.Intent;
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
import com.dbvertex.dilsayproject.Filter.FilterActivity;
import com.dbvertex.dilsayproject.Model.CareerDTO;
import com.dbvertex.dilsayproject.R;
import com.dbvertex.dilsayproject.databinding.ActivityLookingForBinding;
import com.dbvertex.dilsayproject.session.SessionManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LookingForActivity extends AppCompatActivity {
    ActivityLookingForBinding binding;
    SessionManager sessionManager;

    String lookingfor = "";
    ProgressDialog progressDialog;
    RequestQueue queue;

    Intent intent;
    String from="",filterLookingFor="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_looking_for);
        sessionManager = new SessionManager(this);
        progressDialog = new ProgressDialog(LookingForActivity.this, R.style.CustomDialog);

        intent=getIntent();
        if (intent.hasExtra("from"))
        {
            from=intent.getStringExtra("from");
            filterLookingFor=sessionManager.getFilterLookingFor().get(SessionManager.KEY_FILTER_LOOKINGFOR);

            if (!filterLookingFor.isEmpty())
            {
                if (filterLookingFor.equalsIgnoreCase("M"))
                {
                    binding.maleLL.setBackground(getResources().getDrawable(R.drawable.solidgradientcircle));
                    binding.maleIV.setImageDrawable(getResources().getDrawable(R.drawable.malewhite));
                    binding.femaleLL.setBackground(getResources().getDrawable(R.drawable.solidwhitecircle));
                    binding.femaleIV.setImageDrawable(getResources().getDrawable(R.drawable.femalecolor));
                    binding.maleTV.setTextColor(getResources().getColor(R.color.colorPrimary));
                    binding.femaleTV.setTextColor(getResources().getColor(R.color.grey));
                }
                else
                {
                    binding.maleLL.setBackground(getResources().getDrawable(R.drawable.solidwhitecircle));
                    binding.maleIV.setImageDrawable(getResources().getDrawable(R.drawable.malecolor));
                    binding.femaleLL.setBackground(getResources().getDrawable(R.drawable.solidgradientcircle));
                    binding.femaleIV.setImageDrawable(getResources().getDrawable(R.drawable.femalewhite));
                    binding.maleTV.setTextColor(getResources().getColor(R.color.grey));
                    binding.femaleTV.setTextColor(getResources().getColor(R.color.colorPrimary));
                }
            }


            Log.e("filterReleigionList",filterLookingFor+"");


        }

        queue = Volley.newRequestQueue(LookingForActivity.this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_main);
        final LinearLayout back = toolbar.findViewById(R.id.back_LL);
        TextView toolbar_title = toolbar.findViewById(R.id.titleTV);
        toolbar_title.setText("Looking For");


        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LookingForActivity.this, UploadPicsActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                overridePendingTransition(R.anim.trans_left_in,
                        R.anim.trans_left_out);
            }
        });




        binding.maleLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (lookingfor.isEmpty() || lookingfor.equalsIgnoreCase("F")) {
                    binding.maleLL.setBackground(getResources().getDrawable(R.drawable.solidgradientcircle));
                    binding.maleIV.setImageDrawable(getResources().getDrawable(R.drawable.malewhite));
                    binding.femaleLL.setBackground(getResources().getDrawable(R.drawable.solidwhitecircle));
                    binding.femaleIV.setImageDrawable(getResources().getDrawable(R.drawable.femalecolor));
                    binding.maleTV.setTextColor(getResources().getColor(R.color.colorPrimary));
                    binding.femaleTV.setTextColor(getResources().getColor(R.color.grey));
                    if (intent.hasExtra("from"))
                    {

                        filterLookingFor="M";
                    }
                    else
                    {
                        lookingfor = "M";
                        sessionManager.setLookingfor(lookingfor);
                    }


                }



            }


        });

        binding.femaleLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (lookingfor.isEmpty() || lookingfor.equalsIgnoreCase("M")) {
                    binding.femaleLL.setBackground(getResources().getDrawable(R.drawable.solidgradientcircle));
                    binding.femaleIV.setImageDrawable(getResources().getDrawable(R.drawable.femalewhite));
                    binding.maleLL.setBackground(getResources().getDrawable(R.drawable.solidwhitecircle));
                    binding.maleIV.setImageDrawable(getResources().getDrawable(R.drawable.malecolor));
                    binding.maleTV.setTextColor(getResources().getColor(R.color.grey));
                    binding.femaleTV.setTextColor(getResources().getColor(R.color.colorPrimary));
                    if (intent.hasExtra("from"))
                    {

                        filterLookingFor="F";
                    }
                    else
                    {
                        lookingfor = "F";
                        sessionManager.setLookingfor(lookingfor);
                    }

                }


            }


        });

        binding.nextLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (intent.hasExtra("from"))
                {

                        sessionManager.setFilterLookingFor(filterLookingFor);
                    Intent intent = new Intent(LookingForActivity.this, FilterActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    overridePendingTransition(R.anim.trans_left_in,
                            R.anim.trans_left_out);
                }
                else
                {
                    if (!sessionManager.getLookingFor().get(SessionManager.KEY_LOOKINGFOR).isEmpty()) {
                        Intent intent = new Intent(LookingForActivity.this, CommunityActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        overridePendingTransition(R.anim.trans_left_in,
                                R.anim.trans_left_out);
                    }

                    else
                    {
                        Toast.makeText(LookingForActivity.this, "Select one option", Toast.LENGTH_SHORT).show();
                    }
                }

            }
        });

        loadLookingFor();
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(LookingForActivity.this, UploadPicsActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        startActivity(intent);

        overridePendingTransition(R.anim.trans_left_in,
                R.anim.trans_left_out);
    }



    private void loadLookingFor()
    {
        progressDialog.setMessage("Please Wait..");
        progressDialog.setCancelable(true);
        progressDialog.show();

        StringRequest postRequest = new StringRequest(Request.Method.POST, EndPoints.LOADLOOKINGFOR,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("response", response);
                        try {

                            progressDialog.dismiss();
                            JSONObject object=new JSONObject(response);

                            String status=object.getString("status");
                            String message=object.getString("message");

                            //{"status":200,"message":"success","data":{"id":139,"looking_for_saved":"M"}}
                            if (status.equalsIgnoreCase("200") && message.equalsIgnoreCase("success"))
                            {
                                JSONObject jsonObject=object.getJSONObject("data");
                                if (!intent.hasExtra("from"))
                                {
                                    String looking_for_saved=jsonObject.getString("looking_for_saved");
                                    sessionManager.setLookingfor(looking_for_saved);

                                    if (looking_for_saved.equalsIgnoreCase("F"))
                                    {
                                        binding.femaleLL.setBackground(getResources().getDrawable(R.drawable.solidgradientcircle));
                                        binding.femaleIV.setImageDrawable(getResources().getDrawable(R.drawable.femalewhite));
                                        binding.maleLL.setBackground(getResources().getDrawable(R.drawable.solidwhitecircle));
                                        binding.maleIV.setImageDrawable(getResources().getDrawable(R.drawable.malecolor));
                                        binding.maleTV.setTextColor(getResources().getColor(R.color.grey));
                                        binding.femaleTV.setTextColor(getResources().getColor(R.color.colorPrimary));
                                    }

                                    else if (looking_for_saved.equalsIgnoreCase("M"))
                                    {

                                        binding.maleLL.setBackground(getResources().getDrawable(R.drawable.solidgradientcircle));
                                        binding.maleIV.setImageDrawable(getResources().getDrawable(R.drawable.malewhite));
                                        binding.femaleLL.setBackground(getResources().getDrawable(R.drawable.solidwhitecircle));
                                        binding.femaleIV.setImageDrawable(getResources().getDrawable(R.drawable.femalecolor));
                                        binding.maleTV.setTextColor(getResources().getColor(R.color.colorPrimary));
                                        binding.femaleTV.setTextColor(getResources().getColor(R.color.grey));


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

}
