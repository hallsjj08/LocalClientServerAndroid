package com.jjas.clientserver;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

public class ClientServerFragment extends BaseFragment {

    private EditText etIpAddress;
    private Button bConnect;

    public ClientServerFragment() {
        // Required empty public constructor
    }

    public static ClientServerFragment newInstance() {
        return new ClientServerFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_client_server, container, false);

        etIpAddress = v.findViewById(R.id.etIpAddress);
        bConnect = v.findViewById(R.id.bConnect);

        Button bServer = v.findViewById(R.id.bServer);
        bServer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.networkServiceRequest(IncomingRequestHandler.SETUP_HOST_SERVER, null);
            }
        });

        Button bClient = v.findViewById(R.id.bClient);
        bClient.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                etIpAddress.setVisibility(View.VISIBLE);
                bConnect.setVisibility(View.VISIBLE);
            }
        });

        Button bConnect = v.findViewById(R.id.bConnect);
        bConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String ipAddr = etIpAddress.getText().toString();
                String regex = "^(?:[0-9]{1,3}\\.){3}[0-9]{1,3}$";
                if(!ipAddr.isEmpty() && ipAddr.matches(regex)) {
                    listener.networkServiceRequest(IncomingRequestHandler.CONNECT_TO_HOST, ipAddr);
                } else {
                    etIpAddress.setError("IP address should be of the format 192.168.5.3");
                }
            }
        });

        return v;
    }
}
