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

    private static SensorCenter sensorCenter;

    private static final String TAG = "sensor_center";

    private boolean hasHumiditySensor;
    private boolean hasTemperatureSensor;
    private boolean hasPressureSensor;

    private static float humidity;
    private static float temperature;
    private static float pressure;

    private SensorCenter(Activity activity) {
        sensorManager = (SensorManager) activity.getSystemService(Context.SENSOR_SERVICE);
        hasHumiditySensor = sensorManager.getDefaultSensor(Sensor.TYPE_RELATIVE_HUMIDITY) != null;
        hasTemperatureSensor = sensorManager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE) != null;
        hasPressureSensor = sensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE) != null;
    }

    public static SensorCenter getInstance(Activity activity) {
        if (sensorCenter == null)
            sensorCenter = new SensorCenter(activity);
        return sensorCenter;
    }

    public boolean canGetHumidity() {
        return hasHumiditySensor;
    }

    public boolean canGetTemperature() {
        return hasTemperatureSensor;
    }

    public boolean canGetPressure() {
        return hasPressureSensor;
    }

    public void enableSensors() {
        if (hasHumiditySensor)
            sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_RELATIVE_HUMIDITY), SensorManager.SENSOR_DELAY_NORMAL);
        if (hasTemperatureSensor)
            sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE), SensorManager.SENSOR_DELAY_NORMAL);
        if (hasPressureSensor)
            sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE), SensorManager.SENSOR_DELAY_NORMAL);
    }

    public void disableSensors() {
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        switch (event.sensor.getType()) {
            case Sensor.TYPE_RELATIVE_HUMIDITY:
                humidity = event.values[0];
                break;
            case Sensor.TYPE_AMBIENT_TEMPERATURE:
                temperature = event.values[0];
                break;
            case Sensor.TYPE_PRESSURE:
                pressure = event.values[0];
                break;
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {}

    public enum Data {
        NONE(-1),
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

    public double getData(Data type) {
        switch (type) {
            case HUMIDITY:
                return humidity;
            case TEMPERATURE:
                return temperature;
            case PRESSURE:
                return pressure;
            default:
                return 0;
        }
    }

    public static String printData(Data type) {
        switch (type) {
            case HUMIDITY:
                return "(humidity: " + humidity + ")";
            case TEMPERATURE:
                return "(temp.: " + temperature + ")";
            case PRESSURE:
                return "(pressure: " + pressure + ")";
            default:
                return "";
        }
    }

}
