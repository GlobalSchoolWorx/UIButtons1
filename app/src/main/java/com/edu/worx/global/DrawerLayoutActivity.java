package com.edu.worx.global;

import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.google.android.material.navigation.NavigationView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;


public class DrawerLayoutActivity extends AppCompatActivity implements HighFragment.OnFragmentInteractionListener, PrimaryFragment.OnFragmentInteractionListener, MiddleFragment.OnFragmentInteractionListener {

    private DrawerLayout mDrawerLayout;
    Class fragmentClass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Fragment fragmentLevel = null;
        fragmentClass = PrimaryFragment.class;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.drawer_layout);
        mDrawerLayout = findViewById(R.id.drawer_layout);

        Toolbar toolbar = findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);

        ActionBar actionbar = getSupportActionBar();

        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setHomeButtonEnabled(true);
        actionbar.setHomeAsUpIndicator(R.drawable.threelines);

        RadioGroup rg = findViewById(R.id.radiogroup);

        RadioButton easyBtn = findViewById(R.id.easyButton);

        easyBtn.setSelected(true);

        rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {


                Fragment fLevel = null;
                // Insert the fragment by replacing any existing fragment
                FragmentManager fragmentManager = getSupportFragmentManager();
                try {
                    fLevel = (Fragment) (fragmentClass.newInstance());
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InstantiationException e) {
                    e.printStackTrace();
                }
                fragmentManager.beginTransaction().replace(R.id.flContent, fLevel).commit();
            }
        });

        NavigationView navigationView = findViewById(R.id.nav_view);

        try {
            fragmentLevel = (Fragment) fragmentClass.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Insert the fragment by replacing any existing fragment
        FragmentManager fragmentManager = getSupportFragmentManager();

        fragmentManager.beginTransaction().replace(R.id.flContent, fragmentLevel).commit();


        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {

                    //Class fragmentClass;

                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {

                        Fragment fragmentLevel = null;
                        // set item as selected to persist highlight
                        menuItem.setChecked(true);
                        // close drawer when item is tapped
                        mDrawerLayout.closeDrawers();
                        // Add code here to update the UI based on the item selected
                        // For example, swap UI fragments here
                        switch (menuItem.getItemId()) {
                            /*
                            case R.id.nav_high:
                                fragmentClass = HighFragment.class;
                                break;
                                */

                            case R.id.nav_middle:
                                fragmentClass = MiddleFragment.class;
                                break;

                            case R.id.nav_primary:
                                fragmentClass = PrimaryFragment.class;
                                break;

                        }

                        try {
                            fragmentLevel = (Fragment) fragmentClass.newInstance();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        // Insert the fragment by replacing any existing fragment
                        FragmentManager fragmentManager = getSupportFragmentManager();
                        fragmentManager.beginTransaction().replace(R.id.flContent, fragmentLevel).commit();


                        return true;
                    }
                });

        mDrawerLayout.addDrawerListener(
                new DrawerLayout.DrawerListener() {
                    @Override
                    public void onDrawerSlide(View drawerView, float slideOffset) {
                        // Respond when the drawer's position changes
                    }

                    @Override
                    public void onDrawerOpened(View drawerView) {
                        // Respond when the drawer is opened
                    }

                    @Override
                    public void onDrawerClosed(View drawerView) {
                        // Respond when the drawer is closed
                    }

                    @Override
                    public void onDrawerStateChanged(int newState) {
                        // Respond when the drawer motion state changes
                    }
                }
        );
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;


        }
        return super.onOptionsItemSelected(item);
    }

  }