package com.dbvertex.dilsayproject.UserAuth;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.dbvertex.dilsayproject.Adapter.HeightAdapter;
import com.dbvertex.dilsayproject.EndPoints;
import com.dbvertex.dilsayproject.Model.HeightDTO;
import com.dbvertex.dilsayproject.Network.ConnectivityReceiver;
import com.dbvertex.dilsayproject.Network.MyApplication;
import com.dbvertex.dilsayproject.R;
import com.dbvertex.dilsayproject.session.SessionManager;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HeightActivity extends AppCompatActivity implements
        ConnectivityReceiver.ConnectivityReceiverListener{
    private RecyclerView recyclerView;
    private HeightAdapter adapter;
    private List<HeightDTO> heightDTOList;
    LinearLayout nextLL;
    ProgressDialog progressDialog;
    RequestQueue queue;
    SessionManager sessionManager;
    boolean isConnected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_height);

        progressDialog = new ProgressDialog(HeightActivity.this, R.style.CustomDialog);
        queue = Volley.newRequestQueue(HeightActivity.this);
        sessionManager = new SessionManager(this);


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_main);
        LinearLayout back = toolbar.findViewById(R.id.back_LL);
        TextView toolbar_title = toolbar.findViewById(R.id.titleTV);
        toolbar_title.setText("Height");
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(HeightActivity.this, EducationActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                overridePendingTransition(R.anim.trans_left_in,
                        R.anim.trans_left_out);
            }
        });

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        nextLL=findViewById(R.id.nextLL);
        heightDTOList = new ArrayList<>();
        adapter = new HeightAdapter(this, heightDTOList,nextLL);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setAdapter(adapter);

//        nextLL.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(HeightActivity.this, RaisedInActivity.class);
//                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                startActivity(intent);
//
//                overridePendingTransition(R.anim.trans_left_in,
//                        R.anim.trans_left_out);
//            }
//        });

    }

    private void loadHeight()
    {
        progressDialog.setMessage("Please Wait..");
        progressDialog.setCancelable(true);
        progressDialog.show();

        StringRequest postRequest = new StringRequest(Request.Method.POST, EndPoints.LOADHEIGHT,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("response", response);
                        try {

                            heightDTOList.clear();
                            progressDialog.dismiss();
                            JSONObject object=new JSONObject(response);

                            String status=object.getString("status");
                            String message=object.getString("message");
                            if (status.equalsIgnoreCase("200") && message.equalsIgnoreCase("success"))
                            {
                                JSONObject data= object.getJSONObject("data");
                                JSONArray dataarray=data.getJSONArray("height");
                                String height_saved= data.getString("height_saved");
                                if (height_saved.isEmpty())
                                {
                                    String heightsaved= dataarray.getString(0);
                                    sessionManager.setHeight(heightsaved);

                                }
                                else
                                {
                                    sessionManager.setHeight(height_saved);

                                }
                                for (int i=0;i<dataarray.length();i++)
                                {
                                    String heightname=dataarray.getString(i);

                                    HeightDTO heightDTO=new HeightDTO();
                                    heightDTO.setHeightName(heightname);

                                    if (!sessionManager.getHeight().get(SessionManager.KEY_HEIGHT).isEmpty())
                                    {
                                        if (sessionManager.getHeight().get(SessionManager.KEY_HEIGHT)
                                                .equalsIgnoreCase(heightname))
                                        {
                                            heightDTO.setSelected(true);
                                            adapter.selection=i;
                                        }

                                    }

                                    heightDTOList.add(heightDTO);
                                }
                                adapter.notifyDataSetChanged();
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
                params.put("education", sessionManager.getEducation().get(SessionManager.KEY_EDUCATION));
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



    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(HeightActivity.this, EducationActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);

        overridePendingTransition(R.anim.trans_left_in,
                R.anim.trans_left_out);

    }

    @Override
    public void onStart()
    {
        isConnected = ConnectivityReceiver.isConnected();
        Log.e("onStart",isConnected+"");
        if (!isConnected)
        {
            showSnack(isConnected);
        }
        else
        {
            loadHeight();
        }
        super.onStart();
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
                        loadHeight();
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
}
