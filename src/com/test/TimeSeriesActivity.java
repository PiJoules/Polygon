package com.test;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import com.androidplot.xy.SimpleXYSeries;
import com.androidplot.xy.XYSeries;
import com.androidplot.xy.*;
import java.util.ArrayList;


public class TimeSeriesActivity extends Activity implements SensorEventListener{

    private XYPlot plot;
    private LinearLayout screen;
    private ArrayList<Number> series1Numbers;
    private Accelerometer accel;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        
        // hide the title bar
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        
        // hide the notification bar
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        
        // lock the view to a vertical portrait orientation
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
        
        setContentView(R.layout.time_series_example);
        
        accel = new Accelerometer(this,this);
        accel.setShouldFilter(false);
        
        series1Numbers = new ArrayList<Number>();
        
        screen = (LinearLayout) findViewById(R.id.screen);
        screen.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                
            }
        });
        
        plot = (XYPlot) findViewById(R.id.plot1);
        
    }
    
    private void graph(){
        plot.clear();

        series1Numbers.add(accel.getAccel()[0]);
        series1Numbers.remove(0);

        // Turn the above arrays into XYSeries':
        XYSeries series1 = new SimpleXYSeries(
                series1Numbers,          // SimpleXYSeries takes a List so turn our array into a List
                SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, // Y_VALS_ONLY means use the element index as the x value
                "Series1");                             // Set the display title of the series

        // Create a formatter to use for drawing a series using LineAndPointRenderer
        // and configure it from xml:
        LineAndPointFormatter series1Format = new LineAndPointFormatter();

        // add a new series' to the xyplot:
        plot.addSeries(series1, series1Format);

        // reduce the number of range labels
        plot.setTicksPerRangeLabel(3);
        //plot.getGraphWidget().setDomainLabelOrientation(-45);

        plot.redraw();
    }

    public void onSensorChanged(SensorEvent event) {
        accel.updateAccelVals(event);
        if (series1Numbers.size() < 5){
            series1Numbers.add(accel.getAccelUnfiltered()[0]);
        }
        else{
            graph();
        }
    }

    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        
    }
}