package com.example.basket;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.Nullable;

public class DbHelper extends SQLiteOpenHelper {
    static String DbName="SeeJang.db";
    static int DbVersion=3;

    public DbHelper(@Nullable Context context){
        super(context, DbName, null, DbVersion);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase){
        String query_1="CREATE TABLE if not exists cart1(_id INTEGER PRIMARY KEY, name TEXT)";
        sqLiteDatabase.execSQL(query_1);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1){
        String query_1="DROP TABLE IF EXISTS cart1";
        sqLiteDatabase.execSQL(query_1);
        onCreate(sqLiteDatabase);
    }
}

