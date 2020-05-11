package com.edu.worx.global;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import com.edu.worx.global.signin.GoogleSignInActivity;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link FragmentHomework.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link FragmentHomework#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentHomework extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";


    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public FragmentHomework() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FragmentHomework.
     */
    // TODO: Rename and change types and number of parameters
    public static FragmentHomework newInstance(String param1, String param2) {
        FragmentHomework fragment = new FragmentHomework();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_fragment_homework, container, false);

        DisplayMetrics displayMetrics = getContext().getResources().getDisplayMetrics();
        float dpHeight = displayMetrics.heightPixels / displayMetrics.density;
        float dpWidth = displayMetrics.widthPixels / displayMetrics.density;
        boolean verticalOrientation = true; //dpHeight > dpWidth;
        int divFactor = 9;
        LinearLayout.LayoutParams parmsTV;
        float      screenLeftMargin = 0;
        float      screenTopMargin = 0;
        LinearLayout layout = view.findViewById(R.id.SubjectLayout);

        String subjectArray [] = {"ENGLISH_LITERATURE", "ENGLISH_LANGUAGE", "HINDI", "HISTORY", "GEOGRAPHY", "MATHS", "PHYSICS","CHEMISTRY", "BIOLOGY", "HOMESCIENCE"};


        for (int i = 0; i < subjectArray.length; i++) {
            final Button myButton = new Button(getContext());
            myButton.setText(subjectArray [i]);
            myButton.setId(i);
            LinearLayout.LayoutParams parms;

            if (verticalOrientation) {
                parms = new LinearLayout.LayoutParams((int) getPixel(dpWidth / 2), LinearLayout.LayoutParams.WRAP_CONTENT);
            }else {
                parms = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT/*(int)getPixel(100f), (int)getPixel(screenTopMargin/1.5) */, LinearLayout.LayoutParams.WRAP_CONTENT);
            }

            parms.bottomMargin = (int) getPixel(5);
            if(i==0) {
                parms.topMargin = (int)(getPixel(screenTopMargin/3)) + 100;

            }
            if (!verticalOrientation && (i > subjectArray.length /2)) {
                parms.leftMargin = (int) getPixel(3 * screenLeftMargin);
            }else {
                parms.leftMargin = (int) getPixel(dpWidth/4.5);
            }

            myButton.setTextSize(15f);
            myButton.setTextColor(Color.WHITE);
            myButton.setLayoutParams(parms);
            myButton.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.blackbutton));
            layout.addView(myButton);

            myButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    String selectedSubject = myButton.getText().toString();
                    String searchPath = "/Homework/SMS/TENTH/2020-21/" + selectedSubject;


                    Intent intent = new Intent(getContext(), SubjectHomeworkActitvity.class);
                    intent.putExtra(SubjectHomeworkActitvity.FIREBASE_SEARCH_PATH, searchPath);

                    if (GenericAPIs.isInternetConnected(getContext())) {
                        startActivity(intent);
                    }
                }
            });
        }


        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

        return view;
    }

    private float getPixel(double dp){
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, (float)dp, getResources().getDisplayMetrics());
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
