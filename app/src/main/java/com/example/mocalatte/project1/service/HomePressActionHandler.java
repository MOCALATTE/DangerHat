package com.example.mocalatte.project1.service;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.telephony.SmsManager;
import android.util.Log;
import android.widget.Toast;

import com.example.mocalatte.project1.adapter.DBManager;
import com.example.mocalatte.project1.network.DangerRequestThread;

/**
 * Created by 임규진 on 2017-04-21.
 */

public class HomePressActionHandler {
    private long homeKeyPressedTime ;//= 0;
    private Toast toast;
    private Context context;
    private int count;
    private final int MAX_COUNT = 7;    // 이 변수로 홈버튼 몇번 연속 클릭했을 때 기준인지를 결정함..
    public HomePressActionHandler(Context context) {
        this.context = context;

        this.count = 0;
        this.homeKeyPressedTime = System.currentTimeMillis();
    }
    public void onHomePressed() {
        // 이전 홈키눌렸던 때보다 2초 이상 뒤에 눌린것이라면..
        if (System.currentTimeMillis() > homeKeyPressedTime + 2000) {
            count = 1;//countPlus();//count++;
            homeKeyPressedTime = System.currentTimeMillis();
            showGuide(MAX_COUNT-count);

            return;
        }
        // 방금 전에 홈키를 누른 뒤로 2초안에 다시 눌렀다면..
        if (System.currentTimeMillis() <= homeKeyPressedTime + 2000) {
            //context.finish();
            if(toast != null)
                toast.cancel();

            countPlus();//count++;
            homeKeyPressedTime = System.currentTimeMillis();
            showGuide(MAX_COUNT-count);
        }
    }

    private void countPlus(){
        count++;
    }
    private void countMinus(){
        if(count != 0)
            count--;
    }

    public void showGuide(int value) {

        if( value != 0 && value < MAX_COUNT-3){ // value = 0이면 연속클릭조건 달성인것이고, 매번 홈버튼 클릭마다 Toast띄우면 불편하니 카운트 3번까지는 토스트안띄우도록.
            toast = Toast.makeText(context, "앞으로 홈버튼을 "+value+"번 더 누르시면 위험 요청이 발송됩니다.", Toast.LENGTH_SHORT);
            toast.show();

            return ;
        }

        if(value == 0){
            count = 0;
            toast = Toast.makeText(context, "위험 요청을 서버에 발송하였습니다!", Toast.LENGTH_SHORT);
            toast.show();
            // 서버에 업데이트.
            SharedPreferences sp = context.getSharedPreferences("login", context.MODE_PRIVATE);
            long storedid = sp.getLong("id", -1);
            if(storedid != -1){
                try{
                    DangerRequestThread dangerRequestThread = new DangerRequestThread(context, storedid, RealService.mLastKnownLocation.getLatitude(), RealService.mLastKnownLocation.getLongitude());
                    dangerRequestThread.execute();
                } catch (Exception e){
                    Toast.makeText(context, "lastLocation is null", Toast.LENGTH_SHORT).show();
                }

                DBManager dbManager = new DBManager(context);
                SQLiteDatabase db = dbManager.getWritableDatabase();
                Cursor cursor = db.rawQuery("SELECT name, phone FROM " +
                        dbManager.ContactTB, null);
                while (cursor.moveToNext()) {
                    sendSMS(cursor.getString(1), "위급상황 입니다.");
                }
                cursor.close();
                db.close();
            }
            Log.e("홈버튼 연속 클릭완료", "-----");
        }
    }

    private void sendSMS(String phoneNumber, String message)
    {

        SmsManager sms = SmsManager.getDefault();
        sms.sendTextMessage(phoneNumber, null, message, null, null);
    }
}
