package com.example.aldiandika.voltagemonitoring;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.aldiandika.voltagemonitoring.adapter.ListAdapter;
import com.example.aldiandika.voltagemonitoring.data.monitor;
import com.example.aldiandika.voltagemonitoring.util.DatabaseHelper;

import java.util.ArrayList;
import java.util.Calendar;

public class ShowData extends AppCompatActivity {
    DatabaseHelper mydb;
    monitor tempDat;

    private ListView mainList;
    private TextView txt_setTgl;
    private Button btn_show;

    //Date Init
    private String date;
    private int set_year, set_month, set_day;
    private Calendar kalender;
    private String currentDate;
    private DatePickerDialog.OnDateSetListener mDateSetListener;

    private ArrayList<monitor> list_data = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_data);
        setTitle("Tampil Data");

        mainList = (ListView)findViewById(R.id.mainList);
        txt_setTgl = (TextView)findViewById(R.id.txt_setTgl);
        btn_show = (Button)findViewById(R.id.btn_show);

        kalender = Calendar.getInstance();
        set_year = kalender.get(Calendar.YEAR);
        set_month = kalender.get(Calendar.MONTH) + 1;
        set_day = kalender.get(Calendar.DAY_OF_MONTH);

        txt_setTgl.setText(set_year + "/" + set_month + "/" +set_day);

        //listener set tanggal
        txt_setTgl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                kalender = Calendar.getInstance();
                set_year = kalender.get(Calendar.YEAR);
                set_month = kalender.get(Calendar.MONTH);
                set_day = kalender.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(
                        ShowData.this,
                        android.R.style.Theme_Holo_Dialog_MinWidth,
                        mDateSetListener,
                        set_year,set_month,set_day
                );
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
                dialog.show();
                dialog.getWindow().getDecorView().setSystemUiVisibility(
                        ShowData.this.getWindow().getDecorView().getSystemUiVisibility());
                dialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
            }
        });

        mDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                month = month + 1;

                date = year + "/" + month + "/" + day;
                txt_setTgl.setText(date);
            }
        };

        currentDate = txt_setTgl.getText().toString();
        mydb = new DatabaseHelper(this);
        Cursor res = mydb.getDataAt(currentDate);
//        Cursor res = mydb.getAllData();
        if(res.getCount() == 0){
            Toast.makeText(this,"No Data",Toast.LENGTH_LONG).show();
            return;
        }

        while(res.moveToNext()){
            tempDat = new monitor();
            tempDat.setId(res.getString(0));
            tempDat.setV_satu(res.getString(1));
            tempDat.setV_dua(res.getString(2));
            tempDat.setV_tiga(res.getString(3));
            tempDat.setV_empat(res.getString(4));
            tempDat.setArus(res.getString(5));
            tempDat.setDaya(res.getString(6));
            tempDat.setTgl(res.getString(7));
            list_data.add(tempDat);
        }

        mainList.setAdapter(new ListAdapter(ShowData.this,list_data));

    }

    public void showDataAt(View view){
        ArrayList<monitor> list_data = new ArrayList<>();
        mainList.setAdapter(new ListAdapter(ShowData.this,list_data));

        currentDate = txt_setTgl.getText().toString();
        mydb = new DatabaseHelper(this);
        Cursor res = mydb.getDataAt(currentDate);
        if(res.getCount() == 0){
            Toast.makeText(this,"No Data",Toast.LENGTH_LONG).show();
            return;
        }

        while(res.moveToNext()){
            tempDat = new monitor();
            tempDat.setId(res.getString(0));
            tempDat.setV_satu(res.getString(1));
            tempDat.setV_dua(res.getString(2));
            tempDat.setV_tiga(res.getString(3));
            tempDat.setV_empat(res.getString(4));
            tempDat.setArus(res.getString(5));
            tempDat.setDaya(res.getString(6));
            tempDat.setTgl(res.getString(7));
            list_data.add(tempDat);
        }

        mainList.setAdapter(new ListAdapter(ShowData.this,list_data));

    }

    public void kembali(View view){
        finish();
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