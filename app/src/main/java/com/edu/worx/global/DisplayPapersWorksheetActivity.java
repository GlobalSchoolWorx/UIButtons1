package com.edu.worx.global;


import android.app.AlertDialog;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentTabHost;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TabHost;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;

public class DisplayPapersWorksheetActivity extends DisplayMenuActivity implements FragmentWorksheets.OnFragmentInteractionListener, FragmentTestpapers.OnFragmentInteractionListener {
    String TAB_1_TAG = "TEST PAPERS";
    String TAB_2_TAG = "WORKSHEETS";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        String selected_class_subject = getIntent().getStringExtra(DisplaySubjectsActivity.SELECTED_CLASS_SUBJECT);
        String selected_class = getIntent().getStringExtra(DisplaySubjectsActivity.SELECTED_CLASS);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_papers_worksheet);

        // Get a support ActionBar corresponding to this toolbar and enable the Up button
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setTitle(selected_class_subject);

        FragmentTabHost tabHost = (FragmentTabHost)findViewById (R.id.tabHost);
        tabHost.setup(this, getSupportFragmentManager(), android.R.id.tabcontent);

        FragmentTabHost.TabSpec spec1 = tabHost.newTabSpec(TAB_1_TAG);
        spec1.setIndicator(TAB_1_TAG);
        FragmentTabHost.TabSpec spec2 = tabHost.newTabSpec(TAB_2_TAG);
        spec2.setIndicator (TAB_2_TAG);

        Bundle args = new Bundle();
        args.putString(FragmentTestpapers.ARG_SELECTED_CLASS, getIntent().getStringExtra( DisplaySubjectsActivity.SELECTED_CLASS));
        args.putString(FragmentTestpapers.ARG_SELECTED_CLASS_SUBJECT, selected_class_subject);

        tabHost.addTab(spec1, FragmentTestpapers.class, args);

      /* Disable Worksheet Tab for Now  15 Oct 2018 IK */
      if(selected_class.contains("Tenth"))
        tabHost.addTab(spec2, FragmentWorksheets.class, args);
    }


    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem item = menu.findItem(R.id.download_pdf);
        item.setVisible(FALSE);
        return TRUE;
    }

}
