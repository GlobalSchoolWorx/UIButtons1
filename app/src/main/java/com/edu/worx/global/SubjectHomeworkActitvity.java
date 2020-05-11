package com.edu.worx.global;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Scroller;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.ArrayList;

import androidx.annotation.MainThread;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProviders;

public class SubjectHomeworkActitvity extends AppCompatActivity {

    public static final String FIREBASE_SEARCH_PATH = "FIREBASE_SEARCH_PATH";
    private static final String TAG = SubjectHomeworkActitvity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        String searchPath = getIntent().getStringExtra(SubjectHomeworkActitvity.FIREBASE_SEARCH_PATH);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subject_homework_actitvity);
        final LinearLayout progressBar = findViewById(R.id.progressBarLayout2);
        progressBar.bringToFront();

        MyViewModel model = ViewModelProviders.of(this).get(MyViewModel.class);
        model.getHomeworkSet(searchPath, "UIN").observe(this, (result) -> {
        //Log.i(TAG, result.toString());
        addHWLayout (result);

        });

    }


    public void addHWLayout (ArrayList<HomeworkSet> hwSetArr) {

        TableLayout mTableLayout = findViewById(R.id.table_layout);

        int index = 1;

        final LinearLayout progressBar = findViewById(R.id.progressBarLayout2);
        progressBar.setVisibility(View.GONE);
        mTableLayout.removeAllViews();
        float smallTextSize = 12;
        float textSize = 40;
        int leftRowMargin = 10;
        int rightRowMargin = 10;
        int topRowMargin = 10;
        int bottomRowMargin = 10;

        String odd_color_string = "f4e7f4";
        String even_color_string = "f4e7ff";
        int counter = 1;
        int color_num = Integer.parseInt(odd_color_string, 16);
        String backup_issuedate = "";
        String color_string;
        String text_color_string = "#000000";

        setHeaders(mTableLayout);
        for(HomeworkSet hw  : hwSetArr) {

            TableRow tr = new TableRow(this);
            if(backup_issuedate.isEmpty())
                backup_issuedate = hw.ISSUEDATE;
            if(!backup_issuedate.equals(hw.ISSUEDATE) ) {
              //  color_num = (int)Math.floor(Math.random()*16777215);
                counter++;
                if((counter %2) == 0)
                     color_string = even_color_string;
                else
                     color_string = odd_color_string;
            }

            backup_issuedate = hw.ISSUEDATE;

            color_string = Integer.toHexString(color_num);
            color_string = "#"+color_string;
            tr.setId(index);
            index++;
            TableLayout.LayoutParams trParams = new
                    TableLayout.LayoutParams(0, TableLayout.LayoutParams.WRAP_CONTENT);
            TableRow.LayoutParams tvParams = new
                    TableRow.LayoutParams(0, 300, .20f);
            TableRow.LayoutParams tvParams2 = new
                    TableRow.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, .5f);
            tvParams.setMargins(leftRowMargin, topRowMargin, rightRowMargin,
                     bottomRowMargin);
            tvParams2.setMargins(leftRowMargin, topRowMargin, rightRowMargin,
                    bottomRowMargin);

            // trParams.setMargins(10,10,10,10);
            trParams.setMargins(leftRowMargin, topRowMargin, rightRowMargin,
                    bottomRowMargin);
            tr.setPadding(0,0,0,0);
            tr.setLayoutParams(trParams);

            TextView tv1 = new TextView(this);
            tv1.setTextColor(Color.parseColor( text_color_string)); //"#68228b"));
            tv1.setBackgroundColor(Color.parseColor(color_string));
            tv1.setText(hw.ISSUEDATE);
            tv1.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
            tv1.setLayoutParams(tvParams);
            tv1.setGravity(Gravity.LEFT);




            TextView tvSep = new TextView(this);

            TableRow.LayoutParams tvSepLay = new
            TableRow.LayoutParams(0, TableRow.LayoutParams.MATCH_PARENT);
            tvSepLay.span = 3;
            tvSep.setLayoutParams(tvSepLay);
            tvSep.setBackgroundColor(Color.parseColor("#d9d9d9"));
            tvSep.setWidth(10);





            TextView tv2 = new TextView(this);
            tv2.setTextColor(Color.parseColor(text_color_string )); // "#68228b"));

            tv2.setBackgroundColor(Color.parseColor(color_string ));//"#f4e7f4"));



            tv2.setText(hw.CONTENT);
            tv2.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
            tv2.setLayoutParams(tvParams2);
            tv2.setGravity(Gravity.LEFT);
            tv2.setSingleLine(false);
            tv2.setVerticalScrollBarEnabled(true);
            tv2.setScroller(new Scroller(getApplicationContext()));
            tv2.setMovementMethod(new ScrollingMovementMethod());

            if(hw.TYPE.equals("LINK")) {
                tv2.setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.play, 0);
                tv2.setOnClickListener(new View.OnClickListener() {
                    @Override
                     public void onClick(View view) {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(hw.CONTENT)));
                    }

                });
            } else if (hw.TYPE.equals("MSG")) {
                //tv2.setCompoundDrawablesWithIntrinsicBounds(R.drawable.global_school_worx, 0, 0, 0);
            }
              else{
                tv2.setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.download_small, 0);
                tv2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!GenericAPIs.isInternetConnected(getApplicationContext())) {
                            android.app.AlertDialog.Builder subsErrorBuilder = new android.app.AlertDialog.Builder(SubjectHomeworkActitvity.this);
                            subsErrorBuilder.setMessage("No Internet Connection").setTitle("ERROR!");
                        } else {
                            String searchPath = getIntent().getStringExtra(SubjectHomeworkActitvity.FIREBASE_SEARCH_PATH);

                            searchPath = searchPath + "/" + hw.CONTENT;
                            DisplayUtils.displayDocument(SubjectHomeworkActitvity.this, searchPath, "Homework", hw.TYPE);
                        }
                    }
                });


            }

            TextView tv3 = new TextView(this);
            tv3.setTextColor(Color.parseColor(text_color_string )); // "#68228b"));
            tv3.setBackgroundColor(Color.parseColor(color_string));
            tv3.setText(hw.DUEDATE);
            tv3.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
            tv3.setLayoutParams(tvParams);
            tv3.setGravity(Gravity.LEFT);
            tr.addView(tv1);
            tr.addView(tv2);

            tr.addView(tv3);
            mTableLayout.setStretchAllColumns(true);
            mTableLayout.addView(tr, trParams);

            /****************************************************************/
            /***HORIZONTAL SEPARATOR LAYOUT***/
            if (index > 0) {
                // add separator row
                final TableRow trSep = new TableRow(this);
                TableLayout.LayoutParams trParamsSep = new
                        TableLayout.LayoutParams(0,
                        TableLayout.LayoutParams.WRAP_CONTENT);
                trParamsSep.setMargins(leftRowMargin, topRowMargin,
                        rightRowMargin, bottomRowMargin);
                trSep.setLayoutParams(trParamsSep);
                TextView tvSep1 = new TextView(this);
                TableRow.LayoutParams tvSepLay1 = new
                        TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT);
                tvSepLay1.span = 3;
                tvSep1.setLayoutParams(tvSepLay1);
                tvSep1.setBackgroundColor(Color.parseColor("#d9d9d9"));
                tvSep1.setHeight(1);
                trSep.addView(tvSep1);
                mTableLayout.addView(trSep, trParamsSep);
            }

      }


    }

    void setHeaders (TableLayout mTableLayout) {
        TableRow tr = new TableRow(this);
        int leftRowMargin = 10;
        int rightRowMargin = 10;
        int topRowMargin = 10;
        int bottomRowMargin = 10;
        String color_string = "#f8228b";
        String text_color_string = "#000000";
        int textSize = 40;


        TableLayout.LayoutParams trParams = new
                TableLayout.LayoutParams(0, TableLayout.LayoutParams.WRAP_CONTENT);
        TableRow.LayoutParams tvParams = new
                TableRow.LayoutParams(0, 200, .20f);
        TableRow.LayoutParams tvParams2 = new
                TableRow.LayoutParams(0, 200, .5f);
        tvParams.setMargins(leftRowMargin, topRowMargin, rightRowMargin,
                bottomRowMargin);
        tvParams2.setMargins(leftRowMargin, topRowMargin, rightRowMargin,
                bottomRowMargin);

        // trParams.setMargins(10,10,10,10);
        trParams.setMargins(leftRowMargin, topRowMargin, rightRowMargin,
                bottomRowMargin);
        tr.setPadding(0,0,0,0);
        tr.setLayoutParams(trParams);
        tr.setId(0);

        TextView tv1 = new TextView(this);
        tv1.setTextColor(Color.parseColor( text_color_string)); //"#68228b"));
        tv1.setBackgroundColor(Color.parseColor(color_string));
        tv1.setText("ISSUE DATE");
        tv1.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
        tv1.setLayoutParams(tvParams);
        tv1.setGravity(Gravity.CENTER);

        TextView tv2 = new TextView(this);
        tv2.setTextColor(Color.parseColor( text_color_string)); //"#68228b"));
        tv2.setBackgroundColor(Color.parseColor(color_string));
        tv2.setText("CONTENT");
        tv2.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
        tv2.setLayoutParams(tvParams2);
        tv2.setGravity(Gravity.CENTER);

        TextView tv3 = new TextView(this);
        tv3.setTextColor(Color.parseColor( text_color_string)); //"#68228b"));
        tv3.setBackgroundColor(Color.parseColor(color_string));
        tv3.setText("DUE DATE");
        tv3.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
        tv3.setLayoutParams(tvParams);
        tv3.setGravity(Gravity.CENTER);

        tr.addView(tv1);;
        tr.addView(tv2);
        tr.addView(tv3);

        mTableLayout.addView(tr);

    }

    public static class MyViewModel extends ViewModel {
        public MutableLiveData<ArrayList<HomeworkSet>> homeworkSet;

        public MyViewModel() {}

        @MainThread
        public MutableLiveData<ArrayList<HomeworkSet>> getHomeworkSet(String searchPath, String node) {
            homeworkSet = new MutableLiveData<>();
            new Thread(()-> {
                GenericFirebaseQuery quer = new GenericFirebaseQuery();
                homeworkSet.postValue(quer.queryDatabase(searchPath, node, HomeworkSet.class));
            }).start();
            return homeworkSet;
        }
    }
}
