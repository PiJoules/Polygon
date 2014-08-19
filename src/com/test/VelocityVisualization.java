package com.test;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
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


public class VelocityVisualization extends Activity implements SensorEventListener{
    
    // The plot on which the graphs will be placed
    private XYPlot plot;
    // Buttons
    private Button pause, done, zero;
    // Text boxes displaying the velocity values
    private TextView vx, vy;

    // The accelerometer object
    private Accelerometer accel;
    // accelerometer file manager for handling the text file that stores alpha and sma values
    private AccelerometerFileManager afm;

    // number of data points per series that will be displayed on the plot
    private final int ARRAYSIZE = 100;
    // the number of data points retrieved form accelerometer
    private int sensorCount = 1;
    // The initial arraylists holding the values for past velocities
    private ArrayList<Number> xVel, yVel;
    // whether the plot is paused or not
    private boolean paused = false;
    // range of x vals to plot against
    private final ArrayList<Number> xRange = new ArrayList<Number>();
    
    
    // the series that will be plotted on the plot
    private SimpleXYSeries xVelSeries, yVelSeries;
    // formatters for each series that decorate the lines
    // hide is the transparent decoration for hiding the threshold if necessary
    private LineAndPointFormatter xVelFormat, yVelFormat;

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        
        // hide the title bar
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        
        // hide the notification bar
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        
        // lock the view to a vertical portrait orientation
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE);
        
        // set the view the layout of the time_series_example.xml file
        setContentView(R.layout.velocity_visualization_layout);
        
        // initialize the accelerometer
        accel = new Accelerometer(this,this, true, false);
        
        // initiliaze the accelerometer file manager
        afm = new AccelerometerFileManager(this, "");

        // Initialize text boxes
        vx = (TextView) findViewById(R.id.vx);
        vy = (TextView) findViewById(R.id.vy);

        // Setup pause/resume button
        pause = (Button) findViewById(R.id.pause);
        // Action handler for pause button
        pause.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (paused){
                    paused = false;
                    pause.setText("Pause");
                }
                else {
                    paused = true;
                    pause.setText("Resume");
                }
            }
        });

        // setup done button; returns to previous screen on click
        done = (Button) findViewById(R.id.done);
        // Action handler for done button. Exits the screen
        done.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                finish();
            }
        });

        // Setup zero button; Sets accelerometer offset so that current orientation is zero position
        zero = (Button) findViewById(R.id.zero);
        // Action handler for zero button. Calibrates the accelerometer and zeros the velocity
        zero.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                accel.calibrate();
            }
        });
        
        // Set up the plot
        plot = (XYPlot) findViewById(R.id.plot);
        plot.setTitle("Velocity Component Plots");
        plot.getGraphWidget().setSize(new SizeMetrics(0.9f, SizeLayoutType.RELATIVE, 1f, SizeLayoutType.RELATIVE)); // adjust size of plot
        plot.getGraphWidget().setAnchor(AnchorPosition.RIGHT_MIDDLE); // center the plot vertically
        plot.setRangeUpperBoundary(5*1.1, BoundaryMode.FIXED); // set the upper limit for the y axiz
        plot.setRangeLowerBoundary(-5, BoundaryMode.FIXED); // set the lower limit for the y axis
        plot.setRangeStep(XYStepMode.INCREMENT_BY_VAL, 1); // set the range between each tick on the y axis
        plot.getLegendWidget().setSize(new SizeMetrics(30, SizeLayoutType.ABSOLUTE, 150, SizeLayoutType.ABSOLUTE)); // resize the legend
        plot.getLegendWidget().position(10, XLayoutStyle.ABSOLUTE_FROM_RIGHT, 50, YLayoutStyle.ABSOLUTE_FROM_BOTTOM); // add margins to the legend
        plot.getLegendWidget().setAnchor(AnchorPosition.RIGHT_BOTTOM); // align the legend to the bottom right corner of the screen
        plot.setRangeLabel("Velocity (m/s)"); // set the y axis label
        plot.setDomainLabel("Data Point Number"); // set the x axis labe
        
        // fill up the arraylists with initial values
        xVel = new ArrayList<Number>();
        yVel = new ArrayList<Number>();
        for (int i = 0; i < ARRAYSIZE; i++){
            xVel.add(0);
            yVel.add(0);
            
            xRange.add(sensorCount++);
        }

        // create XYSeries from xRange and the individual vectors and assign their titles
        xVelSeries = new SimpleXYSeries(xRange, xVel, "X Vel");
        yVelSeries = new SimpleXYSeries(xRange, yVel, "Y Vel");

        // color the xVelSeries red
        xVelFormat = new LineAndPointFormatter(
                Color.RED,
                Color.TRANSPARENT,
                Color.TRANSPARENT,
                null
        );
        
        // color the YVelSeries green
        yVelFormat = new LineAndPointFormatter(
                Color.GREEN,
                Color.TRANSPARENT,
                Color.TRANSPARENT,
                null
        );
    }

    // Update the rolling plot
    private void graph(){
        plot.clear(); // clear the plot

        // add new series' to the xyplot using the appropriate formats
        plot.addSeries(xVelSeries, xVelFormat);
        plot.addSeries(yVelSeries, yVelFormat);

        plot.redraw(); // redraw the new data
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Stops the accelerometer event listener
        accel.pause();
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        // Reregisters the accelerometer event listener
        accel.resume();
    }

    // method called once the accelerometer values change
    public void onSensorChanged(SensorEvent event) {
        accel.updateAccelVals(event); // update the accelerometer vals in the accel object
        
        // only perform the following if not paused
        if (!paused){
            float[] velocity = accel.getVelocity();
            // add the next calculated velocity
            // to the end of each of the respective series
            xVelSeries.addLast(sensorCount, velocity[0]);
            yVelSeries.addLast(sensorCount, velocity[1]);
            
            // remove the first values of each series to simulate a rolling plot
            xVelSeries.removeFirst();
            yVelSeries.removeFirst();
            
            // Show the numerical value of the velocities in the text boxes
            vx.setText(String.format("vx: %.3f", velocity[0]));
            vy.setText(String.format("vy: %.3f", velocity[1]));
            
            // get another data point
            sensorCount++;
            graph(); // graph the new data set
        }
    }

    // Event handler for a change in accuracy. No action needed
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        
    }
}
