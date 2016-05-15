package com.tpl.arpitgoyal.arduino3;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Vector;

public class HomeScreen extends Activity {

    private static final int REQUEST_ENABLE_BT = 1;
    static BluetoothDevice mmDevice;
    ListView listDevicesFound;
    TextView btnScanDevice;
    TextView stateBluetooth;
    BluetoothAdapter bluetoothAdapter;
    Vector<BluetoothDevice> devices = new Vector<>();
    BluetoothSocket mmSocket;
    Context context;

    ArrayAdapter<String> btArrayAdapter;
    public ListView.OnItemClickListener listOnClickListener =
            new ListView.OnItemClickListener(){
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    for(BluetoothDevice dev : devices){
                        if(dev.getName().equals(btArrayAdapter.getItem(position))) {
                            mmDevice = dev;
                            startActivity(new Intent(HomeScreen.this, MainActivity.class));
                        }
                    }
                }
            };
    private final BroadcastReceiver ActionFoundReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            // TODO Auto-generated method stub
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                devices.add(device);
                btArrayAdapter.add(device.getName());
                btArrayAdapter.notifyDataSetChanged();
                Toast.makeText(context, "New Bluetooth Device Found", Toast.LENGTH_LONG).show();
            }
        }
    };
    private Button.OnClickListener btnScanDeviceOnClickListener
            = new Button.OnClickListener() {

        @Override
        public void onClick(View arg0) {
            // TODO Auto-generated method stub
            if (!bluetoothAdapter.isEnabled()) {
                Toast.makeText(HomeScreen.this, "Bluetooth Is Off", Toast.LENGTH_SHORT).show();
                return;
            }
            btArrayAdapter.clear();
            Toast.makeText(context, "Searching For Bluetooth Devices", Toast.LENGTH_LONG).show();
            bluetoothAdapter.startDiscovery();
        }
    };

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);

        btnScanDevice = (TextView) findViewById(R.id.textView2);

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        context = HomeScreen.this;

        if (!bluetoothAdapter.isEnabled()) {
            Intent enableBluetooth = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBluetooth, 0);
        }

        listDevicesFound = (ListView) findViewById(R.id.listView);
        btArrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1);
        listDevicesFound.setAdapter(btArrayAdapter);
        listDevicesFound.setOnItemClickListener(listOnClickListener);

//        CheckBlueToothState();

        btnScanDevice.setOnClickListener(btnScanDeviceOnClickListener);

        registerReceiver(ActionFoundReceiver,
                new IntentFilter(BluetoothDevice.ACTION_FOUND));
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        unregisterReceiver(ActionFoundReceiver);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        if(requestCode == REQUEST_ENABLE_BT){}
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_home_screen, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.Import) {
          //  startActivity(new Intent(HomeScreen.this, ImportActivity.class));
            new Import(HomeScreen.this, HomeScreen.this).showFiles();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
