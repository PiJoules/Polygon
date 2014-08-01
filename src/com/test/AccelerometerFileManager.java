package com.test;

// Android import
import android.content.Context;

// A class to handle reading and writing to the accelerometer settings file
public class AccelerometerFileManager extends FileManager {
    // Name of file to save settings to    
    private final String ACCELFILE = "accel.txt";
    // The contents of the settings file
    private String accelContents = "";
    // The array of actual numbers contained in the file
    private float[] accelData = new float[4];

    // Constuctor
    public AccelerometerFileManager(Context context, String fileName) {
        // Call constructor of FileManager
        super(context, fileName);
        // Set name of file to save to
        this.setFile(ACCELFILE);
        
        // Check if file exists
        if (readSavedData()){
            // Read in contents of file and save them
            accelContents = this.getContents();
        }
        else {
            // File does not exits
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
    public void changeAccelFileContents(int filterType, float alpha, int sma, float threshold){
        // The text of the file
        String content =
                "filterType" + DELIMETER + filterType + DELIMETER2 +
                "alpha" + DELIMETER + alpha + DELIMETER2 +
                "sma" + DELIMETER + sma + DELIMETER2 +
                "threshold" + DELIMETER + threshold
                ;

        this.accelContents = content;
        // Save the filter type
        // 0 = no filter, 1 = alpha, 2 = sma
        accelData[0] = (float) filterType;
        // Save the alpha constant
        accelData[1] = alpha;
        // Save the number of periods for the SMA
        accelData[2] = (float) sma;
        // Save the threshold value to use
        accelData[3] = threshold;
        // Write settings to file
        writeData(content);
    }
    
    // Method that deletes current contents of file to default values
    public void clearAccelFile(){
        changeAccelFileContents(0, 1f, 1, 5f);
    }
    
    // Get filter from 
    public int getFilterType(){
        // 0 is the index of the filter type
        return (int) accelData[0];
    }
    
}
