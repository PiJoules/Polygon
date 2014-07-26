package com.test;

import android.app.Activity;
import android.content.Context;
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


public class MainMenu extends Activity {

    Button play;
    TableLayout table;
    
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
        table = (TableLayout) findViewById(R.id.table);
        
        System.out.println("Loaded Main Menu");
    }
    
    public String readSavedData(String file){
        // note: stringbuffer is synchronized, stringbuilder is not, but both essentially do same
        StringBuffer datax = new StringBuffer("");
        try {
            FileInputStream in = openFileInput(file);
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
    
    public void writeData(String data, String file){
        try {
            FileOutputStream fOut = openFileOutput(file,Context.MODE_PRIVATE);
            fOut.write(data.getBytes());
            fOut.close();
        }
        catch (IOException e){}
    }
    
    public void checkFile(String file){
        boolean exists = readSavedData(file).equals("");
        if (!exists) writeData("", file);
    }
    
    
    @Override
    protected void onResume(){
        super.onResume();
        
    }
    
}
