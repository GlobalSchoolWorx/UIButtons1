package com.edu.worx.global;

import android.content.Context;
import android.content.Intent;
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
 * {@link PrimaryFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link PrimaryFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PrimaryFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public PrimaryFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment PrimaryFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static PrimaryFragment newInstance(String param1, String param2) {
        PrimaryFragment fragment = new PrimaryFragment();
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_primary, container, false);
        String easyStrArray [] = {"THE SENTENCE","KINDS OF NOUNS","THE NOUN : GENDER","THE NOUN : NUMBER", "THE ADJECTIVE", "COMPARISON OF ADJECTIVE", "Articles : A An and The",
                "THE PRONOUN", "REFLEXIVE AND EMPHATIC","VERBS AND TENSES","THE ADVERB", "DIRECT AND INDIRECT SPEECH", "THE PREPOSITION", "THE CONJUNCTION",
                "THE OPPOSITES", "FUN WITH WORDS"};
        String diffStrArray [] = {"THE SENTENCE", "KINDS OF NOUNS", "THE NOUN : GENDER","THE NOUN : NUMBER", "THE NOUN : THE CASE", "THE ADJECTIVE", "COMPARISON OF ADJECTIVE","Articles : A An and The",
                "INTEROGATIVE PRONOUNS", "RELATIVE PRONOUNS", "REFLEXIVE AND EMPHATIC","THE VERB", "ACTIVE AND PASSIVE VOICE", "THE MOOD", "THE TENSE", "THE USES OF THE TENSE",
                "SUBJECT-VERB AGREEMENT", "THE INFINITIVE", "THE PARTICIPLE", "THE GERUND", "THE CONJUNCTION", "THE ADVERB", "THE PREPOSITION",
                 "MODEL AUXILIARIES", "CORRECT ENGLISH", "AIDS TO VOCABULARY", "WORD-BUILDING", "VERB COMBINATIONS", "IDOMS AND PROVERBS",
                "SIMPLE SENTENCE", "PHRASES", "CLAUSES", "COMPOUND AND COMPLEX" };
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
        tvBtn.setText("PRIMARY");

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
                        selectedParams.add("Primary");
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
