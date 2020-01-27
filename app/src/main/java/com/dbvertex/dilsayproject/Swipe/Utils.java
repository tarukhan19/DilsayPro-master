package com.dbvertex.dilsayproject.Swipe;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.graphics.Point;
import android.os.Build;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.dbvertex.dilsayproject.EndPoints;
import com.dbvertex.dilsayproject.Model.HeightDTO;
import com.dbvertex.dilsayproject.session.SessionManager;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mindorks.placeholderview.SwipePlaceHolderView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by janisharali on 21/08/16.
 */
public class Utils {

    private static final String TAG = "Utils";

    public static void loadProfiles(final Context context, final SessionManager sessionManager, RequestQueue queue,
                                    final SwipePlaceHolderView mSwipeView){


        final List<Profile> profileList = new ArrayList<>();
        StringRequest postRequest = new StringRequest(Request.Method.POST, EndPoints.DASHBOARD,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("DASHBOARDresponse", response);
                        try {


                            //profileList.clear();
                            JSONObject object=new JSONObject(response);

                            String status=object.getString("status");
                            String message=object.getString("message");
                            if (status.equalsIgnoreCase("200") && message.equalsIgnoreCase("success"))
                            {
                                JSONArray dataarray= object.getJSONArray("data");
                                for (int i=0;i<dataarray.length();i++)
                                {
                                    JSONObject obj=dataarray.getJSONObject(i);

                                    Profile profile = new Profile();
                                    profile.setImageUrl(obj.getString("fb_image"));
                                    profile.setName(obj.getString("name"));
                                    profile.setAge(obj.getString("age"));
                                    profile.setLocation(obj.getString("email"));
                                    profile.setDistance(obj.getString("distance"));
                                    profile.setId(obj.getString("id"));
                                    profile.setInstaverified(obj.getString("insta_verified"));

                                    profileList.add(profile);

                                    mSwipeView.addView(new TinderCard(context, profile, mSwipeView,profileList));

                                }
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
                params.put("latitude", sessionManager.getLocation().get(SessionManager.KEY_LATITUDE));
                params.put("longitude", sessionManager.getLocation().get(SessionManager.KEY_LONGITUDE));
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

    public static void submitFilter(final Context context, final SessionManager sessionManager, RequestQueue queue,
                                    final SwipePlaceHolderView mSwipeView)
    {

        final List<Profile> profileList = new ArrayList<>();

        StringRequest postRequest = new StringRequest(Request.Method.POST, EndPoints.FILTER,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("submitFilter", response);
                        try {


                            //profileList.clear();
                            JSONObject object=new JSONObject(response);

                            String status=object.getString("status");
                            String message=object.getString("message");
                            if (status.equalsIgnoreCase("200") && message.equalsIgnoreCase("success"))
                            {
                                JSONArray dataarray= object.getJSONArray("data");
                                for (int i=0;i<dataarray.length();i++)
                                {
                                    JSONObject obj=dataarray.getJSONObject(i);

                                    Profile profile = new Profile();
                                    profile.setImageUrl(obj.getString("fb_image"));
                                    profile.setName(obj.getString("name"));
                                    profile.setAge(obj.getString("age"));
                                    profile.setLocation(obj.getString("email"));
                                    profile.setDistance(obj.getString("distance"));
                                    profile.setId(obj.getString("id"));
                                    profile.setInstaverified(obj.getString("insta_verified"));

                                    profileList.add(profile);

                                    mSwipeView.addView(new TinderCard(context, profile, mSwipeView,profileList));

                                }
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
                params.put("height_start", sessionManager.getFilterMinHeight().get(SessionManager.KEY_FILTER_MIN_HEIGHT));
                params.put("height_end", sessionManager.getFilterMinHeight().get(SessionManager.KEY_FILTER_MAX_HEIGHT));
                params.put("start_age", sessionManager.getFilterStartEndAge().get(SessionManager.KEY_FILTER_STARTAGE));
                params.put("end_age", sessionManager.getFilterStartEndAge().get(SessionManager.KEY_FILTER_ENDAGE));
                params.put("religion", sessionManager.getFilterReligion().get(SessionManager.KEY_FILTER_RELIGION_NAME));
                params.put("gender", sessionManager.getFilterLookingFor().get(SessionManager.KEY_FILTER_LOOKINGFOR));
                params.put("raised_in", sessionManager.getFilterRaisedIn().get(SessionManager.KEY_FILTER_RAISEDIN));

                //params.put("user_id", sessionManager.getUserId().get(SessionManager.KEY_USERID));
                Log.e("params", params.toString());
                return params;
            }
        };
        int socketTimeout = 30000;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        postRequest.setRetryPolicy(policy);
        queue.add(postRequest);
    }


    public static void removeProfile(Context applicationContext,  SwipePlaceHolderView mSwipeView) {
        mSwipeView.removeAllViews();
    }

    public static Point getDisplaySize(WindowManager windowManager)
    {
        try {
            if(Build.VERSION.SDK_INT > 16)
            {
                Display display = windowManager.getDefaultDisplay();
                DisplayMetrics displayMetrics = new DisplayMetrics();
                display.getMetrics(displayMetrics);
                return new Point(displayMetrics.widthPixels, displayMetrics.heightPixels);
            }else
            {
                return new Point(0, 0);
            }
        }catch (Exception e){
            e.printStackTrace();
            return new Point(0, 0);
        }
    }

    public static int dpToPx(int dp) {
        return (int) (dp * Resources.getSystem().getDisplayMetrics().density);
    }


}
