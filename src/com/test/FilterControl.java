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
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Button;

// Object that allows the user to change filtering settings
public class FilterControl extends Activity implements SensorEventListener {
    // Buttons
    private Button done;
    // Text fields
    private TextView description, filter_val_type, filter_val_description;
    // Text fields that allow user to change content to desired filtering settings
    private EditText filter_val;
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
        
        // Intialize text fields
        description = (TextView) findViewById(R.id.description);
        filter_val_description = (TextView) findViewById(R.id.filter_val_description);
        
        filter_val_entry = (LinearLayout) findViewById(R.id.filter_val_entry);

        // Done button that allows the user to exit the Activity
        done = (Button) findViewById(R.id.done);
        // Done button event handler
        done.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                // Exit the Activity
                finish();
            }
        });
        
    }

    // Display information about alpha filter    
    private void showAlphaInfo(){
        description.setText(R.string.alpha_description);
        filter_val.setText(afm.getAccelData()[AccelerometerFileManager.ALPHA] + "");
        filter_val_description.setVisibility(View.VISIBLE);
        filter_val_description.setText(R.string.alpha_val_description);
        filter_val_entry.setVisibility(View.VISIBLE);
    }

     // Method for creating a dialog box that contains the accelerometer plots
    private void createAlertDialog(String message){
        new AlertDialog.Builder(this)
            .setTitle(message)
            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener(){
                public void onClick(DialogInterface dialog, int which){
                    finish();
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

        // Get values of filtering settings
        float val = Float.parseFloat(filter_val.getText().toString());
        
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

        afm.setAlpha(val);
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
