package com.tpl.arpitgoyal.arduino3;

/**
 * Created by Arpit Goyal on 2/28/2015.
 */

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TextView;

/**
 * Created by Arpit Goyal on 2/28/2015.
 */
public class DetailsDialog {

    Context context;
    MainActivity activity;
    TextView cust_name, test_cond_by, sample_id, sample_size;
    EditText edit_cust_name, edit_test_cond_by, edit_sample_id, edit_sample_size;


    public DetailsDialog(Context context, MainActivity activity){
        this.context = context;
        this.activity = activity;
    }

    public void setViews(){
        cust_name = new TextView(context);
        cust_name.setText("Customer's Name : ");
        test_cond_by = new TextView(context);
        test_cond_by.setText("Test Conducted By : ");
        sample_id = new TextView(context);
        sample_id.setText("Sample ID : ");
        sample_size = new TextView(context);
        sample_size.setText("Sample Size : ");

        edit_cust_name = new EditText(context);
        edit_test_cond_by = new EditText(context);
        edit_sample_id = new EditText(context);
        edit_sample_size = new EditText(context);
    }

    public LinearLayout getLayout(){
        TableLayout layout = new TableLayout(context);
        setViews();

        layout.addView(cust_name, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
        layout.addView(edit_cust_name, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));

        layout.addView(test_cond_by, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
        layout.addView(edit_test_cond_by, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));

        layout.addView(sample_id, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
        layout.addView(edit_sample_id, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));

        layout.addView(sample_size, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
        layout.addView(edit_sample_size, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));


        return layout;
    }

    public void showDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage("Enter Details : ");
        builder.setView(getLayout());
        builder.setCancelable(false);
        builder.setPositiveButton("Start", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                activity.cust_name = edit_cust_name.getText().toString();
                activity.test_cond_by = edit_test_cond_by.getText().toString();
                activity.sample_id = edit_sample_id.getText().toString();
                activity.sample_size = edit_sample_size.getText().toString();
                activity.start();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}