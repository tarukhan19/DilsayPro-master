package com.dbvertex.dilsayproject;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.databinding.DataBindingUtil;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
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
import com.dbvertex.dilsayproject.UserAuth.HomePageActivity;
import com.dbvertex.dilsayproject.UserAuth.LocationDescription;
import com.dbvertex.dilsayproject.databinding.ActivityContactUsBinding;
import com.dbvertex.dilsayproject.session.SessionManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ContactUsActivity extends AppCompatActivity {
    ActivityContactUsBinding binding;
    SessionManager sessionManager;
    ProgressDialog dialog;
    RequestQueue queue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_contact_us);

        sessionManager = new SessionManager(ContactUsActivity.this);
        dialog = new ProgressDialog(ContactUsActivity.this, R.style.CustomDialog);
        queue = Volley.newRequestQueue(ContactUsActivity.this);


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_main);
        final LinearLayout back = toolbar.findViewById(R.id.back_LL);
        TextView toolbar_title = toolbar.findViewById(R.id.titleTV);
        toolbar_title.setText("Contact Us");


        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ContactUsActivity.this, HomePageActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intent.putExtra("from","setting");

                startActivity(intent);
                overridePendingTransition(R.anim.trans_left_in,
                        R.anim.trans_left_out);
            }
        });

        binding.fnameET.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (binding.fnameET.getText().toString().length() < 2) {
                    binding.nameTIL.setError("Enter valid Valid");
                } else {
                    binding.nameTIL.setErrorEnabled(false);
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


        binding.emailET.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {


                if (binding.emailET.getText().toString().isEmpty()) {
                    binding.emailTIL.setError("Email Id is required.");

                }

                String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
                if (!binding.emailET.getText().toString().matches(emailPattern)) {
                    binding.emailTIL.setError("Enter valid Email Id.");

                } else {
                    binding.emailTIL.setErrorEnabled(false);
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


        binding.submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                boolean isValidate = true;

                String name = binding.fnameET.getText().toString();
                if (name.isEmpty()) {
                    isValidate = false;
                    binding.nameTIL.setError("Name is required.");
                }
                String email = binding.emailET.getText().toString();
                if (email.isEmpty()) {
                    isValidate = false;
                    binding.emailTIL.setError("Email Id is required.");
                }
                String msg = binding.descripationET.getText().toString();
                if (msg.isEmpty()) {
                    isValidate = false;
                    binding.descTIL.setError("Description is required.");
                }

                if (isValidate) {


                    contactUs(name, email, msg);
                }

            }

        });

    }


    private void contactUs(final String name, final String email, final String msg) {
        dialog.setMessage("Please Wait..");
        dialog.setCancelable(true);
        dialog.show();

        StringRequest postRequest = new StringRequest(Request.Method.POST, EndPoints.CONTACT_US,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        dialog.dismiss();
                        Log.e("response", response);
                        try {
                            JSONObject obj = new JSONObject(response);
                            int status = obj.getInt("status");
                            String message = obj.getString("message");


                            if (status == 200 && message.equals("success")) {

                                final Dialog dialog = new Dialog(ContactUsActivity.this, R.style.CustomDialog);
                                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                                dialog.setContentView(R.layout.item_camerarentalsuccess);
                                dialog.setCanceledOnTouchOutside(false);
                                dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT,
                                        WindowManager.LayoutParams.WRAP_CONTENT);
                                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

                                LinearLayout submit = dialog.findViewById(R.id.submit);
                                ImageView cross = dialog.findViewById(R.id.cross);
                                TextView msgTV = dialog.findViewById(R.id.msgTV);

                                msgTV.setText("Thank you for contacting us. We'll get back to you soon.");
                                dialog.show();

                                submit.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        dialog.dismiss();
                                        Intent intent = new Intent(ContactUsActivity.this, HomePageActivity.class);
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
                                        finish();
                                    }
                                });

                            } else {
                                Toast.makeText(ContactUsActivity.this, message, Toast.LENGTH_SHORT).show();
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
                        dialog.dismiss();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> ob = new HashMap<>();
                ob.put("user_id", sessionManager.getUserId().get(SessionManager.KEY_USERID));
                ob.put("name", name);
                ob.put("email", email);
                ob.put("description", msg);
                //Log.e("params", ob.toString());
                return ob;
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
        Intent intent = new Intent(ContactUsActivity.this, HomePageActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.putExtra("from","setting");

        startActivity(intent);
        overridePendingTransition(R.anim.trans_left_in,
                R.anim.trans_left_out);
    }
}
