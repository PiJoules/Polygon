package com.test;

import android.app.Activity;
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
    private Accelerometer accel;

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
                accel.saveAlpha();
            }
        });
        
        sma = (Button) findViewById(R.id.sma);
        sma.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                showSMAInfo();
                accel.saveSMA();
            }
        });
        
        none = (Button) findViewById(R.id.none);
        none.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                showNoneInfo();
                accel.saveNone();
            }
        });
        
        accelerometer_test = (Button) findViewById(R.id.accelerometer_test);
        accelerometer_test.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent plotTest = new Intent(getApplicationContext(), TimeSeriesActivity.class);
                startActivity(plotTest);
            }
        });
        
        done = (Button) findViewById(R.id.done);
        done.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                finish();
            }
        });
        
        accel = new Accelerometer(this, this);
        accel.setAccelFromFile();
        if (accel.getShouldFilter()){
            if (accel.getFilter()){
                showAlphaInfo();
            }
            else showSMAInfo();
        }
        else showNoneInfo();
    }
    
    private void showAlphaInfo(){
        filter_type.setText(alpha.getText());
        description.setText(R.string.alpha_description);
        filter_val_type.setText("Alpha: ");
        filter_val.setText(accel.getAlpha() + "");
        filter_val_description.setVisibility(View.VISIBLE);
        filter_val_description.setText(R.string.alpha_val_description);
        filter_val_entry.setVisibility(View.VISIBLE);
    }
    
    private void showSMAInfo(){
        filter_type.setText(sma.getText());
        description.setText(R.string.sma_description);
        filter_val_type.setText("N: ");
        filter_val.setText(accel.getPeriods() + "");
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

    public void onSensorChanged(SensorEvent event) {
        
    }

    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        
    }
    
}
