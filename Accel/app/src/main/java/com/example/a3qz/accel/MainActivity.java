package com.example.a3qz.accel;

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
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.Surface;
import android.view.WindowManager;
import com.example.a3qz.accel.HelloIntentService;

import java.io.FileOutputStream;

public class MainActivity extends AppCompatActivity implements SensorEventListener {
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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mSensorManager.registerListener(this, mSensor, SensorManager.SENSOR_DELAY_NORMAL);
        mWindowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        mDisplay = mWindowManager.getDefaultDisplay();
        mIntent = new Intent(this, HelloIntentService.class);
        startService(mIntent);
        sendBroadcast(mIntent);
        sendBroadcast(mIntent);
        sendBroadcast(mIntent);
    }
    @Override
    public void onSensorChanged(SensorEvent event){
        //Log.v("DATA", String.valueOf(event.values.length));
        String filename = "accel";
        String string = "Hello world!";
        FileOutputStream outputStream;
        Context context = this;
        IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        Intent batteryStatus = context.registerReceiver(null, ifilter);
        int level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
        int scale = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
        float batteryPct = level / (float)scale;

        KeyguardManager myKM = (KeyguardManager) context.getSystemService(Context.KEYGUARD_SERVICE);
        if( myKM.inKeyguardRestrictedInputMode()) {
            Log.v("lockstatus", "locked");
            //it is locked
        } else {
            Log.v("lockstatus", "unlocked");
            //it is not locked
        }
        DisplayManager dm = (DisplayManager) context.getSystemService(Context.DISPLAY_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            for (Display display : dm.getDisplays()) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT_WATCH) {
                    if (display.getState() != Display.STATE_OFF) {
                        Log.v("displaystatus", "on");
                    } else {
                        Log.v("displaystatus", "off");
                    }
                }
            }
        }
        Log.v("service", String.valueOf(batteryPct));
        Log.v("DATA", MainActivity.this.getFilesDir().getAbsolutePath());
        final float alpha = 0.8f;

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
        Log.v("DATA", mSensorTimeStamp  +" : " + String.valueOf(mSensorX)+ " : "+String.valueOf(mSensorY)+ " : "+String.valueOf(mSensorZ));
        try {
            outputStream = openFileOutput(filename, Context.MODE_APPEND);
            outputStream.write((mSensorTimeStamp  +" : " + String.valueOf(mSensorX)+ " : "+String.valueOf(mSensorY)+ " : "+String.valueOf(mSensorZ)+'\n').getBytes());
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        sendBroadcast(mIntent);
        // Isolate the force of gravity with the low-pass filter.
        /*gravity[0] = alpha * gravity[0] + (1 - alpha) * event.values[0];
        gravity[1] = alpha * gravity[1] + (1 - alpha) * event.values[1];
        gravity[2] = alpha * gravity[2] + (1 - alpha) * event.values[2];

        // Remove the gravity contribution with the high-pass filter.
        linear_acceleration[0] = event.values[0] - gravity[0];
        linear_acceleration[1] = event.values[1] - gravity[1];
        linear_acceleration[2] = event.values[2] - gravity[2];*/
    }
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }
}
