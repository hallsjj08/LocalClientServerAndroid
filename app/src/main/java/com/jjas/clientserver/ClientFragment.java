package com.jjas.clientserver;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;

public class ClientFragment extends BaseFragment {

    private EditText etName;
    private DatePicker date;

    public ClientFragment() {
        // Required empty public constructor
    }

    public static ClientFragment newInstance() {
        return new ClientFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_client, container, false);

        etName = v.findViewById(R.id.etName);
        date = v.findViewById(R.id.datePicker1);

        Button bSend = v.findViewById(R.id.bSend);
        bSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.networkServiceRequest(NetworkService.SEND_DATA_TO_HOST, "Data");
            }
        });

        Button bDisconnect = v.findViewById(R.id.bDisconnect);
        bDisconnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.networkServiceRequest(NetworkService.DISCONNECT_FROM_HOST, null);
            }
        });

        return v;
    }

}
