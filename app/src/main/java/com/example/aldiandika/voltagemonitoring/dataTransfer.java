package com.example.aldiandika.voltagemonitoring;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.aldiandika.voltagemonitoring.util.Create;
import com.felhr.usbserial.UsbSerialDevice;
import com.felhr.usbserial.UsbSerialInterface;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import static android.support.v4.os.LocaleListCompat.create;

public class dataTransfer extends AppCompatActivity {

    Button btn_start, btn_stop, btn_clear;
    TextView txt_receiveSerial, txt_receiveServer, txt_v1, txt_v2, txt_v3, txt_v4,
             txt_I, txt_daya, txt_lenData;

    //Serial variabel initialization
    public final String ACTION_USB_PERMISSION = "com.example.aldiandika.voltagemonitoring.USB_PERMISSION";
    UsbDevice device;
    UsbDeviceConnection usbConnection;
    UsbManager usbManager;
    UsbSerialDevice serialPort;

    public String value_VSatu;
    public String value_VDua;
    public String value_VTiga;
    public String value_VEmpat;
    public String value_I;
    public String value_P;

    public String data = null;

    public String TAG_SERIAL = "";


    //Receive serial data
    UsbSerialInterface.UsbReadCallback callback = new UsbSerialInterface.UsbReadCallback() {
        @Override
        public void onReceivedData(byte[] bytes) {

            try {
                data = new String(bytes, "UTF-8");

//                data.concat("\n"); // for debugging purpose only
                appendText2(txt_receiveSerial,data);

            }catch (UnsupportedEncodingException e){
                e.printStackTrace();
            }
        }
    };

    //Serial configuration
    private final BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getAction().equals(ACTION_USB_PERMISSION)){
                boolean granted = intent.getExtras().getBoolean(UsbManager.EXTRA_PERMISSION_GRANTED);
                if(granted){
                    usbConnection = usbManager.openDevice(device);
                    serialPort = UsbSerialDevice.createUsbSerialDevice(device, usbConnection);
                    if(serialPort != null){
                        if(serialPort.open()){

//                            setUiEnabled(true);
                            serialPort.setBaudRate(9600);
                            serialPort.setDataBits(UsbSerialInterface.DATA_BITS_8);
                            serialPort.setStopBits(UsbSerialInterface.STOP_BITS_1);
                            serialPort.setParity(UsbSerialInterface.PARITY_NONE);
                            serialPort.setFlowControl(UsbSerialInterface.FLOW_CONTROL_OFF);
                            serialPort.read(callback);
                            appendText(txt_receiveSerial,"Serial Port Opened");
                        }else {
                            Log.d("serial","PORT NOT OPEN");
                        }
                    }else{
                        Log.d("serial","Port is NULL");
                    }
                }else{
                    Log.d("serial","NOT GRANTED!!!");
                }
            }else if(intent.getAction().equals(UsbManager.ACTION_USB_DEVICE_ATTACHED)){
                onClickStart(btn_start);
            }else if(intent.getAction().equals(UsbManager.ACTION_USB_ACCESSORY_DETACHED)){
                onClickStop(btn_stop);
            }

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_transfer);

        btn_start = (Button)findViewById(R.id.btn_start);
        btn_stop = (Button)findViewById(R.id.btn_stop);
        txt_receiveSerial = (TextView)findViewById(R.id.txt_receiveSerial);
        txt_v1 = (TextView)findViewById(R.id.txt_v1);
        txt_v2 = (TextView)findViewById(R.id.txt_v2);
        txt_v3 = (TextView)findViewById(R.id.txt_v3);
        txt_v4 = (TextView)findViewById(R.id.txt_v4);
        txt_I = (TextView)findViewById(R.id.txt_I);
        txt_daya = (TextView)findViewById(R.id.txt_daya);
        txt_lenData = (TextView)findViewById(R.id.txt_lenData);


        setUiEnabled(false);

        usbManager = (UsbManager) getSystemService(this.USB_SERVICE);
        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_USB_PERMISSION);
        filter.addAction(UsbManager.ACTION_USB_DEVICE_ATTACHED);
        filter.addAction(UsbManager.ACTION_USB_DEVICE_DETACHED);
        registerReceiver(broadcastReceiver, filter);

        HashMap<String, UsbDevice> usbDevices = usbManager.getDeviceList();
        if (!usbDevices.isEmpty()) {
            boolean keep = true;
            for (Map.Entry<String, UsbDevice> entry : usbDevices.entrySet()) {
                device = entry.getValue();
                int deviceVID = device.getVendorId();
                if (deviceVID == 0x1A86)// 0x2341 Arduino Vendor ID
                {
                    PendingIntent pi = PendingIntent.getBroadcast(this, 0, new Intent(ACTION_USB_PERMISSION), 0);
                    usbManager.requestPermission(device, pi);
                    keep = false;
                } else {
                    usbConnection= null;
                    device = null;
                }

                if (!keep)
                    break;
            }
        }

    }

    public void setUiEnabled(boolean bool) {
        btn_start.setEnabled(!bool);
        btn_stop.setEnabled(bool);
    }

    public void onClickStart(View view){
//        HashMap<String, UsbDevice> usbDevices = usbManager.getDeviceList();
//        if (!usbDevices.isEmpty()) {
//            boolean keep = true;
//            for (Map.Entry<String, UsbDevice> entry : usbDevices.entrySet()) {
//                device = entry.getValue();
//                int deviceVID = device.getVendorId();
//                if (deviceVID == 0x1A86)// 0x2341 Arduino Vendor ID
//                {
//                    PendingIntent pi = PendingIntent.getBroadcast(this, 0, new Intent(ACTION_USB_PERMISSION), 0);
//                    usbManager.requestPermission(device, pi);
//                    keep = false;
//
////                    TAG_SERIAL = "1";
////                    serialPort.write(TAG_SERIAL.getBytes());
//                    Toast.makeText(dataTransfer.this,"cek", Toast.LENGTH_LONG).show();
//                } else {
//                    usbConnection= null;
//                    device = null;
//                }
//
//                if (!keep)
//                    break;
//            }
//        }

        TAG_SERIAL = "1";
        serialPort.write(TAG_SERIAL.getBytes());
    }

    public void onClickStop(View view){
        serialPort.close();
        setUiEnabled(false);
        appendText(txt_receiveSerial,"\nSerial Connection Closed! \n");
    }

    public void onClickClear(View view){
        try {
            txt_receiveSerial.setText("");
        }catch(NullPointerException e){
            e.printStackTrace();
        }

    }

    private void appendText(TextView txtView, CharSequence text){
        final TextView ftxtView = txtView;
        final CharSequence ftext = text;

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try{
                    ftxtView.append(ftext);
                }catch (NullPointerException e){
                    e.printStackTrace();
                }
            }
        });
    }

    private void appendText2(TextView txtView, String text){ //for debug
        final TextView ftxtView = txtView;
        final String ftext = text;

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try{
                    if(ftext.contains("on")){
                        ftxtView.setText(ftext);
                        TAG_SERIAL = "1";
                        serialPort.write(TAG_SERIAL.getBytes());
                    }else{
                        ftxtView.setText(ftext);
                        TAG_SERIAL = "0";
                        serialPort.write(TAG_SERIAL.getBytes());

                        parsingSerial(ftext);
                    }
                }catch (NullPointerException e){
                    e.printStackTrace();
                }
            }
        });
    }

    private void parsingSerial(String input){
        String[] splitedInput = input.split(" ");
        int pjgData = splitedInput.length;

        txt_lenData.setText(String.valueOf(pjgData));

        if(pjgData == 6){
            if((splitedInput[0].replaceAll("[0-9]+","")).equalsIgnoreCase("a")){
                value_VSatu = splitedInput[0].replaceAll("[a-z]+","");
                txt_v1.setText(value_VSatu);
            }

            if((splitedInput[1].replaceAll("[0-9]+","")).equalsIgnoreCase("b")){
                value_VDua = splitedInput[1].replaceAll("[a-z]+","");
                txt_v2.setText(value_VDua);
            }

            if((splitedInput[2].replaceAll("[0-9]+","")).equalsIgnoreCase("c")){
                value_VTiga = splitedInput[2].replaceAll("[a-z]+","");
                txt_v3.setText(value_VTiga);
            }

            if((splitedInput[3].replaceAll("[0-9]+","")).equalsIgnoreCase("x")){
                value_VEmpat = splitedInput[3].replaceAll("[a-z]+","");
                txt_v4.setText(value_VEmpat);
            }

            if((splitedInput[4].replaceAll("[0-9]+","")).equalsIgnoreCase("y")){
                value_I = splitedInput[4].replaceAll("[a-z]+","");
                txt_I.setText(value_I);
            }

            if((splitedInput[5].replaceAll("[0-9]+","")).equalsIgnoreCase("z")){
                value_P = splitedInput[5].replaceAll("[a-z]+","");
                txt_daya.setText(value_P);
            }
        }
    }



    protected void intoVar(){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    txt_v1.setText(value_VSatu);
                    txt_v2.setText(value_VDua);
                    txt_v3.setText(value_VTiga);
                    txt_v4.setText(value_VEmpat);
                    txt_I.setText(value_I);
                    txt_daya.setText(value_P);

                    TAG_SERIAL = "1";
                    serialPort.write(TAG_SERIAL.getBytes());

                }catch(NullPointerException e){
                    e.printStackTrace();
                }
            }
        });
    }
}
