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
import com.dbvertex.dilsayproject.Adapter.CareerAdapter;
import com.dbvertex.dilsayproject.EndPoints;
import com.dbvertex.dilsayproject.Model.CareerDTO;
import com.dbvertex.dilsayproject.Model.CommunityDTO;
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

public class CarrerActivity extends AppCompatActivity implements
        ConnectivityReceiver.ConnectivityReceiverListener {
    private RecyclerView recyclerView;
    private CareerAdapter adapter;
    private List<CareerDTO> careerDTOList;
    LinearLayout nextLL;

    ProgressDialog progressDialog;
    RequestQueue queue;
    SessionManager sessionManager;
    boolean isConnected;

    Intent intent;
    String from="",filterCareer;
    List<String> filterCareerList,sentfilterCareerList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_carrer);

        progressDialog = new ProgressDialog(CarrerActivity.this, R.style.CustomDialog);
        queue = Volley.newRequestQueue(CarrerActivity.this);
        sessionManager = new SessionManager(this);

        intent=getIntent();
        sentfilterCareerList=new ArrayList<>();
        if (intent.hasExtra("from"))
        {
            from=intent.getStringExtra("from");
            filterCareer=sessionManager.getFilterCareer().get(SessionManager.KEY_FILTER_CAREER);
            filterCareerList = new ArrayList<String>(Arrays.asList(filterCareer.split(",")));

        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_main);
        LinearLayout back = toolbar.findViewById(R.id.back_LL);
        TextView toolbar_title = toolbar.findViewById(R.id.titleTV);
        toolbar_title.setText("Career");




        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(CarrerActivity.this, CommunityActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

                startActivity(intent);

                overridePendingTransition(R.anim.trans_left_in,
                        R.anim.trans_left_out);
            }
        });

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        nextLL=findViewById(R.id.nextLL);
        careerDTOList = new ArrayList<>();
        adapter = new CareerAdapter(this, careerDTOList,nextLL,from,sentfilterCareerList);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setAdapter(adapter);
//
//        nextLL.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(CarrerActivity.this, LocationDescription.class);
//                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                startActivity(intent);
//                overridePendingTransition(R.anim.trans_left_in,
//                        R.anim.trans_left_out);
//            }
//        });


    }


    private void loadCareer()
    {
        progressDialog.setMessage("Please Wait..");
        progressDialog.setCancelable(true);
        progressDialog.show();

        StringRequest postRequest = new StringRequest(Request.Method.POST, EndPoints.LOADCAREERLIST,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("response", response);
                        try {

                            careerDTOList.clear();
                            progressDialog.dismiss();
                            JSONObject object=new JSONObject(response);

                            String status=object.getString("status");
                            String message=object.getString("message");
                            if (status.equalsIgnoreCase("200") && message.equalsIgnoreCase("success"))
                            {
                                JSONObject data= object.getJSONObject("data");
                                JSONArray dataarray=data.getJSONArray("career");
                                String career_saved= data.getString("career_saved");
                                if (career_saved.isEmpty())
                                {
                                    String careersaved= dataarray.getString(0);
                                    sessionManager.setCareer(careersaved);

                                }
                                else
                                {
                                    sessionManager.setCareer(career_saved);

                                }



                                for (int i=0;i<dataarray.length();i++)
                                {
                                    String careername=dataarray.getString(i);
                                    CareerDTO careerDTO=new CareerDTO();
//                                    careerDTO.setCareerId(communityid);
                                    careerDTO.setCareerName(careername);

                                    if (!from.equalsIgnoreCase("filter"))
                                    {
                                        if (!sessionManager.getCareer().get(SessionManager.KEY_CAREER).isEmpty())
                                        {
                                            if (sessionManager.getCareer().get(SessionManager.KEY_CAREER)
                                                    .equalsIgnoreCase(careername))
                                            {
                                                careerDTO.setSelected(true);
                                                adapter.selection=i;
                                            }

                                        }

                                    }
                                    else
                                    {
                                        for (int j=0;j<filterCareerList.size();j++)
                                        {
                                            String filterCommunity = filterCareerList.get(j);

                                            if (filterCommunity.equalsIgnoreCase(careername))
                                            {
                                                careerDTO.setSelected(!careerDTO.isSelected());
                                                sentfilterCareerList.add(filterCommunity);

                                            }

                                        }
                                    }


                                    careerDTOList.add(careerDTO);


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
                params.put("community", sessionManager.getCommunity().get(SessionManager.KEY_COMMUNITY));
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
        Intent intent = new Intent(CarrerActivity.this, CommunityActivity.class);
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
            loadCareer();
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
                        loadCareer();
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
