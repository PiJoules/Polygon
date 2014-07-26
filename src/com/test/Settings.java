package com.test;

import android.app.Activity;
import android.app.AlertDialog;
import static android.content.Context.MODE_WORLD_READABLE;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

/**
 *
 * @author Pi_Joules
 */
public class Settings extends Activity {
    
    private Button clear, done;
    
    private final String SCORESFILE = "scores.txt";

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        
        //Remove title bar and notification bar
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        
        // set to landscape view
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        
        setContentView(R.layout.settings_layout);
        
        clear = (Button) findViewById(R.id.clear);
        clear.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                clearScores();
            }
        });
        
        done = (Button) findViewById(R.id.done);
        done.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                finish();
            }
        });
        
        System.out.println("Loaded Settings");
    }
    
    private void clearScores(){
        new AlertDialog.Builder(this)
                .setTitle("Are you sure you want to delete your local scores?")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener(){
                    public void onClick(DialogInterface dialog, int which){
                        writeData("", SCORESFILE);
                        System.out.println("Scores have been cleared.");
                    }
                })
                .setNegativeButton(android.R.string.no, null)
                .show();
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
    
}
