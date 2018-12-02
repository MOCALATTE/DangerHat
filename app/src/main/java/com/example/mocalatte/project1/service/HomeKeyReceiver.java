package com.example.mocalatte.project1.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import com.example.mocalatte.project1.ui.GlobalApplication;

public class HomeKeyReceiver extends BroadcastReceiver {

    static final String SYSTEM_DIALOG_REASON_KEY = "reason";
    static final String SYSTEM_DIALOG_REASON_GLOBAL_ACTIONS = "globalactions";
    static final String SYSTEM_DIALOG_REASON_RECENT_APPS = "recentapps";
    static final String SYSTEM_DIALOG_REASON_HOME_KEY = "homekey";

    private HomePressActionHandler homePressActionHandler;

    public HomeKeyReceiver() {
        super();
        homePressActionHandler = new HomePressActionHandler(GlobalApplication.context);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
        SharedPreferences sp = context.getSharedPreferences("login", context.MODE_PRIVATE);
        boolean state = sp.getBoolean("home_request_ok", false);
        if(state == true){
            String action = intent.getAction();
            if (action.equals(Intent.ACTION_CLOSE_SYSTEM_DIALOGS)) {
                String reason = intent.getStringExtra(SYSTEM_DIALOG_REASON_KEY);
                if (reason != null) {
                    if (reason.equals(SYSTEM_DIALOG_REASON_HOME_KEY)) {
                        Log.e("",">>> Home Clcik Event");
                        //Toast.makeText(context, "홈키버튼이벤트!", Toast.LENGTH_SHORT).show();
                        homePressActionHandler.onHomePressed();
                    } else if (reason.equals(SYSTEM_DIALOG_REASON_RECENT_APPS)) {
                        Log.e("",">>> Home Long Press Event");
                    }
                }
            }
        }
        else{
            Log.e("",">>> Home Event denied");
        }

        //throw new UnsupportedOperationException("Not yet implemented");
    }
}
