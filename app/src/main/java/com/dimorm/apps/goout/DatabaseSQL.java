package com.dimorm.apps.goout;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Dima on 5/2/2017.
 */

public class DatabaseSQL extends SQLiteOpenHelper {
    public DatabaseSQL(Context context) {
        super(context, "data.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String queryCreateTable = "CREATE TABLE favorites(_id INTEGER PRIMARY KEY AUTOINCREMENT , name TEXT , address TEXT , imageString TEXT ,lat TEXT, lng TEXT)";
        db.execSQL(queryCreateTable);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
