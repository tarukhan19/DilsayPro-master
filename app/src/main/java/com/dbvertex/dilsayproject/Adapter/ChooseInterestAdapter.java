package com.dbvertex.dilsayproject.Adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dbvertex.dilsayproject.Model.ChooseInterestDTO;
import com.dbvertex.dilsayproject.R;
import com.dbvertex.dilsayproject.UserAuth.CommunityActivity;
import com.dbvertex.dilsayproject.session.SessionManager;

import java.util.List;

public class ChooseInterestAdapter extends RecyclerView.Adapter<ChooseInterestAdapter.ViewHolderPollAdapter> {
    private Context mcontex;
    private List<ChooseInterestDTO> chooseInterestDTOS;
    private int mSelectedItemPosition = -1;
    Activity activity;
    SessionManager sessionManager;

    public ChooseInterestAdapter(Context mcontex, List<ChooseInterestDTO> chooseInterestDTOS) {
        this.mcontex = mcontex;
        this.chooseInterestDTOS = chooseInterestDTOS;

    }

    @NonNull
    @Override
    public ViewHolderPollAdapter onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        LayoutInflater mInflater = LayoutInflater.from(mcontex);
        view = mInflater.inflate(R.layout.item_info, parent, false);
        sessionManager = new SessionManager(mcontex);
        activity= (Activity) mcontex;
        return new ViewHolderPollAdapter(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolderPollAdapter holder, final int position) {

        // //Log.e("selectAnsId",selectAnsId.size()+"ansIdlist "+ansIdlist.size()+"");


        holder.communityText.setText(chooseInterestDTOS.get(position).getInterestName());
        if (chooseInterestDTOS.get(position).isChecked()) {
            holder.checked.setVisibility(View.VISIBLE);
            mSelectedItemPosition = position;
        } else
        {
            holder.checked.setVisibility(View.GONE);
        }
        holder.linearlayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int previousSelectState = mSelectedItemPosition;
                mSelectedItemPosition = holder.getAdapterPosition();
                notifyItemChanged(previousSelectState);
                //notify new selected Item
                notifyItemChanged(mSelectedItemPosition);
                notifyDataSetChanged();
                Intent intent = new Intent(mcontex, CommunityActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                activity.startActivity(intent);
                activity.overridePendingTransition(R.anim.trans_left_in,
                        R.anim.trans_left_out);


            }
        });



        holder.bindDataWithViewHolder(chooseInterestDTOS.get(position), position);





    }

    @Override
    public int getItemCount() {
        return chooseInterestDTOS != null ? chooseInterestDTOS.size() : 0;
    }

    public class ViewHolderPollAdapter extends RecyclerView.ViewHolder {

        TextView communityText;
        ImageView checked;
        LinearLayout linearlayout;

        private ChooseInterestDTO mmdata;


        public ViewHolderPollAdapter(View itemView) {
            super(itemView);


            communityText = (TextView) itemView.findViewById(R.id.communityText);
            checked = (ImageView) itemView.findViewById(R.id.checked);
            linearlayout=itemView.findViewById(R.id.linearlayout);
        }

        public void bindDataWithViewHolder(ChooseInterestDTO mpollData, int position) {

            this.mmdata = mpollData;
            //  //Log.e("checked1",mpollData.isChecked()+"");

            //  imgradio.setImageResource(mpollData.isChecked() ? R.drawable.radio_active : R.drawable.radio_inactive);

            if (position == mSelectedItemPosition) {
                checked.setVisibility(View.VISIBLE);
                communityText.setTextColor(mcontex.getResources().getColor(R.color.colorPrimary));
            } else {
                checked.setVisibility(View.GONE);
                communityText.setTextColor(mcontex.getResources().getColor(R.color.black));

            }

        }
    }

}
