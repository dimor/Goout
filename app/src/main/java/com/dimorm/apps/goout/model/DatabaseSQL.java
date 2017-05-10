package com.dimorm.apps.goout.model;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Dima on 5/2/2017.
 */

public class DatabaseSQL extends SQLiteOpenHelper {

    private static DatabaseSQL databaseInstance = null;


    public DatabaseSQL(Context context) {
        super(context, "data.db", null, 1);
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
}
