package com.test.Polygon;

// Event imports
import android.app.Activity;
import android.content.Context;

// Sensor imports
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

// Java imports
import java.util.ArrayList;


public class Accelerometer{
    // Accelerometer readings    
    private float xAccel, yAccel, zAccel;

    // The SensorManager 'mSensorManager' is required to enable and register sensors in the phone
    private SensorManager mSensorManager;
    // The accelerometer object which is used to get new readings
    private Sensor mAccelerometer;
    
    // The SensorEventListener 'sel' is required to enable and register sensors in the phone
    private SensorEventListener sel;

    // File manager for handling the text file that stores accelerometer and filtering settings
    private AccelerometerFileManager afm;

    // Acceleration offsets to use in calibrating the accelerometer for a different zero position
    private float ax_off, ay_off, az_off;
    
    // Exponential moving average constant
    private float alpha;

    /**
    * Object Constructor. Initializes all variables needed to get accelerometer readings and apply
    * appropriate filtering
    * @param a: The Android screen that is using the accelerometer
    * @param sel: The event handler for new accelerometer readings
    */
    public Accelerometer(Activity a, SensorEventListener sel){
    	// Initialize the accelerometer objects

        // Create the sensor manager
        mSensorManager = (SensorManager) a.getSystemService(Context.SENSOR_SERVICE);
        
        // Set the Sensor to the phone's accelerometer
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        
        // Have the SensorManager register and start gethering accelerometer data
        // Uses SENSOR_DELAY_GAME to obtain more frequent accelerometer readings (50 Hz)
        mSensorManager.registerListener(sel, mAccelerometer, SensorManager.SENSOR_DELAY_GAME);
        // Saves the sensor event listener
        this.sel = sel;


        // Initiliaze the accelerometer file manager
        afm = new AccelerometerFileManager(a.getApplicationContext(), "");
        // Get settings from accelerometer settings file
        float[] settings = afm.getAccelData();

        // Initialize filtering settings from file contents
        alpha = settings[AccelerometerFileManager.ALPHA];
        ax_off = settings[AccelerometerFileManager.X_OFFSET];
        ay_off = settings[AccelerometerFileManager.Y_OFFSET];
        az_off = settings[AccelerometerFileManager.Z_OFFSET];

        // Initialize accelerometer values
        xAccel = 0.0f;
        yAccel = 0.0f;
        zAccel = 0.0f;
    }


    // Method called when the screen (Activity) is exited
    public void pause(){
        // Stop event listener when app is not active to prevent unecessary cpu use
        mSensorManager.unregisterListener(sel);
    }

    // Method called when the screen (Activity) is reopened
    public void resume(){
        // Register event handler for sensor updates
        mSensorManager.registerListener(sel, mAccelerometer, SensorManager.SENSOR_DELAY_GAME);
    }

    
    /**
    * This method is called on every new accelerometer reading.
    * @param event: the actual event containing an array of the x, y, and z accelerations
    */
    public void updateAccelVals(SensorEvent event){
        // Y and X flipped and negated for landscape orientation. 
        // Add in offset to change zero position
        xAccel = alpha*(-event.values[1] - ax_off) + (1.0f-alpha)*xAccel;
        yAccel = alpha*(-event.values[0] - ay_off) + (1.0f-alpha)*yAccel;
        zAccel = alpha*(event.values[2] - az_off) + (1.0f-alpha)*zAccel;

        float resultant = (float) Math.sqrt(xAccel*xAccel +
                                            yAccel*yAccel + 
                                            zAccel*zAccel);
            
        // Get accleration components. Scale by 9.81/resultant so net acceleration is 1g
        xAccel = xAccel/resultant*9.81f;
        yAccel = yAccel/resultant*9.81f;
        zAccel = zAccel/resultant*9.81f;
    }

    // Getter method for acceleration method that does not specify which data it wants
    public float[] getAccel(){
        return new float[]{xAccel, yAccel, zAccel};
    }

    // Set the current accelerometer readings as the zero position where the circle will not move
    public void calibrate(){
        // Record current acceleration values to use as offsets
        ax_off = xAccel;
        ay_off = yAccel;
        az_off = zAccel - 9.81f;
        afm.setOffset(new float[]{ax_off, ay_off, az_off});
    }

    // Set the zero position back to the default position
    public void zero(){
        ax_off = 0f;
        ay_off = 0f;
        az_off = 0f;
        afm.setOffset(new float[]{0f,0f,0f});
    }

    // Getter and setter methods for alpha 
    public float getAlpha(){
        return alpha;
    }

    public void setAlpha(float newAlpha){
        alpha = newAlpha;
    }

}
