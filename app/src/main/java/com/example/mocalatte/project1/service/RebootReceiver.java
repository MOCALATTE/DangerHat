package com.example.mocalatte.project1.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;

public class RebootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
        //throw new UnsupportedOperationException("Not yet implemented");

        SharedPreferences sp = context.getSharedPreferences("login", context.MODE_PRIVATE);
        long storedid = sp.getLong("id", -1);
        // 로그인되어있는 상태라면 동작 수행..
        if(storedid != -1){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                Intent in = new Intent(context, RestartService.class);
                context.startForegroundService(in);
            } else {
                Intent in = new Intent(context, RealService.class);
                context.startService(in);
            }
        }

    }
}
