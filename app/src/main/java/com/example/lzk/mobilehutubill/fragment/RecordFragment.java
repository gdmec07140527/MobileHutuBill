package com.example.lzk.mobilehutubill.fragment;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


import com.example.lzk.mobilehutubill.R;
import com.example.lzk.mobilehutubill.adapter.CategorySpinnerAdapter;
import com.example.lzk.mobilehutubill.bean.Category;
import com.example.lzk.mobilehutubill.service.CategoryService;
import com.example.lzk.mobilehutubill.service.RecordService;
import com.example.lzk.mobilehutubill.util.DateUtil;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class RecordFragment extends Fragment {
    private View view;
    private Button bt,bt2;
    private EditText addspend, addcomment;
    private TextView adddate;
    private Spinner mySpinner;
    private List<Category> listAll = new ArrayList<Category>();
    private CategoryService categoryService;
    private RecordService recordService;
    private CategorySpinnerAdapter categorySpinnerAdapter;
    private DatePickerDialog.OnDateSetListener onDateSetListener;
    private String days;
    private int mYear, mMonth, mDay;
    private int cid;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_record, container, false);
        Log.e("TEST", "2狗");
        initView();
        setListener();
        new Thread(new Runnable() {
            @Override
            public void run() {
                querycategorydatas();
            }
        }).start();
        return view;
    }

    private void initView() {
        mySpinner = (Spinner) view.findViewById(R.id.addspinner);
        bt = (Button) view.findViewById(R.id.jiyibi);
        bt2= (Button) view.findViewById(R.id.cleanadd);
        addspend = (EditText) view.findViewById(R.id.addspend);
        addcomment = (EditText) view.findViewById(R.id.addcomment);
        adddate = (TextView) view.findViewById(R.id.adddate);
        adddate.setText(DateUtil.longtostr(System.currentTimeMillis()));
        categoryService = new CategoryService(getActivity());
        recordService = new RecordService(getActivity());
        categorySpinnerAdapter = new CategorySpinnerAdapter(getActivity(), listAll);
        mySpinner.setAdapter(categorySpinnerAdapter);
        mySpinner.setSelection(0);
    }
    public void refreshData(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                querycategorydatas();
            }
        }).start();
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {      //判断标志位
                case 1:
                    categorySpinnerAdapter.notifyDataSetChanged();
                    mySpinner.setSelection(0);
                    break;
            }
        }
    };

    private void querycategorydatas() {
        List<Category> listTemp = categoryService.query();//查询所有数据
        listAll.clear();
        if (categorySpinnerAdapter != null) {
            if (listTemp != null) {
                Category category = new Category();
                category.setId(0);
                category.setName("请选择");
                listAll.add(category);
                listAll.addAll(listTemp);
            }
            Message msg = Message.obtain();  //从全局池中返回一个message实例，避免多次创建message（如new Message）
            msg.what = 1;   //标志消息的标志
            handler.sendMessage(msg);
        }
    }
    public void refresh(){
        OverviewFragment overviewFragment = (OverviewFragment) getActivity().getSupportFragmentManager().findFragmentByTag("android:switcher:" + R.id.main_vpager + ":0");
        if (overviewFragment != null) //可能没有实例化
        {
            if (overviewFragment.getView() != null) {
                overviewFragment.refreshData();//自定义方法更新
            }
        }
        CategoryFragment categoryFragment = (CategoryFragment) getActivity().getSupportFragmentManager().findFragmentByTag("android:switcher:" + R.id.main_vpager + ":2");
        if (categoryFragment != null) //可能没有实例化
        {
            if (categoryFragment.getView() != null) {
                categoryFragment.refreshData();//自定义方法更新
            }
        }
        ChartsFragment chartsFragment = (ChartsFragment) getActivity().getSupportFragmentManager().findFragmentByTag("android:switcher:" + R.id.main_vpager + ":3");
        if (chartsFragment != null) //可能没有实例化
        {
            if (chartsFragment.getView() != null) {
                chartsFragment.refreshData();//自定义方法更新
            }
        }
    }
    private void setListener() {
        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(addspend.getText().toString())||TextUtils.isEmpty(addcomment.getText().toString().trim())){
                    Toast.makeText(getActivity(), "输入必须完整！", Toast.LENGTH_SHORT).show();
                }
                if (mySpinner.getSelectedItemPosition() == 0) {
                    Toast.makeText(getActivity(), "请先选择分类", Toast.LENGTH_SHORT).show();
                } else if (mySpinner.getSelectedItemPosition() != 0) {
                    if (cid == 0) {
                        Toast.makeText(getActivity(), "没有选择分类！", Toast.LENGTH_SHORT).show();
                    } else if (cid != 0) {
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                recordService.add(Double.parseDouble(((addspend.getText().toString()))), cid, addcomment.getText().toString(), DateUtil.strtolong(adddate.getText().toString()));
                                refresh();
                            }
                        }).start();
                    }
                }
            }
        });
        bt2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        mySpinner.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                // TODO Auto-generated method stub
                cid = listAll.get(arg2).getId();
                arg0.setVisibility(View.VISIBLE);
            }

            public void onNothingSelected(AdapterView<?> arg0) {
                arg0.setVisibility(View.VISIBLE);
            }
        });

        //日期选择器
        Calendar ca = Calendar.getInstance();
        mYear = ca.get(Calendar.YEAR);
        mMonth = ca.get(Calendar.MONTH);
        mDay = ca.get(Calendar.DAY_OF_MONTH);
        adddate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO 调用时间选择器
                new DatePickerDialog(getActivity(), onDateSetListener, mYear, mMonth, mDay).show();
            }
        });
        onDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                mYear = year;
                mMonth = monthOfYear;
                mDay = dayOfMonth;
                if (mMonth + 1 < 10) {
                    if (mDay < 10) {
                        days = new StringBuffer().append(mYear).append("年").append("0").
                                append(mMonth + 1).append("月").append("0").append(mDay).append("日").toString();
                    } else {
                        days = new StringBuffer().append(mYear).append("年").append("0").
                                append(mMonth + 1).append("月").append(mDay).append("日").toString();
                    }

                } else {
                    if (mDay < 10) {
                        days = new StringBuffer().append(mYear).append("年").
                                append(mMonth + 1).append("月").append("0").append(mDay).append("日").toString();
                    } else {
                        days = new StringBuffer().append(mYear).append("年").
                                append(mMonth + 1).append("月").append(mDay).append("日").toString();
                    }

                }
                adddate.setText(days);
            }
        };
    }
}
