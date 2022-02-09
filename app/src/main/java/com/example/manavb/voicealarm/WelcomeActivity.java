package com.example.manavb.voicealarm;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.text.HtmlCompat;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class WelcomeActivity extends AppCompatActivity {

    private ViewPager mSlideViewPager;
    private SliderAdapter sliderAdapter;
    private LinearLayout mDotsLayout;
    private TextView[] mDots; // stores the dots
    private Button mBackButton;
    private Button mNextButton;

    private int mCurrentPage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        mSlideViewPager = findViewById(R.id.slideViewPager);
        mDotsLayout = findViewById(R.id.dotLinearLayout);
        mBackButton = findViewById(R.id.back_button);
        mNextButton = findViewById(R.id.next_button);

        sliderAdapter = new SliderAdapter(this);

        mSlideViewPager.setAdapter(sliderAdapter);

        addDotsIndicator();

        mSlideViewPager.addOnPageChangeListener(viewPageChangeListener);

        mBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mSlideViewPager.setCurrentItem(mCurrentPage - 1);
            }
        });

        mNextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mSlideViewPager.setCurrentItem(mCurrentPage + 1);
            }
        });
    }

    private void addDotsIndicator(){
        mDots = new TextView[6];

        for(int i = 0; i < mDots.length; i++){
             mDots[i] = new TextView(this);
             mDots[i].setText(HtmlCompat.fromHtml("&#8226", HtmlCompat.FROM_HTML_MODE_LEGACY));
             mDots[i].setTextSize(40);
             mDots[i].setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorSecondary));
             if (i == 0){ // initially, first dot needs to be selected
                 mDots[i].setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.activityBg));
             }

             mDotsLayout.addView(mDots[i]);
        }

    }

    ViewPager.SimpleOnPageChangeListener viewPageChangeListener = new ViewPager.SimpleOnPageChangeListener(){
        @Override
        public void onPageSelected(int position) {
            if(mDots.length > 0) {
                for(int i = 0; i < mDots.length; i++){
                    mDots[i].setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorSecondary));
                    if (i == position) {
                        mDots[i].setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.activityBg));
                    }
                }
            }

            mCurrentPage = position;
            if (position == 0){ // when on the first slide
                mBackButton.setEnabled(false);
                mNextButton.setEnabled(true);
                mBackButton.setVisibility(View.INVISIBLE);
            } else if(position == mDots.length - 1){ // when on the last slide
                mBackButton.setEnabled(true);
                mNextButton.setEnabled(true);
                mBackButton.setVisibility(View.VISIBLE);

                // Next button changes into a check button on the last slide
                mNextButton.setForeground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_check_icon_no_bg_24));

                // Next button finishes this activity and returns to MainActivity
                mNextButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // SET IT TO TRUE SO THAT IT ONLY PLAYS FOR FIRST TIME TO THE USER
                        SharedPreferences.Editor sharedPreferencesEditor =
                                PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit();
                        sharedPreferencesEditor.putBoolean(
                                MainActivity.ONBOARDING_COMPLETED, true);
                        sharedPreferencesEditor.apply();

                        Intent returnIntent = new Intent();
                        setResult(RESULT_OK, returnIntent);
                        finish();
                    }
                });
            } else{ // all other slides
                mBackButton.setEnabled(true);
                mNextButton.setEnabled(true);
                mBackButton.setVisibility(View.VISIBLE);

                mNextButton.setForeground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_baseline_arrow_forward_ios_24));
                //mNextButton.setText("");

                //On all other slides, next button takes the viewPager to the next slide
                mNextButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mSlideViewPager.setCurrentItem(mCurrentPage + 1);
                    }
                });
            }
        }

    };

}