package com.dbvertex.dilsayproject.Adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.dbvertex.dilsayproject.Filter.FilterActivity;
import com.dbvertex.dilsayproject.Model.EducationDTO;
import com.dbvertex.dilsayproject.R;
import com.dbvertex.dilsayproject.UserAuth.HeightActivity;
import com.dbvertex.dilsayproject.session.SessionManager;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class EducationAdapter extends RecyclerView.Adapter<EducationAdapter.ViewHolderPollAdapter> {
    private Context mcontex;
    private List<EducationDTO> educationDTOList;
    Activity activity;
    SessionManager sessionManager;
    LinearLayout nextLL;
    public  int selection;
    String from;
    String filterEducationName;

    List<String> sentfilterEducationList;
    public EducationAdapter(Context mcontex, List<EducationDTO> educationDTOList, LinearLayout nextLL, String from, List<String> sentfilterEducationList) {
        this.mcontex = mcontex;
        this.educationDTOList = educationDTOList;
        sessionManager = new SessionManager(mcontex);
        activity= (Activity) mcontex;
        this.nextLL=nextLL;
        this.from=from;
        this.sentfilterEducationList=sentfilterEducationList;
    }

    @NonNull
    @Override
    public ViewHolderPollAdapter onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        LayoutInflater mInflater = LayoutInflater.from(mcontex);
        view = mInflater.inflate(R.layout.item_info, parent, false);
        activity= (Activity) mcontex;
        return new ViewHolderPollAdapter(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolderPollAdapter holder, final int position)
    {


        if (from.equalsIgnoreCase("filter"))
        {
            final EducationDTO mdata = educationDTOList.get(position);

            holder.communityText.setText(mdata.getEducationName());
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
                        sentfilterEducationList.add(mdata.getEducationName());

                    } else {
                        sentfilterEducationList.remove(mdata.getEducationName());

                    }
                    Log.e("lenslistsentrrayadd"," "+sentfilterEducationList);

                    notifyDataSetChanged();



                }
            });


        }
        else {

            holder.communityText.setText(educationDTOList.get(position).getEducationName());
            if (position == selection) {
                holder.checked.setVisibility(View.VISIBLE);
                holder.communityText.setTextColor(mcontex.getResources().getColor(R.color.colorPrimary));
            } else {
                holder.checked.setVisibility(View.GONE);
                holder.communityText.setTextColor(mcontex.getResources().getColor(R.color.black));
            }

            holder.linearlayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    selection = position;
                    notifyDataSetChanged();
                    sessionManager.setEducation(educationDTOList.get(position).getEducationName());

                }
            });


            holder.bindDataWithViewHolder(educationDTOList.get(position), position);

        }


        nextLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                Log.e("from",from);
                if (from.equalsIgnoreCase("filter"))
                {
                    StringBuilder sbString = new StringBuilder("");

                    //iterate through ArrayList
                    for (String services : sentfilterEducationList) {
                        sbString.append(services).append(",");
                    }

                    filterEducationName = sbString.toString().trim();
                    if (filterEducationName.length() > 0)
                    {filterEducationName = filterEducationName.substring(0, filterEducationName.length() - 1);}
                    sessionManager.setFilterEducation(filterEducationName);

                    Intent intent = new Intent(mcontex, FilterActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    activity.startActivity(intent);
                    activity.overridePendingTransition(R.anim.trans_left_in,
                            R.anim.trans_left_out);
                }
                else
                {

                    if (sessionManager.getEducation().get(SessionManager.KEY_EDUCATION).isEmpty()) {
                        Toast.makeText(mcontex, "Select education", Toast.LENGTH_SHORT).show();
                    } else {
                        Intent intent = new Intent(mcontex, HeightActivity.class);
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
        return educationDTOList != null ? educationDTOList.size() : 0;
    }

    public class ViewHolderPollAdapter extends RecyclerView.ViewHolder {

        TextView communityText;
        ImageView checked;
        LinearLayout linearlayout;

        private EducationDTO mmdata;


        public ViewHolderPollAdapter(View itemView) {
            super(itemView);


            communityText = (TextView) itemView.findViewById(R.id.communityText);
            checked = (ImageView) itemView.findViewById(R.id.checked);
            linearlayout=itemView.findViewById(R.id.linearlayout);
        }

        public void bindDataWithViewHolder(EducationDTO mpollData, int position) {


            this.mmdata = mpollData;
            if (position==selection)
            {
                mpollData.setSelected(true);

            }

        }
    }

}
