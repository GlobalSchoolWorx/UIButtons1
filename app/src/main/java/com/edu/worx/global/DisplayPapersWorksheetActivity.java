package com.edu.worx.global;


import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.edu.worx.global.signin.GoogleSignInActivity;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentTabHost;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;

public class DisplayPapersWorksheetActivity extends DisplayMenuActivity implements FragmentWorksheets.OnFragmentInteractionListener, FragmentTestpapers.OnFragmentInteractionListener,
                                                                                   FragmentHomework.OnFragmentInteractionListener{
    String TAB_1_TAG = "TEST PAPERS";
    String TAB_2_TAG = "WORKSHEETS";
    String TAB_3_TAG = "HOMEWORK";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        String selected_class_subject = getIntent().getStringExtra(DisplaySubjectsActivity.SELECTED_CLASS_SUBJECT);
        String selected_class = getIntent().getStringExtra(DisplaySubjectsActivity.SELECTED_CLASS);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_papers_worksheet);

        Toolbar toolbar = findViewById(R.id.paper_toolbar);
        setSupportActionBar(toolbar);

        // Get a support ActionBar corresponding to this toolbar and enable the Up button
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            getSupportActionBar().setTitle(selected_class_subject);
        }

        FragmentTabHost tabHost = (FragmentTabHost)findViewById (R.id.tabHost);
        tabHost.setup(this, getSupportFragmentManager(), android.R.id.tabcontent);

        FragmentTabHost.TabSpec spec1 = tabHost.newTabSpec(TAB_1_TAG);
        spec1.setIndicator(TAB_1_TAG);
        FragmentTabHost.TabSpec spec2 = tabHost.newTabSpec(TAB_2_TAG);
        spec2.setIndicator (TAB_2_TAG);
        FragmentTabHost.TabSpec spec3 = tabHost.newTabSpec(TAB_3_TAG);
        spec3.setIndicator (TAB_3_TAG);
        Bundle args = new Bundle();
        args.putString(FragmentTestpapers.ARG_SELECTED_CLASS, getIntent().getStringExtra( DisplaySubjectsActivity.SELECTED_CLASS));
        args.putString(FragmentTestpapers.ARG_SELECTED_CLASS_SUBJECT, selected_class_subject);

        tabHost.addTab(spec1, FragmentTestpapers.class, args);

      /* Disable Worksheet Tab for Now  15 Oct 2018 IK */
      if(selected_class.contains("Tenth") ) {


          if ( GoogleSignInActivity.isHomeworkTabAccessible())
            tabHost.addTab(spec3, FragmentHomework.class, args);

          tabHost.addTab(spec2, FragmentWorksheets.class, args);
      }
    }


    @Override
    public void onFragmentInteraction(Uri uri) {

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
