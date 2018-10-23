package com.edu.worx.global;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

class SwipeAdapter extends FragmentStatePagerAdapter{
    private String selectedClass = "";
    SwipeAdapter(FragmentManager fm, String selectedClass){
        super (fm);
        this.selectedClass = selectedClass;
    }

    @Override
    public Fragment getItem(int position) {
        return PageFragment.newInstance(position, selectedClass);
    }

    @Override
    public int getCount() {
        return 1; // 2 pagers
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return "TestPapers";
            case 1:
                return "Worksheets";
        }
        return "";
    }

}
