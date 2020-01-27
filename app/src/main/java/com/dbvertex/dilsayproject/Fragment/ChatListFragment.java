package com.dbvertex.dilsayproject.Fragment;


import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Base64;
import android.util.Log;
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
import com.dbvertex.dilsayproject.Chatting.ChatInboxActivity;
import com.dbvertex.dilsayproject.EndPoints;
import com.dbvertex.dilsayproject.Model.ChatListDTO;
import com.dbvertex.dilsayproject.Network.ConnectivityReceiver;
import com.dbvertex.dilsayproject.Network.MyApplication;
import com.dbvertex.dilsayproject.R;
import com.dbvertex.dilsayproject.UserAuth.HomePageActivity;
import com.dbvertex.dilsayproject.session.SessionManager;
import com.google.android.material.snackbar.Snackbar;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class ChatListFragment extends Fragment implements ConnectivityReceiver.ConnectivityReceiverListener{

    RecyclerView prodrecycle;
    ChatListAdapter productListAdapter;
    ArrayList<ChatListDTO> chatListDTOArrayList;
    RequestQueue requestQueue;
    ProgressDialog dialog;
    SessionManager session;
    boolean isConnected;
    Intent intent;
    TextView norecrdfound;

    public ChatListFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_chat_list, container, false);
    }
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        Toolbar toolbar = (Toolbar) view.findViewById(R.id.toolbar_main);
        LinearLayout back = toolbar.findViewById(R.id.back_LL);
        TextView toolbar_title = toolbar.findViewById(R.id.titleTV);
        toolbar_title.setText("Chat List");
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



        requestQueue = Volley.newRequestQueue(getActivity());
        dialog = new ProgressDialog(getActivity());
        session = new SessionManager(getActivity());
        norecrdfound=view.findViewById(R.id.norecrdfound);
        prodrecycle= (RecyclerView) view.findViewById(R.id.prodrecycle);
        chatListDTOArrayList = new ArrayList<>();
        productListAdapter = new ChatListAdapter(getActivity(), chatListDTOArrayList);

        @SuppressLint("WrongConstant") LinearLayoutManager layoutManager
                = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        prodrecycle.setLayoutManager(layoutManager);
        prodrecycle.setAdapter(productListAdapter);
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        // setLanguage(session.getCurrentLanguage());

    }

    private void loadChatList()
    {
        dialog.setMessage(("Loading.."));
        dialog.setCancelable(false);
        dialog.show();

        StringRequest postRequest = new StringRequest ( Request.Method.POST, EndPoints.CHAT_LIST,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        chatListDTOArrayList.clear();
                        Log.e("response",response);
                        dialog.dismiss();
                        try
                        {
//{"status":200,"message":"success","Chate_list":[{"chat_id":"1","user_id":"284","username":"Ovi Makers"}]}
                            JSONObject obj = new JSONObject(response);
                            int status=obj.getInt("status");
                            String msg=obj.getString("message");
                            if (status==200 && msg.equalsIgnoreCase("success"))
                            {
                                norecrdfound.setVisibility(View.GONE);
                                prodrecycle.setVisibility(View.VISIBLE);
                                JSONArray Chate_list=obj.getJSONArray("Chate_list");
                                for (int i=0;i<Chate_list.length();i++)
                                {
                                    JSONObject jsonObject=Chate_list.getJSONObject(i);

                                    String chat_id=jsonObject.getString("chat_id");
                                    String rec_user_id=jsonObject.getString("user_id");
                                    String username=jsonObject.getString("username");

                                    ChatListDTO chatListDTO=new ChatListDTO();
                                    chatListDTO.setchatId(chat_id);
                                    chatListDTO.setusername(username);
                                    chatListDTO.setrec_user_id(rec_user_id);

                                    chatListDTOArrayList.add(chatListDTO);


                                }
                                prodrecycle.setAdapter(productListAdapter);

                            }
                            else
                            {
                                norecrdfound.setVisibility(View.VISIBLE);
                                prodrecycle.setVisibility(View.GONE);

                            }


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
            protected Map<String, String> getParams()
            {
                Map<String, String> params = new HashMap<>();
                params.put("user_id",session.getUserId().get(SessionManager.KEY_USERID) );
                return params;
            }

        };
        int socketTimeout = 30000;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        postRequest.setRetryPolicy(policy);
        requestQueue.add(postRequest);
    }

    public String fromBase64(String message) {
        byte[] data = Base64.decode(message, Base64.DEFAULT);
        try {
            return new String(data, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return null;
    }

    //    private void setLanguage(String language1)
//    {
//        Locale locale = new Locale(language1);
//        Locale.setDefault(locale);
//        Configuration config = new Configuration();
//        config.locale = locale;
//        getActivity().getApplicationContext().getResources().updateConfiguration(config, null);
//        Log.e("language1",language1);
//
//    }
    private class ChatListAdapter extends RecyclerView.Adapter<ChatListAdapter.CustomViewHodler>
    {

        private Context mContext;
        ArrayList<ChatListDTO> productListDTOS;
        SessionManager sessionManager;
        long diff ;
        RequestQueue requestQueue;
        ProgressDialog dialog;

        public ChatListAdapter(Context context, ArrayList<ChatListDTO> productListDTOS)
        {
            this.mContext = context;
            this.productListDTOS = productListDTOS;
        }

        @Override
        public CustomViewHodler onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_chat, parent, false);
            return new CustomViewHodler(itemView);    }

        @Override
        public void onBindViewHolder(final CustomViewHodler holder, final int position)
        {
            final ChatListDTO productListDTO = productListDTOS.get(position);



            holder.username.setText(productListDTO.getusername());


            holder.cardView.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view) {


                    Intent intent=new Intent(mContext, ChatInboxActivity.class);
                    intent.putExtra("username",productListDTO.getusername());
                    intent.putExtra("userid",productListDTO.getrec_user_id());



                    intent.putExtra("from","chatfrag");

                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

                    mContext.startActivity(intent);


                }
            });


        }



        @Override
        public int getItemCount() {
            return productListDTOS.size();
        }

        public class CustomViewHodler extends RecyclerView.ViewHolder {
            ImageView iv_pic;
            LinearLayout cardView;
            TextView username;
            public CustomViewHodler(View itemView) {
                super(itemView);
                cardView=itemView.findViewById(R.id.cardView);
                iv_pic=(ImageView)itemView.findViewById(R.id.iv_pic);
                username=(TextView) itemView.findViewById(R.id.username);


            }
        }
    }


    @Override
    public void onStart() {
        super.onStart();
        isConnected = ConnectivityReceiver.isConnected();
        Log.e("onStart",isConnected+"");
        if (!isConnected)
        {
            showSnack(isConnected);
        }
        else
        { loadChatList();}
    }

    // Showing the status in Snackbar
    private void showSnack(boolean isConnected) {
        String message;
        int color;

        Log.e("showSnackisConnected",isConnected+"");
        if (isConnected) {
            message = "Good! Connected to Internet";
            color = Color.WHITE;

        } else {
            message = "Sorry! Not connected to internet";
            color = Color.RED;

        }

        Snackbar snackbar = Snackbar
                .make(getView().findViewById(R.id.ll), message, Snackbar.LENGTH_INDEFINITE).setAction("Retry", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        startActivity(intent);
                        getActivity().overridePendingTransition(0,0);
                    }
                });
        ;

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
        this.isConnected=isConnected;
        Log.e("onNetworkConnectionconn",isConnected+"");

        showSnack(isConnected);



    }

}
