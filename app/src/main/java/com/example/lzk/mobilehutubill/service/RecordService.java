package com.example.lzk.mobilehutubill.service;


import android.content.Context;

import com.example.lzk.mobilehutubill.bean.Record;
import com.example.lzk.mobilehutubill.dao.RecordDao;

import java.util.List;

/**
 * Created by Lzk on 2017/11/28.
 */

public class RecordService {
    Context mContext;
    RecordDao recordDao;
    Record record;

    public RecordService(Context context) {
        this.mContext = context;
        recordDao = new RecordDao(mContext);
        recordDao.openDataBase();
        record = new Record();
    }

    public void add(double spend,int cid,String comment,long date) {
        record.setSpend(spend);
        record.setCid(cid);
        record.setComment(comment);
        record.setDate(date);
        recordDao.insertData(record);
    }

    public void update(int id, double spend,int cid,String comment,long date) {
        record.setSpend(spend);
        record.setCid(cid);
        record.setComment(comment);
        record.setDate(date);
        recordDao.updateData(id, record);
    }

    public void delete(int id) {
        recordDao.deleteData(id);
    }

    public List<Record> querybycid(int cid){
        return recordDao.queryDataListByCid(cid);
    }
    public List<Record> querybydate(long date){
        return recordDao.queryDataListByDate(date);
    }
    public List<Record> querybymonth(long startdate,long enddate){
        return recordDao.queryDataListByMonth(startdate,enddate);
    }
    public int count(int cid){
        return recordDao.getCount(cid);
    }
}
