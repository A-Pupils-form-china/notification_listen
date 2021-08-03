package com.example.notification_listen;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.service.notification.NotificationListenerService;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    public final String TAG = this.getClass().getName();
    EditText ed1;
    TextView tv1;
    TextView tv2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        String string = Settings.Secure.getString(getContentResolver(),"enabled_notification_listeners");
        if (!string.contains(NotificationCollectorService.class.getName())) {   //判断是否开启监听通知权限
            startActivity(new Intent(
                    "android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS"));
        }
        toggleNotificationListenerService();  //重启监听服务
        ed1 = findViewById(R.id.edit);
        tv1 = findViewById(R.id.TV1);
        tv2 = findViewById(R.id.TV2);
    }

    public void startListen(View view){
        if(ed1.getText().toString().length()>"192.168.0.1".length()){
            Intent i = new Intent("com.example.notification_listen.IP_ADDRESS");
            i.putExtra("ip",ed1.getText().toString());
            sendBroadcast(i);
            tv1.setText(R.string.start);
            tv2.setText(("当前主机ip："+ed1.getText().toString()));
            Log.d(TAG,"开始监听");
        }
    }
    public void change_ip_address(View view){
        if(ed1.getText().toString().length()>"192.168.0.1".length()){
            Intent i = new Intent("com.example.notification_listen.IP_ADDRESS");
            i.putExtra("ip", ed1.getText().toString());
            i.putExtra("command","change");
            tv2.setText(("当前主机ip："+ed1.getText().toString()));
            sendBroadcast(i);
            Log.d(TAG,"修改IP");
        }
    }
    private void toggleNotificationListenerService() {
        PackageManager pm = getPackageManager();
        pm.setComponentEnabledSetting(
                new ComponentName(this, NotificationCollectorService.class),
                PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
        pm.setComponentEnabledSetting(
                new ComponentName(this, NotificationCollectorService.class),
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);
    }
}