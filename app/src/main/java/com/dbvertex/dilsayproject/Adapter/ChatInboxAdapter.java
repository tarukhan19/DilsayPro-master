package com.dbvertex.dilsayproject.Adapter;

import android.content.Context;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.dbvertex.dilsayproject.Model.ChatDTO;
import com.dbvertex.dilsayproject.R;
import com.dbvertex.dilsayproject.session.SessionManager;


import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

public class ChatInboxAdapter extends BaseAdapter
{

    private ArrayList<ChatDTO> chatList;
    private Context ctx;
    private String sender;
    SessionManager session;
    public ChatInboxAdapter(ArrayList<ChatDTO> chatList, Context ctx, String sender)
    {
        this.chatList = chatList;
        this.ctx = ctx;
        this.sender = sender;
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int  i, View view, ViewGroup viewGroup) {
        View itemView = LayoutInflater.from(ctx).inflate(R.layout.item_chatbox, viewGroup, false);
        session = new SessionManager(ctx);

        ChatDTO chat = chatList.get(i);

        LinearLayout senderLL = (LinearLayout) itemView.findViewById(R.id.senderLL);
        LinearLayout receiverLL = (LinearLayout) itemView.findViewById(R.id.receiverLL);

        TextView timeTV, msgTV;
        ImageView msgIV;

        if (session.getUserId().get(SessionManager.KEY_USERID).equalsIgnoreCase(chat.getSendBy())) {
            receiverLL.setVisibility(View.GONE);
            senderLL.setVisibility(View.VISIBLE);

            timeTV = (TextView) itemView.findViewById(R.id.stimeTV);
            msgTV = (TextView) itemView.findViewById(R.id.smsgTV);
            msgIV = itemView.findViewById(R.id.smsgIV);

        } else {
            receiverLL.setVisibility(View.VISIBLE);
            senderLL.setVisibility(View.GONE);

            timeTV = (TextView) itemView.findViewById(R.id.rtimeTV);
            msgTV = (TextView) itemView.findViewById(R.id.rmsgTV);
            msgIV = itemView.findViewById(R.id.rmsgIV);
        }

        timeTV.setText(chat.getTime());

        String msg = "";
        try {
            msg = fromBase64(chat.getMsg());

        } catch (IllegalArgumentException e) {
        }
        msgTV.setText(msg);
        if (chat.getImage().isEmpty()) {
            msgIV.setVisibility(View.GONE);
        } else
        {
            msgIV.setVisibility(View.VISIBLE);
            Glide.with(ctx.getApplicationContext()).asBitmap().load(chat.getImage())
                    .thumbnail(0.5f).centerCrop()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(msgIV);

        }

        return itemView;
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
    @Override
    public int getCount() {
        return chatList.size();
    }

}
