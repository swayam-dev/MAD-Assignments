package com.example.sensors;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    // SensorManager gives access to device sensors
    SensorManager sensorManager;

    // Sensor objects
    Sensor accelerometer;
    Sensor lightSensor;
    Sensor proximitySensor;

    // TextViews from your XML
    TextView acc_x, acc_y, acc_z;
    TextView light_id;
    TextView proxy_Distance, proxy_State;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Link TextViews to XML IDs
        acc_x          = findViewById(R.id.acc_x);
        acc_y          = findViewById(R.id.acc_y);
        acc_z          = findViewById(R.id.acc_z);
        light_id       = findViewById(R.id.light_value);
        proxy_Distance = findViewById(R.id.proximity_value);
        proxy_State    = findViewById(R.id.proximity_status);

        // Get the SensorManager system service
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

        // Get each sensor
        accelerometer   = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        lightSensor     = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        proximitySensor = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Register listeners — starts receiving sensor data
        if (accelerometer != null)
            sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);

        if (lightSensor != null)
            sensorManager.registerListener(this, lightSensor, SensorManager.SENSOR_DELAY_NORMAL);

        if (proximitySensor != null)
            sensorManager.registerListener(this, proximitySensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        super.onPause();

        // Unregister to save battery when app is in background
        sensorManager.unregisterListener(this);
    }

    // This method is called automatically whenever a sensor value changes
    @Override
    public void onSensorChanged(SensorEvent event) {

        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];

            acc_x.setText("X-Axis: " + String.format("%.2f", x) + " m/s²");
            acc_y.setText("Y-Axis: " + String.format("%.2f", y) + " m/s²");
            acc_z.setText("Z-Axis: " + String.format("%.2f", z) + " m/s²");
        }

        else if (event.sensor.getType() == Sensor.TYPE_LIGHT) {
            float lux = event.values[0];

            light_id.setText("Illuminance: " + String.format("%.2f", lux) + " lx");
        }

        else if (event.sensor.getType() == Sensor.TYPE_PROXIMITY) {
            float distance = event.values[0];

            proxy_Distance.setText("Distance: " + String.format("%.2f", distance) + " cm");

            // Most phones return 0 = NEAR, non-zero = FAR
            if (distance == 0) {
                proxy_State.setText("State: NEAR");
            } else {
                proxy_State.setText("State: FAR");
            }
        }
    }

    // Required method — we don't need it but must include it
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Not used
    }
}