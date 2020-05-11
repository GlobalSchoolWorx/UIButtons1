package com.edu.worx.global;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import java.util.ArrayList;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MiddleFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MiddleFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MiddleFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    ArrayList<String> selectedParams = new ArrayList<String>();
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private String  std;
    private RadioGroup rg;
    private RadioButton rb;

    private OnFragmentInteractionListener mListener;

    public MiddleFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MiddleFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MiddleFragment newInstance(String param1, String param2) {
        MiddleFragment fragment = new MiddleFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    public  boolean isInternetConnected() {

        ConnectivityManager conMgr = (ConnectivityManager) getActivity().getApplicationContext().getSystemService (Context.CONNECTIVITY_SERVICE);

        // ARE WE CONNECTED TO THE NET
        if (conMgr.getActiveNetworkInfo() != null
                && conMgr.getActiveNetworkInfo().isAvailable()
                && conMgr.getActiveNetworkInfo().isConnected()) {

            return true;


        } else {
            android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(getActivity());
            builder.setMessage("NO Internet Connection").setTitle("ËRROR !!");
            android.app.AlertDialog alertDialog = builder.create();
            alertDialog.show();
            return false;
        }
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {



        /*
        // Inflate the layout for this fragment

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_middle, container, false);

        Button worksheet_sentences = view.findViewById(R.id.sentences_worksheet);
        rg = getActivity().findViewById(R.id.radiogroup);

        RadioButton easyBtn = getActivity().findViewById(R.id.easyButton);
        easyBtn.setSelected(true);
        rg.check(easyBtn.getId());
        selectedParams.add("Middle");
        std = ((RadioButton)getActivity().findViewById(rg.getCheckedRadioButtonId())).getText().toString();

        worksheet_sentences.setOnClickListener( sentences_worksheet->{
            if (isInternetConnected()) {
            String  std;
            RadioGroup rg;
            Intent intent = new Intent( getContext(), EngGrammarWorksheetGeneratorActivity.class);
            selectedParams.clear();
            rg = view.findViewById(R.id.radiogroup);
            std = ((RadioButton)view.findViewById(rg.getCheckedRadioButtonId())).getText().toString();
            selectedParams.add("Middle");
            selectedParams.add(std);

            selectedParams.add("THE SENTENCE");
            intent.putStringArrayListExtra("SELECTED_LVL_TOPIC", selectedParams);
            startActivity(intent); }
            else {
                android.app.AlertDialog.Builder subsErrorBuilder = new android.app.AlertDialog.Builder(getActivity());
                subsErrorBuilder.setMessage("No Internet Connection").setTitle("ERROR!");
            }
        } );

        Button worksheet_nouns = view.findViewById(R.id.nouns_worksheet);

        worksheet_nouns.setOnClickListener( nouns_worksheet->{
            if (isInternetConnected()) {
                String  std;
                RadioGroup rg;
                Intent intent = new Intent( getContext(), EngGrammarWorksheetGeneratorActivity.class);
                selectedParams.clear();
                rg = view.findViewById(R.id.radiogroup);
                std = ((RadioButton)view.findViewById(rg.getCheckedRadioButtonId())).getText().toString();
                selectedParams.add(std);
                selectedParams.add("THE NOUN");
                intent.putStringArrayListExtra("SELECTED_LVL_TOPIC", selectedParams);
                startActivity(intent);
            }
            else {
                android.app.AlertDialog.Builder subsErrorBuilder = new android.app.AlertDialog.Builder(getActivity());
                subsErrorBuilder.setMessage("No Internet Connection").setTitle("ERROR!");
            }
        } );

        Button worksheet_pronouns = view.findViewById(R.id.pronoun_worksheet);

        worksheet_pronouns.setOnClickListener( pronoun_worksheet->{
            if (isInternetConnected()) {

                String  std;
                RadioGroup rg;
                Intent intent = new Intent( getContext(), EngGrammarWorksheetGeneratorActivity.class);
                selectedParams.clear();
                rg = view.findViewById(R.id.radiogroup);
                std = ((RadioButton)view.findViewById(rg.getCheckedRadioButtonId())).getText().toString();
                selectedParams.add(std);
                //   selectedParams.add("Middle");
                selectedParams.add("THE PRONOUN");
                intent.putStringArrayListExtra("SELECTED_LVL_TOPIC", selectedParams);
                startActivity(intent);
            }
            else {
                android.app.AlertDialog.Builder subsErrorBuilder = new android.app.AlertDialog.Builder(getActivity());
                subsErrorBuilder.setMessage("No Internet Connection").setTitle("ERROR!");
            }
        } );

        Button worksheet_adjectives = view.findViewById(R.id.adjective_worksheet);

        worksheet_adjectives.setOnClickListener( adjective_worksheet->{
                    if (isInternetConnected()) {
                        String  std;
                        RadioGroup rg;
                        Intent intent = new Intent( getContext(), EngGrammarWorksheetGeneratorActivity.class);
                        selectedParams.clear();
                        rg = view.findViewById(R.id.radiogroup);
                        std = ((RadioButton)view.findViewById(rg.getCheckedRadioButtonId())).getText().toString();
                        selectedParams.add(std);
                        //   selectedParams.add("Middle");

                        selectedParams.add("THE ADJECTIVE");
                        intent.putStringArrayListExtra("SELECTED_LVL_TOPIC", selectedParams);
                        startActivity(intent);
                    }
            else {
                android.app.AlertDialog.Builder subsErrorBuilder = new android.app.AlertDialog.Builder(getActivity());
                subsErrorBuilder.setMessage("No Internet Connection").setTitle("ERROR!");
            }
        } );

        Button worksheet_degofcomparisons = view.findViewById(R.id.degofcomparisons_worksheet);

        worksheet_degofcomparisons.setOnClickListener( degofcomparisons_worksheet->{
                    if (isInternetConnected()) {
                        String  std;
                        RadioGroup rg;
                        Intent intent = new Intent( getContext(), EngGrammarWorksheetGeneratorActivity.class);
                        selectedParams.clear();
                        rg = view.findViewById(R.id.radiogroup);
                        std = ((RadioButton)view.findViewById(rg.getCheckedRadioButtonId())).getText().toString();
                        selectedParams.add(std);
                        //   selectedParams.add("Middle");

                        selectedParams.add("DEGREES OF COMPARISON");
                        intent.putStringArrayListExtra("SELECTED_LVL_TOPIC", selectedParams);
                        startActivity(intent);
                    }
            else {
                android.app.AlertDialog.Builder subsErrorBuilder = new android.app.AlertDialog.Builder(getActivity());
                subsErrorBuilder.setMessage("No Internet Connection").setTitle("ERROR!");
            }
        } );

        Button worksheet_articles = view.findViewById(R.id.articles_worksheet);

        worksheet_articles.setOnClickListener( articles_worksheet->{
                    if (isInternetConnected()) {
                        String  std;
                        RadioGroup rg;
                        Intent intent = new Intent( getContext(), EngGrammarWorksheetGeneratorActivity.class);
                        selectedParams.clear();
                        rg = view.findViewById(R.id.radiogroup);
                        std = ((RadioButton)view.findViewById(rg.getCheckedRadioButtonId())).getText().toString();
                        selectedParams.add(std);
                        //   selectedParams.add("Middle");

                        selectedParams.add("ARTICLES");
                        intent.putStringArrayListExtra("SELECTED_LVL_TOPIC", selectedParams);
                        startActivity(intent);
                    }
            else {
                android.app.AlertDialog.Builder subsErrorBuilder = new android.app.AlertDialog.Builder(getActivity());
                subsErrorBuilder.setMessage("No Internet Connection").setTitle("ERROR!");
            }
        } );

        Button worksheet_verbs = view.findViewById(R.id.verb_worksheet);

        worksheet_verbs.setOnClickListener( verb_worksheet->{
                    if (isInternetConnected()) {
                        String  std;
                        RadioGroup rg;
                        Intent intent = new Intent( getContext(), EngGrammarWorksheetGeneratorActivity.class);
                        selectedParams.clear();
                        rg = view.findViewById(R.id.radiogroup);
                        std = ((RadioButton)view.findViewById(rg.getCheckedRadioButtonId())).getText().toString();
                        selectedParams.add(std);
                        //   selectedParams.add("Middle");

                        selectedParams.add("VERBS : MODALS AND AUXILLARIES");
                        intent.putStringArrayListExtra("SELECTED_LVL_TOPIC", selectedParams);
                        startActivity(intent);
                    }
            else {
                android.app.AlertDialog.Builder subsErrorBuilder = new android.app.AlertDialog.Builder(getActivity());
                subsErrorBuilder.setMessage("No Internet Connection").setTitle("ERROR!");
            }
        } );

        Button worksheet_simptenses = view.findViewById(R.id.simptenses_worksheet);

        worksheet_simptenses.setOnClickListener( simptenses_worksheet->{
                    if (isInternetConnected()) {
                        String  std;
                        RadioGroup rg;
                        Intent intent = new Intent( getContext(), EngGrammarWorksheetGeneratorActivity.class);
                        selectedParams.clear();
                        rg = view.findViewById(R.id.radiogroup);
                        std = ((RadioButton)view.findViewById(rg.getCheckedRadioButtonId())).getText().toString();
                        selectedParams.add(std);
                        //   selectedParams.add("Middle");

                        selectedParams.add("SIMPLE AND CONTINUOUS TENSES");
                        intent.putStringArrayListExtra("SELECTED_LVL_TOPIC", selectedParams);
                        startActivity(intent);
                    }
            else {
                android.app.AlertDialog.Builder subsErrorBuilder = new android.app.AlertDialog.Builder(getActivity());
                subsErrorBuilder.setMessage("No Internet Connection").setTitle("ERROR!");
            }
        } );

        Button worksheet_perftenses = view.findViewById(R.id.perftenses_worksheet);

        worksheet_perftenses.setOnClickListener( perftenses_worksheet->{
                    if (isInternetConnected()) {
                        String  std;
                        RadioGroup rg;
                        Intent intent = new Intent( getContext(), EngGrammarWorksheetGeneratorActivity.class);
                        selectedParams.clear();
                        rg = view.findViewById(R.id.radiogroup);
                        std = ((RadioButton)view.findViewById(rg.getCheckedRadioButtonId())).getText().toString();
                        selectedParams.add(std);
                        //   selectedParams.add("Middle");

                        selectedParams.add("PERFECT TENSES");
                        intent.putStringArrayListExtra("SELECTED_LVL_TOPIC", selectedParams);
                        startActivity(intent);
                    }
            else {
                android.app.AlertDialog.Builder subsErrorBuilder = new android.app.AlertDialog.Builder(getActivity());
                subsErrorBuilder.setMessage("No Internet Connection").setTitle("ERROR!");
            }
        } );

        Button worksheet_verbtransitive = view.findViewById(R.id.verbtransitive_worksheet);

        worksheet_verbtransitive.setOnClickListener( verbtransitive_worksheet->{
                    if (isInternetConnected()) {
                        String  std;
                        RadioGroup rg;
                        Intent intent = new Intent( getContext(), EngGrammarWorksheetGeneratorActivity.class);
                        selectedParams.clear();
                        rg = view.findViewById(R.id.radiogroup);
                        std = ((RadioButton)view.findViewById(rg.getCheckedRadioButtonId())).getText().toString();
                        selectedParams.add(std);
                        //   selectedParams.add("Middle");

                        selectedParams.add("VERBS : TRANSITIVE AND INTRANSITIVE");
                        intent.putStringArrayListExtra("SELECTED_LVL_TOPIC", selectedParams);
                        startActivity(intent);
                    }
            else {
                android.app.AlertDialog.Builder subsErrorBuilder = new android.app.AlertDialog.Builder(getActivity());
                subsErrorBuilder.setMessage("No Internet Connection").setTitle("ERROR!");
            }
        } );

        Button worksheet_voice = view.findViewById(R.id.voice_worksheet);

        worksheet_voice.setOnClickListener( voice_worksheet->{
                    if (isInternetConnected()) {
                        String  std;
                        RadioGroup rg;
                        Intent intent = new Intent( getContext(), EngGrammarWorksheetGeneratorActivity.class);
                        selectedParams.clear();
                        rg = view.findViewById(R.id.radiogroup);
                        std = ((RadioButton)view.findViewById(rg.getCheckedRadioButtonId())).getText().toString();
                        selectedParams.add(std);
                        //   selectedParams.add("Middle");

                        selectedParams.add("ACTIVE AND PASSIVE VOICE");
                        intent.putStringArrayListExtra("SELECTED_LVL_TOPIC", selectedParams);
                        startActivity(intent);
                    }
                    else {
                        android.app.AlertDialog.Builder subsErrorBuilder = new android.app.AlertDialog.Builder(getActivity());
                        subsErrorBuilder.setMessage("No Internet Connection").setTitle("ERROR!");
                    }

        } );

        Button worksheet_subverbagreement = view.findViewById(R.id.subverbagreement_worksheet);

        worksheet_subverbagreement.setOnClickListener( subverbagreement_worksheet->{
                    if (isInternetConnected()) {
                        String  std;
                        RadioGroup rg;
                        Intent intent = new Intent( getContext(), EngGrammarWorksheetGeneratorActivity.class);
                        selectedParams.clear();
                        rg = view.findViewById(R.id.radiogroup);
                        std = ((RadioButton)view.findViewById(rg.getCheckedRadioButtonId())).getText().toString();
                        selectedParams.add(std);
                        //   selectedParams.add("Middle");

                        selectedParams.add("SUBJECT-VERB AGREEMENT");
                        intent.putStringArrayListExtra("SELECTED_LVL_TOPIC", selectedParams);
                        startActivity(intent);
                    }
            else {
                android.app.AlertDialog.Builder subsErrorBuilder = new android.app.AlertDialog.Builder(getActivity());
                subsErrorBuilder.setMessage("No Internet Connection").setTitle("ERROR!");
            }
        } );

        Button worksheet_adverbs = view.findViewById(R.id.adverbs_worksheet);

        worksheet_adverbs.setOnClickListener( adverbs_worksheet->{
                    if (isInternetConnected()) {
                        String  std;
                        RadioGroup rg;
                        Intent intent = new Intent( getContext(), EngGrammarWorksheetGeneratorActivity.class);
                        selectedParams.clear();
                        rg = view.findViewById(R.id.radiogroup);
                        std = ((RadioButton)view.findViewById(rg.getCheckedRadioButtonId())).getText().toString();
                        selectedParams.add(std);
                        //   selectedParams.add("Middle");

                        selectedParams.add("THE ADVERB");
                        intent.putStringArrayListExtra("SELECTED_LVL_TOPIC", selectedParams);
                        startActivity(intent);
                    }
            else {
                android.app.AlertDialog.Builder subsErrorBuilder = new android.app.AlertDialog.Builder(getActivity());
                subsErrorBuilder.setMessage("No Internet Connection").setTitle("ERROR!");
            }
        } );

        Button worksheet_preposition = view.findViewById(R.id.preposition_worksheet);

        worksheet_preposition.setOnClickListener( preposition_worksheet->{
                    if (isInternetConnected()) {
                        String  std;
                        RadioGroup rg;
                        Intent intent = new Intent( getContext(), EngGrammarWorksheetGeneratorActivity.class);
                        selectedParams.clear();
                        rg = view.findViewById(R.id.radiogroup);
                        std = ((RadioButton)view.findViewById(rg.getCheckedRadioButtonId())).getText().toString();
                        selectedParams.add(std);
                        //   selectedParams.add("Middle");

                        selectedParams.add("THE PREPOSITION");
                        intent.putStringArrayListExtra("SELECTED_LVL_TOPIC", selectedParams);
                        startActivity(intent);
                    }
            else {
                android.app.AlertDialog.Builder subsErrorBuilder = new android.app.AlertDialog.Builder(getActivity());
                subsErrorBuilder.setMessage("No Internet Connection").setTitle("ERROR!");
            }
        } );

        Button worksheet_conjunction = view.findViewById(R.id.conjunction_worksheet);

        worksheet_conjunction.setOnClickListener( conjunction_worksheet->{
                    if (isInternetConnected()) {
                        String  std;
                        RadioGroup rg;
                        Intent intent = new Intent( getContext(), EngGrammarWorksheetGeneratorActivity.class);
                        selectedParams.clear();
                        rg = view.findViewById(R.id.radiogroup);
                        std = ((RadioButton)view.findViewById(rg.getCheckedRadioButtonId())).getText().toString();
                        selectedParams.add(std);
                        //   selectedParams.add("Middle");

                        selectedParams.add("THE CONJUNCTION");
                        intent.putStringArrayListExtra("SELECTED_LVL_TOPIC", selectedParams);
                        startActivity(intent);
                    }
            else {
                android.app.AlertDialog.Builder subsErrorBuilder = new android.app.AlertDialog.Builder(getActivity());
                subsErrorBuilder.setMessage("No Internet Connection").setTitle("ERROR!");
            }
        } );

        Button worksheet_interjection = view.findViewById(R.id.interjection_worksheet);

        worksheet_interjection.setOnClickListener( preposition_worksheet->{
                    if (isInternetConnected()) {
                        String  std;
                        RadioGroup rg;
                        Intent intent = new Intent( getContext(), EngGrammarWorksheetGeneratorActivity.class);
                        selectedParams.clear();
                        rg = view.findViewById(R.id.radiogroup);
                        std = ((RadioButton)view.findViewById(rg.getCheckedRadioButtonId())).getText().toString();
                        selectedParams.add(std);
                        //   selectedParams.add("Middle");

                        selectedParams.add("THE INTERJECTION");
                        intent.putStringArrayListExtra("SELECTED_LVL_TOPIC", selectedParams);
                        startActivity(intent);
                    }
            else {
                android.app.AlertDialog.Builder subsErrorBuilder = new android.app.AlertDialog.Builder(getActivity());
                subsErrorBuilder.setMessage("No Internet Connection").setTitle("ERROR!");
            }
        } );

        Button worksheet_punctuation = view.findViewById(R.id.punctuation_worksheet);

        worksheet_punctuation.setOnClickListener( transformation_worksheet->{
                    if (isInternetConnected()) {
                        String  std;
                        RadioGroup rg;
                        Intent intent = new Intent( getContext(), EngGrammarWorksheetGeneratorActivity.class);
                        selectedParams.clear();
                        rg = view.findViewById(R.id.radiogroup);
                        std = ((RadioButton)view.findViewById(rg.getCheckedRadioButtonId())).getText().toString();
                        selectedParams.add(std);
                        //   selectedParams.add("Middle");

                        selectedParams.add("PUNCTUATION");
                        intent.putStringArrayListExtra("SELECTED_LVL_TOPIC", selectedParams);
                        startActivity(intent);
                    }
            else {
                android.app.AlertDialog.Builder subsErrorBuilder = new android.app.AlertDialog.Builder(getActivity());
                subsErrorBuilder.setMessage("No Internet Connection").setTitle("ERROR!");
            }
        } );

        Button worksheet_transformation = view.findViewById(R.id.transformation_worksheet);

        worksheet_transformation.setOnClickListener( transformation_worksheet->{
                    if (isInternetConnected()) {
                        String  std;
                        RadioGroup rg;
                        Intent intent = new Intent( getContext(), EngGrammarWorksheetGeneratorActivity.class);
                        selectedParams.clear();
                        rg = view.findViewById(R.id.radiogroup);
                        std = ((RadioButton)view.findViewById(rg.getCheckedRadioButtonId())).getText().toString();
                        selectedParams.add(std);
                        //   selectedParams.add("Middle");

                        selectedParams.add("TRANSFORMATION OF SENTENCES");
                        intent.putStringArrayListExtra("SELECTED_LVL_TOPIC", selectedParams);
                        startActivity(intent);
                    }
            else {
                android.app.AlertDialog.Builder subsErrorBuilder = new android.app.AlertDialog.Builder(getActivity());
                subsErrorBuilder.setMessage("No Internet Connection").setTitle("ERROR!");
            }
        } );

        Button worksheet_directindirect = view.findViewById(R.id.directindirect_worksheet);

        worksheet_directindirect.setOnClickListener( directindirect_worksheet->{
                    if (isInternetConnected()) {
                        String  std;
                        RadioGroup rg;
                        Intent intent = new Intent( getContext(), EngGrammarWorksheetGeneratorActivity.class);
                        selectedParams.clear();
                        rg = view.findViewById(R.id.radiogroup);
                        std = ((RadioButton)view.findViewById(rg.getCheckedRadioButtonId())).getText().toString();
                        selectedParams.add(std);
                        //   selectedParams.add("Middle");

                        selectedParams.add("DIRECT AND INDIRECT SPEECH");
                        intent.putStringArrayListExtra("SELECTED_LVL_TOPIC", selectedParams);
                        startActivity(intent);
                    }
            else {
                android.app.AlertDialog.Builder subsErrorBuilder = new android.app.AlertDialog.Builder(getActivity());
                subsErrorBuilder.setMessage("No Internet Connection").setTitle("ERROR!");
            }
        } );

        Button worksheet_kindsofphrases = view.findViewById(R.id.kindsofphrases_worksheet);

        worksheet_kindsofphrases.setOnClickListener( kindsofphrases_worksheet->{
            if (isInternetConnected()) {
                String  std;
                RadioGroup rg;
                Intent intent = new Intent( getContext(), EngGrammarWorksheetGeneratorActivity.class);
                selectedParams.clear();
                rg = view.findViewById(R.id.radiogroup);
                std = ((RadioButton)view.findViewById(rg.getCheckedRadioButtonId())).getText().toString();
                selectedParams.add(std);
                //   selectedParams.add("Middle");

                selectedParams.add("KINDS OF PHRASES");
                intent.putStringArrayListExtra("SELECTED_LVL_TOPIC", selectedParams);
                startActivity(intent);
            }
            else {
                android.app.AlertDialog.Builder subsErrorBuilder = new android.app.AlertDialog.Builder(getActivity());
                subsErrorBuilder.setMessage("No Internet Connection").setTitle("ERROR!");
            }
        } );

        Button worksheet_kindsofclauses = view.findViewById(R.id.kindsofclauses_worksheet);

        worksheet_kindsofclauses.setOnClickListener( kindsofclauses_worksheet->{
            if (isInternetConnected()) {
                String  std;
                RadioGroup rg;
                Intent intent = new Intent( getContext(), EngGrammarWorksheetGeneratorActivity.class);
                selectedParams.clear();
                rg = view.findViewById(R.id.radiogroup);
                std = ((RadioButton)view.findViewById(rg.getCheckedRadioButtonId())).getText().toString();
                selectedParams.add(std);
                //   selectedParams.add("Middle");

                selectedParams.add("KINDS OF CLAUSES");
                intent.putStringArrayListExtra("SELECTED_LVL_TOPIC", selectedParams);
                startActivity(intent);
            }
            else {
                android.app.AlertDialog.Builder subsErrorBuilder = new android.app.AlertDialog.Builder(getActivity());
                subsErrorBuilder.setMessage("No Internet Connection").setTitle("ERROR!");
            }
        } );

        Button worksheet_scc = view.findViewById(R.id.scc_worksheet);

        worksheet_scc.setOnClickListener( scc_worksheet->{
            if (isInternetConnected()) {
                String  std;
                RadioGroup rg;
                Intent intent = new Intent( getContext(), EngGrammarWorksheetGeneratorActivity.class);
                selectedParams.clear();
                rg = view.findViewById(R.id.radiogroup);
                std = ((RadioButton)view.findViewById(rg.getCheckedRadioButtonId())).getText().toString();
                selectedParams.add(std);
                //   selectedParams.add("Middle");

                selectedParams.add("SIMPLE, COMPOUND AND COMPLEX");
                intent.putStringArrayListExtra("SELECTED_LVL_TOPIC", selectedParams);
                startActivity(intent);
            }
            else {
                android.app.AlertDialog.Builder subsErrorBuilder = new android.app.AlertDialog.Builder(getActivity());
                subsErrorBuilder.setMessage("No Internet Connection").setTitle("ERROR!");
            }
        } );

        Button worksheet_synthesis = view.findViewById(R.id.synthesis_worksheet);

        worksheet_synthesis.setOnClickListener( synthesis_worksheet->{
            if (isInternetConnected()) {
                String  std;
                RadioGroup rg;
                Intent intent = new Intent( getContext(), EngGrammarWorksheetGeneratorActivity.class);
                selectedParams.clear();
                rg = view.findViewById(R.id.radiogroup);
                std = ((RadioButton)view.findViewById(rg.getCheckedRadioButtonId())).getText().toString();
                selectedParams.add(std);
                //   selectedParams.add("Middle");

                selectedParams.add("SYNTHESIS OF SENTENCES");
                intent.putStringArrayListExtra("SELECTED_LVL_TOPIC", selectedParams);
                startActivity(intent);
            }
            else {
                android.app.AlertDialog.Builder subsErrorBuilder = new android.app.AlertDialog.Builder(getActivity());
                subsErrorBuilder.setMessage("No Internet Connection").setTitle("ERROR!");
            }
        } );

        return view;

*/

        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_primary, container, false);
        String easyStrArray [] = {"THE SENTENCE", "THE NOUN", "THE PRONOUN", "THE ADJECTIVE", "DEGREES OF COMPARISON", "ARTICLES", "VERBS : MODALS AND AUXILLARIES", "SIMPLE AND CONTINUOUS TENSES",
                "PERFECT TENSES", "VERBS : TRANSITIVE AND INTRANSITIVE", "ACTIVE AND PASSIVE VOICE", "SUBJECT-VERB AGREEMENT", "THE ADVERB", "THE PREPOSITION", "THE CONJUNCTION",
                "THE INTERJECTION", "PUNCTUATION", "TRANSFORMATION OF SENTENCES", "DIRECT AND INDIRECT SPEECH", "KINDS OF PHRASES", "KINDS OF CLAUSES", "SIMPLE, COMPOUND AND COMPLEX",
                "SYNTHESIS OF SENTENCES"};
        String diffStrArray [] = {"THE SENTENCE", "THE NOUN", "THE PRONOUN", "THE ADJECTIVE", "VERBS : MODALS AND AUXILLARIES", "SIMPLE AND CONTINUOUS TENSES",
                "PERFECT TENSES", "VERBS : TRANSITIVE AND INTRANSITIVE", "ACTIVE AND PASSIVE VOICE", "SUBJECT-VERB AGREEMENT", "THE ADVERB", "THE PREPOSITION", "THE CONJUNCTION",
                "THE INTERJECTION", "PUNCTUATION", "TRANSFORMATION OF SENTENCES", "DIRECT AND INDIRECT SPEECH", "KINDS OF PHRASES", "KINDS OF CLAUSES", "SIMPLE, COMPOUND AND COMPLEX",
                "SYNTHESIS OF SENTENCES"};

        String strArray[] = null;

        LinearLayout btnLayout = view.findViewById(R.id.buttonLayout);
        RadioGroup rg = getActivity().findViewById(R.id.radiogroup);
        RadioButton rb = (RadioButton)getActivity().findViewById(rg.getCheckedRadioButtonId());
        CharSequence srt = rb.getText();
        String option = srt.toString();

        if(option.equals("Difficult"))
            strArray = diffStrArray.clone();
        else
            strArray = easyStrArray.clone();

        TextView tvBtn = getActivity().findViewById(R.id.tvLevel);
        tvBtn.setText("MIDDLE");

        for (int i = 0 ; i < strArray.length; i++) {
            /*
              Adding LinearLayout1
                        ---Textview -- Topic Name
                        ---LinearLayout2
                             ---Worksheet Button
            */
            LinearLayout linearLayout1 = new LinearLayout(getContext());
            LinearLayout.LayoutParams params1 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0, 1);
            params1.bottomMargin = 20;
            params1.topMargin = 20;
            params1.leftMargin = 20;
            params1.rightMargin = 20;

            linearLayout1.setLayoutParams(params1);
            linearLayout1.setId(i);
            linearLayout1.setBackground( ContextCompat.getDrawable(getContext(), R.drawable.layout_roundedcorners_bg));
            linearLayout1.setOrientation(LinearLayout.VERTICAL);
            btnLayout.addView(linearLayout1);

            TextView tv = new TextView(getContext());
            LinearLayout.LayoutParams params2 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params2.bottomMargin = 20;
            params2.topMargin = 20;
            params2.leftMargin = 20;
            params2.rightMargin = 20;

            tv.setLayoutParams(params2);
            tv.setTextColor(ContextCompat.getColor(getContext(), R.color.colorAccent));
            tv.setTextSize(20);
            int y = i+1;
            tv.setText(y + ". " + strArray[i]);
            linearLayout1.addView(tv);

            LinearLayout linearLayout2 = new LinearLayout(getContext());
            LinearLayout.LayoutParams params3 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            params3.bottomMargin = 20;
            params3.topMargin = 20;
            params3.leftMargin = 20;
            params3.rightMargin = 20;

            linearLayout2.setLayoutParams(params3);
            linearLayout2.setBackground( ContextCompat.getDrawable(getContext(), R.drawable.layout_roundedcorners_bg));
            linearLayout2.setOrientation(LinearLayout.VERTICAL);
            linearLayout1.addView(linearLayout2);

            Button button = new Button(getContext());
            button.setTextSize(20);
            button.setContentDescription(strArray[i]);
            button.setText("WORKSHEET");
            button.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.button_roundedcorners_bg));
            button.setId(i);
            button.setLayoutParams(params2);
            linearLayout2.addView(button);

            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (GenericAPIs.isInternetConnected(getContext())) {
                        ArrayList<String> selectedParams = new ArrayList<String>();
                        String  std;
                        RadioGroup rg;
                        Intent intent = new Intent( getContext(), EngGrammarWorksheetGeneratorActivity.class);
                        selectedParams.clear();
                        rg = getActivity().findViewById(R.id.radiogroup);
                        std = ((RadioButton)getActivity().findViewById(rg.getCheckedRadioButtonId())).getText().toString();
                        selectedParams.add("Middle");
                        selectedParams.add(std);
                        Button b = (Button) v;
                        String buttonText = b.getContentDescription().toString();
                        selectedParams.add(buttonText);
                        intent.putStringArrayListExtra("SELECTED_LVL_TOPIC", selectedParams);
                        startActivity(intent); }
                    else {
                        android.app.AlertDialog.Builder subsErrorBuilder = new android.app.AlertDialog.Builder(getActivity());
                        subsErrorBuilder.setMessage("No Internet Connection").setTitle("ERROR!");
                    }
                }
            });
        }


        return view;



    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
