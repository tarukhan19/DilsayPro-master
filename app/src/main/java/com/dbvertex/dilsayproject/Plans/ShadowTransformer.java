package com.dbvertex.dilsayproject.Plans;


import android.content.Context;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.cardview.widget.CardView;
import androidx.viewpager.widget.ViewPager;

import com.dbvertex.dilsayproject.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class ShadowTransformer implements ViewPager.OnPageChangeListener, ViewPager.PageTransformer {

    private ViewPager mViewPager;
    private CardAdapter mAdapter;
    private float mLastOffset;
    private boolean mScalingEnabled;
    Context context;
    JSONArray response;
    TextView msgplanTV,likeplanTV,boostplanTV;
    String subscription_plan, likes, boost,messages;

    public ShadowTransformer(ViewPager viewPager, CardAdapter adapter, Context context, JSONArray response, TextView msgplanTV, TextView likeplanTV, TextView boostplanTV) {
        mViewPager = viewPager;
        viewPager.addOnPageChangeListener(this);
        mAdapter = adapter;
        this.context = context;
        this.response = response;
        this.msgplanTV = msgplanTV;
        this.likeplanTV = likeplanTV;
        this.boostplanTV = boostplanTV;
    }

    public void enableScaling(boolean enable) {
        if (mScalingEnabled && !enable) {
            // shrink main card
            CardView currentCard = mAdapter.getCardViewAt(mViewPager.getCurrentItem());
            if (currentCard != null) {
                currentCard.animate().scaleY(1);
                currentCard.animate().scaleX(1);
            }
        }else if(!mScalingEnabled && enable){
            // grow main card
            CardView currentCard = mAdapter.getCardViewAt(mViewPager.getCurrentItem());
            if (currentCard != null) {
                currentCard.animate().scaleY(1.1f);
                currentCard.animate().scaleX(1.1f);
            }
        }

        mScalingEnabled = enable;
    }

    @Override
    public void transformPage(View page, float position) {

    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        int realCurrentPosition;
        int nextPosition;
        float baseElevation = mAdapter.getBaseElevation();
        float realOffset;
        boolean goingLeft = mLastOffset > positionOffset;

        // If we're going backwards, onPageScrolled receives the last position
        // instead of the current one
        if (goingLeft) {
            realCurrentPosition = position + 1;
            nextPosition = position;
            realOffset = 1 - positionOffset;
        } else {
            nextPosition = position + 1;
            realCurrentPosition = position;
            realOffset = positionOffset;
        }

        // Avoid crash on overscroll
        if (nextPosition > mAdapter.getCount() - 1
                || realCurrentPosition > mAdapter.getCount() - 1) {
            return;
        }

        CardView currentCard = mAdapter.getCardViewAt(realCurrentPosition);

        // This might be null if a fragment is being used
        // and the views weren't created yet
        if (currentCard != null) {
            if (mScalingEnabled) {
                currentCard.setScaleX((float) (1 + 0.1 * (1 - realOffset)));
                currentCard.setScaleY((float) (1 + 0.1 * (1 - realOffset)));
            }
            currentCard.setCardElevation((baseElevation + baseElevation
                    * (CardAdapter.MAX_ELEVATION_FACTOR - 1) * (1 - realOffset)));
        }

        CardView nextCard = mAdapter.getCardViewAt(nextPosition);

        // We might be scrolling fast enough so that the next (or previous) card
        // was already destroyed or a fragment might not have been created yet
        if (nextCard != null) {
            if (mScalingEnabled) {
                nextCard.setScaleX((float) (1 + 0.1 * (realOffset)));
                nextCard.setScaleY((float) (1 + 0.1 * (realOffset)));
            }
            nextCard.setCardElevation((baseElevation + baseElevation
                    * (CardAdapter.MAX_ELEVATION_FACTOR - 1) * (realOffset)));
        }

        mLastOffset = positionOffset;
    }

    @Override
    public void onPageSelected(int position) {


        if (position == 0)
        {

            try {
                JSONObject object0 = response.getJSONObject(0);
                //  subscription_plan = object.getString("subscription_plan");
                likes = object0.getString("likes");
                boost = object0.getString("boost");
                messages = object0.getString("messages");
                likeplanTV.setText(likes+"\nLikes");
                boostplanTV.setText(boost+"\nBoost");
                msgplanTV.setText(messages+"\nMessage");




            } catch (JSONException e) {
                e.printStackTrace();
            }

        } else if (position == 1) {

            try {
                JSONObject object0 = response.getJSONObject(1);
                //  subscription_plan = object.getString("subscription_plan");
                likes = object0.getString("likes");
                boost = object0.getString("boost");
                messages = object0.getString("messages");
                likeplanTV.setText(likes+"\nLikes");
                boostplanTV.setText(boost+"\nBoost");
                msgplanTV.setText(messages+"\nMessage");



            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

        if (position == 2) {
            try {
                JSONObject object0 = response.getJSONObject(2);
                //  subscription_plan = object.getString("subscription_plan");
                likes = object0.getString("likes");
                boost = object0.getString("boost");
                messages = object0.getString("messages");
                likeplanTV.setText(likes+"\nLikes");
                boostplanTV.setText(boost+"\nBoost");
                msgplanTV.setText(messages+"\nMessage");





            } catch (JSONException e) {
                e.printStackTrace();
            }

        }


    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }
}
