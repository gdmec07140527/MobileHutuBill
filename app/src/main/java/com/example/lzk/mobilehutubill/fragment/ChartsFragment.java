package com.example.lzk.mobilehutubill.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.lzk.mobilehutubill.R;
import com.example.lzk.mobilehutubill.bean.Record;
import com.example.lzk.mobilehutubill.service.RecordService;
import com.example.lzk.mobilehutubill.util.DateUtil;

import java.util.ArrayList;
import java.util.List;

import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.AxisValue;
import lecho.lib.hellocharts.model.Column;
import lecho.lib.hellocharts.model.ColumnChartData;
import lecho.lib.hellocharts.model.SubcolumnValue;
import lecho.lib.hellocharts.util.ChartUtils;
import lecho.lib.hellocharts.view.ColumnChartView;

public class ChartsFragment extends Fragment {
    private View view;
    private ColumnChartView chart;
    private RecordService recordService;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_charts, container, false);
        Log.e("TEST", "4狗");
        initView();
        new Thread(new Runnable() {
            @Override
            public void run() {
                initData();
            }
        }).start();
        return view;
    }
    public void refreshData(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                initData();
            }
        }).start();
    }
    private void initView(){
        chart = (ColumnChartView) view.findViewById(R.id.columnChart);
        recordService =new RecordService(getActivity());
    }
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {      //判断标志位
                case 1:
                    initcharts();
                    break;
            }
        }
    };

    private int thisMonthTotalDay;
    private List<Double> list=new ArrayList<Double>();
    private void initData(){
        list=new ArrayList<Double>();
        thisMonthTotalDay = DateUtil.thisMonthTotalDay();//本月共有多少天
        String str=DateUtil.longtostr(System.currentTimeMillis()).substring(0,8);
        List<Long> longdate=new ArrayList<Long>();
        for (int i=0;i<thisMonthTotalDay;i++){
            if (i  < 9){
                int ii=i+1;
                longdate.add(DateUtil.strtolong(str+0+ii+"日"));
            }else {
                int ii=i+1;
                longdate.add(DateUtil.strtolong(str+ii+"日"));
            }
        }
        for (int i=0;i<longdate.size();i++){
            long l=longdate.get(i);
            if (recordService.querybydate(l)!=null){
                List<Record> listrecord=recordService.querybydate(l);
                double daySpend =0;
                for (Record record:listrecord){
                    daySpend += record.getSpend();
                }
                list.add(daySpend);
            }else if (recordService.querybydate(l)==null){
                list.add(Double.valueOf(0));
            }
        }
        Message msg = Message.obtain();  //从全局池中返回一个message实例，避免多次创建message（如new Message）
        msg.what = 1;   //标志消息的标志
        handler.sendMessage(msg);
    }
    private void initcharts(){

        int numColumns = thisMonthTotalDay;
        ColumnChartData columnChartData;
        List<Column> columns =new ArrayList<>();
        List<SubcolumnValue>  values;

        for(int i=0;i<numColumns;i++){
            values=new ArrayList<>();
            values.add(new SubcolumnValue(new Double(list.get(i)).floatValue(), ChartUtils.pickColor()));
            Column column = new Column(values);
            //给每一个柱子表上值
            column.setHasLabels(true);
            //添加的地方，选中时出现label
            //column.setHasLabelsOnlyForSelected(true);
            columns.add(column);
        }

        columnChartData = new ColumnChartData(columns);
        Axis axisBootom = new Axis();
        Axis axisLeft = new Axis();
        List<AxisValue> axisValuess=new ArrayList<>();
        for(int i=0;i<numColumns;i++){
            axisValuess.add(new AxisValue(i).setLabel(i+1+"日"));
        }
        axisBootom.setValues(axisValuess);
        axisBootom.setName("日期");
        axisLeft.setName("花费（元）");
        //加入横线
        axisBootom.setHasLines(true);
        axisLeft.setHasLines(true);
        columnChartData.setAxisXBottom(axisBootom);
        columnChartData.setAxisYLeft(axisLeft);
        //添加的地方，选中时变粗。通常情况下点击变粗会立马缩回去，现在回变粗停住停住
        //chart.setValueSelectionEnabled(true);
        chart.setZoomEnabled(false);//禁止手势缩放
        chart.setColumnChartData(columnChartData);
    }
}