/*
 * Copyright 2014 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.edu.worx.global.billing;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.util.Pair;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.BounceInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingManager;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.SkuDetails;
import com.edu.worx.global.BuildConfig;
import com.edu.worx.global.R;
import com.edu.worx.global.events.UserSubscriptionChangedEvent;
import com.edu.worx.global.utils.ApplicationConfigurations;
import com.edu.worx.global.utils.ConnectivityReceiver;
import com.edu.worx.global.utils.ScrimUtil;
import com.firebase.jobdispatcher.Constraint;
import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.firebase.jobdispatcher.RetryStrategy;
import com.firebase.jobdispatcher.Trigger;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Fragment for allowing the user to configure advanced settings.
 */
public class BuySubscriptionFragment extends Fragment implements BillingManager.BillingUpdatesListener {
    public static final String ITEM_SKU = "android.test.purchased";
    public static final String ITEM_SKU_ONE_YEAR = "com.edu.worx.global.one.year.12.2018";
    public static final String ITEM_SKU_SIX_MONTHS = "com.edu.worx.global.six.months.12.2018";
    public static final String SKU_LABEL_ONE_YEAR_SUBSCRIPTION = "One Year";
    public static final String SKU_LABEL_SIX_MONTHS_SUBSCRIPTION = "Six Months";

    public interface OnBuySubscriptionListener {
        void onPurchaseSuccess();
        void onPurchaseFailed();
    }
    private Handler mHandler = new Handler();
    private OnBuySubscriptionListener mListener;
    BillingManager mBillingManager = null;
    private @BillingClient.BillingResponse
    Integer mSubscriptionFetchCode = null;
    private @BillingClient.BillingResponse
    Integer mSkuDetailsFetchCode = null;
    private Map<String, SkuDetails> mSkuDetailsMap = new HashMap<>();

    public BuySubscriptionFragment() { }

    @Override
    public void onAttach(Context activity) {
        super.onAttach(activity);
        if (!(activity instanceof OnBuySubscriptionListener)) {
            throw new ClassCastException("Activity must implement fragment callbacks: OnBuySubscriptionListener.");
        }
        mListener = (OnBuySubscriptionListener) activity;

        mBillingManager = new BillingManager(activity, this, true);
        mBillingManager.startServiceConnection(this::getSkuDetailsAndUpdateUi,
                ()-> updateUi(mBillingManager.getBillingClientResponseCode(), mSkuDetailsFetchCode, mSkuDetailsMap, getView()));
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setRetainInstance(true);
    }

    private void getSkuDetailsAndUpdateUi(){
        List<String> moreSubSkus = new ArrayList<>(3);
        moreSubSkus.add(ITEM_SKU_ONE_YEAR);
        moreSubSkus.add(ITEM_SKU_SIX_MONTHS);
        if (BuildConfig.DEBUG) {
            moreSubSkus.add(ITEM_SKU);
        }
        mBillingManager.querySkuDetailsAsync(BillingClient.SkuType.SUBS, moreSubSkus, (@BillingClient.BillingResponse int skuFetchResponseCode, List<SkuDetails> skuDetailsList) ->{
            if (BillingClient.BillingResponse.OK == skuFetchResponseCode && null != skuDetailsList) {
                for (SkuDetails skuDetails : skuDetailsList) {
                    mSkuDetailsMap.put(skuDetails.getSku(), skuDetails);
                }
            }
            mSkuDetailsFetchCode = skuFetchResponseCode;
            updateUi(mBillingManager.getBillingClientResponseCode(), skuFetchResponseCode, mSkuDetailsMap, getView());
        });
    }

    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.buy_subscription_fragment, container, false);
        final TextView errorText = rootView.findViewById(R.id.error_buy_subscription_text);
        final TextView subscriptionText = rootView.findViewById(R.id.buy_subscription_text);
        View buySubscriptionContainerView = rootView.findViewById(R.id.buy_subscription);
        View buySubscriptionImageView = rootView.findViewById(R.id.image_buy_subscription_button);
        final RadioGroup buySubscriptionRadioGroup = rootView.findViewById(R.id.sku_chooser);

        buySubscriptionContainerView.setBackground(ScrimUtil.makeCubicGradientScrimDrawable(0xaa000000, 8, Gravity.BOTTOM));
        buySubscriptionImageView.setVisibility(View.GONE);
        buySubscriptionRadioGroup.setVisibility(View.GONE);
        RadioButton btn = buySubscriptionRadioGroup.findViewById(R.id.test_subscription_radio);
        if(!BuildConfig.DEBUG) {
            buySubscriptionRadioGroup.removeViewInLayout(btn);
        }
        subscriptionText.setVisibility(View.GONE);
        errorText.setVisibility(View.GONE);

        buySubscriptionImageView.setOnClickListener((final View v) -> {
            String selectedSUK;
            int id = buySubscriptionRadioGroup.getCheckedRadioButtonId();
            switch (id) {
                case R.id.test_subscription_radio:
                    selectedSUK =  ITEM_SKU;
                    break;
                case R.id.six_months_subscription_radio:
                    selectedSUK =  ITEM_SKU_SIX_MONTHS;
                    break;
                default:
                case R.id.one_year_subscription_radio:
                    selectedSUK = ITEM_SKU_ONE_YEAR;
                    break;
            }
            buyClick(getActivity(), selectedSUK);
        });

        updateUi(mSubscriptionFetchCode, mSkuDetailsFetchCode, mSkuDetailsMap, rootView);

        rootView.setAlpha(0);
        rootView.animate().alpha(1f).setDuration(500);

        //EventBus.getDefault().register(this);
        ConnectivityReceiver.setOnConnectivityChangedListener(connectivityChangeListener);
        return rootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        //EventBus.getDefault().unregister(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mHandler.removeCallbacksAndMessages(null);
        ConnectivityReceiver.removeOnConnectivityChangedListener(connectivityChangeListener);
        if(null != mBillingManager){
            mBillingManager.destroy();
        }
    }

    private static final String connectivityListenerTag = "BuySubscriptionTag";
    ConnectivityReceiver.OnConnectivityChangeListener connectivityChangeListener = new ConnectivityReceiver.OnConnectivityChangeListener() {
        @Override
        public void onConnectivityChanged(boolean isConnected) {
            Activity activity = getActivity();
            if(null != activity) {
                activity.runOnUiThread(() -> mBillingManager.startServiceConnection(BuySubscriptionFragment.this::getSkuDetailsAndUpdateUi, ()-> updateUi(mBillingManager.getBillingClientResponseCode(), mSkuDetailsFetchCode, mSkuDetailsMap, getView())));
            }
        }

        @Override
        public String getConnectivityListenerTag() {
            return connectivityListenerTag;
        }
    };

    /*@Subscribe(threadMode = ThreadMode.MAIN)
    public void onNetworkConnectivityStatus(NetworkConnectivityStatusEvent e) {
        Context activity = getActivity();
        if(null != activity) {
            mBillingManager.startServiceConnection(this::getSkuDetailsAndUpdateUi, ()-> updateUi(mBillingManager.getBillingClientResponseCode(), mSkuDetailsFetchCode, mSkuDetailsMap, getView()));
        }
    }*/

    private void updateUi(@BillingClient.BillingResponse Integer billingConnectionResponseCode, @BillingClient.BillingResponse Integer skuDetailsFetchResponseCode, Map<String, SkuDetails> skuDetailsMap, View rootView) {
        if(rootView != null) {
            final TextView errorText = rootView.findViewById(R.id.error_buy_subscription_text);
            final View subscriptionText = rootView.findViewById(R.id.buy_subscription_text);
            View buySubscriptionImageView = rootView.findViewById(R.id.image_buy_subscription_button);
            RadioGroup buySubscriptionRadioGroup = rootView.findViewById(R.id.sku_chooser);

            if (null == billingConnectionResponseCode || BillingClient.BillingResponse.OK != billingConnectionResponseCode || null == skuDetailsFetchResponseCode || BillingClient.BillingResponse.OK != skuDetailsFetchResponseCode) {
                errorText.setVisibility(View.VISIBLE);
                buySubscriptionImageView.setVisibility(View.GONE);
                buySubscriptionRadioGroup.setVisibility(View.GONE);
                subscriptionText.setVisibility(View.GONE);

                // This fragment should only appear when there are no active subscriptions and the inventory was queried once.
                ConnectivityManager cm = null != getActivity()?(ConnectivityManager)getActivity().getSystemService(Context.CONNECTIVITY_SERVICE):null;

                NetworkInfo activeNetwork = null != cm  ? cm.getActiveNetworkInfo() : null;
                boolean hasNetworkConnectivity = activeNetwork != null && activeNetwork.isConnectedOrConnecting();

                if(!hasNetworkConnectivity){
                    String text = getResources().getString(R.string.error_internet_connectivity);
                    errorText.setText(text);
                }else if(null == billingConnectionResponseCode){
                    errorText.setText(R.string.billing_connecting_to_google_play);
                }else if (billingConnectionResponseCode == BillingClient.BillingResponse.BILLING_UNAVAILABLE) {
                    errorText.setText(R.string.billing_unavailable_android_market_version);
                }else if(billingConnectionResponseCode == BillingClient.BillingResponse.SERVICE_DISCONNECTED){
                    errorText.setText(getResources().getString(R.string.error_internet_connectivity));
                }else{
                    errorText.setText(R.string.billing_unavailable_unknown_reason);
                }
            } else {
                errorText.setVisibility(View.GONE);
                buySubscriptionImageView.setVisibility(View.VISIBLE);
                buySubscriptionRadioGroup.setVisibility(View.VISIBLE);
                subscriptionText.setVisibility(View.VISIBLE);
                // Update the labels with Price.
                String price = getPriceOfSKU(skuDetailsMap.get(ITEM_SKU_ONE_YEAR));

                RadioButton btn = buySubscriptionRadioGroup.findViewById(R.id.one_year_subscription_radio);
                btn.setText(String.format("%s %s", SKU_LABEL_ONE_YEAR_SUBSCRIPTION, price));

                price = getPriceOfSKU(skuDetailsMap.get(ITEM_SKU_SIX_MONTHS));
                btn = buySubscriptionRadioGroup.findViewById(R.id.six_months_subscription_radio);
                btn.setText(String.format("%s %s", SKU_LABEL_SIX_MONTHS_SUBSCRIPTION, price));

                buySubscriptionRadioGroup.invalidate();

                float animateDistance = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 100, getResources().getDisplayMetrics());
                buySubscriptionImageView.setAlpha(0);
                buySubscriptionImageView.setTranslationX(-3 * animateDistance);
                buySubscriptionImageView.animate().setDuration(3000).setInterpolator(new BounceInterpolator()).translationX(0).alpha(1);

                subscriptionText.setAlpha(0);
                subscriptionText.setTranslationY(-animateDistance / 2.5f);
                subscriptionText.animate().setDuration(2000).setInterpolator(new BounceInterpolator()).translationY(0).alpha(1);

                buySubscriptionRadioGroup.setAlpha(0);
                buySubscriptionRadioGroup.setTranslationY(animateDistance);
                buySubscriptionRadioGroup.animate().setDuration(2000).setInterpolator(new OvershootInterpolator()).translationY(0).alpha(1);
            }
        }
    }

    private String getPriceOfSKU(SkuDetails skuDetails){
        if(null != skuDetails){
            return skuDetails.getPrice();
        }

        return null;
    }

    public void buyClick(Activity activity, String itemSku) {
        if(BuildConfig.DEBUG) {
            //Tere Bhorase Payare Mai Laad Ladaya
            mBillingManager.initiatePurchaseFlow(activity, itemSku, BillingClient.SkuType.INAPP);
        }else{
            //Tere Bhorase Payare Mai Laad Ladaya
            mBillingManager.initiatePurchaseFlow(activity, itemSku, BillingClient.SkuType.SUBS);
        }
    }

    @Override
    public void onBillingClientConnectionEstablished() { }

    @Override
    public void onBillingClientSetupFinished(int resultCode) { }

    @Override
    public void onConsumeFinished(int result, String skuId, String token) {
        FirebaseJobDispatcher dispatcher = new FirebaseJobDispatcher(new GooglePlayDriver(getContext()));
        dispatcher.mustSchedule(dispatcher.newJobBuilder()
                .setService(SubscriptionCheckService.class) // the JobService that will be called
                .setTag("com.edu.worx.global.billing.SubscriptionCheckService")        // uniquely identifies the job
                .setRetryStrategy(RetryStrategy.DEFAULT_EXPONENTIAL)
                .setConstraints(Constraint.ON_ANY_NETWORK)
                .setReplaceCurrent(false)
                .setTrigger(Trigger.NOW)
                .build());
    }

    @Override
    public void onPurchasesUpdated(List<Purchase> purchases) {
        FirebaseJobDispatcher dispatcher = new FirebaseJobDispatcher(new GooglePlayDriver(getContext().getApplicationContext()));
        Pair<Purchase, Boolean> oneYearSubscription = hasPurchase(purchases, BuySubscriptionFragment.ITEM_SKU_ONE_YEAR);
        Pair<Purchase, Boolean> sixMonthsSubscription = hasPurchase(purchases, BuySubscriptionFragment.ITEM_SKU_SIX_MONTHS);
        Pair<Purchase, Boolean> testSubscription = BuildConfig.DEBUG ? hasPurchase(purchases, BuySubscriptionFragment.ITEM_SKU) : new Pair<>(null, false);
        if ( oneYearSubscription.second || sixMonthsSubscription.second || testSubscription.second) {
            ApplicationConfigurations.changeAdsDisplayLocalState(getContext().getApplicationContext(), false);

            Purchase p = testSubscription.first;
            Calendar cal = Calendar.getInstance();
            long subscriptionDuration = 24*60*60*1000 + 600 * 1000;
            if(sixMonthsSubscription.second){
                p = sixMonthsSubscription.first;
                cal.setTimeInMillis(p.getPurchaseTime());
                cal.add(Calendar.MONTH, 6);
                cal.add(Calendar.DAY_OF_MONTH, 3);
                subscriptionDuration = cal.getTimeInMillis();
            }else if(oneYearSubscription.second){
                p = oneYearSubscription.first;
                cal.setTimeInMillis(p.getPurchaseTime());
                cal.add(Calendar.YEAR, 1);
                cal.add(Calendar.DAY_OF_MONTH, 3);
                subscriptionDuration = cal.getTimeInMillis();
            }

            EventBus.getDefault().postSticky(new UserSubscriptionChangedEvent(true, p));
            long purchaseTime = p.getPurchaseTime();
            int windowStart = (int) ((subscriptionDuration - purchaseTime)/1000);

            dispatcher.mustSchedule(dispatcher.newJobBuilder()
                    .setService(SubscriptionCheckService.class) // the JobService that will be called
                    .setTag("com.edu.worx.global.billing.check.after.expiry.SubscriptionCheckService")        // uniquely identifies the job
                    .setRetryStrategy(RetryStrategy.DEFAULT_EXPONENTIAL)
                    .setConstraints(Constraint.ON_ANY_NETWORK)
                    .setReplaceCurrent(false)
                    .setTrigger(Trigger.executionWindow(windowStart, windowStart + (60*60*24)))
                    .build());
        }else{
            ApplicationConfigurations.changeAdsDisplayLocalState(getContext().getApplicationContext(),true);
            EventBus.getDefault().postSticky(new UserSubscriptionChangedEvent(false, null));
        }
    }

    public Pair<Purchase, Boolean> hasPurchase(List<Purchase> purchases, String sku){
        for(Purchase p : purchases){
            if(sku.equals(p.getSku())){
                return new Pair<>(p, true);
            }
        }

        return new Pair<>(null, false);
    }

    @Override
    public void onLaunchResults(@BillingClient.BillingResponse int result, List<Purchase> purchases) {
        if(BillingClient.BillingResponse.OK == result) {
            mListener.onPurchaseSuccess();
        }else{
            mListener.onPurchaseFailed();
        }
    }
}
