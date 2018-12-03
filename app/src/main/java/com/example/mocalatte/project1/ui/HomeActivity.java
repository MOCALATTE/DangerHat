package com.example.mocalatte.project1.ui;

import android.Manifest;
import android.app.Activity;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.telephony.SmsManager;
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
import com.example.mocalatte.project1.adapter.SosListAdapter;
import com.example.mocalatte.project1.item.ContactItem;
import com.example.mocalatte.project1.item.SosItem;
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

    final int PERMISSION = 1;
    static final int PICK_CONTACT = 2;
    private String people_Number;
    private String people_Name;

    ArrayList<ContactItem> ContactItemList;
    FriendListAdapter friendListAdapter;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_layout);

        Button btn_logout = (Button) findViewById(R.id.button_logout);
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

        Button btn_unlink = (Button) findViewById(R.id.button_unlink);
        btn_unlink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickUnlink();
            }
        });

        Button btnSms = (Button) findViewById(R.id.btn_sms);
        btnSms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ContactItemList != null) {
                    String[] phoneNum = new String[ContactItemList.size()];
                    String message = "위급 상황입니다.";
                    for (int i = 0; i < ContactItemList.size(); i++) {
                        phoneNum[i] = ContactItemList.get(i).getContactNum().toString();
                        if (phoneNum[i].length() > 0 && message.length() > 0) {
                            sendSMS(phoneNum[i], message);

                        } else
                            Toast.makeText(getApplicationContext(), "전화번호와 메시지를 입력해주세요.", Toast.LENGTH_SHORT).show();
                    }
                }else {
                    Toast.makeText(getApplicationContext(), "연락처를 입력해주세요.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        Button btnSos = (Button) findViewById(R.id.btn_sos);
        btnSos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //ArrayList<SosItem> mSosItemList;
                SosListAdapter sosListAdapter;
                ArrayList<SosItem> mSosItemList = new ArrayList<>();
                mSosItemList.add(new SosItem("경찰서","112"));
                mSosItemList.add(new SosItem("간첩신고","112"));
                mSosItemList.add(new SosItem("소방서","112"));
                mSosItemList.add(new SosItem("밀수신고","112"));
                mSosItemList.add(new SosItem("학교폭력 신고 및 상담","112"));
                mSosItemList.add(new SosItem("사이버테러신고","112"));
                //ListView sosList = (ListView)findViewById(R.id.friendlist);
                sosListAdapter = new SosListAdapter(getApplicationContext(), mSosItemList);
                //sosList.setAdapter(sosListAdapter);
            }
        });


        Button btnTel = (Button) findViewById(R.id.btn_tel);
        btnTel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent contact_picker = new Intent(Intent.ACTION_PICK);
                contact_picker.setType(ContactsContract.Contacts.CONTENT_TYPE);
                startActivityForResult(contact_picker, PICK_CONTACT);
            }
        });

        ImageButton requestbtn = (ImageButton) findViewById(R.id.requestbutton);
        requestbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences sp = getSharedPreferences("login", MODE_PRIVATE);
                boolean state = sp.getBoolean("home_request_ok", false);
                sp.edit().putBoolean("home_request_ok", !state).commit();
                initRequestBtnState();

                if (!state == true) {
                    Toast.makeText(HomeActivity.this, "앞으로 홈버튼을 7번 연속으로 클릭하시면 위험 알림을 발송하게 됩니다.", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(HomeActivity.this, "홈버튼을 통한 위험 알림 발송기능을 중지합니다.", Toast.LENGTH_SHORT).show();
                }
            }
        });
        initRequestBtnState();

        final Switch push_switch = (Switch) findViewById(R.id.push_switch);
        push_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                SharedPreferences sp = getSharedPreferences("login", MODE_PRIVATE);
                //boolean state = sp.getBoolean("push_switch", true);
                sp.edit().putBoolean("push_switch", push_switch.isChecked()).commit();
                initPushSwitchState();

                if (push_switch.isChecked()) {
                    Toast.makeText(HomeActivity.this, "다른사람의 위험 요청 푸시를 수신합니다.", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(HomeActivity.this, "다른사람의 위험 요청 푸시를 수신하지 않습니다.", Toast.LENGTH_SHORT).show();
                }
            }
        });
        initPushSwitchState();

        //
        ContactItemList = new ArrayList<>();
        ListView friendList = (ListView)findViewById(R.id.friendlist);
        friendListAdapter = new FriendListAdapter(this, ContactItemList);
        friendList.setAdapter(friendListAdapter);
        initFriendList();

        //getLocationPermission();
        getPermission();    //권한

        if (RealService.serviceIntent == null) {
            serviceIntent = new Intent(this, RealService.class);
            startService(serviceIntent);
        } else {
            serviceIntent = RealService.serviceIntent;//getInstance().getApplication();
            //Toast.makeText(getApplicationContext(), "already RealService done", Toast.LENGTH_LONG).show();
            Log.e("Home - onCreate : ", "already RealService done");
        }
    }

    private void sendSMS(String phoneNumber, String message)
    {

        SmsManager sms = SmsManager.getDefault();
        sms.sendTextMessage(phoneNumber, null, message, null, null);
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

                //ContactItemList = new ArrayList<>();
                if(ContactItemList != null){
                    //ContactItemList.add(new ContactItem(people_Name, people_Number));
                    //friendListAdapter.notifyDataSetChanged();
                    DBManager dbManager = new DBManager(this);
                    SQLiteDatabase db = dbManager.getWritableDatabase();
                    ContentValues values = new ContentValues();
                    values.put("name", people_Name);
                    values.put("phone", people_Number);
                    db.insert(dbManager.ContactTB, null, values);
                    db.close();
                    initFriendList();
                }
                //startActivity(new Intent("android.intent.action.CALL", Uri.parse("tel:"+people_Number)));
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
        ContactItemList.clear();
        DBManager dbManager = new DBManager(this);
        SQLiteDatabase db = dbManager.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT name, phone FROM " +
                dbManager.ContactTB, null);
        while (cursor.moveToNext()) {
            ContactItemList.add(
                    new ContactItem(
                            cursor.getString(0) // name
                            , cursor.getString(1)   // phone
                    )
            );
        }
        cursor.close();
        db.close();
        friendListAdapter.notifyDataSetChanged();
    }
    private void getPermission() {
        //checkSelfPermission을 사용하여 사용자가 권한을 승인해야만 api의 사용이 가능
        //또한, Manifest에서 uses-permission으로 선언된 기능에 대해서만 동의진행이 가능하다
        if (Build.VERSION.SDK_INT >=23 && ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.RECEIVE_SMS) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            //전화, 연락처 접근, 위치수신에 대한 권한 요청, String 배열로 복수개의 요청이 가능함
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.CALL_PHONE, Manifest.permission.READ_CONTACTS, Manifest.permission.SEND_SMS,
                    Manifest.permission.RECEIVE_SMS, Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION);
        }
    }
    /**
     * Prompts the user for permission to use the device location.
     */
    //private boolean getLocationPermission() {
        /*
         * Request location permission, so that we can get the location of the
         * device. The result of the permission request is handled by a callback,
         * onRequestPermissionsResult.
         */
        /*if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
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
    }*/

    /**
     * Handles the result of the request for location permissions.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        //mLocationPermissionGranted = false;
        switch (requestCode) {
            case PERMISSION:
                // If request is cancelled, the result arrays are empty.
                //grantresult는 requestPermissions에서 요청된 String[]순서로 들어옴. 0~N개로 결과를 탐색
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //mLocationPermissionGranted = true;
                    //권한 허가
                    //해당 권한을 사용해서 작업을 진행할 수 있음

                    //// 여기서 현재 위치 GPS 가져오기
                    //getDeviceLocation();
                }
                else{
                    //권한 거부
                    //사용자가 해당권한을 거부했을때 할 동작 수행
                    Toast.makeText(this, "권한을 허용하셔야 합니다.", Toast.LENGTH_SHORT).show();
                    //finish();
                }
                return;
        }
    }
        //updateLocationData();


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