package com.example.btproject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    Button btn_bt_on,btn_bt_off,btn,btn_connect_device,btn_enable,btn_scan,btn_chat;
    BluetoothAdapter myBluetoothAdapter;
    ListView lst_view;
    ArrayList<String> myArrayList = new ArrayList<String>();
    ArrayAdapter<String> myArrayAdapter;
    TextView txt_scan,txt_data;
    UUID MY_UUID = UUID.fromString("0346646d-9385-40af-a7e3-e99563bcd04a");

    IntentFilter intentFilterScan = new IntentFilter(BluetoothAdapter.ACTION_SCAN_MODE_CHANGED);

    Intent btEnablingIntent;
    int requestCodeForEnable;

    //scan the devices
    BroadcastReceiver scanModeReciever = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if(action.equals(BluetoothAdapter.ACTION_SCAN_MODE_CHANGED)){
                int modeValue = intent.getIntExtra(myBluetoothAdapter.EXTRA_SCAN_MODE,myBluetoothAdapter.ERROR);

                if(modeValue == BluetoothAdapter.SCAN_MODE_CONNECTABLE){
                    txt_scan.setText("The device is not in discoverable mode but can still recieve connection");
                }else if(modeValue == BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE){
                    txt_scan.setText("Device is in discoverable mode");
                }else if(modeValue == BluetoothAdapter.SCAN_MODE_NONE){
                    txt_scan.setText("Device is not in discoverable mode and can not recieve connection");
                }else{
                    txt_scan.setText("Error");
                }
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btn_bt_on =(Button) findViewById(R.id.btn_bt_on);
        btn_bt_off = (Button) findViewById(R.id.btn_bt_off);
        btn_connect_device = (Button) findViewById(R.id.btn_connect_device);
        btn_enable = (Button) findViewById(R.id.btn_enable);
        btn_scan = findViewById(R.id.btn_scan);
        btn_chat = findViewById(R.id.btn_chat);
        btn = (Button) findViewById(R.id.btn);
        txt_data = findViewById(R.id.txt_data);
        lst_view = findViewById(R.id.lst_view);
        txt_scan = findViewById(R.id.txt_scan);



        myBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        btEnablingIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        requestCodeForEnable = 1;

//        btn_enable.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
//                intent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION,120);
//                startActivity(intent);
//            }
//        });

        btn_scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
                discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION,5);
                 startActivity(discoverableIntent);
            }
        });

        registerReceiver(scanModeReciever,intentFilterScan);

//        BluetoothOnMethod();
//
//        BluetoothOFFMethod();

//        btn_connect_device.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                myBluetoothAdapter.startDiscovery();
//            }
//        });
//
//        IntentFilter intentFilter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
//
//        registerReceiver(broadcastReceiver,intentFilter);
//        myArrayAdapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1,myArrayList);
//        lst_view.setAdapter(myArrayAdapter);
        Thread2 t =new Thread2();
        t.start();

        btn_chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this,Chat_BT.class);
                startActivity(i);
            }
        });

    }

    Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message msg) {
            txt_data.setText(String.valueOf(msg.arg1));
            return false;
        }
    });
    private class Thread2 extends Thread{
        public void run(){
            for(int i =0; i<50;i++){
//                txt_data.setText(String.valueOf(i));
                Message message = Message.obtain();
                message.arg1 = i;
                handler.sendMessage(message);
                try {
                    sleep(500);
                }catch (InterruptedException e){
                    e.printStackTrace();
                }

            }
        }
    }


//    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            String action = intent.getAction();
//            if(BluetoothDevice.ACTION_FOUND.equals(action)){
//                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
//                myArrayList.add(device.getName());
//                myArrayAdapter.notifyDataSetChanged();
//            }
//        }
//    };

    public void exeButton(View view) {
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Set<BluetoothDevice> bt = myBluetoothAdapter.getBondedDevices();
                String[] strings = new  String[bt.size()];
                int index =0;
                if (bt.size()>0){
                    for (BluetoothDevice device :bt){
                        strings[index] = device.getName();
                        index++;
                    }
                    ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1,strings);
                    lst_view.setAdapter(arrayAdapter);
                }
            }
        });
    }

//    private void BluetoothOFFMethod() {
//        btn_bt_off.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if(myBluetoothAdapter.isEnabled()){
//                    myBluetoothAdapter.disable();
//
//                }
//            }
//        });
//    }
//
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if (requestCode == requestCodeForEnable) {
//            if (resultCode == RESULT_OK) {
//                Toast.makeText(this, "Bluetooth is enabled", Toast.LENGTH_SHORT).show();
//            } else if (resultCode == RESULT_CANCELED) {
//                Toast.makeText(this, "Bluetooth enabling canceled", Toast.LENGTH_SHORT).show();
//            }
//        }
//    }
//
//    private void BluetoothOnMethod() {
//        btn_bt_on.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if(myBluetoothAdapter == null){
//                    Toast.makeText(MainActivity.this, "Bluetooth does not support on this devide", Toast.LENGTH_SHORT).show();
//                }else {
//                    if(!myBluetoothAdapter.isEnabled()){
//                        startActivityForResult(btEnablingIntent,requestCodeForEnable);
//                    }
//                }
//            }
//        });
//    }
}