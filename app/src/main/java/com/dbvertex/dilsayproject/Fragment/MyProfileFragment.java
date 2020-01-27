package com.dbvertex.dilsayproject.Fragment;


import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.appcompat.widget.Toolbar;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
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
import com.dbvertex.dilsayproject.DeleteActivity;
import com.dbvertex.dilsayproject.EndPoints;
import com.dbvertex.dilsayproject.Model.ChooseInterestDTO;
import com.dbvertex.dilsayproject.Model.HeightDTO;
import com.dbvertex.dilsayproject.ProfileDetailActivity;
import com.dbvertex.dilsayproject.R;
import com.dbvertex.dilsayproject.UserAuth.HomePageActivity;
import com.dbvertex.dilsayproject.UserAuth.LifestyleActivity;
import com.dbvertex.dilsayproject.UserAuth.ReligionActivity;
import com.dbvertex.dilsayproject.UserAuth.UploadPicsActivity;
import com.dbvertex.dilsayproject.databinding.FragmentMyProfileBinding;
import com.dbvertex.dilsayproject.insta.AppPreferences;
import com.dbvertex.dilsayproject.insta.AuthenticationDialog;
import com.dbvertex.dilsayproject.insta.AuthenticationListener;
import com.dbvertex.dilsayproject.insta.InstaActivity;
import com.dbvertex.dilsayproject.session.SessionManager;
import com.squareup.picasso.Picasso;
import com.zhy.view.flowlayout.FlowLayout;
import com.zhy.view.flowlayout.TagAdapter;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class MyProfileFragment extends Fragment  implements AuthenticationListener {

    private FragmentMyProfileBinding binding;
    private RequestQueue queue;
    private ProgressDialog progressDialog;
    private SessionManager session;
    private ArrayList<String> photoBMList;
    LayoutInflater mInflater;
    private String foodprefrence, smokeprefrence, drinkprefrence;


    private String token = null;
    private AppPreferences appPreferences = null;
    private AuthenticationDialog authenticationDialog = null;
    private SessionManager sessionManager;


    private InterestAdapter adapter;
    private SelectInterestAdp adp;
    private Dialog interestDialog;
    private AlertDialog.Builder interestAlertdialog;
    private View interestView;
    private RecyclerView recyclerView;
    private List<ChooseInterestDTO> chooseInterestDTOList;
    private List<String> interestlistsent,heightarraylist,religionarraylist,communityarraylist,educationarraylist,occupationarraylist,raisedinarraylist;

    private ListView heightLV;
    private AlertDialog heightDialog;
    private AlertDialog.Builder  heightbuilder;
    private ArrayAdapter<String> heightArrayAdapter;

    private ListView religionLV;
    private AlertDialog religionDialog;
    private AlertDialog.Builder  religionbuilder;
    private ArrayAdapter<String> religionArrayAdapter;

    private ListView communityLV;
    private AlertDialog communityDialog;
    private AlertDialog.Builder  communitybuilder;
    private ArrayAdapter<String> communityArrayAdapter;

    private ListView educationLV;
    private AlertDialog educationDialog;
    private AlertDialog.Builder  educationbuilder;
    private ArrayAdapter<String> educationArrayAdapter;


    private ListView occupationLV;
    private AlertDialog occupationDialog;
    private AlertDialog.Builder  occupationbuilder;
    private ArrayAdapter<String> occupationArrayAdapter;


    private ListView raisedInLV;
    private AlertDialog raisedDialog;
    private AlertDialog.Builder  raisedbuilder;
    private ArrayAdapter<String> raisedArrayAdapter;

    private String name,age,gender,height,raised_in,dob,education,religion,community,career,description,intrests,looking_for,email,mobileno,countrycode;

    public MyProfileFragment() {
        // Required empty public constructor
    }

    private void bindView(ArrayList<String> list) {
        binding.autoPager.startAutoScroll();
        binding.autoPager.setInterval(3000);
        binding.autoPager.setCycle(true);
        binding.autoPager.setStopScrollWhenTouch(true);
        BannerPagerAdapter bannerPagerAdapter = new BannerPagerAdapter(getActivity(), list);
        binding.autoPager.setAdapter(bannerPagerAdapter);
        binding.indicator.setViewPager(binding.autoPager, 0);
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_my_profile, container, false);
        View view = binding.getRoot();
        Toolbar toolbar = (Toolbar) view.findViewById(R.id.toolbar_main);
        LinearLayout back = toolbar.findViewById(R.id.back_LL);
        TextView toolbar_title = toolbar.findViewById(R.id.titleTV);
        toolbar_title.setText("My Profile");
        ImageView back_img = toolbar.findViewById(R.id.back_img);
        back_img.setImageResource(R.drawable.menu);
        queue = Volley.newRequestQueue(getActivity());
        progressDialog = new ProgressDialog(getActivity());
        session = new SessionManager(getActivity());

        sessionManager = new SessionManager(getActivity());
        appPreferences = new AppPreferences(getActivity());

        token = appPreferences.getString(AppPreferences.TOKEN);
        if (token != null) {
            getUserInfoByAccessToken(token);
        }


        mInflater = LayoutInflater.from(getActivity());
        photoBMList = new ArrayList<String>();
        binding.deleteaccntBTN.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(getActivity(), DeleteActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intent.putExtra("from", "MyProfileFragment");
                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.trans_left_in,
                        R.anim.trans_left_out);
            }
        });

        heightbuilder = new AlertDialog.Builder(getActivity());
        heightLV = new ListView(getActivity());
        heightarraylist=new ArrayList<>();
        heightArrayAdapter = new ArrayAdapter<String>(getActivity(),
                R.layout.item_cameraspinner, R.id.text, heightarraylist);
        heightLV.setAdapter(heightArrayAdapter);
        heightbuilder.setView(heightLV);
        heightDialog = heightbuilder.create();

        religionbuilder = new AlertDialog.Builder(getActivity());
        religionLV = new ListView(getActivity());
        religionarraylist=new ArrayList<>();
        religionArrayAdapter = new ArrayAdapter<String>(getActivity(),
                R.layout.item_cameraspinner, R.id.text, religionarraylist);
        religionLV.setAdapter(religionArrayAdapter);
        religionbuilder.setView(religionLV);
        religionDialog = religionbuilder.create();

        communitybuilder = new AlertDialog.Builder(getActivity());
        communityLV = new ListView(getActivity());
        communityarraylist=new ArrayList<>();
        communityArrayAdapter = new ArrayAdapter<String>(getActivity(),
                R.layout.item_cameraspinner, R.id.text, communityarraylist);
        communityLV.setAdapter(communityArrayAdapter);
        communitybuilder.setView(communityLV);
        communityDialog = communitybuilder.create();

        educationbuilder = new AlertDialog.Builder(getActivity());
        educationLV = new ListView(getActivity());
        educationarraylist=new ArrayList<>();
        educationArrayAdapter = new ArrayAdapter<String>(getActivity(),
                R.layout.item_cameraspinner, R.id.text, educationarraylist);
        educationLV.setAdapter(educationArrayAdapter);
        educationbuilder.setView(educationLV);
        educationDialog = educationbuilder.create();

        raisedbuilder = new AlertDialog.Builder(getActivity());
        raisedInLV = new ListView(getActivity());
        raisedinarraylist=new ArrayList<>();
        raisedArrayAdapter = new ArrayAdapter<String>(getActivity(),
                R.layout.item_cameraspinner, R.id.text, raisedinarraylist);
        raisedInLV.setAdapter(raisedArrayAdapter);
        raisedbuilder.setView(raisedInLV);
        raisedDialog = raisedbuilder.create();

        occupationbuilder = new AlertDialog.Builder(getActivity());
        occupationLV = new ListView(getActivity());
        occupationarraylist=new ArrayList<>();
        occupationArrayAdapter = new ArrayAdapter<String>(getActivity(),
                R.layout.item_cameraspinner, R.id.text, occupationarraylist);
        occupationLV.setAdapter(occupationArrayAdapter);
        occupationbuilder.setView(occupationLV);
        occupationDialog = occupationbuilder.create();



        chooseInterestDTOList = new ArrayList<>();
        interestlistsent= new ArrayList<>();
        adapter = new InterestAdapter(getActivity(), chooseInterestDTOList);
        adp=new SelectInterestAdp(getActivity(), interestlistsent);


        interestAlertdialog = new AlertDialog.Builder(getActivity());
        interestAlertdialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked OK button
            }
        });
        interestView = LayoutInflater.from(getActivity()).inflate(R.layout.item_interest, null);
        interestAlertdialog.setView(interestView);
        interestDialog = interestAlertdialog.create();
        recyclerView = (RecyclerView) interestView.findViewById(R.id.rv);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);

        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getActivity(), 3);
        binding.rv.setLayoutManager(mLayoutManager);
        binding.rv.addItemDecoration(new GridSpacingItemDecoration(2, dpToPx(5), true));
        binding.rv.setItemAnimator(new DefaultItemAnimator());
        binding.rv.setAdapter(adp);

        heightLV.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ViewGroup vg = (ViewGroup) view;
                TextView txt = (TextView) vg.findViewById(R.id.text);
                binding.heightTV.setText(txt.getText().toString());
                height = txt.getText().toString();
                heightDialog.dismiss();
            }
        });

        binding.heightLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                heightDialog.setView(heightLV);
                heightDialog.show();

            }
        });


        religionLV.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ViewGroup vg = (ViewGroup) view;
                TextView txt = (TextView) vg.findViewById(R.id.text);
                binding.religionTV.setText(txt.getText().toString());
                religion = txt.getText().toString();
                religionDialog.dismiss();
            }
        });

        binding.religionLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                religionDialog.setView(religionLV);
                religionDialog.show();

            }
        });



        communityLV.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ViewGroup vg = (ViewGroup) view;
                TextView txt = (TextView) vg.findViewById(R.id.text);
                binding.communityTV.setText(txt.getText().toString());
                community = txt.getText().toString();
                communityDialog.dismiss();
            }
        });

        binding.communityLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                communityDialog.setView(communityLV);
                communityDialog.show();

            }
        });


        educationLV.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ViewGroup vg = (ViewGroup) view;
                TextView txt = (TextView) vg.findViewById(R.id.text);
                binding.educationTV.setText(txt.getText().toString());
                education = txt.getText().toString();
                educationDialog.dismiss();
            }
        });

        binding.educationLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                educationDialog.setView(educationLV);
                educationDialog.show();

            }
        });

        occupationLV.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ViewGroup vg = (ViewGroup) view;
                TextView txt = (TextView) vg.findViewById(R.id.text);
                binding.occupationTV.setText(txt.getText().toString());
                career = txt.getText().toString();
                occupationDialog.dismiss();
            }
        });

        binding.occupationLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                occupationDialog.setView(occupationLV);
                occupationDialog.show();

            }
        });

        raisedInLV.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ViewGroup vg = (ViewGroup) view;
                TextView txt = (TextView) vg.findViewById(R.id.text);
                binding.raisedinTV.setText(txt.getText().toString());
                raised_in = txt.getText().toString();
                raisedDialog.dismiss();
            }
        });

        binding.raisedinLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                raisedDialog.setView(raisedInLV);
                raisedDialog.show();

            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((HomePageActivity) getActivity()).clickEvents();

            }
        });

        binding.editLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                interestAlertdialog.setView(recyclerView);
                interestDialog.show();
            }
        });

        binding.editpic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), UploadPicsActivity.class);
                intent.putExtra("from","MyProfileFragment");
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP );
                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.trans_left_in,
                        R.anim.trans_left_out);
            }
        });


        binding.vegLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (foodprefrence.equalsIgnoreCase("nonveg") || foodprefrence.equalsIgnoreCase("")) {
                    binding.vegLL.setBackground(getResources().getDrawable(R.drawable.gradientcurverectangle2));
                    binding.vegTV.setTextColor(getResources().getColor(R.color.white));
                    binding.nonvegLL.setBackground(getResources().getDrawable(R.drawable.greycurverectangle1));
                    binding.nonvegTV.setTextColor(getResources().getColor(R.color.colorPrimary));
                    foodprefrence = "veg";
                }


            }
        });

        binding.nonvegLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.nonvegLL.setBackground(getResources().getDrawable(R.drawable.gradientcurverectangle1));
                binding.nonvegTV.setTextColor(getResources().getColor(R.color.white));
                binding.vegLL.setBackground(getResources().getDrawable(R.drawable.greycurverectangle2));
                binding.vegTV.setTextColor(getResources().getColor(R.color.colorPrimary));
                foodprefrence = "nonveg";
            }
        });

        binding.smokingLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.smokingLL.setBackground(getResources().getDrawable(R.drawable.gradientcurverectangle2));
                binding.smokeTV.setTextColor(getResources().getColor(R.color.white));
                binding.smokeocassionalLL.setBackground(getResources().getDrawable(R.drawable.greycurverectangle1));
                binding.smokeocassionalTV.setTextColor(getResources().getColor(R.color.colorPrimary));
                binding.nonsmokeLL.setBackground(getResources().getDrawable(R.drawable.greycurverectangle));
                binding.nonsmokeTV.setTextColor(getResources().getColor(R.color.colorPrimary));
                smokeprefrence = "smoke";
            }
        });

        binding.nonsmokeLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.smokingLL.setBackground(getResources().getDrawable(R.drawable.greycurverectangle2));
                binding.smokeTV.setTextColor(getResources().getColor(R.color.colorPrimary));
                binding.smokeocassionalLL.setBackground(getResources().getDrawable(R.drawable.greycurverectangle1));
                binding.smokeocassionalTV.setTextColor(getResources().getColor(R.color.colorPrimary));
                binding.nonsmokeLL.setBackground(getResources().getDrawable(R.drawable.gradientcurverectangle));
                binding.nonsmokeTV.setTextColor(getResources().getColor(R.color.white));
                smokeprefrence = "nonsmoke";

            }
        });

        binding.smokeocassionalLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.smokingLL.setBackground(getResources().getDrawable(R.drawable.greycurverectangle2));
                binding.smokeTV.setTextColor(getResources().getColor(R.color.colorPrimary));
                binding.smokeocassionalLL.setBackground(getResources().getDrawable(R.drawable.gradientcurverectangle1));
                binding.smokeocassionalTV.setTextColor(getResources().getColor(R.color.white));
                binding.nonsmokeLL.setBackground(getResources().getDrawable(R.drawable.greycurverectangle));
                binding.nonsmokeTV.setTextColor(getResources().getColor(R.color.colorPrimary));
                smokeprefrence = "smokeocassionally";

            }
        });


        binding.drinkLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.drinkLL.setBackground(getResources().getDrawable(R.drawable.gradientcurverectangle2));
                binding.drinkTV.setTextColor(getResources().getColor(R.color.white));
                binding.drinkocassionalLL.setBackground(getResources().getDrawable(R.drawable.greycurverectangle1));
                binding.drinkocassionalTV.setTextColor(getResources().getColor(R.color.colorPrimary));
                binding.nondrinkLL.setBackground(getResources().getDrawable(R.drawable.greycurverectangle));
                binding.nondrinkTV.setTextColor(getResources().getColor(R.color.colorPrimary));
                drinkprefrence = "drink";
            }
        });

        binding.nondrinkLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.drinkLL.setBackground(getResources().getDrawable(R.drawable.greycurverectangle2));
                binding.drinkTV.setTextColor(getResources().getColor(R.color.colorPrimary));
                binding.drinkocassionalLL.setBackground(getResources().getDrawable(R.drawable.greycurverectangle1));
                binding.drinkocassionalTV.setTextColor(getResources().getColor(R.color.colorPrimary));
                binding.nondrinkLL.setBackground(getResources().getDrawable(R.drawable.gradientcurverectangle));
                binding.nondrinkTV.setTextColor(getResources().getColor(R.color.white));
                drinkprefrence = "nondrink";

            }
        });

        binding.drinkocassionalLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.drinkLL.setBackground(getResources().getDrawable(R.drawable.greycurverectangle2));
                binding.drinkTV.setTextColor(getResources().getColor(R.color.colorPrimary));
                binding.drinkocassionalLL.setBackground(getResources().getDrawable(R.drawable.gradientcurverectangle1));
                binding.drinkocassionalTV.setTextColor(getResources().getColor(R.color.white));
                binding.nondrinkLL.setBackground(getResources().getDrawable(R.drawable.greycurverectangle));
                binding.nondrinkTV.setTextColor(getResources().getColor(R.color.colorPrimary));
                drinkprefrence = "drinkocassionally";

            }
        });

        binding.instaLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                authCheck();
            }
        });

        binding.submitBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (binding.nameTV.getText().toString().isEmpty())
                {
                    binding.nameTIL.setError("Enter Name");
                }
                else if (binding.aboutmeTV.getText().toString().isEmpty())
                {
                    binding.aboutmeTIL.setError("Enter about yourself.");
                }
               else if (interestlistsent.size() == 0) {
                    binding.interestTIL.setError("Select Interest");
                }
                else
                {
                    StringBuilder sbString = new StringBuilder("");
                    for (String language : interestlistsent) {
                        sbString.append(language).append(",");
                    }
                    intrests = sbString.toString();
                    if (intrests.length() > 0)
                        intrests = intrests.substring(0, intrests.length() - 1);

                    description=binding.aboutmeTV.getText().toString();
                    name=binding.nameTV.getText().toString();
                    binding.aboutmeTIL.setErrorEnabled(false);
                    binding.nameTIL.setErrorEnabled(false);
                    binding.interestTIL.setErrorEnabled(false);

                    submit();

                }
            }
        });


        loadDetails();

        return view;
    }

    private void submit()
    {
        progressDialog.setMessage("Please Wait..");
        progressDialog.setCancelable(true);
        progressDialog.show();

        StringRequest postRequest = new StringRequest(Request.Method.POST, EndPoints.UPDATE_PROFILE,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("response", response);
                        try {

                            progressDialog.dismiss();
                            JSONObject object=new JSONObject(response);

                            String status=object.getString("status");
                            String message=object.getString("message");
                            if (status.equalsIgnoreCase("200") && message.equalsIgnoreCase("success"))
                            {
                                openDialog();
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
                params.put("user_id", session.getUserId().get(SessionManager.KEY_USERID));
                params.put("community", community);
                params.put("name",name );
                params.put("email",email );
                params.put("mobile_no",mobileno );
                params.put("country_code",countrycode );
                params.put("dob",dob );
                params.put("gender", gender);
                params.put("height",height );
                params.put("looking_for",looking_for );
                params.put("intrests",intrests );
                params.put("food_prefrence",foodprefrence );
                params.put("smoke", smokeprefrence);
                params.put("drinking", drinkprefrence);
                params.put("religion",religion );
                params.put("community",community );
                params.put("education", education);
                params.put("career",career );
                params.put("raised_in", raised_in);
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

    private void openDialog()
    {
        final Dialog dialog = new Dialog(getActivity(), R.style.CustomDialog);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.item_camerarentalsuccess);
        dialog.setCanceledOnTouchOutside(false);
        dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        LinearLayout submit = dialog.findViewById(R.id.submit);
        ImageView cross = dialog.findViewById(R.id.cross);
        TextView msgTV=dialog.findViewById(R.id.msgTV);
        msgTV.setText("Profile updated sucessfully");
        dialog.show();

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                loadDetails();

            }
        });

        cross.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                loadDetails();
            }
        });

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
                            interestlistsent.clear();
                            JSONObject obj = new JSONObject(response);
                            int status = obj.getInt("status");
                            String message = obj.getString("message");
                            if (status == 1 && message.equalsIgnoreCase("success")) {

                                binding.ll.setVisibility(View.VISIBLE);
                                JSONArray data = obj.getJSONArray("data");
                                JSONObject jsonObject = data.getJSONObject(0);

                                name = jsonObject.getString("name");
                                age = jsonObject.getString("age");
                                email=jsonObject.getString("email");
                                mobileno=jsonObject.getString("mobile_no");
                                countrycode=jsonObject.getString("country_code");

                                gender = jsonObject.getString("gender");
                                height = jsonObject.getString("height");
                                raised_in = jsonObject.getString("raised_in");
                                dob = jsonObject.getString("dob");
                                education = jsonObject.getString("education");
                                foodprefrence = jsonObject.getString("food_prefrence");
                                smokeprefrence = jsonObject.getString("smoke");
                                drinkprefrence = jsonObject.getString("drinking");
                                religion = jsonObject.getString("religion");
                                community = jsonObject.getString("community");
                                career = jsonObject.getString("career");
                                description = jsonObject.getString("description");
                                intrests = jsonObject.getString("intrests");
                                looking_for=jsonObject.getString("looking_for");
                                JSONArray allInterest = jsonObject.getJSONArray("all_intrest");

                                binding.nameTV.setText(name);
                                binding.ageTV.setText(age);
                                binding.heightTV.setText(height);
                                binding.raisedinTV.setText(raised_in);
                                binding.educationTV.setText(education);
                                session.setLifestyle(foodprefrence, smokeprefrence, drinkprefrence);
                                binding.religionTV.setText(religion);
                                binding.communityTV.setText(community);
                                binding.genderTV.setText(gender);
                                binding.occupationTV.setText(career);
                                binding.aboutmeTV.setText(description);
                                loadHeight();
                                loadreligion();
                                loadCommunity();
                                loadEducation();
                                loadRaisedIn();
                                loadOccupation();

                                showFoodPrefrence();

                              List<String>  items = Arrays.asList(intrests.split("\\s*,\\s*"));
                                for (int i = 0; i < allInterest.length(); i++)
                                {
                                    String chooseInterestDTOString = allInterest.getString(i);
                                    ChooseInterestDTO chooseInterestDTO = new ChooseInterestDTO();
                                    chooseInterestDTO.setInterestName(chooseInterestDTOString);
                                    chooseInterestDTOList.add(chooseInterestDTO);

                                    for (int j = 0; j < items.size(); j++)
                                    {

                                        String select_interest = items.get(j);
                                        if (select_interest.equalsIgnoreCase(chooseInterestDTOString))
                                        {
                                            chooseInterestDTO.setSelected(!chooseInterestDTO.isSelected());
                                            interestlistsent.add(select_interest);

                                        }

                                    }
                                    recyclerView.setAdapter(adapter);
                                    binding.rv.setAdapter(adp);

                                }


                                adapter.notifyDataSetChanged();

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
                params.put("user_id", session.getUserId().get(SessionManager.KEY_USERID));

                Log.e("params", params.toString());
                return params;
            }
        };
        int socketTimeout = 30000;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        postRequest.setRetryPolicy(policy);
        queue.add(postRequest);
    }

    private void loadOccupation() {
        progressDialog.setMessage("Please Wait..");
        progressDialog.setCancelable(true);
        progressDialog.show();

        StringRequest postRequest = new StringRequest(Request.Method.POST, EndPoints.LOADCAREERLIST,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("response", response);
                        try {

                            occupationarraylist.clear();
                            progressDialog.dismiss();
                            JSONObject object=new JSONObject(response);

                            String status=object.getString("status");
                            String message=object.getString("message");
                            if (status.equalsIgnoreCase("200") && message.equalsIgnoreCase("success"))
                            {
                                JSONObject data= object.getJSONObject("data");
                                JSONArray dataarray=data.getJSONArray("career");
                                for (int i = 0; i < dataarray.length(); i++)
                                {
                                    String occupationname=dataarray.getString(i);
                                    occupationarraylist.add(occupationname);

                                }
                                occupationLV.setAdapter(occupationArrayAdapter);

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
                params.put("community", community);
                params.put("user_id", session.getUserId().get(SessionManager.KEY_USERID));
                Log.e("params", params.toString());
                return params;
            }
        };
        int socketTimeout = 30000;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        postRequest.setRetryPolicy(policy);
        queue.add(postRequest);
    }

    private void loadRaisedIn() {
        progressDialog.setMessage("Please Wait..");
        progressDialog.setCancelable(true);
        progressDialog.show();

        StringRequest postRequest = new StringRequest(Request.Method.POST, EndPoints.LOADRAISEDIN,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("response", response);
                        try {
                            raisedinarraylist.clear();
                            progressDialog.dismiss();
                            JSONObject object=new JSONObject(response);

                            String status=object.getString("status");
                            String message=object.getString("message");
                            if (status.equalsIgnoreCase("200") && message.equalsIgnoreCase("success"))
                            {
                                JSONObject data= object.getJSONObject("data");
                                JSONArray dataarray=data.getJSONArray("raised_in");
                                for (int i = 0; i < dataarray.length(); i++)
                                {
                                    String raisedinname=dataarray.getString(i);
                                    raisedinarraylist.add(raisedinname);

                                }
                                raisedInLV.setAdapter(raisedArrayAdapter);

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
                params.put("height", height);
                params.put("user_id", session.getUserId().get(SessionManager.KEY_USERID));
                Log.e("params", params.toString());
                return params;
            }
        };
        int socketTimeout = 30000;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        postRequest.setRetryPolicy(policy);
        queue.add(postRequest);
    }

    private void loadEducation() {
        progressDialog.setMessage("Please Wait..");
        progressDialog.setCancelable(true);
        progressDialog.show();

        StringRequest postRequest = new StringRequest(Request.Method.POST, EndPoints.LOADEDUCATIONLIST,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("response", response);
                        try {
                            educationarraylist.clear();
                            progressDialog.dismiss();
                            JSONObject object=new JSONObject(response);
                            String status=object.getString("status");
                            String message=object.getString("message");
                            if (status.equalsIgnoreCase("200") && message.equalsIgnoreCase("success"))
                            {
                                JSONObject data= object.getJSONObject("data");
                                JSONArray dataarray=data.getJSONArray("education");
                                for (int i = 0; i < dataarray.length(); i++)
                                {
                                    String educationname=dataarray.getString(i);
                                    educationarraylist.add(educationname);

                                }
                                educationLV.setAdapter(educationArrayAdapter);

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
                params.put("career", career);
                params.put("user_id", session.getUserId().get(SessionManager.KEY_USERID));
                Log.e("params", params.toString());
                return params;
            }
        };
        int socketTimeout = 30000;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        postRequest.setRetryPolicy(policy);
        queue.add(postRequest);
    }

    private void loadCommunity()
    {
        progressDialog.setMessage("Please Wait..");
        progressDialog.setCancelable(true);
        progressDialog.show();

        StringRequest postRequest = new StringRequest(Request.Method.POST, EndPoints.LOADCOMMUNITYLIST,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("response", response);
                        try {

                            communityarraylist.clear();
                            progressDialog.dismiss();
                            JSONObject object=new JSONObject(response);

                            String status=object.getString("status");
                            String message=object.getString("message");
                            if (status.equalsIgnoreCase("200") && message.equalsIgnoreCase("success"))
                            {
                                JSONObject data= object.getJSONObject("data");
                                JSONArray dataarray=data.getJSONArray("community");

                                for (int i = 0; i < dataarray.length(); i++)
                                {
                                    String communityname=dataarray.getString(i);
                                    communityarraylist.add(communityname);

                                }
                                communityLV.setAdapter(communityArrayAdapter);
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
                params.put("looking_for", looking_for);
                params.put("user_id", session.getUserId().get(SessionManager.KEY_USERID));

                Log.e("params", params.toString());
                return params;
            }
        };
        int socketTimeout = 30000;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        postRequest.setRetryPolicy(policy);
        queue.add(postRequest);
    }

    private void loadreligion()
    {
        progressDialog.setMessage("Please Wait..");
        progressDialog.setCancelable(true);
        progressDialog.show();

        StringRequest postRequest = new StringRequest(Request.Method.POST, EndPoints.LOADRELIGION,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("response", response);
                        try {

                            religionarraylist.clear();
                            progressDialog.dismiss();
                            JSONObject object=new JSONObject(response);

                            String status=object.getString("status");
                            String message=object.getString("message");
                            if (status.equalsIgnoreCase("200") && message.equalsIgnoreCase("success"))
                            {
                                JSONObject data= object.getJSONObject("data");
                                JSONArray dataarray=data.getJSONArray("religion");


                                for (int i = 0; i < dataarray.length(); i++)
                                {
                                    String religionname=dataarray.getString(i);
                                    religionarraylist.add(religionname);

                                }
                                religionLV.setAdapter(religionArrayAdapter);
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
                params.put("raised_in",raised_in);
                params.put("user_id", session.getUserId().get(SessionManager.KEY_USERID));
                Log.e("params", params.toString());
                return params;
            }
        };
        int socketTimeout = 30000;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        postRequest.setRetryPolicy(policy);
        queue.add(postRequest);
    }

    private void loadHeight()
    {
        progressDialog.setMessage("Please Wait..");
        progressDialog.setCancelable(true);
        progressDialog.show();

        StringRequest postRequest = new StringRequest(Request.Method.POST, EndPoints.LOADHEIGHT,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("response", response);
                        try {

                            heightarraylist.clear();
                            progressDialog.dismiss();
                            JSONObject object=new JSONObject(response);

                            String status=object.getString("status");
                            String message=object.getString("message");
                            if (status.equalsIgnoreCase("200") && message.equalsIgnoreCase("success"))
                            {
                                JSONObject data= object.getJSONObject("data");
                                JSONArray dataarray=data.getJSONArray("height");


                                for (int i = 0; i < dataarray.length(); i++)
                                {
                                    String heightname=dataarray.getString(i);
                                    heightarraylist.add(heightname);

                                }
                                heightLV.setAdapter(heightArrayAdapter);
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
                params.put("education", education);
                params.put("user_id", session.getUserId().get(SessionManager.KEY_USERID));
                Log.e("params", params.toString());
                return params;
            }
        };
        int socketTimeout = 30000;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        postRequest.setRetryPolicy(policy);
        queue.add(postRequest);
    }


    public void showFoodPrefrence() {
        if (!session.getLifestyle().get(SessionManager.KEY_FOODPREF).isEmpty()) {
            if (session.getLifestyle().get(SessionManager.KEY_FOODPREF).equalsIgnoreCase("veg")) {
                binding.vegLL.setBackground(getResources().getDrawable(R.drawable.gradientcurverectangle2));
                binding.nonvegLL.setBackground(getResources().getDrawable(R.drawable.greycurverectangle1));
                binding.vegTV.setTextColor(getResources().getColor(R.color.white));
                binding.nonvegTV.setTextColor(getResources().getColor(R.color.colorPrimary));

            } else {

                binding.nonvegLL.setBackground(getResources().getDrawable(R.drawable.gradientcurverectangle1));
                binding.vegLL.setBackground(getResources().getDrawable(R.drawable.greycurverectangle2));
                binding.nonvegTV.setTextColor(getResources().getColor(R.color.white));
                binding.vegTV.setTextColor(getResources().getColor(R.color.colorPrimary));


            }
        }


        if (!session.getLifestyle().get(SessionManager.KEY_SMOKE).isEmpty()) {
            if (session.getLifestyle().get(SessionManager.KEY_SMOKE).equalsIgnoreCase("smoke")) {
                binding.smokingLL.setBackground(getResources().getDrawable(R.drawable.gradientcurverectangle2));
                binding.nonsmokeLL.setBackground(getResources().getDrawable(R.drawable.greycurverectangle));
                binding.smokeocassionalLL.setBackground(getResources().getDrawable(R.drawable.greycurverectangle1));

                binding.smokeTV.setTextColor(getResources().getColor(R.color.white));
                binding.nonsmokeTV.setTextColor(getResources().getColor(R.color.colorPrimary));
                binding.smokeocassionalTV.setTextColor(getResources().getColor(R.color.colorPrimary));

            } else if (session.getLifestyle().get(SessionManager.KEY_SMOKE).equalsIgnoreCase("nonsmoke")) {

                binding.nonsmokeLL.setBackground(getResources().getDrawable(R.drawable.gradientcurverectangle));
                binding.smokingLL.setBackground(getResources().getDrawable(R.drawable.greycurverectangle2));
                binding.smokeocassionalLL.setBackground(getResources().getDrawable(R.drawable.greycurverectangle1));

                binding.nonsmokeTV.setTextColor(getResources().getColor(R.color.white));
                binding.smokeTV.setTextColor(getResources().getColor(R.color.colorPrimary));
                binding.smokeocassionalTV.setTextColor(getResources().getColor(R.color.colorPrimary));


            } else {
                binding.smokeocassionalLL.setBackground(getResources().getDrawable(R.drawable.gradientcurverectangle1));
                binding.smokingLL.setBackground(getResources().getDrawable(R.drawable.greycurverectangle2));
                binding.nonsmokeLL.setBackground(getResources().getDrawable(R.drawable.greycurverectangle));

                binding.smokeocassionalTV.setTextColor(getResources().getColor(R.color.white));
                binding.smokeTV.setTextColor(getResources().getColor(R.color.colorPrimary));
                binding.nonsmokeTV.setTextColor(getResources().getColor(R.color.colorPrimary));

            }
        }


        if (!session.getLifestyle().get(SessionManager.KEY_DRINK).isEmpty()) {
            if (session.getLifestyle().get(SessionManager.KEY_DRINK).equalsIgnoreCase("drink")) {
                binding.drinkLL.setBackground(getResources().getDrawable(R.drawable.gradientcurverectangle2));
                binding.nondrinkLL.setBackground(getResources().getDrawable(R.drawable.greycurverectangle));
                binding.drinkocassionalLL.setBackground(getResources().getDrawable(R.drawable.greycurverectangle1));

                binding.drinkTV.setTextColor(getResources().getColor(R.color.white));
                binding.nondrinkTV.setTextColor(getResources().getColor(R.color.colorPrimary));
                binding.drinkocassionalTV.setTextColor(getResources().getColor(R.color.colorPrimary));


            } else if (session.getLifestyle().get(SessionManager.KEY_DRINK).equalsIgnoreCase("nondrink")) {

                binding.nondrinkLL.setBackground(getResources().getDrawable(R.drawable.gradientcurverectangle));
                binding.drinkLL.setBackground(getResources().getDrawable(R.drawable.greycurverectangle2));
                binding.drinkocassionalLL.setBackground(getResources().getDrawable(R.drawable.greycurverectangle1));

                binding.nondrinkTV.setTextColor(getResources().getColor(R.color.white));
                binding.drinkTV.setTextColor(getResources().getColor(R.color.colorPrimary));
                binding.drinkocassionalTV.setTextColor(getResources().getColor(R.color.colorPrimary));


            } else {
                binding.drinkocassionalLL.setBackground(getResources().getDrawable(R.drawable.gradientcurverectangle1));
                binding.drinkLL.setBackground(getResources().getDrawable(R.drawable.greycurverectangle2));
                binding.nondrinkLL.setBackground(getResources().getDrawable(R.drawable.greycurverectangle));

                binding.drinkocassionalTV.setTextColor(getResources().getColor(R.color.white));
                binding.drinkTV.setTextColor(getResources().getColor(R.color.colorPrimary));
                binding.nondrinkTV.setTextColor(getResources().getColor(R.color.colorPrimary));

            }
        }


    }


    private class InterestAdapter extends RecyclerView.Adapter<InterestAdapter.CustomViewHodler>
    {

        private Context context;
        List<ChooseInterestDTO> interestDTOArrayList;

        public InterestAdapter(Context context, List<ChooseInterestDTO> interestDTOArrayList) {
            this.context = context;
            this.interestDTOArrayList = interestDTOArrayList;
        }

        @Override
        public CustomViewHodler onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_spinner, parent, false);
            return new CustomViewHodler(itemView);
        }

        @Override
        public void onBindViewHolder(final CustomViewHodler holder, final int position) {

            final ChooseInterestDTO chooseInterestDTO = interestDTOArrayList.get(position);
            holder.text.setText(chooseInterestDTO.getInterestName());
            holder.imageLL.setVisibility(chooseInterestDTO.isSelected() ? View.VISIBLE : View.INVISIBLE);
            holder.text.setTextColor(chooseInterestDTO.isSelected() ? getResources().getColor(R.color.colorPrimary) :
                    getResources().getColor(R.color.black));
            holder.text.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v)
                {
                    chooseInterestDTO.setSelected(!chooseInterestDTO.isSelected());
                    holder.imageLL.setVisibility(chooseInterestDTO.isSelected() ? View.VISIBLE : View.INVISIBLE);
                    holder.text.setTextColor(chooseInterestDTO.isSelected() ? getResources().getColor(R.color.colorPrimary) :
                            getResources().getColor(R.color.black));

                    if (chooseInterestDTO.isSelected())
                    {
                        interestlistsent.add(chooseInterestDTO.getInterestName());
                    }
                    else
                    {
                        interestlistsent.remove(chooseInterestDTO.getInterestName());

                    }

                    binding.rv.setAdapter(adp);
                    notifyDataSetChanged();
                    adp.notifyDataSetChanged();
                }
            });

        }

        @Override
        public int getItemCount() {
            return interestDTOArrayList == null ? 0 : interestDTOArrayList.size();
        }

        public class CustomViewHodler extends RecyclerView.ViewHolder {
            //            ImageView imageView;
            TextView text;
            LinearLayout ll, imageLL;

            public CustomViewHodler(View itemView) {
                super(itemView);
                text = itemView.findViewById(R.id.text);
                ll = itemView.findViewById(R.id.ll);
                imageLL = itemView.findViewById(R.id.imageLL);
            }
        }
    }





    private class SelectInterestAdp extends RecyclerView.Adapter<SelectInterestAdp.CustomViewHodler>
    {
        private Context mContext;
        List<String> interestlistsent;
        public SelectInterestAdp(Context context,  List<String> interestlistsent) {
            this.mContext = context;
            this.interestlistsent=interestlistsent;
        }

        @Override
        public CustomViewHodler onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.interestlayout, parent, false);
            return new CustomViewHodler(itemView);
        }

        @Override
        public void onBindViewHolder(final CustomViewHodler holder, final int position)
        {

            String name = interestlistsent.get(position);
            holder.text.setText(name);
            holder.text.setBackground(getResources().getDrawable(R.drawable.gradientbutton));
            holder.text.setTextColor(getResources().getColor(R.color.white));


        }

        @Override
        public int getItemCount() {
            return  interestlistsent == null ? 0 : interestlistsent.size();
        }

        public class CustomViewHodler extends RecyclerView.ViewHolder {
            //            ImageView imageView;
            TextView text;

            public CustomViewHodler(View itemView) {
                super(itemView);
                text = itemView.findViewById(R.id.text);
            }
        }
    }



    public class GridSpacingItemDecoration extends RecyclerView.ItemDecoration {

        private int spanCount;
        private int spacing;
        private boolean includeEdge;

        public GridSpacingItemDecoration(int spanCount, int spacing, boolean includeEdge) {
            this.spanCount = spanCount;
            this.spacing = spacing;
            this.includeEdge = includeEdge;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            int position = parent.getChildAdapterPosition(view); // item position
            int column = position % spanCount; // item column

            if (includeEdge) {
                outRect.left = spacing - column * spacing / spanCount; // spacing - column * ((1f / spanCount) * spacing)
                outRect.right = (column + 1) * spacing / spanCount; // (column + 1) * ((1f / spanCount) * spacing)

                if (position < spanCount) { // top edge
                    outRect.top = spacing;
                }
                outRect.bottom = spacing; // item bottom
            } else {
                outRect.left = column * spacing / spanCount; // column * ((1f / spanCount) * spacing)
                outRect.right = spacing - (column + 1) * spacing / spanCount; // spacing - (column + 1) * ((1f /    spanCount) * spacing)
                if (position >= spanCount) {
                    outRect.top = spacing; // item top
                }
            }
        }
    }

    /**
     * Converting dp to pixel
     */
    private int dpToPx(int dp) {
        Resources r = getResources();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics()));
    }


    public void login() {

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
            authenticationDialog = new AuthenticationDialog(getActivity(), this);
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
                Toast toast = Toast.makeText(getActivity().getApplicationContext(),"Ошибка входа!",Toast.LENGTH_LONG);
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
