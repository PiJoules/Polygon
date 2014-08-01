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
import android.widget.Button;
import android.widget.LinearLayout;
import com.androidplot.ui.SizeLayoutType;
import com.androidplot.ui.SizeMetrics;
import com.androidplot.xy.SimpleXYSeries;
import com.androidplot.xy.XYSeries;
import com.androidplot.xy.*;
import java.util.ArrayList;


public class TimeSeriesActivity extends Activity implements SensorEventListener{

    private XYPlot plot;
    private LinearLayout screen;
    private Button done;
    private ArrayList<Number> series1Numbers;
    private Accelerometer accel;
    private ArrayList<Number> xRange = new ArrayList<Number>();

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        
        // hide the title bar
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        
        // hide the notification bar
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        
        // lock the view to a vertical portrait orientation
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE);
        
        setContentView(R.layout.time_series_example);
        
        accel = new Accelerometer(this,this);
        accel.setShouldFilter(false);
        
        series1Numbers = new ArrayList<Number>();
        
        done = (Button) findViewById(R.id.done);
        done.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                finish();
            }
        });
        
        screen = (LinearLayout) findViewById(R.id.screen);
        screen.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                
            }
        });
        
        plot = (XYPlot) findViewById(R.id.plot1);
        plot.getGraphWidget().setSize(new SizeMetrics(1f, SizeLayoutType.RELATIVE, 1f, SizeLayoutType.RELATIVE));
        plot.setRangeUpperBoundary(10, BoundaryMode.FIXED);
        plot.setRangeLowerBoundary(-10, BoundaryMode.FIXED);
        
    }
    
    private void graph(){
        plot.clear();

        series1Numbers.add(accel.getAccel()[0]);
        series1Numbers.remove(0);
        
        XYSeries accelData = new SimpleXYSeries(xRange, series1Numbers, "XAccel");

        // Create a formatter to use for drawing a series using LineAndPointRenderer
        // and configure it from xml:
        LineAndPointFormatter series1Format = new LineAndPointFormatter();

        // add a new series' to the xyplot:
        plot.addSeries(accelData, series1Format);

        plot.redraw();
    }

    public void onSensorChanged(SensorEvent event) {
        accel.updateAccelVals(event);
        if (series1Numbers.size() < 10){
            series1Numbers.add(accel.getAccelUnfiltered()[0]);
            xRange.add(series1Numbers.size());
        }
        else{
            graph();
        }
    }

    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        
    }
}