package com.tpl.arpitgoyal.arduino3;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TextView;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;


public class ImportActivity extends Activity {

    private GraphView graph;
    private Button but;
    private LineGraphSeries<DataPoint> series;
    public String cust_name, sample_size, sample_id, test_cond_by, peakload, peakdisp;
    TextView view_cust_name, view_test_cond_by, view_sample_size, view_sample_id, view_peak_load, view_peak_disp;
    TableLayout layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_import);

        but = (Button) findViewById(R.id.button2);
        but.setOnClickListener(butlistener);
    }

    Button.OnClickListener butlistener = new Button.OnClickListener(){
        @Override
        public void onClick(View v) {
         //   new Import(ImportActivity.this, ImportActivity.this).showFiles();
        }
    };

    public void doOnClick(){

    }

    public void reDrawGraph() {
        graph.removeAllSeries();
        series = new LineGraphSeries<DataPoint>();
        graph.addSeries(series);
   /*     for (int i = 0; i < MainActivity.datapoints.length; i++)
            series.appendData(MainActivity.datapoints[i], true, MainActivity.datapoints.length);   */
        series.resetData(MainActivity.datapoints_all.toArray(new DataPoint[MainActivity.datapoints_all.size()]));
    }

    /*
        @Override
        public void onConfigurationChanged(Configuration newConfig) {
            super.onConfigurationChanged(newConfig);
            setContentView(R.layout.activity_import);
            but = (Button) findViewById(R.id.button2);
            but.setOnClickListener(butlistener);
        }
    */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_import, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
