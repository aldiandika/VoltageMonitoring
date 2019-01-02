package com.example.aldiandika.voltagemonitoring.util;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.Calendar;
import java.util.Date;

public class DatabaseHelper extends SQLiteOpenHelper{

    private static final String DATABASE_NAME = "monitoringsqlite.db";
    private static final int DATABASE_VERSION = 1;
    private static final String TABLE_NAME = "tablemonitor";
    private static final String COL_1 = "ID";
    private static final String COL_2 = "VOLTAGE_1";
    private static final String COL_3 = "VOLTAGE_2";
    private static final String COL_4 = "VOLTAGE_3";
    private static final String COL_5 = "VOLTAGE_4";
    private static final String COL_6 = "ARUS";
    private static final String COL_7 = "DAYA";
    private static final String COL_8 = "CREATED_AT";


    public DatabaseHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("create table " + TABLE_NAME +
                "(ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                "VOLTAGE_1 TEXT," +
                "VOLTAGE_2 TEXT," +
                "VOLTAGE_3 TEXT," +
                "VOLTAGE_4 TEXT," +
                "ARUS TEXT," +
                "DAYA TEXT," +
                "CREATED_AT TEXT)");

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS "+TABLE_NAME);
        onCreate(sqLiteDatabase);
    }

    public boolean insertData(String VOLTAGE_1,String VOLTAGE_2,String VOLTAGE_3,
                              String VOLTAGE_4,String ARUS,String DAYA, String TGL) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_2,VOLTAGE_1);
        contentValues.put(COL_3,VOLTAGE_2);
        contentValues.put(COL_4,VOLTAGE_3);
        contentValues.put(COL_5,VOLTAGE_4);
        contentValues.put(COL_6,ARUS);
        contentValues.put(COL_7,DAYA);
        contentValues.put(COL_8,TGL);
        long result = db.insert(TABLE_NAME,null ,contentValues);
        if(result == -1)
            return false;
        else
            return true;
    }

    public Cursor getAllData() {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select * from "+TABLE_NAME,null);
        return res;
    }

    public Cursor getDataAt(String dateAt) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select * from "+TABLE_NAME+
                        " where CREATED_AT = ?",
                        new String[]{dateAt});
        return res;
    }


}
