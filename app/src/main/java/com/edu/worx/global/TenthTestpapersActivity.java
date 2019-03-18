package com.edu.worx.global;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import java.io.File;
import java.io.IOException;

import static java.security.AccessController.getContext;

public class TenthTestpapersActivity extends AppCompatActivity {
    public static final String FIREBASE_FILE_PATH = "selected_year";
    public static String selected_class = "selected_class";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        String[] subjectArray = new String[0];
        float      screenLeftMargin;
        float      screenTopMargin;

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tenth_testpapers);

        // Get a support ActionBar corresponding to this toolbar and enable the Up button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        DisplayMetrics displayMetrics = getApplicationContext().getResources().getDisplayMetrics();
        float dpHeight = displayMetrics.heightPixels / displayMetrics.density;
        float dpWidth = displayMetrics.widthPixels / displayMetrics.density;
        boolean verticalOrientation = true; // dpHeight > dpWidth;
        int divFactor = 9;
        LinearLayout layout = (LinearLayout) findViewById(R.id.dynamicSubjectLayout);
        selected_class = getIntent().getStringExtra( TenthTestpapersActivity.FIREBASE_FILE_PATH);

        if (verticalOrientation)
            screenLeftMargin = dpWidth / 2;
        else
            screenLeftMargin = dpWidth / 4;

        try {
            String path = selected_class;

            subjectArray = getAssets().list(path);
        }
        catch (IOException ignored) {}

        screenTopMargin = dpHeight / divFactor ;
        LinearLayout.LayoutParams parms;
        LinearLayout.LayoutParams parmsFirst;
        if (verticalOrientation) {
              parmsFirst = new LinearLayout.LayoutParams((int) getPixel(150f), (int) getPixel(screenTopMargin / 2));
              parms = new LinearLayout.LayoutParams((int) getPixel(150f), (int) getPixel(screenTopMargin / 2.0));
        }
        else {
                parmsFirst = new LinearLayout.LayoutParams((int) getPixel(150f), (int) getPixel(screenTopMargin / 2));
                parms = new LinearLayout.LayoutParams((int) getPixel(150f), (int) getPixel(screenTopMargin / 2.0));
        }

        parmsFirst.topMargin = (int)(getPixel(screenTopMargin/1.5));
        parmsFirst.leftMargin = (int) getPixel(screenLeftMargin);

        //.setPadding(0, (int)(getPixel(screenTopMargin)), 0, 0);
        for (int i = 0; i < subjectArray.length; i++) {
                final Button myButton = new Button(this);
                myButton.setText(subjectArray[i]);
                myButton.setId(i);
                if (!verticalOrientation && (i > subjectArray.length / 2))
                    parms.leftMargin = (int) getPixel(3 * screenLeftMargin);
                else {

                    parms.leftMargin = (int) getPixel(screenLeftMargin);

                }
            parms.topMargin = 20;

                myButton.setTextSize(9f);
                //myButton.setHeight(50);
                if (i==0)
                    myButton.setLayoutParams(parmsFirst);
                else
                    myButton.setLayoutParams(parms);

            myButton.setBackground(ContextCompat.getDrawable(this, R.drawable.blackbutton));
            layout.addView(myButton);

            myButton.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                String selectedSample = myButton.getText().toString();

                                                String filePath = "assets" + File.separator + selected_class + File.separator + selectedSample + File.separator;
                                                Intent intent = new Intent (TenthTestpapersActivity.this, SwipeViewActivity.class);
                                                intent.putExtra(DisplaySubjectsActivity.FIREBASE_FILE_PATH, filePath);
                                                String localDirName = filePath.replace('/', '_');
                                                File mydir = getDir(localDirName, Context.MODE_PRIVATE);

                                                File localFile = new File(mydir, "paper.pdf");

                                                if (localFile.exists() || isInternetConnected())
                                                  startActivity(intent);
                                            }
                                        }
            );
        }
   }


    private boolean isInternetConnected() {
        ConnectivityManager conMgr = (ConnectivityManager) getApplicationContext().getSystemService (Context.CONNECTIVITY_SERVICE);

        // ARE WE CONNECTED TO THE NET
        if (conMgr.getActiveNetworkInfo() != null
                && conMgr.getActiveNetworkInfo().isAvailable()
                && conMgr.getActiveNetworkInfo().isConnected()) {

            return true;


        } else {
            /*
            Intent mainIntent = new Intent(getContext(), DisplaySubjectsActivity.class);
            mainIntent.putExtra(SELECTED_CLASS, getArguments().getString(ARG_PARAM_SELECTED_CLASS));
            startActivity(mainIntent);
*/
            AlertDialog.Builder builder = new AlertDialog.Builder(this);

            builder.setMessage("NO Internet Connection").setTitle("ËRROR !!");


            AlertDialog alertDialog = builder.create();

            alertDialog.show();
            return false;


        }
    }
    private float getPixel(double dp){
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, (float)dp, getResources().getDisplayMetrics());
    }
}
