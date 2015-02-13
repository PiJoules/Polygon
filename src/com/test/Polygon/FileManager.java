package com.test.Polygon;

// Android imports
import android.content.Context;
import static android.content.Context.MODE_WORLD_READABLE;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

// An object that handles reading and writing to locally saved 
public class FileManager {  
    // Name of file to save to  
    private String fileName;
    private final Context context;
    // String for saving the contents of the file
    private String contents = "";
    public final String DELIMETER = "#", DELIMETER2 = "##";
    
    // default constructor
    public FileManager(Context context){
        this.context = context;
    }
    
    // Constructor
    public FileManager(Context context, String fileName){
        // Name of file to save to
        this.fileName = fileName;
        this.context = context;
    }
    
    // Method to read contents of file
    public boolean readSavedData(){
        // note: stringbuffer is synchronized, stringbuilder is not, but both essentially do same
        StringBuffer datax = new StringBuffer("");
        try {
            // Input stream that buffers the content of the file
            FileInputStream in = this.context.openFileInput(this.fileName);
            // Reader that intreprets contents saved to memory on the phone
            InputStreamReader isr = new InputStreamReader(in);
            // Buffer that provides the content of the file
            BufferedReader br = new BufferedReader(isr);
            
            // Read first line
            String readString = br.readLine();
            // Continue to read contents until end of file
            while (readString != null){
                // Add line to saved contents string
                datax.append(readString);
                // Read next line
                readString = br.readLine();
            }
            
            // Close the file being read
            isr.close();
        }
        // may not be able to read file because it contains nothing or does not exist
        catch (IOException e){
            return false;
        }
        this.contents = datax.toString();
        return true;
    }
    
    // Get content of file
    public String getContents(){
        return this.contents;
    }
    
    // Set name of file to save to
    public void setFile(String fileName){
        this.fileName = fileName;
    }
    
    // Write desired content to file
    public boolean writeData(String contents){
        try{
            // Output stream that buffers content to be written to file
            FileOutputStream fOut = this.context.openFileOutput(this.fileName, MODE_WORLD_READABLE);
            // Writer that saves the file to the phone
            OutputStreamWriter osw = new OutputStreamWriter(fOut); 

            // Write the string to the file
            osw.write(contents);

            /* ensure that everything is
             * really written out and close */
            osw.flush();
            osw.close();
        }
        // Catch for errors with writing
        catch (IOException e){
            return false;
        }
        // Save contents of file
        this.contents = contents;
        return true;
    }
    
}
