package com.dbvertex.dilsayproject.Fragment;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.appcompat.widget.Toolbar;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.dbvertex.dilsayproject.AboutUsActivity;
import com.dbvertex.dilsayproject.ContactUsActivity;
import com.dbvertex.dilsayproject.DeleteActivity;
import com.dbvertex.dilsayproject.EndPoints;
import com.dbvertex.dilsayproject.FrequentlyAskQstnActivity;
import com.dbvertex.dilsayproject.PrivacyPolicyActivity;
import com.dbvertex.dilsayproject.R;
import com.dbvertex.dilsayproject.TermsandConditionActivity;
import com.dbvertex.dilsayproject.UserAuth.EducationActivity;
import com.dbvertex.dilsayproject.UserAuth.HomePageActivity;
import com.dbvertex.dilsayproject.UserAuth.LoginActivity;
import com.dbvertex.dilsayproject.databinding.FragmentSettingBinding;
import com.dbvertex.dilsayproject.session.SessionManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


public class SettingFragment extends Fragment {
    FragmentSettingBinding binding;

    ProgressDialog progressDialog;
    RequestQueue queue;
    SessionManager sessionManager;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_setting, container, false);
        View view = binding.getRoot();
        progressDialog = new ProgressDialog(getActivity(), R.style.CustomDialog);
        queue = Volley.newRequestQueue(getActivity());
        sessionManager = new SessionManager(getActivity());

        Toolbar toolbar = (Toolbar) view.findViewById(R.id.toolbar_main);
        LinearLayout back = toolbar.findViewById(R.id.back_LL);
        TextView toolbar_title = toolbar.findViewById(R.id.titleTV);
        toolbar_title.setText("Settings");
        ImageView back_img=toolbar.findViewById(R.id.back_img);
        back_img.setImageResource(R.drawable.menu);



        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((HomePageActivity)getActivity()).clickEvents();


//                Intent intent = new Intent(getActivity(), HomePageActivity.class);
//                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                startActivity(intent);
//                getActivity().overridePendingTransition(R.anim.trans_left_in,
//                        R.anim.trans_left_out);
            }
        });


        binding.contactusLL.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ContactUsActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.trans_left_in,
                        R.anim.trans_left_out);
            }
        });


        binding.tndcLL.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), TermsandConditionActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.trans_left_in,
                        R.anim.trans_left_out);
            }
        });


        binding.privacyLL.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), PrivacyPolicyActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.trans_left_in,
                        R.anim.trans_left_out);
            }
        });


        binding.aboutusLL.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), AboutUsActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.trans_left_in,
                        R.anim.trans_left_out);
            }
        });

        binding.faqLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), FrequentlyAskQstnActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.trans_left_in,
                        R.anim.trans_left_out);
            }
        });


        binding.deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), DeleteActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intent.putExtra("from","SettingFragment");
                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.trans_left_in,
                        R.anim.trans_left_out);
            }
        });


        return view;

    }

//
//    private void showLogOutDialog() {
//
//        final Dialog dialog = new Dialog(getActivity(), R.style.CustomDialog);
//        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
//        dialog.setContentView(R.layout.item_logout);
//        dialog.setCanceledOnTouchOutside(false);
//        dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
//        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
//
//        LinearLayout ok = (LinearLayout) dialog.findViewById(R.id.ok);
//        LinearLayout cancel = (LinearLayout) dialog.findViewById(R.id.cancel);
//        TextView msgTV=dialog.findViewById(R.id.msgTV);
//
//        msgTV.setText("You want to delete your account?");
//
//        ok.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//                deleteAccount();
//
//                dialog.dismiss();
//
//
//            }
//        });
//
//        cancel.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//                dialog.dismiss();
//            }
//        });
//
//
//        dialog.show();
//
//    }




}
