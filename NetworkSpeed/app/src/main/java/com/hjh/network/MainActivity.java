package com.hjh.network;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    /**
     * Activity管理器
     */
    private ActivityManager activityManager;
    /**
     * 浮动图标
     */
    private TextView moveDesc;
    /**
     * 消息处理器
     */
    private Handler handler;
    /**
     * 流量信息对象
     */
    private TrafficBean trafficBean;
    /**
     * 服务
     */
    private ManagerService service;
    /**
     * 标记信息
     */
    private static final String TAG = "MainActivity";



    private TextView netWork;
    private Button btnStart;
    private Intent intent ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (Build.VERSION.SDK_INT >= 21) {
            View decorView = getWindow().getDecorView();
            int option = View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
            decorView.setSystemUiVisibility(option);
            getWindow().setNavigationBarColor(Color.TRANSPARENT);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        intent = new Intent(MainActivity.this, ManagerService.class);
        openNetWork();
        initView();

        Log.d(TAG, "总流量 = " + trafficBean.getTrafficInfo());

    }


    private void initView() {
        btnStart = (Button) findViewById(R.id.btn_start);
        btnStart.setOnClickListener(this);

        bindService(intent, conn, BIND_AUTO_CREATE);
        btnStart.setText("关闭流量监控");
        Toast.makeText(this, "已成功开启流量检测功能,如需关闭应用进程即可~", Toast.LENGTH_SHORT).show();
    }

    private void openNetWork() {
        netWork = (TextView) findViewById(R.id.tv_network);

        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == 1) {
                    netWork.setText(String.valueOf(msg.obj));
                }
                super.handleMessage(msg);
            }
        };
        trafficBean = new TrafficBean(12580, handler, this);
        trafficBean.startCalculateNetSpeed();
    }

    private ServiceConnection conn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder iBinder) {
            service = ((ManagerService.ServiceBinder) iBinder).getService();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            service = null;
        }
    };


    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.btn_start:

                if(btnStart.getText().toString().equals("开启流量监控")){
                    bindService(intent, conn, BIND_AUTO_CREATE);
                    trafficBean.startCalculateNetSpeed();

                    btnStart.setText("关闭流量监控");

                    Toast.makeText(this, "流量监控已成功启动~", Toast.LENGTH_SHORT).show();
                }else if(btnStart.getText().toString().equals("关闭流量监控")){
                    unbindService(conn);
                    trafficBean.stopCalulateNetSpeed();
                    netWork.setText("");
                    btnStart.setText("开启流量监控");

                    Toast.makeText(this, "流量监控已成功关闭~", Toast.LENGTH_SHORT).show();
                }

                break;

        }
    }
}
