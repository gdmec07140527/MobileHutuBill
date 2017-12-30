package com.example.lzk.mobilehutubill.fragment;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
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
import com.example.lzk.mobilehutubill.adapter.CategoryFragmentCategoryRecyclerViewAdapter;
import com.example.lzk.mobilehutubill.adapter.CategoryFragmentRecordRecyclerViewAdapter;
import com.example.lzk.mobilehutubill.bean.Category;
import com.example.lzk.mobilehutubill.bean.Record;
import com.example.lzk.mobilehutubill.service.CategoryService;
import com.example.lzk.mobilehutubill.service.RecordService;
import com.example.lzk.mobilehutubill.util.DateUtil;
import com.example.lzk.mobilehutubill.util.EmptyRecyclerView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class CategoryFragment extends Fragment {
    private View view, mEmptyView, mEmptyView2;
    private EmptyRecyclerView emptyRecyclerView, emptyRecyclerView2;
    private EditText spend, commt, addcategory;
    private TextView datet;
    private Spinner mySpinner;
    private FloatingActionButton fab;
    private CategoryFragmentCategoryRecyclerViewAdapter recycleAdapter;
    private CategoryFragmentRecordRecyclerViewAdapter recycleAdapter2;
    private CategorySpinnerAdapter categorySpinnerAdapter;
    private List<Category> listAll = new ArrayList<Category>();
    private List<Record> listAll2 = new ArrayList<Record>();
    private CategoryService categoryService;
    private RecordService recordService;
    private DatePickerDialog.OnDateSetListener onDateSetListener;
    private int mYear, mMonth, mDay;
    private int spinnercid,cid,cid3;//spinner选择事件属性id//分类数组位置//recyclerview点击事件中的属性id
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_category, container, false);
        Log.e("TEST", "3狗");
        initView();
        setListener();
        new Thread(new Runnable() {
            @Override
            public void run() {
                querycategorydatas();
            }
        }).start();

        //cid3 = listAll.get(0).getId();
        //queryrecorddatas(cid3);//默认显示第一个分类的记录
        return view;
    }
    public void refreshData(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                querycategorydatas();
                queryrecorddatas(cid3);
            }
        }).start();
    }
    public void refreshcategory(){
        RecordFragment recordFragment = (RecordFragment) getActivity().getSupportFragmentManager().findFragmentByTag("android:switcher:" + R.id.main_vpager + ":1");
        if (recordFragment != null) //可能没有实例化
        {
            if (recordFragment.getView() != null) {
                recordFragment.refreshData();//自定义方法更新
            }
        }
    }
    public void refreshrecord(){
        OverviewFragment overviewFragment = (OverviewFragment) getActivity().getSupportFragmentManager().findFragmentByTag("android:switcher:" + R.id.main_vpager + ":0");
        if (overviewFragment != null) //可能没有实例化
        {
            if (overviewFragment.getView() != null) {
                overviewFragment.refreshData();//自定义方法更新
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
    private void querycategorydatas() {
        List<Category> listTemp = categoryService.query();
        listAll.clear();
        if (recycleAdapter != null) {
            if (listTemp != null) {
                listAll.addAll(listTemp);
                List<Integer> list=new ArrayList<Integer>();
                for (int i=0;i<listAll.size();i++){
                    list.add(listAll.get(i).getId());
                }
                List<Integer> list2=new ArrayList<Integer>();
                for (int i=0;i<list.size();i++){
                    list2.add(recordService.count(list.get(i)));
                }
                for (int i=0;i<list2.size();i++){
                    listAll.get(i).setRecordNumber(list2.get(i));
                }
            }
            Message msg = Message.obtain();  //从全局池中返回一个message实例，避免多次创建message（如new Message）
            msg.what = 1;   //标志消息的标志
            handler.sendMessage(msg);
        }
    }

    private void queryrecorddatas(int postion) {
        List<Record> listTemp2 = recordService.querybycid(postion);
        listAll2.clear();
        if (recycleAdapter2 != null) {
            if (listTemp2 != null) {
                listAll2.addAll(listTemp2);
            }
            for (int i=0;i<listAll2.size();i++){
                listAll2.get(i).setCname(listAll.get(cid).getName());
            }
            Message msg = Message.obtain();  //从全局池中返回一个message实例，避免多次创建message（如new Message）
            msg.what = 2;   //标志消息的标志
            handler.sendMessage(msg);
        }
    }
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {      //判断标志位
                case 1:
                    recycleAdapter.notifyDataSetChanged();
                    break;
                case 2:
                    recycleAdapter2.notifyDataSetChanged();
                    break;
                case 3:
                    Toast.makeText(getActivity(),"对应分类下有记录，不能删除！",Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

    private void initView() {
        emptyRecyclerView = (EmptyRecyclerView) view.findViewById(R.id.emptyrecycler);
        mEmptyView = view.findViewById(R.id.id_empty_view);
        emptyRecyclerView2 = (EmptyRecyclerView) view.findViewById(R.id.emptyrecycler2);
        mEmptyView2 = view.findViewById(R.id.id_empty_view2);
        fab= (FloatingActionButton) view.findViewById(R.id.fab);
        categoryService = new CategoryService(getActivity());
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(OrientationHelper.VERTICAL);//设置为垂直布局，这也是默认的
        recycleAdapter = new CategoryFragmentCategoryRecyclerViewAdapter(getActivity(), listAll);
        emptyRecyclerView.setEmptyView(mEmptyView);
        emptyRecyclerView.setLayoutManager(layoutManager);//设置布局管理器
        emptyRecyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));//设置默认分隔线
        emptyRecyclerView.setItemAnimator(new DefaultItemAnimator()); //设置增加或删除条目的动画
        emptyRecyclerView.setAdapter(recycleAdapter);//设置Adapter
        recordService=new RecordService(getActivity());
        LinearLayoutManager layoutManager2 = new LinearLayoutManager(getActivity());
        layoutManager2.setOrientation(OrientationHelper.VERTICAL);//设置为垂直布局，这也是默认的
        recycleAdapter2 = new CategoryFragmentRecordRecyclerViewAdapter(getActivity(), listAll2);
        emptyRecyclerView2.setEmptyView(mEmptyView2);
        emptyRecyclerView2.setLayoutManager(layoutManager2);//设置布局管理器
        emptyRecyclerView2.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));//设置默认分隔线
        emptyRecyclerView2.setItemAnimator(new DefaultItemAnimator()); //设置增加或删除条目的动画
        emptyRecyclerView2.setAdapter(recycleAdapter2);//设置Adapter
    }

    private void setListener() {
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LayoutInflater layoutInflater = LayoutInflater.from(getActivity());//得到自定义对话框
                View DialogView = layoutInflater.inflate(R.layout.dialog_addcategory, null);
                addcategory = (EditText) DialogView.findViewById(R.id.addcategory);
                //创建对话框
                final AlertDialog dlg = new AlertDialog.Builder(getActivity())
                        .setTitle("输入框")
                        .setMessage("增加分类")
                        .setView(DialogView)//设置自定义对话框的样式
                        .setNegativeButton("取消", //设置“取消”按钮
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int whichButton) {
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
                                if(TextUtils.isEmpty(addcategory.getText().toString().trim())){
                                    //输入框内容为空时不调用dismiss
                                    Toast.makeText(getActivity(),"分类必须输入！",Toast.LENGTH_SHORT).show();
                                    return;
                                }
                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        categoryService.add(addcategory.getText().toString().trim());
                                        querycategorydatas();
                                        refreshcategory();
                                    }
                                }).start();
                                dlg.dismiss();
                            }
                        });
                    }
                });

                dlg.show();//显示
            }
        });

        recycleAdapter.setRecyclerViewOnItemClickListener(new CategoryFragmentCategoryRecyclerViewAdapter.RecyclerViewOnItemClickListener() {
            @Override
            public void onItemClickListener(View view, int position) {
                cid=position;
                cid3=listAll.get(position).getId();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        queryrecorddatas(cid3);
                    }
                }).start();
            }
        });

        recycleAdapter.setOnItemLongClickListener(new CategoryFragmentCategoryRecyclerViewAdapter.RecyclerViewOnItemLongClickListener() {
            @Override
            public boolean onItemLongClickListener(View view, final int position) {
                AlertDialog.Builder normalDialog =
                        new AlertDialog.Builder(getActivity());
                normalDialog.setTitle("删除分类").setMessage("你确定要删除吗？");
                normalDialog.setPositiveButton("删除",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        int i= recordService.count(listAll.get(position).getId());
                                        if (i==0){
                                            categoryService.delete(listAll.get(position).getId());
                                            querycategorydatas();
                                            refreshcategory();
                                        }else if (i!=0){
                                            Message msg = Message.obtain();  //从全局池中返回一个message实例，避免多次创建message（如new Message）
                                            msg.what = 3;   //标志消息的标志
                                            handler.sendMessage(msg);
                                        }
                                    }
                                }).start();
                            }
                        });
                normalDialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
                // 创建实例并显示
                normalDialog.show();
                return true;
            }
        });
        recycleAdapter2.setRecyclerViewOnItemClickListener(new CategoryFragmentRecordRecyclerViewAdapter.RecyclerViewOnItemClickListener() {
            @Override
            public void onItemClickListener(View view, final int position) {
                LayoutInflater layoutInflater = LayoutInflater.from(getActivity());//得到自定义对话框
                View DialogView = layoutInflater.inflate(R.layout.dialog_updatarecord, null);
                spend = (EditText) DialogView.findViewById(R.id.spend);
                commt = (EditText) DialogView.findViewById(R.id.commt);
                datet = (TextView) DialogView.findViewById(R.id.date);
                mySpinner = (Spinner) DialogView.findViewById(R.id.spinner2);
                categorySpinnerAdapter = new CategorySpinnerAdapter(getActivity(), listAll);
                spend.setText(listAll2.get(position).getSpend() + "");
                commt.setText(listAll2.get(position).getComment());
                datet.setText(DateUtil.longtostr(listAll2.get(position).getDate()));
                mySpinner.setAdapter(categorySpinnerAdapter);
                mySpinner.setSelection(cid);
                mySpinner.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
                    public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                        spinnercid=listAll.get(arg2).getId();
                        arg0.setVisibility(View.VISIBLE);
                    }

                    public void onNothingSelected(AdapterView<?> arg0) {
                        arg0.setVisibility(View.VISIBLE);
                    }
                });
                datet.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        new DatePickerDialog(getActivity(), onDateSetListener, mYear, mMonth, mDay).show();
                    }
                });

                //创建对话框
                final AlertDialog dlg = new AlertDialog.Builder(getActivity())
                        .setTitle("编辑框")
                        .setView(DialogView)//设置自定义对话框的样式
                        .setNegativeButton("取消", //设置“取消”按钮
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int whichButton) {
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
                                if(TextUtils.isEmpty(spend.getText().toString())||TextUtils.isEmpty(commt.getText().toString().trim())){
                                    //输入框内容为空时不调用dismiss
                                    Toast.makeText(getActivity(),"输入必须完整！",Toast.LENGTH_SHORT).show();
                                    return;
                                }
                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        recordService.update(listAll2.get(position).getId(), Double.parseDouble(((spend.getText().toString()))),spinnercid,commt.getText().toString(), DateUtil.strtolong(datet.getText().toString()));
                                        querycategorydatas();
                                        queryrecorddatas(cid3);
                                        refreshrecord();
                                    }
                                }).start();
                                dlg.dismiss();
                            }
                        });
                    }
                });
                dlg.show();//显示
            }
        });
        recycleAdapter2.setOnItemLongClickListener(new CategoryFragmentRecordRecyclerViewAdapter.RecyclerViewOnItemLongClickListener() {
            @Override
            public boolean onItemLongClickListener(View view, final int position) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("删除记录");
                builder.setMessage("你确定要删除吗？");
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                recordService.delete(listAll2.get(position).getId());
                                querycategorydatas();
                                queryrecorddatas(cid3);
                                refreshrecord();
                            }
                        }).start();
                    }
                });
                builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
                return true;
            }
        });

        Calendar ca = Calendar.getInstance();
        mYear = ca.get(Calendar.YEAR);
        mMonth = ca.get(Calendar.MONTH);
        mDay = ca.get(Calendar.DAY_OF_MONTH);
        onDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                mYear = year;
                mMonth = monthOfYear;
                mDay = dayOfMonth;
                String days;
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
                datet.setText(days);
            }
        };
    }
}
