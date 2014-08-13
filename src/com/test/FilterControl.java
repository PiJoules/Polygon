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
public class FilterControl extends Activity implements SensorEventListener {
    // Buttons
    private Button alpha, sma, none, accelerometer_test, done;
    // Text fields
    private TextView filter_type, description, filter_val_type, filter_val_description;
    // Text fields that allow user to change content to desired filtering settings
    private EditText filter_val, threshold_val;
    // The GUI layout
    private LinearLayout filter_val_entry;
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
        setContentView(R.layout.filter_control_layout);
        
        // File manager for the filtering settings file
        afm = new AccelerometerFileManager(this, "");
        
        // Initialize modifiable text boxes
        filter_val = (EditText) findViewById(R.id.filter_val);
        threshold_val = (EditText) findViewById(R.id.threshold_val);
        threshold_val.setText(afm.getAccelData()[3] + "");
        
        // Intialize text fields
        filter_type = (TextView) findViewById(R.id.filter_type);
        description = (TextView) findViewById(R.id.description);
        filter_val_type = (TextView) findViewById(R.id.filter_val_type);
        filter_val_description = (TextView) findViewById(R.id.filter_val_description);
        
        filter_val_entry = (LinearLayout) findViewById(R.id.filter_val_entry);
        
        // EMA button that allows the user to selct the EMA filter
        alpha = (Button) findViewById(R.id.alpha);
        // EMA button event handler
        alpha.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Display information about EMA
                showAlphaInfo();
                // Edit the accleration file settings
                afm.setFilter(AccelerometerFileManager.EMA);
            }
        });
        
        // SMA button that allows the user to select the SMA filter
        sma = (Button) findViewById(R.id.sma);
        // SMA button event handler
        sma.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Display information about SMA
                showSMAInfo();
                // Edit the acceleration file settings
                afm.setFilter(AccelerometerFileManager.SMA);
            }
        });
        
        // None button that allows the user to select no filter
        none = (Button) findViewById(R.id.none);
        // None button event handler
        none.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Display information about no filtering
                showNoneInfo();
                // Edit the accleration file settings
                afm.setFilter(AccelerometerFileManager.NONE);
            }
        });
        
        // Button to allow the user to test their new settings
        accelerometer_test = (Button) findViewById(R.id.accelerometer_test);
        // Handler for the test button
        accelerometer_test.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Try to save new settings
                if (saveAllData()){
                    Intent plotTest = new Intent(getApplicationContext(), TimeSeriesActivity.class);
                    // Send user to graphing screen
                    startActivity(plotTest);
                }
            }
        });

        // Done button that allows the user to exit the Activity
        done = (Button) findViewById(R.id.done);
        // Done button event handler
        done.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                // Exit the Activity
                finish();
            }
        });
        

        // Set the content of the filter information text field according to the selected filter
        if (afm.getFilterType() == 0){
            showNoneInfo();
        }
        else if (afm.getFilterType() == 1){
            showAlphaInfo();
        }
        else if (afm.getFilterType() == 2){
            showSMAInfo();
        }
    }

    // Display information about alpha filter    
    private void showAlphaInfo(){
        filter_type.setText(alpha.getText());
        description.setText(R.string.alpha_description);
        filter_val_type.setText("Alpha: ");
        filter_val.setText(afm.getAccelData()[1] + "");
        filter_val_description.setVisibility(View.VISIBLE);
        filter_val_description.setText(R.string.alpha_val_description);
        filter_val_entry.setVisibility(View.VISIBLE);
    }
    
    // Display information about SMA filter
    private void showSMAInfo(){
        filter_type.setText(sma.getText());
        description.setText(R.string.sma_description);
        filter_val_type.setText("N: ");
        filter_val.setText((int) afm.getAccelData()[2] + "");
        filter_val_description.setVisibility(View.VISIBLE);
        filter_val_description.setText(R.string.sma_val_description);
        filter_val_entry.setVisibility(View.VISIBLE);
    }
    
    // Display information about no filter
    private void showNoneInfo(){
        filter_type.setText(none.getText());
        description.setText(R.string.none_description);
        filter_val_description.setVisibility(View.GONE);
        filter_val_entry.setVisibility(View.GONE);
    }
    
    // Method for creating a dialog box that contains the accelerometer plots
    private void createAlertDialog(String message){
        new AlertDialog.Builder(this)
                .setTitle(message)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener(){
                    public void onClick(DialogInterface dialog, int which){
                        Intent plotTest = new Intent(getApplicationContext(), TimeSeriesActivity.class);
                        startActivity(plotTest);
                    }
                }).show();
    }
    
    // Method that is called when the user exits the screen. Saves new settings
    @Override
    protected void onDestroy() {
        super.onPause();
        saveAllData();
    }
    
    // Saves new settings
    private boolean saveAllData(){
        // CHecks if text box is empty and sets value to defualt value
        if ("".equals(filter_val.getText().toString())) filter_val.setText("1");
        // CHecks if text box is empty and sets value to defualt value
        if ("".equals(threshold_val.getText().toString())) threshold_val.setText("5.0");

        // Get values of filterring settings
        float val = Float.parseFloat(filter_val.getText().toString());
        float tval = Float.parseFloat(threshold_val.getText().toString());

        // Check the filtering type and change settings file accordingly
        afm.setThreshold(tval);
        if (afm.getFilterType() == AccelerometerFileManager.EMA){
            if (val < 0.01){
                val = 0.01f;
                afm.setAlpha(val);
                filter_val.setText(val+"");
                createAlertDialog("Alpha was set to 0.01 since alpha cannot be less than 0.01.");
                return false;
            }
            else if (val > 1){
                val = 1f;
                afm.setAlpha(val);
                filter_val.setText(val+"");
                createAlertDialog("Alpha was set to 1 since alpha cannot be greater than 1.");
                return false;
            }
            else {
                afm.setAlpha(val);
                return true;
            }
        }
        else if (afm.getFilterType() == AccelerometerFileManager.SMA){
            if (val < 1){
                val = 1;
                afm.setPeriods((int)val);
                filter_val.setText(val+"");
                createAlertDialog("N was set to 1 since N cannot be less than 1.");
                return false;
            }
            else if (val%1 != 0){
                val = Math.round(val);
                afm.setPeriods((int)val);
                filter_val.setText(val+"");
                createAlertDialog("N was rounded since N must be an integer.");
                return false;
            }
            else {
                afm.setPeriods((int) val);
                return true;
            }
        }
        return true;
    }

    // Event handler for new accelerometer readings. The actual readings are handled elsewhere
    public void onSensorChanged(SensorEvent event) {
        
    }

    // Event handler for a change in accuracy settings of the accelerometer. No action is required
    // here for this app.
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        
    }
    
}
