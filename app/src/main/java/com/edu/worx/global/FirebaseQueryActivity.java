package com.edu.worx.global;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;

import com.edu.worx.global.utils.ApplicationConfigurations;
import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.scroll.DefaultScrollHandle;
import com.github.barteksc.pdfviewer.util.FitPolicy;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicInteger;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;



public class FirebaseQueryActivity extends AppCompatActivity {

    File fileHA;
    File fileSA;
    File localFile;
    boolean showAnswers = false;
    long lastSavedDate;
    ArrayList<QuestionSet> mQuess = new ArrayList<>();  // From Firebase
    ArrayList<QuestionSet> mFinalSelectedQuess = new ArrayList<>();  // Final Selected Question Set
    public static final String SELECTED_TOPICS = "SELECTED_TOPICS";
    public static final String SELECTED_STD = "SELECTED_STD";
    public static final String SELECTED_NUM = "SELECTED_NUM";
    public static final String SELECTED_LEVEL = "SELECTED_LEVEL";
    public static final String WITH_ANSWERS = "WITH_ANSWERS";
    public static final String TABLE_QUESTION_SET = "Question_Set_Table2";
    boolean fetchingComplete;
    SQLiteDatabase database = null;
    String ctx;
    boolean shuffle = true;
    private FirebaseAnalytics mFirebaseAnalytics;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        // Obtain the FirebaseAnalytics instance.
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_firebase_query);


        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setLogo(R.drawable.global_school_worx);
            getSupportActionBar().setDisplayUseLogoEnabled(true);
        }

        Bundle bundle = new Bundle();
        //    bundle.putString(FirebaseAnalytics.Param.ITEM_ID, id);

        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "image");
        ctx = getIntent().getStringExtra("CALLING_CONTEXT");
        String fName = getIntent().getStringExtra("HOMEWORK_CONTENT");
        String type = getIntent().getStringExtra("HOMEWORK_TYPE");
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, ctx);
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);

        if ( ctx != null) {
            if (ctx.matches("SAMPLE MIDDLE ENGLISH GRAMMAR WORKSHEETS")) {
                showSampleWorksheet();
            } else if (ctx.matches("ENGLISH GRAMMAR")) {
                queryEngGrammarDatabase();
            }else if (ctx.matches("HOMEWORK")) {
                displayDocument(fName,type,null);
            }

        }
        else
            queryDatabase();

    }

    public void writeToPdfDoc( ArrayList<QuestionSet> arrQuess, boolean withAnswers, boolean shuffle) {


        try {

            if ( true /* permissionCheck == PackageManager.PERMISSION_GRANTED */) {
                boolean dirCreated = false;
                File dir = getApplicationContext().getCacheDir();
                if (!dir.exists())
                    dirCreated = dir.mkdirs();

                Log.d("PDFCreator", "PDF Path: " + dir.toString());
                Date currentDate = Calendar.getInstance().getTime();
                long curDate = currentDate.getTime();
                File oldFile = new File (dir,"worksheet_" + lastSavedDate + ".pdf" );

                if ( oldFile.exists()) {
                    oldFile.delete();
                }

                lastSavedDate = curDate;
                fileSA = new File(dir,"worksheet_show_ans_" + curDate + ".pdf");
                fileSA.createNewFile();
                fileHA = new File(dir,"worksheet_hide_ans_" + curDate + ".pdf");
                fileHA.createNewFile();

                PdfGenerator.generatePdf (this, arrQuess, TRUE, false, fileSA, shuffle);
                PdfGenerator.generatePdf(this, arrQuess, FALSE, false, fileHA, shuffle);

                PDFView pdfView = findViewById(R.id.displayPdfView);
                pdfView.fromFile(fileHA)
                        .defaultPage(0)
                        .enableSwipe(true)
                        .swipeHorizontal(false)
                        .enableAnnotationRendering(true)
                        .enableAntialiasing(true)
                        .pageFitPolicy(FitPolicy.WIDTH)
                        .scrollHandle(new DefaultScrollHandle(this))

                        .load();

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void queryDatabase () {
        //  int permissionCheck = ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (true/*permissionCheck == PackageManager.PERMISSION_GRANTED*/) {
            String std = getIntent().getStringExtra(FirebaseQueryActivity.SELECTED_STD);
            String level = getIntent().getStringExtra(FirebaseQueryActivity.SELECTED_LEVEL);
            int num = getIntent().getIntExtra(FirebaseQueryActivity.SELECTED_NUM, 0);
            boolean withAnswers = getIntent().getBooleanExtra(FirebaseQueryActivity.WITH_ANSWERS, false);
            ArrayList<CharSequence> topics = getIntent().getCharSequenceArrayListExtra(FirebaseQueryActivity.SELECTED_TOPICS);
            ArrayList<QuestionSet> quesLocalArray = new ArrayList<>();
            int topicSize, topicNum;
            CharSequence topicStr;

            topicSize = topics.size();
            topicNum = num / topicSize;
            final LinearLayout progressBar2 = findViewById(R.id.progressBarLayout);
            progressBar2.bringToFront();
            AtomicInteger latch = new AtomicInteger(topicSize);
            AtomicInteger extraQues = new AtomicInteger(num % topicSize);
            for (int z = 0; z < topicSize; z++) {
                topicStr = topics.get(z);

                DatabaseReference masterDbReference = FirebaseDatabase.getInstance().getReference("QuestionSets/" + std + "/" + topicStr.toString() + "/" + level);
                final Query q = masterDbReference.orderByChild("Question");
                CharSequence finalTopicStr = topicStr;
                ValueEventListener valueEventListener = q.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        int extraQuesCnt = extraQues.get();
                        int divFactor = 1;
                        long cnt = dataSnapshot.getChildrenCount();
                        mQuess.clear();
                        quesLocalArray.clear();
                        for (DataSnapshot quesSnapshot : dataSnapshot.getChildren()) {
                            if (quesSnapshot != null) {
                                QuestionSet qSet = quesSnapshot.getValue(QuestionSet.class);
                                mQuess.add(qSet);
                            }
                        }
                        if(extraQues.get() > 1)
                            divFactor = 2;
                        else
                            divFactor = 1;

                        extraQues.getAndSet(extraQuesCnt/divFactor);
                        fetchQuesArrFromLocalDb(quesLocalArray, level, finalTopicStr);
                        chooseMinFreqQuestionSet(quesLocalArray, topicNum + extraQues.get());
                        extraQuesCnt = extraQuesCnt - (extraQuesCnt/divFactor);
                        extraQues.getAndSet(extraQuesCnt);
                        if (latch.getAndDecrement() == 1) {
                            writeToPdfDoc(mFinalSelectedQuess, withAnswers, true);
                            progressBar2.setVisibility (View.GONE);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

                masterDbReference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        //DataSnapshot quesSnapshot[cnt_children];
                        for (DataSnapshot quesSnapshot : dataSnapshot.getChildren()) {
                            if (quesSnapshot != null) {
                                QuestionSet qSet = quesSnapshot.getValue(QuestionSet.class);
                                mQuess.add(qSet);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

            }
            database = openOrCreateDatabase("localQuestionDB.db", Context.MODE_PRIVATE, null);

            database.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_QUESTION_SET + "( "
                    + "UIN" + " INTEGER PRIMARY KEY, "
                    + "STD" + " TEXT, "
                    + "LEVEL" + " TEXT, "
                    + "FREQ" + " INTEGER, "
                    + "TOPIC" + " TEXT "
                    + ")");
        }
    }

    /* Sqlite Database API */
    public void fetchQuesArrFromLocalDb(ArrayList<QuestionSet> quesLocalArray, String level, CharSequence topic){
        int i = 0;
        String columns[] = {"UIN", "STD","LEVEL", "TOPIC", "FREQ"};
        String whereArgs[] = {level, topic.toString()}; // {"Eighth", "Easy", "Percentage"};
        try(Cursor cursor = database.query(TABLE_QUESTION_SET, columns,"LEVEL = ? AND TOPIC = ?", whereArgs, null, null, null)) {
            int cnt = cursor.getCount();
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                QuestionSet ques = new QuestionSet();

                ques.UIN = cursor.getLong(cursor.getColumnIndex("UIN"));
                ques.Freq = cursor.getInt(cursor.getColumnIndex("FREQ"));
                quesLocalArray.add(ques);
                cursor.moveToNext();
            }
        }
    }

    /* Sqlite Database API */
    public void chooseMinFreqQuestionSet( ArrayList<QuestionSet> localDbQuesArray, int reqCnt){
        int i, x, cnt = 0, freq = 0;
        int maxFreq = 1000;
        for (i = 0; i < maxFreq && cnt < reqCnt ; i++) {
            for(x=0; x<mQuess.size() && cnt<reqCnt; x++) {
                QuestionSet ques = mQuess.get(x);

                freq = existsInLocalDb (localDbQuesArray, ques);
                if (freq > 0) {
                    if (freq > maxFreq) maxFreq = freq;
                    if (freq == i) {
                        if (!mFinalSelectedQuess.contains(ques)) {
                            mFinalSelectedQuess.add(ques);
                            updateQuesInLocalDb(ques, freq+1); // freq++ update in SQLite Database
                            cnt++;
                        }
                    }

                } else{
                    mFinalSelectedQuess.add(ques);
                    updateQuesInLocalDb(ques, 1); // update in SQLite Database
                    cnt++;
                }

            }
        }
    }

    /* Sqlite Database API */
    public void updateQuesInLocalDb( QuestionSet ques, int new_freq){
        ContentValues values = new ContentValues();
        String whereArgs[] = { Long.toString(ques.UIN) };
        database.delete(TABLE_QUESTION_SET, "UIN = ?", whereArgs);

        values.put("FREQ", new_freq);
        values.put("UIN", ques.UIN);
        values.put("STD", ques.Std);
        values.put("LEVEL", ques.Level);
        values.put("TOPIC", ques.Topic);
        database.insert(TABLE_QUESTION_SET,null, values);
    }

    /* Sqlite Database API */
    public int existsInLocalDb(ArrayList<QuestionSet> localDbQuesArray, QuestionSet ques){
        int freq = 0;

        for(int i=0; i<localDbQuesArray.size(); i++) {
            if (localDbQuesArray.get(i).UIN == ques.UIN)
                freq = localDbQuesArray.get(i).Freq;
        }

        return freq;
    }


    private void queryEngGrammarDatabase() {

        int maxCnt = 5;
        ArrayList<String> selectedTopics;
        ArrayList<String> selectedTopicsIndex;
        ArrayList<String> mParams;
        Intent intent = getIntent();
        final LinearLayout progressBar2 = findViewById(R.id.progressBarLayout);
        ArrayList<QuestionSet> quesLocalArray = new ArrayList<>();

        progressBar2.bringToFront();

        selectedTopicsIndex = intent.getStringArrayListExtra("SELECTED_SUBTOPICS_INDEX");
        selectedTopics = intent.getStringArrayListExtra("SELECTED_SUBTOPICS");
        mParams = intent.getStringArrayListExtra("SELECTED_PARAMS");
        AtomicInteger latch = new AtomicInteger(selectedTopicsIndex.size());
        for (int i = 0; i < selectedTopicsIndex.size(); i++) {



            DatabaseReference masterDbReference2 = FirebaseDatabase.getInstance().getReference(mParams.get(0) + "EngGrammar/" +  "/" + mParams.get(1) + "/" + mParams.get(2) + "/"+ selectedTopicsIndex.get(i) );
            final Query q2 = masterDbReference2.orderByChild("Question");

            q2.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    mQuess.clear();
                    quesLocalArray.clear();

                    for (DataSnapshot quesSnapshot : dataSnapshot.getChildren()) {
                        if (quesSnapshot != null) {
                            QuestionSet qSet = quesSnapshot.getValue(QuestionSet.class);
                            mQuess.add(qSet);

                        }
                    }


                    fetchQuesArrFromLocalDb(quesLocalArray, mParams.get(1),  mParams.get(2));
                    chooseMinFreqQuestionSet(quesLocalArray, maxCnt);


                    if (latch.getAndDecrement() == 1) {
                        int ins = 0;
                        for (int z = 0; z < selectedTopics.size() && ins < mFinalSelectedQuess.size(); z++) {
                            QuestionSet qSubtopic = new QuestionSet(selectedTopics.get(z));
                            ins = 5*z + z;
                            mFinalSelectedQuess.add(ins, qSubtopic);

                        }
                        writeToPdfDoc(mFinalSelectedQuess, false, false);
                        progressBar2.setVisibility(View.GONE);
                    }
                }


                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        }

        database = openOrCreateDatabase("localQuestionDB.db", Context.MODE_PRIVATE, null);

        database.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_QUESTION_SET + "( "
                + "UIN" + " INTEGER PRIMARY KEY, "
                + "STD" + " TEXT, "
                + "LEVEL" + " TEXT, "
                + "FREQ" + " INTEGER, "
                + "TOPIC" + " TEXT "
                + ")");

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.popupmenu, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

        if (ctx != null && (ctx.matches("ENGLISH GRAMMAR") || ctx.matches("SAMPLE MIDDLE ENGLISH GRAMMAR WORKSHEETS")) ){
            MenuItem item = menu.findItem(R.id.show_answers);
            item.setVisible(FALSE);
            item = menu.findItem(R.id.hide_answers);
            item.setVisible(FALSE);
        }
        else if(showAnswers) {
            MenuItem item = menu.findItem(R.id.show_answers);
            item.setVisible(FALSE);
            item = menu.findItem(R.id.hide_answers);
            item.setVisible(TRUE);
        }
        else {
            MenuItem item = menu.findItem(R.id.hide_answers);
            item = menu.findItem(R.id.hide_answers);
            item.setVisible(FALSE);
            item = menu.findItem(R.id.show_answers);
            item.setVisible(TRUE);
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch(item.getItemId()) {
            case R.id.show_answers :
                showAnswers = true;
                showFile(fileSA);
                break;
            case R.id.hide_answers :
                showAnswers = false;
                showFile(fileHA);
                break;
            case R.id.download_pdf :
                if(showAnswers)
                    onDownload(fileSA.toString());
                else
                    onDownload(fileHA.toString());
                break;

            case (R.id.Info) :{
                String msg = "Global School Worx is an application that provides easy access to Testpapers And Worksheets up till Tenth grade. \nChildren can master different topics by practising various worksheets related to a single Topic.\nSome sample school test papers are also provided.\nGlobal School Worx is dedicated to provide education to One & All and Welcome people to contribute by sending their kid’s school papers on globalschoolworx@gmail.com";
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage(msg).show();
            }
        }
        return super.onOptionsItemSelected(item);
    }

    //private final String LAST_DOWNLOADED_TIME_KEY = "LAST_DOWNLOADED_TIME_KEY";
    private final String LAST_DOWNLOADED_TIMESTAMP_KEY = "LAST_DOWNLOADED_TIMESTAMP_KEY";
    private void onDownload(String fileToPrint) {
        ActivityCompat.requestPermissions(FirebaseQueryActivity.this,
                new String[] {
                        android.Manifest.permission.READ_EXTERNAL_STORAGE,
                        android.Manifest.permission.WRITE_EXTERNAL_STORAGE
                },200);

        downloadWatermarkedPdf();
    }

    private void instantDownload () {
        Calendar cal = Calendar.getInstance();
        Date currentDate = cal.getTime();
        long curDate = currentDate.getTime();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);

        try {
            int permissionCheck;
            permissionCheck = ContextCompat.checkSelfPermission(FirebaseQueryActivity.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
            if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
                File root = android.os.Environment.getExternalStorageDirectory();
                File dir = new File(root.getAbsolutePath() + "/download");
                dir.mkdirs();
                File downloadedFile = new File(dir, "worksheet" + curDate + ".pdf");
                if (downloadedFile.exists()) {
                    downloadedFile.delete();
                }

                downloadedFile.createNewFile();
                boolean withAnswers = getIntent().getBooleanExtra(FirebaseQueryActivity.WITH_ANSWERS, false);
                if (ctx != null && ctx.matches("SAMPLE MIDDLE ENGLISH GRAMMAR WORKSHEETS")) {
                    FileChannel inChannel = null;
                    FileChannel outChannel = null;

                    try {
                        inChannel = new FileInputStream(fileHA).getChannel();
                        outChannel = new FileOutputStream(downloadedFile).getChannel();
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }

                    try {
                        inChannel.transferTo(0, inChannel.size(), outChannel);
                    } finally {
                        if (inChannel != null)
                            inChannel.close();
                        if (outChannel != null)
                            outChannel.close();
                    }

                } else {
                    if (ctx != null && ctx.matches("ENGLISH GRAMMAR"))
                        shuffle = false;
                    else
                        shuffle = true;

                    PdfGenerator.generatePdf(FirebaseQueryActivity.this, mFinalSelectedQuess, withAnswers, true, downloadedFile, shuffle);
                }


                prefs.edit()
                        .putLong(LAST_DOWNLOADED_TIMESTAMP_KEY, curDate)
                        .apply();

                AlertDialog.Builder downloadSuccessfulBuilder = new AlertDialog.Builder(FirebaseQueryActivity.this);
                String onSuccessDownloadMsg = "Please check " + downloadedFile.getPath() + " in Files/Download Folder";
                downloadSuccessfulBuilder.setMessage(onSuccessDownloadMsg).setTitle("Download Successful!");
                AlertDialog downloadSuccessfulDlg = downloadSuccessfulBuilder.create();
                downloadSuccessfulDlg.show();

            }else{
                AlertDialog.Builder downloadFailureBuilder = new AlertDialog.Builder(FirebaseQueryActivity.this);
                downloadFailureBuilder.setMessage("Permission Denied! Please try again by granting permission.").setTitle("Error!").show();
            }
        } catch (IOException e) {
            e.printStackTrace();
            AlertDialog.Builder downloadFailureBuilder = new AlertDialog.Builder(FirebaseQueryActivity.this);
            downloadFailureBuilder.setMessage("Please try again!").setTitle("Error!").show();

        }
    }
    private void downloadWatermarkedPdf() {
        Calendar cal = Calendar.getInstance();
        Date currentDate = cal.getTime();
        long curDate = currentDate.getTime();
        boolean hasSubscription = ApplicationConfigurations.hasSusbscription(getApplicationContext());

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        long lastDownloadedTimeStamp = prefs.getLong(LAST_DOWNLOADED_TIMESTAMP_KEY, 0);
        long nextValidDownload = (lastDownloadedTimeStamp == 0 ? lastDownloadedTimeStamp : lastDownloadedTimeStamp + (12*60*60*1000));

        if ( hasSubscription || (ctx != null && ctx.matches("SAMPLE MIDDLE ENGLISH GRAMMAR WORKSHEETS"))){
            instantDownload ();  // Unlimited Downloads
        }
        else if( nextValidDownload < curDate ){


            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Download Limit : One Document per 12 hours. Do you want to continue Downloading this file?").setTitle("Confirmation...")
                    .setIcon(R.mipmap.confirmation)
                    .setPositiveButton(android.R.string.yes, (dialog, whichButton) -> {
                        try {
                            int permissionCheck;
                            permissionCheck = ContextCompat.checkSelfPermission(FirebaseQueryActivity.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
                            if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
                                File root = android.os.Environment.getExternalStorageDirectory();
                                File dir = new File(root.getAbsolutePath() + "/download");
                                dir.mkdirs();
                                File downloadedFile = new File(dir, "worksheet" + curDate + ".pdf");
                                if (downloadedFile.exists()) {
                                    downloadedFile.delete();
                                }

                                downloadedFile.createNewFile();
                                boolean withAnswers = getIntent().getBooleanExtra(FirebaseQueryActivity.WITH_ANSWERS, false);


                                if (ctx != null && ctx.matches("ENGLISH GRAMMAR"))
                                    shuffle = false;
                                else
                                    shuffle = true;

                                PdfGenerator.generatePdf(FirebaseQueryActivity.this, mFinalSelectedQuess, withAnswers, true, downloadedFile, shuffle);



                                prefs.edit()
                                        .putLong(LAST_DOWNLOADED_TIMESTAMP_KEY, curDate)
                                        .apply();

                                AlertDialog.Builder downloadSuccessfulBuilder = new AlertDialog.Builder(FirebaseQueryActivity.this);
                                String onSuccessDownloadMsg = "Please check " + downloadedFile.getPath() + " in Files/Download Folder";
                                downloadSuccessfulBuilder.setMessage(onSuccessDownloadMsg).setTitle("Download Successful!");
                                AlertDialog downloadSuccessfulDlg = downloadSuccessfulBuilder.create();
                                downloadSuccessfulDlg.show();

                            }else{
                                AlertDialog.Builder downloadFailureBuilder = new AlertDialog.Builder(FirebaseQueryActivity.this);
                                downloadFailureBuilder.setMessage("Permission Denied! Please try again by granting permission.").setTitle("Error!").show();
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                            AlertDialog.Builder downloadFailureBuilder = new AlertDialog.Builder(FirebaseQueryActivity.this);
                            downloadFailureBuilder.setMessage("Please try again!").setTitle("Error!").show();
                        }
                    }).setNegativeButton(android.R.string.no, (dialog, whichButton) -> {});
            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            long breaktime = nextValidDownload - curDate;
            int hrs, mins, secs;
            //long l = convertTimeToHrsMinSecs(breaktime, &hrs;, &mins, &secs);
            {
                int x = (int)breaktime;
                hrs = x/(1000*3600);
                x = x - hrs*(3600*1000);
                mins = x / (60*1000);
                x = x - mins*(60*1000);
                secs = (x / 1000) + 5;

            }

            builder.setMessage("You have reached your free limit of One Download per 12 hours. Please come back after " +hrs+"hrs "+mins+"mins "+secs+"secs.").setTitle("Download Limit reached...").setIcon(R.mipmap.error);
            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch(requestCode){
            case 200:
                for (int grantResult1 : grantResults) {
                    if (grantResult1 == PackageManager.PERMISSION_DENIED) {
                        // Show a message to user that permission is not granted.
                        finish();
                    }
                }
                //      downloadWatermarkedPdf();
                break;

            case 100:
                for (int grantResult : grantResults) {
                    if (grantResult == PackageManager.PERMISSION_DENIED) {
                        // Show a message to user that permission is not granted.
                        finish();
                    }
                }

                break;
        }
    }

    void showFileType (File file, String type, Context ctx) {
        switch (type) {
            case "PDF" :
                showFile(file);
                break;
            case "XLS" :
                //showDocFile(file, ctx);
                showFile(file);
                break;
            case "DOC" :
                // showDocFile(file, ctx);
                showFile(file);
                break;
        }
    }

    void showDocFile (File file, Context ctx) {


        WebView urlWebView = (WebView) findViewById(R.id.webView);
        urlWebView.setWebViewClient(new AppWebViewClients());
        urlWebView.getSettings().setJavaScriptEnabled(true);
        urlWebView.getSettings().setUseWideViewPort(true);
        urlWebView.loadUrl(file.toString());
    }


    public class AppWebViewClients extends WebViewClient {

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            // TODO Auto-generated method stub
            view.loadUrl(url);
            return true;
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            // TODO Auto-generated method stub
            super.onPageFinished(view, url);

        }
    }
        /*
        Uri path = null;
        try {
            path = FileProvider.getUriForFile(ctx, ctx.getApplicationContext().getPackageName() + ".provider", file.getCanonicalFile());
        }
        catch (IOException e) {
            Toast.makeText(this,"Canonical File Error", Toast.LENGTH_SHORT).show();
        }
        Intent excelIntent = new Intent(Intent.ACTION_VIEW);
        //Uri.parse(file.toString()
        excelIntent.setDataAndType(path , "application/vnd.ms-excel");
      //  ctx.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(file.toString())).se);
        excelIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        try {
            ctx.startActivity(excelIntent);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(this,"No Application available to viewExcel", Toast.LENGTH_SHORT).show();
        }
        */


    void showFile ( File file) {
        PDFView pdfView = findViewById(R.id.displayPdfView);
        pdfView.fromFile(file)
                .defaultPage(0)
                .enableSwipe(true)
                .swipeHorizontal(false)
                .enableAnnotationRendering(true)
                .enableAntialiasing(true)
                .pageFitPolicy(FitPolicy.WIDTH)
                .scrollHandle(new DefaultScrollHandle(this))
                .load();
    }

    void showSampleWorksheet () {
        ArrayList<String> mParams;
        Properties properties = new Properties();
        String sampleFile = "";
        String firebaseDirName = "", topic = "";
        try {

            SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();
            SAXParser parser = saxParserFactory.newSAXParser();
            parser.parse(getResources().getAssets().open("configurations/samples_middleenggrammar.xml"), new DefaultHandler(){
                @Override
                public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
                    if(qName == "sample"){
                        String key = attributes.getValue("name");
                        String value = attributes.getValue("value");
                        properties.setProperty(key, value);
                    }
                }
            });
        }catch (SAXException e) {
            e.printStackTrace();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        mParams = getIntent().getStringArrayListExtra("SELECTED_PARAMS");
        if (mParams != null) {
            topic = properties.getProperty(mParams.get(0));
            sampleFile = properties.getProperty(mParams.get(2));
        }

        firebaseDirName = "middleenggrammar" + "/sample_worksheets" + File.separator + sampleFile;
        displaySamplePDF(firebaseDirName);

    }


    public void displaySamplePDF (String firebaseDirName){
        displayDocument (firebaseDirName, "PDF", this);
    }
    private void signInAnonymously() {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        mAuth.signInAnonymously().addOnSuccessListener(this, new  OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                // do your stuff
            }
        })
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Log.e(TAG, "signInAnonymously:FAILURE", exception);
                    }
                });
    }

    public void displayDocument (String firebaseDirName, String type, Context ctx) {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseStorage inst = FirebaseStorage.getInstance();
        StorageReference mStorageReference = inst.getReference();;
        StorageReference fileRef = mStorageReference.child(firebaseDirName);
        FirebaseUser user = mAuth.getCurrentUser();

        if (user != null) {
            // do your stuff
        } else {
            signInAnonymously();
        }

        String localDirName = firebaseDirName.replace('/' , '_');
        localDirName = localDirName.replace('.' , '_');
        File mydir =     ctx.getDir(localDirName, Context.MODE_PRIVATE);


        if(type.equals("PDF")) {
            localFile = new File(mydir, "sample.pdf");
        }else {
            localFile = new File(mydir, firebaseDirName);
        }

        fetchingComplete = false;

        if(localFile.exists()) {
            localFile.delete();
        }

        if (!localFile.exists()) {
            try {
                localFile.createNewFile();

                this.runOnUiThread(() -> {
                    if(type.equals("PDF")) {
                        final LinearLayout progressBar = findViewById(R.id.progressBarLayout);
                        progressBar.bringToFront();
                    } });


                FileDownloadTask fileDownloadTask = fileRef.getFile(localFile);
                fileDownloadTask.addOnSuccessListener(taskSnapshot -> {
                    // Local temp file has been created
                    runOnUiThread(() -> {
                        if(type.equals("PDF")) {
                            LinearLayout progressBar = (LinearLayout) findViewById(R.id.progressBarLayout);
                            progressBar.setVisibility(View.GONE);
                        }
                    });

                    fetchingComplete = true;
                    fileHA = localFile;
                    showFile(localFile);
                   // downloadFileType(localFile, firebaseDirName, type, ctx);

                });

                fileDownloadTask.addOnFailureListener(e -> runOnUiThread(() -> {
                    if(type.equals("PDF")) {
                        LinearLayout progressBar = findViewById(R.id.progressBarLayout);
                        progressBar.setVisibility(View.GONE);
                        /* Delete the local file path which exists due to previous failed download*/
                        localFile.delete();
                    }
                }));
            }catch (NullPointerException e) {
                if(type.equals("PDF")) {
                    final LinearLayout progressBar = findViewById(R.id.progressBarLayout);
                    progressBar.setVisibility(View.GONE);
                }
            } catch (IOException ignored) { }


        } else {
            runOnUiThread(() -> {
                if(type.equals("PDF")) {
                    LinearLayout progressBar = findViewById(R.id.progressBarLayout);
                    progressBar.setVisibility(View.GONE);
                }
            });

            fetchingComplete = true;
            downloadFileType(localFile, firebaseDirName , type, ctx);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    void downloadFileType(File inputFile, String fName, String type, Context ctx) {
        //long curDate = currentDate.getTime();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(ctx);


        try {
            int permissionCheck;

            ActivityCompat.requestPermissions(this,
                    new String[] {
                            android.Manifest.permission.READ_EXTERNAL_STORAGE,
                            android.Manifest.permission.WRITE_EXTERNAL_STORAGE
                    },200);

            permissionCheck = ContextCompat.checkSelfPermission(ctx, android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
            if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
                File root = android.os.Environment.getExternalStorageDirectory();
                File dir = new File(root.getAbsolutePath() + "/download"+"/homework");
                dir.mkdirs();

                File downloadedFile = new File(dir, fName);
                if (downloadedFile.exists()) {
                    downloadedFile.delete();
                }

                downloadedFile.createNewFile();
                FileInputStream instream = new FileInputStream(inputFile);
                FileOutputStream outstream = new FileOutputStream(downloadedFile);

                byte[] buffer = new byte[1024];

                int length;
                /*copying the contents from input stream to
                 * output stream using read and write methods
                 */
                while ((length = instream.read(buffer)) > 0){
                    outstream.write(buffer, 0, length);

                    AlertDialog.Builder downloadSuccessfulBuilder = new AlertDialog.Builder(ctx);
                    String onSuccessDownloadMsg = "Please check " + downloadedFile.getPath() + " in Files/Download Folder";
                    downloadSuccessfulBuilder.setMessage(onSuccessDownloadMsg).setTitle("Download Successful!");
                    AlertDialog downloadSuccessfulDlg = downloadSuccessfulBuilder.create();
                    downloadSuccessfulDlg.show();
                }
            } else {
                /*
                AlertDialog.Builder downloadFailureBuilder = new AlertDialog.Builder(ctx);
                downloadFailureBuilder.setMessage("Permission Denied! Please try again by granting permission.").setTitle("Error!").show();
                 */
            }
        }
        catch (IOException e) {}

    }
}
