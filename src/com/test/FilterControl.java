package com.test;

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


public class FilterControl extends Activity implements SensorEventListener {
    
    private Button alpha, sma, none, accelerometer_test, done;
    private TextView filter_type, description, filter_val_type, filter_val_description;
    private EditText filter_val;
    private LinearLayout filter_val_entry;
    private AccelerometerFileManager afm;

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle); // call the parent class' onCreate method
        
        // hide the title bar
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        
        // hide the notification bar
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        
        // lock the view to a vertical portrait orientation
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        
        setContentView(R.layout.filter_control_layout);
        
        afm = new AccelerometerFileManager(this, "");
        
        filter_val = (EditText) findViewById(R.id.filter_val);
        
        filter_type = (TextView) findViewById(R.id.filter_type);
        description = (TextView) findViewById(R.id.description);
        filter_val_type = (TextView) findViewById(R.id.filter_val_type);
        filter_val_description = (TextView) findViewById(R.id.filter_val_description);
        
        filter_val_entry = (LinearLayout) findViewById(R.id.filter_val_entry);
        
        alpha = (Button) findViewById(R.id.alpha);
        alpha.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                showAlphaInfo();
                afm.changeAccelFileContents(1, afm.getAccelData()[1], (int)afm.getAccelData()[2]);
            }
        });
        
        sma = (Button) findViewById(R.id.sma);
        sma.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                showSMAInfo();
                afm.changeAccelFileContents(2, afm.getAccelData()[1], (int)afm.getAccelData()[2]);
            }
        });
        
        none = (Button) findViewById(R.id.none);
        none.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                showNoneInfo();
                afm.changeAccelFileContents(0, afm.getAccelData()[1], (int)afm.getAccelData()[2]);
            }
        });
        
        accelerometer_test = (Button) findViewById(R.id.accelerometer_test);
        accelerometer_test.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (saveAllData()){
                    Intent plotTest = new Intent(getApplicationContext(), TimeSeriesActivity.class);
                    startActivity(plotTest);
                }
            }
        });
        
        done = (Button) findViewById(R.id.done);
        done.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                finish();
            }
        });
        
        if (afm.getFilterType() == 0) showNoneInfo();
        else if (afm.getFilterType() == 1) showAlphaInfo();
        else if (afm.getFilterType() == 2) showSMAInfo();
    }
    
    private void showAlphaInfo(){
        filter_type.setText(alpha.getText());
        description.setText(R.string.alpha_description);
        filter_val_type.setText("Alpha: ");
        filter_val.setText(afm.getAccelData()[1] + "");
        filter_val_description.setVisibility(View.VISIBLE);
        filter_val_description.setText(R.string.alpha_val_description);
        filter_val_entry.setVisibility(View.VISIBLE);
    }
    
    private void showSMAInfo(){
        filter_type.setText(sma.getText());
        description.setText(R.string.sma_description);
        filter_val_type.setText("N: ");
        filter_val.setText((int) afm.getAccelData()[2] + "");
        filter_val_description.setVisibility(View.VISIBLE);
        filter_val_description.setText(R.string.sma_val_description);
        filter_val_entry.setVisibility(View.VISIBLE);
    }
    
    private void showNoneInfo(){
        filter_type.setText(none.getText());
        description.setText(R.string.none_description);
        filter_val_description.setVisibility(View.GONE);
        filter_val_entry.setVisibility(View.GONE);
    }
    
    private void createAlertDialog(String message){
        new AlertDialog.Builder(this) // create the alert message
                .setTitle(message) // set the title of the alert message to say the user's score
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener(){
                    public void onClick(DialogInterface dialog, int which){
                        Intent plotTest = new Intent(getApplicationContext(), TimeSeriesActivity.class);
                        startActivity(plotTest);
                    }
                }).show();
    }
    
    @Override
    protected void onDestroy() {
        super.onPause();
        saveAllData();
    }
    
    private boolean saveAllData(){
        float val = Float.parseFloat(filter_val.getText().toString());
        if (afm.getFilterType() == 0){
            afm.changeAccelFileContents(0, afm.getAccelData()[1], (int) afm.getAccelData()[2]);
        }
        else if (afm.getFilterType() == 1){
            if (val < 0.01){
                val = 0.01f;
                afm.changeAccelFileContents(1, val, (int) afm.getAccelData()[2]);
                filter_val.setText(val+"");
                createAlertDialog("Alpha was set to 0.01 since alpha cannot be less than 0.01.");
                return false;
            }
            else if (val > 1){
                val = 1f;
                afm.changeAccelFileContents(1, val, (int) afm.getAccelData()[2]);
                filter_val.setText(val+"");
                createAlertDialog("Alpha was set to 1 since alpha cannot be greater than 1.");
                return false;
            }
            else {
                afm.changeAccelFileContents(1, val, (int) afm.getAccelData()[2]);
                return true;
            }
        }
        else if (afm.getFilterType() == 2){
            if (val < 1){
                val = 1;
                afm.changeAccelFileContents(2, afm.getAccelData()[1], (int)val);
                filter_val.setText(val+"");
                createAlertDialog("N was set to 1 since N cannot be less than 1.");
                return false;
            }
            else if (val%1 != 0){
                val = Math.round(val);
                afm.changeAccelFileContents(2, afm.getAccelData()[1], (int)val);
                filter_val.setText(val+"");
                createAlertDialog("N was rounded since N must be an integer.");
                return false;
            }
            else {
                afm.changeAccelFileContents(2, afm.getAccelData()[1], (int)val);
                return true;
            }
        }
        return true;
    }

    public void onSensorChanged(SensorEvent event) {
        
    }

    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        
    }
    
}
