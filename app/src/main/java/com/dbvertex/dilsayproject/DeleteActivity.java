package com.dbvertex.dilsayproject;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.databinding.DataBindingUtil;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
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
import com.dbvertex.dilsayproject.UserAuth.LoginActivity;
import com.dbvertex.dilsayproject.databinding.ActivityDeleteBinding;
import com.dbvertex.dilsayproject.session.SessionManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class DeleteActivity extends AppCompatActivity {
    ActivityDeleteBinding binding;
    String reason = "";
    ProgressDialog progressDialog;
    RequestQueue queue;
    SessionManager sessionManager;
    Intent intent;
    String from;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_delete);
        progressDialog = new ProgressDialog(DeleteActivity.this, R.style.CustomDialog);
        queue = Volley.newRequestQueue(DeleteActivity.this);
        sessionManager = new SessionManager(DeleteActivity.this);

        intent=getIntent();
        from=intent.getStringExtra("from");

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_main);
        final LinearLayout back = toolbar.findViewById(R.id.back_LL);
        TextView toolbar_title = toolbar.findViewById(R.id.titleTV);
        toolbar_title.setText("Delete Account");


        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DeleteActivity.this, HomePageActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intent.putExtra("from",from);

                startActivity(intent);
                overridePendingTransition(R.anim.trans_left_in,
                        R.anim.trans_left_out);
            }
        });


        binding.reason1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                reason = binding.reason1TV.getText().toString();
                binding.reason1.setBackground(getResources().getDrawable(R.drawable.checked_bg));
                binding.reason2.setBackground(getResources().getDrawable(R.drawable.deletebg));
                binding.reason3.setBackground(getResources().getDrawable(R.drawable.deletebg));
                binding.reason4.setBackground(getResources().getDrawable(R.drawable.deletebg));
                binding.reason5.setBackground(getResources().getDrawable(R.drawable.deletebg));
                binding.reason6.setBackground(getResources().getDrawable(R.drawable.deletebg));


            }
        });

        binding.reason2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                reason = binding.reason2TV.getText().toString();
                binding.reason1.setBackground(getResources().getDrawable(R.drawable.deletebg));
                binding.reason2.setBackground(getResources().getDrawable(R.drawable.checked_bg));
                binding.reason3.setBackground(getResources().getDrawable(R.drawable.deletebg));
                binding.reason4.setBackground(getResources().getDrawable(R.drawable.deletebg));
                binding.reason5.setBackground(getResources().getDrawable(R.drawable.deletebg));
                binding.reason6.setBackground(getResources().getDrawable(R.drawable.deletebg));


            }
        });


        binding.reason3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                reason = binding.reason3TV.getText().toString();
                binding.reason1.setBackground(getResources().getDrawable(R.drawable.deletebg));
                binding.reason2.setBackground(getResources().getDrawable(R.drawable.deletebg));
                binding.reason3.setBackground(getResources().getDrawable(R.drawable.checked_bg));
                binding.reason4.setBackground(getResources().getDrawable(R.drawable.deletebg));
                binding.reason5.setBackground(getResources().getDrawable(R.drawable.deletebg));
                binding.reason6.setBackground(getResources().getDrawable(R.drawable.deletebg));

            }
        });

        binding.reason4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                reason = binding.reason4TV.getText().toString();
                binding.reason1.setBackground(getResources().getDrawable(R.drawable.deletebg));
                binding.reason2.setBackground(getResources().getDrawable(R.drawable.deletebg));
                binding.reason3.setBackground(getResources().getDrawable(R.drawable.deletebg));
                binding.reason4.setBackground(getResources().getDrawable(R.drawable.checked_bg));
                binding.reason5.setBackground(getResources().getDrawable(R.drawable.deletebg));
                binding.reason6.setBackground(getResources().getDrawable(R.drawable.deletebg));

            }
        });

        binding.reason5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                reason = binding.reason5TV.getText().toString();
                binding.reason1.setBackground(getResources().getDrawable(R.drawable.deletebg));
                binding.reason2.setBackground(getResources().getDrawable(R.drawable.deletebg));
                binding.reason3.setBackground(getResources().getDrawable(R.drawable.deletebg));
                binding.reason4.setBackground(getResources().getDrawable(R.drawable.deletebg));
                binding.reason5.setBackground(getResources().getDrawable(R.drawable.checked_bg));
                binding.reason6.setBackground(getResources().getDrawable(R.drawable.deletebg));

            }
        });

        binding.reason6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                reason = binding.reason6TV.getText().toString();
                binding.reason1.setBackground(getResources().getDrawable(R.drawable.deletebg));
                binding.reason2.setBackground(getResources().getDrawable(R.drawable.deletebg));
                binding.reason3.setBackground(getResources().getDrawable(R.drawable.deletebg));
                binding.reason4.setBackground(getResources().getDrawable(R.drawable.deletebg));
                binding.reason5.setBackground(getResources().getDrawable(R.drawable.deletebg));
                binding.reason6.setBackground(getResources().getDrawable(R.drawable.checked_bg));

            }
        });

        binding.continueBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (reason.isEmpty()) {
                    Toast.makeText(DeleteActivity.this, "Select Reason", Toast.LENGTH_SHORT).show();
                } else {
                    deleteAccount();
                }
            }
        });


    }


    private void deleteAccount() {
        progressDialog.setMessage("Please Wait..");
        progressDialog.setCancelable(true);
        progressDialog.show();

        StringRequest postRequest = new StringRequest(Request.Method.POST, EndPoints.DELETE_ACCOUNT,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressDialog.dismiss();
                        Log.e("response", response);
                        try {
                            JSONObject obj = new JSONObject(response);
                            int status = obj.getInt("status");
                            String message = obj.getString("message");


                            if (status == 200 && message.equals("success")) {

                                final Dialog dialog = new Dialog(DeleteActivity.this, R.style.CustomDialog);
                                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                                dialog.setContentView(R.layout.item_camerarentalsuccess);
                                dialog.setCanceledOnTouchOutside(false);
                                dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT,
                                        WindowManager.LayoutParams.WRAP_CONTENT);
                                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

                                LinearLayout submit = dialog.findViewById(R.id.submit);
                                ImageView cross = dialog.findViewById(R.id.cross);
                                TextView msgTV = dialog.findViewById(R.id.msgTV);

                                msgTV.setText("Your account has been deleted successfully.");
                                dialog.show();

                                submit.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        dialog.dismiss();
                                        Intent intent = new Intent(DeleteActivity.this, LoginActivity.class);
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
                                    }
                                });

                            } else {
                                Toast.makeText(DeleteActivity.this, message, Toast.LENGTH_SHORT).show();
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
                ob.put("deleted_reason", reason);

                Log.e("params",ob.toString());


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

        Intent intent = new Intent(DeleteActivity.this, HomePageActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.putExtra("from",from);

        startActivity(intent);
        overridePendingTransition(R.anim.trans_left_in,
                R.anim.trans_left_out);
    }
}
