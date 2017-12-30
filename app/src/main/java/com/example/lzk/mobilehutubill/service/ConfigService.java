package com.example.lzk.mobilehutubill.service;

import android.content.Context;

import com.example.lzk.mobilehutubill.bean.Config;
import com.example.lzk.mobilehutubill.dao.ConfigDao;

import java.util.List;

/**
 * Created by Lzk on 2017/12/25.
 */

public class ConfigService {
    Context mContext;
    ConfigDao configDao;
    Config config;
    public ConfigService(Context context) {
        this.mContext = context;
        configDao=new ConfigDao(mContext);
        configDao.openDataBase();
        config=new Config();
    }
    public void add(String key,String value) {
        config.setConfigkey(key);
        config.setValue(value);
        configDao.insertData(config);
    }
    public void update(int id,String key,String value) {
        config.setConfigkey(key);
        config.setValue(value);
        configDao.updateData(id,config);
    }
    public List<Config> querybykey(String key){
        return configDao.queryDataListByKey(key);
    }
}
