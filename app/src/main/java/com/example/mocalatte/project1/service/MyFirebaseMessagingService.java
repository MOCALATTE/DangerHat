package com.example.mocalatte.project1.service;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.example.mocalatte.project1.R;
import com.example.mocalatte.project1.ui.MapsActivity;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MyFirebaseMessagingService  extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Log.e("push arrived","-----");

        SharedPreferences sp = getSharedPreferences("login", MODE_PRIVATE);
        long storedid = sp.getLong("id", -1);
        boolean push_switch = sp.getBoolean("push_switch", true);
        if(storedid == -1 || push_switch==false){
            Log.e("수신안함.. ","표시안함");
        }
        else{
            //Log.e("getData : ", remoteMessage.getData());
            //Log.e("getTo : ", remoteMessage.getTo().toString());
            Log.e("getData :", remoteMessage.getData().get("message"));
            sendNotification(remoteMessage.getData().get("message")/*.get("data")*/);
        }
    }

    @Override
    public void onNewToken(String s) {
        super.onNewToken(s);
        Log.e("onNewToken", s);
        sendRegistrationToServer(s);
    }

    private void sendRegistrationToServer(String token) {
        // Add custom implementation, as needed.

        SharedPreferences sp = getSharedPreferences("login", MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("token", token);
        editor.putInt("tokenRefreshed", 1);
        editor.commit();
    }

    private void sendNotification(String messageBody) {

        Log.e("sendNotification", "Working!!");
        Log.e("messageBody", messageBody);
        // 수신되면 인텐트에 지정한 액티비티가 실행되므로 Main말고 다른 액티비티 만들어서 처리하도록 하거나 로그인 했는지 검사로직 필요!!!!!!!!!!
        Intent intent = new Intent(this, MapsActivity.class);
        intent.putExtra("gps", messageBody);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent,
                PendingIntent.FLAG_ONE_SHOT);// Second param is Request code

        Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        // Sets an ID for the notification, so it can be updated.
        int notifyID = 1;

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("위험 요청 수신!!")
                .setContentText("클릭해서 위치를 확인하세요!!!!!")
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);
                //.setChannelId();

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String CHANNEL_ID = "my_channel_01";// The id of the channel.
            notificationBuilder.setChannelId(CHANNEL_ID);
            CharSequence name = getString(R.string.channel_name);// The user-visible name of the channel.
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel mChannel = new NotificationChannel(CHANNEL_ID, name, importance);

            notificationManager.createNotificationChannel(mChannel);
        }

        notificationManager.notify(/*0*/notifyID, notificationBuilder.build()); // First param is ID of notification

        /*try {
            SimpleDateFormat dateFormat = new SimpleDateFormat(
                    "yyyy-MM-dd HH:mm", Locale.getDefault());
            JSONObject messageJson = new JSONObject(messageBody);
            Date date = dateFormat.parse(messageJson.getString("date"));
            DBManager dbManager = new DBManager(this);
            SQLiteDatabase db = dbManager.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put("room_number", messageJson.getInt("roomnumber"));
            values.put("room_hash", messageJson.getInt("room_hash"));
            values.put("nickname", messageJson.getString("nickname"));
            values.put("joincode", messageJson.getString("joincode"));
            values.put("message", messageJson.getString("message"));
            values.put("writedate", messageJson.getString("date"));
            db.insert(dbManager.chatContentTB, null, values);
            db.close();

            // 수신되면 인텐트에 지정한 액티비티가 실행되므로 Main말고 다른 액티비티 만들어서 처리하도록 하거나 로그인 했는지 검사로직 필요!!!!!!!!!!
            Intent intent = new Intent(this, SplashActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent,
                    PendingIntent.FLAG_ONE_SHOT);// Second param is Request code

            Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentTitle(messageJson.getString("nickname") + " : " + messageJson.getString("message"))
                    .setContentText(messageJson.getString("date"))
                    .setAutoCancel(true)
                    .setSound(defaultSoundUri)
                    .setContentIntent(pendingIntent);

            NotificationManager notificationManager =
                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);


            notificationManager.notify(0, notificationBuilder.build()); // First param is ID of notification

        } catch (JSONException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }*/

    }
}
