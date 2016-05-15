package com.tpl.arpitgoyal.arduino3;

import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothSocket;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.Random;
import java.util.UUID;
import java.util.Vector;

import mockDevice.MockDevice;


public class MainActivity extends Activity {

    boolean isgraphchanged = false;
    boolean connected = false;
    int times = 0;

    public long previous_time = 0;
    public static boolean go = false;
    boolean cleared = true;
    public static DataPoint[] datapoints = null;
    public DataPoint datapoint = null;
    public static Vector<Double> dataX = new Vector<>();
    public static Vector<Double> dataY = new Vector<>();
    public static Vector<DataPoint> datapoints_all = new Vector<>();
    public static int counter = 0;
    public static int temp_counter = 0;
    private double maxX = 50;
    public double max_load = 0, max_displacement = 0;

    private GraphView graph = null;
    private Random rand = new Random();
    private EditText data;
    private Button but, startstop;
    public TextView peak_load, peak_displacement, live_load, live_displacement;
    public TextView tv1, tv2, tv3, tv4;
    private EditText dialog_load, dialog_displacement, dialog_time;
    View rootView;
    public static Bitmap scrshot = null;

    public String cust_name, sample_size, sample_id, test_cond_by, peakload, peakdisp;
    String maxload, maxdisp;

    int lastxvalue = 0;
    private LineGraphSeries<DataPoint> series = null;
    private Handler handler = new Handler();
    private Runnable runnable = null;
    private Runnable runnable2 = null;
    private Runnable runnable3 = null;
    private boolean stop = true;
    private int width_inc = 50;

    BluetoothSocket mmSocket;
    OutputStream mmOutputStream;
    InputStream mmInputStream;
    BufferedReader reader;
    OutputStreamWriter writer;
    Thread workerThread;
    byte[] readBuffer;
    int readBufferPosition;
    volatile boolean stopWorker;
    char[] buffer = new char[1024];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //  setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        setContentView(R.layout.activity_main);

        but = (Button) findViewById(R.id.button);
        startstop = (Button) findViewById(R.id.button3);
       // data = (EditText) findViewById(R.id.textView);
        peak_displacement = (TextView) findViewById(R.id.peak_displacement);
        peak_load = (TextView) findViewById(R.id.peak_load);
        live_displacement = (TextView) findViewById(R.id.live_displacement);
        live_load = (TextView) findViewById(R.id.live_load);
        tv1 = (TextView) findViewById(R.id.tv1);
        tv2 = (TextView) findViewById(R.id.tv2);
        tv3 = (TextView) findViewById(R.id.tv3);
        tv4 = (TextView) findViewById(R.id.tv4);


        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width=dm.widthPixels;
        int height=dm.heightPixels;
        int dens=dm.densityDpi;
        double wi=(double)width/(double)dens;
        double hi=(double)height/(double)dens;
        double x = Math.pow(wi,2);
        double y = Math.pow(hi,2);
        double screenInches = Math.sqrt(x+y);

        graph = new GraphView(MainActivity.this);
        graph.getViewport().setMinX(0);
        graph.getViewport().setMaxX(1000);
        graph.getGridLabelRenderer().setVerticalAxisTitle("Load (kn)");
        tv1.setText("Load (kn)");  tv3.setText("Load (kn)");
        tv2.setText("Time (ms)");  tv4.setText("Time (ms)");
        graph.getGridLabelRenderer().setHorizontalAxisTitle("Time (ms)");
        //graph.getViewport().setXAxisBoundsManual(true);
        //graph.getViewport().setScrollable(true);

        rootView = graph.getRootView();

        // int len = height - 250;
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 420);
        graph.setPadding(0, 0, 10, 0);
        this.addContentView(graph, params);

   /*     runnable2 = new Runnable() {
            @Override
            public void run() {
                getData();
                handler.post(runnable2);
            }
        };  */

        runnable3 = new Runnable() {
            @Override
            public void run() {
              //  getResetData();

                switch(GraphSelection.selection){
                    case 0 : getResetData();
                        break;
                    case 1 : getResetData(new Double(0));
                        break;
                    case 2 : getResetData(new Double(0), new Double(1));
                        break;
                }
                // Logic For Multi Graphs
                handler.post(runnable3);
            }
        };

        but.setOnClickListener(buttonlistener);
        startstop.setOnClickListener(listener);
    }

    public void start(){
        if(stop == true && connected == true){
            startstop.setText("STOP");
            stop = false;
            addNewSeries();
            handler.post(runnable3);
        }else{
            startstop.setText("START");
            stop = true;
            handler.removeCallbacks(runnable3);
        }
    }

    EditText maxLoad = null;
    EditText maxDisp = null;
    EditText edit = null;

    public void initDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setMessage("Enter Maximum Values : ");
        LinearLayout layout = new LinearLayout(MainActivity.this);
        layout.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        TextView t = new TextView(MainActivity.this); t.setText("Load (kn)");
        TextView T = new TextView(MainActivity.this); T.setText("Displacement (mm)");
        layout.addView(t);
        maxLoad = new EditText(MainActivity.this);
        maxLoad.setText("0.0");
        layout.addView(maxLoad);
        layout.addView(T, param);
        maxDisp = new EditText(MainActivity.this);
        maxDisp.setText("0.0");
        layout.addView(maxDisp);
        builder.setView(layout);builder.setPositiveButton("Send", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                try {
                    if(connected == false) {
                        openBT();
                        connected = true;
                        but.setText("DISCONNECT");
                        maxload = maxLoad.getText().toString();
                        maxdisp = maxDisp.getText().toString();
                        writer.write(maxload + "," + maxdisp + ",");
                        writer.flush();
                    }
                    else{
                        closeBT();
                        connected = false;
                        but.setText("CONNECT");
                    }
                }catch(Exception e){
                    Toast.makeText(MainActivity.this, "Exception While Sending Data" , Toast.LENGTH_LONG).show();
                }
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

    Button.OnClickListener listener = new Button.OnClickListener(){
        @Override
        public void onClick(View v) {
            if(isgraphchanged == true || cleared == true) {
                lastxvalue = 0;
                isgraphchanged = false;
                cleared = false;
                live_displacement.setText("0");
                live_load.setText("0");
                peak_displacement.setText("0");
                peak_load.setText("0");
                new DetailsDialog(MainActivity.this, MainActivity.this).showDialog();
            }
            else start();
        }
    };

    public void closeBT(){
        try {
            writer.close();
            reader.close();
            Toast.makeText(MainActivity.this, "Connection To "+ HomeScreen.mmDevice.getName() + " Closed", Toast.LENGTH_SHORT).show();
        }catch(Exception e){
            Toast.makeText(MainActivity.this, "Error Closing Connection", Toast.LENGTH_SHORT).show();
        }
    }

    Button.OnClickListener buttonlistener = new Button.OnClickListener(){
        @Override
        public void onClick(View v) {
            if(connected == false) {
                initDialog();
                but.setText("DISCONNECT");
            }
            else {
                connected = false;
                closeBT();
                but.setText("CONNECT");
            }
        }
    };

    public void openBT() {
        UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb"); //Standard
        //SerialPortService ID
        try {
            mmSocket = HomeScreen.mmDevice.createRfcommSocketToServiceRecord(uuid);
            mmSocket.connect();
            mmOutputStream = mmSocket.getOutputStream();
            mmInputStream = mmSocket.getInputStream();
            Toast.makeText(this, "Connection Established With " + HomeScreen.mmDevice.getName(), Toast.LENGTH_LONG).show();
        }catch(Exception e){
            Toast.makeText(MainActivity.this, "Unable to connect to bluetooth device.", Toast.LENGTH_LONG).show();
            /*
             injecting mock device's output and input stream
              */
            MockDevice device = new MockDevice();
            mmOutputStream = device.getOutputStream();
            mmInputStream = device.getInputStream();
            Toast.makeText(MainActivity.this, "Connection established with mock device", Toast.LENGTH_LONG).show();
        }
        writer = new OutputStreamWriter(mmOutputStream);
        reader = new BufferedReader(new InputStreamReader(mmInputStream));
    }

    Vector<Double> load = new Vector<Double>();
    Vector<Double> disp = new Vector<Double>();
    Vector<Double> time = new Vector<Double>();

    /**
     * Data Format that app is expecting from arduino
     * circuit is
     * reader.readLine() -> load, displacement, time
     */
    public void getResetData(){
        String s = "" ;
        int count = 0;
        load.clear(); disp.clear(); time.clear();
        try {
                    s = reader.readLine();
            Thread.sleep(500);
                    String[] vals = s.split(",");
                    load.add(new Double(vals[0]));
                    disp.add(new Double(vals[1]));
                    time.add(new Double(vals[2]));

        } catch (Exception e) {
              Toast.makeText(this, "Exception while getting data e : " + s, Toast.LENGTH_LONG).show();
        }
        try {
            DataPoint d;
            for (int i = 0; i < load.size(); i++) {
                datapoints_all.add(d = new DataPoint(time.get(i), load.get(i)));
                live_load.setText(new Double(d.getY()).toString());
          /*      if(datapoints_all.size() > 1)
                if (d.getX() < datapoints_all.get(datapoints_all.size() - 2).getX()) {
                    //adjustAddedPoint(datapoints_all.toArray(new DataPoint[datapoints_all.size()]));
                    DataPoint last = datapoints_all.get(datapoints_all.size() - 2);
                    datapoints_all.remove(datapoints_all.size() - 2);
                    datapoints_all.add(last);
                }  */
                peak_load.setText(max_in_loadY(datapoints_all.toArray(new DataPoint[datapoints_all.size()])).toString());
                resetAndAppend(datapoints_all.toArray(new DataPoint[datapoints_all.size()]));
            }
        }catch(Exception e){
            Toast.makeText(this, "Exception While Sorting", Toast.LENGTH_SHORT).show();
        }
    }

    public void getResetData(Double Y){
        String s ;
        int count = 0;
        load.clear(); disp.clear(); time.clear();
        try {
                s = reader.readLine();
                String[] vals = s.split(",");
                load.add(new Double(vals[0]));
                disp.add(new Double(vals[1]));
                time.add(new Double(vals[2]));

        } catch (Exception e) {
            //  Toast.makeText(this, "Exception while getting data e : " + ch.toString(), Toast.LENGTH_LONG).show();
        }
        DataPoint d ;
            for(int i = 0; i < disp.size(); i++){
            datapoints_all.add(d = new DataPoint(time.get(i),disp.get(i)));
                live_displacement.setText(new Double(d.getY()).toString());
                if(datapoints_all.size() > 1)
                    if(d.getX() < datapoints_all.get(datapoints_all.size() - 2).getX()) {
                    //adjustAddedPoint(datapoints_all.toArray(new DataPoint[datapoints_all.size()]));
                    DataPoint last = datapoints_all.get(datapoints_all.size() - 2);
                    datapoints_all.remove(datapoints_all.size() - 2);
                    datapoints_all.add(last);
                    }
            live_displacement.setText(new Double(d.getY()).toString());
            peak_displacement.setText(max_in_displacementY(datapoints_all.toArray(new DataPoint[datapoints_all.size()])).toString());
            resetAndAppend(datapoints_all.toArray(new DataPoint[datapoints_all.size()]));
        }
    }

    public void getResetData(Double X , Double Y){
        String s ;
        int count = 0;
        load.clear(); disp.clear(); time.clear();
        try {
                s = reader.readLine();
                String[] vals = s.split(",");
                load.add(new Double(vals[0]));
                disp.add(new Double(vals[1]));
                time.add(new Double(vals[2]));

        } catch (Exception e) {
            //  Toast.makeText(this, "Exception while getting data e : " + ch.toString(), Toast.LENGTH_LONG).show();
        }
          DataPoint d ;
            for(int i = 0; i < disp.size(); i++){
            datapoints_all.add(d = new DataPoint(disp.get(i),load.get(i)));
                live_load.setText(new Double(d.getY()).toString());
                live_displacement.setText(new Double(d.getY()).toString());
                if(datapoints_all.size() > 1)
                    if(d.getX() < datapoints_all.get(datapoints_all.size() - 2).getX()) {
                    //adjustAddedPoint(datapoints_all.toArray(new DataPoint[datapoints_all.size()]));
                    DataPoint last = datapoints_all.get(datapoints_all.size() - 2);
                    datapoints_all.remove(datapoints_all.size() - 2);
                    datapoints_all.add(last);
                    }
            live_displacement.setText(new Double(d.getX()).toString());
            peak_displacement.setText(max_in_displacementX(datapoints_all.toArray(new DataPoint[datapoints_all.size()])).toString());
            live_load.setText(new Double(d.getY()).toString());
            peak_load.setText(max_in_loadY(datapoints_all.toArray(new DataPoint[datapoints_all.size()])).toString());
            resetAndAppend(datapoints_all.toArray(new DataPoint[datapoints_all.size()]));
        }
    }

    public void resetAndAppend(DataPoint[] D) {
   /*     if(maxX < D[D.length - 1].getX()) {
            maxX += 10;
            graph.getViewport().setMaxX(maxX);
        }   */
        try {
            series.resetData(D);
        }catch(Exception e){
            Toast.makeText(MainActivity.this, "InValid Value Added", Toast.LENGTH_SHORT).show();
        }
    }

    public double getTime(){
        long time= System.currentTimeMillis();
        previous_time += (time - previous_time);
        previous_time /= 1000;
        return previous_time;
    }

    public void reDrawGraph() {
        series = new LineGraphSeries<DataPoint>();
        graph.addSeries(series);
    /*    for (int i = 0; i < MainActivity.datapoints.length; i++)
            series.appendData(MainActivity.datapoints[i], true, MainActivity.datapoints.length);  */
        series.resetData(datapoints_all.toArray(new DataPoint[datapoints_all.size()]));
    }

    public void clearGraph() {
        graph.removeAllSeries();
    }

    public void addNewSeries() {
        clearGraph();
        series = new LineGraphSeries<DataPoint>();
        graph.addSeries(series);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onPause() {
        super.onPause();
        handler.removeCallbacks(runnable);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (stop == false)
            handler.post(runnable2);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        datapoints_all.clear();
        handler.removeCallbacks(runnable3);
    }

    public void changeLabels(){
        String xlabel = "";
        String ylabel = "";
        switch(GraphSelection.selection){
            case 0 : ylabel = "Load (kn)"; xlabel = "Time (ms)";
                tv1.setText("Load (kn)");  tv3.setText("Load (kn)");
                tv2.setText("Time (ms)");  tv4.setText("Time (ms)");
                break;
            case 1 : ylabel = "Displacement (mm)"; xlabel = "Time (ms)";
                tv1.setText("Displacement (mm)");  tv3.setText("Displacement (mm)");
                tv2.setText("Time (ms)");  tv4.setText("Time (ms)");
                break;
            case 2 : ylabel = "Load (kn)"; xlabel = "Displacement (mm)";
                tv1.setText("Displacement (mm)");  tv3.setText("Displacement (mm)");
                tv2.setText("Load (kn)");  tv4.setText("Load (kn)");
        }
        graph.getGridLabelRenderer().setVerticalAxisTitle(ylabel);
        graph.getGridLabelRenderer().setHorizontalAxisTitle(xlabel);
        isgraphchanged = true;
        datapoints_all.clear();
        load.clear();
        disp.clear();
        time.clear();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if(id == R.id.Select_Graph){
            new GraphSelection(MainActivity.this, MainActivity.this).showDialog();
            changeLabels();
        }
        else if(id == R.id.Enter_Values){
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setPositiveButton("Add", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    switch(GraphSelection.selection){
                        case 0 :    Double d = new Double(dialog_load.getText().toString());
                            Double t = new Double(dialog_time.getText().toString());
                            DataPoint A = null;
                            datapoints_all.add(A = new DataPoint(t, d));
                            if(datapoints_all.size() > 1)
                                if(A.getX() < datapoints_all.get(datapoints_all.size() - 2).getX()) {
                                //adjustAddedPoint(datapoints_all.toArray(new DataPoint[datapoints_all.size()]));
                                DataPoint last = datapoints_all.get(datapoints_all.size() - 2);
                                datapoints_all.remove(datapoints_all.size() - 2);
                                datapoints_all.add(last);
                                }
                            resetAndAppend(datapoints_all.toArray(new DataPoint[datapoints_all.size()]));
                            break;
                        case 1 :    Double D = new Double(dialog_displacement.getText().toString());
                            Double T = new Double(dialog_time.getText().toString());
                            DataPoint B = null;
                            datapoints_all.add(B = new DataPoint(T, D));
                            if(datapoints_all.size() > 1)
                                if(B.getX() < datapoints_all.get(datapoints_all.size() - 2).getX()) {
                                //adjustAddedPoint(datapoints_all.toArray(new DataPoint[datapoints_all.size()]));
                                DataPoint last = datapoints_all.get(datapoints_all.size() - 2);
                                datapoints_all.remove(datapoints_all.size() - 2);
                                datapoints_all.add(last);
                                }
                            resetAndAppend(datapoints_all.toArray(new DataPoint[datapoints_all.size()]));
                            break;
                        case 2 :
                            Double l = new Double(dialog_load.getText().toString());
                            Double disp = new Double(dialog_displacement.getText().toString());
                            DataPoint a = null;
                            datapoints_all.add(a = new DataPoint(disp, l));
                            if(datapoints_all.size() > 1)
                                if(a.getX() < datapoints_all.get(datapoints_all.size() - 2).getX()) {
                                //adjustAddedPoint(datapoints_all.toArray(new DataPoint[datapoints_all.size()]));
                                DataPoint last = datapoints_all.get(datapoints_all.size() - 2);
                                datapoints_all.remove(datapoints_all.size() - 2);
                                datapoints_all.add(last);
                                }
                            resetAndAppend(datapoints_all.toArray(new DataPoint[datapoints_all.size()]));
                            break;
                    }
                }
            });
            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            if(GraphSelection.selection == 0){
                builder.setMessage("Enter Value For Load & Time : ");
                LinearLayout layout = new LinearLayout(MainActivity.this);
                layout.setOrientation(LinearLayout.VERTICAL);
                LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT);
                dialog_load = new EditText(MainActivity.this);
                dialog_load.setLayoutParams(param);
                TextView t = new TextView(MainActivity.this);
                t.setText("Load");
                layout.addView(t);
                layout.addView(dialog_load);
                dialog_time = new EditText(MainActivity.this);
                dialog_time.setLayoutParams(param);
                TextView T = new TextView(MainActivity.this);
                T.setText("Time");
                layout.addView(T);
                layout.addView(dialog_time);
                builder.setView(layout);
            }else if(GraphSelection.selection == 1){
                builder.setMessage("Enter Value For Displacement & Time : ");
                LinearLayout layout = new LinearLayout(MainActivity.this);
                layout.setOrientation(LinearLayout.VERTICAL);
                LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT);
                dialog_load = new EditText(MainActivity.this);
                dialog_load.setLayoutParams(param);
                TextView t = new TextView(MainActivity.this);
                t.setText("Displacement");
                layout.addView(t);
                layout.addView(dialog_load);
                dialog_time= new EditText(MainActivity.this);
                dialog_time.setLayoutParams(param);
                TextView T = new TextView(MainActivity.this);
                T.setText("Time");
                layout.addView(T);
                layout.addView(dialog_time);
                builder.setView(layout);
            }else{
                builder.setMessage("Enter Value For Load & Displacement : ");
                LinearLayout layout = new LinearLayout(MainActivity.this);
                layout.setOrientation(LinearLayout.VERTICAL);
                LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT);
                dialog_load = new EditText(MainActivity.this);
                dialog_load.setLayoutParams(param);
                TextView t = new TextView(MainActivity.this);
                t.setText("Load");
                layout.addView(t);
                layout.addView(dialog_load);
                dialog_displacement = new EditText(MainActivity.this);
                dialog_displacement.setLayoutParams(param);
                TextView T = new TextView(MainActivity.this);
                T.setText("Displacement");
                layout.addView(T);
                layout.addView(dialog_displacement);
                builder.setView(layout);
            }
            builder.create().show();
        }
        else if (id == R.id.Export) {
            new Export(MainActivity.this, MainActivity.this).showDialog();
            return true;
        } else if (id == R.id.Import) {
            cleared = false;
            graph.removeAllSeries();
            new Import(MainActivity.this, MainActivity.this).showFiles();
            return true;
        } else if (id == R.id.Clear) {
            if(stop == true){
                graph.removeAllSeries();
                live_load.setText("0"); peak_load.setText("0");
                live_displacement.setText("0"); peak_displacement.setText("0");
                datapoints_all.clear();
                cleared = true;
            }else {
                Toast.makeText(MainActivity.this, "Stop Receiving Data First", Toast.LENGTH_LONG).show();
            }
        }
        return super.onOptionsItemSelected(item);
    }

    public void adjustAddedPoint(DataPoint[] D){
        DataPoint temp = null;
        for(int i=0; i<D.length; i++)
            for(int j=i; j<D.length - 1; j++)
                if(D[i].getX() > D[j+1].getX()){
                     temp = D[i];
                     D[i] = D[j+1];
                     D[j+1] = temp;
                }
    }

    public Double max_in_loadY(DataPoint[] D){
        for(int i = 0; i < D.length; i++){
            if(max_load < D[i].getY())
                max_load = D[i].getY();
        }
        return max_load;
    }

    public Double max_in_displacementY(DataPoint[] D){
        for(int i = 0; i < D.length; i++){
            if(max_displacement < D[i].getY())
                max_displacement = D[i].getY();
        }
        return max_displacement;
    }

    public Double max_in_displacementX(DataPoint[] D){
        for(int i = 0; i < D.length; i++){
            if(max_displacement < D[i].getX())
                max_displacement = D[i].getX();
        }
        return max_displacement;
    }

    public Double max_in_loadX(DataPoint[] D){
        for(int i = 0; i < D.length; i++){
            if(max_load < D[i].getX())
                max_load = D[i].getX();
        }
        return max_load;
    }

    public void takeScreenshot(){
        View view = graph;
        Bitmap bitmap = Bitmap.createBitmap(view.getWidth(),
                view.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);
        OutputStream fout = null;
        File imageFile = new File(Environment.getExternalStorageDirectory() + "/pic.jpeg");

        try {
            fout = new FileOutputStream(imageFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, fout);
            fout.flush();
            fout.close();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            Toast.makeText(this, "Exception ScreenShot : " + e.toString(), Toast.LENGTH_LONG).show();
        }
    }
}
