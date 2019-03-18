package com.edu.worx.global;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.itextpdf.text.pdf.parser.Line;

import java.io.File;
import java.io.IOException;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;

public class DisplaySubjectsActivity extends DisplayMenuActivity {

    public static final String SELECTED_CLASS = "SELECTED_CLASS";
    public static final String SELECTED_CLASS_SUBJECT = "SELECTED_CLASS_SUBJECT";
    public static final String FIREBASE_FILE_PATH = "FIREBASE_FILE_PATH";
    public static String selected_class = "";
    public static String selected_class_subject = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        String[] subjectArray = new String[0];
        float      screenLeftMargin;
        float      screenTopMargin;

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_subjects);
        invalidateOptionsMenu();
        /* AD CONTENT REQUIRED PRIVACY POLICY */
        AdView mAdView = findViewById(R.id.adView2);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        // my_child_toolbar is defined in the layout file
       // setSupportActionBar(findViewById(R.id.cl))
        selected_class = getIntent().getStringExtra( DisplaySubjectsActivity.SELECTED_CLASS);
        // Get a support ActionBar corresponding to this toolbar and enable the Up button
        android.support.v7.widget.Toolbar toolbar = findViewById(R.id.subjects_toolbar);
        setSupportActionBar(toolbar);
        if ( getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            getSupportActionBar().setTitle("Class: " + selected_class + "            SUBJECTS");
        }

        DisplayMetrics displayMetrics = getApplicationContext().getResources().getDisplayMetrics();
        float dpHeight = displayMetrics.heightPixels / displayMetrics.density;
        float dpWidth = displayMetrics.widthPixels / displayMetrics.density;
        boolean verticalOrientation = true; //dpHeight > dpWidth;
        int divFactor = 9;
        LinearLayout.LayoutParams parmsTV;
        LinearLayout layout = findViewById(R.id.dynamicSubjectLayout);


        if (verticalOrientation)
            screenLeftMargin = dpWidth / 2;
        else
            screenLeftMargin = dpWidth / 4;

        try {
            String path = selected_class;
            if(getAssets() != null && path != null)
              subjectArray = getAssets().list(path);
        }
        catch (IOException ignored) {}
        screenTopMargin = dpHeight / divFactor ;


        for (int i = 0; i < subjectArray.length; i++) {
            final Button myButton = new Button(this);
            myButton.setText(subjectArray [i]);
            myButton.setId(i);
            LinearLayout.LayoutParams parms;

            if (verticalOrientation)
                parms = new LinearLayout.LayoutParams((int)getPixel(dpWidth/2), LinearLayout.LayoutParams.WRAP_CONTENT);
            else
                parms = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT/*(int)getPixel(100f), (int)getPixel(screenTopMargin/1.5) */, LinearLayout.LayoutParams.WRAP_CONTENT);

            parms.bottomMargin = (int) getPixel(5);
            if(i==0) {
                parms.topMargin = (int)(getPixel(screenTopMargin/3)) + 100;

            }
            if (!verticalOrientation && (i > subjectArray.length /2)) {
                parms.leftMargin = (int) getPixel(3 * screenLeftMargin);
            }else {
                parms.leftMargin = (int) getPixel(dpWidth/4.5);
            }

            myButton.setTextSize(15f);
            myButton.setTextColor(Color.WHITE);
            myButton.setLayoutParams(parms);
            myButton.setBackground(ContextCompat.getDrawable(this, R.drawable.blackbutton));
            layout.addView(myButton);

            myButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String selectedSubject = myButton.getText().toString();
                    String filePath = "assets" + File.separator + selected_class + File.separator + selectedSubject + File.separator;
                    String localPath = "assets" + "_" + selected_class + "_" + selectedSubject;
                    selected_class_subject = selected_class + File.separator + selectedSubject + File.separator + "Testpapers";
                    Intent intent = new Intent(getApplicationContext(), DisplayPapersWorksheetActivity.class);
                    intent.putExtra(DisplaySubjectsActivity.FIREBASE_FILE_PATH, filePath);
                    intent.putExtra(DisplaySubjectsActivity.SELECTED_CLASS, selected_class);
                    intent.putExtra(DisplaySubjectsActivity.SELECTED_CLASS_SUBJECT, selected_class_subject);
                    String localDirName = filePath.replace('/', '_');
                    File mydir = getApplicationContext().getDir(localDirName, Context.MODE_PRIVATE);

                    File localFile = new File(mydir, "paper.pdf");

                    if (localFile.exists() || isInternetConnected())
                        startActivity(intent);
                }
            });
        }
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
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("SELECTED_CLASS", selected_class);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        selected_class = savedInstanceState.getString("SELECTED_CLASS");
    }

    private float getPixel(double dp){
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, (float)dp, getResources().getDisplayMetrics());
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

}