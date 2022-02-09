package com.example.manavb.voicealarm;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.Arrays;

public class DatabaseHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "alarms.db";
    public static final String TABLE_NAME = "alarms_table";
    public static final String COL_1 = "ID";
    public static final String COL_2 = "NAME";    // string
    public static final String COL_3 = "TIME";    // string
    public static final String COL_4 = "STATUS";  // boolean
    public static final String COL_5 = "DAYS";    // string (boolean array values converted)
    public static final String COL_6 = "RINGTONE";  // string (change later, maybe)
    public static final String COL_7 = "VIBRATION"; // boolean


    public DatabaseHelper( Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + TABLE_NAME + "(ID INTEGER PRIMARY KEY AUTOINCREMENT, NAME TEXT, TIME TEXT, STATUS BOOLEAN, DAYS TEXT, RINGTONE TEXT, VIBRATION BOOLEAN)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public boolean insertData(String name, String time, boolean status, boolean[] days, String ringtoneURI, boolean vibration){
        SQLiteDatabase db =  this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_2, name);
        System.out.println("----- Value of time being written to the database: " + time);
        contentValues.put(COL_3, time);
        contentValues.put(COL_4, status);
        contentValues.put(COL_5, Arrays.toString(days));
        //Log.d("databasestuff", "days array in databaseHelper: " + Arrays.toString(days)); // for testing puposes
        contentValues.put(COL_6, ringtoneURI);
        contentValues.put(COL_7, vibration);

        long result = db.insert(TABLE_NAME, null, contentValues);
        if(result == -1)
            return false;
        else
            return true;
    }

    public Cursor getAllData(){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor result = db.rawQuery("select * from " + TABLE_NAME, null);
        return result;
    }

    public boolean updateData(String id, String name, String time, boolean status, boolean[] days, String ringtoneURI, boolean vibration){
        SQLiteDatabase db =  this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_1, id);
        contentValues.put(COL_2, name);
        contentValues.put(COL_3, time);
        contentValues.put(COL_4, status);
        contentValues.put(COL_5, Arrays.toString(days));
        contentValues.put(COL_6, ringtoneURI);
        contentValues.put(COL_7, vibration);
        db.update(TABLE_NAME, contentValues, "ID = ?", new String[]{id});
        return true;
    }


}
