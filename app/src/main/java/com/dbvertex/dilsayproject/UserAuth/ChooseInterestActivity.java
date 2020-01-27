package com.dbvertex.dilsayproject.UserAuth;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.dbvertex.dilsayproject.Adapter.ChooseInterestAdapter;
import com.dbvertex.dilsayproject.Adapter.EducationAdapter;
import com.dbvertex.dilsayproject.EndPoints;
import com.dbvertex.dilsayproject.Model.ChooseInterestDTO;
import com.dbvertex.dilsayproject.Model.CommunityDTO;
import com.dbvertex.dilsayproject.Network.ConnectivityReceiver;
import com.dbvertex.dilsayproject.Network.MyApplication;
import com.dbvertex.dilsayproject.R;
import com.dbvertex.dilsayproject.databinding.ActivityChooseInterestBinding;
import com.dbvertex.dilsayproject.session.SessionManager;
import com.google.android.material.snackbar.Snackbar;
import com.xiaofeng.flowlayoutmanager.FlowLayoutManager;
import com.zhy.view.flowlayout.FlowLayout;
import com.zhy.view.flowlayout.TagAdapter;
import com.zhy.view.flowlayout.TagFlowLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChooseInterestActivity extends AppCompatActivity implements
        ConnectivityReceiver.ConnectivityReceiverListener {
    private List<String> interestDTOList, selectInterestDTOList;
    LinearLayout nextLL;
    ProgressDialog progressDialog;
    RequestQueue queue;
    SessionManager sessionManager;
    LayoutInflater mInflater;
    String selectedInterest;
    boolean isConnected;
    TextView textView, textview1;
    ActivityChooseInterestBinding binding;
    InterestAdapter adapter;
    ChooseInterestAdapter adapter1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_interest);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_choose_interest);

        mInflater = LayoutInflater.from(ChooseInterestActivity.this);
        textView = findViewById(R.id.text);
        textview1 = findViewById(R.id.textview);
        progressDialog = new ProgressDialog(ChooseInterestActivity.this, R.style.CustomDialog);
        queue = Volley.newRequestQueue(ChooseInterestActivity.this);
        sessionManager = new SessionManager(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_main);
        LinearLayout back = toolbar.findViewById(R.id.back_LL);
        TextView toolbar_title = toolbar.findViewById(R.id.titleTV);
        toolbar_title.setText("Choose Interest");


        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ChooseInterestActivity.this, LifestyleActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                overridePendingTransition(R.anim.trans_left_in,
                        R.anim.trans_left_out);
            }
        });

        nextLL = findViewById(R.id.nextLL);
        interestDTOList = new ArrayList<>();


        adapter = new InterestAdapter(this, interestDTOList);
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this, 3);
        binding.interestListRV.setLayoutManager(mLayoutManager);
        binding.interestListRV.addItemDecoration(new GridSpacingItemDecoration(2, dpToPx(5), true));
        binding.interestListRV.setItemAnimator(new DefaultItemAnimator());
        binding.interestListRV.setAdapter(adapter);
        adapter.notifyDataSetChanged();





        nextLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (selectInterestDTOList.size() == 0) {
                    Toast.makeText(ChooseInterestActivity.this, "Choose Interest", Toast.LENGTH_SHORT).show();
                } else {

                    StringBuilder sbString = new StringBuilder("");
                    for (String language : selectInterestDTOList) {
                        sbString.append(language).append(",");
                    }

                    selectedInterest = sbString.toString();
                    if (selectedInterest.length() > 0)
                        selectedInterest = selectedInterest.substring(0, selectedInterest.length() - 1);
                    sessionManager.setInterest(selectedInterest);
                    Intent intent = new Intent(ChooseInterestActivity.this, MyLocationActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    overridePendingTransition(R.anim.trans_left_in,
                            R.anim.trans_left_out);

                }

            }
        });


    }

    private void loadInterest()
    {
        progressDialog.setMessage("Please Wait..");
        progressDialog.setCancelable(true);
        progressDialog.show();

        StringRequest postRequest = new StringRequest(Request.Method.POST, EndPoints.INTERESELIST,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {
                        Log.e("response",response);

                        try {
                            interestDTOList.clear();
//                            selectInterestDTOList.clear();
                            progressDialog.dismiss();
                            JSONObject object = new JSONObject(response);

                            String status = object.getString("status");
                            String message = object.getString("message");
                            JSONObject jsonObject = object.getJSONObject("data");
                            if (status.equalsIgnoreCase("200") && message.equalsIgnoreCase("success"))
                            {
                                JSONArray dataarray = jsonObject.getJSONArray("interest");

                                selectedInterest=jsonObject.getString("intrest_saved");
                                if (!selectedInterest.isEmpty())
                                {
                                    binding.selectinterestListRV.setVisibility(View.VISIBLE);
                                    binding.text.setVisibility(View.GONE);
                                    selectInterestDTOList = new ArrayList<String>(Arrays.asList(selectedInterest.split("\\s*,\\s*")) );

                                    boolean isAdd=false;
                                    for (int i=0;i<dataarray.length();i++)
                                    {
                                        isAdd=false;
                                        String interest=dataarray.getString(i);
                                        for (int j=0;j<selectInterestDTOList.size();j++)
                                        {
                                            if (selectInterestDTOList.get(j).equalsIgnoreCase(interest))
                                            {
                                                isAdd=false;
                                                break;
                                            }
                                            else
                                                isAdd=true;

                                        }
                                        if (isAdd)
                                        {
                                            interestDTOList.add(interest);
                                        }
                                    }

                                }
                                else
                                {
                                    selectInterestDTOList = new ArrayList<>();
                                    for (int i = 0; i < dataarray.length(); i++) {
                                        String interest = dataarray.getString(i);
                                        interestDTOList.add(interest);
                                    }
                                }

                                adapter1 = new ChooseInterestActivity.ChooseInterestAdapter(ChooseInterestActivity.this, selectInterestDTOList);
                                RecyclerView.LayoutManager mLayoutManager1 = new GridLayoutManager(ChooseInterestActivity.this, 3);
                                binding.selectinterestListRV.setLayoutManager(mLayoutManager1);
                                binding.selectinterestListRV.addItemDecoration(new GridSpacingItemDecoration(2, dpToPx(5), true));
                                binding.selectinterestListRV.setItemAnimator(new DefaultItemAnimator());
                                binding.selectinterestListRV.setAdapter(adapter1);
                                adapter1.notifyDataSetChanged();

                                binding.interestListRV.setAdapter(adapter);
                                binding.selectinterestListRV.setAdapter(adapter1);
                                adapter.notifyDataSetChanged();
                                adapter1.notifyDataSetChanged();

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
                params.put("user_id", sessionManager.getUserId().get(SessionManager.KEY_USERID));
                params.put("food_prefrence", sessionManager.getLifestyle().get(SessionManager.KEY_FOODPREF));
                params.put("smoke", sessionManager.getLifestyle().get(SessionManager.KEY_SMOKE));
                params.put("drinking", sessionManager.getLifestyle().get(SessionManager.KEY_DRINK));
                return params;
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
        Intent intent = new Intent(ChooseInterestActivity.this, LifestyleActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        startActivity(intent);

        overridePendingTransition(R.anim.trans_left_in,
                R.anim.trans_left_out);

    }


    @Override
    public void onStart() {
        isConnected = ConnectivityReceiver.isConnected();
        if (!isConnected) {
            showSnack(isConnected);
        } else {
            loadInterest();
        }
        super.onStart();
    }

    // Showing the status in Snackbar
    private void showSnack(boolean isConnected) {
        String message;
        int color;

        if (isConnected) {
            message = "Good! Connected to Internet";
            color = Color.WHITE;

        } else {
            message = "Sorry! Not connected to internet";
            color = Color.RED;

        }

        Snackbar snackbar = Snackbar
                .make(findViewById(R.id.ll), message, Snackbar.LENGTH_INDEFINITE).setAction("Retry", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        loadInterest();
                    }
                });

        View sbView = snackbar.getView();
        TextView textView = (TextView) sbView.findViewById(R.id.snackbar_text);
        textView.setTextColor(color);
        snackbar.show();
    }

    @Override
    public void onResume() {
        super.onResume();

        // register connection status listener
        MyApplication.getInstance().setConnectivityListener(this);
    }

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        this.isConnected = isConnected;
        showSnack(isConnected);


    }


    private class InterestAdapter extends RecyclerView.Adapter<InterestAdapter.CustomViewHodler>
    {

        private Context context;
        List<String> interestDTOArrayList;

        public InterestAdapter(Context context, List<String> interestDTOArrayList) {
            this.context = context;
            this.interestDTOArrayList = interestDTOArrayList;
        }

        @Override
        public CustomViewHodler onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.recycle_tv, parent, false);
            return new CustomViewHodler(itemView);
        }

        @Override
        public void onBindViewHolder(final CustomViewHodler holder, final int position) {

            final String chooseInterestDTO = interestDTOArrayList.get(position);
            holder.text.setText("#"+chooseInterestDTO+" +");

            holder.text.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v)
                {
                    String select = interestDTOList.get(position).toString();
                    selectInterestDTOList.add(select);
                    interestDTOList.remove(position);
                    binding.selectinterestListRV.setVisibility(View.VISIBLE);
                    binding.text.setVisibility(View.GONE);
                    if (interestDTOList.size() == 0) {
                        textview1.setVisibility(View.VISIBLE);
                        binding.interestListRV.setVisibility(View.GONE);

                    } else {
                        textview1.setVisibility(View.GONE);
                       binding.interestListRV.setVisibility(View.VISIBLE);
                    }
                    notifyDataSetChanged();
                    adapter1.notifyDataSetChanged();

//
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


    private class ChooseInterestAdapter extends RecyclerView.Adapter<ChooseInterestAdapter.CustomViewHodler>
    {

        private Context context;
        List<String> selectInterestDTOList;

        public ChooseInterestAdapter(Context context, List<String> selectInterestDTOList) {
            this.context = context;
            this.selectInterestDTOList = selectInterestDTOList;
        }

        @Override
        public CustomViewHodler onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.recycle_tv, parent, false);
            return new CustomViewHodler(itemView);
        }

        @Override
        public void onBindViewHolder(final CustomViewHodler holder, final int position) {

            final String chooseInterestDTO = selectInterestDTOList.get(position);
            Log.e("chooseInterestDTO",chooseInterestDTO);
            holder.text.setBackground(getResources().getDrawable(R.drawable.gradientbutton));
            holder.text.setTextColor(getResources().getColor(R.color.white));
            holder.text.setText("#"+chooseInterestDTO+" X");

            holder.text.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v)
                {
                    String unselect = selectInterestDTOList.get(position).toString();
                    interestDTOList.add(unselect);
                    selectInterestDTOList.remove(position);
                    binding.interestListRV.setVisibility(View.VISIBLE);
                    binding.textview.setVisibility(View.GONE);
                    if (selectInterestDTOList.size() == 0) {
                        textView.setVisibility(View.VISIBLE);
                        binding.selectinterestListRV.setVisibility(View.GONE);
                    } else {
                        textView.setVisibility(View.GONE);
                        binding.selectinterestListRV.setVisibility(View.VISIBLE);
                    }
                    notifyDataSetChanged();
                    adapter.notifyDataSetChanged();

//
                }
            });

        }

        @Override
        public int getItemCount() {
            return selectInterestDTOList == null ? 0 : selectInterestDTOList.size();
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



}
