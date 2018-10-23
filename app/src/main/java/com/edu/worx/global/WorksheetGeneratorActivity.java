package com.edu.worx.global;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Paint;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;

import java.util.ArrayList;

public class WorksheetGeneratorActivity extends AppCompatActivity {

    protected CharSequence[] topics = { "Rational Numbers","Compound Interest", "Factorisation", "Percentage","Linear Equations"};
    protected ArrayList<CharSequence> selectedtopics = new ArrayList<CharSequence>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        String[] std_array = {"Eighth"};
        String[] level_array = {"Easy", "Medium","Advanced"};

        for (int i =0; i<topics.length; i++ ) {
            selectedtopics.add(topics[i]);
        };

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_worksheet_generator);
        EditText editTextBox = (EditText)findViewById(R.id.numQuesCnt);
        CheckBox rationalNumCB = (CheckBox) findViewById(R.id.rationalNumbersCheckBox);
        CheckBox compountInterestCB = (CheckBox) findViewById(R.id.compoundInterestCheckBox);
        CheckBox factorisationCB = (CheckBox) findViewById(R.id.factorisationCheckBox);
        CheckBox percentageCB = (CheckBox) findViewById(R.id.percentageCheckBox);
        CheckBox linearEquationsCB = (CheckBox) findViewById(R.id.linearEquationsCheckBox);

        CheckBox withAnswersButton = findViewById(R.id.answersCheckBox);
        Button genWorksheetButton = findViewById(R.id.genWorksheet);
        rationalNumCB.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked)
                    selectedtopics.add(topics[0]);
                else
                    selectedtopics.remove(topics[0]);
            }
        });

        compountInterestCB.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked)
                    selectedtopics.add(topics[1]);
                else
                    selectedtopics.remove(topics[1]);
            }
        });

        factorisationCB.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked)
                    selectedtopics.add(topics[2]);
                else
                    selectedtopics.remove(topics[2]);
            }
        });

        percentageCB.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked)
                    selectedtopics.add(topics[3]);
                else
                    selectedtopics.remove(topics[3]);
            }
        });

        linearEquationsCB.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked)
                    selectedtopics.add(topics[4]);
                else
                    selectedtopics.remove(topics[4]);
            }
        });

        genWorksheetButton.setOnClickListener(uploadButton-> {
            Intent intent = new Intent(getApplicationContext(), FirebaseQueryActivity.class);
            String level = "Easy";
            int num = Integer.valueOf(editTextBox.getText().toString());
            String std = "Eighth" ;
            boolean withAnswers = false;

            if(num>25){
                android.app.AlertDialog.Builder numFailureBuilder = new android.app.AlertDialog.Builder(this);
                numFailureBuilder.setMessage("Question Count should not be more than 25.").setTitle("Error!").show();
            }
            else if(selectedtopics.size() == 0){
                android.app.AlertDialog.Builder numFailureBuilder = new android.app.AlertDialog.Builder(this);
                numFailureBuilder.setMessage("Atleast One Topic should be selected.").setTitle("Error!").show();
            }
            else {
                withAnswers = withAnswersButton.isChecked();

                intent.putExtra(FirebaseQueryActivity.SELECTED_LEVEL, level);
                intent.putExtra(FirebaseQueryActivity.SELECTED_NUM, num);
                intent.putExtra(FirebaseQueryActivity.SELECTED_STD, std);
                intent.putCharSequenceArrayListExtra(FirebaseQueryActivity.SELECTED_TOPICS, selectedtopics);
                intent.putExtra(FirebaseQueryActivity.WITH_ANSWERS, withAnswers);

                startActivity(intent);
            }
        });
    }

}
