package com.test;

// Event imports
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;

// Sensor imports
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

// OS imports
import android.os.Bundle;
import android.os.Handler;


public class Accelerometer implements SensorEventListener{
    // Accelerometer stuff

    // Accelerometer readings    
    private float xAccelFiltered, yAccelFiltered, zAccelFiltered, // Filtered sensor readings
                  xAccelUnfiltered, yAccelUnfiltered, zAccelUnfiltered; // Unfiltered sensor readings
    // The SensorManager 'mSensorManager' is required to enable and register sensors in the phone
    private SensorManager mSensorManager;
    // The SensorManager 'mSensorManager' is required to enable and register sensors in the phone
    private Sensor mAccelerometer;

    // Filtering settings
    // Moving average constant
    private float alpha;
    // Minimum accelerometer reading to consider non-zero
    private float threshold;

    public Accelerometer(Activity a){
    	// Initialize the accelerometer objects
        // Create the sensor manager
        mSensorManager = (SensorManager) a.getSystemService(Context.SENSOR_SERVICE);
        // Set the Sensor to the phone's accelerometer
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        // Have the SensorManager register and start gethering accelerometer data
        mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);

        // Initialize accelerometer values
        xAccelFiltered = 0.0f;
        yAccelFiltered = 0.0f;
        zAccelFiltered = 0.0f;
        xAccelUnfiltered = 0.0f;
        yAccelUnfiltered = 0.0f;
        zAccelUnfiltered = 0.0f;

        // Initialize filtering settings to default values
        alpha = 0.95f; // Large alpha for limited lag
        threshold = 9.81f/1000.0f; // Small threshold to detect small rotations
    }

    public void pause(){
        mSensorManager.unregisterListener(this);
    }

    public void resume(){
        mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
    }
    

    // Gets new accelerometer values and interprets and filters it
    public void onSensorChanged(SensorEvent event) {
        // Calculate net acceleration
        float resultant = (float) Math.sqrt(event.values[0]*event.values[0] +
                                            event.values[1]*event.values[1] + 
                                            event.values[2]*event.values[2]);
        
        // Get accleration components. Scale by 9.81/resultant so net acceleration is 1g
        xAccelFiltered = -event.values[0]/resultant*9.81f; // for xAccel, tilting right is negative, so take opposite
        yAccelFiltered = event.values[1]/resultant*9.81f;
        zAccelFiltered = event.values[2]/resultant*9.81f;
    }

    // onAccuracyChanged is an abstract method of the SensorEventManager interface, so it must be
    // implemented. However, no action is currently needed in this method
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    
    }

    // Getter methods for acceleration data. Returns an array
    public float[] getAccelFiltered(){
        return new float[]{xAccelFiltered, yAccelFiltered, zAccelFiltered};
    }

    public float[] getAccelUnfiltered(){
        return new float[]{xAccelUnfiltered, yAccelUnfiltered, zAccelUnfiltered};
    }

    // Getter and setter methods for alpha and threshold
    public float getAlpha(){
        return alpha;
    }

    public void setAlpha(float newAlpha){
        alpha = newAlpha;
    }

    public float getThreshold(){
        return threshold;
    }

    public void getThreshold(float newThreshold){
        threshold = newThreshold;
    }

}
