package com.test;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

/**
 *
 * @author Pi_Joules
 */
public class Settings extends Activity {
    
    private Button filter, clear, done;

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        
        //Remove title bar and notification bar
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        
        // set to landscape view
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        
        setContentView(R.layout.settings_layout);
        
        filter = (Button) findViewById(R.id.filter);
        filter.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent filterControl = new Intent(getApplicationContext(), FilterControl.class);
                startActivity(filterControl);
            }
        });
        
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
    }
    
    private void clearScores(){
        final ScoreManager sm = new ScoreManager(this,null);
        new AlertDialog.Builder(this)
                .setTitle("Are you sure you want to delete your local scores?")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener(){
                    public void onClick(DialogInterface dialog, int which){
                        sm.clearData();
                    }
                })
                .setNegativeButton(android.R.string.no, null)
                .show();
    }
    
}
