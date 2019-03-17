package com.edu.worx.global;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Paint;
import android.net.ConnectivityManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;

import com.edu.worx.global.billing.BuySubscriptionActivity;
import com.edu.worx.global.utils.ApplicationConfigurations;

import java.util.ArrayList;
import java.util.Collections;

import static com.edu.worx.global.utils.ApplicationConfigurations.hasSusbscription;

public class WorksheetGeneratorActivity extends AppCompatActivity {

    protected CharSequence[] topics = { "Percentage","Rational Numbers","Linear Equations", "Compound Interest","Direct & Indirect Variations", "Profit, Loss & Discount", "Square Roots", "Factorisation", "Cube Roots", "Percentage and its Applications", "Algebraic Expressions", "Expansions", "Linear (simultaneous) Equations", "Mensuration", "Indices"};
    protected ArrayList<CharSequence> selectedtopics = new ArrayList<CharSequence>();


    public  boolean isInternetConnected() {

        ConnectivityManager conMgr = (ConnectivityManager) this.getApplicationContext().getSystemService (Context.CONNECTIVITY_SERVICE);

        // ARE WE CONNECTED TO THE NET
        if (conMgr.getActiveNetworkInfo() != null
                && conMgr.getActiveNetworkInfo().isAvailable()
                && conMgr.getActiveNetworkInfo().isConnected()) {

            return true;


        } else {
            android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
            builder.setMessage("NO Internet Connection").setTitle("ËRROR !!");
            android.app.AlertDialog alertDialog = builder.create();
            alertDialog.show();
            return false;
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_worksheet_generator);
        Collections.addAll(selectedtopics, topics);
        EditText editTextBox = (EditText)findViewById(R.id.numQuesCnt);
        CheckBox rationalNumCB = (CheckBox) findViewById(R.id.rationalNumbersCheckBox);
        CheckBox compountInterestCB = (CheckBox) findViewById(R.id.compoundInterestCheckBox);
        CheckBox factorisationCB = (CheckBox) findViewById(R.id.factorisationCheckBox);
        CheckBox percentageCB = (CheckBox) findViewById(R.id.percentageCheckBox);
        CheckBox linearEquationsCB = (CheckBox) findViewById(R.id.linearEquationsCheckBox);

        CheckBox percentageApplicationsCB = (CheckBox) findViewById(R.id.percentageApplicationsCheckBox);
        CheckBox indicesCB = (CheckBox) findViewById(R.id.indicesCheckBox);
        CheckBox expansionsCB = (CheckBox) findViewById(R.id.expansionsCheckBox);
        CheckBox algebraicCB = (CheckBox) findViewById(R.id.algebraicExpressionsCheckBox);
        CheckBox linearSimEquationsCB = (CheckBox) findViewById(R.id.linearSimEquationsCheckBox);

        CheckBox squareRootsCB = (CheckBox) findViewById(R.id.squareRootsCheckBox);
        CheckBox cubeRootsCB = (CheckBox) findViewById(R.id.cubeRootsCheckBox);
        CheckBox profitLossDiscountCB = (CheckBox) findViewById(R.id.profitLossDiscountCheckBox);
        CheckBox directIndirectCB = (CheckBox) findViewById(R.id.directIndirectCheckBox);
        CheckBox mensurationCB = (CheckBox) findViewById(R.id.mensurationCheckBox);

        Button genWorksheetButton = findViewById(R.id.genWorksheet);

        rationalNumCB.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked)
                    selectedtopics.add(topics[1]);
                else
                    selectedtopics.remove(topics[1]);
            }
        });

        compountInterestCB.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked)
                    selectedtopics.add(topics[3]);
                else
                    selectedtopics.remove(topics[3]);
            }
        });

        factorisationCB.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked)
                    selectedtopics.add(topics[7]);
                else
                    selectedtopics.remove(topics[7]);
            }
        });

        percentageCB.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked)
                    selectedtopics.add(topics[0]);
                else
                    selectedtopics.remove(topics[0]);
            }
        });

        linearEquationsCB.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked)
                    selectedtopics.add(topics[2]);
                else
                    selectedtopics.remove(topics[2]);
            }
        });

        percentageApplicationsCB.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked)
                    selectedtopics.add(topics[9]);
                else
                    selectedtopics.remove(topics[9]);
            }
        });

        indicesCB.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked)
                    selectedtopics.add(topics[14]);
                else
                    selectedtopics.remove(topics[14]);
            }
        });

        algebraicCB.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked)
                    selectedtopics.add(topics[10]);
                else
                    selectedtopics.remove(topics[10]);
            }
        });

        expansionsCB.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked)
                    selectedtopics.add(topics[11]);
                else
                    selectedtopics.remove(topics[11]);
            }
        });

        linearSimEquationsCB.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked)
                    selectedtopics.add(topics[12]);
                else
                    selectedtopics.remove(topics[12]);
            }
        });

        squareRootsCB.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked)
                    selectedtopics.add(topics[6]);
                else
                    selectedtopics.remove(topics[6]);
            }
        });

        cubeRootsCB.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked)
                    selectedtopics.add(topics[8]);
                else
                    selectedtopics.remove(topics[8]);
            }
        });

        mensurationCB.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked)
                    selectedtopics.add(topics[13]);
                else
                    selectedtopics.remove(topics[13]);
            }
        });

        directIndirectCB.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked)
                    selectedtopics.add(topics[4]);
                else
                    selectedtopics.remove(topics[4]);
            }
        });

        profitLossDiscountCB.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked)
                    selectedtopics.add(topics[5]);
                else
                    selectedtopics.remove(topics[5]);
            }
        });

        genWorksheetButton.setOnClickListener(uploadButton-> {
            Intent intent = new Intent(getApplicationContext(), FirebaseQueryActivity.class);
            String level = "Easy";
            int num = Integer.valueOf(editTextBox.getText().toString());
            String std = "Eighth" ;
            boolean withAnswers = false;

            if(false/*!hasSusbscription(this) */){
                //int freeclicks = getFreeClicksLeft();
                android.app.AlertDialog.Builder subsErrorBuilder = new android.app.AlertDialog.Builder(this);
                subsErrorBuilder.setMessage("Subscribe for Unlimited Worksheets.").setTitle("Subscription Required !");

                subsErrorBuilder.setPositiveButton("Continue to Subscribe", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(getApplicationContext(), BuySubscriptionActivity.class);
                        startActivity(intent);

                    }
                });

                subsErrorBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

                subsErrorBuilder.show();
            } else {
                if (!isInternetConnected()) {
                    android.app.AlertDialog.Builder subsErrorBuilder = new android.app.AlertDialog.Builder(this);
                    subsErrorBuilder.setMessage("No Internet Connection").setTitle("ERROR!");
                }
                if (num > 25) {
                    android.app.AlertDialog.Builder numFailureBuilder = new android.app.AlertDialog.Builder(this);
                    numFailureBuilder.setMessage("Question Count should not be more than 25.").setTitle("Error!").show();
                } else if (selectedtopics.size() == 0) {
                    android.app.AlertDialog.Builder numFailureBuilder = new android.app.AlertDialog.Builder(this);
                    numFailureBuilder.setMessage("Atleast One Topic should be selected.").setTitle("Error!").show();
                } else {
                    withAnswers = false;

                    intent.putExtra(FirebaseQueryActivity.SELECTED_LEVEL, level);
                    intent.putExtra(FirebaseQueryActivity.SELECTED_NUM, num);
                    intent.putExtra(FirebaseQueryActivity.SELECTED_STD, std);
                    intent.putCharSequenceArrayListExtra(FirebaseQueryActivity.SELECTED_TOPICS, selectedtopics);
                    intent.putExtra(FirebaseQueryActivity.WITH_ANSWERS, false );

                    startActivity(intent);
                }
            }
        });
    }

}
