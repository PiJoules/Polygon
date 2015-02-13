package com.test.Polygon;

// Android import
import android.content.Context;

// A class to handle reading and writing to the accelerometer settings file
public class AccelerometerFileManager extends FileManager {
    // Name of file to save settings to    
    private final String ACCEL_FILE = "accel.txt";
    // The contents of the settings file
    private String accelContents = "";
    // The array of actual numbers contained in the file
    private float[] accelData = new float[4];
    // Constants indicating the array index of each setting in accelContents
    public static final int ALPHA = 0;
    public static final int X_OFFSET = 1;
    public static final int Y_OFFSET = 2;
    public static final int Z_OFFSET = 3;    

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
                "alpha" + DELIMETER + newData[ALPHA] + DELIMETER2 +
                "x offset" + DELIMETER + newData[X_OFFSET] + DELIMETER2 +
                "y offset" + DELIMETER + newData[Y_OFFSET] + DELIMETER2 +
                "z offset" + DELIMETER + newData[Z_OFFSET]
                ;

        this.accelContents = content;
        // Save new settings
        accelData = newData;
        // Write settings to file
        writeData(content);
    }
    
    // Method that deletes current contents of file to default values
    public void clearAccelFile(){
        changeAccelFileContents(new float[]{1f, 0f, 0f, 0f});
    }

    public void setAlpha(float newAlpha){
        accelData[ALPHA] = newAlpha;
        changeAccelFileContents(accelData);
    }

    public void setOffset(float[] newOffset){
        accelData[X_OFFSET] = newOffset[0];
        accelData[Y_OFFSET] = newOffset[1];
        accelData[Z_OFFSET] = newOffset[2];
        changeAccelFileContents(accelData);
    }

}
