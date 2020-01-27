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
import com.dbvertex.dilsayproject.Adapter.ReligionAdapter;
import com.dbvertex.dilsayproject.EndPoints;
import com.dbvertex.dilsayproject.Model.ReligionDTO;
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

public class ReligionActivity extends AppCompatActivity  implements
        ConnectivityReceiver.ConnectivityReceiverListener{

    private RecyclerView recyclerView;
    private ReligionAdapter adapter;
    private List<ReligionDTO> religionDTOList;
    LinearLayout nextLL;
    ProgressDialog progressDialog;
    RequestQueue queue;
    SessionManager sessionManager;
    boolean isConnected;
    Intent intent;
    String from="",filterReleigion;
    List<String> filterReleigionList,sentfilterReleigionList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_religion);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_main);
        LinearLayout back = toolbar.findViewById(R.id.back_LL);
        TextView toolbar_title = toolbar.findViewById(R.id.titleTV);
        toolbar_title.setText("Religion");


        progressDialog = new ProgressDialog(ReligionActivity.this, R.style.CustomDialog);
        queue = Volley.newRequestQueue(ReligionActivity.this);
        sessionManager = new SessionManager(this);
        intent=getIntent();

        sentfilterReleigionList=new ArrayList<>();
        if (intent.hasExtra("from"))
        {
            from=intent.getStringExtra("from");
            filterReleigion=sessionManager.getFilterReligion().get(SessionManager.KEY_FILTER_RELIGION_NAME);
            filterReleigionList = new ArrayList<String>(Arrays.asList(filterReleigion.split(",")));

            Log.e("filterReleigionList",filterReleigionList+"");


        }
        back.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ReligionActivity.this, RaisedInActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

                startActivity(intent);

                overridePendingTransition(R.anim.trans_left_in,
                        R.anim.trans_left_out);
            }
        });

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        nextLL=findViewById(R.id.nextLL);
        religionDTOList = new ArrayList<>();
        adapter = new ReligionAdapter(this, religionDTOList,nextLL,from,sentfilterReleigionList);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setAdapter(adapter);


    }

    private void loadReligion()
    {

        progressDialog.setMessage("Please Wait..");
        progressDialog.setCancelable(true);
        progressDialog.show();

        StringRequest postRequest = new StringRequest(Request.Method.POST, EndPoints.LOADRELIGION,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("response", response);
                        try {

                            religionDTOList.clear();
                            progressDialog.dismiss();
                            JSONObject object=new JSONObject(response);

                            String status=object.getString("status");
                            String message=object.getString("message");
                            if (status.equalsIgnoreCase("200") && message.equalsIgnoreCase("success"))
                            {
                                JSONObject data= object.getJSONObject("data");
                                JSONArray dataarray=data.getJSONArray("religion");
                                String religion_saved= data.getString("religion_saved");
                                if (!from.equalsIgnoreCase("filter"))
                                {
                                    if (religion_saved.isEmpty())
                                    {
                                        String religionsaved= dataarray.getString(0);
                                        sessionManager.setReligion(religionsaved);
                                    }
                                    else
                                    {
                                        sessionManager.setReligion(religion_saved);

                                    }
                                }


                                for (int i=0;i<dataarray.length();i++)
                                {
                                    String religionname=dataarray.getString(i);
                                    ReligionDTO religionDTO=new ReligionDTO();
                                    religionDTO.setRelegionName(religionname);
                                    if (!from.equalsIgnoreCase("filter"))
                                    {
                                        if (!sessionManager.getReligion().get(SessionManager.KEY_RELIGION).isEmpty())
                                        {

                                            if (sessionManager.getReligion().get(SessionManager.KEY_RELIGION)
                                                    .equalsIgnoreCase(religionname))
                                            {
                                                religionDTO.setSelected(true);
                                                adapter.selection=i;
                                            }

                                        }


                                    }
                                    else
                                    {
                                        for (int j=0;j<filterReleigionList.size();j++)
                                        {
                                            String filterreligion = filterReleigionList.get(j);

                                            if (filterreligion.equalsIgnoreCase(religionname))
                                            {
                                                religionDTO.setSelected(!religionDTO.isSelected());
                                                sentfilterReleigionList.add(filterreligion);

                                            }

                                        }
                                    }
                                    religionDTOList.add(religionDTO);

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
                params.put("raised_in", sessionManager.getRaisedIn().get(SessionManager.KEY_RAISEDIN));
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
        Intent intent = new Intent(ReligionActivity.this, RaisedInActivity.class);
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
            loadReligion();
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
                        loadReligion();
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
