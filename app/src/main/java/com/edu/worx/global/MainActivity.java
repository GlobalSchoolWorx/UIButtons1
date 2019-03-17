package com.edu.worx.global;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toolbar;

import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingManager;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.SkuDetails;
import com.edu.worx.global.billing.BuySubscriptionFragment;
import com.edu.worx.global.billing.SubscriptionCheckService;
import com.firebase.jobdispatcher.Constraint;
import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.firebase.jobdispatcher.Job;
import com.firebase.jobdispatcher.Lifetime;
import com.firebase.jobdispatcher.RetryStrategy;
import com.firebase.jobdispatcher.Trigger;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicBoolean;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;

public class MainActivity extends DisplayMenuActivity {
    AtomicBoolean alreadyStarted = new AtomicBoolean(false);
    boolean promoValid = false;
    String errorMsg ="Free Promo Offer has ended.Please Upgrade to the latest version from Google Play.";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_class);
        setUpBilling();
        android.support.v7.widget.Toolbar toolbar = findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setLogo(R.drawable.global_school_worx);
            getSupportActionBar().setDisplayUseLogoEnabled(true);
        }

        invalidateOptionsMenu();

        MobileAds.initialize(this, "ca-app-pub-3060173573728154~4462719073");
        scheduleSubscriptionCheck();

        DisplayMetrics displayMetrics = getApplicationContext().getResources().getDisplayMetrics();
        float dpHeight = displayMetrics.heightPixels / displayMetrics.density;
        float dpWidth = displayMetrics.widthPixels / displayMetrics.density;
        Button wgButton = findViewById(R.id.worksheetGeneratorButton);
        FirebaseStorage inst = FirebaseStorage.getInstance();
        StorageReference mStorageReference = inst.getReference("config.xml");
        Properties properties = new Properties();
        File mydir = getDir("", Context.MODE_PRIVATE);
        File propFile = new File(mydir, "config.xml");

        try {
              if(propFile.exists()) {
                propFile.delete();
            }

            propFile.createNewFile();
            FileDownloadTask  fileDownloadTask = mStorageReference.getFile(propFile);

            fileDownloadTask.addOnSuccessListener(this, new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                    try(InputStream ios = new FileInputStream(propFile)) {
                        properties.loadFromXML(ios);

                        promoValid = properties.getProperty("free_offer") != null && properties.getProperty("free_offer").equalsIgnoreCase("true");

                        if (properties.getProperty("error_msg") != null) {
                            errorMsg = properties.getProperty("error_msg");
                        } else {
                            errorMsg = "Free Promo Offer has ended.Please Upgrade to the latest version from Google Play.";
                        }

                    } catch (IOException ignore) {
                    } finally{
                        if(!alreadyStarted.get())
                          setupWorksheetBtnOnClickListener(wgButton, false);
                    }
                }
            });

            fileDownloadTask.addOnFailureListener(this, new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    if(!alreadyStarted.get())
                      setupWorksheetBtnOnClickListener(wgButton, false);
                }
            });
        } catch (Exception ignored) {}
          finally {
            setupWorksheetBtnOnClickListener(wgButton, true);
        }

        Button grammarbutton = findViewById(R.id.grammarButton);
        grammarbutton.setOnClickListener(uploadButton-> {

        Intent intent = new Intent (MainActivity.this, DrawerLayoutActivity.class);
        startActivity (intent);
        });
/*
        Button searchtutor = findViewById(R.id.searchTutor);
        searchtutor.setOnClickListener(searchTutor-> {
            Intent intent = new Intent (MainActivity.this, SearchLocalTutorActivity.class);
            startActivity (intent);
        });
*/
        Button uploadbutton = findViewById(R.id.uploadButton);
        uploadbutton.setOnClickListener(uploadButton-> {
            if(isInternetConnected()) {
                Intent intent = new Intent(MainActivity.this, DisplayUploadActivity.class);

                startActivity(intent);
            }
        });



        Button button1 = findViewById(R.id.buttonClass1);

        button1.setOnClickListener( slideView-> {
            String selectedClass = button1.getText().toString();
            selectedClass = selectedClass.intern();
            Intent intent = new Intent (MainActivity.this, DisplaySubjectsActivity.class);
            intent.putExtra( DisplaySubjectsActivity.SELECTED_CLASS, selectedClass);
            startActivity (intent);
        });


        Button button2 = findViewById(R.id.buttonClass2);
         button2.setOnClickListener(slideView -> {
            String selectedClass = button2.getText().toString();
            selectedClass = selectedClass.intern();
            Intent intent = new Intent (MainActivity.this, DisplaySubjectsActivity.class);
            intent.putExtra( DisplaySubjectsActivity.SELECTED_CLASS, selectedClass);
            startActivity (intent);
        });

        Button button3 = findViewById(R.id.buttonClass3);
        button3.setOnClickListener(slideView -> {
            String selectedClass = button3.getText().toString();
            selectedClass = selectedClass.intern();
            Intent intent = new Intent (MainActivity.this, DisplaySubjectsActivity.class);
            intent.putExtra( DisplaySubjectsActivity.SELECTED_CLASS, selectedClass);
            startActivity (intent);
        });

        Button button4 = findViewById(R.id.buttonClass4);
        button4.setOnClickListener(slideView -> {
            String selectedClass = button4.getText().toString();
            selectedClass = selectedClass.intern();
            Intent intent = new Intent (MainActivity.this, DisplaySubjectsActivity.class);
            intent.putExtra( DisplaySubjectsActivity.SELECTED_CLASS, selectedClass);
            startActivity (intent);
        });

        Button button5 = findViewById(R.id.buttonClass5);
        button5.setOnClickListener(slideView -> {
            String selectedClass = button5.getText().toString();
            selectedClass = selectedClass.intern();
            Intent intent = new Intent (MainActivity.this, DisplaySubjectsActivity.class);
            intent.putExtra( DisplaySubjectsActivity.SELECTED_CLASS, selectedClass);
            startActivity (intent);
        });

        Button button6 = findViewById(R.id.buttonClass6);
        button6.setOnClickListener(slideView -> {
            String selectedClass = button6.getText().toString();
            selectedClass = selectedClass.intern();
            Intent intent = new Intent (MainActivity.this, DisplaySubjectsActivity.class);
            intent.putExtra( DisplaySubjectsActivity.SELECTED_CLASS, selectedClass);
            startActivity (intent);
        });

        Button button7 = findViewById(R.id.buttonClass7);
        button7.setOnClickListener(slideView -> {
            String selectedClass = button7.getText().toString();
            selectedClass = selectedClass.intern();
            Intent intent = new Intent (MainActivity.this, DisplaySubjectsActivity.class);
            intent.putExtra( DisplaySubjectsActivity.SELECTED_CLASS, selectedClass);
            startActivity (intent);
        });

        Button button8 = findViewById(R.id.buttonClass8);
        button8.setOnClickListener(slideView -> {
            String selectedClass = button8.getText().toString();
            selectedClass = selectedClass.intern();
            Intent intent = new Intent (MainActivity.this, DisplaySubjectsActivity.class);
            intent.putExtra( DisplaySubjectsActivity.SELECTED_CLASS, selectedClass);
            startActivity (intent);
        });

        Button button9 = findViewById(R.id.buttonClass9);
        button9.setOnClickListener(slideView -> {
            String selectedClass = button9.getText().toString();
            selectedClass = selectedClass.intern();
            Intent intent = new Intent (MainActivity.this, DisplaySubjectsActivity.class);
            intent.putExtra( DisplaySubjectsActivity.SELECTED_CLASS, selectedClass);
            startActivity (intent);
        });

        Button button10 = findViewById(R.id.buttonClass10);
        button10.setOnClickListener(slideView -> {
            String selectedClass = button10.getText().toString();
            selectedClass = selectedClass.intern();
            Intent intent = new Intent (MainActivity.this, DisplayPapersWorksheetActivity.class);
            intent.putExtra( DisplaySubjectsActivity.SELECTED_CLASS, selectedClass);
            intent.putExtra( DisplaySubjectsActivity.SELECTED_CLASS_SUBJECT, selectedClass);
            startActivity (intent);
        });

        Button consume = findViewById(R.id.consume);
        consume.setOnClickListener(v -> {consumeSubscription();});

    }

    private void scheduleSubscriptionCheck(){
        FirebaseJobDispatcher dispatcher = new FirebaseJobDispatcher(new GooglePlayDriver(this));

        Job myJob = dispatcher.newJobBuilder()
                .setService(SubscriptionCheckService.class) // the JobService that will be called
                .setTag("com.edu.worx.global.billing.check.regular.interval.SubscriptionCheckService")        // uniquely identifies the job
                .setRetryStrategy(RetryStrategy.DEFAULT_EXPONENTIAL)
                .setRecurring(true)
                .setLifetime(Lifetime.FOREVER)
                .setConstraints(Constraint.ON_ANY_NETWORK)
                .setReplaceCurrent(false)
                //.setTrigger(Trigger.NOW)
                .setTrigger(Trigger.executionWindow(3*60*60, 4*60*60))
                //.setTrigger(Trigger.executionWindow(3, 4))
                .build();
        dispatcher.mustSchedule(myJob);

        dispatcher.mustSchedule(dispatcher.newJobBuilder()
                .setService(SubscriptionCheckService.class) // the JobService that will be called
                .setTag("com.edu.worx.global.billing.check.now.SubscriptionCheckService")        // uniquely identifies the job
                .setRetryStrategy(RetryStrategy.DEFAULT_EXPONENTIAL)
                .setConstraints(Constraint.ON_ANY_NETWORK)
                .setReplaceCurrent(true)
                .setTrigger(Trigger.NOW)
                .build());
    }

    private void setupWorksheetBtnOnClickListener(Button wgButton, boolean override) {
        wgButton.setEnabled(true);
        alreadyStarted.set(true);
        if(override)
            promoValid = true;

        wgButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isInternetConnected()) {
                    if(!promoValid) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                        builder.setMessage(errorMsg).setTitle("Error!").show();
                    } else {
                        Intent intent = new Intent (MainActivity.this, WorksheetGeneratorActivity.class);
                        startActivity (intent);
                    }
                }
            }
        });

    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem item = menu.findItem(R.id.show_answers);
        item.setVisible(FALSE);
        item = menu.findItem(R.id.hide_answers);
        item.setVisible(FALSE);
        item = menu.findItem(R.id.download_pdf);
        item.setVisible(FALSE);

        return TRUE;
    }

    public  boolean isInternetConnected() {

    ConnectivityManager conMgr = (ConnectivityManager) getApplicationContext().getSystemService (Context.CONNECTIVITY_SERVICE);

    // ARE WE CONNECTED TO THE NET
    if (conMgr.getActiveNetworkInfo() != null
        && conMgr.getActiveNetworkInfo().isAvailable()
        && conMgr.getActiveNetworkInfo().isConnected()) {

            return true;


    } else {
      AlertDialog.Builder builder = new AlertDialog.Builder(this);
      builder.setMessage("NO Internet Connection").setTitle("ËRROR !!");
      AlertDialog alertDialog = builder.create();
      alertDialog.show();
      return false;
    }
    }

    private BillingManager mBillingManager = null;
    private boolean setUpComplete = false;
    private void setUpBilling(){
        mBillingManager = new BillingManager(this, new BillingManager.BillingUpdatesListener() {

            @Override
            public void onPurchasesUpdated(List<Purchase> purchases) {
            }

            @Override
            public void onBillingClientConnectionEstablished() {
            }

            @Override
            public void onConsumeFinished(int result, String skuId, String token) {
            }

            @Override
            public void onLaunchResults(int result, List<Purchase> purchases) {
            }

            @Override
            public void onBillingClientSetupFinished(@BillingClient.BillingResponse int resultCode) {
                Button consume = findViewById(R.id.consume);
                consume.setVisibility(BuildConfig.DEBUG ? View.VISIBLE : View.GONE);
            }
        });
    }

    public void consumeSubscription() {
        Purchase testPurchase = BuildConfig.DEBUG ? mBillingManager.getPurchase(BuySubscriptionFragment.ITEM_SKU) : null;
        if ( null != testPurchase) {
            String token = testPurchase.getPurchaseToken();
            mBillingManager.consumeAsync(token);
        }
    }
}
