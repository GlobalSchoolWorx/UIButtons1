package com.edu.worx.global;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTabHost;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TabHost;

import java.io.File;
import java.io.IOException;

import static com.edu.worx.global.FragmentTestpapers.ARG_SELECTED_CLASS_SUBJECT;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link FragmentWorksheets.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link FragmentWorksheets#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentWorksheets extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    public static final String ARG_SELECTED_CLASS = "selected_class";

    String TAB_1_TAG = "ENGLISH WORKSHEETS";
    String TAB_2_TAG = "MATHS WORKSHEETS";

    private OnFragmentInteractionListener mListener;

    public FragmentWorksheets() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FragmentWorksheets.
     */
    // TODO: Rename and change types and number of parameters
    public static FragmentWorksheets newInstance(String param1, String param2) {
        FragmentWorksheets fragment = new FragmentWorksheets();
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        String[] subjectArray = new String[0];
        float  screenLeftMargin;
        float  screenTopMargin;
        View view = inflater.inflate(R.layout.fragment_fragment_worksheets, container, false);
        DisplayMetrics displayMetrics =  getContext().getResources().getDisplayMetrics();
        float dpHeight = displayMetrics.heightPixels / displayMetrics.density;
        float dpWidth = displayMetrics.widthPixels / displayMetrics.density;
        boolean verticalOrientation = true; // dpHeight > dpWidth;
        int divFactor = 9;
        LinearLayout layout = (LinearLayout) view.findViewById(R.id.dynamicSubjectWorksheetLayout);

        if (verticalOrientation)
            screenLeftMargin = dpWidth / 2;
        else
            screenLeftMargin = dpWidth / 4;

        try {
            String path = getArguments().getString(ARG_SELECTED_CLASS_SUBJECT);
            path = path + File.separator + "Worksheets";
            subjectArray = getContext().getAssets().list(path);
            divFactor = subjectArray.length;
            if (divFactor < 6)
                divFactor = 6;
        } catch (IOException ignored) {}

        screenTopMargin = dpHeight / divFactor ;
        LinearLayout.LayoutParams parms;
        LinearLayout.LayoutParams parmsFirst;
        if (verticalOrientation) {
            parmsFirst = new LinearLayout.LayoutParams((int) getPixel(100f), (int) getPixel(screenTopMargin / 3));
            parms = new LinearLayout.LayoutParams((int) getPixel(100f), (int) getPixel(screenTopMargin / 3));
        }
        else {
            parmsFirst = new LinearLayout.LayoutParams((int) getPixel(100f), (int) getPixel(screenTopMargin / 2));
            parms = new LinearLayout.LayoutParams((int) getPixel(100f), (int) getPixel(screenTopMargin / 1.5));
        }

        parmsFirst.topMargin = (int)(getPixel(screenTopMargin));
        parmsFirst.leftMargin = (int) getPixel(screenLeftMargin) - 50;

        view.setPadding(0, (int)(getPixel(screenTopMargin)), 0, 0);
        if (subjectArray.length == 0)
          view.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.global_school_worx_tbd_bg, null));
        else
          for (int i = 0; i < subjectArray.length; i++) {
            final Button myButton = new Button(getContext());
            myButton.setText(subjectArray[i]);
            myButton.setId(i);
            if (!verticalOrientation && (i > subjectArray.length / 2))
                parms.leftMargin = (int) getPixel(3 * screenLeftMargin);
            else {
                parms.leftMargin = (int) getPixel(screenLeftMargin) - 50;
            }

            parms.bottomMargin = 10;
            parmsFirst.bottomMargin = 10;

            myButton.setTextSize(12f);
            if (i==0)
                myButton.setLayoutParams(parmsFirst);
            else
                myButton.setLayoutParams(parms);

              myButton.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.blackbutton));

              layout.addView(myButton);
            myButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String selectedClassSubject = getArguments().getString(ARG_SELECTED_CLASS_SUBJECT);
                    String selectedTopic = myButton.getText().toString();
                    String filePath = "assets" + File.separator + selectedClassSubject + File.separator + "Worksheets/" + selectedTopic + File.separator;
                    Intent intent = new Intent(getContext(), SwipeViewActivity.class);
                    intent.putExtra(DisplaySubjectsActivity.FIREBASE_FILE_PATH, filePath);
                    String localDirName = filePath.replace('/', '_');
                    File mydir = getContext().getDir(localDirName, Context.MODE_PRIVATE);

                    File localFile = new File(mydir, "paper.pdf");

                    if (localFile.exists() || isInternetConnected())
                        startActivity(intent);
                }
            });
        }
        return view;
    }
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof FragmentWorksheets.OnFragmentInteractionListener) {
            mListener = (FragmentWorksheets.OnFragmentInteractionListener) context;
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

    @Override
    public boolean onContextItemSelected(MenuItem item) {

        if(item.getItemId() == R.id.download_pdf) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setMessage("Unable to Download Worksheets.").setTitle("ËRROR !!");
            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        }
        return super.onContextItemSelected(item);
    }
}
