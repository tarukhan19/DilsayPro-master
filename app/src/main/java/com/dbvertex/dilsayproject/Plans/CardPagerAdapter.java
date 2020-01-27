package com.dbvertex.dilsayproject.Plans;


import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.cardview.widget.CardView;
import androidx.viewpager.widget.PagerAdapter;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.dbvertex.dilsayproject.ContactUsActivity;
import com.dbvertex.dilsayproject.EndPoints;
import com.dbvertex.dilsayproject.R;
import com.dbvertex.dilsayproject.UserAuth.HomePageActivity;
import com.dbvertex.dilsayproject.UserAuth.PlansActivity;
import com.dbvertex.dilsayproject.UserAuth.SplashActivity;
import com.dbvertex.dilsayproject.session.SessionManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CardPagerAdapter extends PagerAdapter implements CardAdapter {

    private List<CardView> mViews;
    private List<CardItem> mData;
    private float mBaseElevation;
    CardView cardView;
    Context context;
    JSONArray response;
    TextView msgplanTV, likeplanTV, boostplanTV;
    ProgressDialog progressDialog;
    RequestQueue requestQueue;
    SessionManager sessionManager;
    String planid;
    TextView priceTV1, priceTV2;
    String price1, price2;
    Activity activity;
    RadioButton payRB, freetrialRB;
    LinearLayout freetrialLL, payLL;

    public CardPagerAdapter(Context context, JSONArray response, TextView msgplanTV,
                            TextView likeplanTV, TextView boostplanTV) {
        mData = new ArrayList<>();
        mViews = new ArrayList<>();
        this.context = context;
        this.response = response;
        this.msgplanTV = msgplanTV;
        this.likeplanTV = likeplanTV;
        this.boostplanTV = boostplanTV;
        sessionManager = new SessionManager(context);
        requestQueue = Volley.newRequestQueue(context);
        progressDialog = new ProgressDialog(context);
        activity = (Activity) context;

    }

    public void addCardItem(CardItem item) {
        mViews.add(null);
        mData.add(item);
    }

    public float getBaseElevation() {
        return mBaseElevation;
    }

    @Override
    public CardView getCardViewAt(int position) {
        return mViews.get(position);
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View view = LayoutInflater.from(container.getContext())
                .inflate(R.layout.adapter, container, false);
        container.addView(view);
        bind(mData.get(position), view, position);
        CardView cardView = (CardView) view.findViewById(R.id.cardView);

        if (mBaseElevation == 0) {
            mBaseElevation = cardView.getCardElevation();
        }

        cardView.setMaxCardElevation(mBaseElevation * MAX_ELEVATION_FACTOR);
        mViews.set(position, cardView);
        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
        mViews.set(position, null);

    }

    private void bind(CardItem item, View view, final int pos) {

        LinearLayout ll = (LinearLayout) view.findViewById(R.id.linearlayout);
        LinearLayout bottomLL = view.findViewById(R.id.bottomLL);
        TextView button = view.findViewById(R.id.button);
        TextView textView = view.findViewById(R.id.tv);
        Button applybutton = view.findViewById(R.id.applybutton);
        TextView applycoupon = view.findViewById(R.id.applycoupon);
        final TextView priceTV = view.findViewById(R.id.priceTV);
        payRB = view.findViewById(R.id.pay);
        freetrialRB = view.findViewById(R.id.freetrial);
        freetrialLL = view.findViewById(R.id.freetrialLL);
        payLL = view.findViewById(R.id.payLL);

        freetrialLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (pos == 1) {
                    if (freetrialRB.isChecked()) {
                        freetrialRB.setChecked(false);
                    } else {
                        freetrialRB.setChecked(true);
                    }

                } else if (pos == 2) {
                    if (freetrialRB.isChecked()) {
                        freetrialRB.setChecked(false);
                    } else {
                        freetrialRB.setChecked(true);
                    }

                } else if (pos == 0) {
                    if (freetrialRB.isChecked()) {
                        freetrialRB.setChecked(false);
                    } else {
                        freetrialRB.setChecked(true);
                    }

                }

            }
        });


        payLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (pos == 1) {
                    if (payRB.isChecked()) {
                        payRB.setChecked(false);
                    } else {
                        payRB.setChecked(true);
                    }
                } else if (pos == 2) {
                    if (payRB.isChecked()) {
                        payRB.setChecked(false);
                    } else {
                        payRB.setChecked(true);
                    }
                } else if (pos == 0) {
                    if (payRB.isChecked()) {
                        payRB.setChecked(false);
                    } else {
                        payRB.setChecked(true);
                    }
                }

            }
        });

        applybutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (pos == 1) {
                    planid = "2";
                } else if (pos == 2) {
                    planid = "3";

                } else if (pos == 0) {
                    planid = "1";
                }

                submitPlan();
//                Intent intent=new Intent(context,HomePageActivity.class );
//                intent.putExtra("opendialog","plan");
//                context.startActivity(intent);
//                activity.overridePendingTransition(R.anim.trans_left_in,
//                        R.anim.trans_left_out);
            }
        });

        applycoupon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (pos == 1) {
                    planid = "2";
                } else if (pos == 2) {
                    planid = "3";

                } else if (pos == 0) {
                    planid = "1";
                }
                openDialog(pos);
            }
        });


//        TextView contentTextView = (TextView) view.findViewById(R.id.contentTextView);
//        titleTextView.setText(item.getTitle());
//        contentTextView.setText(item.getText());


        if (pos == 0) {
            ll.setBackground(context.getResources().getDrawable(R.drawable.freeplan));
            button.setText("Free Membership");


            try {
                JSONObject object0 = response.getJSONObject(0);
                //  subscription_plan = object.getString("subscription_plan");
                String likes0 = object0.getString("likes");
                String boost0 = object0.getString("boost");
                String messages0 = object0.getString("messages");
                String price0 = object0.getString("price");

                likeplanTV.setText(likes0 + "\nLikes");
                boostplanTV.setText(boost0 + "\nBoost");
                msgplanTV.setText(messages0 + "\nMessage");

                textView.setText("\u2022 " + likes0 + " likes per month" + "\n"
                        + "\u2022 " + messages0 + " message per day" + "\n"
                        + "\u2022 " + "See unlimited profiles" + "\n"
                        + "\u2022 " + "Filters free members can use" + "\n"
                        + "  Age" + "\n"
                        + "  Height" + "\n" + "  Religion" + "\n" + "  Gender" + "\n" + "  Raised In"
                );

                bottomLL.setVisibility(View.GONE);
                applycoupon.setVisibility(View.GONE);


            } catch (JSONException e) {
                e.printStackTrace();
            }

        } else if (pos == 1) {
            ll.setBackground(context.getResources().getDrawable(R.drawable.goldplan));
            button.setText("Gold Membership");

            try {
                JSONObject object0 = response.getJSONObject(1);
                //  subscription_plan = object.getString("subscription_plan");
                String likes0 = object0.getString("likes");
                String boost0 = object0.getString("boost");
                String messages0 = object0.getString("messages");
                String price0 = object0.getString("price");
                priceTV.setText("$" + price0);

                priceTV1 = priceTV;


                likeplanTV.setText(likes0 + "\nLikes");
                boostplanTV.setText(boost0 + "\nBoost");
                msgplanTV.setText(messages0 + "\nMessage");
                textView.setText("\u2022 " + likes0 + "likes per month" + "\n"
                        + "\u2022 " + messages0 + " message per day" + "\n"
                        + "\u2022 " + "See unlimited profiles" + "\n"
                        + "\u2022 " + "Advance Filters" + "\n"
                        + "\u2022 " + "Reset Dislikes" + "\n"
                        + "\u2022 " + "Chat with 1 match per day" + "\n"


                );


                bottomLL.setVisibility(View.VISIBLE);
                applycoupon.setVisibility(View.VISIBLE);


            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

        if (pos == 2) {
            ll.setBackground(context.getResources().getDrawable(R.drawable.platinumplan));
            button.setText("Platinum Membership");

            try {
                JSONObject object0 = response.getJSONObject(2);
                String likes0 = object0.getString("likes");
                String boost0 = object0.getString("boost");
                String messages0 = object0.getString("messages");
                String price0 = object0.getString("price");
                priceTV.setText("$" + price0);

                priceTV2 = priceTV;

                likeplanTV.setText(likes0 + "\nLikes");
                boostplanTV.setText(boost0 + "\nBoost");
                msgplanTV.setText(messages0 + "\nMessage");

                textView.setText("\u2022 " + likes0 + " likes per month" + "\n"
                        + "\u2022 " + messages0 + " message per day" + "\n"
                        + "\u2022 " + "See unlimited profiles" + "\n"
                        + "\u2022 " + "Advance Filters" + "\n"
                        + "\u2022 " + "Reset Dislikes" + "\n"
                        + "\u2022 " + "Rewind" + "\n"
                        + "\u2022 " + "See mutual friends" + "\n"
                        + "\u2022 " + "Use advance location filter" + "\n"

                        + "\u2022 " + "Chat with match" + "\n");

                bottomLL.setVisibility(View.VISIBLE);
                applycoupon.setVisibility(View.VISIBLE);


            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

    }

    private void openDialog(final int pos) {
        final Dialog dialog = new Dialog(context, R.style.CustomDialog);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.item_couponcode);
        dialog.setCanceledOnTouchOutside(false);

        dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        LinearLayout crossImage = (LinearLayout) dialog.findViewById(R.id.crossImage);
        LinearLayout submit = (LinearLayout) dialog.findViewById(R.id.submit);
        final EditText couponcodeET = dialog.findViewById(R.id.couponcodeET);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (couponcodeET.getText().toString().isEmpty()) {
                    Toast.makeText(context, "Enter valid coupon.", Toast.LENGTH_SHORT).show();
                } else {
                    String promo_codeS = couponcodeET.getText().toString().trim();
                    dialog.dismiss();
                    submitCouponCode(promo_codeS, pos);
                }
            }
        });


        crossImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        dialog.show();

    }

    private void submitCouponCode(final String promo_codeS, final int pos) {

        progressDialog.setMessage("Please Wait..");
        progressDialog.setCancelable(true);
        progressDialog.show();

        StringRequest postRequest = new StringRequest(Request.Method.POST, EndPoints.PROMOCODE,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressDialog.dismiss();
                        Log.e("response", response);
                        try {
//{"status":200,"message":"Success.","data":{"plan_price":"3000","discount price":150,"Total price":2850}}
                            JSONObject obj = new JSONObject(response);
                            String status = obj.getString("status");
                            String message = obj.getString("message");

                            if (status.equalsIgnoreCase("200") && message.equalsIgnoreCase("Success.")) {
                                JSONObject dataObj = obj.getJSONObject("data");
                                if (pos == 1) {
                                    price1 = dataObj.getString("Total price");
                                    priceTV1.setText("$" + price1);
                                } else if (pos == 2) {
                                    price2 = dataObj.getString("Total price");
                                    priceTV2.setText("$" + price2);

                                }


//                                grandtotaldouble=dataObj.getDouble("Total price");
//                                grandtotalTV.setText(String.valueOf(grandtotaldouble));
//
//                                applycoupon.setText("Coupon applied successfully.");

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
                Map<String, String> ob = new HashMap<>();
                ob.put("user_id", sessionManager.getUserId().get(SessionManager.KEY_USERID));
                ob.put("plan_id", planid);

                Log.e("params", ob.toString());
                return ob;
            }
        };
        int socketTimeout = 30000;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        postRequest.setRetryPolicy(policy);
        requestQueue.add(postRequest);
    }


    private void submitPlan() {
        progressDialog.setMessage("Please Wait..");
        progressDialog.setCancelable(true);
        progressDialog.show();

        StringRequest postRequest = new StringRequest(Request.Method.POST, EndPoints.PLANSUBMIT,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("response", response);
                        try {

                            progressDialog.dismiss();
                            JSONObject obj = new JSONObject(response);
                            int status = obj.getInt("status");
                            String message = obj.getString("message");

                            if (status == 200 && message.equalsIgnoreCase("success")) {
                                Intent intent = new Intent(context, HomePageActivity.class);
                                intent.putExtra("opendialog", "plan");
                                context.startActivity(intent);
                                sessionManager.setLoginSession();

                                activity.overridePendingTransition(R.anim.trans_left_in,
                                        R.anim.trans_left_out);

                            }
                            else
                            {
                                Intent intent = new Intent(context, HomePageActivity.class);
                                intent.putExtra("opendialog", "plan");
                                context.startActivity(intent);
                                sessionManager.setLoginSession();

                                activity.overridePendingTransition(R.anim.trans_left_in,
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
//                params.put("user_id", sessionManager.getUserId().get(SessionManager.KEY_USERID));
                params.put("user_id", sessionManager.getUserId().get(SessionManager.KEY_USERID));
                params.put("plan_id", planid);

                Log.e("paramssssssssss", params.toString());
                return params;
            }
        };
        int socketTimeout = 30000;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        postRequest.setRetryPolicy(policy);
        requestQueue.add(postRequest);
    }

}
