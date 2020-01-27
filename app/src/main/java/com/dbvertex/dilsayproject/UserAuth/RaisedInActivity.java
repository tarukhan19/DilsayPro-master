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
import com.dbvertex.dilsayproject.Adapter.RaisedInAdapter;
import com.dbvertex.dilsayproject.EndPoints;
import com.dbvertex.dilsayproject.Model.HeightDTO;
import com.dbvertex.dilsayproject.Model.RaisedInDTO;
import com.dbvertex.dilsayproject.Network.ConnectivityReceiver;
import com.dbvertex.dilsayproject.Network.MyApplication;
import com.dbvertex.dilsayproject.R;
import com.dbvertex.dilsayproject.session.SessionManager;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RaisedInActivity extends AppCompatActivity implements
        ConnectivityReceiver.ConnectivityReceiverListener
    {
    private RecyclerView recyclerView;
    private RaisedInAdapter adapter;
    private List<RaisedInDTO> raisedInDTOList;
    LinearLayout nextLL;
    ProgressDialog progressDialog;
    RequestQueue queue;
    SessionManager sessionManager;
    boolean isConnected;
    Intent intent;
    String from="",filterRaisedIn;
    List<String> filterRaisedInList,sentfilterRaisedInList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_raised_in);

        progressDialog = new ProgressDialog(RaisedInActivity.this, R.style.CustomDialog);
        queue = Volley.newRequestQueue(RaisedInActivity.this);
        sessionManager = new SessionManager(this);
        intent=getIntent();

        sentfilterRaisedInList=new ArrayList<>();
        if (intent.hasExtra("from"))
        {
            from=intent.getStringExtra("from");
            filterRaisedIn=sessionManager.getFilterRaisedIn().get(SessionManager.KEY_FILTER_RAISEDIN);
            filterRaisedInList = new ArrayList<String>(Arrays.asList(filterRaisedIn.split(",")));

        }
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_main);
        LinearLayout back = toolbar.findViewById(R.id.back_LL);
        TextView toolbar_title = toolbar.findViewById(R.id.titleTV);
        toolbar_title.setText("Raised In");


        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RaisedInActivity.this, HeightActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                overridePendingTransition(R.anim.trans_left_in,
                        R.anim.trans_left_out);
            }
        });

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        nextLL=findViewById(R.id.nextLL);
        raisedInDTOList = new ArrayList<>();
        adapter = new RaisedInAdapter(this, raisedInDTOList,nextLL,from,sentfilterRaisedInList);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setAdapter(adapter);
//
//        nextLL.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(RaisedInActivity.this, ReligionActivity.class);
//                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                startActivity(intent);
//
//                overridePendingTransition(R.anim.trans_left_in,
//                        R.anim.trans_left_out);
//            }
//        });

        loadRaised();


    }

    private void loadRaised()
    {
        progressDialog.setMessage("Please Wait..");
        progressDialog.setCancelable(true);
        progressDialog.show();

        StringRequest postRequest = new StringRequest(Request.Method.POST, EndPoints.LOADRAISEDIN,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("response", response);
                        try {
                            raisedInDTOList.clear();
                            progressDialog.dismiss();
                            JSONObject object=new JSONObject(response);

                            String status=object.getString("status");
                            String message=object.getString("message");
                            if (status.equalsIgnoreCase("200") && message.equalsIgnoreCase("success"))
                            {
                                JSONObject data= object.getJSONObject("data");
                                JSONArray dataarray=data.getJSONArray("raised_in");
                                String raised_in_saved= data.getString("raised_in_saved");
                                if (!from.equalsIgnoreCase("filter"))
                                {
                                    if (raised_in_saved.isEmpty())
                                    {
                                        String raisedinsaved= dataarray.getString(0);
                                        sessionManager.setRaisedIn(raisedinsaved);

                                    }
                                    else
                                    {
                                        sessionManager.setRaisedIn(raised_in_saved);

                                    }
                                }

                                for (int i=0;i<dataarray.length();i++)
                                {
                                    String raisedinname=dataarray.getString(i);

                                    RaisedInDTO raisedInDTO=new RaisedInDTO();
                                  //  raisedInDTO.setRaisedInId(raisedinid);
                                    raisedInDTO.setRaisedInName(raisedinname);
                                    if (!from.equalsIgnoreCase("filter"))
                                    {

                                        if (!sessionManager.getRaisedIn().get(SessionManager.KEY_RAISEDIN).isEmpty())
                                        {
                                            if (sessionManager.getRaisedIn().get(SessionManager.KEY_RAISEDIN)
                                                    .equalsIgnoreCase(raisedinname))
                                            {
                                                raisedInDTO.setSelected(true);
                                                adapter.selection=i;
                                            }

                                        }


                                    }
                                    else
                                    {
                                        for (int j=0;j<filterRaisedInList.size();j++)
                                        {
                                            String filterreligion = filterRaisedInList.get(j);

                                            if (filterreligion.equalsIgnoreCase(raisedinname))
                                            {
                                                raisedInDTO.setSelected(!raisedInDTO.isSelected());
                                                sentfilterRaisedInList.add(filterreligion);

                                            }

                                        }
                                    }

                                    raisedInDTOList.add(raisedInDTO);


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
                params.put("height", sessionManager.getHeight().get(SessionManager.KEY_HEIGHT));
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
        Intent intent = new Intent(RaisedInActivity.this, HeightActivity.class);
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
            loadRaised();
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
                        loadRaised();
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
        this.isConnected = isConnected;
        Log.e("onNetworkConnectionconn", isConnected + "");

        showSnack(isConnected);


    }
}
