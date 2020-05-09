package com.android.billingclient.api;

/*
 * Copyright 2017 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;

import com.android.billingclient.api.BillingClient.BillingResponse;
import com.android.billingclient.api.BillingClient.FeatureType;
import com.android.billingclient.api.BillingClient.SkuType;
import com.android.billingclient.api.Purchase.PurchasesResult;
import com.android.billingclient.util.BillingHelper;
import com.edu.worx.global.BuildConfig;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Handles all the interactions with Play Store (via Billing library), maintains connection to
 * it through BillingClient and caches temporary states/data if needed
 */
public class BillingManager implements PurchasesUpdatedListener {
    private static final String RESPONSE_CONSUME_INTENT_ACTION = "consume_response_intent_action";
    private static final String RESPONSE_CONSUME_CODE = "response_consume_code_key";
   // private static final String UUID_CODE = "uuid_code_key";
    private static final String RESPONSE_PURCHASE_TOKEN = "response_purchase_token_key";

    // Default value of mBillingClientResponseCode until BillingManager was not yet initialized
    private static final int BILLING_MANAGER_NOT_INITIALIZED  = -1;

    private static final String TAG = "BillingManager";

    /** A reference to BillingClient **/
    private BillingClient mBillingClient;

    /**
     * True if billing service is connected now.
     */
    private boolean mIsServiceConnected;

    private final BillingUpdatesListener mBillingUpdatesListener;

    private final Context mContext;

    private final List<Purchase> mPurchases = new ArrayList<>();

    private Set<String> mTokensToBeConsumed;

    private int mBillingClientResponseCode = BILLING_MANAGER_NOT_INITIALIZED;

    /* BASE_64_ENCODED_PUBLIC_KEY should be YOUR APPLICATION'S PUBLIC KEY
     * (that you got from the Google Play developer console). This is not your
     * developer public key, it's the *app-specific* public key.
     *
     * Instead of just storing the entire literal string here embedded in the
     * program,  construct the key at runtime from pieces or
     * use bit manipulation (for example, XOR with some other string) to hide
     * the actual key.  The key itself is not secret information, but we don't
     * want to make it easy for an attacker to replace the public key with one
     * of their own and then fake messages from the server.
     */
    private static final String BASE_64_ENCODED_PUBLIC_KEY = StringSecurity.getKey();

    /**
     * Listener to the updates that happen when purchases list was updated or consumption of the
     * item was finished
     */
    public interface BillingUpdatesListener {
        void onBillingClientConnectionEstablished();
        void onBillingClientSetupFinished(@BillingResponse int resultCode);
        void onConsumeFinished(@BillingResponse int result, String skuId, String token);
        void onPurchasesUpdated(List<Purchase> purchases);
        void onLaunchResults(@BillingResponse int result, List<Purchase> purchases);
    }

    public interface ServiceRequestListener{
        void onServiceExecuted(@BillingResponse int resultCode);
    }

    private final BroadcastReceiver onPurchaseFinishedReceiver =
            new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {

                    if (mBillingUpdatesListener == null) {
                        BillingHelper.logWarn(
                                TAG, "PurchasesUpdatedListener is null - no way to return the response.");
                        return;
                    }
                    // Receiving the result from local broadcast and triggering a callback on listener.
                    @BillingResponse
                    int responseCode = intent.getIntExtra(ProxyBillingActivity.RESPONSE_CODE, BillingResponse.ERROR);
                    Bundle resultData = intent.getBundleExtra(ProxyBillingActivity.RESPONSE_BUNDLE);
                    List<Purchase> purchases = BillingHelper.extractPurchases(resultData);
                    mBillingUpdatesListener.onLaunchResults(responseCode, purchases);
                }
            };

    //This is to ensure that other instance of Billing Manager get a notification about consumption, had the billing manager be singleton, we could have done this in consumeAsync() call.
    private final BroadcastReceiver onPurchaseConsumedReceiver =
            new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    // Receiving the result from local broadcast and triggering a callback on listener.
                    @BillingResponse int responseCode = intent.getIntExtra(RESPONSE_CONSUME_CODE, BillingResponse.ERROR);
                    String purchaseToken = intent.getStringExtra(RESPONSE_PURCHASE_TOKEN);
                    String skuId = null;
                    for (Purchase p : mPurchases) {
                        if (p.getPurchaseToken().equals(purchaseToken)) {
                            skuId = p.getSku();
                            if (BillingResponse.OK == responseCode) {
                                mPurchases.remove(p);
                            }
                            break;
                        }
                    }

                    if (null != mTokensToBeConsumed) {
                        mTokensToBeConsumed.remove(purchaseToken);
                    }

                    if (null != mBillingUpdatesListener) {
                        mBillingUpdatesListener.onConsumeFinished(responseCode, skuId, purchaseToken);
                    }
                }
            };

    //private static AtomicLong idGenerator = new AtomicLong(0L);
    //private long mId = -1L;
    public BillingManager(final Context context, final BillingUpdatesListener updatesListener) {
        this(context, updatesListener, false);
    }

    public BillingManager(Context context, final BillingUpdatesListener updatesListener, boolean lazySetup) {
        Log.d(TAG, "Creating Billing client.");

        //mId = idGenerator.getAndIncrement();
        mContext = context;
        mBillingUpdatesListener = updatesListener;
        mBillingClient = BillingClient.newBuilder(context).setListener(this).build();
        IntentFilter purchaseIntent = new IntentFilter(ProxyBillingActivity.RESPONSE_INTENT_ACTION);
        LocalBroadcastManager.getInstance(context).registerReceiver(onPurchaseFinishedReceiver, purchaseIntent);

        IntentFilter consumePurchaseIntent = new IntentFilter(RESPONSE_CONSUME_INTENT_ACTION);
        LocalBroadcastManager.getInstance(context).registerReceiver(onPurchaseConsumedReceiver, consumePurchaseIntent);

        if(null != mBillingUpdatesListener && !lazySetup) {
            Log.d(TAG, "Starting setup.");
            // Start setup. This is asynchronous and the specified listener will be called
            // once setup completes.
            // It also starts to report all the new purchases through onPurchasesUpdated() callback.
            startServiceConnection(() -> {
                // Notifying the listener that billing client is ready
                mBillingUpdatesListener.onBillingClientConnectionEstablished();
                // IAB is fully set up. Now, let's get an inventory of stuff we own.
                Log.d(TAG, "Setup successful. Querying inventory.");
                queryPurchases(mBillingUpdatesListener::onBillingClientSetupFinished);
            }, () -> mBillingUpdatesListener.onBillingClientSetupFinished(mBillingClientResponseCode));
        }
    }

    /**
     * Handle a callback that purchases were updated from the Billing library
     */
    @Override
    public void onPurchasesUpdated(int resultCode, List<Purchase> purchases) {
        if (resultCode == BillingResponse.OK) {
            for (Purchase purchase : purchases) {
                handlePurchase(purchase);
            }
            if(null != mBillingUpdatesListener) {
                mBillingUpdatesListener.onPurchasesUpdated(mPurchases);
            }
        } else if (resultCode == BillingResponse.USER_CANCELED) {
            Log.i(TAG, "onPurchasesUpdated() - user cancelled the purchase flow - skipping");
        } else {
            Log.w(TAG, "onPurchasesUpdated() got unknown resultCode: " + resultCode);
        }
    }

    /**
     * Start a purchase flow
     */
    public void initiatePurchaseFlow(final Activity activity, final String skuId, final @SkuType String billingType) {
        initiatePurchaseFlow(activity, skuId, null, billingType);
    }

    /**
     * Start a purchase or subscription replace flow
     */
    @SuppressWarnings("WeakerAccess")
    public void initiatePurchaseFlow(final Activity activity, final String skuId, final String oldSku, final @SkuType String billingType) {
        Runnable purchaseFlowRequest = () -> {
            Log.d(TAG, "Launching in-app purchase flow. Replace old SKU? " + (oldSku != null));
            BillingFlowParams purchaseParams = BillingFlowParams.newBuilder()
                    .setSku(skuId).setType(billingType).setOldSku(oldSku).build();
            @BillingResponse int launchCode =  mBillingClient.launchBillingFlow(activity, purchaseParams);
            if(BillingResponse.OK != launchCode){
                if(null != mBillingUpdatesListener){
                    mBillingUpdatesListener.onLaunchResults(launchCode, mPurchases);
                }
            }
        };

        executeServiceRequest(purchaseFlowRequest, () -> {
            if(null != mBillingUpdatesListener){
                mBillingUpdatesListener.onLaunchResults(mBillingClientResponseCode, mPurchases);
            }
        });
    }

    public Context getContext() {
        return mContext;
    }

    /**
     * Clear the resources
     */
    public void destroy() {
        Log.d(TAG, "Destroying the manager.");
        LocalBroadcastManager.getInstance(mContext).unregisterReceiver(onPurchaseFinishedReceiver);
        LocalBroadcastManager.getInstance(mContext).unregisterReceiver(onPurchaseConsumedReceiver);

        if (mBillingClient != null && mBillingClient.isReady()) {
            mBillingClient.endConnection();
            mBillingClient = null;
        }
    }

    public void querySkuDetailsAsync(@SkuType final String itemType, final List<String> skuList, final SkuDetailsResponseListener listener) {
        // Creating a runnable from the request to use it inside our connection retry policy below
        Runnable queryRequest = () -> {
            // Query the purchase async
            SkuDetailsParams.Builder params = SkuDetailsParams.newBuilder();
            params.setSkusList(skuList).setType(itemType);
            mBillingClient.querySkuDetailsAsync(params.build(), listener);
        };

        executeServiceRequest(queryRequest, ()->listener.onSkuDetailsResponse(mBillingClientResponseCode, null));
    }

    public void consumeAsync(final String purchaseToken) {
        // If we've already scheduled to consume this token - no action is needed (this could happen
        // if you received the token when querying purchases inside onReceive() and later from
        // onActivityResult()
        if (mTokensToBeConsumed == null) {
            mTokensToBeConsumed = new HashSet<>();
        } else if (mTokensToBeConsumed.contains(purchaseToken)) {
            Log.i(TAG, "Token was already scheduled to be consumed - skipping...");
            return;
        }
        mTokensToBeConsumed.add(purchaseToken);

        executeServiceRequest(() -> {
            // Consume the purchase async
            mBillingClient.consumeAsync(purchaseToken, (responseCode, purchaseToken1) -> {
                // If billing service was disconnected, we try to reconnect 1 time
                // (feel free to introduce your retry policy here).
                if(BillingResponse.OK == responseCode) {
                    /*GwallApplication app = (GwallApplication) mContext.getApplicationContext();
                    BillingTransactionManager.deleteBillingTransaction(app.getBillingDB(), purchaseToken1);*/

                    Intent intent = new Intent(RESPONSE_CONSUME_INTENT_ACTION);
                    intent.putExtra(RESPONSE_CONSUME_CODE, responseCode);
                    //intent.putExtra(UUID_CODE, mId);
                    intent.putExtra(RESPONSE_PURCHASE_TOKEN, purchaseToken1);
                    LocalBroadcastManager.getInstance(mContext).sendBroadcast(intent);
                }
            });
        }, () -> mTokensToBeConsumed.remove(purchaseToken));
    }

    /**
     * Returns the value Billing client response code or BILLING_MANAGER_NOT_INITIALIZED if the
     * clien connection response was not received yet.
     */
    public int getBillingClientResponseCode() {
        return mBillingClientResponseCode;
    }

    /**
     * Handles the purchase
     * <p>Note: Notice that for each purchase, we check if signature is valid on the client.
     * It's recommended to move this check into your backend.
     * See {@link Security#verifyPurchase(String, String, String)}
     * </p>
     * @param purchase Purchase to be handled
     */
    private void handlePurchase(Purchase purchase) {
        if (!BuildConfig.DEBUG && !verifyValidSignature(purchase.getOriginalJson(), purchase.getSignature())) {
            Log.i(TAG, "Got a purchase: " + purchase + "; but signature is bad. Skipping...");
            return;
        }

        Log.d(TAG, "Got a verified purchase: " + purchase);

        mPurchases.add(purchase);

        /*GwallApplication app = (GwallApplication) mContext.getApplicationContext();
        try {
            BillingTransactionManager.addOrUpdateBillingTransaction(app.getBillingDB(), BillingTransaction.parse(purchase.getOriginalJson()));
        } catch (JSONException e) {
            String productId = purchase.getSku();
            String packageName = purchase.getPackageName();
            Long purchaseTime = purchase.getPurchaseTime();
            String orderId = purchase.getOrderId();
            String purchaseToken = purchase.getPurchaseToken();

            BillingTransactionManager.addOrUpdateBillingTransaction(app.getBillingDB(), new BillingTransaction(orderId, productId, packageName, BillingTransaction.PurchaseState.PURCHASED, purchaseToken, purchaseTime));
        }*/
    }

    /**
     * Handle a result from querying of purchases and report an updated list to the listener
     */
    private void onQueryPurchasesFinished(PurchasesResult result) {
        // Have we been disposed of in the meantime? If so, or bad result code, then quit
        if (mBillingClient == null || result.getResponseCode() != BillingResponse.OK) {
            Log.w(TAG, "Billing client was null or result code (" + result.getResponseCode()
                    + ") was bad - quitting");
            return;
        }

        Log.d(TAG, "Query inventory was successful.");

        // Update the UI and purchases inventory with new list of purchases
        mPurchases.clear();
        onPurchasesUpdated(BillingResponse.OK, result.getPurchasesList());
    }

    /**
     * Checks if subscriptions are supported for current client
     * <p>Note: This method does not automatically retry for RESULT_SERVICE_DISCONNECTED.
     * It is only used in unit tests and after queryPurchases execution, which already has
     * a retry-mechanism implemented.
     * </p>
     */
    @SuppressWarnings("WeakerAccess")
    public boolean areSubscriptionsSupported() {
        int responseCode = mBillingClient.isFeatureSupported(FeatureType.SUBSCRIPTIONS);
        if (responseCode != BillingResponse.OK) {
            Log.w(TAG, "areSubscriptionsSupported() got an error response: " + responseCode);
        }
        return responseCode == BillingResponse.OK;
    }

    /**
     * Query purchases across various use cases and deliver the result in a formalized way through
     * a listener
     */
    public void queryPurchases(ServiceRequestListener serviceRequestListener) {
        Runnable queryToExecute = () -> {
            long time = System.currentTimeMillis();
            PurchasesResult purchasesResult = mBillingClient.queryPurchases(SkuType.INAPP);
            Log.i(TAG, "Querying purchases elapsed time: " + (System.currentTimeMillis() - time)
                    + "ms");
            // If there are subscriptions supported, we add subscription rows as well
            if (areSubscriptionsSupported()) {
                PurchasesResult subscriptionResult = mBillingClient.queryPurchases(SkuType.SUBS);
                Log.i(TAG, "Querying purchases and subscriptions elapsed time: "
                        + (System.currentTimeMillis() - time) + "ms");
                Log.i(TAG, "Querying subscriptions result code: "
                        + subscriptionResult.getResponseCode());

                if (subscriptionResult.getResponseCode() == BillingResponse.OK) {
                    if(null != subscriptionResult.getPurchasesList()) {
                        purchasesResult.getPurchasesList().addAll(subscriptionResult.getPurchasesList());
                    }
                } else {
                    Log.e(TAG, "Got an error response trying to query subscription purchases");
                }
            } else if (purchasesResult.getResponseCode() == BillingResponse.OK) {
                Log.i(TAG, "Skipped subscription purchases query since they are not supported");
            } else {
                Log.w(TAG, "queryPurchases() got an error response code: " + purchasesResult.getResponseCode());
            }
            onQueryPurchasesFinished(purchasesResult);
            if(null != serviceRequestListener){
                serviceRequestListener.onServiceExecuted(purchasesResult.getResponseCode());
            }
        };

        executeServiceRequest(queryToExecute, () ->{
            if(null != serviceRequestListener){
                serviceRequestListener.onServiceExecuted(mBillingClientResponseCode);
            }
        });
    }

    public void startServiceConnection(final Runnable executeOnSuccess, final Runnable executeOnFailure) {
        mBillingClient.startConnection(new BillingClientStateListener() {
            @Override
            public void onBillingSetupFinished(@BillingResponse int billingResponseCode) {
                Log.d(TAG, "Setup finished. Response code: " + billingResponseCode);
                mBillingClientResponseCode = billingResponseCode;
                if (billingResponseCode == BillingResponse.OK) {
                    mIsServiceConnected = true;
                    if (executeOnSuccess != null) {
                        executeOnSuccess.run();
                    }
                }else{
                    if (executeOnFailure != null) {
                        executeOnFailure.run();
                    }
                }
            }

            @Override
            public void onBillingServiceDisconnected() {
                mIsServiceConnected = false;
                if (executeOnFailure != null) {
                    executeOnFailure.run();
                }
            }
        });
    }

    private void executeServiceRequest(Runnable runnable, Runnable onFail) {
        if (mIsServiceConnected) {
            runnable.run();
        } else {
            // If billing service was disconnected, we try to reconnect 1 time.
            // (feel free to introduce your retry policy here).
            startServiceConnection(runnable, onFail);
        }
    }

    /**
     * Verifies that the purchase was signed correctly for this developer's public key.
     * <p>Note: It's strongly recommended to perform such check on your backend since hackers can
     * replace this method with "constant true" if they decompile/rebuild your app.
     * </p>
     */
    private boolean verifyValidSignature(String signedData, String signature) {
        // Some sanity checks to see if the developer (that's you!) really followed the
        // instructions to run this sample (don't put these checks on your app!)
        if (BASE_64_ENCODED_PUBLIC_KEY.contains("CONSTRUCT_YOUR")) {
            throw new RuntimeException("Please update your app's public key at: "
                    + "BASE_64_ENCODED_PUBLIC_KEY");
        }

        try {
            return Security.verifyPurchase(BASE_64_ENCODED_PUBLIC_KEY, signedData, signature);
        } catch (IOException e) {
            Log.e(TAG, "Got an exception trying to validate a purchase: " + e);
            return false;
        }
    }

    public boolean hasPurchase(String sku){
        for(Purchase p : mPurchases){
            if(sku.equals(p.getSku())){
                return true;
            }
        }

        return false;
    }

    public Purchase getPurchase(String skuId){
        for(Purchase p : mPurchases){
            if(skuId.equals(p.getSku())){
                return p;
            }
        }

        return null;
    }

    public String getPurchaseToken(String skuId){
        for(Purchase p : mPurchases){
            if(skuId.equals(p.getSku())){
                return p.getPurchaseToken();
            }
        }

        return null;
    }
}