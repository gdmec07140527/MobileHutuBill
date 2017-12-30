package com.example.lzk.mobilehutubill.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.lzk.mobilehutubill.R;
import com.example.lzk.mobilehutubill.bean.Record;
import com.example.lzk.mobilehutubill.util.DateUtil;

import java.util.List;

/**
 * Created by Lzk on 2017/12/7.
 */

public class CategoryFragmentRecordRecyclerViewAdapter extends RecyclerView.Adapter<CategoryFragmentRecordRecyclerViewAdapter.MyViewHolder>implements View.OnClickListener, View.OnLongClickListener{
    private List<Record> mRecordList;
    private Context mContext;
    //private LayoutInflater inflater;
    private RecyclerViewOnItemClickListener onItemClickListener;
    private RecyclerViewOnItemLongClickListener onItemLongClickListener;

    public CategoryFragmentRecordRecyclerViewAdapter(Context context, List<Record> recordList) {
        this.mContext = context;
        this.mRecordList = recordList;
        //inflater = LayoutInflater.from(mContext);
    }

    @Override
    public int getItemCount() {
        return mRecordList.size();
    }

    //填充onCreateViewHolder方法返回的holder中的控件
    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        holder.textView.setText(mRecordList.get(position).getSpend() + "");
        holder.textView2.setText(mRecordList.get(position).getCname()+"");
        holder.textView3.setText(mRecordList.get(position).getComment());
        holder.textView4.setText(DateUtil.longtostr(mRecordList.get(position).getDate()));
        holder.itemView.setTag(position);//给view设置tag以作为参数传递到监听回调方法中
    }

    //重写onCreateViewHolder方法，返回一个自定义的ViewHolder
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //View view = inflater.inflate(R.layout.item_home, parent, false);
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_record, parent, false);
        MyViewHolder holder = new MyViewHolder(view);
        view.setOnClickListener(this);
        view.setOnLongClickListener(this);
        //这里 我们可以拿到点击的item的view 对象，所以在这里给view设置点击监听，
        return holder;
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView textView;
        private TextView textView2;
        private TextView textView3;
        private TextView textView4;
        public MyViewHolder(View view) {
            super(view);
            textView = (TextView) view.findViewById(R.id.record_spend);
            textView2 = (TextView) view.findViewById(R.id.record_category);
            textView3 = (TextView) view.findViewById(R.id.record_comment);
            textView4 = (TextView) view.findViewById(R.id.record_date);
        }
    }
    @Override
    public void onClick(View v) {
        if (onItemClickListener != null) {
            //注意这里使用getTag方法获取数据
            onItemClickListener.onItemClickListener(v, (Integer) v.getTag());
        }
    }
    @Override
    public boolean onLongClick(View v) {
        return onItemLongClickListener != null && onItemLongClickListener.onItemLongClickListener(v, (Integer) v.getTag());
    }

    /*设置点击事件*/
    public void setRecyclerViewOnItemClickListener(RecyclerViewOnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    /*设置长按事件*/
    public void setOnItemLongClickListener(RecyclerViewOnItemLongClickListener onItemLongClickListener) {
        this.onItemLongClickListener = onItemLongClickListener;
    }

    public interface RecyclerViewOnItemClickListener {

        void onItemClickListener(View view, int position);

    }

    public interface RecyclerViewOnItemLongClickListener {

        boolean onItemLongClickListener(View view, int position);

    }
}
