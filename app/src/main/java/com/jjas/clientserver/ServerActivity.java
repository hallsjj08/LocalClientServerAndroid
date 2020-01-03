package com.jjas.clientserver;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.util.Log;
import android.widget.TextView;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.Enumeration;

public class ServerActivity extends AppCompatActivity {

    public static final String TAG = "ServerActivity";
    public static final int PORT_NUMBER = 9001;
    public static final String DISSCONNECT = "exit";
    private Handler uiHandler;
    private Handler handler;
    private HandlerThread handlerThread;
    private ServerSocket serverSocket;
    private Socket socket;
    private ObjectInputStream ois;
    private ObjectOutputStream oos;

    private TextView tvIP;
    private TextView tvInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_server);

        uiHandler = new Handler(getMainLooper()) {
            @Override
            public void handleMessage(@NonNull Message msg) {
                super.handleMessage(msg);
            }
        };

        tvIP = findViewById(R.id.tvIpAddress);
        tvInfo = findViewById(R.id.tvClientInfo);

        tvIP.setText(getIpAddress());

    }

    @Override
    protected void onResume() {
        super.onResume();
        startServer(uiHandler);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(handlerThread != null) {
            handlerThread.quitSafely();
        }
    }

    private void startServer(Handler handler) {
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
                    setupServer(uiHandler);
                } catch (IOException e) {
                    cleanup();
                }
            }
        });
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
            tvInfo.setText(message);
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
            if(message.equalsIgnoreCase(DISSCONNECT)) break;
        }
        Log.d(TAG, "Shutting down Socket server!!");
        //close the ServerSocket object
        serverSocket.close();
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

    private String getIpAddress() {
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements(); ) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress() && inetAddress instanceof Inet4Address) {
                        return inetAddress.getHostAddress();
                    }
                }
            }
        } catch (SocketException ex) {
            ex.printStackTrace();
        }
        return null;
    }
}
