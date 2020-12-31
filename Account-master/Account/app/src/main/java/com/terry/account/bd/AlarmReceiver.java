package com.terry.account.bd;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

import com.terry.account.R;

public class AlarmReceiver  extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if("com.smile.account.alarm".equals(intent.getAction())){
            final String CHANNEL_ID = "channel_id_1";
            final String CHANNEL_NAME = "channel_name_1";
            NotificationManager mNotificationManager = (NotificationManager)
                    context.getSystemService(Context.NOTIFICATION_SERVICE);
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                //只在Android O之上需要渠道
                NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID,
                        CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH);
                //如果这里用IMPORTANCE_NOENE就需要在系统的设置里面开启渠道，
                //通知才能正常弹出
                mNotificationManager.createNotificationChannel(notificationChannel);
            }
            NotificationCompat.Builder builder= new NotificationCompat.Builder(context,CHANNEL_ID);
            builder.setSmallIcon(R.mipmap.ic_launcher)
                    .setContentTitle(context.getResources().getString(R.string.app_name))
                    .setContentText("到每天的记账时间啦")
                    .setAutoCancel(true);
            mNotificationManager.notify(0x103, builder.build());

        }
    }
}