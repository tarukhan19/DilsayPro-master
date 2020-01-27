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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dbvertex.dilsayproject.Model.EducationDTO;
import com.dbvertex.dilsayproject.Model.FaqInDTO;
import com.dbvertex.dilsayproject.R;
import com.dbvertex.dilsayproject.UserAuth.HeightActivity;
import com.dbvertex.dilsayproject.session.SessionManager;

import java.util.List;

public class FaqAdapter extends RecyclerView.Adapter<FaqAdapter.ViewHolderPollAdapter> {
    private Context mcontex;
    private List<FaqInDTO> faqInDTOList;
    Activity activity;
    SessionManager sessionManager;
    public  int selection;

    public FaqAdapter(Context mcontex, List<FaqInDTO> faqInDTOList) {
        this.mcontex = mcontex;
        this.faqInDTOList = faqInDTOList;
        sessionManager = new SessionManager(mcontex);
        activity= (Activity) mcontex;
    }

    @NonNull
    @Override
    public ViewHolderPollAdapter onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        LayoutInflater mInflater = LayoutInflater.from(mcontex);
        view = mInflater.inflate(R.layout.item_faq, parent, false);
        sessionManager = new SessionManager(mcontex);
        activity= (Activity) mcontex;
        return new ViewHolderPollAdapter(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolderPollAdapter holder, final int position) {

        // //Log.e("selectAnsId",selectAnsId.size()+"ansIdlist "+ansIdlist.size()+"");


        holder.qstnTV.setText("Question:  "+faqInDTOList.get(position).getQuestn());
        holder.ansTV.setText("Answer:  "+faqInDTOList.get(position).getAnswer());

    }

    @Override
    public int getItemCount() {
        return faqInDTOList != null ? faqInDTOList.size() : 0;
    }

    public class ViewHolderPollAdapter extends RecyclerView.ViewHolder {

        TextView qstnTV,ansTV;

        private EducationDTO mmdata;


        public ViewHolderPollAdapter(View itemView) {
            super(itemView);


            qstnTV = (TextView) itemView.findViewById(R.id.qstnTV);
            ansTV = (TextView) itemView.findViewById(R.id.ansTV);
        }


    }

}
