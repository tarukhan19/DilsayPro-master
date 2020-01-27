package com.dbvertex.dilsayproject.Adapter;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.dbvertex.dilsayproject.EndPoints;
import com.dbvertex.dilsayproject.Model.DisLikeDTO;
import com.dbvertex.dilsayproject.R;
import com.dbvertex.dilsayproject.session.SessionManager;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class DislikeAdapter extends RecyclerView.Adapter<DislikeAdapter.CustomViewHodler> {

    private Context mContext;
    ArrayList<DisLikeDTO> disLikeDTOArrayList;
    long diff;
    Activity activity;
    SessionManager sessionManager;
    RequestQueue requestQueue;

    public DislikeAdapter(Context context, ArrayList<DisLikeDTO> disLikeDTOArrayList) {
        this.mContext = context;
        this.disLikeDTOArrayList = disLikeDTOArrayList;
        sessionManager = new SessionManager(context);
        activity = (Activity) context;
        requestQueue = Volley.newRequestQueue(context);
    }

    @Override
    public CustomViewHodler onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_dislike, parent, false);
        return new CustomViewHodler(itemView);
    }

    @Override
    public void onBindViewHolder(CustomViewHodler holder, final int position) {
        final DisLikeDTO disLikeDTO = disLikeDTOArrayList.get(position);

        Picasso.with(mContext).load(disLikeDTO.getImage())
                .into(holder.iv_pic);
        holder.nameageTV.setText(disLikeDTO.getName());
        holder.profcasteTV.setText(disLikeDTO.getProfession());

        holder.dislikeLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String pos=disLikeDTOArrayList.get(position).getId();

                disLikeDTOArrayList.remove(position);
                notifyDataSetChanged();
                notifyItemChanged(position);
                likeProfile(pos);
            }
        });

        holder.thinklaterLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String pos=disLikeDTOArrayList.get(position).getId();

                disLikeDTOArrayList.remove(position);
                notifyDataSetChanged();
                notifyItemChanged(position);
                thinklaterProfile(pos);
            }
        });

    }



    @Override
    public int getItemCount() {
        return disLikeDTOArrayList.size();
    }

    public class CustomViewHodler extends RecyclerView.ViewHolder {
        ImageView iv_pic;
        LinearLayout dislikeLL,thinklaterLL;
        TextView nameageTV,profcasteTV,distanceTV;
        CardView cardView;
        public CustomViewHodler(View itemView) {
            super(itemView);
            cardView=itemView.findViewById(R.id.cardView);
            iv_pic=(ImageView)itemView.findViewById(R.id.imageview);
            dislikeLL=itemView.findViewById(R.id.dislikeLL);
            thinklaterLL=itemView.findViewById(R.id.thinklaterLL);
            nameageTV=itemView.findViewById(R.id.nameageTV);
            profcasteTV=itemView.findViewById(R.id.profcasteTV);
            distanceTV=itemView.findViewById(R.id.distanceTV);
        }
    }


    private void likeProfile(final String position)
    {
        StringRequest postRequest = new StringRequest(Request.Method.POST, EndPoints.LIKE_PROFILE,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("response", response);
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
                params.put("liked_id",position );

                Log.e("params", params.toString());
                return params;
            }
        };
        int socketTimeout = 30000;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        postRequest.setRetryPolicy(policy);
        requestQueue.add(postRequest);
    }



    private void thinklaterProfile(final String position)
    {
        StringRequest postRequest = new StringRequest(Request.Method.POST, EndPoints.THINKLATER_PROFILE,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("response", response);
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
                params.put("think_later_id",position);

                Log.e("params", params.toString());
                return params;
            }
        };
        int socketTimeout = 30000;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        postRequest.setRetryPolicy(policy);
        requestQueue.add(postRequest);
    }


}


