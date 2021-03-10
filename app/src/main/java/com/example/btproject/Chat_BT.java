package com.example.btproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.io.IOException;
import java.util.Set;
import java.util.UUID;

public class Chat_BT extends AppCompatActivity {

    Button btn_listen,btn_list_device,btn_send;
    TextView txt_msg,txt_status;
    EditText edt_msg;
    ListView lst_view;
    BluetoothAdapter bluetoothAdapter;
    BluetoothDevice btArray;

    static final int STATE_LISTEN = 1;
    static final int STATE_CONNECTING = 2;
    static final int STATE_CONNECTED = 3;
    static final int STATE_CONNECTING_FAILED = 4;
    static final int STATE_MESSAGE_RECIEVED = 5;

    private static final String  APP_NAME = "BTChat";
    private static final UUID MY_UUID = UUID.fromString("0346646d-9385-40af-a7e3-e99563bcd04a");

    int REQUEST_ENABLE_BLUETOOTH =1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat__b_t);

        btn_listen = findViewById(R.id.btn_listen);
        btn_list_device  = findViewById(R.id.btn_list_device);
        btn_send = findViewById(R.id.btn_send);
        txt_msg = findViewById(R.id.txt_msg);
        txt_status = findViewById(R.id.txt_status);
        edt_msg = findViewById(R.id.edt_msg);
        lst_view = findViewById(R.id.lst_view);

        if (!bluetoothAdapter.isEnabled()){

            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent,REQUEST_ENABLE_BLUETOOTH);
        }
        implementListeners();

        btn_listen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ServerClass serverClass = new ServerClass();
                serverClass.start();
            }
        });
    }

    private void implementListeners() {
        btn_list_device.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Set<BluetoothDevice> bt = bluetoothAdapter.getBondedDevices();
                String[] strings = new String[bt.size()];
                int index = 0;

                if (bt.size()>0){
                    for (BluetoothDevice device : bt) {
                        btArray = device;
                        strings[index] = device.getName();
                        index++;
                    }
                    ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1,strings);
                    lst_view.setAdapter(arrayAdapter);
                }
            }
        });
    }
    Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message msg) {
            switch (msg.what){
                case STATE_LISTEN:
                    txt_status.setText("Listening");
                    break;
                case STATE_CONNECTING:
                    txt_status.setText("Connecting");
                    break;
                case STATE_CONNECTED:
                    txt_status.setText("Connected");
                    break;
                case STATE_CONNECTING_FAILED:
                    txt_status.setText("Connection failed");
                    break;
                case STATE_MESSAGE_RECIEVED:
                    break;

            }
            return true;
        }
    });

    private class ServerClass extends Thread{
        private BluetoothServerSocket serverSocket;

        public  ServerClass(){
            try{
                serverSocket = bluetoothAdapter.listenUsingRfcommWithServiceRecord(APP_NAME,MY_UUID);
            }catch(IOException e) {
                e.printStackTrace();
            }
        }

        public void run(){
            BluetoothSocket socket =null;

            while (socket==null){
                try {
                    Message message = Message.obtain();
                    message.what = STATE_CONNECTING;
                    handler.sendMessage(message);
                    socket = serverSocket.accept();
                }catch (IOException e){
                    e.printStackTrace();
                    Message message = Message.obtain();
                    message.what = STATE_CONNECTING_FAILED;
                    handler.sendMessage(message);
                }
                if (socket!= null){
                    Message message = Message.obtain();
                    message.what = STATE_CONNECTED;
                    handler.sendMessage(message);
                    break;
                }
            }

        }
    }
}