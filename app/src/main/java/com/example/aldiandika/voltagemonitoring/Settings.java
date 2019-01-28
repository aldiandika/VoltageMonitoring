package com.example.aldiandika.voltagemonitoring;

import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.Toast;

import com.example.aldiandika.voltagemonitoring.data.DataSetting;
import com.example.aldiandika.voltagemonitoring.util.DatabaseHelper;
import com.example.aldiandika.voltagemonitoring.util.DatabaseSettingHelper;

public class Settings extends AppCompatActivity {

    DatabaseSettingHelper settingDb;
    RadioGroup radio_sensor;
    RadioButton radio_sensorSatu, radio_sensorDua;

    Button btn_lihatData, btn_simpan;
    ImageButton btn_back;

    SeekBar seek_vr, seek_vs, seek_vt;
    EditText edit_vr, edit_vs, edit_vt;

    DataSetting dataSetting;

    int selectedSensor, valueVr, valueVs, valueVt,
    seekbarValR, seekbarValS, seekbarValT;

    int editValR,editValS,editValT;

    String editSVr,editSVs,editSVt;

//    public static String FLAG_SENSOR;
//    public static int CONSTVR, CONSTVS, CONSTVT;

    private String stringDebug;

    boolean FLAG_SEEK;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        seek_vr = (SeekBar)findViewById(R.id.seek_vr);
        seek_vs = (SeekBar)findViewById(R.id.seek_vs);
        seek_vt = (SeekBar)findViewById(R.id.seek_vt);

        edit_vr = (EditText)findViewById(R.id.edit_vr);
        edit_vs = (EditText)findViewById(R.id.edit_vs);
        edit_vt = (EditText)findViewById(R.id.edit_vt);

        btn_lihatData = (Button)findViewById(R.id.btn_lihatdata);
        btn_simpan = (Button)findViewById(R.id.btn_simpan);

        btn_back = (ImageButton)findViewById(R.id.btn_back);

        radio_sensor = (RadioGroup)findViewById(R.id.radio_sensor);
        radio_sensorSatu = (RadioButton)findViewById(R.id.radio_sensorSatu);
        radio_sensorDua = (RadioButton)findViewById(R.id.radio_sensorDua);

        settingDb = new DatabaseSettingHelper(this);
        Cursor cursor = settingDb.getAllData();

        dataSetting = new DataSetting();

        if(cursor.getCount() == 0){
            dataSetting.setFlagSensor("1");
            dataSetting.setConst_vr(0);
            dataSetting.setConst_vs(0);
            dataSetting.setConst_vt(0);

            selectedSensor = Integer.parseInt(dataSetting.getFlagSensor());
            valueVr = dataSetting.getConst_vr();
            valueVs = dataSetting.getConst_vs();
            valueVt = dataSetting.getConst_vt();

            boolean dataInserted = settingDb.insertData(dataSetting.getFlagSensor(),dataSetting.getConst_vr(),
                    dataSetting.getConst_vs(),dataSetting.getConst_vt());

            if(dataInserted){
//                Toast.makeText(this,"Data Inserted", Toast.LENGTH_SHORT).show();
                stringDebug = "Data Inserted";
            }else{
                stringDebug = "Insert Failed";
//                Toast.makeText(this,"Insert Failed",Toast.LENGTH_SHORT).show();
            }

        }else{
            cursor = settingDb.getDataAtId("1");
//            stringDebug = String.valueOf(cursor.getCount());
            while(cursor.moveToNext()){
                dataSetting.setFlagSensor(cursor.getString(1));
                dataSetting.setConst_vr(cursor.getInt(2));
                dataSetting.setConst_vs(cursor.getInt(3));
                dataSetting.setConst_vt(cursor.getInt(4));
            }

            selectedSensor = Integer.parseInt(dataSetting.getFlagSensor());
            valueVr = dataSetting.getConst_vr();
            valueVs = dataSetting.getConst_vs();
            valueVt = dataSetting.getConst_vt();

            stringDebug = dataSetting.getFlagSensor() + " , " +
                    String.valueOf(dataSetting.getConst_vr()) + " , " +
                    String.valueOf(dataSetting.getConst_vs()) + " , " +
                    String.valueOf(dataSetting.getConst_vt());
        }


//        radio button
        if(selectedSensor == 1){
            radio_sensorSatu.setChecked(true);
            radio_sensorDua.setChecked(false);
        }else{
            radio_sensorDua.setChecked(true);
            radio_sensorSatu.setChecked(false);
        }

        radio_sensor.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int index) {
                if(index == R.id.radio_sensorSatu){
                    selectedSensor = 1;
                }else{
                    selectedSensor = 2;
                }

            }
        });
//    ==================================================================================

//        seekbar

        edit_vr.setText(String.valueOf(valueVr));
        edit_vs.setText(String.valueOf(valueVs));
        edit_vt.setText(String.valueOf(valueVt));

        edit_vr.setSelection(edit_vr.getText().length());
        edit_vs.setSelection(edit_vs.getText().length());
        edit_vt.setSelection(edit_vt.getText().length());

        seekbarValR = 50 + Integer.parseInt(String.valueOf(edit_vr.getText()));
        seekbarValS = 50 + Integer.parseInt(String.valueOf(edit_vs.getText()));;
        seekbarValT = 50 + Integer.parseInt(String.valueOf(edit_vt.getText()));;

        seek_vr.setProgress(seekbarValR);
        seek_vs.setProgress(seekbarValS);
        seek_vt.setProgress(seekbarValT);

        seek_vr.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int val, boolean b) {
                edit_vr.setText(String.valueOf(val - 50));
                edit_vr.setSelection(edit_vr.getText().length());

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        seek_vs.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int val, boolean b) {
                edit_vs.setText(String.valueOf(val - 50));
                edit_vs.setSelection(edit_vs.getText().length());
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        seek_vt.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int val, boolean b) {
                edit_vt.setText(String.valueOf(val - 50));
                edit_vt.setSelection(edit_vt.getText().length());
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

//    ==================================================================================
        //edit text

        edit_vr.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                try{
                    editSVr = edit_vr.getText().toString();
                    editValR = Integer.parseInt(editSVr);
                    seek_vr.setProgress(editValR+50);
                    edit_vr.setSelection(edit_vr.getText().length());
//                    Toast.makeText(Settings.this,""+editValR,Toast.LENGTH_SHORT).show();
                }catch (NumberFormatException e){
                    e.printStackTrace();
                }
            }
        });

        edit_vs.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                try{
                    editSVs = edit_vs.getText().toString();
                    editValS = Integer.parseInt(editSVs);
                    seek_vs.setProgress(editValS+50);
                    edit_vs.setSelection(edit_vs.getText().length());
//                    Toast.makeText(Settings.this,""+editValR,Toast.LENGTH_SHORT).show();
                }catch (NumberFormatException e){
                    e.printStackTrace();
                }
            }
        });

        edit_vt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                try{
                    editSVt = edit_vt.getText().toString();
                    editValT = Integer.parseInt(editSVt);
                    seek_vt.setProgress(editValT+50);
                    edit_vt.setSelection(edit_vt.getText().length());
//                    Toast.makeText(Settings.this,""+editValR,Toast.LENGTH_SHORT).show();
                }catch (NumberFormatException e){
                    e.printStackTrace();
                }
            }
        });


    }

    public void saveData(View View){

        try {
            valueVr = Integer.parseInt(edit_vr.getText().toString());
            valueVs = Integer.parseInt(edit_vs.getText().toString());
            valueVt = Integer.parseInt(edit_vt.getText().toString());
        }catch (NullPointerException e){
            e.printStackTrace();
            Toast.makeText(this,"Parameter Voltage Tidak Boleh Kosong",Toast.LENGTH_SHORT).show();
        }catch (NumberFormatException e){
            e.printStackTrace();
            Toast.makeText(this,"Parameter Voltage Tidak Boleh Kosong",Toast.LENGTH_SHORT).show();
        }

        stringDebug = String.valueOf(selectedSensor) + " , " +
                String.valueOf(valueVr) + " , " +
                String.valueOf(valueVs) + " , " +
                String.valueOf(valueVt);

        dataSetting.setFlagSensor(String.valueOf(selectedSensor));
        dataSetting.setConst_vr(valueVr);
        dataSetting.setConst_vs(valueVs);
        dataSetting.setConst_vt(valueVt);

        settingDb.updateData("1",dataSetting.getFlagSensor(),dataSetting.getConst_vr(),
                dataSetting.getConst_vs(),dataSetting.getConst_vt());
        Toast.makeText(this, "Data Tersimpan", Toast.LENGTH_SHORT).show();
        finish();
    }

    public void lihatData(View view){
        Intent intent = new Intent(this,ShowData.class);
        startActivity(intent);
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

    public void toBack(View view){
        finish();
    }
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            hideSystemUI();
        }
    }
}
