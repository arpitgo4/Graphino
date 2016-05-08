package com.tpl.arpitgoyal.arduino3;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.os.Environment;
import android.widget.Toast;

import com.aspose.pdf.Document;
import com.aspose.pdf.Page;
import com.aspose.pdf.Position;
import com.aspose.pdf.TextBuilder;
import com.aspose.pdf.TextFragment;

import java.io.File;
import java.io.FileInputStream;
import java.util.Date;

/**
 * Created by Arpit Goyal on 2/28/2015.
 */
public class PdfCreator extends AsyncTask {

    Context context;
    MainActivity activity;
    Paint paint = new Paint(Color.BLACK);
    private int fontsize = 15;
    private int titlefontsize = 30;
    ProgressDialog progress;


    public PdfCreator(Context context, MainActivity activity){
        this.context = context;
        this.activity = activity;
    }

    public void createPDF(String filename, String imagename){
        String path = Environment.getExternalStorageDirectory() + "/Arduino/" + filename +".pdf";
        File file = new File(path);
        try {
            if (!file.exists())
                file.createNewFile();
        }catch(Exception e){
            Toast.makeText(context, "Error In File Creation", Toast.LENGTH_SHORT).show();
        }
        Document document = new Document();

        // add a page to PDF file
        Page page= document.getPages().add();

        // add text in new page
        //create text fragment

        TextFragment title = new TextFragment("Arduino Data Monitoring");
        title.setPosition(new Position(100, 670));

        title.getTextState().setFont(com.aspose.pdf.FontRepository.findFont("Helvetica"));
        title.getTextState().setFontSize(titlefontsize);

        TextFragment left_top = new TextFragment("Date : " + new Date().toString() + "                     Sample ID : " + activity.sample_id);
        left_top.setPosition(new Position(18, 590));

        //set text properties
        left_top.getTextState().setFont(com.aspose.pdf.FontRepository.findFont("Helvetica"));
        left_top.getTextState().setFontSize(fontsize);

        TextFragment left_bottom = new TextFragment("Customer's Name : " + activity.cust_name + "                         Test Conducted By : " + activity.test_cond_by);
        left_bottom.setPosition(new Position(18, 120));

        left_bottom.getTextState().setFont(com.aspose.pdf.FontRepository.findFont("Helvetica"));
        left_bottom.getTextState().setFontSize(fontsize);


        TextFragment right_bottom = null;

        //set text properties
        // create TextBuilder object
        TextBuilder textBuilder = new TextBuilder(page);
        // append the text fragment to the PDF page

        switch(GraphSelection.selection) {
            case 0 :
                right_bottom = new TextFragment("Sample Size : " + activity.sample_size
                      + "                       Max Load : " + activity.peakload
                );
                right_bottom.setPosition(new Position(40, 100));

                break;
            case 1 : right_bottom = new TextFragment("Sample Size : " + activity.sample_size
                    + "                         Max Disp : " + activity.peakdisp
                );
                right_bottom.setPosition(new Position(40, 100));

                break;
            case 2 : right_bottom = new TextFragment("Sample Size : " + activity.sample_size
                    + "           Max Load : " + activity.peakload
                    + "           Max Disp : " + activity.peakdisp
            );
                right_bottom.setPosition(new Position(18, 100));

                break;
        }

        right_bottom.getTextState().setFont(com.aspose.pdf.FontRepository.findFont("Helvetica"));
        right_bottom.getTextState().setFontSize(fontsize);

        textBuilder.appendText(title);
        textBuilder.appendText(left_top);
        textBuilder.appendText(left_bottom);
        textBuilder.appendText(right_bottom);

        String imagePath=Environment.getExternalStorageDirectory() + "/pic.jpeg";

        //set coordinates
        int lowerLeftX = 15;
        int lowerLeftY = 570;
        int upperRightX = 580;
        int upperRightY = 150;

        //load image into stream
        try {
            FileInputStream imageStream = new FileInputStream(new File(imagePath));

            //add image to Images collection of Page Resources
            page.getResources().getImages().add(imageStream);
            //using GSave operator: this operator saves current graphics state
            page.getContents().add(new com.aspose.pdf.Operator.GSave());
            //create Rectangle and Matrix objects
            com.aspose.pdf.Rectangle rectangle = new com.aspose.pdf.Rectangle(lowerLeftX, lowerLeftY, upperRightX, upperRightY);
            com.aspose.pdf.Matrix matrix = new com.aspose.pdf.Matrix(new double[]{rectangle.getURX() - rectangle.getLLX(), 0, 0, rectangle.getURY() - rectangle.getLLY(), rectangle.getLLX(), rectangle.getLLY()});
            //using ConcatenateMatrix (concatenate matrix) operator: defines how image must be placed
            page.getContents().add(new com.aspose.pdf.Operator.ConcatenateMatrix(matrix));
            com.aspose.pdf.XImage ximage = page.getResources().getImages().get_Item(page.getResources().getImages().size());
            //using Do operator: this operator draws image
            page.getContents().add(new com.aspose.pdf.Operator.Do(ximage.getName()));
            //using GRestore operator: this operator restores graphics state
            page.getContents().add(new com.aspose.pdf.Operator.GRestore());
            // save the newly generated PDF file

            // close image stream
            imageStream.close();
        }catch(Exception e){

        }
        // save the PDF file
        document.save(path);

    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        progress = ProgressDialog.show(context, "",  "Exporting . . . ", true);
    }

    @Override
    protected Object doInBackground(Object[] params) {
        createPDF((String)params[0], (String)params[1]);
        return null;
    }

    @Override
    protected void onPostExecute(Object o) {
        progress.dismiss();
        Toast.makeText(context, "File Saved", Toast.LENGTH_SHORT).show();
    }
}
