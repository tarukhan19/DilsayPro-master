package com.dbvertex.dilsayproject.UserAuth;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
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
import com.dbvertex.dilsayproject.EndPoints;
import com.dbvertex.dilsayproject.Plans.CardItem;
import com.dbvertex.dilsayproject.Plans.CardPagerAdapter;
import com.dbvertex.dilsayproject.Plans.ShadowTransformer;
import com.dbvertex.dilsayproject.R;
import com.dbvertex.dilsayproject.ShopAdapter;
import com.dbvertex.dilsayproject.session.SessionManager;
import com.yarolegovich.discretescrollview.DSVOrientation;
import com.yarolegovich.discretescrollview.DiscreteScrollView;
import com.yarolegovich.discretescrollview.InfiniteScrollAdapter;
import com.yarolegovich.discretescrollview.transform.ScaleTransformer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class PlansActivity extends AppCompatActivity implements View.OnClickListener{

    private ViewPager mViewPager;

    private CardPagerAdapter mCardAdapter;
    private ShadowTransformer mCardShadowTransformer;
  ProgressDialog progressDialog;
    RequestQueue queue;
    SessionManager sessionManager;

    TextView msgplanTV,likeplanTV,boostplanTV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plans);
        mViewPager = (ViewPager) findViewById(R.id.viewPager);
        msgplanTV=findViewById(R.id.msgplanTV);
        likeplanTV=findViewById(R.id.likeplanTV);
        boostplanTV=findViewById(R.id.boostplanTV);

        progressDialog = new ProgressDialog(PlansActivity.this, R.style.CustomDialog);
        queue = Volley.newRequestQueue(PlansActivity.this);
        sessionManager = new SessionManager(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_main);
        LinearLayout back = toolbar.findViewById(R.id.back_LL);
        TextView toolbar_title = toolbar.findViewById(R.id.titleTV);
        toolbar_title.setText("Plans");




        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PlansActivity.this, LocationDescription.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                overridePendingTransition(R.anim.trans_left_in,
                        R.anim.trans_left_out);
            }
        });



        loadPlans();
    }

    private void loadPlans()
    {
        progressDialog.setMessage("Please Wait..");
        progressDialog.setCancelable(true);
        progressDialog.show();

        StringRequest postRequest = new StringRequest(Request.Method.GET, EndPoints.PLANS,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("response", response);
                        try {

                            progressDialog.dismiss();
                            JSONObject object=new JSONObject(response);

                            String status=object.getString("status");
                            String message=object.getString("message");
                            if (status.equalsIgnoreCase("1") && message.equalsIgnoreCase("success"))
                            {

                                JSONArray jsonArray=object.getJSONArray("data");
                                mCardAdapter = new CardPagerAdapter(PlansActivity.this,jsonArray,
                                        msgplanTV,likeplanTV,boostplanTV);
                                mCardAdapter.addCardItem(new CardItem(R.string.title_1, R.string.text_1));
                                mCardAdapter.addCardItem(new CardItem(R.string.title_2, R.string.text_1));
                                mCardAdapter.addCardItem(new CardItem(R.string.title_3, R.string.text_1));
                                mCardShadowTransformer = new ShadowTransformer(mViewPager, mCardAdapter,PlansActivity.this,
                                        jsonArray,
                                        msgplanTV,likeplanTV,boostplanTV);

                                mViewPager.setAdapter(mCardAdapter);
                                mViewPager.setPageTransformer(false, mCardShadowTransformer);
                                mViewPager.setOffscreenPageLimit(3);


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
        ) ;
        int socketTimeout = 30000;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        postRequest.setRetryPolicy(policy);
        queue.add(postRequest);
    }

    @Override
    public void onClick(View view) {

            mViewPager.setAdapter(mCardAdapter);
            mViewPager.setPageTransformer(false, mCardShadowTransformer);

    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(PlansActivity.this, LocationDescription.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        overridePendingTransition(R.anim.trans_left_in,
                R.anim.trans_left_out);
    }
}
