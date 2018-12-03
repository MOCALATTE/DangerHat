package com.example.mocalatte.project1.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Handler;
import android.os.IBinder;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.Toast;

import com.example.mocalatte.project1.network.UpdateGPSThread;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.util.Calendar;

public class RealService extends Service {

    public static Intent serviceIntent;
    HomeKeyReceiver homeKeyReceiver;
    Thread mainThread;

    final Handler handler = new Handler();

    // 위치 관련
    // The geographical location where the device is currently located. That is, the last-known
    // location retrieved by the Fused Location Provider.
    private Location mLastKnownLocation;
    // The entry point to the Fused Location Provider.
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private boolean mLocationPermissionGranted;


    public RealService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        serviceIntent = intent;

        //Toast.makeText(this, "RealService onStartCommand", Toast.LENGTH_SHORT).show();
        if(homeKeyReceiver == null){
            homeKeyReceiver = new HomeKeyReceiver();
            IntentFilter filter = new IntentFilter(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
            registerReceiver(homeKeyReceiver,filter);
        }

        if(mainThread == null){
            mainThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    while (true) {
                        handler.post(new Runnable() {
                            public void run() {
                                //Toast.makeText(getApplicationContext(), "Service Hello", Toast.LENGTH_SHORT).show();
                                Log.e("Service Hello", "Hello");
                                if(getLocationPermission() == true/*mLocationPermissionGranted == true*/){
                                    updateLocationData();
                                }
                                else{
                                    Toast.makeText(getApplicationContext(), "권한이 없습니다.", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                        SystemClock.sleep(1 * 60 * 1000);  // 테스트용도.. 60초마다..
                        //SystemClock.sleep(4 * 60 * 1000); //4분마다 실행..
                        //SystemClock.sleep(120 * 60 * 1000); //2시간마다 실행..
                    }
                }
            });
            mainThread.start();
        }

        // Google Map api
        // Construct a FusedLocationProviderClient.
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        /*// Prompt the user for permission.
        boolean resultOfPerm = getLocationPermission();
        if(resultOfPerm == true){
            // Turn on the My Location layer and the related control on the map.
            updateLocationData();

            // Get the current location of the device and set the position of the map.
            //getDeviceLocation();
        }*/

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        serviceIntent = null;
        setAlarmTimer();
        Thread.currentThread().interrupt();

        if (mainThread != null) {
            mainThread.interrupt();
            mainThread = null;
        }
        if(homeKeyReceiver != null){
            unregisterReceiver(homeKeyReceiver);
            homeKeyReceiver = null;
        }
    }

    protected void setAlarmTimer() {
        final Calendar c = Calendar.getInstance();
        c.setTimeInMillis(System.currentTimeMillis());
        c.add(Calendar.SECOND, 1);
        Intent intent = new Intent(this, AlarmRecever.class);
        PendingIntent sender = PendingIntent.getBroadcast(this, 0, intent, 0);

        AlarmManager mAlarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        mAlarmManager.set(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), sender);
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
        if (ContextCompat.checkSelfPermission(getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
        } else {
            mLocationPermissionGranted = false;
            /*ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);*/
        }

        return mLocationPermissionGranted;
    }

    /**
     * Gets the current location of the device, and positions the map's camera.
     */
    private void getDeviceLocation() {
        /*
         * Get the best and most recent location of the device, which may be null in rare
         * cases when a location is not available.
         */
        try {
            if (mLocationPermissionGranted) {
                Task<Location> locationResult = mFusedLocationProviderClient.getLastLocation();
                locationResult.addOnCompleteListener(new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        if (task.isSuccessful() && task.getResult() != null) {
                            // Set the map's camera position to the current location of the device.
                            mLastKnownLocation = task.getResult();
                            Log.e("lat : ", mLastKnownLocation.getLatitude()+"");
                            Log.e("lng : ", mLastKnownLocation.getLongitude()+"");
                            Toast.makeText(getApplicationContext(), "lat : "+mLastKnownLocation.getLatitude()+"\n"
                                    + "lng : "+mLastKnownLocation.getLongitude(), Toast.LENGTH_SHORT).show();

                            // 서버에 업데이트.
                            SharedPreferences sp = getSharedPreferences("login", MODE_PRIVATE);
                            long storedid = sp.getLong("id", -1);
                            if(storedid != -1){
                                UpdateGPSThread updateGPSThread = new UpdateGPSThread(getApplicationContext(), storedid, mLastKnownLocation.getLatitude(), mLastKnownLocation.getLongitude()) ;
                                updateGPSThread.execute();
                            }
                        } else {
                            Log.d("TASK IS NOT SUCCESSED", "Current location is null. Using defaults.");
                            Log.e("TASK IS NOT SUCCESSED", "Exception: %s", task.getException());
                        }
                    }
                });
            }
        } catch (SecurityException e)  {
            Log.e("Exception: %s", e.getMessage());
        }
    }

    private void updateLocationData() {
        try {
            if (mLocationPermissionGranted) {
                // 여기서 현재 위치 GPS 가져오기
                getDeviceLocation();
            } else {
                mLastKnownLocation = null;
                //getLocationPermission();
            }
        } catch (SecurityException e)  {
            Log.e("Exception: %s", e.getMessage());
        }
    }

}
