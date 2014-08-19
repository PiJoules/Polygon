package com.test;

// Android imports
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

public class Settings extends Activity {
    // Buttons on the settings screen    
    private Button filter, noise, vels, clear, pramod, done;

    // This method creates the screen (Activity). It overrides the default creation method in the
    // Activity class
    @Override
    public void onCreate(Bundle icicle) {
        // Call onCreate method of Activity
        super.onCreate(icicle);
        
        //Remove title bar and notification bar
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        
        // set to landscape view
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        
        // Set GUI layout
        setContentView(R.layout.settings_layout);

        // Create filter button        
        filter = (Button) findViewById(R.id.filter);
        // Create filter button handler
        filter.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Sends user to the filter control Activity
                Intent filterControl = new Intent(getApplicationContext(), FilterControl.class);
                startActivity(filterControl);
            }
        });

        // Create filter button
        noise = (Button) findViewById(R.id.noise);
        // Create filter button handler
        noise.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Sends user to the filter control Activity
                Intent noiseControl = new Intent(getApplicationContext(), NoiseControl.class);
                startActivity(noiseControl);
            }
        });
        
        // create vels button
        vels = (Button) findViewById(R.id.velocities);
        vels.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent velocityVisualization = new Intent(getApplicationContext(), VelocityVisualization.class);
                startActivity(velocityVisualization);
            }
        });

        // Create clear button        
        clear = (Button) findViewById(R.id.clear);
        // Clear button handler
        clear.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Deletes locally saved scores
                clearScores();
            }
        });
        
        pramod = (Button) findViewById(R.id.pramod);
        pramod.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent playScreen = new Intent(getApplicationContext(), Test2.class);
                playScreen.putExtra("pramod", true);
                // Starts the game
                startActivity(playScreen);
            }
        });

        // Create done button        
        done = (Button) findViewById(R.id.done);
        // Done button handler
        done.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                // Exits this activity
                finish();
            }
        });
    }


    // this method deletes the contents of the high scoes file using the ScoreManager    
    private void clearScores(){
        final ScoreManager sm = new ScoreManager(this);
        // Create alert dialog box
        new AlertDialog.Builder(this)
                // Set title message
                .setTitle("Are you sure you want to delete your local scores?")
                // Handler for "yes" button
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener(){
                    public void onClick(DialogInterface dialog, int which){
                        // Tells the ScoreManager to delete saved scores
                        sm.clearData();
                    }
                })
                .setNegativeButton(android.R.string.no, null)
                .show();
    }
    
}
