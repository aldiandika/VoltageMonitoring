package com.example.aldiandika.voltagemonitoring.util;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import com.example.aldiandika.voltagemonitoring.MainActivity;

public class DatabaseSettingHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "datasetting.db";
    private static final int DATABASE_VERSION = 1;
    private static final String TABLE_NAME = "tablesetting";
    private static final String COL_1 = "ID";
    private static final String COL_2 = "FLAG_SENSOR";
    private static final String COL_3 = "CONST_VR";
    private static final String COL_4 = "CONST_VS";
    private static final String COL_5 = "CONST_VT";
    private static final String COL_6 = "FLAG_WL";

    public DatabaseSettingHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("create table " + TABLE_NAME +
                "(ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                "FLAG_SENSOR TEXT," +
                "CONST_VR INTEGER," +
                "CONST_VS INTEGER," +
                "CONST_VT INTEGER," +
                "FLAG_WL)");
    }

    public boolean insertData(String flagSensor,int constVr,int constVs,
                              int constVt, String flagWakelock) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_2,flagSensor);
        contentValues.put(COL_3,constVr);
        contentValues.put(COL_4,constVs);
        contentValues.put(COL_5,constVt);
        contentValues.put(COL_6,flagWakelock);
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

    public boolean updateData(String id, String flagSensor, int constVr, int constVs,
                              int constVt, String flagWakelock) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_1,id);
        contentValues.put(COL_2,flagSensor);
        contentValues.put(COL_3,constVr);
        contentValues.put(COL_4,constVs);
        contentValues.put(COL_5,constVt);
        contentValues.put(COL_6,flagWakelock);
        db.update(TABLE_NAME, contentValues, "ID = ?",new String[] { id });
        return true;
    }

    public Integer deleteData (String id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_NAME, "ID = ?",new String[] {id});
    }

    public Cursor getDataAtId(String id) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select * from "+TABLE_NAME+
                        " where ID = ?",
                new String[]{id});
        return res;
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS "+TABLE_NAME);
        onCreate(sqLiteDatabase);

    }
}
