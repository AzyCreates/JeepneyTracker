package com.example.jeepneytracker;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {
    private static final String DBName = "JeepneyLogDB";
    private static final int DBVersion = 1;
    private static final String TableName = "JeepneyArrivals";

    public DBHelper(Context context) {
        super(context, DBName, null, DBVersion);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE JeepneyArrivals (\n" +
                " LogID INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,\n" +
                " PlateNumber TEXT (50) NOT NULL,\n" +
                " Passengers TEXT (10) NOT NULL,\n" +
                " TimeLogged TEXT (50)\n" +
                ")");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TableName);
        onCreate(db);
    }

    //adding records to database
    public boolean InsertData(String PlateNumber, String Passengers, String TimeLogged) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("PlateNumber", PlateNumber);
        cv.put("Passengers", Passengers);
        cv.put("TimeLogged", TimeLogged);
        long result = db.insert(TableName, null, cv);
        if (result == -1)
            return false;
        else
            return true;
    }

    //displaying records from database
    public Cursor getData() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor record = db.rawQuery("Select LogID, PlateNumber, Passengers, TimeLogged from JeepneyArrivals", null);
        return record;
    }

    //updating records in the database
    public boolean updateData(int LogID, String PlateNumber, String Passengers) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("PlateNumber", PlateNumber);
        cv.put("Passengers", Passengers);
        long result = db.update(TableName, cv, "LogID=?", new String[]{String.valueOf(LogID)});
        if (result == 1)
            return true;
        else
            return false;
    }

    //removing records from the database
    public boolean deleteData(int LogID) {
        SQLiteDatabase db = this.getWritableDatabase();
        long result = db.delete(TableName, "LogID=?", new String[]{String.valueOf(LogID)});
        if (result == 1)
            return true;
        else
            return false;
    }
}