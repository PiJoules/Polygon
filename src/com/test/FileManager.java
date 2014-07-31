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
public class FileManager {
    
    private String fileName;
    private final Context context;
    String contents = "";
    
    public FileManager(Context context, String fileName){
        this.fileName = fileName;
        this.context = context;
    }
    
    public boolean readSavedData(){
        // note: stringbuffer is synchronized, stringbuilder is not, but both essentially do same
        StringBuffer datax = new StringBuffer("");
        try {
            FileInputStream in = this.context.openFileInput(this.fileName);
            InputStreamReader isr = new InputStreamReader(in);
            BufferedReader br = new BufferedReader(isr);
            
            String readString = br.readLine();
            while (readString != null){
                datax.append(readString);
                readString = br.readLine();
            }
            
            isr.close();
        }
        // may not be able to read file because it contains nothing or does not exist
        catch (IOException e){
            return false;
        }
        this.contents = datax.toString();
        return true;
    }
    
    public String getContents(){
        return this.contents;
    }
    
    public void setFile(String fileName){
        this.fileName = fileName;
    }
    
    public boolean writeData(String contents){
        try{
            FileOutputStream fOut = this.context.openFileOutput(this.fileName, MODE_WORLD_READABLE);
            OutputStreamWriter osw = new OutputStreamWriter(fOut); 

            // Write the string to the file
            osw.write(contents);

            /* ensure that everything is
             * really written out and close */
            osw.flush();
            osw.close();
        }
        catch (IOException e){
            return false;
        }
        this.contents = contents;
        return true;
    }
    
}
