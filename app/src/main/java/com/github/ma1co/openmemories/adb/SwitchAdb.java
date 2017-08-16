package com.github.ma1co.openmemories.adb;

import android.app.Activity;
import android.content.Context;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.Gravity;
import android.widget.Toast;

import com.sony.scalar.hardware.indicator.Light;

public class SwitchAdb extends Activity {
    private static int[] tunOn = new int[] {200, 100, 200, 100, 200};
    private static int[] turnOff = new int[] {70, 50, 70, 50, 70};
    private int[] pattern;
    boolean enabled;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String[] adbStartCommand = { getApplicationInfo().nativeLibraryDir + "/libadbd.so" };

        WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);

        int state = wifiManager.getWifiState();
        enabled = !((state == WifiManager.WIFI_STATE_ENABLING || state == WifiManager.WIFI_STATE_ENABLED) && Procfs.findProcess(adbStartCommand) != -1);

        pattern = enabled ? tunOn : turnOff;

        ((WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE)).setWifiEnabled(enabled);

        CharSequence toastText = "Error switch ADB";
        try {
            if (enabled) {
                toastText = "ADB turn On";
                Shell.execAndroid(TextUtils.join(" ", adbStartCommand) + " &");
            } else {
                toastText = "ADB turn Off";
                int pid = Procfs.findProcess(adbStartCommand);
                if (Procfs.findProcess(adbStartCommand) != -1)
                    Shell.exec("kill -HUP " + pid);
            }
        } catch (NativeException e) {
            e.printStackTrace();
        }

        Toast toast = Toast.makeText(getApplicationContext(), toastText, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.TOP, 0, 0);
        toast.show();

        final Handler handler = new Handler();
        Runnable runnable = new Runnable() {
            int count = 0;
            int maxCount = pattern.length;
            boolean on = true;

            @Override
            public void run() {
                try {
                    if (count >= maxCount) {
                        Light.setState(Light.LID_SELF_TIMER, false);
                    } else {
                        Light.setState(Light.LID_SELF_TIMER, on);
                        on = !on;
                        handler.postDelayed(this, pattern[count]);
                        count++;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        runnable.run();

        finish();
    }
}
