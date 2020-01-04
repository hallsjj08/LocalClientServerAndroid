package com.jjas.clientserver;


import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;

import org.json.JSONException;
import org.json.JSONObject;

public class ClientFragment extends BaseFragment {

    public static final String TAG = "ClientFragment";

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
                listener.networkServiceRequest(IncomingRequestHandler.SEND_DATA_TO_HOST, converDataToJsonString());
            }
        });

        Button bDisconnect = v.findViewById(R.id.bDisconnect);
        bDisconnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.networkServiceRequest(IncomingRequestHandler.DISCONNECT_FROM_HOST, null);
            }
        });

        return v;
    }

    private String converDataToJsonString() {

        String data = "Error converting data";

        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("name", etName.getText().toString());
        } catch (JSONException e) {
            Log.e(TAG, "Error converting data to json string.", e);
        }

        return data;
    }

}
