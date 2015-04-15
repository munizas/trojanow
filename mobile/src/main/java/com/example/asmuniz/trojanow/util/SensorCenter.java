package com.example.asmuniz.trojanow.util;

import android.hardware.Sensor;

/**
 * Created by asmuniz on 3/30/15.
 *
 * Contains common operations that will be perform regarding Sensors.
 */
public class SensorCenter {

    enum Data {
        TEMPERATURE(Sensor.TYPE_AMBIENT_TEMPERATURE),
        HUMIDITY(Sensor.TYPE_RELATIVE_HUMIDITY);

        int androidSensorType;

        Data(int androidSensorType) {
            this.androidSensorType = androidSensorType;
        }

        public int getSensorType() {
            return androidSensorType;
        }
    }

    public static void getData(Data type) {

    }

}
