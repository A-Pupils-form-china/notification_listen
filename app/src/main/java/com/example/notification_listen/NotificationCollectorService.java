package com.example.notification_listen;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.provider.ContactsContract;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.util.Log;

import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class NotificationCollectorService extends NotificationListenerService {
    Thread thread;
    public final String TAG = this.getClass().getName();

    String ip = null;
    MyReceive myReceive;
    public void onCreate(){
        super.onCreate();
        myReceive = new MyReceive();
        IntentFilter filter = new IntentFilter();
        filter.addAction("com.example.notification_listen.IP_ADDRESS");
        registerReceiver(myReceive, filter);
    }
    public void onDestroy(){
        unregisterReceiver(myReceive);
    }
    public void onNotificationPosted(StatusBarNotification sbn){
        Log.d("notification","receive");
        if(ip!=null) {
            thread = new Thread(() -> {
                try {
                    DataOutputStream dos;
                    Socket socket = new Socket(ip, 8888);

                    dos = new DataOutputStream(
                            new BufferedOutputStream(socket.getOutputStream()));
                    String data = sbn.getPackageName() + "&" + sbn.getNotification().extras.getString("android.title") + "&" + sbn.getNotification().extras.getString("android.text") + "&";
                    Log.d(TAG, data);
                    dos.write(data.getBytes());
                    dos.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            thread.start();
        }
    }
    class MyReceive extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getStringExtra("ip")!=null && NotificationCollectorService.this.ip==null){
                Log.d(TAG,"接收广播");
                NotificationCollectorService.this.ip=intent.getStringExtra("ip");
            }
            if(intent.hasExtra("command"))
            if(intent.getStringExtra("command").equals("change")){
                Log.d(TAG, "更改ip地址");
                NotificationCollectorService.this.ip=intent.getStringExtra("ip");
            }
        }
    }
}
