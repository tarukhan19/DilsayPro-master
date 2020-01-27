package com.dbvertex.dilsayproject;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
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
import com.dbvertex.dilsayproject.Adapter.FaqAdapter;
import com.dbvertex.dilsayproject.Model.FaqInDTO;
import com.dbvertex.dilsayproject.UserAuth.HomePageActivity;
import com.dbvertex.dilsayproject.session.SessionManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FrequentlyAskQstnActivity extends AppCompatActivity {
    LinearLayout back_LL;
    Toolbar toolbar_main;
    ProgressDialog progressDialog;
    RequestQueue queue;
    TextView titleTV;
    private RecyclerView recyclerView;
    private FaqAdapter adapter;
    private List<FaqInDTO> faqDTOList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_frequently_ask_qstn);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);

        progressDialog = new ProgressDialog(FrequentlyAskQstnActivity.this, R.style.CustomDialog);
        queue = Volley.newRequestQueue(FrequentlyAskQstnActivity.this);
        faqDTOList = new ArrayList<>();
        adapter = new FaqAdapter(this, faqDTOList);


        if (android.os.Build.VERSION.SDK_INT >= 21) {
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(this.getResources().getColor(R.color.colorPrimary));
        }
        toolbar_main = findViewById(R.id.toolbar_main);
        titleTV = (TextView) toolbar_main.findViewById(R.id.titleTV);
        back_LL = toolbar_main.findViewById(R.id.back_LL);
        titleTV.setText("FAQ");
        back_LL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FrequentlyAskQstnActivity.this, HomePageActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intent.putExtra("from", "setting");

                startActivity(intent);

                overridePendingTransition(R.anim.trans_left_in,
                        R.anim.trans_left_out);
            }
        });

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setAdapter(adapter);

        loadData();


    }

    private void loadData() {
        progressDialog.setMessage("Please Wait..");
        progressDialog.setCancelable(true);
        progressDialog.show();

        StringRequest postRequest = new StringRequest(Request.Method.GET, EndPoints.FAQ,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("response", response);
                        try {
                            faqDTOList.clear();
                            progressDialog.dismiss();
                            JSONObject object = new JSONObject(response);

                            String status = object.getString("Status");
                            String message = object.getString("Message");
                            if (status.equalsIgnoreCase("200") && message.equalsIgnoreCase("success")) {
                                JSONArray dataarray = object.getJSONArray("data");
                                for (int i = 0; i < dataarray.length(); i++) {
                                    JSONObject obj = dataarray.getJSONObject(i);
                                    String question = obj.getString("question");
                                    String answer = obj.getString("answer");
                                    String id = obj.getString("id");

                                    FaqInDTO faqInDTO = new FaqInDTO();
                                    faqInDTO.setQuestn(question);
                                    faqInDTO.setAnswer(answer);
                                    faqInDTO.setId(id);

                                    faqDTOList.add(faqInDTO);


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

        };
        int socketTimeout = 30000;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        postRequest.setRetryPolicy(policy);
        queue.add(postRequest);
    }
}
