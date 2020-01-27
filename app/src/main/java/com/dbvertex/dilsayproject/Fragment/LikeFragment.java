package com.dbvertex.dilsayproject.Fragment;


import android.app.ProgressDialog;
import android.content.res.Resources;
import android.graphics.Rect;
import android.os.Bundle;

import androidx.annotation.Nullable;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.dbvertex.dilsayproject.Adapter.LikeAdapter;
import com.dbvertex.dilsayproject.EndPoints;
import com.dbvertex.dilsayproject.Model.LikeDTO;
import com.dbvertex.dilsayproject.R;
import com.dbvertex.dilsayproject.session.SessionManager;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class LikeFragment extends Fragment {

    RecyclerView recycler_view;
    LikeAdapter likeAdapter;
    ArrayList<LikeDTO> likeDTOArrayList;
    RequestQueue requestQueue;
    ProgressDialog dialog;
    LinearLayout emptylist;
    SessionManager session;
    public LikeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_like, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        recycler_view=view.findViewById(R.id.recycler_view);
        requestQueue = Volley.newRequestQueue(getActivity());
        dialog = new ProgressDialog(getActivity());
        session = new SessionManager(getActivity());
        emptylist=view.findViewById(R.id.emptylist);
        likeDTOArrayList = new ArrayList<>();
        likeAdapter = new LikeAdapter(getActivity(), likeDTOArrayList);

        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getActivity(), 2);
        recycler_view.setLayoutManager(mLayoutManager);
        recycler_view.addItemDecoration(new GridSpacingItemDecoration(2, dpToPx(5), true));
        recycler_view.setItemAnimator(new DefaultItemAnimator());
        recycler_view.setAdapter(likeAdapter);

        loadLikeList();

    }

    private void loadLikeList()
    {

        dialog.setMessage("Please Wait..");
        dialog.setCancelable(false);
        dialog.show();

        StringRequest postRequest = new StringRequest(Request.Method.POST, EndPoints.LISTING,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        likeDTOArrayList.clear();

                        dialog.dismiss();
                        Log.e("LISTING", response);

                        try
                        {

                            JSONObject obj = new JSONObject(response);
                            int status = obj.getInt("status");
                            String message = obj.getString("message");

///{"status":1,"message":"No record Found!"}
                            if (status == 200 && message.equals("success"))
                            {

                                likeDTOArrayList.clear();
                                emptylist.setVisibility(View.GONE);
                                recycler_view.setVisibility(View.VISIBLE);
                                JSONArray data_list = obj.getJSONArray("data");
                                for (int x = 0; x < data_list.length(); x++)
                                {
                                    JSONObject dataJSONObject = data_list.getJSONObject(x);
                                    LikeDTO likeDTO = new LikeDTO();
                                    String id=dataJSONObject.getString("id");
                                    String name=dataJSONObject.getString("name");
                                    String career=dataJSONObject.getString("career");
                                    String religion=dataJSONObject.getString("religion");
                                    String community=dataJSONObject.getString("community");
                                    String raised_in=dataJSONObject.getString("raised_in");
                                    String education=dataJSONObject.getString("education");
                                    String fb_image=dataJSONObject.getString("fb_image");
                                    String dob=dataJSONObject.getString("dob");
                                    likeDTO.setAge(dob);
                                    likeDTO.setCaste(religion);
                                    likeDTO.setId(id);
                                    likeDTO.setImage(fb_image);
                                    likeDTO.setLocation(raised_in);
                                    likeDTO.setName(name);
                                    likeDTO.setProfession(career);

//                                    productListDTO.setProdimage(String.valueOf(prodImage.get(0)));
//                                    productListDTO.setProdName(dataJSONObject.getString("product_title"));
//                                    productListDTO.setPrice(dataJSONObject.getString("product_price"));
//                                    productListDTO.setProdcondition(dataJSONObject.getString("product_condition"));
//                                    productListDTO.setProdId(dataJSONObject.getString("product_id"));
//                                    productListDTO.setCountryname(dataJSONObject.getString("country"));
//                                    String categ_id=dataJSONObject.getString("product_category_id");
//
//                                    for (int j=0;j<categorylist.length();j++)
//                                    {
//                                        JSONObject catobject = categorylist.getJSONObject(j);
//                                        if (categ_id.equalsIgnoreCase(catobject.getString("id")))
//                                        {
//                                            productListDTO.setCategName(catobject.getString("productt_category"));
//                                        }
//                                    }
                                    likeDTOArrayList.add(likeDTO);
                                }
                                recycler_view.setAdapter(likeAdapter);
                            }

                            else if (status==1 && message.equalsIgnoreCase("No record Found!")){
                                emptylist.setVisibility(View.VISIBLE);
                                recycler_view.setVisibility(View.GONE);
                                likeDTOArrayList.clear();
                                likeAdapter.notifyDataSetChanged();}

                        } catch (Exception ex) {
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        dialog.dismiss();
                    }
                }
        ) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("user_id",session.getUserId().get(SessionManager.KEY_USERID) );
                params.put("list","1" );

                Log.e("params", params.toString());
                return params;
            }

        };

        int socketTimeout = 30000;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        postRequest.setRetryPolicy(policy);
        requestQueue.add(postRequest);
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



}
