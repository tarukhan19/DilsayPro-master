package com.dbvertex.dilsayproject.Adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.dbvertex.dilsayproject.Filter.FilterActivity;
import com.dbvertex.dilsayproject.Model.CommunityDTO;
import com.dbvertex.dilsayproject.R;
import com.dbvertex.dilsayproject.UserAuth.CarrerActivity;
import com.dbvertex.dilsayproject.UserAuth.CommunityActivity;
import com.dbvertex.dilsayproject.UserAuth.LifestyleActivity;
import com.dbvertex.dilsayproject.session.SessionManager;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class CommunityAdapter extends RecyclerView.Adapter<CommunityAdapter.ViewHolderPollAdapter> {
    private Context mcontex;
    private List<CommunityDTO> communityDTOList;
    SessionManager sessionManager;
    Activity activity;
    LinearLayout nextLL;
    public  int selection;
    List<String> sentfilterCommunityList;
    String filterCommunityName;
    String from;
    public CommunityAdapter(Context mcontex, List<CommunityDTO> communityDTOList, LinearLayout nextLL,
                            String from, List<String> sentfilterCommunityList)
    {
        this.mcontex = mcontex;
        this.communityDTOList = communityDTOList;
        this.nextLL = nextLL;
        this.sentfilterCommunityList=sentfilterCommunityList;
        this.from=from;
    }

    @NonNull
    @Override
    public ViewHolderPollAdapter onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        LayoutInflater mInflater = LayoutInflater.from(mcontex);
        view = mInflater.inflate(R.layout.item_info, parent, false);
        sessionManager = new SessionManager(mcontex);
        activity = (Activity) mcontex;
        return new ViewHolderPollAdapter(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolderPollAdapter holder, final int position)
    {
        if (from.equalsIgnoreCase("filter"))
        {
            final CommunityDTO mdata = communityDTOList.get(position);

            holder.communityText.setText(mdata.getCommunityName());
            if (mdata.isSelected())
            {
                holder.checked.setVisibility(View.VISIBLE);
                holder.communityText.setTextColor(mcontex.getResources().getColor(R.color.colorPrimary));
            } else
            {
                holder.checked.setVisibility(View.GONE);
                holder.communityText.setTextColor(mcontex.getResources().getColor(R.color.black));
            }
            Log.e("isselected",mdata.isSelected()+"");
            holder.linearlayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {


                    mdata.setSelected(!mdata.isSelected());
                    if (mdata.isSelected())
                    {
                        holder.checked.setVisibility(View.VISIBLE);
                        holder.communityText.setTextColor(mcontex.getResources().getColor(R.color.colorPrimary));
                    } else
                    {
                        holder.checked.setVisibility(View.GONE);
                        holder.communityText.setTextColor(mcontex.getResources().getColor(R.color.black));
                    }
                    if (mdata.isSelected()) {
                        sentfilterCommunityList.add(mdata.getCommunityName());

                    } else {
                        sentfilterCommunityList.remove(mdata.getCommunityName());

                    }
                    Log.e("lenslistsentrrayadd"," "+sentfilterCommunityList);

                    notifyDataSetChanged();



                }
            });


        }
        else
        {
            holder.communityText.setText(communityDTOList.get(position).getCommunityName());

            if (position==selection)
            {
                holder.checked.setVisibility(View.VISIBLE);
                holder.communityText.setTextColor(mcontex.getResources().getColor(R.color.colorPrimary));
            } else
            {
                holder.checked.setVisibility(View.GONE);
                holder.communityText.setTextColor(mcontex.getResources().getColor(R.color.black));
            }

            holder.linearlayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    selection = position;
                    notifyDataSetChanged();
                    sessionManager.setCommunity(communityDTOList.get(position).getCommunityName());

                }
            });


            holder.bindDataWithViewHolder(communityDTOList.get(position), position);


        }



        nextLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                if (from.equalsIgnoreCase("filter"))
                {
                    StringBuilder sbString = new StringBuilder("");

                    //iterate through ArrayList
                    for (String services : sentfilterCommunityList) {
                        sbString.append(services).append(",");
                    }

                    filterCommunityName = sbString.toString().trim();
                    if (filterCommunityName.length() > 0)
                    {filterCommunityName = filterCommunityName.substring(0, filterCommunityName.length() - 1);}
                    sessionManager.setFilterCommunity(filterCommunityName);

                    Intent intent = new Intent(mcontex, FilterActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    activity.startActivity(intent);
                    activity.overridePendingTransition(R.anim.trans_left_in,
                            R.anim.trans_left_out);
                }
                else
                {
                    if (sessionManager.getCommunity().get(SessionManager.KEY_COMMUNITY).isEmpty())
                    {
                        Toast.makeText(mcontex, "Select community", Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        Intent intent = new Intent(mcontex, LifestyleActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        activity.startActivity(intent);
                        activity.overridePendingTransition(R.anim.trans_left_in,
                                R.anim.trans_left_out);
                    }


                }





            }
        });



    }

    @Override
    public int getItemCount() {
        return communityDTOList != null ? communityDTOList.size() : 0;
    }

    public class ViewHolderPollAdapter extends RecyclerView.ViewHolder {

        TextView communityText;
        ImageView checked;
        LinearLayout linearlayout;

        private CommunityDTO mmdata;


        public ViewHolderPollAdapter(View itemView) {
            super(itemView);
            communityText = (TextView) itemView.findViewById(R.id.communityText);
            checked = (ImageView) itemView.findViewById(R.id.checked);
            linearlayout = itemView.findViewById(R.id.linearlayout);
        }

        public void bindDataWithViewHolder(CommunityDTO mpollData, int position)
        {

            this.mmdata = mpollData;
            if (position==selection)
            {
                mpollData.setSelected(true);

            }


        }
    }

}
