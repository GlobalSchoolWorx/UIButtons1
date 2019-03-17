package com.edu.worx.global.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.edu.worx.global.R;

public class ApplicationConfigurations {
    private static SharedPreferences mPrefs = null;
    private static String mKeyAdsDisplayLocalState = null;
    private static Boolean mAdsDisplayLocalState = null;

    public static void changeAdsDisplayLocalState(Context cxt, boolean flag){
        if(null == mKeyAdsDisplayLocalState) {
            mKeyAdsDisplayLocalState = cxt.getResources().getString(R.string.key_ads_display_local_state);
        }

        if(null == mPrefs){
            mPrefs = PreferenceManager.getDefaultSharedPreferences(cxt);
        }

        mAdsDisplayLocalState = flag;
        mPrefs.edit().putBoolean(mKeyAdsDisplayLocalState, flag).apply();
    }

    public static boolean isAdsLocallyEnabled(Context cxt){
        // Make sure to change the default in /xml/preference
        if(null == mAdsDisplayLocalState){
            if(null == mKeyAdsDisplayLocalState) {
                mKeyAdsDisplayLocalState = cxt.getResources().getString(R.string.key_ads_display_local_state);
            }

            if(null == mPrefs){
                mPrefs = PreferenceManager.getDefaultSharedPreferences(cxt);
            }
            mAdsDisplayLocalState =  mPrefs.getBoolean(mKeyAdsDisplayLocalState, true);
        }
        return mAdsDisplayLocalState;
    }

    public static boolean hasSusbscription(Context cxt){
        return !isAdsLocallyEnabled(cxt);
    }
}