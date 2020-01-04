package com.jjas.clientserver;

import android.Manifest;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.os.StrictMode;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class MainActivity extends AppCompatActivity implements ServiceConnection, OnFragmentInteractionListener {

    public static final String TAG = "MainActivity";
    private Messenger networkService;
    private boolean bound;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        requestPermissions();

        getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer,
                ClientServerFragment.newInstance()).commit();
    }

    private void requestPermissions() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_WIFI_STATE)
                != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.READ_CONTACTS)) {
            } else {
                // No explanation needed; request the permission
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_NETWORK_STATE,
                                Manifest.permission.ACCESS_WIFI_STATE,
                                Manifest.permission.INTERNET},
                        1001);
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        bindService(new Intent(this, NetworkService.class),
                this, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onStop() {
        super.onStop();
        unbindService(this);
    }

    @Override
    public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
        networkService = new Messenger(iBinder);
        bound = true;
    }

    @Override
    public void onServiceDisconnected(ComponentName componentName) {
        networkService = null;
        bound = false;
    }

    @Override
    public void networkServiceRequest(int request, String args) {
        Log.d(TAG, "Network Request: " + request);
        try {
            switch (request) {
                case IncomingRequestHandler.SETUP_HOST_SERVER:
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.fragmentContainer, ServerFragment.newInstance()).commit();
                    break;
                case IncomingRequestHandler.CONNECT_TO_HOST:
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.fragmentContainer, ClientFragment.newInstance()).commit();
                    break;
            }

            Message msg = Message.obtain(null, request, 0, 0);
            if(bound) {
                if(args != null) {
                    Bundle bundle = new Bundle();
                    bundle.putString(IncomingRequestHandler.EXTRA_MSG_DATA, args);
                    msg.setData(bundle);
                }
                networkService.send(msg);
            }
        } catch (RemoteException e) {
            Log.e(TAG, "Error sending request to network service.", e);
        }
    }
}
