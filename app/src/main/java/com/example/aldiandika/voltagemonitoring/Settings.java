package com.example.aldiandika.voltagemonitoring;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;

public class Settings extends AppCompatActivity {

    RadioGroup radio_sensor;
    RadioButton radio_sensorSatu, radio_sensorDua;

    Button btn_lihatData, btn_simpan;

    SeekBar seek_vr, seek_vs, seek_vt;
    EditText edit_vr, edit_vs, edit_vt;
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

        radio_sensor = (RadioGroup)findViewById(R.id.radio_sensor);
        radio_sensorSatu = (RadioButton)findViewById(R.id.radio_sensorSatu);
        radio_sensorDua = (RadioButton)findViewById(R.id.radio_sensorDua);


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
}
