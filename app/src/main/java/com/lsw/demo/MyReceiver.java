package com.lsw.demo;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class MyReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Toast.makeText(context, "This is BroadcastReceiverDemo Receiver", Toast.LENGTH_SHORT).show();
        if(isOrderedBroadcast()){
            Toast.makeText(context, "这是有序广播", Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(context, "这是无序广播", Toast.LENGTH_SHORT).show();
        }
//        abortBroadcast();
    }
}
