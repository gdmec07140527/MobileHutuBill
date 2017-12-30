package com.example.lzk.mobilehutubill.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.example.lzk.mobilehutubill.bean.Category;
import com.example.lzk.mobilehutubill.util.DBOpenHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Lzk on 2017/11/23.
 */

public class CategoryDao {
    private static final String DB_NAME = "hutubill.db";//数据库名称
    private static final String TABLE_NAME = "category";//数据表名称
    private static final int DB_VERSION = 1;//数据库版本

    //表的字段名
    private static String KEY_ID = "id";
    private static String KEY_NAME = "name";

    private SQLiteDatabase mSQLiteDatabase;
    private Context mContext;
    private DBOpenHelper mDBOpenHelper;//数据库打开帮助类


    public CategoryDao(Context context) {
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
    public long insertData(Category category) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(KEY_NAME, category.getName());
        return mSQLiteDatabase.insert(TABLE_NAME, null, contentValues);
    }

    //删除一条数据
    public long deleteData(long id) {
        return mSQLiteDatabase.delete(TABLE_NAME, KEY_ID + "=" + id, null);
    }


    //更新一条数据
    public long updateData(long id, Category category) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(KEY_NAME, category.getName());
        return mSQLiteDatabase.update(TABLE_NAME, contentValues, KEY_ID + "=" + id, null);
    }

    //查询一条数据
    public List<Category> queryData(long id) {
        Cursor cursor = mSQLiteDatabase.query(TABLE_NAME, new String[]{KEY_ID, KEY_NAME},
                KEY_ID + "=" + id, null, null, null, null);
        return ConvertToCategory(cursor);
    }

    //查询所有数据
    public List<Category> queryDataList() {
        Cursor cursor = mSQLiteDatabase.query(TABLE_NAME, new String[]{KEY_ID, KEY_NAME},
                null, null, null, null, null);
        return ConvertToCategory(cursor);
    }


    private List<Category> ConvertToCategory(Cursor cursor) {
        int ResultCounts = cursor.getCount();
        if (ResultCounts == 0 || !cursor.moveToFirst()) {
            return null;
        }
        List<Category> mCategoryList = new ArrayList<>();
        for (int i = 0; i < ResultCounts; i++) {
            Category category = new Category();
            category.setId(cursor.getInt(0));
            category.setName(cursor.getString(cursor.getColumnIndex(KEY_NAME)));
            mCategoryList.add(category);
            cursor.moveToNext();
        }
        return mCategoryList;
    }

}
