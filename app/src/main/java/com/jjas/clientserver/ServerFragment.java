package com.jjas.clientserver;


import android.content.Context;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Locale;

public class ServerFragment extends BaseFragment {

    public static final String TAG = "ServerFragment";
    private TextView tvIP;
    private TextView tvInfo;

    public ServerFragment() {
        // Required empty public constructor
    }

    public static ServerFragment newInstance() {
        return new ServerFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_server, container, false);

        tvIP = v.findViewById(R.id.tvIpAddress);
        tvInfo = v.findViewById(R.id.tvClientInfo);

        String ipMessage = "Server IP Address: " + getIpAddress();
        tvIP.setText(ipMessage);
        tvInfo.setText("Waiting on client info...");

        return v;
    }

    private String getIpAddress() {
        WifiManager wifiManager = (WifiManager) getContext().getSystemService(Context.WIFI_SERVICE);
        int address = wifiManager.getConnectionInfo().getIpAddress();

        return String.format(Locale.getDefault(), "%d.%d.%d.%d",
                (address & 0xff), (address >> 8 & 0xff),
                (address >> 16 & 0xff), (address >> 24 & 0xff));
    }

    public void clientInfoReceived(String info) {
        tvInfo.setText(info);
    }

}
