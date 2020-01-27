package com.dbvertex.dilsayproject;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.databinding.DataBindingUtil;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
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
import com.dbvertex.dilsayproject.Adapter.BannerPagerAdapter;
import com.dbvertex.dilsayproject.Chatting.ChatInboxActivity;
import com.dbvertex.dilsayproject.UserAuth.HomePageActivity;
import com.dbvertex.dilsayproject.UserAuth.OtpActivity;
import com.dbvertex.dilsayproject.UserAuth.UploadPicsActivity;
import com.dbvertex.dilsayproject.databinding.ActivityProfileDetailBinding;
import com.dbvertex.dilsayproject.databinding.ItemBlockBinding;
import com.dbvertex.dilsayproject.session.SessionManager;
import com.google.firebase.auth.FirebaseAuth;
import com.viewpagerindicator.CirclePageIndicator;
import com.zhy.view.flowlayout.FlowLayout;
import com.zhy.view.flowlayout.TagAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProfileDetailActivity extends AppCompatActivity {
    ActivityProfileDetailBinding binding;
    String mobileno = "", countrycode = "";
    ProgressDialog progressDialog;
    RequestQueue queue;
    SessionManager sessionManager;
    boolean isConnected;
    LayoutInflater mInflater;
    String userid, reason,description,name;
    Intent intent;
    Dialog dialog;
    private String verificationId;
    private FirebaseAuth mAuth;
    private ArrayList<String> photoBMList;

    private void bindView(ArrayList<String> list) {
        binding.autoPager.startAutoScroll();
        binding.autoPager.setInterval(3000);
        binding.autoPager.setCycle(true);
        binding.autoPager.setStopScrollWhenTouch(true);
        BannerPagerAdapter adp = new BannerPagerAdapter(this, list);
        binding.autoPager.setAdapter(adp);
        binding.indicator.setViewPager(binding.autoPager, 1);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_profile_detail);
        mInflater = LayoutInflater.from(ProfileDetailActivity.this);
        photoBMList = new ArrayList<String>();

        progressDialog = new ProgressDialog(ProfileDetailActivity.this, R.style.CustomDialog);
        queue = Volley.newRequestQueue(ProfileDetailActivity.this);
        sessionManager = new SessionManager(this);
        intent = getIntent();
        userid = sessionManager.getProfileDetailUserId().get(SessionManager.KEY_DETAILPAGE_USERID);

        binding.blockLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDialog();
            }
        });

        binding.likeLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.likeIV.setBackground(getResources().getDrawable(R.drawable.solidgradientcircle));
                binding.likeIV.setImageDrawable(getResources().getDrawable(R.drawable.like_home));

                binding.dislikeIV.setBackground(getResources().getDrawable(R.drawable.solidwhitecircle));
                binding.dislikeIV.setImageDrawable(getResources().getDrawable(R.drawable.activity_dislike_feel));
                binding.thinklaterIV.setBackground(getResources().getDrawable(R.drawable.solidwhitecircle));
                binding.thinklaterIV.setImageDrawable(getResources().getDrawable(R.drawable.activity_no_feel_thinklater));

                likeProfile();
            }
        });


        binding.dislikeLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.dislikeIV.setBackground(getResources().getDrawable(R.drawable.solidgradientcircle));
                binding.dislikeIV.setImageDrawable(getResources().getDrawable(R.drawable.dislike_home));

                binding.likeIV.setBackground(getResources().getDrawable(R.drawable.solidwhitecircle));
                binding.likeIV.setImageDrawable(getResources().getDrawable(R.drawable.activity_like_feel));
                binding.thinklaterIV.setBackground(getResources().getDrawable(R.drawable.solidwhitecircle));
                binding.thinklaterIV.setImageDrawable(getResources().getDrawable(R.drawable.activity_no_feel_thinklater));

                dislikeProfile();
            }
        });


        binding.thinklater.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.thinklaterIV.setBackground(getResources().getDrawable(R.drawable.solidgradientcircle));
                binding.thinklaterIV.setImageDrawable(getResources().getDrawable(R.drawable.think_later));

                binding.dislikeIV.setBackground(getResources().getDrawable(R.drawable.solidwhitecircle));
                binding.dislikeIV.setImageDrawable(getResources().getDrawable(R.drawable.activity_dislike_feel));
                binding.likeIV.setBackground(getResources().getDrawable(R.drawable.solidwhitecircle));
                binding.likeIV.setImageDrawable(getResources().getDrawable(R.drawable.activity_like_feel));

                thinklaterProfile();
            }
        });


        binding.backIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfileDetailActivity.this, HomePageActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

                startActivity(intent);

                overridePendingTransition(R.anim.trans_left_in,
                        R.anim.trans_left_out);
            }
        });
        binding.messageLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ProfileDetailActivity.this, ChatInboxActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra("userid",userid);
                intent.putExtra("username",name);
                intent.putExtra("from","detail");

                startActivity(intent);

                overridePendingTransition(R.anim.trans_left_in,
                        R.anim.trans_left_out);
            }
        });

        loadDetails();
    }

    private void openDialog() {
        dialog = new Dialog(ProfileDetailActivity.this, R.style.CustomDialog);
        final ItemBlockBinding binding = DataBindingUtil.inflate(LayoutInflater.from(dialog.getContext()), R.layout.item_block,
                null, false);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(binding.getRoot());
        dialog.setCanceledOnTouchOutside(false);
        dialog.getWindow().setLayout(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));



        Typeface customFont = Typeface.createFromAsset(getAssets(),"fonts/BodoniFLF-Roman.ttf");
        binding.unmatchRB.setTypeface(customFont);
        binding.inapprofileRB.setTypeface(customFont);
        binding.inapmsgRB.setTypeface(customFont);
        binding.harasserRB.setTypeface(customFont);
        binding.stolenpicRB.setTypeface(customFont);
        binding.spamRB.setTypeface(customFont);
        binding.unresponsiveRB.setTypeface(customFont);
        binding.notinterestedRB.setTypeface(customFont);

        binding.otherRB.setTypeface(customFont);

        binding.sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (binding.unmatchRB.isChecked())
                {
                    reason = "unmatch";

                }
                else if (binding.inapprofileRB.isChecked())
                {
               reason = "inappropriate profile";

                }
                else if (binding.inapmsgRB.isChecked())
                {
                    reason = "inappropriate message";

                }
                else if (binding.harasserRB.isChecked())
                {
                    reason = "harasser";
                }
                else if (binding.stolenpicRB.isChecked())
                {
                    reason = "stolen pic";
                }
                else if (binding.spamRB.isChecked())
                {
                    reason = "spam";
                }
                else if (binding.unresponsiveRB.isChecked())
                {
                    reason = "unresponsive";
                }
                else if (binding.notinterestedRB.isChecked())
                {
                    reason = "not interested";
                }
                else if (binding.otherRB.isChecked())
                {
                    reason = "other";
                }


                if (binding.descripationET.getText().toString().isEmpty())
                {
                    Toast.makeText(ProfileDetailActivity.this, "Enter description", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    submit(binding.descripationET.getText().toString());
                }




            }
        });




        dialog.show();
    }

    private void submit(final String description)
    {
        progressDialog.setMessage("Please Wait..");
        progressDialog.setCancelable(true);
        progressDialog.show();

        StringRequest postRequest = new StringRequest(Request.Method.POST, EndPoints.REPORTABUSE,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("response", response);
                        try {

                            progressDialog.dismiss();
                            JSONObject obj = new JSONObject(response);
                            int status = obj.getInt("status");
                            String message = obj.getString("message");
                            if (status == 200 && message.equalsIgnoreCase("success"))
                            {
                                dialog.dismiss();
                                final Dialog dialog = new Dialog(ProfileDetailActivity.this, R.style.CustomDialog);
                                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                                dialog.setContentView(R.layout.item_camerarentalsuccess);
                                dialog.setCanceledOnTouchOutside(false);
                                dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT,
                                        WindowManager.LayoutParams.WRAP_CONTENT);
                                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

                                LinearLayout submit = dialog.findViewById(R.id.submit);
                                ImageView cross = dialog.findViewById(R.id.cross);
                                TextView msgTV = dialog.findViewById(R.id.msgTV);
                                LinearLayout linearlayout=dialog.findViewById(R.id.linearlayout);

                                linearlayout.setVisibility(View.GONE);

                                msgTV.setText("Thank you for review. We'll take appropriate action.");
                                dialog.show();

                                submit.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        dialog.dismiss();

                                        Intent intent = new Intent(ProfileDetailActivity.this, HomePageActivity.class);
                                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(intent);
                                        overridePendingTransition(R.anim.trans_left_in,
                                                R.anim.trans_left_out);

                                    }
                                });

                                cross.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        dialog.dismiss();
                                        Intent intent = new Intent(ProfileDetailActivity.this, HomePageActivity.class);
                                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(intent);
                                        overridePendingTransition(R.anim.trans_left_in,
                                                R.anim.trans_left_out);
                                    }
                                });


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
                params.put("reported_user_id", userid);
                params.put("user_id", sessionManager.getUserId().get(SessionManager.KEY_USERID));
                params.put("reason", reason);
                params.put("description", description);
                Log.e("params", params.toString());
                return params;
            }
        };
        int socketTimeout = 30000;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        postRequest.setRetryPolicy(policy);
        queue.add(postRequest);
    }

    private void loadDetails() {
        progressDialog.setMessage("Please Wait..");
        progressDialog.setCancelable(true);
        progressDialog.show();

        StringRequest postRequest = new StringRequest(Request.Method.POST, EndPoints.PROFILEDETAIL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("response", response);
                        try {

                            progressDialog.dismiss();
                            JSONObject obj = new JSONObject(response);
                            int status = obj.getInt("status");
                            String message = obj.getString("message");
// {"status":200,"message":"success","data":{"mobile_no":"9522335636","country_code":"+91"}}
                            if (status == 1 && message.equalsIgnoreCase("success")) {

                                binding.ll.setVisibility(View.VISIBLE);
                                JSONArray data = obj.getJSONArray("data");
                                JSONObject jsonObject = data.getJSONObject(0);
                                name = jsonObject.getString("name");
                                String age = jsonObject.getString("age");
                                String gender = jsonObject.getString("gender");
                                String height = jsonObject.getString("height");
                                String raised_in = jsonObject.getString("raised_in");
                                String dob = jsonObject.getString("dob");
                                String education = jsonObject.getString("education");
                                String food_prefrence = jsonObject.getString("food_prefrence");
                                String smoke = jsonObject.getString("smoke");
                                String drinking = jsonObject.getString("drinking");
                                String religion = jsonObject.getString("religion");
                                String community = jsonObject.getString("community");
                                String career = jsonObject.getString("career");
                                String description = jsonObject.getString("description");
                                String intrests = jsonObject.getString("intrests");

                                binding.usernameTV.setText(name);
                                binding.ageTV.setText(age);
                                binding.heightTV.setText(height);
                                binding.countryTV.setText(" ," + raised_in);
                                binding.educationTV.setText(education);
                                binding.dietTV.setText(food_prefrence);
                                binding.smokingTV.setText(smoke);
                                binding.drinkingTV.setText(drinking);
                                binding.religionTV.setText(religion);
                                binding.communityTV.setText(community);
                                binding.careerTV.setText(career);
                                binding.descriptionTV.setText(description);
                                List<String> items = Arrays.asList(intrests.split("\\s*,\\s*"));

                                Log.e("intrests", items + "");

                                binding.idFlowlayout.setAdapter(new TagAdapter<String>(items) {
                                    @Override
                                    public View getView(FlowLayout parent, int position, String s) {
                                        TextView tv = (TextView) mInflater.inflate(R.layout.tv,
                                                binding.idFlowlayout, false);
                                        tv.setBackground(getResources().getDrawable(R.drawable.gradientbutton));
                                        tv.setTextColor(getResources().getColor(R.color.white));

                                        tv.setText("#" + s + " ");
                                        return tv;
                                    }
                                });

                                JSONArray fb_image = jsonObject.getJSONArray("fb_image");
                                String fbpic = fb_image.getString(0);
                                photoBMList.add(fbpic);

                                JSONArray profile_image = jsonObject.getJSONArray("profile_image");
                                for (int i = 0; i < profile_image.length(); i++) {
                                    String path = profile_image.getString(i);
                                    photoBMList.add(path);
                                }
                                bindView(photoBMList);

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
                params.put("user_id", userid);

                Log.e("params", params.toString());
                return params;
            }
        };
        int socketTimeout = 30000;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        postRequest.setRetryPolicy(policy);
        queue.add(postRequest);
    }


    private void likeProfile() {
        StringRequest postRequest = new StringRequest(Request.Method.POST, EndPoints.LIKE_PROFILE,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("response", response);
                        try {


                            JSONObject object = new JSONObject(response);
// {"status":200,"message":"success","data":{"user_id":"243","liked_id":"236","created":"2019-08-06 21:57:24"}}
                            String status = object.getString("status");
                            String message = object.getString("message");
                            if (status.equalsIgnoreCase("200") && message.equalsIgnoreCase("success")) {
                                sessionManager.setRewindId(userid);
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
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("user_id", sessionManager.getUserId().get(SessionManager.KEY_USERID));
                params.put("liked_id", userid);

                Log.e("params", params.toString());
                return params;
            }
        };
        int socketTimeout = 30000;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        postRequest.setRetryPolicy(policy);
        queue.add(postRequest);
    }


    private void dislikeProfile() {
        StringRequest postRequest = new StringRequest(Request.Method.POST, EndPoints.DISLIKE_PROFILE,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("response", response);
                        try {


                            JSONObject object = new JSONObject(response);

                            String status = object.getString("status");
                            String message = object.getString("message");
                            if (status.equalsIgnoreCase("200") && message.equalsIgnoreCase("success")) {
                                sessionManager.setRewindId(userid);
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
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("user_id", sessionManager.getUserId().get(SessionManager.KEY_USERID));
                params.put("disliked_id", userid);

                Log.e("params", params.toString());
                return params;
            }
        };
        int socketTimeout = 30000;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        postRequest.setRetryPolicy(policy);
        queue.add(postRequest);
    }


    private void thinklaterProfile() {
        StringRequest postRequest = new StringRequest(Request.Method.POST, EndPoints.THINKLATER_PROFILE,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("response", response);
                        try {


                            JSONObject object = new JSONObject(response);

                            String status = object.getString("status");
                            String message = object.getString("message");
                            if (status.equalsIgnoreCase("200") && message.equalsIgnoreCase("success"))
                            {

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
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("user_id", sessionManager.getUserId().get(SessionManager.KEY_USERID));
                params.put("think_later_id", userid);

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
        Intent intent = new Intent(ProfileDetailActivity.this, HomePageActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        overridePendingTransition(R.anim.trans_left_in,
                R.anim.trans_left_out);

    }

}
