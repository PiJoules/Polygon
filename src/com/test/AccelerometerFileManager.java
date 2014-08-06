package com.test;

// Android import
import android.content.Context;

// A class to handle reading and writing to the accelerometer settings file
public class AccelerometerFileManager extends FileManager {
    // Name of file to save settings to    
    private final String ACCEL_FILE = "accel.txt";
    // The contents of the settings file
    private String accelContents = "";
    // The array of actual numbers contained in the file
    private float[] accelData = new float[7];
    // Constants representing each type of filter
    public static final int NONE = 0;
    public static final int EMA = 1;
    public static final int SMA = 2;
    // Constants indicating the array index of each setting in accelContents
    public static final int TYPE = 0;
    public static final int ALPHA = 1;
    public static final int PERIODS = 2;
    public static final int THRESHOLD = 3;
    public static final int X_OFFSET = 4;
    public static final int Y_OFFSET = 5;
    public static final int Z_OFFSET = 6;
    

    // Constuctor
    public AccelerometerFileManager(Context context, String fileName) {
        // Call constructor of FileManager
        super(context, fileName);
        // Set name of file to save to
        this.setFile(ACCEL_FILE);
        
        // Check if file exists
        if (readSavedData()){
            // Read in contents of file and save them
            accelContents = this.getContents();
        }
        else {
            // File does not exist
            clearAccelFile();
        }
        // Removes formatting from file contents
        parseAccelFileContents();
    }
    
    // Read content of settings file
    private void parseAccelFileContents(){
        // Remove delimter to get lines of file
        String[] rows = accelContents.split(DELIMETER2);
        // Go through lines
        for (int i = 0; i < rows.length; i++){
            // Remove formatting and save filtering setting value
            accelData[i] = Float.parseFloat(rows[i].split(DELIMETER)[1]);
        }
    }
    
    // Get the filter settings
    public float[] getAccelData(){
        return this.accelData;
    }
    
    // Method to edit the filtering settings
    public void changeAccelFileContents(int filterType, float alpha, int sma, float threshold, float[] offset){
        // The text of the file
        String content =
                "filterType" + DELIMETER + filterType + DELIMETER2 +
                "alpha" + DELIMETER + alpha + DELIMETER2 +
                "sma" + DELIMETER + sma + DELIMETER2 +
                "threshold" + DELIMETER + threshold + DELIMETER2 +
                "x offset" + DELIMETER + offset[0] + DELIMETER2 +
                "y offset" + DELIMETER + offset[1] + DELIMETER2 +
                "z offset" + DELIMETER + offset[2]
                ;

        this.accelContents = content;
        // Save the filter type
        // 0 = no filter, 1 = alpha, 2 = sma
        accelData[TYPE] = (float) filterType;
        // Save the alpha constant
        accelData[ALPHA] = alpha;
        // Save the number of periods for the SMA
        accelData[PERIODS] = (float) sma;
        // Save the threshold value to use
        accelData[THRESHOLD] = threshold;
        // Save the acceleration offset values
        accelData[X_OFFSET] = offset[0];
        accelData[Y_OFFSET] = offset[1];
        accelData[Z_OFFSET] = offset[2];
        // Write settings to file
        writeData(content);
    }
    
    // Method that deletes current contents of file to default values
    public void clearAccelFile(){
        changeAccelFileContents(0, 1f, 1, 5f, new float[]{0, 0, 0});
    }
    
    // Get filter from file
    public int getFilterType(){
        // 0 is the index of the filter type
        return (int) accelData[TYPE];
    }

    // Setter methods for filtering settings
    public void setFilter(int newType){
        changeAccelFileContents(newType, accelData[ALPHA], (int) accelData[PERIODS], accelData[THRESHOLD],
            new float[]{accelData[X_OFFSET], accelData[Y_OFFSET], accelData[Z_OFFSET]});
    }

    public void setAlpha(float newAlpha){
        changeAccelFileContents((int) accelData[TYPE], newAlpha, (int) accelData[PERIODS], accelData[THRESHOLD],
            new float[]{accelData[X_OFFSET], accelData[Y_OFFSET], accelData[Z_OFFSET]});
    }

    public void setPeriods(int newPeriods){
        changeAccelFileContents((int) accelData[TYPE], accelData[ALPHA], newPeriods, accelData[THRESHOLD],
            new float[]{accelData[X_OFFSET], accelData[Y_OFFSET], accelData[Z_OFFSET]});
    }

    public void setThreshold(float newThreshold){
        changeAccelFileContents((int) accelData[TYPE], accelData[ALPHA], (int) accelData[PERIODS], newThreshold,
            new float[]{accelData[X_OFFSET], accelData[Y_OFFSET], accelData[Z_OFFSET]});
    }

    public void setOffset(float[] newOffset){
        changeAccelFileContents((int) accelData[TYPE], accelData[ALPHA], (int) accelData[PERIODS], accelData[THRESHOLD],
            newOffset);
    }
}

