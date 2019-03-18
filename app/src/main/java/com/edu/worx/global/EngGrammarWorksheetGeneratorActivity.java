package com.edu.worx.global;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.edu.worx.global.billing.BuySubscriptionActivity;
import com.edu.worx.global.utils.ApplicationConfigurations;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.itextpdf.text.pdf.parser.Line;

import java.lang.reflect.Type;
import java.util.ArrayList;


public class EngGrammarWorksheetGeneratorActivity extends AppCompatActivity {

    protected ArrayList<String> selectedTopicsIndex = new ArrayList<String>();
    protected ArrayList<String> selectedTopics = new ArrayList<String>();

    ArrayList<QuestionSet> mQuess = new ArrayList<>();
    ArrayList<String> mParams;

    public static class EngQuestionIndexSet {
      public String Question;
      public String Index;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_eng_grammar_worksheet_generator);
        mParams = getIntent().getStringArrayListExtra("SELECTED_LVL_TOPIC");
        createDynamicLayout (mParams);

    }


    public void createDynamicLayout (ArrayList<String> mParams) {
        int i;
        LinearLayout ll = findViewById(R.id.eng_worksheet_gen_layout);
        final LinearLayout progressBar = findViewById(R.id.progressBarLayout);
        Button genWorksheetButton = new Button(this);
        genWorksheetButton.setText("Generate Worksheet");

        progressBar.bringToFront();
        TextView tv = new TextView(this);

        tv.setTextSize(18);
        tv.setTypeface(null, Typeface.BOLD ) ;
        tv.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        tv.setText("Select Questions to be Added \n in the Worksheet");
        tv.setPadding(10, 20, 10, 10);

        LinearLayout.LayoutParams params_tv = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        params_tv.setMargins(15, 15, 15, 15);
        tv.setLayoutParams(params_tv);

        ll.addView(tv);

        DatabaseReference masterDbReference = FirebaseDatabase.getInstance().getReference("MiddleEngSubTopicIndex/"+mParams.get(1));
        final Query q = masterDbReference.orderByChild("Question");
        ValueEventListener valueEventListener = q.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                long i = dataSnapshot.getChildrenCount();
                long  counter = i;
                for (DataSnapshot quesSnapshot : dataSnapshot.getChildren() ){

                     counter--;

                    if(quesSnapshot != null ){
                        EngQuestionIndexSet eq = quesSnapshot.getValue(EngQuestionIndexSet.class);
                        EngQuestionIndexSet eq1 = quesSnapshot.getValue(EngQuestionIndexSet.class);
                        {

                            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                                    LinearLayout.LayoutParams.MATCH_PARENT,
                                    LinearLayout.LayoutParams.WRAP_CONTENT
                            );

                            params_tv.setMargins(10, 10, 10, 40);

                            CheckBox cb = new CheckBox(getApplicationContext());
                            cb.setTextSize(15);
                            cb.setText(eq1.Question);
                            cb.setPadding(50,50,50,30);
                            //cb.setLayoutParams(params);
                            ll.addView(cb);
                           cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                               @Override
                               public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                   if(isChecked) {
                                       selectedTopicsIndex.add(eq1.Index);
                                       selectedTopics.add(eq1.Question);
                                   }
                                   else {
                                       selectedTopicsIndex.remove(eq1.Index);
                                       selectedTopics.remove(eq1.Question);
                                   }

                               }
                           });
                        }
                    }
                    if (counter == 0) {
                        progressBar.setVisibility(View.GONE);
                        genWorksheetButton.setPadding(20,20,20,40);
                        ll.addView(genWorksheetButton);

                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        /*
        Button genWorksheetButton = new Button(this);
        genWorksheetButton.setText("Generate Worksheet");
        ll.addView(genWorksheetButton);
        */


        genWorksheetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (selectedTopics.size() == 0)
                {
                    String msg = "Please select atleast one CheckBox to Generate Worksheet";
                    android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(EngGrammarWorksheetGeneratorActivity.this);
                    builder.setMessage(msg);
                    builder.show();
                    return;
                }

                if (!ApplicationConfigurations.hasSusbscription(getApplicationContext())){

                        //int freeclicks = getFreeClicksLeft();
                        android.app.AlertDialog.Builder subsErrorBuilder = new android.app.AlertDialog.Builder(EngGrammarWorksheetGeneratorActivity.this);
                        subsErrorBuilder.setMessage("Subscribe for Unlimited Worksheets.").setTitle("Subscription Required !");

                        subsErrorBuilder.setPositiveButton("Continue to Subscribe", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent = new Intent(getApplicationContext(), BuySubscriptionActivity.class);
                                startActivity(intent);

                            }
                        });

                    subsErrorBuilder.setNegativeButton("View Sample Worksheet", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent(getApplicationContext(), FirebaseQueryActivity.class);
                            intent.putExtra("CALLING_CONTEXT", "SAMPLE MIDDLE ENGLISH GRAMMAR WORKSHEETS");
                            intent.putStringArrayListExtra("SELECTED_SUBTOPICS_INDEX", selectedTopicsIndex);
                            intent.putStringArrayListExtra("SELECTED_SUBTOPICS", selectedTopics);
                            intent.putStringArrayListExtra("SELECTED_PARAMS", mParams);
                            startActivity(intent);
                        }
                    });
                        subsErrorBuilder.show();
                    System.out.print("HELLO");
                }
                else {
                    Intent intent = new Intent(getApplicationContext(), FirebaseQueryActivity.class);
                    intent.putExtra("CALLING_CONTEXT", "ENGLISH GRAMMAR");
                    intent.putStringArrayListExtra("SELECTED_SUBTOPICS_INDEX", selectedTopicsIndex);
                    intent.putStringArrayListExtra("SELECTED_SUBTOPICS", selectedTopics);
                    intent.putStringArrayListExtra("SELECTED_PARAMS", mParams);
                    startActivity(intent);
                }
            }
        });
    }

}
