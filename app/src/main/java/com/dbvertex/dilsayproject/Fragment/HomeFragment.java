package com.dbvertex.dilsayproject.Fragment;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.dbvertex.dilsayproject.EndPoints;
import com.dbvertex.dilsayproject.R;
import com.dbvertex.dilsayproject.Swipe.Profile;
import com.dbvertex.dilsayproject.Swipe.TinderCard;
import com.dbvertex.dilsayproject.Swipe.Utils;
import com.dbvertex.dilsayproject.UserAuth.CarrerActivity;
import com.dbvertex.dilsayproject.UserAuth.HomePageActivity;
import com.dbvertex.dilsayproject.session.SessionManager;
import com.mindorks.placeholderview.SwipeDecor;
import com.mindorks.placeholderview.SwipePlaceHolderView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


public class HomeFragment extends Fragment {

    private SwipePlaceHolderView mSwipeView;
    private Context mContext;
    ImageView menu,iv_pic,redo;
    ProgressDialog progressDialog;
    RequestQueue queue;
    SessionManager sessionManager;
    static ImageButton rejectBtn, acceptBtn, undoBtn;
    Intent intent;

    public HomeFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_home, container, false);
        mSwipeView = (SwipePlaceHolderView) view.findViewById(R.id.swipeView);
        progressDialog = new ProgressDialog(getActivity(), R.style.CustomDialog);
        queue = Volley.newRequestQueue(getActivity());
        sessionManager = new SessionManager(getActivity());

        intent=getActivity().getIntent();

        rejectBtn = view.findViewById(R.id.rejectBtn);
        acceptBtn = view.findViewById(R.id.acceptBtn);
        undoBtn = view.findViewById(R.id.undoBtn);
        redo=view.findViewById(R.id.redo);
        iv_pic=view.findViewById(R.id.iv_pic);
        mContext = getActivity().getApplicationContext();
        int bottomMargin = Utils.dpToPx(35);
        menu = view.findViewById(R.id.menu);
        menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ((HomePageActivity) getActivity()).clickEvents();
                //homePageActivity.clickEvents();
            }
        });
        Point windowSize = Utils.getDisplaySize(getActivity().getWindowManager());


        Glide.with(mContext).load(sessionManager.getFacebookData().get(SessionManager.KEY_FACEBOOKDP)).placeholder(R.color.gray)
                .into(iv_pic);
        mSwipeView.getBuilder()
                .setDisplayViewCount(3)
                .setIsUndoEnabled(true)
                .setHeightSwipeDistFactor(10)
                .setWidthSwipeDistFactor(5)
                .setSwipeDecor(new SwipeDecor()
                        .setViewWidth(windowSize.x)
                        .setViewHeight(windowSize.y - bottomMargin)
                        .setViewGravity(Gravity.TOP)
                        .setPaddingTop(20)
                        .setRelativeScale(0.01f)
                        .setSwipeMaxChangeAngle(2f)
                        .setSwipeInMsgLayoutId(R.layout.tinder_swipe_in_msg_view)
                        .setSwipeOutMsgLayoutId(R.layout.tinder_swipe_out_msg_view));
        if (!sessionManager.getLocation().get(SessionManager.KEY_LATITUDE).isEmpty()) {
            if (intent.hasExtra("from"))
            {
                Utils.submitFilter(getActivity().getApplicationContext(), sessionManager, queue, mSwipeView);

            }
            else
            {
                Utils.loadProfiles(getActivity().getApplicationContext(), sessionManager, queue, mSwipeView);

            }

        }


        rejectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSwipeView.doSwipe(false);
            }
        });

        acceptBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSwipeView.doSwipe(true);
            }
        });


        redo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                redoProfile();
                mSwipeView.undoLastSwipe();
            }
        });
        return view;
    }

    public void reject() {
        rejectBtn.performClick();
    }


    public void accept() {
        acceptBtn.performClick();
    }


    public void thinklater() {
        Utils.removeProfile(getActivity().getApplicationContext(),  mSwipeView);
    }




    private void redoProfile()
    {
        StringRequest postRequest = new StringRequest(Request.Method.POST, EndPoints.REDO_PROFILE,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("REDO_PROFILEresponse", response);
                        try {


                            JSONObject object=new JSONObject(response);

                            String status=object.getString("status");
                            String message=object.getString("message");
                            if (status.equalsIgnoreCase("1") && message.equalsIgnoreCase("success"))
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
                params.put("rewind_id",sessionManager.getRewindId().get(SessionManager.KEY_REWINDID));

                Log.e("REDO_PROFILEparams", params.toString());
                return params;
            }
        };
        int socketTimeout = 30000;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        postRequest.setRetryPolicy(policy);
        queue.add(postRequest);
    }


}
