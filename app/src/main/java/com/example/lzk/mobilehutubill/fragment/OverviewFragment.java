package com.example.lzk.mobilehutubill.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lzk.mobilehutubill.R;
import com.example.lzk.mobilehutubill.bean.Config;
import com.example.lzk.mobilehutubill.bean.Record;
import com.example.lzk.mobilehutubill.service.ConfigService;
import com.example.lzk.mobilehutubill.service.RecordService;
import com.example.lzk.mobilehutubill.util.ArithUtil;
import com.example.lzk.mobilehutubill.util.DateUtil;
import com.example.lzk.mobilehutubill.util.DoughnutView;

import java.text.DecimalFormat;
import java.util.List;

public class OverviewFragment extends Fragment {

    private DoughnutView doughnutView;
    private View view;
    private TextView jinrixiaofei, benyuexiaofei, benyueyusuan, rijunxiaofei, benyueshengyu, rijunkeyong, juliyuemo;
    private RecordService recordService;
    private ConfigService configService;
    private double monthSpend = 0;//本月消费
    private double todaySpend = 0;//今日消费
    private double avgSpendPerDay = 0;//日均消费
    private double monthAvailable = 0;//本月剩余
    private double dayAvgAvailable = 0;//日均可用
    private double usagePercentage = 0;//百分比
    private double monthBudget = 0;// 预算
    private int thisMonthTotalDay = DateUtil.thisMonthTotalDay();//本月共有多少天
    private int monthLeftDay = DateUtil.thisMonthLeftDay();//距离月末

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_overview, container, false);
        Log.e("TEST", "1狗");
        initView();
        isfirst();
        new Thread(new Runnable() {
            @Override
            public void run() {
                initData();
            }
        }).start();
        return view;
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {      //判断标志位
                case 1:
                    updateUI();
                    break;
            }
        }
    };

    private void updateUI() {
        jinrixiaofei.setText(todaySpend + "");
        benyuexiaofei.setText(monthSpend + "");
        benyueyusuan.setText(monthBudget + "");
        rijunxiaofei.setText(avgSpendPerDay + "");
        if (monthAvailable<0){
            benyueshengyu.setText("超支"+Math.abs(monthAvailable));
            rijunkeyong.setText("0.00");
        }else if (monthAvailable>=0){
            benyueshengyu.setText(monthAvailable + "");
            rijunkeyong.setText(dayAvgAvailable + "");
        }
        juliyuemo.setText(monthLeftDay + "天");
        doughnutView.setValue((float) usagePercentage);
        cleandata();
    }

    public void refreshData() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                initData();
            }
        }).start();
    }

    private void initView() {
        jinrixiaofei = (TextView) view.findViewById(R.id.jinrixiaofei);
        benyuexiaofei = (TextView) view.findViewById(R.id.benyuexiaofei);
        rijunxiaofei = (TextView) view.findViewById(R.id.rijunxiaofei);
        benyueshengyu = (TextView) view.findViewById(R.id.benyueshengyu);
        rijunkeyong = (TextView) view.findViewById(R.id.rijunkeyong);
        juliyuemo = (TextView) view.findViewById(R.id.juliyuemo);
        benyueyusuan = (TextView) view.findViewById(R.id.benyueyusuan);
        doughnutView = (DoughnutView) view.findViewById(R.id.doughnutView);
        recordService = new RecordService(getActivity());
        configService = new ConfigService(getActivity());
    }

    private void cleandata() {
        todaySpend = 0;
        monthSpend = 0;
        avgSpendPerDay = 0;
        monthAvailable = 0;
        dayAvgAvailable = 0;
        usagePercentage = 0;
        monthBudget = 0;
        thisMonthTotalDay = DateUtil.thisMonthTotalDay();
        monthLeftDay = DateUtil.thisMonthLeftDay();
    }

    private void initData() {
        List<Record> thisMonthRecords = recordService.querybymonth(DateUtil.monthBegin().getTime(), DateUtil.monthEnd().getTime());
        List<Record> toDayRecords = recordService.querybydate(DateUtil.strtolong(DateUtil.longtostr(System.currentTimeMillis())));
        List<Config> list = configService.querybykey("budget");
        //本月预算
        if (list != null) {
            monthBudget = Double.parseDouble(((list.get(0).getValue())));
        }
        // 统计今日消费
        if (toDayRecords != null) {
            for (Record record : toDayRecords) {
                todaySpend =ArithUtil.add(todaySpend,record.getSpend());
            }
            //todaySpend = todaySpendtemp;
        }
        // 统计本月消费
        if (thisMonthRecords != null) {
            for (Record record : thisMonthRecords) {
                monthSpend =ArithUtil.add(monthSpend,record.getSpend());
            }
            //monthSpend = monthSpendtemp;
        }

        // 计算日均消费
        try {
            avgSpendPerDay=ArithUtil.div(monthSpend,thisMonthTotalDay,2);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        // 计算本月剩余
        monthAvailable=ArithUtil.sub(monthBudget,monthSpend);
        // 计算日均可用
        try {
            dayAvgAvailable=ArithUtil.div(monthAvailable,monthLeftDay,2);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        // 计算使用比例
        if (monthBudget != 0) {
            try {
                usagePercentage=ArithUtil.div(monthSpend*360,monthBudget,3);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        Message msg = Message.obtain();  //从全局池中返回一个message实例，避免多次创建message（如new Message）
        msg.what = 1;   //标志消息的标志
        handler.sendMessage(msg);
    }

    private void isfirst() {
        SharedPreferences shared = getActivity().getSharedPreferences("is", getActivity().MODE_PRIVATE);
        boolean isfer = shared.getBoolean("isfer", true);
        final SharedPreferences.Editor editor = shared.edit();
        if (isfer) {
            //第一次进入跳转
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());//得到自定义对话框
            View DialogView = layoutInflater.inflate(R.layout.dialog_addbudget, null);
            final EditText firstyusuan = (EditText) DialogView.findViewById(R.id.addtext);
            //创建对话框
            final AlertDialog dlg = new AlertDialog.Builder(getActivity())
                    .setTitle("输入框")
                    .setMessage("设置预算")
                    .setView(DialogView)//设置自定义对话框的样式
                    .setNegativeButton("取消", //设置“取消”按钮
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    getActivity().finish();
                                }
                            })
                    .setPositiveButton("确定", null)
                    .create();//创建弹出框
            dlg.setOnShowListener(new DialogInterface.OnShowListener() {
                @Override
                public void onShow(DialogInterface dialog) {
                    Button b = dlg.getButton(AlertDialog.BUTTON_POSITIVE);
                    b.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (TextUtils.isEmpty(firstyusuan.getText().toString())) {
                                //输入框内容为空时不调用dismiss
                                Toast.makeText(getActivity(), "预算必须输入！", Toast.LENGTH_SHORT).show();
                                return;
                            }
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    ConfigService configService = new ConfigService(getActivity());
                                    configService.add("budget", firstyusuan.getText().toString());
                                    initData();
                                }
                            }).start();
                            editor.putBoolean("isfer", false);
                            editor.commit();
                            dlg.dismiss();
                        }
                    });
                }
            });
            dlg.show();//显示
        } else {
            //第二次进入跳转
        }
    }
}
