package com.jjas.clientserver;

import android.app.Service;
import android.content.Intent;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Messenger;

import androidx.annotation.Nullable;

public class NetworkService extends Service {

    public static final String TAG = "NetworkService";

    private HandlerThread handlerThread;
    private IncomingRequestHandler requestHandler;
    private Messenger messenger;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        handlerThread = new HandlerThread("NetworkServiceThread");
        handlerThread.start();
        requestHandler = new IncomingRequestHandler(handlerThread.getLooper(), this);
        this.messenger = new Messenger(requestHandler);
        return messenger.getBinder();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        handlerThread.quitSafely();
    }
}
