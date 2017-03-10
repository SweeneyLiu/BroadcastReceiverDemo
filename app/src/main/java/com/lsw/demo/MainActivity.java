package com.lsw.demo;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.text.format.DateFormat;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    private TextView mTextMessage;

    private NetworkChangeReceiver networkChangeReceiver;
    private IntentFilter intentFilter;

    private LocalBroadcastManager mLocalBroadcastManager;
    private LocalBroadcastReceiver mLocalBroadcastReceiver;

    private final static String m12 = "hh:mm";
    private final static String m24 = "kk:mm";
    private String mFormat;
    private TextView textView;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    mTextMessage.setText(R.string.title_home);
                    return true;
                case R.id.navigation_dashboard:
                    mTextMessage.setText(R.string.title_dashboard);
                    return true;
                case R.id.navigation_notifications:
                    mTextMessage.setText(R.string.title_notifications);
                    return true;
            }
            return false;
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTextMessage = (TextView) findViewById(R.id.message);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        //注册广播
        intentFilter = new IntentFilter();
        intentFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        networkChangeReceiver = new NetworkChangeReceiver();
        registerReceiver(networkChangeReceiver, intentFilter);

        //发送有序广播
        Button button = (Button)findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent("com.lsw.test.mybroadcast");
                sendBroadcast(intent);
//                MyReceiver myReceiver = new MyReceiver();
//                sendOrderedBroadcast(intent,null,myReceiver,null,0,null,null);
            }
        });

        //发送本地广播
        Button localButton = (Button)findViewById(R.id.local_receiver_button);
        localButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =new Intent("com.lsw.test.localbroadcast");
                mLocalBroadcastManager.sendBroadcast(intent);
            }
        });

        mLocalBroadcastReceiver = new LocalBroadcastReceiver();
        mLocalBroadcastManager = LocalBroadcastManager.getInstance(this);
        IntentFilter intentFilter = new IntentFilter("com.lsw.test.localbroadcast");
        mLocalBroadcastManager.registerReceiver(mLocalBroadcastReceiver,intentFilter);


        //时间更新
        textView = (TextView)findViewById(R.id.time_text);
        IntentFilter filter=new IntentFilter();
        filter.addAction(Intent.ACTION_TIME_TICK);
        TimeReceiver timeReceiver =new TimeReceiver();
        registerReceiver(timeReceiver,filter);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(networkChangeReceiver);
        mLocalBroadcastManager.unregisterReceiver(mLocalBroadcastReceiver);
    }

    class NetworkChangeReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
//            Toast.makeText(MainActivity.this, "network changed", Toast.LENGTH_SHORT).show();
            ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
            if ((networkInfo != null) && (networkInfo.isAvailable())) {
                Toast.makeText(MainActivity.this, "network is available", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(MainActivity.this, "network is not available", Toast.LENGTH_SHORT).show();
            }
        }
    }

    class LocalBroadcastReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            Toast.makeText(MainActivity.this, "This is local receiver", Toast.LENGTH_SHORT).show();
        }
    }

    class TimeReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
             if (action.equals(Intent.ACTION_TIME_TICK)) {
                 textView.setText(getTime());
             }
        }
    }

    private boolean get24HourMode()
    {
        return android.text.format.DateFormat.is24HourFormat(this);
    }

    private void setFormat()
    {
        if (get24HourMode())
        {
            mFormat = m24;
        } else
        {
            mFormat = m12;
        }
    }

    private String getTime(){
        Calendar mCalendar = Calendar.getInstance();
        setFormat();
        mCalendar.setTimeInMillis(System.currentTimeMillis());
        return (DateFormat.format(mFormat, mCalendar)).toString();
    }
}
