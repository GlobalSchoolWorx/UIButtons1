package com.edu.worx.global;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.util.ArrayList;


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
        // Inflate the layout for this fragment

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_middle, container, false);

        Button worksheet_sentences = view.findViewById(R.id.sentences_worksheet);

        selectedParams.add("Middle");

        worksheet_sentences.setOnClickListener( sentences_worksheet->{
            if (isInternetConnected()) {
            Intent intent = new Intent( getContext(), EngGrammarWorksheetGeneratorActivity.class);
            selectedParams.clear();
            selectedParams.add("Middle");
            selectedParams.add("The Sentence");
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
                Intent intent = new Intent(getContext(), EngGrammarWorksheetGeneratorActivity.class);
                selectedParams.clear();
                selectedParams.add("Middle");
                selectedParams.add("The Noun");
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
                Intent intent = new Intent(getContext(), EngGrammarWorksheetGeneratorActivity.class);
                selectedParams.clear();
                selectedParams.add("Middle");
                selectedParams.add("The Pronoun");
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
                        Intent intent = new Intent(getContext(), EngGrammarWorksheetGeneratorActivity.class);
                        selectedParams.clear();
                        selectedParams.add("Middle");
                        selectedParams.add("The Adjective");
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
                        Intent intent = new Intent(getContext(), EngGrammarWorksheetGeneratorActivity.class);
                        selectedParams.clear();
                        selectedParams.add("Middle");
                        selectedParams.add("Degrees of Comparison");
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
                        Intent intent = new Intent(getContext(), EngGrammarWorksheetGeneratorActivity.class);
                        selectedParams.clear();
                        selectedParams.add("Middle");
                        selectedParams.add("Articles");
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
                        Intent intent = new Intent(getContext(), EngGrammarWorksheetGeneratorActivity.class);
                        selectedParams.clear();
                        selectedParams.add("Middle");
                        selectedParams.add("Verbs : Modals and Auxillaries");
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
                        Intent intent = new Intent(getContext(), EngGrammarWorksheetGeneratorActivity.class);
                        selectedParams.clear();
                        selectedParams.add("Middle");
                        selectedParams.add("Simple and Continuous Tenses");
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
                        Intent intent = new Intent(getContext(), EngGrammarWorksheetGeneratorActivity.class);
                        selectedParams.clear();
                        selectedParams.add("Middle");
                        selectedParams.add("Perfect Tenses");
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
                        Intent intent = new Intent(getContext(), EngGrammarWorksheetGeneratorActivity.class);
                        selectedParams.clear();
                        selectedParams.add("Middle");
                        selectedParams.add("Verbs : Transitive and Intransitive");
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
                        Intent intent = new Intent(getContext(), EngGrammarWorksheetGeneratorActivity.class);
                        selectedParams.clear();
                        selectedParams.add("Middle");
                        selectedParams.add("Active and Passive Voice");
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
                        Intent intent = new Intent(getContext(), EngGrammarWorksheetGeneratorActivity.class);
                        selectedParams.clear();
                        selectedParams.add("Middle");
                        selectedParams.add("Subject-Verb Agreement");
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
                        Intent intent = new Intent(getContext(), EngGrammarWorksheetGeneratorActivity.class);
                        selectedParams.clear();
                        selectedParams.add("Middle");
                        selectedParams.add("The Adverb");
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
                        Intent intent = new Intent(getContext(), EngGrammarWorksheetGeneratorActivity.class);
                        selectedParams.clear();
                        selectedParams.add("Middle");
                        selectedParams.add("The Preposition");
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
                        Intent intent = new Intent(getContext(), EngGrammarWorksheetGeneratorActivity.class);
                        selectedParams.clear();
                        selectedParams.add("Middle");
                        selectedParams.add("The Conjunction");
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
                        Intent intent = new Intent(getContext(), EngGrammarWorksheetGeneratorActivity.class);
                        selectedParams.clear();
                        selectedParams.add("Middle");
                        selectedParams.add("The Interjection");
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
                        Intent intent = new Intent(getContext(), EngGrammarWorksheetGeneratorActivity.class);
                        selectedParams.clear();
                        selectedParams.add("Middle");
                        selectedParams.add("Punctuation");
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
                        Intent intent = new Intent(getContext(), EngGrammarWorksheetGeneratorActivity.class);
                        selectedParams.clear();
                        selectedParams.add("Middle");
                        selectedParams.add("Transformation of Sentences");
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
                        Intent intent = new Intent(getContext(), EngGrammarWorksheetGeneratorActivity.class);
                        selectedParams.clear();
                        selectedParams.add("Middle");
                        selectedParams.add("Direct and Indirect Speech");
                        intent.putStringArrayListExtra("SELECTED_LVL_TOPIC", selectedParams);
                        startActivity(intent);
                    }
            else {
                android.app.AlertDialog.Builder subsErrorBuilder = new android.app.AlertDialog.Builder(getActivity());
                subsErrorBuilder.setMessage("No Internet Connection").setTitle("ERROR!");
            }
        } );

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
