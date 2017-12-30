package com.example.lzk.mobilehutubill.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.example.lzk.mobilehutubill.bean.Config;
import com.example.lzk.mobilehutubill.util.DBOpenHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Lzk on 2017/12/25.
 */

public class ConfigDao {
    private static final String DB_NAME = "hutubill.db";//数据库名称
    private static final String TABLE_NAME = "config";//数据表名称
    private static final int DB_VERSION = 1;//数据库版本

    //表的字段名
    private static String KEY_ID = "id";
    private static String KEY_CONFIG = "configkey";
    private static String VALUE = "value";
    private SQLiteDatabase mSQLiteDatabase;
    private Context mContext;
    private DBOpenHelper mDBOpenHelper;//数据库打开帮助类

    public ConfigDao(Context context) {
        mContext = context;
    }
    //打开数据库
    public void openDataBase() {
        mDBOpenHelper = new DBOpenHelper(mContext, DB_NAME, null, DB_VERSION);
        try {
            mSQLiteDatabase = mDBOpenHelper.getWritableDatabase();//获取可写数据库
        } catch (SQLException e) {
            mSQLiteDatabase = mDBOpenHelper.getReadableDatabase();//获取只读数据库
        }
    }

    //关闭数据库
    public void closeDataBase() {
        if (mSQLiteDatabase != null) {
            mSQLiteDatabase.close();
        }
    }

    //插入一条数据
    public long insertData(Config config) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(KEY_CONFIG, config.getConfigkey());
        contentValues.put(VALUE, config.getValue());
        return mSQLiteDatabase.insert(TABLE_NAME, null, contentValues);
    }

    //更新一条数据
    public long updateData(int id, Config config) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(KEY_CONFIG, config.getConfigkey());
        contentValues.put(VALUE, config.getValue());
        return mSQLiteDatabase.update(TABLE_NAME, contentValues, KEY_ID + "=" + id, null);
    }

    //根据key
    public List<Config> queryDataListByKey(String key) {
        Cursor cursor = mSQLiteDatabase.query(TABLE_NAME, new String[]{KEY_ID, KEY_CONFIG,VALUE},
                KEY_CONFIG + "='"+key+"'", null, null, null, null);
        return ConvertToConfig(cursor);
    }

    private List<Config> ConvertToConfig(Cursor cursor) {
        int ResultCounts = cursor.getCount();
        if (ResultCounts == 0 || !cursor.moveToFirst()) {
            return null;
        }
        List<Config> mConfigList = new ArrayList<>();
        for (int i = 0; i < ResultCounts; i++) {
            Config config = new Config();
            config.setId(cursor.getInt(0));
            config.setConfigkey(cursor.getString(cursor.getColumnIndex(KEY_CONFIG)));
            config.setValue(cursor.getString(cursor.getColumnIndex(VALUE)));
            mConfigList.add(config);
            cursor.moveToNext();
        }
        return mConfigList;
    }
}
