package com.jjas.clientserver;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import androidx.annotation.NonNull;

import org.json.JSONObject;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class IncomingRequestHandler extends Handler {

    public static final String TAG = "IncomingRequestHandler";
    public static final String EXTRA_MSG_DATA = "msg_data";
    public static final int SETUP_HOST_SERVER = 0;
    public static final int CONNECT_TO_HOST = 1;
    public static final int SEND_DATA_TO_HOST = 2;
    public static final int DISCONNECT_FROM_HOST = 3;

    public static final int PORT_NUMBER = 9001;

    private ServerSocket serverSocket;
    private Socket socket;

    private ObjectInputStream ois;
    private ObjectOutputStream oos;

    private Context context;

    IncomingRequestHandler(Looper looper, Context context) {
        super(looper);
        this.context = context.getApplicationContext();
    }

    @Override
    public void handleMessage(@NonNull Message msg) {
        switch (msg.what) {
            case SETUP_HOST_SERVER:
                setupServer();
                Log.d(TAG, "Setting up as host.");
                break;
            case CONNECT_TO_HOST:
                setupHostConnection(msg.getData().getString(EXTRA_MSG_DATA));
                Log.d(TAG, "Connecting client to host.");
                break;
            case SEND_DATA_TO_HOST:
                sendDataToHost(msg.getData().getString(EXTRA_MSG_DATA));
                Log.d(TAG, "Sending data to host.");
                break;
            case DISCONNECT_FROM_HOST:
                disconnectFromHost();
                Log.d(TAG, "Disconnecting from host.");
                break;
            default:
                super.handleMessage(msg);
                break;
        }
    }

    private void sendBroadcast(Intent intent) {
        context.sendBroadcast(intent);
    }


    private void setupServer() {
            try {
                serverSocket = new ServerSocket(PORT_NUMBER);
                Log.d(TAG, "Waiting for the client request");
                //creating socket and waiting for client connection
                socket = serverSocket.accept();
                //read from socket to ObjectInputStream object
                ois = new ObjectInputStream(socket.getInputStream());
                //convert ObjectInputStream object to String
                String message = ois.readUTF();
                Log.d(TAG, "Message Received: " + message);
                //create ObjectOutputStream object
                oos = new ObjectOutputStream(socket.getOutputStream());
                //write object to Socket
                oos.writeObject("Success: "+message);
                //close resources
                ois.close();
                oos.close();
            } catch (IOException e) {
                Log.e(TAG, "Error setting up host.", e);
            } finally {
                cleanup();
            }
    }

    private void cleanup() {
        try{
            if(ois != null) ois.close();
            if(oos != null) oos.close();
            if(socket != null) socket.close();
            if(serverSocket != null) serverSocket.close();
        } catch (IOException e) {
            Log.e(TAG, "Error closing resources.", e);
        }
    }

    private void disconnectFromHost() {
        if(socket == null) return;

        try (ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream())){
            oos.writeUTF("exit");
            Log.d(TAG, "Sending disconnect request to Socket Server");
        } catch (IOException e) {
            Log.e(TAG, "Error sending info to server.", e);
        }
    }

    private void setupHostConnection(String ipAddress) {
        if(socket == null) {
            try {
                socket = new Socket(ipAddress, PORT_NUMBER);
            } catch (IOException e) {
                Log.e(TAG, "Error connecting to host.", e);
            }
        }
    }

    private void sendDataToHost(String data) {
        if(socket == null) return;
        try (ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream())){
            //write to socket using ObjectOutputStream

            oos.writeUTF(data);
            Log.d(TAG, "Sending request to Socket Server");
            //read the server response message
        } catch (IOException e) {
            Log.e(TAG, "Error sending info to server.", e);
        }
    }
}
