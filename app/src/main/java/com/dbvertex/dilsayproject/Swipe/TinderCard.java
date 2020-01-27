package com.dbvertex.dilsayproject.Swipe;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.ImageView;
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
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.MultiTransformation;
import com.dbvertex.dilsayproject.EndPoints;
import com.dbvertex.dilsayproject.Fragment.HomeFragment;
import com.dbvertex.dilsayproject.ProfileDetailActivity;
import com.dbvertex.dilsayproject.R;
import com.dbvertex.dilsayproject.session.SessionManager;
import com.mindorks.placeholderview.SwipePlaceHolderView;
import com.mindorks.placeholderview.annotations.Click;
import com.mindorks.placeholderview.annotations.Layout;
import com.mindorks.placeholderview.annotations.Resolve;
import com.mindorks.placeholderview.annotations.View;
import com.mindorks.placeholderview.annotations.swipe.SwipeCancelState;
import com.mindorks.placeholderview.annotations.swipe.SwipeHead;
import com.mindorks.placeholderview.annotations.swipe.SwipeIn;
import com.mindorks.placeholderview.annotations.swipe.SwipeInState;
import com.mindorks.placeholderview.annotations.swipe.SwipeOut;
import com.mindorks.placeholderview.annotations.swipe.SwipeOutState;
import com.mindorks.placeholderview.annotations.swipe.SwipeView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jp.wasabeef.glide.transformations.BlurTransformation;
import jp.wasabeef.glide.transformations.RoundedCornersTransformation;


/**
 * Created by janisharali on 19/08/16.
 */
@Layout(R.layout.tinder_card_view)
public class TinderCard {

    RequestQueue queue;
    SessionManager sessionManager;

    @View(R.id.profileImageView)
    private ImageView profileImageView;

    @View(R.id.nameage)
    private TextView nameAgeTxt;

    @View(R.id.occupation)
    private TextView locationNameTxt;

    @View(R.id.distanceTV)
    private TextView distanceTV ;

    @View(R.id.instaverify)
    private TextView instaverify ;

    @View(R.id.likeIV)
    private ImageView likeIV;

    @View(R.id.dislikeIV)
    private ImageView dislikeIV;

    @View(R.id.thinklaterIV)
    private ImageView thinklaterIV;

    @SwipeView
    private android.view.View cardView;

    private Profile mProfile;
    private Context mContext;
    private SwipePlaceHolderView mSwipeView;
    List<Profile> mprofileArrayList;

  //  Activity activity;


    public TinderCard(Context context, Profile profile, SwipePlaceHolderView swipeView, List<Profile> profileArrayList) {
        mContext = context;
        mProfile = profile;
        mSwipeView = swipeView;
        mprofileArrayList=profileArrayList;
//        activity = (Activity) mContext;

        queue = Volley.newRequestQueue(context);
        sessionManager = new SessionManager(context);
    }

    @Resolve
    private void onResolved(){
        MultiTransformation multi = new MultiTransformation(
                new BlurTransformation(mContext, 30),
                new RoundedCornersTransformation(
                        mContext, Utils.dpToPx(7), 0,
                        RoundedCornersTransformation.CornerType.TOP));

        Glide.with(mContext).load(mProfile.getImageUrl())

                .into(profileImageView);
        nameAgeTxt.setText(mProfile.getName() + ", " + mProfile.getAge());
        locationNameTxt.setText(mProfile.getLocation());
        if (mProfile.getInstaverified().equalsIgnoreCase("0"))
        {
            instaverify.setText("Not Verified");
        }
        else
        {
            instaverify.setText("Verified");
        }
        Log.e("distance",Integer.parseInt(mProfile.getDistance())+"");
        distanceTV.setText(Integer.parseInt(mProfile.getDistance()) +" KM");

        likeIV.setOnClickListener(new android.view.View.OnClickListener() {
            @Override
            public void onClick(android.view.View view) {
                new HomeFragment().accept();

            }
        });

        dislikeIV.setOnClickListener(new android.view.View.OnClickListener() {
            @Override
            public void onClick(android.view.View view) {
                new HomeFragment().reject();

            }
        });

        thinklaterIV.setOnClickListener(new android.view.View.OnClickListener() {
            @Override
            public void onClick(android.view.View view) {

            }
        });


    }

    @SwipeHead
    private void onSwipeHeadCard() {
        Glide.with(mContext).load(mProfile.getImageUrl()).placeholder(R.color.gray)
                .into(profileImageView);
        cardView.invalidate();
    }

    @Click(R.id.profileImageView)
    private void onClick(){
        Intent intent = new Intent(mContext, ProfileDetailActivity.class);
        sessionManager.setProfileDetailUserId(mProfile.getId());
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        mContext.startActivity(intent);

    }

    @SwipeOut
    private void onSwipedOut(){
        Log.e("EVENT", "left");
        dislikeProfile();
    }

    @SwipeCancelState
    private void onSwipeCancelState(){
        Log.e("EVENT", "onSwipeCancelState");
    }

    @SwipeIn
    private void onSwipeIn(){
        Log.e("EVENT", "right");
        likeProfile();
    }

    @SwipeInState
    private void onSwipeInState(){
        Log.e("EVENT", "onSwipeInState");
    }

    @SwipeOutState
    private void onSwipeOutState(){
        Log.e("EVENT", "onSwipeOutState");
    }

    private void likeProfile()
    {
        StringRequest postRequest = new StringRequest(Request.Method.POST, EndPoints.LIKE_PROFILE,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("response", response);
                        try {


                            JSONObject object=new JSONObject(response);
// {"status":200,"message":"success","data":{"user_id":"243","liked_id":"236","created":"2019-08-06 21:57:24"}}
                            String status=object.getString("status");
                            String message=object.getString("message");
                            if (status.equalsIgnoreCase("200") && message.equalsIgnoreCase("success"))
                            {
                            sessionManager.setRewindId(mProfile.getId());
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
                params.put("liked_id",mProfile.getId() );

                Log.e("params", params.toString());
                return params;
            }
        };
        int socketTimeout = 30000;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        postRequest.setRetryPolicy(policy);
        queue.add(postRequest);
    }






    private void dislikeProfile()
    {
        StringRequest postRequest = new StringRequest(Request.Method.POST, EndPoints.DISLIKE_PROFILE,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("response", response);
                        try {


                            JSONObject object=new JSONObject(response);

                            String status=object.getString("status");
                            String message=object.getString("message");
                            if (status.equalsIgnoreCase("200") && message.equalsIgnoreCase("success"))
                            {
                                sessionManager.setRewindId(mProfile.getId());
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
                params.put("disliked_id",mProfile.getId() );

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
