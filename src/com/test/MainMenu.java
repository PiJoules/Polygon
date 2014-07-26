package com.test;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TableLayout;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.Arrays;

/**
 *
 * @author Pi_Joules
 */
public class MainMenu extends Activity {

    // xml stuff
    private Button play, settings;
    private TableLayout table;
    
    private final String SCORESFILE = "scores.txt";
    
    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        
        //Remove title bar and notification bar
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        
        // set to landscape view
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        
        setContentView(R.layout.main_menu_layout);
        
        play = (Button) findViewById(R.id.play);
        play.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                Intent playScreen = new Intent(getApplicationContext(), Test2.class);
                startActivity(playScreen);
            }
        });
        
        settings = (Button) findViewById(R.id.settings);
        settings.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent settingsScreen = new Intent(getApplicationContext(), Settings.class);
                startActivity(settingsScreen);
            }
        });
        
        table = (TableLayout) findViewById(R.id.table);
        
        System.out.println("Loaded Main Menu");
    }
    
    private String readSavedData(String fileName){
        // note: stringbuffer is synchronized, stringbuilder is not, but both essentially do same
        StringBuffer datax = new StringBuffer("");
        try {
            FileInputStream in = openFileInput(fileName);
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
    
    private void writeData(String contents, String fileName){
        try{
            FileOutputStream fOut = openFileOutput(fileName, MODE_WORLD_READABLE);
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
    
    
    @Override
    protected void onResume(){
        super.onResume();
        System.out.println("Contents of saved data: " + readSavedData(SCORESFILE));
    }
    
}
