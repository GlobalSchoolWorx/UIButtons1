package com.edu.worx.global;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.File;
import java.io.IOException;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link FragmentTestpapers.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * create an instance of this fragment.
 */
public class FragmentTestpapers extends Fragment {
    public static final String ARG_SELECTED_CLASS = "selected_class";
    public static final String ARG_SELECTED_CLASS_SUBJECT = "selected_class_subject";

    private OnFragmentInteractionListener mListener;

    public FragmentTestpapers() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivity().invalidateOptionsMenu();
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        String[] yearArray = new String[0];
        float  screenLeftMargin;
        float  screenTopMargin;
        View view = inflater.inflate(R.layout.fragment_fragment_testpapers, container, false);
        DisplayMetrics displayMetrics =  getContext().getResources().getDisplayMetrics();
        float dpHeight = displayMetrics.heightPixels / displayMetrics.density;
        float dpWidth = displayMetrics.widthPixels / displayMetrics.density;
        boolean verticalOrientation = true; //dpHeight > dpWidth;
        int divFactor = 9;
        LinearLayout layout = (LinearLayout) view.findViewById(R.id.dynamicSubjectTespapersLayout);

        if (verticalOrientation) {
            screenLeftMargin = dpWidth / 4;
            if (getArguments().getString(ARG_SELECTED_CLASS_SUBJECT).contains("Tenth"))
                screenLeftMargin =   screenLeftMargin - 20;
        }
        else
            screenLeftMargin = dpWidth / 4;

        try {
            String path = getArguments().getString(ARG_SELECTED_CLASS_SUBJECT);

            if(path.contains("Tenth"))
                path = path + File.separator + "Testpapers";
            yearArray = getContext().getAssets().list(path);
            divFactor = yearArray.length;
            if (divFactor < 6)
                divFactor = 10;
        } catch (IOException ignored) {}

        screenTopMargin = dpHeight / divFactor ;
        LinearLayout.LayoutParams parms;
        LinearLayout.LayoutParams parmsFirst;
        if (verticalOrientation) {
            parmsFirst = new LinearLayout.LayoutParams((int) getPixel(dpWidth/2), (int) getPixel(screenTopMargin));
            parms = new LinearLayout.LayoutParams((int) getPixel(dpWidth/2), (int) getPixel(screenTopMargin));
        }
        else {
            parmsFirst = new LinearLayout.LayoutParams((int) getPixel(100f), (int) getPixel(screenTopMargin / 2));
            parms = new LinearLayout.LayoutParams((int) getPixel(100f), (int) getPixel(screenTopMargin / 1.5));
        }

        parmsFirst.topMargin = (int)(getPixel(screenTopMargin));
        parmsFirst.leftMargin = (int) getPixel(screenLeftMargin);

        view.setPadding(0, (int)(getPixel(screenTopMargin)), 0, 0);
        for (int i = 0; i < yearArray.length; i++) {
            final Button myButton = new Button(getContext());
            myButton.setText(yearArray[i]);
            myButton.setId(i);
            if (!verticalOrientation && (i > yearArray.length / 2))
                parms.leftMargin = (int) getPixel(3 * screenLeftMargin);
            else {
                parms.leftMargin = (int) getPixel(screenLeftMargin);
            }
            parmsFirst.bottomMargin = 10;
            parms.bottomMargin = 10;

            myButton.setTextSize(12f);
            if (i==0) {
                myButton.setLayoutParams(parmsFirst);
            }else{
                myButton.setLayoutParams(parms);
            }
            myButton.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.blackbutton));

            layout.addView(myButton);
            myButton.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                String selectedClassSubject = getArguments().getString(ARG_SELECTED_CLASS_SUBJECT);
                                                String selectedYear = myButton.getText().toString();
                                                String filePath = "assets" + File.separator + selectedClassSubject + File.separator + selectedYear + File.separator;
                                                //String localPath = "assets" + "_" + selectedClass + "_" + selectedSubject;
                                                if (!filePath.contains("Tenth")) {
                                                    Intent intent = new Intent(getContext(), SwipeViewActivity.class);
                                                    intent.putExtra(DisplaySubjectsActivity.FIREBASE_FILE_PATH, filePath);
                                                    //intent.putExtra(DisplaySubjectsActivity.SELECTED_CLASS, selectedClass);
                                                    String localDirName = filePath.replace('/', '_');
                                                    File mydir = getContext().getDir(localDirName, Context.MODE_PRIVATE);

                                                    File localFile = new File(mydir, "paper.pdf");

                                                    if (localFile.exists() || isInternetConnected()) {
                                                        startActivity(intent);
                                                    }
                                                } else {
                                                    Intent intent = new Intent(getContext(), TenthTestpapersActivity.class);

                                                    filePath = selectedClassSubject + File.separator + "Testpapers"+ File.separator + selectedYear;
                                                    intent.putExtra (TenthTestpapersActivity.FIREBASE_FILE_PATH, filePath);

                                                    startActivity(intent);
                                                }
                                            }
                                        });
        }
        return view;
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
    interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }

    private boolean isInternetConnected() {
        ConnectivityManager conMgr = (ConnectivityManager) getContext().getSystemService (Context.CONNECTIVITY_SERVICE);

        // ARE WE CONNECTED TO THE NET
        if (conMgr.getActiveNetworkInfo() != null
                && conMgr.getActiveNetworkInfo().isAvailable()
                && conMgr.getActiveNetworkInfo().isConnected()) {

            return true;


        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setMessage("NO Internet Connection").setTitle("ËRROR !!");
            AlertDialog alertDialog = builder.create();
            alertDialog.show();
            return false;
        }
    }

    private float getPixel(double dp){
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, (float)dp, getResources().getDisplayMetrics());

    }
}
