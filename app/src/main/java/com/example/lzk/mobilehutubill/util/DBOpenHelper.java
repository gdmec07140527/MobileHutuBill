package com.example.lzk.mobilehutubill.util;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Lzk on 2017/11/25.
 */

public class DBOpenHelper extends SQLiteOpenHelper {

    public DBOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        //第三个参数CursorFactory指定在执行查询时获得一个游标实例的工厂类,设置为null,代表使用系统默认的工厂类
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table if not exists config (id integer primary key autoincrement, configkey text not null,value text not null);");
        db.execSQL("create table if not exists category (id integer primary key autoincrement, name text not null)");
        db.execSQL("create table if not exists record (id integer primary key autoincrement,spend real not null,cid integer not null, comment text not null, date integer not null, FOREIGN KEY(cid) REFERENCES category(id));");
    }

    //当打开数据库时传入的版本号与当前的版本号不同时会调用该方法
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //db.execSQL("DROP TABLE IF EXISTS category");
        //onCreate(db);
    }
}
