package com.edu.worx.global.billing;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.WindowManager;
import android.widget.Toast;

import com.edu.worx.global.R;


public class BuySubscriptionActivity extends AppCompatActivity implements BuySubscriptionFragment.OnBuySubscriptionListener {
    private final String FRAGMENT_TAG = "current_fragment";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.buy_subscription_layout);

        if(null == savedInstanceState) {
            Bundle extraData = getIntent().getExtras();
            FragmentTransaction t = getSupportFragmentManager().beginTransaction();
            BuySubscriptionFragment currentFragment = new BuySubscriptionFragment();
            currentFragment.setArguments(extraData);
            t.replace(R.id.main_frame, currentFragment, FRAGMENT_TAG);
            t.commit();
        }
    }

    @Override
    public void onPurchaseSuccess() {
        Toast.makeText(this,"Subscription Succesful!", Toast.LENGTH_LONG).show();
        finish();
    }

    @Override
    public void onPurchaseFailed() { }
}
