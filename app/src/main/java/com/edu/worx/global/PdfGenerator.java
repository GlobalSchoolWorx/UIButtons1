package com.edu.worx.global;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.pdf.PdfDocument;
import android.support.v4.util.Pair;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.util.DisplayMetrics;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;


public class PdfGenerator {
    private static final int mPageWidthA4Size = 595;
    private static final int mPageHeightA4Size = 841;

    public static void generatePdf(Context cxt, ArrayList<QuestionSet> arrQuess, boolean withAnswers, boolean withWatermark, File targetPdf, boolean shuffle){
        Random seed = new Random();
        DisplayMetrics displayMetrics = cxt.getApplicationContext().getResources().getDisplayMetrics();
        //float dpHeight = displayMetrics.heightPixels / displayMetrics.density;
        //float dpWidth = displayMetrics.widthPixels / displayMetrics.density;
        //int dpHeightInt = (int) dpHeight, dpWidthInt = (int) dpWidth;

        //AssetManager am = cxt.getApplicationContext().getAssets();
        //Typeface typeface = Typeface.createFromAsset(am, "fonts/freeSans.ttf");
        TextPaint paint = new TextPaint();
        //paint.setTypeface(typeface);
        paint.setAntiAlias(true);
        paint.setTextSize(8 * displayMetrics.density);
        paint.setColor(0xFF000000);
        Layout.Alignment alignment = Layout.Alignment.ALIGN_NORMAL;
        final int leftPadding = (int) (7.6 * displayMetrics.density);
        final int rightPadding = (int)(11.4 * displayMetrics.density);
        final int topPadding = (int) (3.8 * displayMetrics.density);
        final int footer = (int) (20 * displayMetrics.density);
        final int textAreaWidth = mPageWidthA4Size -(leftPadding+rightPadding);
        CombinedStringBuilder csb = getQuestionsnAnswers(seed, arrQuess, shuffle);
        StaticLayout staticLayout = new StaticLayout(csb.questions, paint, textAreaWidth, alignment, 1.2f, 0.8f, true);

        Pair<Integer, Integer> pageDimension = getPageDimension(staticLayout, topPadding, footer);
        PdfDocument.PageInfo pageInfo = getPageInfo(pageDimension, 1);

        PdfDocument document = new PdfDocument();
        PdfDocument.Page page = document.startPage(pageInfo);
        Canvas canvas = page.getCanvas();
        paint.setColor(0xFFA1E7EE);

        canvas.drawRect(0, 0, pageDimension.first, pageDimension.second, paint);
        canvas.save();
        canvas.translate(leftPadding, topPadding);

        staticLayout.draw(canvas);
        canvas.restore();

        paint.setColor(0xFF000000);
        if(withWatermark) {
            drawWatermark(canvas, mPageWidthA4Size, mPageHeightA4Size, staticLayout.getHeight() + topPadding, leftPadding, rightPadding);
        }
        canvas.save();
        canvas.translate(leftPadding, topPadding);
        staticLayout.draw(canvas);
        canvas.restore();

        document.finishPage(page);

        if(withAnswers) {

            paint.setAntiAlias(true);
            paint.setTextSize(8 * displayMetrics.density);
            paint.setColor(0xFF000000);

            staticLayout = new StaticLayout("ANSWERS\n\n\n"+ csb.answers, paint, textAreaWidth, alignment, 1.2f, 0.8f, true);

            pageDimension = getPageDimension(staticLayout, topPadding, footer);
            pageInfo = getPageInfo(pageDimension, 2);
            page = document.startPage(pageInfo);
            canvas = page.getCanvas();
            paint.setColor(0xFFA1E7EE);

            canvas.drawRect(0, 0, pageDimension.first, pageDimension.second, paint);
            canvas.save();
            canvas.translate(leftPadding, topPadding);

            staticLayout.draw(canvas);
            canvas.restore();

            paint.setColor(0xFF000000);
            if(withWatermark) {
                drawWatermark(canvas, mPageWidthA4Size, mPageHeightA4Size, staticLayout.getHeight() + topPadding, leftPadding, rightPadding);
            }
            canvas.save();
            canvas.translate(leftPadding, topPadding);
            staticLayout.draw(canvas);
            canvas.restore();

            document.finishPage(page);


        }

        try {
            document.writeTo(new FileOutputStream(targetPdf));
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(cxt, "Can't generate PDF due to internal error. Please report the problem!", Toast.LENGTH_LONG).show();
        }
        document.close();
    }

    private static PdfDocument.PageInfo getPageInfo(Pair<Integer, Integer> pageDimension, int pageNumber){
        return new PdfDocument.PageInfo.Builder(pageDimension.first, pageDimension.second, pageNumber).create();
    }

    private static Pair<Integer, Integer> getPageDimension(StaticLayout staticLayout, int topPadding, int footer){
        return new Pair<>(mPageWidthA4Size, staticLayout.getHeight()+topPadding+footer);
    }

    private static void drawWatermark(Canvas canvas, int pagewidth, int pageHeight, int textAreaHeight, int leftPadding, int rightPadding){
        TextPaint paintWaterMark = new TextPaint();
        paintWaterMark.setAntiAlias(true);
        paintWaterMark.setTextSize(48);
        paintWaterMark.setColor(Color.LTGRAY);
        paintWaterMark.setFakeBoldText(true);
        paintWaterMark.setTextAlign(Paint.Align.CENTER);
        Path[] paths = getWatermarkPaths(pagewidth, pageHeight, textAreaHeight);
        for(Path path : paths) {
            canvas.save();
            canvas.drawTextOnPath("GLOBAL SCHOOL WORX", path, 0, 0, paintWaterMark);
            canvas.restore();
        }

        float lineStrokeSize = 0.6f;
        paintWaterMark.setTextSize(10);
        paintWaterMark.setFakeBoldText(false);
        paintWaterMark.setTextAlign(Paint.Align.LEFT);
        paintWaterMark.setStrokeWidth(lineStrokeSize);
        canvas.save();
        canvas.translate(leftPadding, textAreaHeight);
        Layout.Alignment alignment = Layout.Alignment.ALIGN_NORMAL;
        StaticLayout staticLayout = new StaticLayout("***Downloaded from Google App GLOBAL SCHOOL WORX*** e-mail:globalschoolworx@gmail.com", paintWaterMark, pagewidth-(leftPadding+rightPadding), alignment, 1.2f, 0.8f, true);
        canvas.drawLine(0.0f, 0.0f, (float)pagewidth, 0.0f, paintWaterMark);
        canvas.restore();
        canvas.save();
        canvas.translate(leftPadding, textAreaHeight+lineStrokeSize);
        staticLayout.draw(canvas);
        canvas.restore();
    }

    private static Path[] getWatermarkPaths(int pagewidth, int pageHeight, int sheetHeight){
        int cnt = Math.max((sheetHeight/pageHeight)+1, 1);
        Path paths[] = new Path[cnt];
        for(int i=0; i<cnt; i++){
            paths[i] = new Path();
            paths[i].moveTo(0, (i+1)*pageHeight);
            paths[i].lineTo(pagewidth, i*pageHeight);
        }

        return paths;
    }
    private static CombinedStringBuilder getQuestionsnAnswers( Random seed, ArrayList<QuestionSet> arrQuess, boolean shuffle){
        StringBuilder questions = new StringBuilder();
        StringBuilder answers = new StringBuilder();
        CombinedStringBuilder quesansArr = new CombinedStringBuilder();
        String romanTag = "";
        quesansArr.answers = answers;
        quesansArr.questions = questions;


        ArrayList<QuestionSet> questionsSet = new ArrayList<>(arrQuess);
        int tag = 1, z, counter = 0;
        int repeatCounterAfter = 6;  /* Only 5 sub questions per Selected Question */

        if (!shuffle) {
            for ( int i = 0; i<arrQuess.size(); i++) {
                z = counter % repeatCounterAfter;
                switch (counter) {
                    case 1:
                        romanTag = "i";
                        break;
                    case 2:
                        romanTag = "ii";
                        break;
                    case 3:
                        romanTag = "iii";
                        break;
                    case 4:
                        romanTag = "iv";
                        break;
                    case 5:
                        romanTag = "v";
                        break;
                    case 6:
                        romanTag = "vi";
                        break;
                    case 7:
                        romanTag = "vii";
                        break;
                    case 8:
                        romanTag = "viii";
                        break;
                    case 9:
                        romanTag = "ix";
                        break;
                    case 10:
                        romanTag = "x";
                        break;
                }
                if (z == 0) {
                    questions.append("Q").append(tag).append(": ").append(arrQuess.get(i).Question).append("\n\n");
                    tag++;
                } else
                    questions.append(romanTag).append(") ").append(arrQuess.get(i).Question).append("\n\n");

                if (counter == repeatCounterAfter) /* Reset Counter after  every 5 sub questions + 1 main question */
                    counter = 0;

                    counter++;
            }
            return quesansArr;
        }


        while(!questionsSet.isEmpty()) {
            int index = seed.nextInt(questionsSet.size()-1);

            questions.append("Q").append(tag).append(": ").append(questionsSet.get(index).Question).append("\n\n");
            answers.append("Answer").append(tag).append(": ").append(questionsSet.get(index).Answer).append("\n\n");
            questionsSet.remove(index);

            if(questionsSet.size() == 1){
                tag++;
                questions.append("Q").append(tag).append(": ").append(questionsSet.get(0).Question).append("\n\n");
                answers.append("Answer").append(tag).append(": ").append(questionsSet.get(0).Answer).append("\n\n");
                questionsSet.remove(0);
            }
            tag++;
        }

        return quesansArr;
    }
}
