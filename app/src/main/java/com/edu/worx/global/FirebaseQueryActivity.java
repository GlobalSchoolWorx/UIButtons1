package com.edu.worx.global;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.scroll.DefaultScrollHandle;
import com.github.barteksc.pdfviewer.util.FitPolicy;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.ColumnText;
import com.itextpdf.text.pdf.GrayColor;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;



public class FirebaseQueryActivity extends AppCompatActivity {

    File file;
    long lastSavedDate;
    ArrayList<QuestionSet> mQuess = new ArrayList<>();  // From Firebase
    ArrayList<QuestionSet> mFinalSelectedQuess = new ArrayList<>();  // Final Selected Question Set
    public static final String SELECTED_TOPICS = "SELECTED_TOPICS";
    public static final String SELECTED_STD = "SELECTED_STD";
    public static final String SELECTED_NUM = "SELECTED_NUM";
    public static final String SELECTED_LEVEL = "SELECTED_LEVEL";
    public static final String WITH_ANSWERS = "WITH_ANSWERS";
    public static final String TABLE_QUESTION_SET = "Question_Set_Table2";
    SQLiteDatabase database = null;

    public static class QuestionSet {
        public long UIN;
        public String Answer;
        public String Description;
        public String Keywords;
        public String Level;
        public String OptionA;
        public String OptionB;
        public String OptionC;
        public String OptionD;
        public String Question;
        public String Std;
        public String SubTopic;
        public String TimeLimit;
        public String Topic;
        public int    Freq;

        public void QuestionSet () {
            return;
        }
        public void QuestionSet (long inUIN, String inAnswer, String inDescription, String inKeywords, String inLevel,
                         String inOptionA, String inOptionB, String inOptionC, String inOptionD,
                         String inQuestion, String inStd, String inSubTopic, String inTimeLimit,
                         String inTopic) {

            Answer = inAnswer;
            Description = inDescription;
            Keywords = inKeywords;
            Level = inLevel;
            OptionA = inOptionA;
            OptionB = inOptionB;
            OptionC = inOptionC;
            OptionD = inOptionD;
            Question = inQuestion;
            Std = inStd;
            SubTopic = inSubTopic;
            TimeLimit = inTimeLimit;
            Topic = inTopic;
            UIN = inUIN;
        }

     }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_firebase_query);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        ActivityCompat.requestPermissions(this,
                new String[]{
                        android.Manifest.permission.READ_EXTERNAL_STORAGE,
                        android.Manifest.permission.WRITE_EXTERNAL_STORAGE
                }, 100);

        queryDatabase();
    }

    private void writeToPdfDoc( ArrayList<QuestionSet> arrQuess, boolean withAnswers) {
        try {
            int permissionCheck;
            permissionCheck = ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
            if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
                boolean dirCreated = false;
                File dir = getCacheDir();
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
                file = new File(dir,"worksheet_" + curDate + ".pdf");
                file.createNewFile();
                PdfGenerator.generatePdf(this, arrQuess, withAnswers, false, file);

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
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void queryDatabase () {
        int permissionCheck = ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
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
                        fetchQuesArrFromLocalDb(quesLocalArray, std, topicNum + extraQues.get(), level, finalTopicStr);
                        chooseMinFreqQuestionSet(quesLocalArray, topicNum + extraQues.get());
                        extraQuesCnt = extraQuesCnt - (extraQuesCnt/divFactor);
                        extraQues.getAndSet(extraQuesCnt);
                        if (latch.getAndDecrement() == 1) {
                            writeToPdfDoc(mFinalSelectedQuess, withAnswers);
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
    public void fetchQuesArrFromLocalDb(ArrayList<QuestionSet> quesLocalArray, String std, int num, String level, CharSequence topic){
        int i = 0;
        String columns[] = {"UIN", "STD","LEVEL", "TOPIC", "FREQ"};
        String whereArgs[] = {std, level, topic.toString()}; // {"Eighth", "Easy", "Percentage"};
        try(Cursor cursor = database.query(TABLE_QUESTION_SET, columns,"STD = ? AND LEVEL = ? AND TOPIC = ?", whereArgs, null, null, null)) {
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.popupmenu, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch(item.getItemId()) {
            case R.id.download_pdf :
                onDownload(file.toString());
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

    private void downloadWatermarkedPdf() {
        Calendar cal = Calendar.getInstance();
        Date currentDate = cal.getTime();
        long curDate = currentDate.getTime();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        long lastDownloadedTimeStamp = prefs.getLong(LAST_DOWNLOADED_TIMESTAMP_KEY, 0);
        long nextValidDownload = (lastDownloadedTimeStamp == 0 ? lastDownloadedTimeStamp : lastDownloadedTimeStamp + (12*60*60*1000));

        if( nextValidDownload < curDate ){
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Download Limit : One Document per 12 hours. Do you want to continue Downloading this file?").setTitle("Confirmation...")
                    .setIcon(R.mipmap.confirmation)
                    .setPositiveButton(android.R.string.yes, (dialog, whichButton) -> {
                        try {
                            int permissionCheck;
                            permissionCheck = ContextCompat.checkSelfPermission(FirebaseQueryActivity.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
                            if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
                                File root = android.os.Environment.getExternalStorageDirectory();
                                File dir = new File (root.getAbsolutePath() + "/download");
                                dir.mkdirs();
                                File downloadedFile = new File(dir, "worksheet"+ curDate+".pdf");
                                if(downloadedFile.exists()) {
                                    downloadedFile.delete();
                                }

                                downloadedFile.createNewFile();
                                boolean withAnswers = getIntent().getBooleanExtra( FirebaseQueryActivity.WITH_ANSWERS, false);
                                PdfGenerator.generatePdf(FirebaseQueryActivity.this, mFinalSelectedQuess, withAnswers, true, downloadedFile);
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
                downloadWatermarkedPdf();
                break;

            case 100:
                for (int grantResult : grantResults) {
                    if (grantResult == PackageManager.PERMISSION_DENIED) {
                        // Show a message to user that permission is not granted.
                        finish();
                    }
                }
   //             queryDatabase();   IK
                break;
        }
    }
}
