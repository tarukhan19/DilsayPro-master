package com.dbvertex.dilsayproject.Fragment;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dbvertex.dilsayproject.R;
import com.dbvertex.dilsayproject.UserAuth.HomePageActivity;
import com.dbvertex.dilsayproject.session.SessionManager;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class MyActivityFragment extends Fragment {
    private TabLayout tabLayout;
    private ViewPager viewPager;
    Intent intent;
    boolean isConnected;
    SessionManager sessionManager;
    String country;

    public MyActivityFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_my_activity, container, false);

        sessionManager = new SessionManager(getActivity().getApplicationContext());


        intent=getActivity().getIntent();


        viewPager = (ViewPager) view.findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        tabLayout = (TabLayout) view.findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

        setupTabIcons();

        Toolbar toolbar = (Toolbar) view.findViewById(R.id.toolbar_main);
        LinearLayout back = toolbar.findViewById(R.id.back_LL);
        TextView toolbar_title = toolbar.findViewById(R.id.titleTV);
        toolbar_title.setText("My Activity");
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


        return  view;
    }


    @SuppressLint("ResourceType")
    private void setupTabIcons() {

        TextView tabOne = (TextView) LayoutInflater.from(getActivity()).inflate(R.layout.custom_tab, null);
        tabOne.setText("Like");
//        tabLayout.getTabAt(0).setCustomView(tabOne);

        tabOne.setTextColor(getResources().getColorStateList(R.drawable.selector_textview));
        tabOne.setTextSize(12);
        tabLayout.getTabAt(0).setCustomView(tabOne);

        TextView tabTwo = (TextView) LayoutInflater.from(getActivity()).inflate(R.layout.custom_tab, null);
        tabTwo.setText("Dislike");
//        tabLayout.getTabAt(1).setCustomView(tabTwo);
        tabTwo.setTextColor(getResources().getColorStateList(R.drawable.selector_textview));
        tabTwo.setTextSize(12);
        tabLayout.getTabAt(1).setCustomView(tabTwo);


        TextView tabThree = (TextView) LayoutInflater.from(getActivity()).inflate(R.layout.custom_tab, null);
        tabThree.setText("Think Later");
//        tabLayout.getTabAt(1).setCustomView(tabTwo);
        tabThree.setTextColor(getResources().getColorStateList(R.drawable.selector_textview));
        tabThree.setTextSize(12);
        tabLayout.getTabAt(2).setCustomView(tabThree);

    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getActivity().getSupportFragmentManager());
        adapter.addFrag(new LikeFragment(), "like");
        adapter.addFrag(new DislikeFragment(), "dislike");
        adapter.addFrag(new ThinkLaterFragment(), "think");

        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(0);

    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFrag(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }



}
