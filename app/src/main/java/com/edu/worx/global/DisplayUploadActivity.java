package com.edu.worx.global;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.common.AccountPicker;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.net.URLConnection;
import java.util.Calendar;
import java.util.Date;



public class DisplayUploadActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    public static final int REQUEST_CODE = 1;
    public static final int ACCOUNT_REQUEST_CODE = 2;
    public static String accountName = "";

    Uri fileUri;
    private StorageReference mStorageReference;
    int selectedClass = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        FirebaseStorage inst = FirebaseStorage.getInstance();
        mStorageReference = inst.getReference();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_upload);

        Spinner spinner = findViewById(R.id.spinner1);
        Button  uploadButton = findViewById(R.id.uploadDoc);

        spinner.setOnItemSelectedListener(this);

        uploadButton.setOnClickListener(view -> {
            startOpenDocument();

        });
    }

    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
        // An item was selected. You can retrieve the selected item using
        parent.getItemAtPosition(pos);
        selectedClass = pos+1;


    }

    public void onNothingSelected(AdapterView<?> parent) {
        // Another interface callback
    }

    public void startOpenDocument ()
    {
        // ACTION_OPEN_DOCUMENT is the intent to choose a file via the system's file
        // browser.
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);

        // Filter to only show results that can be "opened", such as a
        // file (as opposed to a list of contacts or timezones)
        intent.addCategory(Intent.CATEGORY_OPENABLE);

        // Filter to show only images, using the image MIME data type.
        // If one wanted to search for ogg vorbis files, the type would be "audio/ogg".
        // To search for all documents available via installed storage providers,
        // it would be "*/*".
        intent.setType("*/*");
        startActivityForResult(intent, REQUEST_CODE);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK) {
            // handle result
            fileUri = data.getData();
            if(fileUri != null) {
                Intent intent = AccountPicker.newChooseAccountIntent(null, null, new String[]{"com.google"},
                        false, "To get Reward Points", "bi", null, null);
                startActivityForResult(intent, ACCOUNT_REQUEST_CODE);
            }

        }
        if (requestCode == ACCOUNT_REQUEST_CODE) {
            if ( resultCode == RESULT_OK) {
                accountName = data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
            }
            uploadDoc(fileUri);

        }
    }

    public void uploadDoc(Uri fileUri){
        //File uploadedFile = new File("");
        Date currentDate = Calendar.getInstance().getTime();
        //displaying a progress dialog while upload is going on
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Uploading");
        progressDialog.show();
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        String type = mime.getExtensionFromMimeType(cR.getType(fileUri));
        StorageReference fileRef = mStorageReference.child("uploads/Class"+selectedClass+"_"+ accountName + "_"+ currentDate.toString()+"_."+type);

        UploadTask fileUploadTask = fileRef.putFile(fileUri);
        fileUploadTask.addOnSuccessListener(taskSnapshot -> {
            progressDialog.dismiss();
            //and displaying a success toast
            if(accountName != "")
                Toast.makeText(getApplicationContext(), "File Uploaded Successfully. After verification, your reward points will be mailed to :"+accountName, Toast.LENGTH_LONG).show();
            else
                Toast.makeText(getApplicationContext(), "File Uploaded Successfully.", Toast.LENGTH_LONG).show();

        });
        fileUploadTask.addOnFailureListener(onFailureListener-> {
            Exception e = fileUploadTask.getException();
            progressDialog.dismiss();
            return;

        });

         fileUploadTask.addOnProgressListener(taskSnapshot-> {
                            //calculating progress percentage
                double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();

                //displaying percentage in progress dialog
                progressDialog.setMessage("Uploaded " + ((int) progress) + "%...");

        });
    }
}
