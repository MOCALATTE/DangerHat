package com.example.mocalatte.project1.service;

import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;

import com.example.mocalatte.project1.R;

public class RestartService extends Service {
    public RestartService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "default");
        builder.setSmallIcon(R.mipmap.ic_launcher);
        // ~~ 생략 ~~
        Notification notification = builder.build();
        startForeground(9, notification);

        Intent in = new Intent(this, RealService.class);
        startService(in);

        stopForeground(true);
        stopSelf();

        return START_NOT_STICKY;//super.onStartCommand(intent, flags, startId);
    }
}
