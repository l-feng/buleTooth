package com.example.administrator.myapplication.sql;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Administrator on 2018/5/23 0023.
 */

public class OrderDBHelper extends SQLiteOpenHelper {//SQLiteOpenHelper类封装了一些存储应用数据的常用数据库操作
    // 这个类主要用于建数据库和建表用，如创建、打开、以及更新数据库等。
    private static final int DB_VERSION = 1;
    private static final String DB_NAME = "biShe.db";
    public static final String TABLE_NAME = "WeatherData";

    public OrderDBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        // create table Orders(Id integer primary key, CustomName text, OrderPrice integer, Country text);
        String sql = "create table if not exists " + TABLE_NAME + " (data text primary key,  temperature text, humidity text,infrared text,smoke text)";
        sqLiteDatabase.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        String sql = "DROP TABLE IF EXISTS " + TABLE_NAME;
        sqLiteDatabase.execSQL(sql);
        onCreate(sqLiteDatabase);
    }
}