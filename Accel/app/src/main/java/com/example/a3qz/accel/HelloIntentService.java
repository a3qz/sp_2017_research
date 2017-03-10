package com.example.a3qz.accel;

import android.app.IntentService;
import android.app.KeyguardManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.hardware.display.DisplayManager;
import android.os.BatteryManager;
import android.os.Build;
import android.util.Log;
import android.view.Display;
import android.view.Surface;
import android.view.WindowManager;
import android.widget.Toast;

/**
 * Created by a3qz on 2/22/17.
 */

public class HelloIntentService extends IntentService implements SensorEventListener {
    private SensorManager mSensorManager;
    private Sensor mSensor;
    private Display mDisplay;
    private WindowManager mWindowManager;
    private float mSensorX;
    private float mSensorY;
    private float mSensorZ;
    private long mSensorTimeStamp;
    private long mCpuTimeStamp;
    Intent mIntent;
    /**
     * A constructor is required, and must call the super IntentService(String)
     * constructor with a name for the worker thread.
     */
    public HelloIntentService() {
        super("HelloIntentService");
    }

    /**
     * The IntentService calls this method from the default worker thread with
     * the intent that started the service. When this method returns, IntentService
     * stops the service, as appropriate.
     */
    @Override
    protected void onHandleIntent(Intent intent) {
        // Normally we would do some work here, like download a file.
        // For our sample, we just sleep for 5 seconds.
        try {
            Thread.sleep(5000);
            Context context = this;
            KeyguardManager myKM = (KeyguardManager) context.getSystemService(Context.KEYGUARD_SERVICE);
            if( myKM.inKeyguardRestrictedInputMode()) {
                Log.v("service2", "locked");
                //it is locked
            } else {
                Log.v("service2", "unlocked");
                //it is not locked
            }
            IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
            Intent batteryStatus = context.registerReceiver(null, ifilter);
            int level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
            int scale = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
            float batteryPct = level / (float)scale;

            DisplayManager dm = (DisplayManager) context.getSystemService(Context.DISPLAY_SERVICE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                for (Display display : dm.getDisplays()) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT_WATCH) {
                        if (display.getState() != Display.STATE_OFF) {
                            Log.v("service2", "on");
                        } else {
                            Log.v("service2", "off");
                        }
                    }
                }
            }
            Log.v("service2", mSensorTimeStamp  +" : " + String.valueOf(mSensorX)+ " : "+String.valueOf(mSensorY)+ " : "+String.valueOf(mSensorZ));


            Log.v("service2", String.valueOf(batteryPct));
            Log.v("service2", "made it");
            mIntent = new Intent(this, HelloIntentService.class);
            startService(mIntent);
            sendBroadcast(mIntent);
            //Toast.makeText(this, "here", Toast.LENGTH_SHORT).show();
        } catch (InterruptedException e) {
            // Restore interrupt status.
            Thread.currentThread().interrupt();
        }
        //Toast.makeText(this, "here", Toast.LENGTH_SHORT).show();
        //Log.v("service2", "made it");

    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Toast.makeText(this, "service starting", Toast.LENGTH_SHORT).show();
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mSensorManager.registerListener(this, mSensor, SensorManager.SENSOR_DELAY_NORMAL);
        mWindowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        mDisplay = mWindowManager.getDefaultDisplay();
        return super.onStartCommand(intent,flags,startId);
    }

    @Override
    public void onSensorChanged(SensorEvent event){

        switch (mDisplay.getRotation()) {
            case Surface.ROTATION_0:
                mSensorX = event.values[0];
                mSensorY = event.values[1];
                mSensorZ = event.values[2];
                break;
            case Surface.ROTATION_90:
                mSensorX = -event.values[1];
                mSensorY = event.values[0];
                mSensorZ = event.values[2];
                break;
            case Surface.ROTATION_180:
                mSensorX = -event.values[0];
                mSensorY = -event.values[1];
                mSensorZ = event.values[2];
                break;
            case Surface.ROTATION_270:
                mSensorX = event.values[1];
                mSensorY = -event.values[0];
                mSensorZ = event.values[2];
                break;
        }
        mSensorTimeStamp = event.timestamp;
        mCpuTimeStamp = System.nanoTime();
        //Log.v("service2", "accel");
        //Log.v("service2", mSensorTimeStamp  +" : " + String.valueOf(mSensorX)+ " : "+String.valueOf(mSensorY)+ " : "+String.valueOf(mSensorZ));

    }
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }
}