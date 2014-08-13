package com.test;

// Android imports
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

// Object that allows the user to change filtering settings
public class NoiseControl extends Activity implements SensorEventListener {
    // Buttons
    private Button done, noise_test;
    // EditTexts
    private EditText rangeMin, rangeMax, bufferInput;
    // File manager for the GUI settings
    private AccelerometerFileManager afm;

    // Method that creates the screen (Activity). Overrides the onCreate method of Activity
    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle); // call the parent class' onCreate method
        
        // hide the title bar
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        
        // hide the notification bar
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        
        // lock the view to a vertical portrait orientation
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        
        // Set the GUI layout
        setContentView(R.layout.noise_control_layout);
        
        // File manager for the filtering settings file
        afm = new AccelerometerFileManager(this, "");

        // Initialize modifiable text boxes
        rangeMin = (EditText) findViewById(R.id.range_min_val);
        rangeMax = (EditText) findViewById(R.id.range_max_val);
        bufferInput = (EditText) findViewById(R.id.buffer_size_val);

        // Done button that allows the user to exit the Activity
        done = (Button) findViewById(R.id.done);
        // Done button event handler
        done.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                // Exit the Activity
                finish();
            }
        });

        noise_test = (Button) findViewById(R.id.noise_test);
        // Handler for the noise plot button
        noise_test.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Try to save new settings
                if (saveAllData()){
                    Intent noiseTest = new Intent(getApplicationContext(), NoiseVisualization.class);
                    // Send user to noise visualiztion screen
                    startActivity(noiseTest);
                }
            }
        });
        
    }
 
    // Method that is called when the user exits the screen. Saves new settings
    @Override
    protected void onDestroy() {
        super.onPause();
        saveAllData();
    }
    
    // Saves new settings
    private boolean saveAllData(){
        return true; // temporary
    }

    // Event handler for new accelerometer readings. The actual readings are handled elsewhere
    public void onSensorChanged(SensorEvent event) {
        
    }

    // Event handler for a change in accuracy settings of the accelerometer. No action is required
    // here for this app.
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        
    }
    
}
