package com.dimorm.apps.goout.model;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class DatabaseSQL extends SQLiteOpenHelper {
    private static DatabaseSQL databaseInstance = null;
    SQLiteDatabase database;
    private Context context;
    public DatabaseSQL(Context context) {
        super(context, "data.db", null, 1);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String queryCreateTableFav = "CREATE TABLE favorites(_id INTEGER PRIMARY KEY AUTOINCREMENT , name TEXT , address TEXT , imageString TEXT ,lat TEXT, lng TEXT)";
        db.execSQL(queryCreateTableFav);

        String queryCreateTableHistory = "CREATE TABLE history(_id INTEGER PRIMARY KEY AUTOINCREMENT , name TEXT , address TEXT , imageString TEXT ,lat TEXT, lng TEXT)";
        db.execSQL(queryCreateTableHistory);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public static DatabaseSQL getDatabaseInstance (Context context){
        if(databaseInstance == null)
        {
            databaseInstance = new DatabaseSQL(context);
        }
        return databaseInstance;
    }


    public static Cursor getCursor (Context context){
        databaseInstance = new DatabaseSQL(context);
        return databaseInstance.getReadableDatabase().query("history", null, null, null, null, null, null);
    }





}
