package com.hjh.network;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class ManagerReceiver extends BroadcastReceiver {
    public ManagerReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        context.stopService(new Intent(context,ManagerService.class));
    }
}
