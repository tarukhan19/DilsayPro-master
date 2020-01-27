package com.dbvertex.dilsayproject.Filter;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.crystal.crystalrangeseekbar.interfaces.OnRangeSeekbarChangeListener;
import com.crystal.crystalrangeseekbar.interfaces.OnRangeSeekbarFinalValueListener;
import com.crystal.crystalrangeseekbar.widgets.CrystalRangeSeekbar;
import com.dbvertex.dilsayproject.EndPoints;
import com.dbvertex.dilsayproject.HideKeyboard;
import com.dbvertex.dilsayproject.Model.HeightDTO;
import com.dbvertex.dilsayproject.R;
import com.dbvertex.dilsayproject.UserAuth.CarrerActivity;
import com.dbvertex.dilsayproject.UserAuth.CommunityActivity;
import com.dbvertex.dilsayproject.UserAuth.EducationActivity;
import com.dbvertex.dilsayproject.UserAuth.HomeActivity;
import com.dbvertex.dilsayproject.UserAuth.HomePageActivity;
import com.dbvertex.dilsayproject.UserAuth.LookingForActivity;
import com.dbvertex.dilsayproject.UserAuth.RaisedInActivity;
import com.dbvertex.dilsayproject.UserAuth.ReligionActivity;
import com.dbvertex.dilsayproject.databinding.ActivityFilterBinding;
import com.dbvertex.dilsayproject.session.SessionManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class FilterActivity extends AppCompatActivity {
    ActivityFilterBinding binding;
    SessionManager sessionManager;
    RequestQueue queue;
    ProgressDialog progressDialog;

    RecyclerView minHeightrv, maxHeightrv;
    Dialog minHeightDialog, maxHeightDialog;
    AlertDialog.Builder minHeightbuilder, maxHeightbuilder;
    View minHeightView, maxHeightView;

    ArrayList<HeightDTO> minHeightDTOArrayList, maxHeightDTOArrayList;

    MinHeightAdapter minHeightAdapter;
    MaxHeightAdapter maxHeightAdapter;
    String startage,endage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_filter);
        if (Build.VERSION.SDK_INT >= 21)
        {
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(this.getResources().getColor(R.color.colorPrimary));
        }
        queue = Volley.newRequestQueue(this);
        sessionManager = new SessionManager(getApplicationContext());
        progressDialog = new ProgressDialog(this);
        minHeightDTOArrayList=new ArrayList<>();
        maxHeightDTOArrayList=new ArrayList<>();

        minHeightAdapter = new MinHeightAdapter(this, minHeightDTOArrayList);
        maxHeightAdapter = new MaxHeightAdapter(this, maxHeightDTOArrayList);

        minHeightbuilder = new AlertDialog.Builder(this);
        minHeightView = LayoutInflater.from(this).inflate(R.layout.item_rv, null);
        minHeightbuilder.setView(minHeightView);
        minHeightDialog = minHeightbuilder.create();
        minHeightrv = (RecyclerView) minHeightView.findViewById(R.id.rv);
        minHeightrv.setLayoutManager(new LinearLayoutManager(this));
        minHeightrv.setHasFixedSize(true);
        minHeightrv.setAdapter(minHeightAdapter);

        binding.minheightLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                minHeightbuilder.setView(minHeightrv);
                minHeightDialog.show();
            }
        });


        maxHeightbuilder = new AlertDialog.Builder(this);
        maxHeightView = LayoutInflater.from(this).inflate(R.layout.item_rv, null);
        maxHeightbuilder.setView(maxHeightView);
        maxHeightDialog = maxHeightbuilder.create();
        maxHeightrv = (RecyclerView) maxHeightView.findViewById(R.id.rv);
        maxHeightrv.setLayoutManager(new LinearLayoutManager(this));
        maxHeightrv.setHasFixedSize(true);
        maxHeightrv.setAdapter(maxHeightAdapter);

        binding.maxheightLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                maxHeightbuilder.setView(maxHeightrv);
                maxHeightDialog.show();
            }
        });

        binding.agerangeSeekbar.setOnRangeSeekbarChangeListener(new OnRangeSeekbarChangeListener() {
            @Override
            public void valueChanged(Number minValue, Number maxValue)
            {
                binding.ageMinVal.setText(String.valueOf(minValue));
                binding.ageMaxVal.setText(String.valueOf(maxValue));
                startage=String.valueOf(maxValue);
                endage=String.valueOf(minValue);
            }
        });


        binding.religionLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(FilterActivity.this, ReligionActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intent.putExtra("from", "filter");
                startActivity(intent);

                overridePendingTransition(R.anim.trans_left_in,
                        R.anim.trans_left_out);
            }
        });

        binding.communityLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(FilterActivity.this, CommunityActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intent.putExtra("from", "filter");
                startActivity(intent);

                overridePendingTransition(R.anim.trans_left_in,
                        R.anim.trans_left_out);
            }
        });

        binding.educationLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(FilterActivity.this, EducationActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intent.putExtra("from", "filter");
                startActivity(intent);

                overridePendingTransition(R.anim.trans_left_in,
                        R.anim.trans_left_out);
            }
        });

        binding.careerLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(FilterActivity.this, CarrerActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intent.putExtra("from", "filter");
                startActivity(intent);

                overridePendingTransition(R.anim.trans_left_in,
                        R.anim.trans_left_out);
            }
        });

        binding.raisedinLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(FilterActivity.this, RaisedInActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intent.putExtra("from", "filter");
                startActivity(intent);

                overridePendingTransition(R.anim.trans_left_in,
                        R.anim.trans_left_out);
            }
        });

        binding.genderLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(FilterActivity.this, LookingForActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intent.putExtra("from", "filter");
                startActivity(intent);

                overridePendingTransition(R.anim.trans_left_in,
                        R.anim.trans_left_out);
            }
        });

        binding.submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                sessionManager.setFilterStartEndAge(startage,endage);
                Intent intent = new Intent(FilterActivity.this, HomePageActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intent.putExtra("from", "filter");
                startActivity(intent);
                overridePendingTransition(R.anim.trans_left_in,
                        R.anim.trans_left_out);
            }
        });

        if (!sessionManager.getFilterReligion().get(SessionManager.KEY_FILTER_RELIGION_NAME).isEmpty()) {
            binding.reliogionTV.setText(sessionManager.getFilterReligion().get(SessionManager.KEY_FILTER_RELIGION_NAME));
        }
        if (!sessionManager.getFilterMinHeight().get(SessionManager.KEY_FILTER_MIN_HEIGHT).isEmpty()) {
            binding.minHeightTV.setText(sessionManager.getFilterMinHeight().get(SessionManager.KEY_FILTER_MIN_HEIGHT));
        }
        if (!sessionManager.getFilterMaxHeight().get(SessionManager.KEY_FILTER_MAX_HEIGHT).isEmpty()) {
            binding.maxHeightTV.setText(sessionManager.getFilterMaxHeight().get(SessionManager.KEY_FILTER_MAX_HEIGHT));
        }
        if (!sessionManager.getFilterCommunity().get(SessionManager.KEY_FILTER_COMMUNITY).isEmpty())
        {
            binding.communityTV.setText(sessionManager.getFilterCommunity().get(SessionManager.KEY_FILTER_COMMUNITY));
        }
        if (!sessionManager.getFilterEducation().get(SessionManager.KEY_FILTER_EDUCATION).isEmpty())
        {
            binding.educationTV.setText(sessionManager.getFilterEducation().get(SessionManager.KEY_FILTER_EDUCATION));
        }
        if (!sessionManager.getFilterCareer().get(SessionManager.KEY_FILTER_CAREER).isEmpty())
        {
            binding.careerTV.setText(sessionManager.getFilterCareer().get(SessionManager.KEY_FILTER_CAREER));
        }
        if (!sessionManager.getFilterRaisedIn().get(SessionManager.KEY_FILTER_RAISEDIN).isEmpty())
        {
            binding.raisedinTV.setText(sessionManager.getFilterRaisedIn().get(SessionManager.KEY_FILTER_RAISEDIN));
        }
        if (!sessionManager.getFilterLookingFor().get(SessionManager.KEY_FILTER_LOOKINGFOR).isEmpty())
        {
            if (sessionManager.getFilterLookingFor().get(SessionManager.KEY_FILTER_LOOKINGFOR).equalsIgnoreCase("F"))
            {
                binding.genderTV.setText("Female");
            }
            else
            {
                binding.genderTV.setText("Male");
            }
        }
        loadHeight();
    }


    private void loadHeight() {
        progressDialog.setMessage("Please Wait..");
        progressDialog.setCancelable(true);
        progressDialog.show();

        StringRequest postRequest = new StringRequest(Request.Method.POST, EndPoints.LOADHEIGHT,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("response", response);
                        try {

                            progressDialog.dismiss();
                            JSONObject object = new JSONObject(response);

                            String status = object.getString("status");
                            String message = object.getString("message");
                            if (status.equalsIgnoreCase("200") && message.equalsIgnoreCase("success")) {
                                JSONObject data = object.getJSONObject("data");
                                JSONArray dataarray = data.getJSONArray("height");

                                for (int i = 0; i < dataarray.length(); i++) {
                                    HeightDTO heightDTO=new HeightDTO();
                                    String heightname = dataarray.getString(i);
                                    heightDTO.setHeightName(heightname);
                                    minHeightDTOArrayList.add(heightDTO);
                                    maxHeightDTOArrayList.add(heightDTO);

                                }
                                maxHeightAdapter.notifyDataSetChanged();
                                minHeightAdapter.notifyDataSetChanged();

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
                params.put("education", sessionManager.getEducation().get(SessionManager.KEY_EDUCATION));
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

    private class MinHeightAdapter extends RecyclerView.Adapter<MinHeightAdapter.CustomViewHodler>
    {
        private Context mContext;
        ArrayList<HeightDTO> minheightDTOS;
        ArrayList<String> minheightList;

        public MinHeightAdapter(Context mContext, ArrayList<HeightDTO> minheightDTOS) {
            this.mContext = mContext;
            this.minheightDTOS = minheightDTOS;
            minheightList = new ArrayList<>();
        }

        @Override
        public CustomViewHodler onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_cameraspinner, parent, false);
            return new CustomViewHodler(itemView);
        }

        @Override
        public void onBindViewHolder(final CustomViewHodler holder, final int position)
        {

            final HeightDTO heightDTO = minheightDTOS.get(position);
            holder.text.setText(heightDTO.getHeightName());
            holder.text.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v)
                {
                    binding.minHeightTV.setText(heightDTO.getHeightName());
                    minHeightDialog.dismiss();
                    sessionManager.setFilterMinHeight(heightDTO.getHeightName());
                }
            });

        }

        @Override
        public int getItemCount() {
            return minheightDTOS == null ? 0 : minheightDTOS.size();
        }

        public class CustomViewHodler extends RecyclerView.ViewHolder {
            //            ImageView imageView;
            TextView text;
            LinearLayout ll;

            public CustomViewHodler(View itemView) {
                super(itemView);
                text = itemView.findViewById(R.id.text);
                ll = itemView.findViewById(R.id.ll);
            }
        }
    }


    private class MaxHeightAdapter extends RecyclerView.Adapter<MaxHeightAdapter.CustomViewHodler> {
        private Context mContext;
        ArrayList<HeightDTO> maxheightDTOS;
        ArrayList<String> maxheightList;

        public MaxHeightAdapter(Context mContext, ArrayList<HeightDTO> maxheightDTOS) {
            this.mContext = mContext;
            this.maxheightDTOS = maxheightDTOS;
            maxheightList = new ArrayList<>();
        }

        @Override
        public CustomViewHodler onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_cameraspinner, parent, false);
            return new CustomViewHodler(itemView);
        }

        @Override
        public void onBindViewHolder(final CustomViewHodler holder, final int position) {

            final HeightDTO heightDTO = maxheightDTOS.get(position);
            holder.text.setText(heightDTO.getHeightName());
            holder.text.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    binding.maxHeightTV.setText(heightDTO.getHeightName());
                    maxHeightDialog.dismiss();
                    sessionManager.setFilterMaxHeight(heightDTO.getHeightName());


                }
            });

        }

        @Override
        public int getItemCount() {
            return maxheightDTOS == null ? 0 : maxheightDTOS.size();
        }

        public class CustomViewHodler extends RecyclerView.ViewHolder {
            //            ImageView imageView;
            TextView text;
            LinearLayout ll;

            public CustomViewHodler(View itemView) {
                super(itemView);
                text = itemView.findViewById(R.id.text);
                ll = itemView.findViewById(R.id.ll);
            }
        }
    }


}
