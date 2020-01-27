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
import com.dbvertex.dilsayproject.Model.CareerDTO;
import com.dbvertex.dilsayproject.R;
import com.dbvertex.dilsayproject.UserAuth.EducationActivity;
import com.dbvertex.dilsayproject.session.SessionManager;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class CareerAdapter extends RecyclerView.Adapter<CareerAdapter.ViewHolderPollAdapter> {
    private Context mcontex;
    private List<CareerDTO> careerDTOList;
    Activity activity;
    SessionManager sessionManager;
    LinearLayout nextLL;
    public  int selection;
    List<String> sentfilterCareerList;
    String filterCareerName;
    String from;
    public CareerAdapter(Context mcontex, List<CareerDTO> careerDTOList, LinearLayout nextLL, String from, List<String> sentfilterCareerList) {
        this.mcontex = mcontex;
        this.careerDTOList = careerDTOList;
        this.nextLL = nextLL;
        this.sentfilterCareerList=sentfilterCareerList;
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
            final CareerDTO mdata = careerDTOList.get(position);

            holder.communityText.setText(mdata.getCareerName());
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
                        sentfilterCareerList.add(mdata.getCareerName());

                    } else {
                        sentfilterCareerList.remove(mdata.getCareerName());

                    }
                    Log.e("lenslistsentrrayadd"," "+sentfilterCareerList);

                    notifyDataSetChanged();



                }
            });


        }

        else {

            holder.communityText.setText(careerDTOList.get(position).getCareerName());

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
                    sessionManager.setCareer(careerDTOList.get(position).getCareerName());

                }
            });


            holder.bindDataWithViewHolder(careerDTOList.get(position), position);

        }

        nextLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (from.equalsIgnoreCase("filter"))
                {
                    StringBuilder sbString = new StringBuilder("");

                    //iterate through ArrayList
                    for (String services : sentfilterCareerList) {
                        sbString.append(services).append(",");
                    }

                    filterCareerName = sbString.toString().trim();
                    if (filterCareerName.length() > 0)
                    {filterCareerName = filterCareerName.substring(0, filterCareerName.length() - 1);}
                    sessionManager.setFilterCareer(filterCareerName);

                    Intent intent = new Intent(mcontex, FilterActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    activity.startActivity(intent);
                    activity.overridePendingTransition(R.anim.trans_left_in,
                            R.anim.trans_left_out);
                }
                else
                {
                    if (sessionManager.getCareer().get(SessionManager.KEY_CAREER).isEmpty())
                    {
                        Toast.makeText(mcontex, "Select career", Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        Intent intent = new Intent(mcontex, EducationActivity.class);
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
        return careerDTOList != null ? careerDTOList.size() : 0;
    }

    public class ViewHolderPollAdapter extends RecyclerView.ViewHolder {

        TextView communityText;
        ImageView checked;
        LinearLayout linearlayout;

        private CareerDTO mmdata;


        public ViewHolderPollAdapter(View itemView) {
            super(itemView);
            communityText = (TextView) itemView.findViewById(R.id.communityText);
            checked = (ImageView) itemView.findViewById(R.id.checked);
            linearlayout = itemView.findViewById(R.id.linearlayout);
        }

        public void bindDataWithViewHolder(CareerDTO mpollData, int position) {


            this.mmdata = mpollData;
            if (position==selection)
            {
                mpollData.setSelected(true);

            }

        }
    }

}
