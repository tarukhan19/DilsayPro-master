package com.dbvertex.dilsayproject.insta;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
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
import com.dbvertex.dilsayproject.EndPoints;
import com.dbvertex.dilsayproject.R;
import com.dbvertex.dilsayproject.UserAuth.HomeActivity;
import com.dbvertex.dilsayproject.session.SessionManager;
import com.squareup.picasso.Picasso;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class InstaActivity extends AppCompatActivity implements AuthenticationListener{
    private String token = null;
    private AppPreferences appPreferences = null;
    private AuthenticationDialog authenticationDialog = null;
    private View info = null;
    ImageView pic;
    TextView id,name;
    LinearLayout veryLL;
    Button continueBtn;
    SessionManager sessionManager;
    RequestQueue queue;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insta);
        queue = Volley.newRequestQueue(InstaActivity.this);
        sessionManager = new SessionManager(this);
        progressDialog = new ProgressDialog(InstaActivity.this, R.style.CustomDialog);

        info = findViewById(R.id.info);
        appPreferences = new AppPreferences(this);
        pic = findViewById(R.id.pic);
        id = findViewById(R.id.id);
        name = findViewById(R.id.name);
        veryLL=findViewById(R.id.veryLL);
        continueBtn=findViewById(R.id.continueBtn);
        token = appPreferences.getString(AppPreferences.TOKEN);
        if (token != null) {
            getUserInfoByAccessToken(token);
        }

        continueBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(InstaActivity.this, HomeActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                overridePendingTransition(R.anim.trans_left_in,
                        R.anim.trans_left_out);
            }
        });
        authCheck();
    }


    public void login() {
//        info.setVisibility(View.VISIBLE);
        veryLL.setVisibility(View.VISIBLE);
        Picasso.with(this).load(appPreferences.getString(AppPreferences.PROFILE_PIC)).into(pic);
        id.setText(appPreferences.getString(AppPreferences.USER_ID));
        name.setText(appPreferences.getString(AppPreferences.USER_NAME));
        verifyInsta();
    }

    public void logout() {
        token = null;
       // info.setVisibility(View.GONE);
        appPreferences.clear();
    }

    @Override
    public void onTokenReceived(String auth_token) {
        if (auth_token == null)
            return;
        appPreferences.putString(AppPreferences.TOKEN, auth_token);
        token = auth_token;
        getUserInfoByAccessToken(token);
    }

    public void authCheck()
    {
        if(token!=null)
        {
            logout();
        }
        else {
            authenticationDialog = new AuthenticationDialog(InstaActivity.this, this);
            authenticationDialog.setCancelable(true);
            authenticationDialog.show();
        }
    }
    private void getUserInfoByAccessToken(String token) {
        new RequestInstagramAPI().execute();
    }

    private class RequestInstagramAPI extends AsyncTask<Void, String, String> {

        @Override
        protected String doInBackground(Void... params) {
            HttpClient httpClient = new DefaultHttpClient();
            HttpGet httpGet = new HttpGet(getResources().getString(R.string.get_user_info_url) + token);
            try {
                HttpResponse response = httpClient.execute(httpGet);
                HttpEntity httpEntity = response.getEntity();
                return EntityUtils.toString(httpEntity);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String response) {
            super.onPostExecute(response);
            if (response != null) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    Log.e("response", jsonObject.toString());
                    JSONObject jsonData = jsonObject.getJSONObject("data");

                    Log.e("data",jsonData.toString());
                    if (jsonData.has("id")) {
                        //сохранение данных пользователя
                        appPreferences.putString(AppPreferences.USER_ID, jsonData.getString("id"));
                        appPreferences.putString(AppPreferences.USER_NAME, jsonData.getString("username"));
                        appPreferences.putString(AppPreferences.PROFILE_PIC, jsonData.getString("profile_picture"));

                        //TODO: сохранить еще данные
                        login();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            } else {
                Toast toast = Toast.makeText(getApplicationContext(),"Ошибка входа!",Toast.LENGTH_LONG);
                toast.show();
            }
        }
    }



    private void verifyInsta()
    {
        progressDialog.setMessage("Please Wait..");
        progressDialog.setCancelable(true);
        progressDialog.show();

        StringRequest postRequest = new StringRequest(Request.Method.POST, EndPoints.VERIFYINSTA,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("VERIFYINSTA", response);
                        try {

                            progressDialog.dismiss();
                            JSONObject object=new JSONObject(response);




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
                params.put(",insta_verified", "1");
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

}
