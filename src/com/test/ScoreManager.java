/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.test;

import android.content.Context;
import static android.content.Context.MODE_WORLD_READABLE;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

/**
 *
 * @author Pi_Joules
 */
public class ScoreManager {
    
    private final String SCORESFILE = "scores.txt";
    private Context context;
    
    public ScoreManager(Context context){
        this.context = context;
    }
    
    public String readSavedData(){
        // note: stringbuffer is synchronized, stringbuilder is not, but both essentially do same
        StringBuffer datax = new StringBuffer("");
        try {
            FileInputStream in = this.context.openFileInput(SCORESFILE);
            InputStreamReader isr = new InputStreamReader(in);
            BufferedReader br = new BufferedReader(isr);
            
            String readString = br.readLine();
            while (readString != null){
                datax.append(readString);
                readString = br.readLine();
            }
            
            isr.close();
        }
        catch (IOException e){
            return "";
        }
        return datax.toString();
    }
    
    public void writeData(String contents){
        try{
            FileOutputStream fOut = this.context.openFileOutput(SCORESFILE, MODE_WORLD_READABLE);
            OutputStreamWriter osw = new OutputStreamWriter(fOut); 

            // Write the string to the file
            osw.write(contents);

            /* ensure that everything is
             * really written out and close */
            osw.flush();
            osw.close();
        }
        catch (IOException e){}
    }
}
