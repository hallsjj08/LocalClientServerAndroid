package com.jjas.clientserver;

import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class ClientActivity extends AppCompatActivity {

    public static final String TAG = "ClientActivity";

    private EditText etIP;
    private EditText etName;
    private DatePicker date;

    Handler handler;
    HandlerThread handlerThread;

    Socket socket;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clent);

        etIP = findViewById(R.id.etIpAddress);
        etName = findViewById(R.id.etName);
        date = findViewById(R.id.datePicker1);

        Button bSend = findViewById(R.id.bSend);
        bSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                connectToHost(etIP.getText().toString(), etName.getText().toString());
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        disconnectFromHost();
        handlerThread.quitSafely();
    }

    private void connectToHost(final String ipAddress, final String name) {
        handlerThread = new HandlerThread("ServerThread") {
            @Override
            public boolean quitSafely() {
                cleanup();
                return super.quitSafely();
            }
        };
        handlerThread.start();
        handler = new Handler(handlerThread.getLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                try {
                    setupHostConnection(ipAddress, name);
                } catch (IOException e) {
                    cleanup();
                }
            }
        });
    }

    private void disconnectFromHost() {
        if(socket == null) return;

        try (ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream())){
            oos.writeUTF(ServerActivity.DISSCONNECT);
            Log.d(TAG, "Sending disconnect request to Socket Server");
        } catch (IOException e) {
            Log.e(TAG, "Error sending info to server.", e);
        }
    }

    private void setupHostConnection(String ipAddress, String name) throws IOException {

        if(socket == null) socket = new Socket(ipAddress, ServerActivity.PORT_NUMBER);

        try (ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
             ObjectInputStream ois = new ObjectInputStream(socket.getInputStream())){
            //write to socket using ObjectOutputStream

            oos.writeUTF(name);
            Log.d(TAG, "Sending request to Socket Server");
            //read the server response message

            String message = ois.readUTF();
            Log.d(TAG, "Message: " + message);
        } catch (IOException e) {
            Log.e(TAG, "Error sending info to server.", e);
            throw e;
        }

    }

    private void cleanup() {
        try {
            if(socket != null && !socket.isClosed()) socket.close();
        } catch (IOException e) {
            Log.e(TAG, "Error closing socket during cleanup.", e);
        }
    }
}
