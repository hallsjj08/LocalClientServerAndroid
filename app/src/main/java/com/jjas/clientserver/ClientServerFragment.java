package com.jjas.clientserver;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

public class ClientServerFragment extends BaseFragment {

    private EditText etIpAddress;

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

        Button bServer = v.findViewById(R.id.bServer);
        bServer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.networkServiceRequest(NetworkService.SETUP_HOST_SERVER, null);
            }
        });

        Button bClient = v.findViewById(R.id.bClient);
        bClient.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.networkServiceRequest(NetworkService.CONNECT_TO_HOST, etIpAddress.getText().toString());
            }
        });

        return v;
    }
}
