package com.example.myapplication.bluetooth;


import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.media.MediaScannerConnection;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.method.ScrollingMovementMethod;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.myapplication.R;
import com.example.myapplication.activity.DiaryActivity;
import com.example.myapplication.bluetooth.Constants;
import com.example.myapplication.bluetooth.SerialListener;
import com.example.myapplication.bluetooth.SerialService;
import com.example.myapplication.bluetooth.SerialSocket;
import com.example.myapplication.utils.UserData;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;

public class TerminalFragment extends Fragment implements ServiceConnection, SerialListener {

    private enum Connected { False, Pending, True }

    private String deviceAddress;
    private SerialService service;

    private TextView receiveText;
    private Button sendText;

    private Connected connected = Connected.False;
    private boolean initialStart = true;
    private boolean hexEnabled = false;
    private boolean pendingNewline = false;
    private String newline = TextUtil.newline_crlf;
    private boolean autoScroll = true;

    private int calibrating_status;

    /*프로그래스바 사용*/
    private TextView mTextView;
    private ProgressBar mProgressBar;
    private Handler handler;


    class psensor_cali_data
    {
        public byte flag;
        public int inear_value;
        public int outear_value;
    }


    /*
     * Lifecycle
     */
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
//        setRetainInstance(true);
        deviceAddress = getArguments().getString("device");

        handler = new Handler(Looper.getMainLooper()){
            @Override
            public void handleMessage(@NonNull Message msg) {
                if(mProgressBar.getProgress() == 100){
                    sendText.setEnabled(true);
                }

            }
        };

    }

    @Override
    public void onDestroy() {
        if (connected != Connected.False)
            disconnect();
        getActivity().stopService(new Intent(getActivity(), SerialService.class));
        super.onDestroy();
    }

    @Override
    public void onStart() {
        super.onStart();
        if(service != null){
            service.attach(this);
        }

        else{
            getActivity().startService(new Intent(getActivity(), SerialService.class)); // prevents service destroy on unbind from recreated activity caused by orientation change
        }

    }

    @Override
    public void onStop() {
        if(service != null && !getActivity().isChangingConfigurations())
            service.detach();
        super.onStop();
    }

    @SuppressWarnings("deprecation") // onAttach(context) was added with API 23. onAttach(activity) works for all API versions
    @Override
    public void onAttach(@NonNull Activity activity) {
        super.onAttach(activity);
        service = new SerialService();
        getActivity().bindService(new Intent(getActivity(), SerialService.class), this, Context.BIND_AUTO_CREATE);
    }

    @Override
    public void onDetach() {
        try { getActivity().unbindService(this); } catch(Exception ignored) {}
        super.onDetach();
    }

    @Override
    public void onResume() {
        super.onResume();
        if(initialStart && service != null) {
            initialStart = false;
            getActivity().runOnUiThread(this::connect);
        }
    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder binder) {
        service = ((SerialService.SerialBinder) binder).getService();
        service.attach(this);
        if(initialStart && isResumed()) {
            initialStart = false;
            getActivity().runOnUiThread(this::connect);
        }
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        service = null;
    }

    /*
     * UI
     */
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_terminal, container, false);
        receiveText = view.findViewById(R.id.receive_text);                          // TextView performance decreases with number of spans
        receiveText.setTextColor(ContextCompat.getColor(getActivity(),R.color.colorRecieveText)); // set as default color to reduce number of spans
        receiveText.setMovementMethod(ScrollingMovementMethod.getInstance());
        receiveText.setEnabled(false);

        sendText = view.findViewById(R.id.send_text);
        sendText.setOnClickListener(v -> send("a"));

        mTextView = (TextView) view.findViewById(R.id.loading_percent);
        mProgressBar = view.findViewById(R.id.progressBar_loadc);

        new DownloadTask().execute();    //내부 클래스 실행

        return view;
    }

//    @Override
//    public void onCreateOptionsMenu(@NonNull Menu menu, MenuInflater inflater) {
//        inflater.inflate(R.menu.menu_terminal, menu);
//        menu.findItem(R.id.hex).setChecked(hexEnabled);
//        menu.findItem(R.id.autoscroll).setChecked(autoScroll);
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        int id = item.getItemId();
//        if (id == R.id.clear) {
//            receiveText.setText("");
//            return true;
//        } else if (id == R.id.newline) {
//            String[] newlineNames = getResources().getStringArray(R.array.newline_names);
//            String[] newlineValues = getResources().getStringArray(R.array.newline_values);
//            int pos = java.util.Arrays.asList(newlineValues).indexOf(newline);
//            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
//            builder.setTitle("Newline");
//            builder.setSingleChoiceItems(newlineNames, pos, (dialog, item1) -> {
//                newline = newlineValues[item1];
//                dialog.dismiss();
//            });
//            builder.create().show();
//            return true;
//        } else if (id == R.id.hex) {
//            hexEnabled = !hexEnabled;
//            sendText.setText("");
//            sendText.setHint(hexEnabled ? "HEX mode" : "");
//            item.setChecked(hexEnabled);
//            return true;
//        }
//        else if (id == R.id.autoscroll)
//        {
//            autoScroll = !autoScroll;
//            item.setChecked(autoScroll);
//
//            if (autoScroll) {
//                receiveText.setGravity(Gravity.BOTTOM);
//            } else
//            {
//                receiveText.setGravity(Gravity.NO_GRAVITY);
//            }
//
//            return true;
//        }
//        else {
//            return super.onOptionsItemSelected(item);
//        }
//    }

    /*
     * Serial + UI
     */
    private void connect() {
        try {
            BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            BluetoothDevice device = bluetoothAdapter.getRemoteDevice(deviceAddress);
//            status("connecting...");
            connected = Connected.Pending;
            SerialSocket socket = new SerialSocket(getActivity().getApplicationContext(), device);
            service.connect(socket);
        } catch (Exception e) {
            onSerialConnectError(e);
        }
    }

    private void disconnect() {
        connected = Connected.False;
        service.disconnect();
    }

    private void send(String str) {
        if(connected != Connected.True) {
            Toast.makeText(getActivity(), "not connected", Toast.LENGTH_SHORT).show();
            return;
        }
        try {
            String msg;
            byte[] data;
            if(hexEnabled) {
                StringBuilder sb = new StringBuilder();
                TextUtil.toHexString(sb, TextUtil.fromHexString(str));
                TextUtil.toHexString(sb, newline.getBytes());
                msg = sb.toString();
                data = TextUtil.fromHexString(msg);
            } else {
                msg = str;
                data = (str + newline).getBytes();
            }
//            SpannableStringBuilder spn = new SpannableStringBuilder(msg + '\n');
//            spn.setSpan(new ForegroundColorSpan(ContextCompat.getColor(getActivity(),R.color.colorSendText)), 0, spn.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//            receiveText.append(spn);
//            receiveText.setEnabled(false);
            service.write(data);
            receiveText.setText(null);
        } catch (Exception e) {
            onSerialIoError(e);
        }
    }

    private void sendBinaryData(byte[] cmd) {
        if(connected != Connected.True) {
            Toast.makeText(getActivity(), "not connected", Toast.LENGTH_SHORT).show();
            return;
        }
        try {
            String msg;
            byte[] data;
            StringBuilder sb = new StringBuilder();
            TextUtil.toHexString(sb, cmd);
            msg = sb.toString();

            SpannableStringBuilder spn = new SpannableStringBuilder(msg + '\n');
            spn.setSpan(new ForegroundColorSpan(ContextCompat.getColor(getActivity(),R.color.colorSendText)), 0, spn.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            receiveText.append(spn);
            service.write(cmd);
        } catch (Exception e) {
            onSerialIoError(e);
        }
    }

    private void receiveBinary(byte[] data) {
        int len;
        int raceid;
        boolean error = true;

        if ((data[0] == Constants.PROTOCOL_FRAME_START) && (data[1] == Constants.PROTOCOL_FRAME_RSP)) {
            receiveText.append(TextUtil.toHexString(data) + '\n');
            Toast.makeText(getActivity(),receiveText.getText().toString()+ "2",Toast.LENGTH_SHORT).show();

            len = data[3] & 0xff;
            len <<= 8;
            len |= data[2] & 0xff;

            if (len + 4 == data.length)
            {
                raceid = data[5] & 0xff;
                raceid <<= 8;
                raceid |= data[4] & 0xff;

                if (raceid == Constants.CUSTOMER_RACE_CMD_ID)
                {
                    int cmd = data[Constants.EVENT_INDEX];
                    int index = Constants.PARAM_INDEX;
                    int result = data[index];
                    index += 1;
                    error = false;
                    switch (cmd)
                    {
                        case Constants.PSENSOR_CLEAN_PRODUCT_MODE: {
                            if (result == 1) {
                                Toast.makeText(service, "clean product mode success", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(service, "clean product mode failed.", Toast.LENGTH_SHORT).show();
                            }
                            break;
                        }

                        case Constants.PSENSOR_SET_PRODUCT_MODE: {
                            if (result == 1) {
                                Toast.makeText(service, "set product mode success", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(service, "set product mode failed.", Toast.LENGTH_SHORT).show();
                            }
                            break;
                        }

                        case Constants.PSENSOR_CHECK_PRODUCT_MODE: {
                            boolean customer_ui =  ((result >> Constants.CUSTOMER_UI_INDEX) & 0x1) == 1;
                            boolean product_mode = ((result >> Constants.CUSTOMER_PRODUCT_INDEX) & 0x1) == 1;

                            if (!customer_ui && product_mode) {
                                Toast.makeText(service, "product mode", Toast.LENGTH_SHORT).show();
                            } else if (customer_ui && !product_mode) {
                                Toast.makeText(service, "user mode", Toast.LENGTH_SHORT).show();
                            }
                            else
                            {
                                Toast.makeText(service, "earphone error", Toast.LENGTH_SHORT).show();
                            }
                            break;
                        }
                        case Constants.PSENSOR_GET_CALI_DATA: {
                            psensor_cali_data left_value;
                            psensor_cali_data right_value;
                            if (result == 1)
                            {
                                byte[] cali_byte = new byte[Constants.CALIDATA_LEN];
                                StringBuffer prompt = new StringBuffer();

                                System.arraycopy(data, index , cali_byte, 0, Constants.CALIDATA_LEN);
                                left_value = processCaliData(cali_byte);
                                if (left_value == null)
                                {
                                    Toast.makeText(service, "left calidata format error.", Toast.LENGTH_SHORT).show();
                                }
                                else
                                {
                                    if (left_value.flag == Constants.NOT_EXIST)
                                    {
                                        prompt.append("left earphone not exist!");
                                    }
                                    else if (left_value.flag == Constants.CALIBRATED)
                                    {
                                        prompt.append("left earphone calibrated!");
                                        prompt.append("in ear value:");
                                        prompt.append(TextUtil.shortToHex(left_value.inear_value));
                                        prompt.append(",out ear value:");
                                        prompt.append(TextUtil.shortToHex(left_value.outear_value));
                                    }
                                    else if (left_value.flag == Constants.NOT_CALIBRATED)
                                    {
                                        prompt.append("left earphone not calibrated.");
                                    }
                                }
                                index += Constants.CALIDATA_LEN;
                                System.arraycopy(data, index , cali_byte, 0, Constants.CALIDATA_LEN);

                                right_value = processCaliData(cali_byte);
                                if (right_value == null)
                                {
                                    Toast.makeText(service, "right calidata format error.", Toast.LENGTH_SHORT).show();
                                }
                                else
                                {
                                    if (right_value.flag == Constants.NOT_EXIST)
                                    {
                                        prompt.append("right earphone not exist!");
                                    }
                                    else if (right_value.flag == Constants.CALIBRATED)
                                    {
                                        prompt.append(", right earphone calibrated!");
                                        prompt.append("in ear value:");
                                        prompt.append(TextUtil.shortToHex(right_value.inear_value));
                                        prompt.append(",out ear value:");
                                        prompt.append(TextUtil.shortToHex(right_value.outear_value));
                                    }
                                    else if (right_value.flag == Constants.NOT_CALIBRATED)
                                    {
                                        prompt.append("right earphone not calibrated.");
                                    }
                                }

                                AlertDialog dialog = new AlertDialog.Builder(getActivity())
                                        .setTitle("cali data")
                                        .setMessage(prompt)
                                        .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                // Toast.makeText(getActivity(), "Cancel", Toast.LENGTH_SHORT).show();
                                                dialog.dismiss();
                                            }
                                        })
                                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                //  Toast.makeText(getActivity(), "OK", Toast.LENGTH_SHORT).show();
                                                dialog.dismiss();
                                            }
                                        }).create();
                                dialog.show();
                            }
                            else
                            {
                                Toast.makeText(service, "get calidata failed.", Toast.LENGTH_LONG).show();
                            }

                            break;
                        }
                        case Constants.PSENSOR_GET_RAW_DATA: {
                            if (result == 1) {
                                int raw_data;

                                raw_data = data[index + 1] & 0xff;
                                raw_data <<= 8;
                                raw_data |= data[index] & 0xff;

                                String info = new String("raw data is");
                                info += raw_data;

                                Toast.makeText(service, info, Toast.LENGTH_SHORT).show();
                            }
                            else
                            {
                                Toast.makeText(service, "get raw data failed.", Toast.LENGTH_LONG).show();
                            }

                            break;
                        }

                        case Constants.PSENSOR_GET_INEAR_STATUS: {
                            if (result == 1) {
                                Toast.makeText(service, "in ear", Toast.LENGTH_SHORT).show();
                            }
                            else
                            {
                                Toast.makeText(service, "out ear", Toast.LENGTH_LONG).show();
                            }

                            break;
                        }

                        case Constants.PSENSOR_RACE_SPP_LOG_ON: {
                            if (result == 1) {
                                Toast.makeText(service, "log on success", Toast.LENGTH_SHORT).show();
                            }
                            else
                            {
                                Toast.makeText(service, "log on failed.", Toast.LENGTH_LONG).show();
                            }

                            break;
                        }

                        case Constants.PSENSOR_RACE_SPP_LOG_OFF: {
                            if (result == 1) {
                                Toast.makeText(service, "log off success", Toast.LENGTH_SHORT).show();
                            }
                            else
                            {
                                Toast.makeText(service, "log off failed.", Toast.LENGTH_LONG).show();
                            }
                            break;
                        }

                        case Constants.PSENSOR_RACE_CAL_CT: {
                            if (result == 1) {
                                //  Toast.makeText(service, "out ear calibrated ok", Toast.LENGTH_SHORT).show();

                                calibrating_status = Constants.PSENSOR_RACE_CAL_CT;

                                Toast.makeText(service, "out ear calibrate cmd ok, polling calibrated status", Toast.LENGTH_SHORT).show();
                                new Handler().postDelayed(new Runnable() {
                                    public void run() {
                                        byte[] cmd = consCommandByte(Constants.PSENSOR_QUERY_CALI_STATUS);
                                        sendBinaryData(cmd);
                                    }
                                }, 1000);
                            }
                            else
                            {
                                Toast.makeText(service, "out ear calibrated failed.", Toast.LENGTH_LONG).show();
                                calibrating_status = 0;
                            }

                            break;
                        }

                        case Constants.PSENSOR_RACE_CAL_G2: {
                            if (result == 1) {
                                Toast.makeText(service, "in ear calibrate cmd ok, polling calibrated status", Toast.LENGTH_SHORT).show();
                                calibrating_status = Constants.PSENSOR_RACE_CAL_G2;
                                new Handler().postDelayed(new Runnable() {
                                    public void run() {
                                        byte[] cmd = consCommandByte(Constants.PSENSOR_QUERY_CALI_STATUS);
                                        sendBinaryData(cmd);
                                    }
                                }, 1000);
                            }
                            else
                            {
                                Toast.makeText(service, "in ear calibrated failed.", Toast.LENGTH_LONG).show();
                                calibrating_status = 0;
                            }
                            break;
                        }

                        case Constants.PSENSOR_QUERY_CALI_STATUS: {
                            String strAction;
                            if (calibrating_status == Constants.PSENSOR_RACE_CAL_G2)
                            {
                                strAction = "in ear ";
                            }
                            else if (calibrating_status == Constants.PSENSOR_RACE_CAL_CT)
                            {
                                strAction = "out ear ";
                            }
                            else {
                                strAction = "";
                            }

                            if (result == Constants.PSENSOR_QUERY_CALI_FAIL) {
                                Toast.makeText(service,  strAction + "calibrated value failed.", Toast.LENGTH_LONG).show();
                                calibrating_status = 0;
                            }
                            else if (result == Constants.PSENSOR_QUERY_CALI_SUCCESS) {
                                Toast.makeText(service,  strAction + "calibrated value success.", Toast.LENGTH_LONG).show();
                                calibrating_status = 0;
                            } else {
                                new Handler().postDelayed(new Runnable() {
                                    public void run() {
                                        byte[] cmd = consCommandByte(Constants.PSENSOR_QUERY_CALI_STATUS);
                                        sendBinaryData(cmd);
                                    }
                                }, 1000);
                            }

                            break;
                        }

                        case Constants.PSENSOR_ANC_HIGH:
                        case Constants.PSENSOR_ANC_LOW:
                        case Constants.PSENSOR_ANC_WIND:
                        case Constants.PSENSOR_CHANNEL_LEFT:
                        case Constants.PSENSOR_CHANNEL_RIGHT: {
                            final String[] ok_array =
                                    {
                                            "anc high success"  ,
                                            "anc low success",
                                            "anc wind success",
                                            "switch left channel success",
                                            "switch right channel success"
                                    };
                            final String[] nok_array =
                                    {
                                            "anc high failed"  ,
                                            "anc low failed",
                                            "anc wind failed",
                                            "switch left channel failed",
                                            "switch right channel failed"
                                    };

                            int idx = cmd - Constants.PSENSOR_ANC_HIGH;

                            if (result == 1) {
                                Toast.makeText(service, ok_array[idx], Toast.LENGTH_SHORT).show();
                            }
                            else {
                                Toast.makeText(service,  nok_array[idx], Toast.LENGTH_SHORT).show();
                            }

                            break;
                        }

                        default: {
                            throw new IllegalStateException("Unexpected value: " + cmd);
                        }
                    }
                }
            }
        }
        else
        {
            if (TextUtil.isAllAscii(data)) {
                error = false;
                String str = new String(data);
//                receiveText.append(str);
                Toast.makeText(getActivity(),str,Toast.LENGTH_SHORT).show();
//                UserData.write("food_g",str);
            }
        }

        if (error)
        {
            Toast.makeText(service, "error", Toast.LENGTH_SHORT).show();
        }
    }


    private void receive(byte[] data) {
        if(hexEnabled) {
            receiveText.append(TextUtil.toHexString(data) + '\n');
            Toast.makeText(getActivity(),"hexenabled1",Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getActivity(),"hexenabled3",Toast.LENGTH_SHORT).show();
            String msg = new String(data);
            if(newline.equals(TextUtil.newline_crlf) && msg.length() > 0) {
                // don't show CR as ^M if directly before LF
                msg = msg.replace(TextUtil.newline_crlf, TextUtil.newline_lf);
                // special handling if CR and LF come in separate fragments
                if (pendingNewline && msg.charAt(0) == '\n') {
                    Editable edt = receiveText.getEditableText();
                    if (edt != null && edt.length() > 1)
                        edt.replace(edt.length() - 2, edt.length(), "");
                }
                pendingNewline = msg.charAt(msg.length() - 1) == '\r';
            }
            receiveText.append(TextUtil.toCaretString(msg, newline.length() != 0));
            Toast.makeText(getActivity(),"hexenabled2",Toast.LENGTH_SHORT).show();
        }
    }

    private void status(String str) {
        SpannableStringBuilder spn = new SpannableStringBuilder(str + '\n');
//        spn.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.colorStatusText)), 0, spn.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        receiveText.append(spn);
    }

    /*
     * SerialListener
     */
    @Override
    public void onSerialConnect() {
//        status("connected");
        Toast.makeText(getActivity(),"connected",Toast.LENGTH_SHORT).show();
        connected = Connected.True;
    }

    @Override
    public void onSerialConnectError(Exception e) {
//        status("connection failed: " + e.getMessage());
        disconnect();
    }

    @Override
    public void onSerialRead(byte[] data) {
        receiveBinary(data);
    }

    @Override
    public void onSerialIoError(Exception e) {
//        status("connection lost: " + e.getMessage());
        disconnect();
    }

    byte[] consCommandByte(byte cmd) {
        byte[] cmdArray = new byte[] { 0x05, 0x5A, 0x05, 0x00, 0x00, 0x20, 0x00, 0x0B, 0x13};

        cmdArray[Constants.EVENT_INDEX] = cmd;
        cmdArray[Constants.PARAM_INDEX] = cmd;

        return cmdArray;
    }

    psensor_cali_data  processCaliData(byte[] rsp)
    {
        psensor_cali_data data;

        if (rsp[0] == Constants.LEFT_CHANNEL || rsp[0] == Constants.RIGHT_CHANNEL)
        {
            data = new psensor_cali_data();
            data.flag = rsp[1];

            data.outear_value = rsp[3] & 0xff;
            data.outear_value <<= 8;
            data.outear_value |= rsp[2] & 0xff;

            data.inear_value = rsp[5] & 0xff;
            data.inear_value <<= 8;
            data.inear_value |= rsp[4] & 0xff;

            return data;
        }

        return null;
    }

        /*내부 클래스*/
    class DownloadTask extends AsyncTask<Void, Integer, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            for (int i = 0; i <= 100; i++) {
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
            mTextView.setText(values[0] + "%");
            mProgressBar.setProgress(values[0]);
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            sendText.setEnabled(true);
        }
    }

}

