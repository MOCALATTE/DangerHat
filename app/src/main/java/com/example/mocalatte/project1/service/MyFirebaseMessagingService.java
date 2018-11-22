package com.example.mocalatte.project1.service;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MyFirebaseMessagingService  extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Log.e("push arrived","-----");
        sendNotification(remoteMessage.getData().get("data"));
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
