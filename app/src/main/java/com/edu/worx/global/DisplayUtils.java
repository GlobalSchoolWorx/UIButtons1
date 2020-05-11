package com.edu.worx.global;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DownloadManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Environment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class DisplayUtils {

    static String localDirName;

    private static void signInAnonymously(FirebaseAuth auth) {
        FirebaseUser user = auth.getCurrentUser();

        if (user == null) {
            auth.signInAnonymously().addOnSuccessListener(authResult -> {})
                    .addOnFailureListener(exception -> { });
        }
    }

    public static void displayDocument(Activity activity, String firebaseDirName, String dirName, String type) {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        signInAnonymously(mAuth);
        FirebaseStorage inst = FirebaseStorage.getInstance();
        StorageReference mStorageReference = inst.getReference();;
        StorageReference fileRef = mStorageReference.child(firebaseDirName);

        String localFileName = firebaseDirName.replace('/' , '_');
        localDirName = localFileName.replace('.' , '_');
        File mydir = activity.getDir(localDirName, Context.MODE_PRIVATE);
        String localName = "sample." + type.toLowerCase();
        File localFile = new File(mydir, localName);

        if(localFile.exists()) {
            localFile.delete();
        }

        if (!localFile.exists()) {
            try {
                localFile.createNewFile();

                FileDownloadTask fileDownloadTask = fileRef.getFile(localFile);
                fileDownloadTask.addOnSuccessListener(taskSnapshot -> downloadFileType(activity, localFile, localFileName, dirName, type));
                fileDownloadTask.addOnFailureListener(e -> {
                    AlertDialog.Builder downloadFailureBuilder = new AlertDialog.Builder(activity);

                    downloadFailureBuilder.setMessage("Unable to download the File.").setTitle("Download Failed!");
                    AlertDialog downloadFailDlg = downloadFailureBuilder.create();
                    downloadFailDlg.show();
                });
            }catch (NullPointerException ignored) {
                System.out.println();

            } catch (IOException ignored) { }
        } else {
            downloadFileType(activity, localFile, localDirName, dirName, type);
        }
    }

    /* type to be used when type specific handling is required */
    private static void downloadFileType(Activity activity, File inputFile, String fName, String localDirName, String type) {
        try {
            int permissionCheck;

            ActivityCompat.requestPermissions(activity,
                    new String[] {
                            android.Manifest.permission.READ_EXTERNAL_STORAGE,
                            android.Manifest.permission.WRITE_EXTERNAL_STORAGE
                    },200);
            permissionCheck = ContextCompat.checkSelfPermission(activity, android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
            if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
                File root = android.os.Environment.getExternalStorageDirectory();
                File dir = new File(root.getAbsolutePath() + "/download/"+ localDirName);
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
                while ((length = instream.read(buffer)) > 0) {
                    outstream.write(buffer, 0, length);
                }
                String onSuccessDownloadMsg = "Please check " + downloadedFile.getPath() + " in Files/Downloads Folder. \nDo you want to open the Downloads folder";

                AlertDialog.Builder downloadSuccessfulBuilder = new AlertDialog.Builder(activity);


                downloadSuccessfulBuilder.setMessage(onSuccessDownloadMsg).setTitle("Download Successful!");
                /*
                AlertDialog downloadSuccessfulDlg = downloadSuccessfulBuilder.create();
                downloadSuccessfulDlg.show();
                */
            //    downloadSuccessfulBuilder.setTitle("Download Successful!");
                downloadSuccessfulBuilder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        openFolder(activity,"download/"+ localDirName );
                    }
                });

                downloadSuccessfulBuilder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                downloadSuccessfulBuilder.show();

            } else {

            }
        }
        catch (IOException e) {}

    }

    public static void openFolder(Activity activity, String folderName){
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        Uri uri = Uri.parse(Environment.DIRECTORY_DOWNLOADS);
        intent.setDataAndType(uri, "*/*");
        // activity.startActivity(Intent.createChooser(intent, "Open folder"));
        activity.startActivity(new Intent(DownloadManager.ACTION_VIEW_DOWNLOADS));
    }
}
