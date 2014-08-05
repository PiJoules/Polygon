package com.test;

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
    private float xAccelFiltered, yAccelFiltered, zAccelFiltered, // Filtered readings
                  xAccelUnfiltered, yAccelUnfiltered, zAccelUnfiltered; // Unfiltered readings
    
    // The SensorManager 'mSensorManager' is required to enable and register sensors in the phone
    private SensorManager mSensorManager;
    // The accelerometer object which is used to get new readings
    private Sensor mAccelerometer;
    
    // The SensorEventListener 'sel' is required to enable and register sensors in the phone
    private SensorEventListener sel;

    // Filtering settings
    // Static constants representing the filtering method. Used so other objects can easily switch
    // between filter modes using these constants
    public static final boolean EMA = true; // alpha
    public static final boolean SMA = false; // sma
    
    // Exponential moving average constant
    private float alpha;
    
    // Averaging periods for SMA
    private int periods;
    
    // An array list containing previous accelerometer readings. Each entry is a set of readings
    // containing x acceleration, y acceleration, and z acceleration in that order
    private ArrayList<float[]> pastData;
    
    // boolean indicating whether or not filter should be used
    private boolean shouldFilter;
    
    // boolean indicating which filter to use
    private boolean filterMode;
    

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


        // Initialize accelerometer values
        xAccelFiltered = 0.0f;
        yAccelFiltered = 0.0f;
        zAccelFiltered = 0.0f;
        xAccelUnfiltered = 0.0f;
        yAccelUnfiltered = 0.0f;
        zAccelUnfiltered = 0.0f;

        // Initialize filtering settings to default values
        alpha = 0.95f; // Large alpha for limited lag
        periods = 5;   // Small to limit lag
        pastData = new ArrayList<float[]>(); // Contains last [periods] readings
        shouldFilter = true; // Filtering on by default
        filterMode = EMA; // Default to EMA filter
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
    * This method is called on every new accelerometer reading. It applies filtering according to
    * the settings
    * @param event: the actual event containing an array of the x, y, and z accelerations
    */
    public void updateAccelVals(SensorEvent event){
        // Check if pastData contains correct number of readings. Remove extras accoringly
        if(periods < pastData.size()){
            
            // Too many readings, delete extra
            int excessReadings = pastData.size() - periods + 1; // + 1 because new reading will be added
            for(int i = 0; i < excessReadings; i++){
                pastData.remove(0);
            }
        }
        else if(periods == pastData.size()){
            // Delete oldest reading
            pastData.remove(0);
        }
        // Else, readings should only be added, so removing step is skipped

        // Save raw unfiltered data
        yAccelUnfiltered = -event.values[0]; // yAccel negated because tilting right is negative
        xAccelUnfiltered = -event.values[1];
        zAccelUnfiltered = event.values[2];

        // Add unfiltered data to history
        pastData.add(new float[]{xAccelUnfiltered, yAccelUnfiltered, zAccelUnfiltered});

        // Check if filtering is turned on
        if(shouldFilter){
            // Filtering is on, check which type to use
            if(filterMode == EMA){
                // Apply exponential moving average filter
                xAccelFiltered = alpha*xAccelUnfiltered + (1.0f-alpha)*xAccelFiltered;
                yAccelFiltered = alpha*yAccelUnfiltered + (1.0f-alpha)*yAccelFiltered;
                zAccelFiltered = alpha*zAccelUnfiltered + (1.0f-alpha)*zAccelFiltered;
            }
            else{
                // Apply simple moving average filter
                // Summing variables
                float xSum = 0;
                float ySum = 0;
                float zSum = 0;

                // Sum past data
                for(float[] data : pastData){
                    xSum += data[0];
                    ySum += data[1];
                    zSum += data[2];
                }
                
                // Calculate average acceleration                
                xAccelFiltered = xSum/periods;
                yAccelFiltered = ySum/periods;
                zAccelFiltered = zSum/periods;
            }
        }
        else{
            // Filtering is off. Must set filtered readings, so they are set to unfiltered readings
            xAccelFiltered = xAccelUnfiltered;
            yAccelFiltered = yAccelUnfiltered;
            zAccelFiltered = zAccelUnfiltered;
        }

        // Additional filtering needed for our app. Since the magnitude of the acceleration is not
        // necesarilly 1g, the acceleration is scaled so that it is and the acceleration components
        // act as tilt angles

        // Calculate net acceleration by 
        float resultant = (float) Math.sqrt(xAccelFiltered*xAccelFiltered +
                                            yAccelFiltered*yAccelFiltered + 
                                            zAccelFiltered*zAccelFiltered);
        
        // Get accleration components. Scale by 9.81/resultant so net acceleration is 1g
        xAccelFiltered = xAccelFiltered/resultant*9.81f;
        yAccelFiltered = yAccelFiltered/resultant*9.81f;
        zAccelFiltered = zAccelFiltered/resultant*9.81f;
    }


    // Getter methods for acceleration data. Returns an array
    public float[] getAccelFiltered(){
        return new float[]{xAccelFiltered, yAccelFiltered, zAccelFiltered};
    }

    public float[] getAccelUnfiltered(){
        return new float[]{xAccelUnfiltered, yAccelUnfiltered, zAccelUnfiltered};
    }

    // Getter method for acceleration method that does not specify which data it wants
    public float[] getAccel(){
        // Filtering is on, return filtered data
        if(shouldFilter){
            return new float[]{xAccelFiltered, yAccelFiltered, zAccelFiltered};
        }
        // Filtering is off, return unfiltered data
        return new float[]{xAccelUnfiltered, yAccelUnfiltered, zAccelUnfiltered};
    }

    // Getter and setter methods for filter mode
    public boolean getFilter(){
        return filterMode;
    }

    // Filter is defined by class constants EMA and SMA
    public void setFilter(boolean filter){
        filterMode = filter;
    }
    

    // Getter and setter methods for alpha and SMA averaging periods
    public float getAlpha(){
        return alpha;
    }

    public void setAlpha(float newAlpha){
        alpha = newAlpha;
    }

    public int getPeriods(){
        return periods;
    }

    public void setPeriods(int newNum){
        periods = newNum;
    }


    // Getter and setter methods for whether or not filtering is on
    public void setShouldFilter(boolean set){
        this.shouldFilter = set;
    }
    
    public boolean getShouldFilter(){
        return this.shouldFilter;
    }
}
