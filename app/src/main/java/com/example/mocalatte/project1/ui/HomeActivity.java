package com.example.mocalatte.project1.ui;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.Toast;

import com.example.mocalatte.project1.R;
import com.example.mocalatte.project1.adapter.DBManager;
import com.example.mocalatte.project1.adapter.FriendListAdapter;
import com.example.mocalatte.project1.item.FriendListMenu;
import com.example.mocalatte.project1.service.RealService;
import com.kakao.network.ErrorResult;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.LogoutResponseCallback;
import com.kakao.usermgmt.callback.UnLinkResponseCallback;
import com.kakao.util.helper.log.Logger;

import java.util.ArrayList;


public class HomeActivity extends Activity {

    //HomeKeyReceiver homeKeyReceiver;
    private Intent serviceIntent;

    private boolean mLocationPermissionGranted;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;

    ArrayList<FriendListMenu> friendListMenus;
    FriendListAdapter friendListAdapter;

    static final int PICK_CONTACT = 2;
    private String people_Number;
    private String people_Name;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_layout);

        Button btn_logout = (Button)findViewById(R.id.button_logout);
        btn_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UserManagement.getInstance().requestLogout(new LogoutResponseCallback() {
                    @Override
                    public void onCompleteLogout() {
                        onClickLogout();
                    }
                });
            }
        });

        Button btn_unlink = (Button)findViewById(R.id.button_unlink);
        btn_unlink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickUnlink();
            }
        });

        ArrayList<FriendListMenu> fff = new ArrayList<>();
        for(int i=0; i<5; i++)
            fff.add(new FriendListMenu("홍길동", i+""));

        ListView friendList = (ListView)findViewById(R.id.friendlist);
        friendList.setAdapter(friendListAdapter);

        //
        Button btnTel = (Button) findViewById(R.id.btn_tel);
        btnTel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent contact_picker = new Intent(Intent.ACTION_PICK);
                contact_picker.setType(ContactsContract.Contacts.CONTENT_TYPE);
                startActivityForResult(contact_picker, PICK_CONTACT);
            }
        });

        ImageButton requestbtn = (ImageButton)findViewById(R.id.requestbutton);
        requestbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences sp = getSharedPreferences("login", MODE_PRIVATE);
                boolean state = sp.getBoolean("home_request_ok", false);
                sp.edit().putBoolean("home_request_ok", !state).commit();
                initRequestBtnState();

                if(!state == true){
                    Toast.makeText(HomeActivity.this, "앞으로 홈버튼을 7번 연속으로 클릭하시면 위험 알림을 발송하게 됩니다.", Toast.LENGTH_SHORT).show();
                }
                else{
                    Toast.makeText(HomeActivity.this, "홈버튼을 통한 위험 알림 발송기능을 중지합니다.", Toast.LENGTH_SHORT).show();
                }
            }
        });
        initRequestBtnState();

        final Switch push_switch = (Switch)findViewById(R.id.push_switch);
        push_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                SharedPreferences sp = getSharedPreferences("login", MODE_PRIVATE);
                //boolean state = sp.getBoolean("push_switch", true);
                sp.edit().putBoolean("push_switch", push_switch.isChecked()).commit();
                initPushSwitchState();

                if(push_switch.isChecked()){
                    Toast.makeText(HomeActivity.this, "다른사람의 위험 요청 푸시를 수신합니다.", Toast.LENGTH_SHORT).show();
                }
                else{
                    Toast.makeText(HomeActivity.this, "다른사람의 위험 요청 푸시를 수신하지 않습니다.", Toast.LENGTH_SHORT).show();
                }
            }
        });
        initPushSwitchState();

        //

        getLocationPermission();

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

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == PICK_CONTACT && resultCode == RESULT_OK){
            Log.d("INNOVER", "request OK_PICKCONTACT");

            Uri dataUri = data.getData();
            Cursor cursor = managedQuery(dataUri, null, null, null, null);

            while (cursor.moveToNext()) {
                int getcolumnId = cursor.getColumnIndex(ContactsContract.Contacts._ID);

                String id = cursor.getString(getcolumnId);
                people_Name = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.Contacts.DISPLAY_NAME));
                String hasPhoneNumber = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER));

                if(hasPhoneNumber.equalsIgnoreCase("1")) {
                    hasPhoneNumber = "true";
                }else {
                    hasPhoneNumber = "false";
                }

                if(Boolean.parseBoolean(hasPhoneNumber)) {
                    Cursor phones = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                            null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID+" = "+id, null, null);
                    while(phones.moveToNext()) {
                        people_Number = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));

                    }
                    phones.close();	//	End
                }
                Log.d("test", "name: "+people_Name);
                Log.d("test", "number: "+people_Number);
                startActivity(new Intent("android.intent.action.CALL", Uri.parse("tel:"+people_Number)));
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    // 홈버튼 연속 클릭으로 자신의 위험을 알릴 것인지 아닌지를 결정하는 버튼 설정..
    // 현재 SharedPreferences에 저장된 값에 따라 on/off 상태를 표시함.
    public void initRequestBtnState(){
        SharedPreferences sp = getSharedPreferences("login", MODE_PRIVATE);
        boolean state = sp.getBoolean("home_request_ok", false);
        ImageButton requestbtn = (ImageButton)findViewById(R.id.requestbutton);

        if(state == true){
            Log.e("home_request_ok : ", "true");
            requestbtn.setImageResource(R.drawable.on);
            sp.edit().putBoolean("home_request_ok", true).commit();
        }
        else{
            Log.e("home_request_ok : ", "false");
            requestbtn.setImageResource(R.drawable.off);
            sp.edit().putBoolean("home_request_ok", false).commit();
        }
    }
    // 위험 요청 수신여부 Switch 설정..
    public void initPushSwitchState(){
        SharedPreferences sp = getSharedPreferences("login", MODE_PRIVATE);
        boolean state = sp.getBoolean("push_switch", true);
        Switch push_switch = (Switch)findViewById(R.id.push_switch);

        if(state == true){
            Log.e("push_switch : ", "true");
            push_switch.setChecked(true);
            sp.edit().putBoolean("push_switch", true).commit();
        }
        else{
            Log.e("push_switch : ", "false");
            push_switch.setChecked(false);
            sp.edit().putBoolean("push_switch", false).commit();
        }
    }

    // Sqlite로부터 SMS보낼 연락처 목록을 세팅함
    public void initFriendList(){
        friendListMenus.clear();

        DBManager dbManager = new DBManager(this);
        SQLiteDatabase db = dbManager.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT name, phone FROM " +
                dbManager.ContactTB, null);


        while (cursor.moveToNext()) {
            friendListMenus.add(
                    new FriendListMenu(
                            cursor.getString(0) // name
                            , cursor.getString(1)   // phone
                    )
            );
        }
        cursor.close();
        db.close();
        friendListAdapter.notifyDataSetChanged();

    }
    /**
     * Prompts the user for permission to use the device location.
     */
    private boolean getLocationPermission() {
        /*
         * Request location permission, so that we can get the location of the
         * device. The result of the permission request is handled by a callback,
         * onRequestPermissionsResult.
         */
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
        } else {
            mLocationPermissionGranted = false;
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }

        return mLocationPermissionGranted;
    }

    /**
     * Handles the result of the request for location permissions.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        mLocationPermissionGranted = false;
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mLocationPermissionGranted = true;

                    //// 여기서 현재 위치 GPS 가져오기
                    //getDeviceLocation();
                }
                else{
                    Toast.makeText(this, "위치 권한을 허용하셔야 합니다.", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        }
        //updateLocationData();
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

    // 로그아웃
    private void onClickLogout() {
        UserManagement.getInstance().requestLogout(new LogoutResponseCallback() {
            @Override
            public void onCompleteLogout() {
                GlobalApplication.redirectLoginActivity(HomeActivity.this);
            }
        });
    }

    // 탈퇴
    private void onClickUnlink() {
        final String appendMessage = getString(R.string.com_kakao_confirm_unlink);
        new AlertDialog.Builder(this)
                .setMessage(appendMessage)
                .setPositiveButton(getString(R.string.com_kakao_ok_button),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                UserManagement.getInstance().requestUnlink(new UnLinkResponseCallback() {
                                    @Override
                                    public void onFailure(ErrorResult errorResult) {
                                        Logger.e(errorResult.toString());
                                    }

                                    @Override
                                    public void onSessionClosed(ErrorResult errorResult) {
                                        GlobalApplication.redirectLoginActivity(HomeActivity.this);
                                    }

                                    @Override
                                    public void onNotSignedUp() {
                                        //redirectSignupActivity();
                                        GlobalApplication.redirectLoginActivity(HomeActivity.this);
                                    }

                                    @Override
                                    public void onSuccess(Long userId) {
                                        GlobalApplication.redirectLoginActivity(HomeActivity.this);
                                    }
                                });
                                dialog.dismiss();
                            }
                        })
                .setNegativeButton(getString(R.string.com_kakao_cancel_button),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).show();

    }

}