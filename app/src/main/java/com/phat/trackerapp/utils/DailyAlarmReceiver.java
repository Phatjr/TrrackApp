package com.phat.trackerapp.utils;

import android.app.Notification.Builder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;


public class DailyAlarmReceiver extends BroadcastReceiver {
    public static int MID = 1001;
    public static String NOTIFICATION_CHANNEL_ID = "dailyNotificationCountdown";
    int code = 0;
    Builder mNotifyBuilder;

    public void onReceive(Context context, Intent intent) {
        this.code = intent.getIntExtra("alarmRequestCode", 0);
        int time = intent.getIntExtra("alarmTime", 0);
        int typeAlarm = intent.getIntExtra("typeAlarm", 0);
        Log.d("222222", typeAlarm+" ");
        int i = this.code;

//        if (i == 112) {
//            Log.d("999999999999tttttt", "receiver time");
//            if (Utils.INSTANCE.isShowNotification(context, time)) {
//                NOTIFICATION_CHANNEL_ID = NOTIFICATION_CHANNEL_ID + typeAlarm;
//                setNotification(context,typeAlarm);
//            }
//            Utils.INSTANCE.reminder(context);
//        }
    }

//    private String getTitleForNoti(Context context, int position) {
//        switch (position) {
//            case 1:
//                return context.getString(R.string.txt_heart_rate);
//            case 2:
//                return context.getString(R.string.txt_blood_sugar);
//            case 3:
//                return context.getString(R.string.txt_weight_and_bmi);
//            default:
//                return context.getString(R.string.txt_blood_pressure);
//        }
//    }
//
//    @SuppressLint("WrongConstant")
//    public void setNotification(Context context, int typeAlarm) {
//        NotificationManager notificationManager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
//        Intent intent = new Intent(context, SplashActivity.class);
//        intent.setFlags(67108864);
//        @SuppressLint("WrongConstant") PendingIntent activity = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE | 134217728);
//        String str = context.getString(R.string.txt_content_reminder);
//        String title = context.getString(R.string.txt_alarm_for) +" "+ getTitleForNoti(context, typeAlarm);
//        if (VERSION.SDK_INT >= 26) {
//            NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, "DailyNotification", 2);
//            notificationChannel.setLightColor(-16776961);
//            notificationChannel.setLockscreenVisibility(1);
//            notificationManager.createNotificationChannel(notificationChannel);
//            this.mNotifyBuilder = new Builder(context, NOTIFICATION_CHANNEL_ID).setSmallIcon(R.drawable.logo).setContentTitle(title).setContentText(str).setAutoCancel(true).setContentIntent(activity).setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher_round));
//        } else {
//            this.mNotifyBuilder = new Builder(context).setSmallIcon(R.drawable.logo).setContentTitle(title).setContentText(str).setPriority(1).setDefaults(2).setAutoCancel(true).setContentIntent(activity).setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher_round));
//        }
//        notificationManager.notify(MID, this.mNotifyBuilder.build());
//    }

//    public void setNotification2(Context context) {
//        @SuppressLint("RemoteViewLayout") final RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.test_notifi);
//
//// build notification
//        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context)
//                .setSmallIcon(R.mipmap.ic_launcher_round)
//                .setContentTitle("Content Title")
//                .setContentText("Content Text")
//                .setContent(remoteViews)
//                .setPriority(NotificationCompat.PRIORITY_MIN);
//
//        final Notification notification = mBuilder.build();
//
//// set big content view for newer androids
//        if (VERSION.SDK_INT >= 16) {
//            notification.bigContentView = remoteViews;
//        }
//
//        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
//        mNotificationManager.notify(11, notification);
//    }
}
