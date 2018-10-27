package com.edu.worx.global;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.IdentityHashMap;
import java.util.InvalidPropertiesFormatException;
import java.util.Properties;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicBoolean;

import ng.max.slideview.SlideView;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;

public class MainActivity extends DisplayMenuActivity {
    AtomicBoolean alreadyStarted = new AtomicBoolean(false);
    boolean promoValid = false;
    String errorMsg ="Free Promo Offer has ended.Please Upgrade to the latest version from Google Play.";
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        getSupportActionBar().setLogo(R.drawable.global_school_worx);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        setContentView(R.layout.activity_display_class);
        invalidateOptionsMenu();

       /*
        if(!BuildConfig.DEBUG)
            //MobileAds.initialize(this, "ca-app-pub-3940256099942544~3347511713");
        //else   Test Ap Id
            MobileAds.initialize(this, "ca-app-pub-3060173573728154~4462719073" ); */

        /* AD CONTENT REQUIRED PRIVACY POLICY */
        AdView mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

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
        //    wgButton.setEnabled(false);
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
        } catch (Exception e) {}
          finally {
            setupWorksheetBtnOnClickListener(wgButton, true);
        }

        Button uploadbutton = findViewById(R.id.uploadButton);
        uploadbutton.setOnClickListener(uploadButton-> {
            Intent intent = new Intent (MainActivity.this, DisplayUploadActivity.class);

            startActivity (intent);
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
        MenuItem item = menu.findItem(R.id.download_pdf);
        item.setVisible(FALSE);
        return TRUE;
    }

    public boolean isInternetConnected() {
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
}
