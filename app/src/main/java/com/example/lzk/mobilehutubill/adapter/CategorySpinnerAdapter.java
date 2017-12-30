package com.example.lzk.mobilehutubill.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.lzk.mobilehutubill.R;
import com.example.lzk.mobilehutubill.bean.Category;

import java.util.List;

/**
 * Created by Lzk on 2017/12/7.
 */

public class CategorySpinnerAdapter extends BaseAdapter {
    private List<Category> mList;
    private Context mContext;

    public CategorySpinnerAdapter(Context mContext, List<Category> mList) {
        this.mContext = mContext;
        this.mList = mList;
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public Object getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater layoutInflater=LayoutInflater.from(mContext);
        convertView=layoutInflater.inflate(R.layout.item_spinner_category, null);
        if(convertView!=null)
        {
            TextView textView2=(TextView)convertView.findViewById(R.id.textView13);
            textView2.setText(mList.get(position).getName());
        }
        return convertView;
    }
}
