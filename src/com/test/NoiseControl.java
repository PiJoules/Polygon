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
                // Save new settings
                saveAllData();
                Intent noiseTest = new Intent(getApplicationContext(), NoiseVisualization.class);
                // Send user to noise visualiztion screen
                startActivity(noiseTest);
                
            }
        });
        
    }
 
    // Method that is called when the user exits the screen. Saves new settings
    @Override
    protected void onDestroy() {
        super.onPause();
        saveAllData();
    }
    
    // Method for creating a dialog box that contains the accelerometer plots
    private void createAlertDialog(String message){
        new AlertDialog.Builder(this)
                .setTitle(message)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener(){
                    public void onClick(DialogInterface dialog, int which){
                    }
                }).show();
    }

    // Saves new settings
    private void saveAllData(){
        float sizeVal = Float.parseFloat(bufferInput.getText().toString());
        float minVal = Float.parseFloat(rangeMin.getText().toString());
        float maxVal = Float.parseFloat(rangeMax.getText().toString());
        // Make sure buffer size is a positive integer
        if (sizeVal < 1){
            sizeVal = 1;
            bufferInput.setText(sizeVal+"");
            createAlertDialog("Buffer size was set to 1 since it cannot be less than 1.");
        }
        else if (sizeVal%1 != 0){
            sizeVal = Math.round(sizeVal);
            bufferInput.setText(sizeVal+"");
            createAlertDialog("Buffer size was rounded since it must be an integer.");
        }
        // Make sure min and max are > 0 and max > min
        if(minVal < 0){
            minVal = 0;
            rangeMin.setText("0.0");
            createAlertDialog("Minimum may not be negative. Min reset to 0");
        }
        if(maxVal < 0){
            maxVal = 0;
            rangeMax.setText("0.0");
            createAlertDialog("Maximum may not be negative. Max reset to 0");
        }
        if(minVal > maxVal){
            maxVal = minVal + .01f;
            rangeMax.setText(maxVal + "");
            createAlertDialog("Maximum must be greater than minimum");
        }
        // Save new settings
        afm.setBuffer((int) sizeVal);
        afm.setMax(maxVal);
        afm.setMin(minVal);
    }

    // Event handler for new accelerometer readings. The actual readings are handled elsewhere
    public void onSensorChanged(SensorEvent event) {
        
    }

    // Event handler for a change in accuracy settings of the accelerometer. No action is required
    // here for this app.
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        
    }
    
}
