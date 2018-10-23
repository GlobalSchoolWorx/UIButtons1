package com.edu.worx.global;

import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class SwipeViewActivity extends AppCompatActivity implements PageFragment.OnFragmentInteractionListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_swipe_view);
        // Get a support ActionBar corresponding to this toolbar and enable the Up button
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        final ViewPager myViewPager = (ViewPager) findViewById(R.id.viewPager);
        String selectedClass = getIntent().getStringExtra(DisplaySubjectsActivity.SELECTED_CLASS);

        SwipeAdapter swipeAdapter = new SwipeAdapter(getSupportFragmentManager(), selectedClass);
        myViewPager.setAdapter(swipeAdapter);
        myViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                View fragView = myViewPager.getChildAt(position);
            }

            @Override
            public void onPageSelected(int position) {
                int i=0;
                i = i+1;
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                int i=0;
                i = i+1;
            }
        });
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
