package com.edu.worx.global.events;

import com.android.billingclient.api.Purchase;

public class UserSubscriptionChangedEvent {
    private final boolean mIsSubscribed;
    private final Purchase mPurchase;

    public UserSubscriptionChangedEvent(boolean isSubscribed, Purchase purchase){
        mIsSubscribed = isSubscribed;
        mPurchase = purchase;
    }

    public boolean isUserSubscribed(){
        return mIsSubscribed;
    }

    public Purchase getPurchase() {
        return mPurchase;
    }
}
