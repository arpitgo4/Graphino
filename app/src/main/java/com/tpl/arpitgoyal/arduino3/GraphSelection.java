package com.tpl.arpitgoyal.arduino3;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.widget.ArrayAdapter;

/**
 * Created by Arpit Goyal on 2/28/2015.
 */
public class GraphSelection {

    Context context;
    MainActivity activity;
    public static int selection = 0;
    String[] graphs = {
            "Load v/s Time", "Displacement v/s Time", "Load v/s Displacement"
    };
    ArrayAdapter<String> adapter = null;

    public GraphSelection(Context context, MainActivity activity){
        this.context = context;
        this.activity = (MainActivity) activity;
        adapter = new ArrayAdapter<String>(context, android.R.layout.select_dialog_singlechoice ,graphs);
    }

    public void showDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Graphs");
        builder.setSingleChoiceItems(adapter, selection, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                selection = which;
                activity.changeLabels();
                dialog.dismiss();
            }
        });
        builder.setCancelable(true);
        AlertDialog dialog = builder.create();
        dialog.show();
    }

}
