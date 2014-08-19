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
    private float xAccelRaw, yAccelRaw, zAccelRaw, // Unchanged sensor readings
                  xAccelFiltered, yAccelFiltered, zAccelFiltered, // Filtered readings
                  xAccelUnfiltered, yAccelUnfiltered, zAccelUnfiltered, // Unfiltered but offset readings
                  xAccelTemp, yAccelTemp, zAccelTemp; // Variables to hold last acceleration readings

    // Estimated current velocity 
    private float xVel, yVel, zVel;
    
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
    
    // Averaging periods for SMA
    private int periods;
    
    // An array list containing previous accelerometer readings. Each entry is a set of readings
    // containing x acceleration, y acceleration, and z acceleration in that order
    private ArrayList<float[]> pastData;
    
    // Integer indicating which filter (or no filter) to use
    private int filterMode;

    // Additional filtering settings that control whether an offset is used and whether data is
    // normalized
    private boolean offset, normalize;
    

    /**
    * Object Constructor. Initializes all variables needed to get accelerometer readings and apply
    * appropriate filtering
    * @param a: The Android screen that is using the accelerometer
    * @param sel: The event handler for new accelerometer readings
    */
    public Accelerometer(Activity a, SensorEventListener sel, boolean shouldOffset, boolean shouldNormalize){
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
        filterMode = (int) settings[AccelerometerFileManager.TYPE];
        alpha = settings[AccelerometerFileManager.ALPHA];
        periods = (int) settings[AccelerometerFileManager.PERIODS];
        ax_off = settings[AccelerometerFileManager.X_OFFSET];
        ay_off = settings[AccelerometerFileManager.Y_OFFSET];
        az_off = settings[AccelerometerFileManager.Z_OFFSET];

        // Initialize accelerometer values
        xAccelFiltered = 0.0f;
        yAccelFiltered = 0.0f;
        zAccelFiltered = 0.0f;
        xAccelUnfiltered = 0.0f;
        yAccelUnfiltered = 0.0f;
        zAccelUnfiltered = 0.0f;
        xAccelTemp = 0.0f;
        yAccelTemp = 0.0f;
        zAccelTemp = 0.0f;
        xAccelRaw = 0.0f;
        yAccelRaw = 0.0f;
        zAccelRaw = 0.0f;

        // Initialize velocity
        xVel = 0.0f;
        yVel = 0.0f;
        zVel = 0.0f;

        // Initialize array list of past readings
        pastData = new ArrayList<float[]>();

        // Save normalization setting. If this is true, the resultant will always be 1g
        normalize = shouldNormalize;
        offset = shouldOffset;
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

        // Save raw acceleration values
        xAccelRaw = -event.values[0];
        yAccelRaw = -event.values[1];
        zAccelRaw = event.values[2];

        // Save unfiltered data
        // Y and X flipped and negated for landscape orientation. 
        if(offset){
            // Add in offset to change zero position
            yAccelUnfiltered = xAccelRaw - ax_off;
            xAccelUnfiltered = yAccelRaw - ay_off;
            zAccelUnfiltered = zAccelRaw - az_off;
        }
        else{
            // Do not apply offset to change zero position
            yAccelUnfiltered = xAccelRaw;
            xAccelUnfiltered = yAccelRaw;
            zAccelUnfiltered = zAccelRaw;   
        }

        // Add unfiltered data to history
        pastData.add(new float[]{xAccelUnfiltered, yAccelUnfiltered, zAccelUnfiltered});

        // Check if filtering is turned on
        if(filterMode != AccelerometerFileManager.NONE){
            // Filtering is on, check which type to use
            if(filterMode == AccelerometerFileManager.EMA){
                // Apply exponential moving average filter
                xAccelFiltered = alpha*xAccelUnfiltered + (1.0f-alpha)*xAccelFiltered;
                yAccelFiltered = alpha*yAccelUnfiltered + (1.0f-alpha)*yAccelFiltered;
                zAccelFiltered = alpha*zAccelUnfiltered + (1.0f-alpha)*zAccelFiltered;
            }
            else{
                // Apply simple moving average filter
                // Summing variables
                float xSum = 0f;
                float ySum = 0f;
                float zSum = 0f;

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


        // Perform trapezoidal numerical intergration on acceleration values to estimate current velocity
        xVel = xVel + .5f*(xAccelFiltered + xAccelTemp)*.02f;
        yVel = yVel + .5f*(yAccelFiltered + yAccelTemp)*.02f;
        // Subtract gravity from z velocity
        zVel = zVel + .5f*(zAccelFiltered + zAccelTemp - 9.81f)*.02f;

        // Save current acceleration readings for use in integration
        xAccelTemp = xAccelFiltered;
        yAccelTemp = yAccelFiltered;
        zAccelTemp = zAccelFiltered;


        // Additional filtering needed for our app. Since the magnitude of the acceleration is not
        // necesarilly 1g, the acceleration is scaled so that it is and the acceleration components
        // act as tilt angles
        if(normalize){
            // Calculate net acceleration by 
            float resultant = (float) Math.sqrt(xAccelFiltered*xAccelFiltered +
                                                yAccelFiltered*yAccelFiltered + 
                                                zAccelFiltered*zAccelFiltered);
            
            // Get accleration components. Scale by 9.81/resultant so net acceleration is 1g
            xAccelFiltered = xAccelFiltered/resultant*9.81f;
            yAccelFiltered = yAccelFiltered/resultant*9.81f;
            zAccelFiltered = zAccelFiltered/resultant*9.81f;
        }
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
        if(filterMode != AccelerometerFileManager.NONE){
            return new float[]{xAccelFiltered, yAccelFiltered, zAccelFiltered};
        }
        // Filtering is off, return unfiltered data
        return new float[]{xAccelUnfiltered, yAccelUnfiltered, zAccelUnfiltered};
    }

    // Getter method for velocity calculated from numerical intergration
    public float[] getVelocity(){
        return new float[]{xVel, yVel, zVel};
    }

    // Set the current accelerometer readings as the zero position where the circle will not move
    public void calibrate(){
        // Record current acceleration values to use as offsets
        ax_off = xAccelRaw;
        ay_off = yAccelRaw;
        az_off = zAccelRaw - 9.81f;
        afm.setOffset(new float[]{ax_off, ay_off, az_off});
        xVel = 0f;
        yVel = 0f;
        zVel = 0f;
    }

    // Set the zero position back to the default position
    public void zero(){
        ax_off = 0f;
        ay_off = 0f;
        az_off = 0f;
        afm.setOffset(new float[]{0f,0f,0f});
        xVel = 0f;
        yVel = 0f;
        zVel = 0f;
    }


    // Getter and setter methods for filter mode
    public int getFilter(){
        return filterMode;
    }

    // Filter is defined by class constants EMA and SMA
    public void setFilter(int filter){
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

}
