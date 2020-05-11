package com.edu.worx.global;

import android.app.AlertDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import androidx.appcompat.app.AppCompatActivity;

public class GenericAPIs extends AppCompatActivity {

    public static boolean isInternetConnected(Context ctx) {
        ConnectivityManager conMgr = (ConnectivityManager) ctx.getSystemService (Context.CONNECTIVITY_SERVICE);

        if (null != conMgr && null != conMgr.getActiveNetworkInfo()
                && conMgr.getActiveNetworkInfo().isAvailable()
                && conMgr.getActiveNetworkInfo().isConnected()) {

            return true;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
        builder.setMessage("NO Internet Connection").setTitle("ËRROR !!");
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
        return false;

    }
}
