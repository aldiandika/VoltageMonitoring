package com.example.aldiandika.voltagemonitoring;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Typeface;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbManager;
import android.os.AsyncTask;
import android.os.BatteryManager;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.aldiandika.voltagemonitoring.server.Server;
import com.example.aldiandika.voltagemonitoring.util.DatabaseHelper;
import com.example.aldiandika.voltagemonitoring.util.DatabaseSettingHelper;
import com.example.aldiandika.voltagemonitoring.util.JSONParser;
import com.felhr.usbserial.UsbSerialDevice;
import com.felhr.usbserial.UsbSerialInterface;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class dataTransfer extends AppCompatActivity{

    Button btn_start, btn_stop, btn_clear, btn_database;
    ImageButton ibtn_setting;
    ImageView img_konSukses, img_konGagal;
    TextView txt_receiveSerial, txt_receiveServer, txt_v1, txt_v2, txt_v3, txt_v4,
             txt_I, txt_daya, txt_lenData, txt_battery;

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
    public String status_SerialMikro;
    public String data = null;

    public String TAG_SERIAL = "";
    int moveSerial;

//    JSONParser jsonParser = new JSONParser();
//    JSONObject json;
    String url_create = Server.serverURL + "store";

    int success;
    String cekJson;

//    int cekCrash = 0, last_cekCrash = 0, buff_cekCrash = 0;

    public static final String TAG_SUCCESS = "success";
    public static final String TAG_V_SATU = "voltage_satu"; //Vr
    public static final String TAG_V_DUA = "voltage_dua"; //Vs
    public static final String TAG_V_TIGA = "voltage_tiga"; //Vt
    public static final String TAG_V_EMPAT = "voltage_empat"; //arus 1
    public static final String TAG_I = "arus"; //arus 2
    public static final String TAG_P = "daya"; //daya

    float f_Vr, f_Vs, f_Vt, f_I1, f_I2, f_daya, last_Vt, last_I1;

    public static int status_kirimDB; //0 = "Gagal database", 1 = "Gagal Koneksi", 2 = "Sukses"

    public static boolean FLAG_DATA_COMPLETE = false;
    public static boolean FLAG_ARUS_1 = true;
//    static boolean FLAG_CRASH = false;

    private MyTimerTask.StoreData myasyncTask;
    private MyTimerTask.StoreSQlite sqliteAsyncTask;

    int pjgData, deviceStatus;

//    String currentBatteryStatus="";
    float percentage;
    String[] splitedInput;

    //SQlite variables
    Date tgl;
    String formattedDate;
//    boolean inserted;
    DatabaseHelper dbSqlite;
    DatabaseSettingHelper dbSetting;

    private int param_vr, param_vs, param_vt;
    private String sensorArus;

    private String stringDebug;


//    int count;//for debug
//    int countCrash;
//    Thread crashThread;

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
                Toast.makeText(dataTransfer.this,"error serial",Toast.LENGTH_SHORT).show();
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
                            Toast.makeText(dataTransfer.this, "Serial open", Toast.LENGTH_SHORT).show();
//                            setUiEnabled(true);
                            serialPort.setBaudRate(38400);
                            serialPort.setDataBits(UsbSerialInterface.DATA_BITS_8);
                            serialPort.setStopBits(UsbSerialInterface.STOP_BITS_1);
                            serialPort.setParity(UsbSerialInterface.PARITY_NONE);
                            serialPort.setFlowControl(UsbSerialInterface.FLOW_CONTROL_OFF);
                            serialPort.read(callback);
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
//                onClickStart(btn_start);
            }else if(intent.getAction().equals(UsbManager.ACTION_USB_ACCESSORY_DETACHED)){
//                onClickStop(btn_stop);
            }


        }
    };

    //Battery information
    private BroadcastReceiver mBroadccastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            deviceStatus = intent.getIntExtra(BatteryManager.EXTRA_STATUS,-1);
            int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
            int scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
            int batteryLevel=(int)(((float)level / (float)scale) * 100.0f);

            txt_battery.setText("" +batteryLevel+ " %");
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_transfer);

        //Initiallization
        btn_start = (Button)findViewById(R.id.btn_start);
        btn_stop = (Button)findViewById(R.id.btn_stop);
        ibtn_setting = (ImageButton)findViewById(R.id.ibtn_setting);

        Typeface typeface = Typeface.createFromAsset(getAssets(),"fonts/segoeui.ttf");
        txt_receiveSerial = (TextView)findViewById(R.id.txt_receiveSerial);
        txt_v1 = (TextView)findViewById(R.id.txt_v1);
        txt_v2 = (TextView)findViewById(R.id.txt_v2);
        txt_v3 = (TextView)findViewById(R.id.txt_v3);
        txt_v4 = (TextView)findViewById(R.id.txt_v4);
        txt_daya = (TextView)findViewById(R.id.txt_daya);
        txt_battery = (TextView)findViewById(R.id.txt_battery);

        txt_v1.setTypeface(typeface);
        txt_v2.setTypeface(typeface);
        txt_v3.setTypeface(typeface);
        txt_v4.setTypeface(typeface);
        txt_daya.setTypeface(typeface);

        img_konGagal = (ImageView)findViewById(R.id.img_konGagal);
        img_konSukses = (ImageView)findViewById(R.id.img_konSukses);
        img_konSukses.setVisibility(View.GONE);
        img_konGagal.setVisibility(View.GONE);
        //============================================================================

        //Debug
//        btn_database = (Button)findViewById(R.id.btn_database);
//        txt_I = (TextView)findViewById(R.id.txt_I);
//        txt_lenData = (TextView)findViewById(R.id.txt_lenData);

        setUiEnabled(false);

        usbManager = (UsbManager) getSystemService(this.USB_SERVICE);
        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_USB_PERMISSION);
        filter.addAction(UsbManager.ACTION_USB_DEVICE_ATTACHED);
        filter.addAction(UsbManager.ACTION_USB_DEVICE_DETACHED);
        registerReceiver(broadcastReceiver, filter);

        IntentFilter mFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        registerReceiver(mBroadccastReceiver,mFilter);

        tgl = Calendar.getInstance().getTime();

        SimpleDateFormat df = new SimpleDateFormat("yyyy/M/d");
        formattedDate = df.format(tgl);

//        Toast.makeText(dataTransfer.this, formattedDate, Toast.LENGTH_SHORT).show();

        //For debug
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

        //getdata parama from sqlite
        dbSetting = new DatabaseSettingHelper(this);
        Cursor cursor = dbSetting.getAllData();

        if(cursor.getCount() == 0){
            FLAG_ARUS_1 = true;
            param_vr = 0;
            param_vs = 0;
            param_vt = 0;
        }else{
            while(cursor.moveToNext()){
                sensorArus = cursor.getString(1);
                param_vr = cursor.getInt(2);
                param_vs = cursor.getInt(3);
                param_vt = cursor.getInt(4);
            }

            if(sensorArus.equalsIgnoreCase("1")){
                FLAG_ARUS_1 = true;
            }else{
                FLAG_ARUS_1 = false;
            }
        }

        stringDebug = String.valueOf(FLAG_ARUS_1) + " , " +
                String.valueOf(param_vr) + " , " +
                String.valueOf(param_vs) + " , " +
                String.valueOf(param_vt);

//        Toast.makeText(this,stringDebug,Toast.LENGTH_SHORT).show();
//    ======================================================================================



        MyTimerTask myTask = new MyTimerTask();
        Timer myTimer = new Timer();

        myTimer.schedule(myTask,1000, 5000);


        //        cekCrashThread();
    }

    /*
    public void toDatabase(View view){
        Intent intent = new Intent(this,ShowData.class);
        startActivity(intent);
    }
    */

    public void setUiEnabled(boolean bool) {
        btn_start.setEnabled(!bool);
        btn_stop.setEnabled(bool);
    }

    public void onClickStart(View view){
        TAG_SERIAL = "$10#";
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
                    parsingSerial(ftext);
                    ftxtView.setText(ftext);

//                    Toast.makeText(dataTransfer.this,"cek= "+cekCrash+" last_cek= "+last_cekCrash,Toast.LENGTH_SHORT).show();
//                    txt_daya.setText(String.valueOf(moveSerial));

                }
                catch (NullPointerException e){
                    e.printStackTrace();
                }
            }
        });
    }



    private void parsingSerial(String input){
        splitedInput = input.split(" ");
        pjgData = splitedInput.length;


//        txt_lenData.setText(String.valueOf(pjgData));
        if(pjgData == 6){
            if((splitedInput[0].replaceAll("[0-9]+","")).equalsIgnoreCase("a")){
                value_VSatu = splitedInput[0].replaceAll("[a-z]+","");
                //convert to real data
                f_Vr = (((float)Integer.parseInt(value_VSatu))/100) + param_vr;
                value_VSatu = String.format("%.2f",f_Vr);
                txt_v1.setText(value_VSatu); //Vr
            }

            if((splitedInput[1].replaceAll("[0-9]+","")).equalsIgnoreCase("b")){
                value_VDua = splitedInput[1].replaceAll("[a-z]+","");
                //convert to real data
                f_Vs = (((float)Integer.parseInt(value_VDua))/100) + param_vs;
                value_VDua = String.format("%.2f",f_Vs);
                txt_v2.setText(value_VDua); //Vs
            }

            if((splitedInput[2].replaceAll("[0-9]+","")).equalsIgnoreCase("c")){
                value_VTiga = splitedInput[2].replaceAll("[a-z]+","");
                //convert to real data
                f_Vt = (((float)Integer.parseInt(value_VTiga))/100) + param_vt;
                value_VTiga = String.format("%.2f",f_Vt);
                txt_v3.setText(value_VTiga); //Vt
            }

            if((splitedInput[3].replaceAll("[0-9]+","")).equalsIgnoreCase("d")){
                value_VEmpat = splitedInput[3].replaceAll("[a-z]+","");
                //convert to real data
                f_I1 = ((float)Integer.parseInt(value_VEmpat))/100;
                value_VEmpat = String.format("%.2f",f_I1);

                if(FLAG_ARUS_1){
                    f_daya = f_I1*f_Vt;
                    value_P = String.format("%.2f",f_daya);
                    txt_v4.setText(value_VEmpat); //I
                    txt_daya.setText(value_P);
                }

            }

            if((splitedInput[4].replaceAll("[0-9]+","")).equalsIgnoreCase("e")){
                value_I = splitedInput[4].replaceAll("[a-z]+",""); //I2
                //convert to real data
                f_I2 = ((float)Integer.parseInt(value_I))/100;
                value_I = String.format("%.2f",f_I2);

                if(!FLAG_ARUS_1){
                    f_daya = f_I2*f_Vt;
                    value_P = String.format("%.2f",f_daya);
                    txt_v4.setText(value_I); //I2
                    txt_daya.setText(value_P);
                }
            }

            if((splitedInput[5].replaceAll("[0-9]+","")).equalsIgnoreCase("f")){
                status_SerialMikro = splitedInput[5].replaceAll("[a-z]+",""); //status
            }

//=====================================================================


//            cekCrash++;
//            txt_daya.setText(String.valueOf(cekCrash));
//            Toast.makeText(dataTransfer.this, ""+buff_cekCrash, Toast.LENGTH_SHORT).show();

//============================================================
            FLAG_DATA_COMPLETE = true;
            pjgData = 0;
            splitedInput = null;
        }else{
            FLAG_DATA_COMPLETE = false;
        }

    }

    public void toSetting(View view){
        Intent intent = new Intent(this,Settings.class);
        startActivity(intent);
    }

    @Override
    protected void onPause() {
        super.onPause();

        unregisterReceiver(broadcastReceiver);
        unregisterReceiver(mBroadccastReceiver);
    }

    @Override
    protected void onResume() {
        super.onResume();
        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_USB_PERMISSION);
        filter.addAction(UsbManager.ACTION_USB_DEVICE_ATTACHED);
        filter.addAction(UsbManager.ACTION_USB_DEVICE_DETACHED);
        registerReceiver(broadcastReceiver, filter);

        IntentFilter mFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        registerReceiver(mBroadccastReceiver,mFilter);

        dbSetting = new DatabaseSettingHelper(this);
        Cursor cursor = dbSetting.getAllData();

        if(cursor.getCount() == 0){
            FLAG_ARUS_1 = true;
            param_vr = 0;
            param_vs = 0;
            param_vt = 0;
        }else{
            while(cursor.moveToNext()){
                sensorArus = cursor.getString(1);
                param_vr = cursor.getInt(2);
                param_vs = cursor.getInt(3);
                param_vt = cursor.getInt(4);
            }

            if(sensorArus.equalsIgnoreCase("1")){
                FLAG_ARUS_1 = true;
            }else{
                FLAG_ARUS_1 = false;
            }
        }

        stringDebug = String.valueOf(FLAG_ARUS_1) + " , " +
                String.valueOf(param_vr) + " , " +
                String.valueOf(param_vs) + " , " +
                String.valueOf(param_vt);

//        Toast.makeText(this,stringDebug,Toast.LENGTH_SHORT).show();
    }

    private void hideSystemUI() {
        // Enables regular immersive mode.
        // For "lean back" mode, remove SYSTEM_UI_FLAG_IMMERSIVE.
        // Or for "sticky immersive," replace it with SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE
                        // Set the content to appear under the system bars so that the
                        // content doesn't resize when the system bars hide and show.
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        // Hide the nav bar and status bar
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            hideSystemUI();
        }
    }

    class MyTimerTask extends TimerTask{

        final class StoreData extends AsyncTask<String, String, String>{
            @Override
            protected String doInBackground(String... strings) {
                JSONParser jsonParser = new JSONParser();
                JSONObject json;
                List<NameValuePair> params = new ArrayList<>();

                //For debug only
//                value_VSatu = "1";
//                value_VDua = "2";
//                value_VTiga = "3";
//                value_VEmpat = "4";
//                value_I = "5";
//                value_P = "6";
                if(!value_VSatu.isEmpty() && !value_VDua.isEmpty() && !value_VTiga.isEmpty()
                        && !value_VEmpat.isEmpty() && !value_I.isEmpty() && !value_P.isEmpty()){

                    params.add(new BasicNameValuePair(TAG_V_SATU,value_VSatu));
                    params.add(new BasicNameValuePair(TAG_V_DUA,value_VDua));
                    params.add(new BasicNameValuePair(TAG_V_TIGA,value_VTiga));
                    params.add(new BasicNameValuePair(TAG_V_EMPAT,value_VEmpat));
                    params.add(new BasicNameValuePair(TAG_I,value_I));
                    params.add(new BasicNameValuePair(TAG_P,value_P));

                    json = jsonParser.makeHttpRequest(url_create, "GET", params);

                    try{
                        success = 0;
                        success = json.getInt(TAG_SUCCESS);

                        if(success == 1){
                            return "sukses";
                        }else{
                            return "gagal koneksi";
                        }

                    }catch (JSONException e){
                        e.printStackTrace();
                        cekJson = "0";
                        return "gagal koneksi";
                    }
                    catch (NullPointerException e){
                        e.printStackTrace();
                        cekJson = "0";
                        return "gagal koneksi";
                    }
                }

                return "gagal database";
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                if(s.equalsIgnoreCase("gagal database")){
                    status_kirimDB = 0;

                    img_konSukses.setVisibility(View.GONE);
                    img_konGagal.setVisibility(View.VISIBLE);

                }else if(s.equalsIgnoreCase("gagal koneksi")){
                    status_kirimDB = 1;

                    img_konSukses.setVisibility(View.GONE);
                    img_konGagal.setVisibility(View.VISIBLE);

                }else if(s.equalsIgnoreCase("sukses")){
                    status_kirimDB = 2;
                    FLAG_DATA_COMPLETE = false;
                    success = 0;

                    img_konSukses.setVisibility(View.VISIBLE);
                    img_konGagal.setVisibility(View.GONE);
                }else{
                    img_konSukses.setVisibility(View.GONE);
                    img_konGagal.setVisibility(View.VISIBLE);
                }

//                Toast.makeText(dataTransfer.this, cekJson, Toast.LENGTH_SHORT).show();

            }
        }

        final class StoreSQlite extends AsyncTask<String, String, String>{

            Date dateLocal;
            String formattedDateLocal;
            DatabaseHelper dbSqlite;


            @Override
            protected void onPreExecute() {
                super.onPreExecute();

                dbSqlite = new DatabaseHelper(dataTransfer.this);
                dateLocal = Calendar.getInstance().getTime();

                SimpleDateFormat formatter = new SimpleDateFormat("yyyy/M/d");
                formattedDateLocal = formatter.format(dateLocal);
            }

            @Override
            protected String doInBackground(String... strings) {

                //For debug only
//                value_VSatu = String.valueOf(count);
//                value_VDua = "2";
//                value_VTiga = "3";
//                value_VEmpat = "4";
//                value_I = "5";
//                value_P = "6";

                if(!value_VSatu.isEmpty() && !value_VDua.isEmpty() && !value_VTiga.isEmpty()
                        && !value_VEmpat.isEmpty() && !value_I.isEmpty() && !value_P.isEmpty() &&
                        !formattedDateLocal.isEmpty()) {

                     boolean inserted = dbSqlite.insertData(value_VSatu,value_VDua,value_VTiga,value_VEmpat,
                            value_I,value_P,formattedDateLocal);

                    //For debug
//                    boolean inserted = dbSqlite.insertData("1",
//                            "1",
//                            "1",
//                            "1",
//                            "1","1",
//                            "1");

                    if(!inserted){
                        return "SQlite Gagal";
                    }else{
                        return "SQlite Sukses";
                    }
                }
                return "SQlite Sukses";
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
//                Toast.makeText(dataTransfer.this,s,Toast.LENGTH_LONG).show();
            }
        }
        @Override
        public void run() {

//            buff_cekCrash = cekCrash;
//            if(buff_cekCrash == last_cekCrash){
//                countCrash++;
//                if(countCrash >= 2){
//                    countCrash = 0;
//                    cekCrash = 0;
//
//                    Intent intent = new Intent(dataTransfer.this,dataTransfer.class);
//                    startActivity(intent);
//                }
//            }

            //for bug
            if(FLAG_DATA_COMPLETE){
                myasyncTask =  new StoreData();
                myasyncTask.execute();

//                count++;
                sqliteAsyncTask = new StoreSQlite();
                sqliteAsyncTask.execute();

                TAG_SERIAL = "$1#";
                serialPort.write(TAG_SERIAL.getBytes());

            }else{
                try{
                    TAG_SERIAL = "$1#";
                    serialPort.write(TAG_SERIAL.getBytes());
                }catch (NullPointerException e){
                    e.printStackTrace();
                }

            }

        }
    }

}

 /*
    private void cekCrashThread(){
        crashThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while(!FLAG_CRASH){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if((cekCrash == last_cekCrash) && (cekCrash != 0 && last_cekCrash != 0)){
                                if(countCrash == Integer.MAX_VALUE){
                                    countCrash = 0;
                                }else{
                                    countCrash++;
                                }

                                if(countCrash > 5){
                                    Toast.makeText(dataTransfer.this,"Serial error",Toast.LENGTH_SHORT).show();
                                    unregisterReceiver(broadcastReceiver);
                                    FLAG_CRASH = true;
                                }
                            }
                            last_cekCrash = cekCrash;

                            txt_v1.setText(String.valueOf(last_cekCrash));
                            txt_v2.setText(String.valueOf(countCrash));
                        }
                    });

                    try {
                        Thread.sleep(1000);//5
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
//                serialPort.close();
            }
        });
        crashThread.start();
    }
    */
