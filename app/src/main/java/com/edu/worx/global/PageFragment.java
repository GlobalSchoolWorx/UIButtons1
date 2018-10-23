package com.edu.worx.global;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.print.PrintAttributes;
import android.print.PrintDocumentAdapter;
import android.print.PrintDocumentInfo;
import android.print.PrintManager;
import android.print.pdf.PrintedPdfDocument;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.scroll.DefaultScrollHandle;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.ColumnText;
import com.itextpdf.text.pdf.GrayColor;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfImportedPage;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Calendar;
import java.util.Date;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link PageFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link PageFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PageFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM_SELECTED_CLASS = "SELECTED_CLASS";
    private OnFragmentInteractionListener mListener;
    private PDFView pdfView;
    private StorageReference mStorageReference;
    File localFile;
    String localDirName;
    int  pageCount;
    boolean fetchingComplete;
    public PageFragment() {
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @return A new instance of fragment PageFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static PageFragment newInstance(int param1, String selectedClass) {
        PageFragment fragment = new PageFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_PARAM1, param1);
        args.putString(ARG_PARAM_SELECTED_CLASS, selectedClass);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(TRUE);
        FirebaseStorage inst = FirebaseStorage.getInstance();
        mStorageReference = inst.getReference();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.page_fragment, container, false);

        pdfView = (PDFView) view.findViewById(R.id.fragmentPdfView);

        displayPDF(view, getActivity().getIntent().getStringExtra(DisplaySubjectsActivity.FIREBASE_FILE_PATH));
        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.popupmenu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch(item.getItemId()) {
            case R.id.download_pdf :
                onDownload(localFile.toString());
                break;

            case (R.id.Info) :{
                String msg = "Global School Worx is an application that provides easy access to Testpapers And Worksheets up till Tenth grade. \nChildren can master different topics by practising various worksheets related to a single Topic.\nSome sample school test papers are also provided.\nGlobal School Worx is dedicated to provide education to One & All and Welcome people to contribute by sending their kid’s school papers on globalschoolworx@gmail.com";
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setMessage(msg).show();
            }
        }
        return super.onOptionsItemSelected(item);
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if(mListener !=null) {
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
     * Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    public void displayPDF (final View view, String firebaseDirName) {

        StorageReference fileRef = mStorageReference.child(firebaseDirName + "paper.pdf");

        localDirName = firebaseDirName.replace('/', '_');
        File mydir = getContext().getDir(localDirName, Context.MODE_PRIVATE);

        localFile = new File(mydir, "paper.pdf");
        fetchingComplete = false;

        if (!localFile.exists()) {
            try {
                localFile.createNewFile();

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        final LinearLayout progressBar = (LinearLayout) view.findViewById(R.id.progressBar);
                        progressBar.bringToFront();
                    }           });


                FileDownloadTask fileDownloadTask = fileRef.getFile(localFile);
                long fileSize = localFile.getUsableSpace();
                fileDownloadTask.addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                        // Local temp file has been created
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                LinearLayout progressBar = (LinearLayout) view.findViewById(R.id.progressBar);

                                progressBar.setVisibility(View.GONE);

                            }
                        });

                        fetchingComplete = true;
                        pdfView.fromFile(localFile)
                                .defaultPage(1)
                                .enableSwipe(true)
                                .swipeHorizontal(false)
                                .enableAnnotationRendering(true)
                                .scrollHandle(new DefaultScrollHandle(PageFragment.this.getContext()))
                                .load();
                        pageCount = pdfView.getPageCount();

                    }
                });

                fileDownloadTask.addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                LinearLayout progressBar = (LinearLayout) view.findViewById(R.id.progressBar);
                                progressBar.setVisibility(View.GONE);
                                /* Delete the local file path which exists due to previous failed download*/
                                localFile.delete();
                            }
                        });
                    }
                });
            }catch (NullPointerException e) {
                final LinearLayout progressBar = (LinearLayout) view.findViewById(R.id.progressBar);
                progressBar.setVisibility(View.GONE);
            } catch (IOException ignored) {
            }


        } else {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    LinearLayout progressBar = (LinearLayout) view.findViewById(R.id.progressBar);
                    progressBar.setVisibility(View.GONE);
                }                });

            int size = (int)localFile.getUsableSpace();
            fetchingComplete = true;
            pdfView.fromFile(localFile)
                    .defaultPage(1)
                    .enableSwipe(true)
                    .swipeHorizontal(false)
                    .enableAnnotationRendering(true)
                    .scrollHandle(new DefaultScrollHandle(this.getContext()))
                    .load();

        }
    }

    public boolean watermarkPDF(String srcfirebaseDirName, String dest) {
        try {
            PdfReader pdfReader = new PdfReader(srcfirebaseDirName);
            PdfStamper pdfStamper = new PdfStamper(pdfReader, new FileOutputStream(dest));

            PdfContentByte content;
            for(int i=1; i<= pdfReader.getNumberOfPages(); i++){


                content = pdfStamper.getOverContent(i);

                Font bigFont = new Font(Font.FontFamily.HELVETICA, 52, Font.BOLD, new GrayColor(0.75f));
                Font smallFont = new Font(Font.FontFamily.HELVETICA, 14, Font.BOLD, new GrayColor(0.75f));
                float width = (float) pdfReader.getPageSize(1).getWidth();
                float height = (float) pdfReader.getPageSize(1).getHeight()-100;
                float rotation = (height/width)*45;
                ColumnText.showTextAligned(content, Element.ALIGN_LEFT, new Phrase("       GLOBAL   SCHOOL   WORX ", bigFont), 0, 0, rotation, PdfWriter.RUN_DIRECTION_DEFAULT, 100);
                ColumnText.showTextAligned(content, Element.ALIGN_CENTER, new Phrase("     globalschoolworx@gmail.com", smallFont), width/2, height/2, rotation, PdfWriter.RUN_DIRECTION_DEFAULT, 100);
                ColumnText.showTextAligned(content, Element.ALIGN_LEFT, new Phrase("***Downloaded from Google App GLOBAL SCHOOL WORX***", smallFont), 10, 10, 0, PdfWriter.RUN_DIRECTION_DEFAULT, 100);

            }

            pdfStamper.close();
            pdfReader.close();

        } catch (IOException e) {
            e.printStackTrace();
            return FALSE;
        }
        catch (DocumentException e) {
            e.printStackTrace();
            return FALSE;
        }

        return TRUE;
    }


     private void onDownload(String fileToPrint) {
         File root = android.os.Environment.getExternalStorageDirectory();
         File dir = new File (root.getAbsolutePath() + "/download");
         dir.mkdirs();
         File downloadedFile = new File(dir, localDirName+"paper.pdf");
         File mydir = getContext().getDir("", Context.MODE_PRIVATE);
         File localWatermarkFile = new File(mydir, "watermark.pdf");
         Date currentDate = Calendar.getInstance().getTime();
         long curDate = currentDate.getTime();
         long nextValidDownload = 0;

         if(localWatermarkFile.exists())
             nextValidDownload = localWatermarkFile.lastModified() + (12*60*60*1000) ;

         if( nextValidDownload < curDate){

             if (!fetchingComplete){
                 AlertDialog.Builder msgbuilder = new AlertDialog.Builder(getContext());
                 msgbuilder.setMessage("Still fetching the document. Please wait...");
                 msgbuilder.show();
                 return;
             }
             AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
             builder.setMessage("Download Limit : One Document per 12 hours. Do you want to continue Downloading this file?").setTitle("Confirmation...")
                     .setIcon(R.mipmap.confirmation)
                    .setPositiveButton(android.R.string.yes,  new DialogInterface.OnClickListener(){
                     public void onClick(DialogInterface dialog, int whichButton) {

                           boolean newFileCreated = FALSE;
                           boolean errFileAlreadyExist = FALSE;
                         /* Request user permissions in runtime */
                           ActivityCompat.requestPermissions(getActivity(),
                           new String[] {
                           android.Manifest.permission.READ_EXTERNAL_STORAGE,
                           android.Manifest.permission.WRITE_EXTERNAL_STORAGE
                           },100);


                           try {
                               int permissionCheck;
                               permissionCheck = ContextCompat.checkSelfPermission(getContext(), android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
                               if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
                                   if(!downloadedFile.exists()) {
                                       downloadedFile.createNewFile();
                                       newFileCreated = TRUE;

                                       if(!localWatermarkFile.exists()) {
                                           try {
                                               localWatermarkFile.createNewFile();
                                           } catch (IOException e) {
                                           }
                                       }
                                   }
                                   else
                                       errFileAlreadyExist = TRUE;
                               }

                        } catch (IOException e) {
                           e.printStackTrace();
                     }
                     if(newFileCreated && watermarkPDF(localFile.getAbsolutePath(), downloadedFile.getAbsolutePath())) {
                         localWatermarkFile.setLastModified(currentDate.getTime());
                         AlertDialog.Builder downloadSuccessfulBuilder = new AlertDialog.Builder(getContext());
                         String onSuccessDownloadMsg = "Please check " + localDirName + "paper.pdf in Files/Download Folder";
                         downloadSuccessfulBuilder.setMessage(onSuccessDownloadMsg).setTitle("Download Successful!");
                         AlertDialog downloadSuccessfulDlg = downloadSuccessfulBuilder.create();
                         downloadSuccessfulDlg.show();
                     }
                     else{
                         AlertDialog.Builder downloadFailureBuilder = new AlertDialog.Builder(getContext());
                         if(errFileAlreadyExist)
                           downloadFailureBuilder.setMessage("File already exists. Try again after deleting previously stored file.").setTitle("Error!").show();
                         else
                           downloadFailureBuilder.setMessage("Please try again.").setTitle("Error!").show();
                     }

                 }}).setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                     public void onClick(DialogInterface dialog, int whichButton) {
                     return;
             }});
             AlertDialog alertDialog = builder.create();
             alertDialog.show();


         }
         else {
             AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
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
             return;
         }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
    }

    public static void copyFile(File src, File dst) throws IOException {
        try (InputStream in = new FileInputStream(src)) {
            try (OutputStream out = new FileOutputStream(dst)) {
                // Transfer bytes from in to out
                byte[] buf = new byte[1024];
                int len;
                while ((len = in.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }
            }
        }
    }

    public void convertTimeToHrsMinSecs (){

    }


}

