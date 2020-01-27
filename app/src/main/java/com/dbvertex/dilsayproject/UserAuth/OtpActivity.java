package com.dbvertex.dilsayproject.UserAuth;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.databinding.DataBindingUtil;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
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
import com.dbvertex.dilsayproject.EndPoints;
import com.dbvertex.dilsayproject.HideKeyboard;
import com.dbvertex.dilsayproject.Network.ConnectivityReceiver;
import com.dbvertex.dilsayproject.Network.MyApplication;
import com.dbvertex.dilsayproject.R;
import com.dbvertex.dilsayproject.databinding.ActivityOtpBinding;
import com.dbvertex.dilsayproject.session.SessionManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskExecutors;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.hbb20.CountryCodePicker;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class OtpActivity extends AppCompatActivity implements
        ConnectivityReceiver.ConnectivityReceiverListener {

    ActivityOtpBinding binding;
    String mobileno = "", countrycode = "";
    ProgressDialog progressDialog;
    RequestQueue queue;
    SessionManager sessionManager;
    boolean isConnected;

    String fb_id, userid;

    private String verificationId;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_otp);
        progressDialog = new ProgressDialog(OtpActivity.this, R.style.CustomDialog);
        queue = Volley.newRequestQueue(OtpActivity.this);
        sessionManager = new SessionManager(this);


        Toolbar toolbar_main = findViewById(R.id.toolbar_main);
        TextView titleTV = (TextView) toolbar_main.findViewById(R.id.titleTV);
        LinearLayout back_LL = toolbar_main.findViewById(R.id.back_LL);
        titleTV.setText("OTP");

        if (!sessionManager.getMobileNumber().get(SessionManager.KEY_MOBILENUMBER).isEmpty()) {
            mobileno = sessionManager.getMobileNumber().get(SessionManager.KEY_MOBILENUMBER);
            countrycode = sessionManager.getMobileNumber().get(SessionManager.KEY_COUNTRYCODE);
            fb_id = sessionManager.getFacebookData().get(SessionManager.KEY_FACEBOOKID);
            userid = sessionManager.getUserId().get(SessionManager.KEY_USERID);
        } else {

        }

        binding.countrycode.setText(countrycode+" -");

        binding.mobileno.setText(mobileno);
        mAuth = FirebaseAuth.getInstance();

        binding.editnumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openUpdateDialog();
            }
        });

        binding.continueBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                HideKeyboard.hideKeyboard(OtpActivity.this);
                String otp = binding.otpET1.getText().toString().trim() +
                        binding.otpET2.getText().toString().trim() +
                        binding.otpET3.getText().toString().trim() +
                        binding.otpET4.getText().toString().trim() +
                        binding.otpET5.getText().toString().trim() +
                        binding.otpET6.getText().toString().trim();

                if (otp.isEmpty()) {
                    Toast.makeText(OtpActivity.this, "Please enter OTP", Toast.LENGTH_SHORT).show();
                } else {
                    verifyCode(otp);

                }


            }
        });
        binding.resend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.otpET1.setText("");
                binding.otpET2.setText("");
                binding.otpET3.setText("");
                binding.otpET4.setText("");
                binding.otpET5.setText("");
                binding.otpET6.setText("");
                binding.otpET1.requestFocus();
                Toast.makeText(OtpActivity.this, "OTP sent on given number", Toast.LENGTH_SHORT).show();

                sendVerificationCode(countrycode + mobileno);
            }
        });

        sendVerificationCode(countrycode + mobileno);
        back_LL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(OtpActivity.this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                overridePendingTransition(R.anim.trans_left_in,
                        R.anim.trans_left_out);
            }
        });


        binding.otpET1.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (binding.otpET1.getText().toString().length() == 1) {
                    binding.otpLL1.setBackground(getResources().getDrawable(R.drawable.solidgradientcircle));
                } else {
                    binding.otpLL1.setBackground(getResources().getDrawable(R.drawable.solidwhitecircle));
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

                if (binding.otpET1.getText().toString().length() == 1) {
                    binding.otpET2.requestFocus();
                }

                if (binding.otpET1.getText().toString().length() > 1) {
                    String str = binding.otpET1.getText().toString();
                    //  //Log.e("OPT1>>", str);
                    // //Log.e("OPT1>>", str.substring(1, 2));
                    binding.otpET1.setText(str.substring(1, 2));
                }
            }
        });


        binding.otpET2.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (binding.otpET2.getText().toString().length() == 1) {
                    binding.otpLL2.setBackground(getResources().getDrawable(R.drawable.solidgradientcircle));
                } else {
                    binding.otpLL2.setBackground(getResources().getDrawable(R.drawable.solidwhitecircle));
                }
                // TODO Auto-generated method stub
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                // TODO Auto-generated method stub
            }

            @Override
            public void afterTextChanged(Editable s) {

                if (binding.otpET2.getText().toString().length() == 0) {
                    binding.otpET1.requestFocus();
                }

                if (binding.otpET2.getText().toString().length() == 1) {
                    binding.otpET3.requestFocus();
                }

                if (binding.otpET2.getText().toString().length() > 1) {
                    String str = binding.otpET2.getText().toString();
                    //  //Log.e("OPT2>>", str);
                    //  //Log.e("OPT2>>", str.substring(1, 2));
                    binding.otpET2.setText(str.substring(1, 2));
                }
            }
        });


        binding.otpET3.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (binding.otpET3.getText().toString().length() == 1) {
                    binding.otpLL3.setBackground(getResources().getDrawable(R.drawable.solidgradientcircle));
                } else {
                    binding.otpLL3.setBackground(getResources().getDrawable(R.drawable.solidwhitecircle));
                }
                // TODO Auto-generated method stub
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                // TODO Auto-generated method stub
            }

            @Override
            public void afterTextChanged(Editable s) {

                if (binding.otpET3.getText().toString().length() == 0) {
                    binding.otpET2.requestFocus();
                }

                if (binding.otpET3.getText().toString().length() == 1) {
                    binding.otpET4.requestFocus();
                }

                if (binding.otpET3.getText().toString().length() > 1) {
                    String str = binding.otpET3.getText().toString();
                    //  //Log.e("OPT3>>", str);
                    // //Log.e("OPT3>>", str.substring(1, 2));
                    binding.otpET3.setText(str.substring(1, 2));
                }
            }
        });

        binding.otpET4.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (binding.otpET4.getText().toString().length() == 1) {
                    binding.otpLL4.setBackground(getResources().getDrawable(R.drawable.solidgradientcircle));
                } else {
                    binding.otpLL4.setBackground(getResources().getDrawable(R.drawable.solidwhitecircle));
                }
                // TODO Auto-generated method stub
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                // TODO Auto-generated method stub
            }

            @Override
            public void afterTextChanged(Editable s) {

                if (binding.otpET4.getText().toString().length() == 0) {
                    binding.otpET3.requestFocus();
                }

                if (binding.otpET4.getText().toString().length() == 1) {
                    binding.otpET5.requestFocus();
                }

                if (binding.otpET4.getText().toString().length() > 1) {
                    String str = binding.otpET4.getText().toString();
                    binding.otpET4.setText(str.substring(1, 2));
                }
            }
        });


        binding.otpET5.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (binding.otpET5.getText().toString().length() == 1) {
                    binding.otpLL5.setBackground(getResources().getDrawable(R.drawable.solidgradientcircle));
                } else {
                    binding.otpLL5.setBackground(getResources().getDrawable(R.drawable.solidwhitecircle));
                }
                // TODO Auto-generated method stub
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                // TODO Auto-generated method stub
            }

            @Override
            public void afterTextChanged(Editable s) {

                if (binding.otpET5.getText().toString().length() == 0) {
                    binding.otpET4.requestFocus();
                }

                if (binding.otpET5.getText().toString().length() == 1) {
                    binding.otpET6.requestFocus();
                }

                if (binding.otpET5.getText().toString().length() > 1) {
                    String str = binding.otpET5.getText().toString();
                    //  //Log.e("OPT3>>", str);
                    // //Log.e("OPT3>>", str.substring(1, 2));
                    binding.otpET5.setText(str.substring(1, 2));
                }
            }
        });


        binding.otpET6.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (binding.otpET6.getText().toString().length() == 1) {
                    binding.otpLL6.setBackground(getResources().getDrawable(R.drawable.solidgradientcircle));
                } else {
                    binding.otpLL6.setBackground(getResources().getDrawable(R.drawable.solidwhitecircle));
                }
                // TODO Auto-generated method stub
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                // TODO Auto-generated method stub
            }

            @Override
            public void afterTextChanged(Editable s) {

                if (binding.otpET6.getText().toString().length() == 0) {
                    binding.otpET5.requestFocus();
                }

                if (binding.otpET6.getText().toString().length() > 1) {
                    String str = binding.otpET6.getText().toString();
                    //  //Log.e("OPT4>>", str);
                    // //Log.e("OPT4>>", str.substring(1, 2));
                    binding.otpET6.setText(str.substring(1, 2));
                }
            }
        });
    }

    private void openUpdateDialog() {

        final Dialog dialog = new Dialog(OtpActivity.this, R.style.CustomDialog);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.item_updatemobileno);
        dialog.setCanceledOnTouchOutside(false);
        dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        Button continueBtn = dialog.findViewById(R.id.continueBtn);
        final EditText mobileNoET = dialog.findViewById(R.id.mobileNoET);
        final CountryCodePicker countryCodePicker = (CountryCodePicker) dialog.findViewById(R.id.ccp);

        TextView titleTV = dialog.findViewById(R.id.titleTV);
        LinearLayout back_LL = dialog.findViewById(R.id.back_LL);
        titleTV.setText("Update Mobile No.");
        mobileNoET.setText(mobileno);


        back_LL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        continueBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (mobileNoET.getText().toString().length() < 8 || mobileNoET.getText().length() > 15)
                {
                    Toast.makeText(OtpActivity.this, "Enter valid Mobile number", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    mobileno = mobileNoET.getText().toString().trim();
                    countrycode = countryCodePicker.getSelectedCountryCodeWithPlus();
                    dialog.dismiss();
                    binding.otpET1.setText("");
                    binding.otpET2.setText("");
                    binding.otpET3.setText("");
                    binding.otpET4.setText("");
                    binding.otpET5.setText("");
                    binding.otpET6.setText("");
                    binding.otpET1.requestFocus();
                    updateMobileNo();
                }


            }
        });

        dialog.show();
    }

    private void updateMobileNo() {
        progressDialog.setMessage("Please Wait..");
        progressDialog.setCancelable(true);
        progressDialog.show();

        StringRequest postRequest = new StringRequest(Request.Method.POST, EndPoints.UPDATE_MOBILE_NO,
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
                            if (status == 200 && message.equalsIgnoreCase("success")) {

                                JSONObject data = obj.getJSONObject("data");
                                mobileno = data.getString("mobile_no");
                                countrycode = data.getString("country_code");
                                binding.mobileno.setText(mobileno);
                                binding.countrycode.setText(countrycode+" -");

                                sessionManager.setMobileNumber(mobileno, countrycode);

                                sendVerificationCode(countrycode + mobileno);


                            }

                            else if (status==1 && message.equalsIgnoreCase("Mobile Number All Ready Exist!"))
                            {
                                Toast.makeText(OtpActivity.this, "Mobile number already exists.", Toast.LENGTH_SHORT).show();
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
                params.put("mobile_no", mobileno);
                params.put("country_code", countrycode);

                Log.e("params", params.toString());
                return params;
            }
        };
        int socketTimeout = 30000;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        postRequest.setRetryPolicy(policy);
        queue.add(postRequest);
    }


    private void sendVerificationCode(String number) {
        Log.e("number", number);
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                number,
                120,
                TimeUnit.SECONDS,
                TaskExecutors.MAIN_THREAD,
                mCallBack
        );

    }

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks
            mCallBack = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

        @Override
        public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);
            verificationId = s;
        }

        @Override
        public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
            String code = phoneAuthCredential.getSmsCode();
//            Log.e("code", code);
            Toast.makeText(OtpActivity.this, "OTP sent on given number", Toast.LENGTH_SHORT).show();
            if (code != null)
            {

            }
        }

        @Override
        public void onVerificationFailed(FirebaseException e) {
            Toast.makeText(OtpActivity.this,"Invalid mobile number", Toast.LENGTH_LONG).show();
            Log.e("e.getMessage()", e + "");
        }
    };


    private void verifyCode(String code)
    {
        progressDialog.setMessage("OTP Verifying ...");
        progressDialog.setCancelable(true);
        progressDialog.show();
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);
        signInWithCredential(credential);
    }


    private void signInWithCredential(PhoneAuthCredential credential)
    {

        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            progressDialog.dismiss();
                            if (isConnected)
                                otpVerify();
                            else
                                showSnack(isConnected);
                        } else
                        {
                            progressDialog.dismiss();
                            Toast.makeText(OtpActivity.this, task.getException().getMessage(),
                                    Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(OtpActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        overridePendingTransition(R.anim.trans_left_in,
                R.anim.trans_left_out);
    }


    private void otpVerify() {
        progressDialog.setMessage("Please Wait..");
        progressDialog.setCancelable(true);
        progressDialog.show();

        StringRequest postRequest = new StringRequest(Request.Method.POST, EndPoints.OTP_VERIFY,
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
                                //{"status":200,"message":"success","data":{"fb_id":"2360644337504490","user_id":"14"}}
                                JSONObject dataObj = obj.getJSONObject("data");
                                String userid = dataObj.getString("id");
                                sessionManager.setUserId(userid);
                                sessionManager.setStep("1");
                                Intent in = new Intent(OtpActivity.this, UploadPicsActivity.class);
                                in.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(in);
                                overridePendingTransition(R.anim.trans_left_in,
                                        R.anim.trans_left_out);

                            } else {
                                Intent intent = new Intent(OtpActivity.this, HomePageActivity.class);
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
