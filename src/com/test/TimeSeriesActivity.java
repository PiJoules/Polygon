package com.test;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import com.androidplot.ui.AnchorPosition;
import com.androidplot.ui.SizeLayoutType;
import com.androidplot.ui.SizeMetrics;
import com.androidplot.ui.XLayoutStyle;
import com.androidplot.ui.YLayoutStyle;
import com.androidplot.xy.BoundaryMode;
import com.androidplot.xy.LineAndPointFormatter;
import com.androidplot.xy.SimpleXYSeries;
import com.androidplot.xy.XYPlot;
import com.androidplot.xy.XYStepMode;
import java.util.ArrayList;


public class TimeSeriesActivity extends Activity implements SensorEventListener{

    private XYPlot plot;
    private Button done, start_pause, reset, show_hide;
    private TextView count;
    private ArrayList<Number> XAccel, YAccel, ZAccel, threshold;
    private SimpleXYSeries XAccelSeries, YAccelSeries, ZAccelSeries, thresholdSeries;
    private final int ARRAYSIZE = 100;
    private int sensorCount = 1;
    private Accelerometer accel;
    private boolean paused = false;
    private final ArrayList<Number> xRange = new ArrayList<Number>();
    private LineAndPointFormatter XAccelFormat, YAccelFormat, ZAccelFormat, thresholdFormat, hide;
    private AccelerometerFileManager afm;
    private boolean showThreshold = true;
    private int[] counts = new int[3];
    private boolean[] highFlags = {false, false, false};

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
        
        afm = new AccelerometerFileManager(this, "");
        
        XAccel = new ArrayList<Number>();
        YAccel = new ArrayList<Number>();
        ZAccel = new ArrayList<Number>();
        threshold = new ArrayList<Number>();
        
        for (int i = 0; i < ARRAYSIZE; i++){
            XAccel.add(0);
            YAccel.add(0);
            ZAccel.add(0);
            threshold.add(afm.getAccelData()[3]);
            xRange.add(sensorCount++);
        }
        
        done = (Button) findViewById(R.id.done);
        done.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                finish();
            }
        });
        
        start_pause = (Button) findViewById(R.id.start_pause);
        start_pause.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (paused){
                    paused = false;
                    start_pause.setText("Pause");
                }
                else {
                    paused = true;
                    start_pause.setText("Resume");
                }
            }
        });
        
        reset = (Button) findViewById(R.id.reset);
        reset.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                counts[0] = 0;
                counts[1] = 0;
                counts[2] = 0;
            }
        });
        
        show_hide = (Button) findViewById(R.id.show_hide);
        show_hide.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (showThreshold){
                    show_hide.setText("Show");
                    showThreshold = false;
                }
                else {
                    show_hide.setText("Hide");
                    showThreshold = true;
                }
            }
        });
        
        count = (TextView) findViewById(R.id.count);
        
        plot = (XYPlot) findViewById(R.id.plot1);
        plot.getGraphWidget().setSize(new SizeMetrics(0.9f, SizeLayoutType.RELATIVE, 1f, SizeLayoutType.RELATIVE));
        plot.getGraphWidget().setAnchor(AnchorPosition.RIGHT_MIDDLE);
        plot.setRangeUpperBoundary(11, BoundaryMode.FIXED);
        plot.setRangeLowerBoundary(-11, BoundaryMode.FIXED);
        plot.setRangeStep(XYStepMode.INCREMENT_BY_VAL, 1);
        plot.getLegendWidget().setSize(new SizeMetrics(30, SizeLayoutType.ABSOLUTE, 300, SizeLayoutType.ABSOLUTE));
        plot.getLegendWidget().position(50, XLayoutStyle.ABSOLUTE_FROM_RIGHT, 50, YLayoutStyle.ABSOLUTE_FROM_BOTTOM);
        plot.getLegendWidget().setAnchor(AnchorPosition.RIGHT_BOTTOM);
        plot.setRangeLabel("Acceleration (m/s^2)");
        plot.setDomainLabel("Data Point Number");
        
        if (afm.getFilterType() == 0){
            plot.setTitle("Raw Accelerometer Data");
            accel.setShouldFilter(false);
        }
        else if (afm.getFilterType() == 1){
            plot.setTitle("Alpha Filter Ouput with Alpha Value (" + afm.getAccelData()[1] + ")");
            accel.setShouldFilter(true);
            accel.setFilter(true);
            accel.setAlpha(afm.getAccelData()[1]);
        }
        else if (afm.getFilterType() == 2){
            plot.setTitle("SMA Filter Ouput with Period Count of (" + afm.getAccelData()[2] + ")");
            accel.setShouldFilter(true);
            accel.setFilter(false);
            accel.setPeriods((int) afm.getAccelData()[2]);
        }
        
        // create XYSeries from xRange and 
        XAccelSeries = new SimpleXYSeries(xRange, XAccel, "XAccel");
        YAccelSeries = new SimpleXYSeries(xRange, YAccel, "YAccel");
        ZAccelSeries = new SimpleXYSeries(xRange, ZAccel, "ZAccel");
        thresholdSeries = new SimpleXYSeries(xRange, threshold, "Threshold");
        
        XAccelFormat = new LineAndPointFormatter(
                Color.RED,
                Color.RED,
                Color.TRANSPARENT,
                null
        );
        YAccelFormat = new LineAndPointFormatter(
                Color.GREEN,
                Color.GREEN,
                Color.TRANSPARENT,
                null
        );
        ZAccelFormat = new LineAndPointFormatter(
                Color.BLUE,
                Color.BLUE,
                Color.TRANSPARENT,
                null
        );
        thresholdFormat = new LineAndPointFormatter(
                Color.CYAN,
                Color.CYAN,
                Color.TRANSPARENT,
                null
        );
        hide = new LineAndPointFormatter(
                Color.TRANSPARENT,
                Color.TRANSPARENT,
                Color.TRANSPARENT,
                null
        );
        
    }
    
    private void graph(){
        plot.clear(); // clear the plot

        // add a new series' to the xyplot:
        plot.addSeries(XAccelSeries, XAccelFormat);
        plot.addSeries(YAccelSeries, YAccelFormat);
        plot.addSeries(ZAccelSeries, ZAccelFormat);
        if (showThreshold) plot.addSeries(thresholdSeries, thresholdFormat);
        else plot.addSeries(thresholdSeries, hide);

        plot.redraw();
    }

    public void onSensorChanged(SensorEvent event) {
        accel.updateAccelVals(event); // update the accelerometer vals
        if (!paused){
            XAccelSeries.addLast(sensorCount, accel.getAccel()[0]);
            YAccelSeries.addLast(sensorCount, accel.getAccel()[1]);
            ZAccelSeries.addLast(sensorCount, accel.getAccel()[2]);
            thresholdSeries.addLast(sensorCount, afm.getAccelData()[3]);
            XAccelSeries.removeFirst();
            YAccelSeries.removeFirst();
            ZAccelSeries.removeFirst();
            thresholdSeries.removeFirst();
            
            if (showThreshold){
                if (accel.getAccel()[0] > afm.getAccelData()[3] && !highFlags[0]){
                    counts[0]++;
                    highFlags[0] = true;
                }
                else if (accel.getAccel()[0] <= afm.getAccelData()[3] && highFlags[0]){
                    highFlags[0] = false;
                }
                if (accel.getAccel()[1] > afm.getAccelData()[3] && !highFlags[1]){
                    counts[1]++;
                    highFlags[1] = true;
                }
                else if (accel.getAccel()[1] <= afm.getAccelData()[3] && highFlags[1]){
                    highFlags[1] = false;
                }
                if (accel.getAccel()[2] > afm.getAccelData()[3] && !highFlags[2]){
                    counts[2]++;
                    highFlags[2] = true;
                }
                else if (accel.getAccel()[2] <= afm.getAccelData()[3] && highFlags[2]){
                    highFlags[2] = false;
                }
            }
            
            count.setText("x:" + counts[0] + " y:" + counts[1] + " z:" + counts[2]);
            
            sensorCount++;
            graph();
        }
    }

    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        
    }
}