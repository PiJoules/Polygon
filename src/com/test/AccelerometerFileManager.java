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
    private float[] accelData = new float[11];
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
    public static final int BUFFER_SIZE = 7;
    public static final int RANGE_MIN = 8;
    public static final int RANGE_MAX = 9;
    public static final int BINS = 10;
    

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
    public void changeAccelFileContents(float[] newData){
        // The text of the file
        String content =
                "filterType" + DELIMETER + newData[TYPE] + DELIMETER2 +
                "alpha" + DELIMETER + newData[ALPHA] + DELIMETER2 +
                "sma" + DELIMETER + newData[PERIODS] + DELIMETER2 +
                "threshold" + DELIMETER + newData[THRESHOLD] + DELIMETER2 +
                "x offset" + DELIMETER + newData[X_OFFSET] + DELIMETER2 +
                "y offset" + DELIMETER + newData[Y_OFFSET] + DELIMETER2 +
                "z offset" + DELIMETER + newData[Z_OFFSET] + DELIMETER2 +
                "buffer size" + DELIMETER + newData[BUFFER_SIZE] + DELIMETER2 +
                "range min" + DELIMETER + newData[RANGE_MIN] + DELIMETER2 +
                "range max" + DELIMETER + newData[RANGE_MAX] + DELIMETER2 +
                "bins" + DELIMETER + newData[BINS]
                ;

        this.accelContents = content;
        // Save new settings
        accelData = newData;
        // Write settings to file
        writeData(content);
    }
    
    // Method that deletes current contents of file to default values
    public void clearAccelFile(){
        changeAccelFileContents(new float[]{0, 1f, 1, 5f, 0, 0, 0, 50, .9f, 1.1f, 10f});
    }
    
    // Get filter from file
    public int getFilterType(){
        // 0 is the index of the filter type
        return (int) accelData[TYPE];
    }

    // Setter methods for filtering settings
    public void setFilter(int newType){
        accelData[TYPE] = newType;
        changeAccelFileContents(accelData);
    }

    public void setAlpha(float newAlpha){
        accelData[ALPHA] = newAlpha;
        changeAccelFileContents(accelData);
    }

    public void setPeriods(int newPeriods){
        accelData[PERIODS] = newPeriods;
        changeAccelFileContents(accelData);
    }

    public void setThreshold(float newThreshold){
        accelData[THRESHOLD] = newThreshold;
        changeAccelFileContents(accelData);
    }

    public void setOffset(float[] newOffset){
        accelData[X_OFFSET] = newOffset[0];
        accelData[Y_OFFSET] = newOffset[1];
        accelData[Z_OFFSET] = newOffset[2];
        changeAccelFileContents(accelData);
    }

    public void setBuffer(int newSize){
        accelData[BUFFER_SIZE] = newSize;
        changeAccelFileContents(accelData);
    }

    public void setMin(float newMin){
        accelData[RANGE_MIN] = newMin;
        changeAccelFileContents(accelData);
    }

    public void setMax(float newMax){
        accelData[RANGE_MAX] = newMax;
        changeAccelFileContents(accelData);
    }

    public void setBins(int newBins){
        accelData[BINS] = newBins;
        changeAccelFileContents(accelData);
    }
}

