package com.example.myapplication.activity;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;

import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.R;
import com.example.myapplication.bluetooth.ConnectedThread;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;


public class MainActivity_loadc extends AppCompatActivity {

    /*프로그래스바 사용*/
    private TextView mTextView;
    private ProgressBar mProgressBar;

    //블루투스
    private static final String TAG = "MainActivity_loadc";
    UUID BT_MODULE_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"); // "random" unique identifier

    TextView textStatus;
    Button btnParied, btnSearch, btnSend;
    ListView listView;

    BluetoothAdapter btAdapter;
    Set<BluetoothDevice> pairedDevices;
    ArrayAdapter<String> btArrayAdapter;
    ArrayList<String> deviceAddressArray;

    BluetoothSocket btSocket = null;
    ConnectedThread connectedThread;

    private ActivityResultLauncher<Intent> bluetoothActivityLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_loadc);

        mTextView = (TextView) findViewById(R.id.loading_percent);
        mProgressBar = findViewById(R.id.progressBar_loadc);

        new DownloadTask().execute();    //내부 클래스 실행

        //블루투스
        // Get permission
        String[] permission_list = {
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
        };
        ActivityCompat.requestPermissions(MainActivity_loadc.this, permission_list, 1);

        // Enable bluetooth
        btAdapter = BluetoothAdapter.getDefaultAdapter();
        if (!btAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            bluetoothActivityLauncher.launch(enableBtIntent);
        }

        // variables
//        textStatus = (TextView) findViewById(R.id.text_status);
//        btnParied = (Button) findViewById(R.id.btn_paired);
//        btnSearch = (Button) findViewById(R.id.btn_search);
//        btnSend = (Button) findViewById(R.id.btn_send);
//        listView = (ListView) findViewById(R.id.listview);

        listView.setOnItemClickListener(new myOnItemClickListener());

        bluetoothActivityLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                this::onbluetoothResult
        );
    }

    private void onbluetoothResult(ActivityResult result) {

        if (result.getResultCode() == Activity.RESULT_OK) {


        }
    }

    //페어링된 장치 검색
    public void PairedDevices(){
        btArrayAdapter.clear();
        if(deviceAddressArray!=null && !deviceAddressArray.isEmpty()){ deviceAddressArray.clear(); }
        pairedDevices = btAdapter.getBondedDevices();
        if (pairedDevices.size() > 0) {
            // There are paired devices. Get the name and address of each paired device.
            for (BluetoothDevice device : pairedDevices) {
                String deviceName = device.getName();
                String deviceHardwareAddress = device.getAddress(); // MAC address
                btArrayAdapter.add(deviceName);
                deviceAddressArray.add(deviceHardwareAddress);
            }
        }
    }

    //주변 장치 검색(에러남)
    public void SearchDevices(){
        // Check if the device is already discovering
        Log.e(TAG,"실행1");
        if(btAdapter.isDiscovering()){
            btAdapter.cancelDiscovery();
            Log.e(TAG,"실행2");
        } else {
            if (btAdapter.isEnabled()) {
                btAdapter.startDiscovery();
                btArrayAdapter.clear();
                Log.e(TAG,"실행3");
                if (deviceAddressArray != null && !deviceAddressArray.isEmpty()) {
                    deviceAddressArray.clear();
                }
                IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
                registerReceiver(receiver, filter);
                Log.e(TAG,"실행4");
            } else {
                Toast.makeText(getApplicationContext(), "bluetooth not on", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // Create a BroadcastReceiver for ACTION_FOUND.
    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                // Discovery has found a device. Get the BluetoothDevice
                // object and its info from the Intent.
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                String deviceName = device.getName();
                String deviceHardwareAddress = device.getAddress(); // MAC address
                btArrayAdapter.add(deviceName);
                deviceAddressArray.add(deviceHardwareAddress);
                btArrayAdapter.notifyDataSetChanged();
            }
        }
    };

    @Override
    protected void onDestroy() {
        // Don't forget to unregister the ACTION_FOUND receiver.
        unregisterReceiver(receiver);
        super.onDestroy();
    }

    public class myOnItemClickListener implements AdapterView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Toast.makeText(getApplicationContext(), btArrayAdapter.getItem(position), Toast.LENGTH_SHORT).show();

            textStatus.setText("try...");

            final String name = btArrayAdapter.getItem(position); // get name
            final String address = deviceAddressArray.get(position); // get address
            boolean flag = true;

            BluetoothDevice device = btAdapter.getRemoteDevice(address);

            try {
                btSocket = createBluetoothSocket(device);
            } catch (IOException e) {
                flag = false;
                textStatus.setText("socket creation failed!");
                e.printStackTrace();
            }

            try {
                btSocket.connect();
            } catch (IOException e) {
                // Close the socket
                try {
                    btSocket.close();
                } catch (IOException e2) {
                    Log.e(TAG, "unable to close() socket during connection failure", e2);
                }
            }

            connectedThread = new ConnectedThread(btSocket);
            textStatus.setText("connected to "+name);
            connectedThread.start();
        }
    }

    private BluetoothSocket createBluetoothSocket(BluetoothDevice device) throws IOException {
        try {
            final Method m = device.getClass().getMethod("createInsecureRfcommSocketToServiceRecord", UUID.class);
            return (BluetoothSocket) m.invoke(device, BT_MODULE_UUID);
        } catch (Exception e) {
            Log.e(TAG, "Could not create Insecure RFComm Connection",e);
        }
        return  device.createRfcommSocketToServiceRecord(BT_MODULE_UUID);
    }


    /*내부 클래스*/
    class DownloadTask extends AsyncTask<Void, Integer, Void>{

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            for (int i = 0; i <= 100; i++){
                try {
                    Thread.sleep(100);                  //0.1초 간격으로 sleep
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                final int percent = i;
                publishProgress(percent);

            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            mTextView.setText(values[0] +"%");
            mProgressBar.setProgress(values[0]);
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
        }
    }

}


