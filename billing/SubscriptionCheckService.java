package com.edu.worx.global.billing;

import android.content.Context;

import com.android.billingclient.api.BillingClient.BillingResponse;
import com.android.billingclient.api.BillingManager;
import com.android.billingclient.api.Purchase;
import com.edu.worx.global.BuildConfig;
import com.edu.worx.global.events.UserSubscriptionChangedEvent;
import com.edu.worx.global.utils.ApplicationConfigurations;
import com.firebase.jobdispatcher.JobParameters;
import com.firebase.jobdispatcher.JobService;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

public class SubscriptionCheckService extends JobService {
    private BillingManager mBillingManager = null;
    @Override
    public boolean onStartJob(JobParameters job) {

        mBillingManager = new BillingManager(this, new BillingManager.BillingUpdatesListener() {
            @Override
            public void onBillingClientConnectionEstablished() { }

            @Override
            public void onBillingClientSetupFinished(int resultCode) {
                jobFinished(job, resultCode != BillingResponse.OK);
            }

            @Override
            public void onConsumeFinished(int result, String skuId, String token) { }

            @Override
            public void onPurchasesUpdated(List<Purchase> purchaseList) {

                if(null != purchaseList) {
                    handleInventoryFetched(getApplication(), mBillingManager);
                }
            }

            @Override
            public void onLaunchResults(int result, List<Purchase> purchases) {}
        });

        return true;
    }

    @Override
    public boolean onStopJob(JobParameters job) {
        return true;
    }

    public static void handleInventoryFetched(Context cxt, BillingManager billingManager){
        UserSubscriptionChangedEvent event = EventBus.getDefault().getStickyEvent(UserSubscriptionChangedEvent.class);
        final boolean lastSubscriptionStatus = null != event && event.isUserSubscribed();
        boolean currentSubscriptionStatus = false;
        Purchase oneYear = billingManager.getPurchase(BuySubscriptionFragment.ITEM_SKU_ONE_YEAR);
        Purchase sixMonths = billingManager.getPurchase(BuySubscriptionFragment.ITEM_SKU_SIX_MONTHS);
        Purchase testPurchase = BuildConfig.DEBUG ? billingManager.getPurchase(BuySubscriptionFragment.ITEM_SKU) : null;
        if ( null != oneYear || null != sixMonths || null != testPurchase) {
            currentSubscriptionStatus = true;
            ApplicationConfigurations.changeAdsDisplayLocalState(cxt.getApplicationContext(),false);
        } else {
            ApplicationConfigurations.changeAdsDisplayLocalState(cxt.getApplicationContext(),true);
        }

        if (lastSubscriptionStatus != currentSubscriptionStatus) {
            EventBus.getDefault().postSticky(new UserSubscriptionChangedEvent(currentSubscriptionStatus, null != oneYear ? oneYear : null != sixMonths ? sixMonths : testPurchase));
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if(null != mBillingManager){
            mBillingManager.destroy();
        }
    }
}
