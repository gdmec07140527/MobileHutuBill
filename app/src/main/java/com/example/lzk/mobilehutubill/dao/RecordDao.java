package com.example.lzk.mobilehutubill.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.Nullable;

import com.example.lzk.mobilehutubill.bean.Category;
import com.example.lzk.mobilehutubill.bean.Record;
import com.example.lzk.mobilehutubill.util.DBOpenHelper;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Lzk on 2017/12/7.
 */

public class RecordDao {
    private static final String DB_NAME = "hutubill.db";//数据库名称
    private static final String TABLE_NAME = "record";//数据表名称
    private static final int DB_VERSION = 1;//数据库版本

    //表的字段名
    private static String KEY_ID = "id";
    private static String KEY_SPEND = "spend";
    private static String KEY_CID = "cid";
    private static String KEY_COMMENT = "comment";
    private static String KEY_DATE = "date";

    private SQLiteDatabase mSQLiteDatabase;
    private Context mContext;
    private DBOpenHelper mDBOpenHelper;//数据库打开帮助类


    public RecordDao(Context context) {
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
    public long insertData(Record record) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(KEY_SPEND, record.getSpend());
        contentValues.put(KEY_CID, record.getCid());
        contentValues.put(KEY_COMMENT, record.getComment());
        contentValues.put(KEY_DATE, record.getDate());
        return mSQLiteDatabase.insert(TABLE_NAME, null, contentValues);
    }

    //删除一条数据
    public long deleteData(int id) {
        return mSQLiteDatabase.delete(TABLE_NAME, KEY_ID + "=" + id, null);
    }

    //更新一条数据
    public long updateData(int id, Record record) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(KEY_SPEND, record.getSpend());
        contentValues.put(KEY_CID, record.getCid());
        contentValues.put(KEY_COMMENT, record.getComment());
        contentValues.put(KEY_DATE, record.getDate());
        return mSQLiteDatabase.update(TABLE_NAME, contentValues, KEY_ID + "=" + id, null);
    }

    //查询某个分类下所有数据
    public List<Record> queryDataListByCid(int cid) {
        Cursor cursor = mSQLiteDatabase.query(TABLE_NAME, new String[]{KEY_ID, KEY_SPEND,KEY_CID,KEY_COMMENT,KEY_DATE},
                KEY_CID + "=" + cid, null, null, null, null);
        return ConvertToRecord(cursor);
    }
    //查询某个日期下所有数据
    public List<Record> queryDataListByDate(long date) {
        Cursor cursor = mSQLiteDatabase.query(TABLE_NAME, new String[]{KEY_ID, KEY_SPEND,KEY_CID,KEY_COMMENT,KEY_DATE},
                KEY_DATE + "=" + date, null, null, null, null);
        return ConvertToRecord(cursor);
    }

    public List<Record> queryDataListByMonth(long startdate,long enddate) {
        Cursor cursor = mSQLiteDatabase.query(TABLE_NAME, new String[]{KEY_ID, KEY_SPEND,KEY_CID,KEY_COMMENT,KEY_DATE},
                KEY_DATE + ">" + "="+ startdate+" and "+KEY_DATE + "<" + "="+ enddate, null, null, null, null);
        return ConvertToRecord(cursor);
    }
    public int getCount(int cid) {
        Cursor cursor = mSQLiteDatabase.rawQuery("select count(*)from "+TABLE_NAME+" where "+KEY_CID+" = "+cid+"",null);
        cursor.moveToFirst();
        return cursor.getInt(0);
    }

    private List<Record> ConvertToRecord(Cursor cursor) {
        int ResultCounts = cursor.getCount();
        if (ResultCounts == 0 || !cursor.moveToFirst()) {
            return null;
        }
        List<Record> mRecordList = new ArrayList<>();
        for (int i = 0; i < ResultCounts; i++) {
            Record record = new Record();
            record.setId(cursor.getInt(0));
            record.setSpend(cursor.getDouble(cursor.getColumnIndex(KEY_SPEND)));
            record.setCid(cursor.getInt(cursor.getColumnIndex(KEY_CID)));
            record.setComment(cursor.getString(cursor.getColumnIndex(KEY_COMMENT)));
            record.setDate(cursor.getLong(cursor.getColumnIndex(KEY_DATE)));
            mRecordList.add(record);
            cursor.moveToNext();
        }
        return mRecordList;
    }
}
