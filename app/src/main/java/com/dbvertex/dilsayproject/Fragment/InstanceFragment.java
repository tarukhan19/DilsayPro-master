package com.dbvertex.dilsayproject.Fragment;


import android.os.Bundle;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dbvertex.dilsayproject.R;
import com.dbvertex.dilsayproject.UserAuth.HomePageActivity;

/**
 * A simple {@link Fragment} subclass.
 */
public class InstanceFragment extends Fragment {


    public InstanceFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_instance, container, false);


        Toolbar toolbar = (Toolbar) view.findViewById(R.id.toolbar_main);
        LinearLayout back = toolbar.findViewById(R.id.back_LL);
        TextView toolbar_title = toolbar.findViewById(R.id.titleTV);
        toolbar_title.setText("Instance Match");
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


        return view;
    }

}
