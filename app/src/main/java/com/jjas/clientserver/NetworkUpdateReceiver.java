package com.jjas.clientserver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class NetworkUpdateReceiver extends BroadcastReceiver {

    NetworkServiceUpdates updates;

    public NetworkUpdateReceiver(NetworkServiceUpdates updates) {
        this.updates = updates;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if(intent.getAction().equals("data_received")) {
            updates.onMessageReceived(intent.getStringExtra("message"));
        }
    }
}
