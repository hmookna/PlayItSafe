package com.example.playitsafe;
import android.app.Activity;
import android.app.KeyguardManager;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.PowerManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.example.playitsafe.DBconnect.SessionManager;

/**
 * Created by Mook on 28/02/2016.
 */
public class BluetoothReceiver extends BroadcastReceiver {
    public SessionManager session;

    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

        if (BluetoothDevice.ACTION_ACL_CONNECTED.equals(action)) {

            //Device is now connected
            Toast.makeText(context, "ACTION_ACL_CONNECTED " + device, Toast.LENGTH_LONG).show();
            wakeDevice(context);
            session = new SessionManager(context);
            session.setIsShortcut(true);
            PackageManager pm = context.getPackageManager();
            Intent i = pm.getLaunchIntentForPackage(context.getPackageName());
            context.startActivity(i);

        }

    }

    public void wakeDevice(Context context) {
        //To wake up the screen
        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        PowerManager.WakeLock wakeLock = pm.newWakeLock((PowerManager.SCREEN_BRIGHT_WAKE_LOCK | PowerManager.FULL_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP), "TAG");
        wakeLock.acquire();

        //To release the screen lock:
        KeyguardManager keyguardManager = (KeyguardManager) context.getSystemService(Context.KEYGUARD_SERVICE);
        KeyguardManager.KeyguardLock keyguardLock = keyguardManager.newKeyguardLock("TAG");
        keyguardLock.disableKeyguard();

    }

}

