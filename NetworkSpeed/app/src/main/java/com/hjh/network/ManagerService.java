package com.hjh.network;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v7.app.NotificationCompat;

public class ManagerService extends Service {

    private ServiceBinder binder = new ServiceBinder();
    private NotificationCompat.Builder mBuilder;
    private Handler handler;
    private TrafficBean trafficBean;




    @Override
    public void onCreate() {
        super.onCreate();
        // 定义Notification的各种属性
       /* Notification notification =new Notification(R.drawable.cloud,
                "hhhhhh", System.currentTimeMillis());
        notification.flags |= Notification.FLAG_ONGOING_EVENT; // 将此通知放到通知栏的Ongoing即正在运行组中
        notification.flags |= Notification.FLAG_NO_CLEAR; // 表明在点击了通知栏中的清除通知后，此通知不清除，经常与FLAG_ONGOING_EVENT一起使用
        notification.flags |= Notification.FLAG_SHOW_LIGHTS;
        notification.defaults = Notification.DEFAULT_LIGHTS;
        notification.ledARGB = Color.BLUE;
        notification.ledOnMS =5000;*/
        initView();
    }

    /**
     * 主界面显示网速
     */
    private void initView() {

        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == 1) {
                    setDesktop(String.valueOf(msg.obj));

                }
                super.handleMessage(msg);
            }
        };
        trafficBean = new TrafficBean(12580, handler, this);
        trafficBean.startCalculateNetSpeed();

    }




    //第一种
    public void setDesktop(String str) {
        mBuilder = (NotificationCompat.Builder) new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.cloud)
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setVisibility(android.support.v4.app.NotificationCompat.VISIBILITY_PUBLIC)
                .setContentText(str);
// Creates an explicit intent for an Activity in your app
        Intent resultIntent = new Intent(this, MainActivity.class);

// The stack builder object will contain an artificial back stack for the
// started Activity.
// This ensures that navigating backward from the Activity leads out of
// your application to the Home screen.
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
// Adds the back stack for the Intent (but not the Intent itself)

        stackBuilder.addParentStack(MainActivity.class);
// Adds the Intent that starts the Activity to the top of the stack
        stackBuilder.addNextIntent(resultIntent);

        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        mBuilder.setContentIntent(resultPendingIntent);
        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
// mId allows you to update the notification later on.
        mNotificationManager.notify(1, mBuilder.build());


    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        startService(new Intent(this, ManagerService.class));
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return binder;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY_COMPATIBILITY;
    }

    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);
    }


    public class ServiceBinder extends Binder {
        public ManagerService getService() {
            return ManagerService.this;
        }
    }
}
