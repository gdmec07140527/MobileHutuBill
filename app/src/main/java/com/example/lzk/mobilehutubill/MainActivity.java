package com.example.lzk.mobilehutubill;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lzk.mobilehutubill.adapter.FragmentPagerAdapter;
import com.example.lzk.mobilehutubill.fragment.OverviewFragment;
import com.example.lzk.mobilehutubill.service.ConfigService;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by Coder-pig on 2015/8/28 0028.
 */
public class MainActivity extends AppCompatActivity implements RadioGroup.OnCheckedChangeListener, ViewPager.OnPageChangeListener {

    //UI Objects
    private RadioGroup rg_bottom_bar;
    private RadioButton rb_overview;
    private RadioButton rb_record;
    private RadioButton rb_category;
    private RadioButton rb_charts;
    private ViewPager main_vpager;
    private ListView listView;
    private DrawerLayout drawerLayout;

    private FragmentPagerAdapter mAdapter;
    //几个代表页面的常量
    public static final int PAGE_ONE = 0;
    public static final int PAGE_TWO = 1;
    public static final int PAGE_THREE = 2;
    public static final int PAGE_FOUR = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        initDate();
        checkPermission();
    }

    private void initView() {
        rg_bottom_bar = (RadioGroup) findViewById(R.id.rg_bottom_bar);
        rb_overview = (RadioButton) findViewById(R.id.rb_overview);
        rb_record = (RadioButton) findViewById(R.id.rb_record);
        rb_category = (RadioButton) findViewById(R.id.rb_category);
        rb_charts = (RadioButton) findViewById(R.id.rb_charts);
        main_vpager = (ViewPager) findViewById(R.id.main_vpager);
        listView = (ListView) findViewById(R.id.v4_listview);
        drawerLayout = (DrawerLayout) findViewById(R.id.v4_drawerlayout);
        rg_bottom_bar.setOnCheckedChangeListener(this);
        rb_overview.setChecked(true);
        mAdapter = new FragmentPagerAdapter(getSupportFragmentManager());
        main_vpager.setAdapter(mAdapter);
        main_vpager.setCurrentItem(0);
        main_vpager.addOnPageChangeListener(this);
        main_vpager.setOffscreenPageLimit(3);
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        switch (checkedId) {
            case R.id.rb_overview:
                main_vpager.setCurrentItem(PAGE_ONE);
                break;
            case R.id.rb_record:
                main_vpager.setCurrentItem(PAGE_TWO);
                break;
            case R.id.rb_category:
                main_vpager.setCurrentItem(PAGE_THREE);
                break;
            case R.id.rb_charts:
                main_vpager.setCurrentItem(PAGE_FOUR);
                break;
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        /*当页面在滑动的时候会调用此方法，在滑动被停止之前，此方法回一直得到调用。其中三个参数的含义分别为：
        position :当前页面，即你点击滑动的页面（从A滑B，则是A页面的position。官方说明：Position index of the first page currently being displayed. Page position+1 will be visible if positionOffset is nonzero.）
        positionOffset:当前页面偏移的百分比
        positionOffsetPixels:当前页面偏移的像素位置*/
    }

    @Override
    public void onPageSelected(int position) {
        //此方法是页面跳转完后得到调用，position是你当前选中的页面的Position（位置编号）(从A滑动到B，就是B的position)
    }

    @Override
    public void onPageScrollStateChanged(int state) {
        /*此方法是在状态改变的时候调用，其中state这个参数有三种状态：
        SCROLL_STATE_DRAGGING（1）表示用户手指“按在屏幕上并且开始拖动”的状态（手指按下但是还没有拖动的时候还不是这个状态，只有按下并且手指开始拖动后log才打出。）
        SCROLL_STATE_IDLE（0）滑动动画做完的状态。
        SCROLL_STATE_SETTLING（2）在“手指离开屏幕”的状态。
        一个完整的滑动动作，三种状态的出发顺序为（1，2，0）*/
        //state的状态有三个，0表示什么都没做，1正在滑动，2滑动完毕
        if (state == 2) {
            switch (main_vpager.getCurrentItem()) {
                case PAGE_ONE:
                    rb_overview.setChecked(true);
                    break;
                case PAGE_TWO:
                    rb_record.setChecked(true);
                    break;
                case PAGE_THREE:
                    rb_category.setChecked(true);
                    break;
                case PAGE_FOUR:
                    rb_charts.setChecked(true);
                    break;
            }
        }
    }

    private void initDate() {
        List<String> list = new ArrayList<String>();
        list.add("设置预算");
        list.add("备份");
        list.add("恢复");
        list.add("关于应用");
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, list);
        listView.setAdapter(adapter);
        setOnClickListener();
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {      //判断标志位
                case 1:
                    Toast.makeText(MainActivity.this,"备份成功！数据库已备份到sd卡根目录！",Toast.LENGTH_SHORT).show();
                    break;
                case 2:
                    Toast.makeText(MainActivity.this,"恢复成功！数据库已从sd卡根目录恢复！",Toast.LENGTH_SHORT).show();
                    Intent intent = getBaseContext().getPackageManager()
                            .getLaunchIntentForPackage(getBaseContext().getPackageName());
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    break;
                case 3:
                    Toast.makeText(MainActivity.this,"备份失败！请检查权限或存储空间是否已满！",Toast.LENGTH_SHORT).show();
                    break;
                case 4:
                    Toast.makeText(MainActivity.this,"恢复失败！请检查权限或备份文件是否存在！",Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };
    private void setOnClickListener() {
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                showDrawerLayout();
                if (position == 0) {
                    LayoutInflater layoutInflater = LayoutInflater.from(MainActivity.this);//得到自定义对话框
                    View DialogView = layoutInflater.inflate(R.layout.dialog_addbudget, null);
                    final EditText firstyusuan = (EditText) DialogView.findViewById(R.id.addtext);
                    //创建对话框
                    final AlertDialog dlg = new AlertDialog.Builder(MainActivity.this)
                            .setTitle("输入框")
                            .setMessage("设置预算")
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
                                    if (TextUtils.isEmpty(firstyusuan.getText().toString())) {
                                        //输入框内容为空时不调用dismiss
                                        Toast.makeText(MainActivity.this, "预算必须输入！", Toast.LENGTH_SHORT).show();
                                        return;
                                    }
                                    new Thread(new Runnable() {
                                        @Override
                                        public void run() {
                                            ConfigService configService = new ConfigService(MainActivity.this);
                                            configService.update(1, "budget", firstyusuan.getText().toString());
                                            OverviewFragment overviewFragment = (OverviewFragment) MainActivity.this.getSupportFragmentManager().findFragmentByTag("android:switcher:" + R.id.main_vpager + ":0");
                                            if (overviewFragment != null) //可能没有实例化
                                            {
                                                if (overviewFragment.getView() != null) {
                                                    overviewFragment.refreshData();//自定义方法更新
                                                }
                                            }
                                        }
                                    }).start();
                                    dlg.dismiss();
                                }
                            });
                        }
                    });
                    dlg.show();//显示
                } else if (position == 1) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            copyDataBaseToSD();
                        }
                    }).start();
                }else if (position == 2) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            copySDToDataBase();
                        }
                    }).start();
                }else if (position==3){
                    LayoutInflater layoutInflater = LayoutInflater.from(MainActivity.this);//得到自定义对话框
                    View DialogView = layoutInflater.inflate(R.layout.dialog_about, null);
                    final TextView textView3  = (TextView) DialogView.findViewById(R.id.textView3);
                    final TextView textView4  = (TextView) DialogView.findViewById(R.id.textView4);
                    final TextView textView5  = (TextView) DialogView.findViewById(R.id.textView5);
                    final TextView textView6  = (TextView) DialogView.findViewById(R.id.textView6);
                    final TextView textView7  = (TextView) DialogView.findViewById(R.id.textView7);
                    textView3.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            openinternet(textView3.getText().toString());
                        }
                    });
                    textView4.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            openinternet(textView4.getText().toString());
                        }
                    });
                    textView5.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            openinternet(textView5.getText().toString());
                        }
                    });
                    textView6.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            openinternet(textView6.getText().toString());
                        }
                    });
                    textView7.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            openinternet(textView7.getText().toString());
                        }
                    });
                    //创建对话框
                    final AlertDialog dlg = new AlertDialog.Builder(MainActivity.this)
                            .setView(DialogView)//设置自定义对话框的样式
                            .setPositiveButton("确定", //设置“取消”按钮
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int whichButton) {
                                        }
                                    })
                            .create();//创建弹出框
                    dlg.show();//显示
                }
            }
        });
    }
    private void openinternet(String s){
        Intent intent = new Intent();
        intent.setAction("android.intent.action.VIEW");
        Uri content_url = Uri.parse(s);
        intent.setData(content_url);
        startActivity(intent);
    }
    private void showDrawerLayout() {
        if (!drawerLayout.isDrawerOpen(Gravity.LEFT)) {
            drawerLayout.openDrawer(Gravity.LEFT);
        } else {
            drawerLayout.closeDrawer(Gravity.LEFT);
        }
    }

    public void copyDataBaseToSD() {
        if (!Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            return;
        }
        File dbFile = new File(MainActivity.this.getApplication().getDatabasePath("hutubill") + ".db");
        File file = new File(Environment.getExternalStorageDirectory(), "hutubill.db");
        FileChannel inChannel = null, outChannel = null;
        try {
            file.createNewFile();
            inChannel = new FileInputStream(dbFile).getChannel();
            outChannel = new FileOutputStream(file).getChannel();
            inChannel.transferTo(0, inChannel.size(), outChannel);
            Message msg = Message.obtain();  //从全局池中返回一个message实例，避免多次创建message（如new Message）
            msg.what = 1;   //标志消息的标志
            handler.sendMessage(msg);
        } catch (Exception e) {
            Log.e("TAG", "copy dataBase to SD error.");
            Message msg = Message.obtain();  //从全局池中返回一个message实例，避免多次创建message（如new Message）
            msg.what = 3;   //标志消息的标志
            handler.sendMessage(msg);
            e.printStackTrace();
        } finally {
            try {
                if (inChannel != null) {
                    inChannel.close();
                    inChannel = null;
                }
                if (outChannel != null) {
                    outChannel.close();
                    outChannel = null;
                }
            } catch (IOException e) {
                Log.e("TAG", "file close error.");
                e.printStackTrace();
            }
        }
    }
    public void copySDToDataBase() {
        if (!Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            return;
        }
        File dbFile = new File(MainActivity.this.getApplication().getDatabasePath("hutubill") + ".db");
        File file = new File(Environment.getExternalStorageDirectory(), "hutubill.db");
        FileChannel inChannel = null, outChannel = null;
        try {
            file.createNewFile();
            inChannel = new FileInputStream(file).getChannel();
            outChannel = new FileOutputStream(dbFile).getChannel();
            inChannel.transferTo(0, inChannel.size(), outChannel);
            Message msg = Message.obtain();  //从全局池中返回一个message实例，避免多次创建message（如new Message）
            msg.what = 2;   //标志消息的标志
            handler.sendMessage(msg);
        } catch (Exception e) {
            Log.e("TAG", "copy SD to dataBase error.");
            Message msg = Message.obtain();  //从全局池中返回一个message实例，避免多次创建message（如new Message）
            msg.what = 4;   //标志消息的标志
            handler.sendMessage(msg);
            e.printStackTrace();
        } finally {
            try {
                if (inChannel != null) {
                    inChannel.close();
                    inChannel = null;
                }
                if (outChannel != null) {
                    outChannel.close();
                    outChannel = null;
                }
            } catch (IOException e) {
                Log.e("TAG", "file close error.");
                e.printStackTrace();
            }
        }
    }
    //声明一个long类型变量：用于存放上一点击“返回键”的时刻
    private long mExitTime;
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        //判断用户是否点击了“返回键”
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            //与上次点击返回键时刻作差
            if ((System.currentTimeMillis() - mExitTime) > 2000) {
                //大于2000ms则认为是误操作，使用Toast进行提示
                Toast.makeText(this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
                //并记录下本次点击“返回键”的时刻，以便下次进行判断
                mExitTime = System.currentTimeMillis();
            } else {
                //小于2000ms则认为是用户确实希望退出程序-调用System.exit()方法进行退出
                finish();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }


    /**
     * 点击按钮，将数据库备份保存到外部存储器备。
     *
     * 需要3个权限(都是危险权限):
     *      1. 读取外部存储器权限;
     *      2. 写入外部存储器权限.
     */
    private static final int MY_PERMISSION_REQUEST_CODE = 10000;
    public void checkPermission() {
        /**
         * 第 1 步: 检查是否有相应的权限
         */
        boolean isAllGranted = checkPermissionAllGranted(
                new String[] {
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                }
        );
        // 如果这2个权限全都拥有, 则直接执行备份代码
        if (isAllGranted) {
            //doBackup();
            return;
        }

        /**
         * 第 2 步: 请求权限
         */
        // 一次请求多个权限, 如果其他有权限是已经授予的将会自动忽略掉
        ActivityCompat.requestPermissions(
                this,
                new String[] {
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                },
                MY_PERMISSION_REQUEST_CODE
        );
    }

    /**
     * 检查是否拥有指定的所有权限
     */
    private boolean checkPermissionAllGranted(String[] permissions) {
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                // 只要有一个权限没有被授予, 则直接返回 false
                return false;
            }
        }
        return true;
    }

    /**
     * 第 3 步: 申请权限结果返回处理
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == MY_PERMISSION_REQUEST_CODE) {
            boolean isAllGranted = true;

            // 判断是否所有的权限都已经授予了
            for (int grant : grantResults) {
                if (grant != PackageManager.PERMISSION_GRANTED) {
                    isAllGranted = false;
                    break;
                }
            }

            if (isAllGranted) {
                // 如果所有的权限都授予了, 则执行备份代码
                //doBackup();

            } else {
                // 弹出对话框告诉用户需要权限的原因, 并引导用户去应用权限管理中手动打开权限按钮
                openAppDetails();
            }
        }
    }

    /**
     * 第 4 步: 备份数据库操作
     */
    /*private void doBackup() {
        // 本文主旨是讲解如果动态申请权限, 具体备份代码不再展示, 就假装备份一下
        Toast.makeText(this, "正在备份数据库...", Toast.LENGTH_SHORT).show();
    }*/

    /**
     * 打开 APP 的详情设置
     */
    private void openAppDetails() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("备份数据库需要访问 “外部存储器”，请到 “应用信息 -> 权限” 中授予！");
        builder.setPositiveButton("去手动授权", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                intent.addCategory(Intent.CATEGORY_DEFAULT);
                intent.setData(Uri.parse("package:" + getPackageName()));
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                intent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
                startActivity(intent);
                finish();
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        builder.show();
    }
}
