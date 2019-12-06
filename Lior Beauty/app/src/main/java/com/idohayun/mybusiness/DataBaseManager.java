package com.idohayun.mybusiness;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DataBaseManager extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;
    private static final String TAG = "DataBaseManager";
    private static final String DATABASE_NAME = "user_details.db", TABLE_NAME = "personal_data",
                            COL1 = "ID",
                            COL2 = "USERNAME",
                            COL3 = "PASSWORD",
                            COL4 = "PHONE";

    public DataBaseManager(Context context) {
        super(context,DATABASE_NAME,null,DATABASE_VERSION);
    }

    public void closeDB() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.close();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE " + TABLE_NAME + "(" + COL1 + " INTEGER , "
                + COL2 + " TEXT, " + COL3 + " TEXT, " + COL4 + " INTEGER);";
        db.execSQL(createTable);
    }

    public boolean addData(baseUSER user) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL1,user.getId());
        contentValues.put(COL2,user.getName());
        contentValues.put(COL3,user.getPassword());
        contentValues.put(COL4,user.getPhone());

        long result = db.insert(TABLE_NAME,null,contentValues);
        //check if data insert correctly
        return !(result == -1);
    }

    public boolean updateData(baseUSER user) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL2,user.getName());
        contentValues.put(COL3,user.getPassword());
        contentValues.put(COL4,user.getPhone());

        long result = db.update(TABLE_NAME,contentValues,"ID="+user.getId(),null);
        return !(result == -1);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

    public Cursor getData() {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT * FROM " + TABLE_NAME;
        Cursor data = db.rawQuery(query,null);
        return data;
    }

    public boolean onDeleteData() {
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            db.execSQL("delete from " + TABLE_NAME);
        } catch (SQLException e) {
            Log.d(TAG, "onDeleteData: " + e.getMessage());
            return false;
        }
        return true;
    }
}
