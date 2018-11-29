package com.example.mocalatte.project1.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import com.example.mocalatte.project1.R;
import com.example.mocalatte.project1.adapter.FriendListAdapter;
import com.example.mocalatte.project1.item.FriendListMenu;
import com.example.mocalatte.project1.service.RealService;

import java.util.ArrayList;


public class HomeActivity extends Activity {

    //HomeKeyReceiver homeKeyReceiver;
    private Intent serviceIntent;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_layout);

        ArrayList<FriendListMenu> fff = new ArrayList<>();
        for(int i=0; i<5; i++)
            fff.add(new FriendListMenu("홍길동", i+""));

        ListView friendList = (ListView)findViewById(R.id.friendlist);
        FriendListAdapter friendListAdapter = new FriendListAdapter(this, fff);
        friendList.setAdapter(friendListAdapter);

        ImageButton requestbtn = (ImageButton)findViewById(R.id.requestbutton);
        requestbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        if (RealService.serviceIntent==null) {
            serviceIntent = new Intent(this, RealService.class);
            startService(serviceIntent);
        } else {
            serviceIntent = RealService.serviceIntent;//getInstance().getApplication();
            Toast.makeText(getApplicationContext(), "already RealService done", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (serviceIntent!=null) {
            stopService(serviceIntent);
            serviceIntent = null;
        }
    }

    /*@Override
    protected void onResume() {
        super.onResume();
        if(homeKeyReceiver == null){
            homeKeyReceiver = new HomeKeyReceiver();
            IntentFilter filter = new IntentFilter(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
            registerReceiver(homeKeyReceiver,filter);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.e("Home onPause", "-----");
        if(homeKeyReceiver != null){
            //unregisterReceiver(homeKeyReceiver);
            //homeKeyReceiver = null;
        }
    }*/

}