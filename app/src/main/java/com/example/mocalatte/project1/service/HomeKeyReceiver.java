package com.example.mocalatte.project1.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

public class HomeKeyReceiver extends BroadcastReceiver {

    static final String SYSTEM_DIALOG_REASON_KEY = "reason";
    static final String SYSTEM_DIALOG_REASON_GLOBAL_ACTIONS = "globalactions";
    static final String SYSTEM_DIALOG_REASON_RECENT_APPS = "recentapps";
    static final String SYSTEM_DIALOG_REASON_HOME_KEY = "homekey";

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
        Log.e("",">>> Home Event");

        String action = intent.getAction();
        if (action.equals(Intent.ACTION_CLOSE_SYSTEM_DIALOGS)) {
            String reason = intent.getStringExtra(SYSTEM_DIALOG_REASON_KEY);
            if (reason != null) {
                if (reason.equals(SYSTEM_DIALOG_REASON_HOME_KEY)) {
                    Log.e("",">>> Home Clcik Event");
                    Toast.makeText(context, "홈키버튼이벤트!", Toast.LENGTH_SHORT).show();
                } else if (reason.equals(SYSTEM_DIALOG_REASON_RECENT_APPS)) {
                    Log.e("",">>> Home Long Press Event");
                }
            }
        }
        /*else if(action.equals(Intent.ACTION_BOOT_COMPLETED)){
            Toast.makeText(context, "부트이벤트!", Toast.LENGTH_SHORT).show();
        }*/

        //throw new UnsupportedOperationException("Not yet implemented");
    }
}
