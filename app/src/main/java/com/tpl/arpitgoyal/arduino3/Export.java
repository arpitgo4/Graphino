package com.tpl.arpitgoyal.arduino3;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Environment;
import android.widget.EditText;

import com.jjoe64.graphview.series.DataPoint;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by Arpit Goyal on 2/28/2015.
 */
public class Export implements Serializable {

    private DataPoint[] data = null;
    Context context = null;
    MainActivity activity;
    public static String filename = null;
    private EditText name = null;

    public Export(Context context, MainActivity activity){
        this.context = context;
        this.activity = activity;
        this.name = new EditText(context);
    }

    public void showDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Arduino").setIcon(R.mipmap.ic_launcher);
        builder.setMessage("Enter Filename : ");
        builder.setView(name);
        name.setText(new Date().getDate() + "-" + new Date().getMonth() + "-" + activity.sample_id);
        builder.setPositiveButton("Save" , new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int which) {
                filename = name.getText().toString();
                activity.peakload = activity.peak_load.getText().toString();
                activity.peakdisp = activity.peak_displacement.getText().toString();
                activity.takeScreenshot();
                new PdfCreator(context, activity).execute(filename, Environment.getExternalStorageDirectory() + "/pic.jpeg");
            }
        });
        builder.setNegativeButton("Cancel" , new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int which) {}
        });
        builder.create().show();
    }

  /*  public OutputStream getFileStream(){
        try{
            File file = Environment.getExternalStorageDirectory();
            String dirpath = file.toString() + "/Arduino";
            File dir = new File(dirpath);
            if(!dir.exists())
                dir.mkdir();
            FileOutputStream fileout = new FileOutputStream(dirpath + "/" + filename);
            return fileout;
        }catch(Exception e){
            Toast.makeText(context, "Error While Opening Stream To File : " + e.toString(), Toast.LENGTH_SHORT).show();
        }
        return null;
    }

    public void writeToFile(){
        try{
            File file = Environment.getExternalStorageDirectory();
            String dirpath = file.toString() + "/Arduino";
            File dir = new File(dirpath);
            if(!dir.exists())
                dir.mkdir();
          //  Vector<Vector<Double>> vec = readTempFile();
            Vector<Double> vecX = new Vector<Double>();
            Vector<Double> vecY = new Vector<Double>();
            FileOutputStream fileout = new FileOutputStream(dirpath + "/" + filename);
            ObjectOutputStream objout = new ObjectOutputStream(fileout);
      //      MainActivity.dataX.trimToSize(); MainActivity.dataY.trimToSize();
            for(int i = 0; i < MainActivity.datapoints_all.size(); i++)
                vecX.add(MainActivity.datapoints_all.get(i).getX());

            for(int i = 0; i < MainActivity.datapoints_all.size(); i++)
                vecY.add(MainActivity.datapoints_all.get(i).getY());


            objout.writeObject(new Integer(GraphSelection.selection));
            objout.writeObject(vecX);
            objout.writeObject(vecY);

            activity.peakload = activity.peak_load.getText().toString();
            activity.peakdisp = activity.peak_displacement.getText().toString();

            String[] info_0 = {
                    activity.cust_name, activity.test_cond_by ,activity.sample_id, activity.sample_size, activity.peakload
            };
            String[] info_1 = {
                    activity.cust_name, activity.test_cond_by ,activity.sample_id, activity.sample_size, activity.peakdisp
            };
            String[] info_2 = {
                    activity.cust_name, activity.test_cond_by ,activity.sample_id, activity.sample_size, activity.peakload, activity.peakdisp
            };
            switch(GraphSelection.selection){
                case 0 : objout.writeObject(info_0);
                    break;
                case 1 : objout.writeObject(info_1);
                    break;
                case 2 : objout.writeObject(info_2);
                    break;
            }

            objout.close();
            MainActivity.dataX.clear(); MainActivity.dataY.clear();
            MainActivity.datapoints_all.clear();
            Toast.makeText(context, "Data Saved On SD-CARD", Toast.LENGTH_LONG).show();
        }catch(Exception e){
            Toast.makeText(context, "Exception : " + e.toString(), Toast.LENGTH_LONG).show();
        }
    }

    public Vector<Vector<Double>> readTempFile(){
        Vector<Double> dataX = new Vector<Double>();
        Vector<Double> dataY = new Vector<Double>();
        Vector<Vector<Double>> vector = new Vector<Vector<Double>>();
        try {
            File file = Environment.getExternalStorageDirectory();
            String dirpath = file.toString() + "/Arduino";
            File dir = new File(dirpath);
            String[] files = dir.list();
            int i = 0;
            File tempfile = null;
            while (i < MainActivity.temp_counter) {
                FileInputStream filein = new FileInputStream(tempfile = new File(dirpath + "/temp" + i));
                ObjectInputStream objin = new ObjectInputStream(filein);
                dataX.addAll((Vector<Double>) objin.readObject());
                dataY.addAll((Vector<Double>) objin.readObject());
                objin.close();
                tempfile.delete();
                i++;
            }
            vector.add(dataX);
            vector.add(dataY);
            return vector;
        }catch(Exception e){
            Toast.makeText(context, "Exception : " + e.toString(), Toast.LENGTH_LONG).show();
        }
        return null;
    }

    public void writeToTempFile(){
        try{
            File file = Environment.getExternalStorageDirectory();
            String dirpath = file.toString() + "/Arduino";
            File dir = new File(dirpath);
            if(!dir.exists())
                dir.mkdir();
            FileOutputStream fileout = new FileOutputStream(dirpath + "/temp" + MainActivity.temp_counter);
            ObjectOutputStream objout = new ObjectOutputStream(fileout);
            MainActivity.dataX.trimToSize(); MainActivity.dataY.trimToSize();
            objout.writeObject(MainActivity.dataX);
            objout.writeObject(MainActivity.dataY);
            objout.close();
        }catch(Exception e){
            Toast.makeText(context, "Exception : " + e.toString(), Toast.LENGTH_LONG).show();
        }
    }

    public void deleteTempFiles(){
        try {
            File file = Environment.getExternalStorageDirectory();
            String dirpath = file.toString() + "/Arduino";
            File dir = new File(dirpath);
            int i = 0;
            File tempfile = null;
            while (i < MainActivity.temp_counter) {
                tempfile = new File(dirpath + "/temp" + i);
                tempfile.delete();
                i++;
            }
        }catch(Exception e){
            Toast.makeText(context, "Exception : " + e.toString(), Toast.LENGTH_LONG).show();
        }
    }  */
}
