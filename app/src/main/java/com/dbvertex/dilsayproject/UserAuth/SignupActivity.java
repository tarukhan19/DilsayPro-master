package com.dbvertex.dilsayproject.UserAuth;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.databinding.DataBindingUtil;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
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
import com.dbvertex.dilsayproject.Fcm.Config;
import com.dbvertex.dilsayproject.HideKeyboard;
import com.dbvertex.dilsayproject.Network.ConnectivityReceiver;
import com.dbvertex.dilsayproject.Network.MyApplication;
import com.dbvertex.dilsayproject.R;
import com.dbvertex.dilsayproject.databinding.ActivitySignupBinding;
import com.dbvertex.dilsayproject.session.SessionManager;
import com.facebook.AccessToken;
import com.facebook.appevents.AppEventsLogger;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class SignupActivity extends AppCompatActivity implements View.OnClickListener,
        ConnectivityReceiver.ConnectivityReceiverListener {
    ActivitySignupBinding binding;
    String name, emailid, mobileno, gender, dateofbirth, countrycode, fb_id;
    int mYear, mMonth, mDay;
    ProgressDialog progressDialog;
    SessionManager sessionManager;
    RequestQueue queue;
    int diff;
    boolean isConnected;
    String regId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_signup);
        sessionManager = new SessionManager(this);
        progressDialog = new ProgressDialog(SignupActivity.this, R.style.CustomDialog);
        queue = Volley.newRequestQueue(SignupActivity.this);

        Toolbar toolbar_main = findViewById(R.id.toolbar_main);
        TextView titleTV = (TextView) toolbar_main.findViewById(R.id.titleTV);
        LinearLayout back_LL = toolbar_main.findViewById(R.id.back_LL);
        titleTV.setText("Sign Up");

        back_LL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                overridePendingTransition(R.anim.trans_left_in,
                        R.anim.trans_left_out);
            }
        });

        name = sessionManager.getFacebookData().get(SessionManager.KEY_USERNAME);
        emailid = sessionManager.getFacebookData().get(SessionManager.KEY_EMAIL);
        fb_id = sessionManager.getFacebookData().get(SessionManager.KEY_FACEBOOKID);

        if (!sessionManager.getMobileNumber().get(SessionManager.KEY_MOBILENUMBER).isEmpty()) {
            binding.mobileNoET.setText(sessionManager.getMobileNumber().get(SessionManager.KEY_MOBILENUMBER));
            binding.dobTV.setText(sessionManager.getDetails().get(SessionManager.KEY_DATEOFBIRTH));
            if (sessionManager.getDetails().get(SessionManager.KEY_GENDER).equalsIgnoreCase("F")) {
                binding.female.setChecked(true);
                binding.male.setChecked(false);
            } else {
                binding.female.setChecked(false);
                binding.male.setChecked(true);
            }
        }
        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener( SignupActivity.this,
                new OnSuccessListener<InstanceIdResult>() {
                    @Override
                    public void onSuccess(InstanceIdResult instanceIdResult) {
                        regId = instanceIdResult.getToken();
                        Log.e("Token",regId);
                    }
                });

        binding.nameTV.setText(name);
        binding.emailTV.setText(emailid);

//        Glide.with(getApplicationContext()).load(profilepic)
//                .thumbnail(0.5f)
//                .diskCacheStrategy(DiskCacheStrategy.ALL).placeholder(R.drawable.defaultpic)
//                .into(binding.profileImage);

        binding.continueBtn.setOnClickListener(this);
        binding.dobTV.setOnClickListener(this);

        binding.mobileNoET.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (binding.mobileNoET.getText().toString().length() < 8 || binding.mobileNoET.getText().length() > 15) {
                    binding.mobileTIL.setError("Enter valid Mobile number");
                } else {
                    binding.mobileTIL.setErrorEnabled(false);
                }

                // TODO Auto-generated method stub
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                // TODO Auto-generated method stub
            }

            @Override
            public void afterTextChanged(Editable s) {

                // TODO Auto-generated method stub

            }
        });

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.continueBtn:

                boolean isinvalidate = true;


                mobileno = binding.mobileNoET.getText().toString();
                dateofbirth = binding.dobTV.getText().toString();
                countrycode = binding.ccp.getSelectedCountryCodeWithPlus();

                if (binding.male.isChecked()) {
                    gender = "M";
                } else {
                    gender = "F";
                }

                if (mobileno.length() < 8 || mobileno.length() > 15) {
                    binding.mobileTIL.setError("Enter valid Mobile number");
                    binding.mobileNoET.requestFocus();
                    isinvalidate = false;
                } else {
                    binding.mobileTIL.setErrorEnabled(false);
                    binding.mobileTIL.setError(null);

                }

                if (diff == 0) {
                    binding.dobTIL.setError("Select valid date of birth");
                    isinvalidate = false;

                } else {
                    binding.dobTIL.setErrorEnabled(false);
                    binding.dobTIL.setError(null);

                }

                if (diff < 18  || diff>60) {
                    binding.dobTIL.setError("Min and Max age should be\n18 and 60 years.");
                    isinvalidate = false;

                } else {
                    binding.dobTIL.setErrorEnabled(false);
                    binding.dobTIL.setError(null);

                }

                if (gender.isEmpty()) {
                    binding.genderTIL.setError("Select gender");
                    isinvalidate = false;

                } else {
                    binding.genderTIL.setErrorEnabled(false);
                    binding.genderTIL.setError(null);


                }


                if (isinvalidate) {

                    HideKeyboard.hideKeyboard(SignupActivity.this);
                    if (isConnected)
                        registration();
                    else
                        showSnack(isConnected);

                }


                break;

            case R.id.dobTV:
                showDateTimePicker();
                break;


        }

    }


    private void showDateTimePicker() {
        Calendar mcurrentDate = Calendar.getInstance();
        mYear = mcurrentDate.get(Calendar.YEAR);
        mMonth = mcurrentDate.get(Calendar.MONTH);
        mDay = mcurrentDate.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog mDatePicker = new DatePickerDialog(SignupActivity.this, new DatePickerDialog.OnDateSetListener() {
            public void onDateSet(DatePicker datepicker, int selectedyear, int selectedmonth, int selectedday) {
                Calendar myCalendar = Calendar.getInstance();
                myCalendar.set(Calendar.YEAR, selectedyear);
                myCalendar.set(Calendar.MONTH, selectedmonth);
                myCalendar.set(Calendar.DAY_OF_MONTH, selectedday);
                String myFormat = "yyyy-MM-dd"; //Change as you need
                SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.ENGLISH);
                dateofbirth = sdf.format(myCalendar.getTime());
                binding.dobTV.setText(sdf.format(myCalendar.getTime()));
                binding.dobTIL.setErrorEnabled(false);


                SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");
                Date dob = null;
                try {
                    dob = sf.parse(dateofbirth);
                    Date dt = new Date();
                    diff = getDiffYears(dob, dt);
                    Log.e("diff", diff + "");

                } catch (ParseException e) {
                    e.printStackTrace();
                }

                mDay = selectedday;
                mMonth = selectedmonth;
                mYear = selectedyear;
            }
        }, mYear, mMonth, mDay);
        mDatePicker.getDatePicker().setMaxDate(System.currentTimeMillis());

        mDatePicker.show();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        overridePendingTransition(R.anim.trans_left_in,
                R.anim.trans_left_out);
    }


    private int getDiffYears(Date dob, Date dt) {
        Calendar a = getCalendar(dob);
        Calendar b = getCalendar(dt);
        int diff = b.get(Calendar.YEAR) - a.get(Calendar.YEAR);
        if (a.get(Calendar.MONTH) > b.get(Calendar.MONTH) ||
                (a.get(Calendar.MONTH) == b.get(Calendar.MONTH) && a.get(Calendar.DATE) > b.get(Calendar.DATE))) {
            diff--;
        }
        return diff;

    }

    public Calendar getCalendar(Date date) {
        Calendar cal = Calendar.getInstance(Locale.US);
        cal.setTime(date);
        return cal;
    }

    private void registration() {
        progressDialog.setMessage("Please Wait..");
        progressDialog.setCancelable(true);
        progressDialog.show();

        StringRequest postRequest = new StringRequest(Request.Method.POST, EndPoints.SIGNUP,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("response", response);
                        try {

                            progressDialog.dismiss();
                            JSONObject obj = new JSONObject(response);
                            int status = obj.getInt("status");
                            String message = obj.getString("message");
//{"status":200,"message":"success","data":{"id":11,"name":"Tarannum Khan","email":"tarukhan19@gmail.com",
// "mobile_no":"9522335636","dob":"1990-04-26","gender":"F","facebook_id":"2360644337504490"}}

                            if (status == 200 && message.equalsIgnoreCase("success")) {
                                JSONObject dataObj = obj.getJSONObject("data");
                                String userid = dataObj.getString("id");
                                sessionManager.setUserId(userid);
                                sessionManager.setDetails(dateofbirth, gender);
                                sessionManager.setMobileNumber(mobileno,countrycode);

                                Intent intent = new Intent(SignupActivity.this, OtpActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                                overridePendingTransition(R.anim.trans_left_in,
                                        R.anim.trans_left_out);
                            }

                            else if (status==1 && message.equalsIgnoreCase("Mobile Number All Ready Exist!"))
                            {
                                Toast.makeText(SignupActivity.this, "Mobile number already exists.", Toast.LENGTH_SHORT).show();
                            }
                            else {
                                Intent intent = new Intent(SignupActivity.this, HomePageActivity.class);
                                startActivity(intent);
                                overridePendingTransition(R.anim.trans_left_in,
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
                params.put("fb_id", fb_id);
                params.put("name", name);
                params.put("email", emailid);
                params.put("mobile_no", mobileno);
                params.put("dob", dateofbirth);
                params.put("gender", gender);
                params.put("country_code", countrycode);
                params.put("device_type", "android");
                params.put("device_id", regId);
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
    public void onStart() {
        super.onStart();

        isConnected = ConnectivityReceiver.isConnected();
        Log.e("onStart", isConnected + "");
        if (!isConnected) {
            showSnack(isConnected);
        }


    }


    protected void onResume() {
        super.onResume();
        // Logs 'install' and 'app activate' App Events.
        MyApplication.getInstance().setConnectivityListener(this);

    }

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        this.isConnected = isConnected;
        Log.e("onNetworkConnectionconn", isConnected + "");

        showSnack(isConnected);

    }

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
}
