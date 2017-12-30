package com.example.lzk.mobilehutubill.service;

import android.content.Context;

import com.example.lzk.mobilehutubill.bean.Category;
import com.example.lzk.mobilehutubill.dao.CategoryDao;

import java.util.List;

/**
 * Created by Lzk on 2017/11/28.
 */

public class CategoryService {
    Context mContext;
    CategoryDao categoryDao;
    Category category;

    public CategoryService(Context context) {
        this.mContext = context;
        categoryDao = new CategoryDao(mContext);
        categoryDao.openDataBase();
        category = new Category();
    }

    public void add(String name) {
        category.setName(name);
        categoryDao.insertData(category);
    }

    public void update(int id,String name) {
        category.setName(name);
        categoryDao.updateData(id,category);
    }

    public void delete(int id) {
        categoryDao.deleteData(id);
    }
    public List<Category> query(){
        return categoryDao.queryDataList();
    }
}
