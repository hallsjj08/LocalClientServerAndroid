package com.jjas.clientserver;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class NetworkService extends Service {

    public static final String TAG = "NetworkService";
    public static final int SETUP_HOST_SERVER = 0;
    public static final int CONNECT_TO_HOST = 1;
    public static final int SEND_DATA_TO_HOST = 2;
    public static final int DISCONNECT_FROM_HOST = 3;

    public static final int PORT_NUMBER = 9001;

    private ServerSocket serverSocket;
    private Socket socket;

    private ObjectInputStream ois;
    private ObjectOutputStream oos;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Messenger messenger = new Messenger(new IncomingRequestHandler(this));
        return messenger.getBinder();
    }

    private static class IncomingRequestHandler extends Handler {

        private Context context;

        IncomingRequestHandler(Context context) {
            this.context = context.getApplicationContext();
        }

        @Override
        public void handleMessage(@NonNull Message msg) {
            switch (msg.what) {
                case SETUP_HOST_SERVER:
                    Log.d(TAG, "Setting up as host.");
                    break;
                case CONNECT_TO_HOST:
                    Log.d(TAG, "Connecting client to host.");
                    break;
                case SEND_DATA_TO_HOST:
                    Log.d(TAG, "Sending data to host.");
                    break;
                case DISCONNECT_FROM_HOST:
                    Log.d(TAG, "Disconnecting from host.");
                    break;
                default:
                    super.handleMessage(msg);
                    break;
            }
        }
    }


    private void setupServer(Handler handler) throws IOException {
        serverSocket = new ServerSocket(PORT_NUMBER);
        //keep listens indefinitely until receives 'exit' call or program terminates
        while(true){
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
            socket.close();
            //terminate the server if client sends exit request
//            if(message.equalsIgnoreCase(DISSCONNECT)) break;
        }
//        Log.d(TAG, "Shutting down Socket server!!");
        //close the ServerSocket object
//        serverSocket.close();
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

    private void setupHostConnection(String ipAddress, String name) throws IOException {

        if(socket == null) socket = new Socket(ipAddress, PORT_NUMBER);

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
}
