package com.example.jacky.servicetester;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Jacky on 2016/8/8.
 */
public class MyService extends Service {

    public static final String TAG = "MyService";
    boolean RUN_THREAD = true;
    private Thread t;
    private final IBinder binder = new MyLocalBinder();
    SimpleDateFormat df;
    String time;

    //Constructor
    public MyService(){

    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "Service created");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG, "Service started");

        Runnable r = new Runnable() {
            @Override
            public void run() {
                for(int i=0; i<5; i--){
                    while(RUN_THREAD) {
                        long futureTime = System.currentTimeMillis() + 1000;
                        while (System.currentTimeMillis() < futureTime) {
                            synchronized (this) {
                                try {
                                    df = new SimpleDateFormat("HH:mm:ss", Locale.CHINESE);
                                    time = df.format(new Date());
                                    wait(futureTime - System.currentTimeMillis());
                                } catch (Exception e) {
                                    //Error message
                                }
                            }
                        }
                    }
                }
            }
        };

        t = new Thread(r);
        t.start();

        //If the SERVICE is destroy by android, the SERVICE will restart
        //return Service.START_STICKY;
        //If the SERVICE is destroy by android, the SERVICE will not restart
        return Service.START_NOT_STICKY;
    }


    @Override
    public void onDestroy() {
        RUN_THREAD = false;
        t.interrupt();
        t = null;
        super.onDestroy();
        Log.i(TAG, "Service stopped");
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.i(TAG, "Bind service started");
        return binder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.i(TAG, "Bind service stopped");
        return false;
    }

    public String getCurrentTime(){
        Log.i(TAG, time);
        return time;
    }

    //*Bridge for communicate with the Service******************************************************
    public class MyLocalBinder extends Binder {
        MyService getService(){
            return MyService.this;
        }
    }

}
