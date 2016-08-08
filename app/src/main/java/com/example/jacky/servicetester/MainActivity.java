package com.example.jacky.servicetester;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.jacky.servicetester.MyService.MyLocalBinder;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    //Service
    Button startServiceBtn;
    Button stopServiceBtn;
    TextView msgTv;

    MyService myServiceBinder;
    Handler handler;
    Runnable run;
    boolean isBound = false;

    public static final String TAG = "MainActivity";

    public void showTime(){
        if(isBound == true) {
            handler = new Handler();
            run = new Runnable() {
                @Override
                public void run() {
                    String currentTime = myServiceBinder.getCurrentTime();
                    msgTv.setText(currentTime);
                    showTime();
                }
            };
            handler.postDelayed(run, 1000);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
    }

    public void init(){
        //Service
        startServiceBtn = (Button) findViewById(R.id.startServiceBtn);
        stopServiceBtn = (Button) findViewById(R.id.stopServiceBtn);
        msgTv = (TextView) findViewById(R.id.msgTv);

        startServiceBtn.setOnClickListener(this);
        stopServiceBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v){
        switch (v.getId()) {
            case R.id.startServiceBtn:
                Intent startService = new Intent(this, MyService.class);
                startService(startService);
                bindService(startService, myServiceConnection, Context.BIND_NOT_FOREGROUND);
                break;
            case R.id.stopServiceBtn:
                stopService(new Intent (this, MyService.class));
                unbindService(myServiceConnection);
                handler.removeCallbacks(run);
                msgTv.setText("Service stopped");
                break;
            default:
                break;
        }
    }

    //Linkage of communicate of the Service
    private ServiceConnection myServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            MyLocalBinder binder = (MyLocalBinder) service;
            myServiceBinder = binder.getService();
            isBound = true;
            showTime();

        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            isBound = false;
        }
    };

}
