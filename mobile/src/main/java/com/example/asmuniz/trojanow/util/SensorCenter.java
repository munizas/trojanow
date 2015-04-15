package com.example.asmuniz.trojanow.util;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

/**
 * Created by asmuniz on 3/30/15.
 *
 * Contains common operations that will be perform regarding Sensors.
 */
public class SensorCenter implements SensorEventListener {

    private static Sensor sensor;
    private static SensorManager sensorManager;
    private static double sensorData = Double.MIN_VALUE;

    private static SensorCenter sensorCenter = new SensorCenter();

    private SensorCenter() {}

    public static SensorCenter getInstance() {
        return sensorCenter;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        sensorData = event.values[0];
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    public enum Data {
        TEMPERATURE(Sensor.TYPE_AMBIENT_TEMPERATURE),
        HUMIDITY(Sensor.TYPE_RELATIVE_HUMIDITY),
        PRESSURE(Sensor.TYPE_PRESSURE);

        int androidSensorType;

        Data(int androidSensorType) {
            this.androidSensorType = androidSensorType;
        }

        public int getSensorType() {
            return androidSensorType;
        }
    }

    public double getData(Activity activity, Data type) {
        sensorManager = (SensorManager) activity.getSystemService(Context.SENSOR_SERVICE);
        sensor = sensorManager.getDefaultSensor(type.getSensorType());
        sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL);
        //while (sensorData == Double.MIN_VALUE)
        //    ;
        sensorManager.unregisterListener(this);
        double reading = sensorData;
        sensorData = Double.MIN_VALUE;
        return reading;
    }

}
