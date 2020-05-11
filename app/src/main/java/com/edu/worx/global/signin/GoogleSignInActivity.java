package com.edu.worx.global.signin;

import android.accounts.Account;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.edu.worx.global.MainActivity;
import com.edu.worx.global.R;
//import com.google.android.gms.auth.api.signin.GoogleSignIn;
//import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.edu.worx.global.utils.MyJsonReader;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.ParseException;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;



// Configure sign-in to request the user's ID, email address, and basic
// profile. ID and basic profile are included in DEFAULT_SIGN_IN.


public class GoogleSignInActivity extends AppCompatActivity {

    static boolean canAccessHomeworkTab = false;
    GoogleSignInClient mGoogleSignInClient;
    int RC_SIGN_IN = 0;
    GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail().build();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_google_sign_in);

         // GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        SignInButton btn = findViewById(R.id.signInButton);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mySignIn();
            }
        });

        Button btnSkip = findViewById(R.id.skipButton);
        btnSkip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mySkipSignIn();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        // Check for existing Google Sign In account, if the user is already signed in
// the GoogleSignInAccount will be non-null.
          GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
      //  updateUI(account);
    }

    private void mySignIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    private void mySkipSignIn() {
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    public void verifyAccount(GoogleSignInAccount account) throws IOException {


        FirebaseStorage mFirebaseStorage = FirebaseStorage.getInstance();
        StorageReference mReference = mFirebaseStorage.getReference();
        StorageReference fileReference = mReference.child("Homework/SMS/sms_signin.json");
        File myDir =     getDir("GSWSignIn", Context.MODE_PRIVATE);
        File localFile = new File(myDir, "local_sms_signin.txt");
        if(localFile.exists()) {
            localFile.delete();
        }
        FileDownloadTask fileDownloadTask = fileReference.getFile(localFile);

        fileDownloadTask.addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                FileInputStream inputStream = null;
                try {
/*
                    BufferedReader br = new BufferedReader(new FileReader(localFile.toString()));

                    String strfile = br.readLine();
                    String strfile2 = br.readLine();

 */
                    FileInputStream in = new FileInputStream(localFile);
                    MyJsonReader jReader = new MyJsonReader();
                    canAccessHomeworkTab = jReader.readJsonStream(in, account.getEmail());

                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        });

        fileDownloadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });

    }

    public static boolean isHomeworkTabAccessible() {
        return canAccessHomeworkTab;
    }
    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            System.out.println();
            String name = account.getEmail();
           // runOnUiThread(() -> {
                try {
                    verifyAccount(account);
                } catch (IOException e) {
                    e.printStackTrace();
                }
       //     });
            startActivity(new Intent(this, MainActivity.class));
            finish();
            // Signed in successfully, show authenticated UI.

        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            //Log.w(TAG, "signInResult:failed code=" + e.getStatusCode());
            //updateUI(null);

        }
    }
}
