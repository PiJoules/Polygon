/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.test;

import android.content.Context;

/**
 *
 * @author Pi_Joules
 */
public class AccelerometerFileManager extends FileManager {
    
    private final String ACCELFILE = "accel.txt";
    private String accelContents = "";
    private float[] accelData = new float[4]; // 0 -> filterType, 1 -> alpha val, 2 -> sma count

    public AccelerometerFileManager(Context context, String fileName) {
        super(context, fileName);
        this.setFile(ACCELFILE);
        
        if (readSavedData()){
            accelContents = this.getContents();
        }
        else {
            clearAccelFile();
        }
        parseAccelFileContents();
    }
    
    private void parseAccelFileContents(){
        String[] rows = accelContents.split(DELIMETER2);
        for (int i = 0; i < rows.length; i++){
            accelData[i] = Float.parseFloat(rows[i].split(DELIMETER)[1]);
        }
    }
    
    public float[] getAccelData(){
        return this.accelData;
    }
    
    // 0 = no filter, 1 = alpha, 2 = sma
    public void changeAccelFileContents(int filterType, float alpha, int sma, float threshold){
        String content =
                "filterType" + DELIMETER + filterType + DELIMETER2 +
                "alpha" + DELIMETER + alpha + DELIMETER2 +
                "sma" + DELIMETER + sma + DELIMETER2 +
                "threshold" + DELIMETER + threshold
                ;
        this.accelContents = content;
        accelData[0] = (float) filterType;
        accelData[1] = alpha;
        accelData[2] = (float) sma;
        accelData[3] = threshold;
        writeData(content);
    }
    
    public void clearAccelFile(){
        changeAccelFileContents(0, 1f, 1, 5f);
    }
    
    public int getFilterType(){
        return (int) accelData[0];
    }
    
}
