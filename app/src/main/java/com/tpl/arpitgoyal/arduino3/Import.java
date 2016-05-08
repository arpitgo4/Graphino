package com.tpl.arpitgoyal.arduino3;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.widget.Toast;

import java.io.File;
import java.io.Serializable;

/**
 * Created by Arpit Goyal on 2/28/2015.
 */
public class Import implements Serializable {

    public Context context = null;
    private String[] filelist = null;
    private String filename = null;
    private MainActivity main = null;
    private HomeScreen imp = null;
    public static Integer graphselected;
    String[] info;

    public Import(Context context, MainActivity main) {
        this.context = context;
        this.main = main;
    }

    public Import(Context context, HomeScreen imp) {
        this.context = context;
        this.imp = imp;
    }


    public void showFiles() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Files");
        File root = Environment.getExternalStorageDirectory();
        final File dir = new File(root + "/Arduino");
        if (dir.exists()) {
            filelist = dir.list();
            if (dir.list() == null) {
                Toast.makeText(context, "No Files On SD-Card", Toast.LENGTH_LONG).show();
            }
            builder.setItems(filelist, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    filename = filelist[which];
/*                    readFromFile(dir.toString() + "/" + filename);
                    if(imp != null) {
                        imp.setTableRows();
                        switch(graphselected){
                            case 0 : imp.view_peak_load.setText(info[4]);
                                break;
                            case 1 : imp.view_peak_disp.setText(info[4]);
                                break;
                            case 2 : imp.view_peak_load.setText(info[4]);
                                imp.view_peak_disp.setText(info[5]);
                                break;
                        }
                    }   */
                    File file = new File(Environment.getExternalStorageDirectory() + "/Arduino/" + filename);
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setDataAndType(Uri.fromFile(file), "application/pdf");
                    intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                    if(imp == null) main.startActivity(intent);
                    else imp.startActivity(intent);

                }
            });
            builder.create().show();
        } else {
            Toast.makeText(context, "Directory Does'nt Exists.", Toast.LENGTH_LONG).show();
        }
    }

/*    public void readFromFile(String filepath) {
        try {
            FileInputStream filein = new FileInputStream(new File(filepath));
            ObjectInputStream objin = new ObjectInputStream((filein));
            graphselected = (Integer) objin.readObject();
            Vector<Double> dataX = (Vector<Double>) objin.readObject();
            Vector<Double> dataY = (Vector<Double>) objin.readObject();
            info = (String[]) objin.readObject();
            //  DataPoint[] data = new DataPoint[dataX.size()];
            MainActivity.datapoints_all.clear();
            DataPoint d;
            for (int i = 0; i < dataX.size(); i++) {
                // data[i] = new DataPoint(dataX.get(i), dataY.get(i));
                d = new DataPoint(dataX.get(i), dataY.get(i));
                MainActivity.datapoints_all.add(d);
            }

            //    MainActivity.datapoints = data;
            objin.close();
            if (main == null) {
                imp.view_cust_name.setText(info[0]);
                imp.view_test_cond_by.setText(info[1]);
                imp.view_sample_id.setText(info[2]);
                imp.view_sample_size.setText(info[3]);
                imp.reDrawGraph();
            } else if (imp == null) {
                main.cust_name = info[0];
                main.test_cond_by = info[1];
                main.sample_id = info[2];
                main.sample_size = info[3];
                main.reDrawGraph();
            }
        } catch (Exception e) {
            Toast.makeText(context, "Exception : " + e.toString(), Toast.LENGTH_LONG).show();
        }
    }  */
}