package com.edu.worx.global;

import android.app.Activity;
import android.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ActionProvider;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;

public class DisplayMenuActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_menu);
    }


    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.popupmenu, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case (R.id.show_answers) : {
                return FALSE;
            }
            case (R.id.hide_answers) : {
                return FALSE;
            }
            case (R.id.download_pdf) :{
               return FALSE;
            }

        }
        return TRUE;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch(item.getItemId()) {

            case (R.id.download_pdf) : {
               break;
            }
            case (R.id.Info): {
                    String msg = "Global School Worx is an application that provides easy access to Testpapers And Worksheets up till Tenth grade. \nChildren can master different topics by practising various worksheets related to a single Topic.\nSome sample school test papers are also provided.\nGlobal School Worx is dedicated to provide education to One & All and Welcome people to contribute by sending their kid’s school papers on globalschoolworx@gmail.com";
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setMessage(msg);
                    builder.show();
                    break;
                }
            }


        return super.onOptionsItemSelected(item);
    }
}
