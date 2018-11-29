package com.example.mocalatte.project1.ui;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListView;

import com.example.mocalatte.project1.R;
import com.example.mocalatte.project1.adapter.FriendListAdapter;
import com.example.mocalatte.project1.item.FriendListMenu;

import java.util.ArrayList;


public class HomeActivity extends Activity {


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
    }
}