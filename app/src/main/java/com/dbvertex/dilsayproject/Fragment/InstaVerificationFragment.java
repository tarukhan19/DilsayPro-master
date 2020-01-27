package com.dbvertex.dilsayproject.Fragment;


import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.dbvertex.dilsayproject.ContactUsActivity;
import com.dbvertex.dilsayproject.R;
import com.dbvertex.dilsayproject.UserAuth.HomePageActivity;
import com.dbvertex.dilsayproject.insta.AppPreferences;
import com.dbvertex.dilsayproject.insta.AuthenticationDialog;
import com.dbvertex.dilsayproject.insta.AuthenticationListener;
import com.dbvertex.dilsayproject.insta.InstaActivity;
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

/**
 * A simple {@link Fragment} subclass.
 */
public class InstaVerificationFragment extends Fragment  {

   Button button;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.fragment_insta_verification, container, false);
        button = view.findViewById(R.id.btn_login);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), InstaActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.trans_left_in,
                        R.anim.trans_left_out);
            }
        });

        return view;
    }



}
