package com.test;

// these are all the classes from other packages necessary for plotting the
// accelerometer data
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

// the TimeSeriesActivity is an Activity that uses the SensorEventListener for
// getting accelerometer values
public class NoiseVisualization extends Activity implements SensorEventListener{

    private XYPlot plot; // the plot on which the graphs will be placed
    
    private TextView mean, mean_outside, variance;

    // The accelerometer object from which we received accelerometer values
    private Accelerometer accel;
    // accelerometer file manager for handling the text file that stores alpha and sma values
    private AccelerometerFileManager afm;

    // Number of readings to save in buffer
    private int bufferSize;
    // List of past readings 
    private ArrayList<float[]> bufferedData;

    // formatters for each series that decorate the lines
    // hide is the transparent decoration for hiding the threshold if necessary
//    private LineAndPointFormatter XAccelFormat, YAccelFormat, ZAccelFormat, thresholdFormat, hide;
    

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        // hide the title bar
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        
        // hide the notification bar
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        
        // lock the view to a vertical portrait orientation
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE);
        
        // set the view the layout of the time_series_example.xml file
        setContentView(R.layout.noise_visualization);
        
        // Initialize the accelerometer
        accel = new Accelerometer(this,this, false, false);
        
        // Initiliaze the accelerometer file manager
        afm = new AccelerometerFileManager(this, "");

        bufferSize = 1000;
        bufferedData = new ArrayList<float[]>();
        
        // Initialize text boxes
        mean = (TextView) findViewById(R.id.mean);
        mean_outside = (TextView) findViewById(R.id.mean_outside);
        variance = (TextView) findViewById(R.id.variance);

        // setup and decorate the plot
        plot = (XYPlot) findViewById(R.id.histogram);
        plot.getGraphWidget().setSize(new SizeMetrics(0.9f, SizeLayoutType.RELATIVE, 1f, SizeLayoutType.RELATIVE)); // adjust size of plot
        plot.setRangeUpperBoundary(1000, BoundaryMode.FIXED); // set the upper limit for the y axiz
        plot.setRangeLowerBoundary(0, BoundaryMode.FIXED); // set the lower limit for the y axis
        plot.setRangeStep(XYStepMode.INCREMENT_BY_VAL, 100); // set the range between each tick on the y axis
        //plot.getLegendWidget().setSize(new SizeMetrics(30, SizeLayoutType.ABSOLUTE, 300, SizeLayoutType.ABSOLUTE)); // resize the legend
        //plot.getLegendWidget().position(50, XLayoutStyle.ABSOLUTE_FROM_RIGHT, 50, YLayoutStyle.ABSOLUTE_FROM_BOTTOM); // add margins to the legend
        //plot.getLegendWidget().setAnchor(AnchorPosition.RIGHT_BOTTOM); // align the legend to the bottom right corner of the screen
        plot.setRangeLabel("Occurences"); // set the y axis label
        plot.setDomainLabel("Acceleration Range"); // set the x axis label
/*                
        // create XYSeries from xRange and the individual vectors and assign their titles
        XAccelSeries = new SimpleXYSeries(xRange, XAccel, "XAccel");
        YAccelSeries = new SimpleXYSeries(xRange, YAccel, "YAccel");
        ZAccelSeries = new SimpleXYSeries(xRange, ZAccel, "ZAccel");
        thresholdSeries = new SimpleXYSeries(xRange, threshold, "Threshold");
        
        // color the XAccelSeries red
        XAccelFormat = new LineAndPointFormatter(
                Color.RED,
                Color.RED,
                Color.TRANSPARENT,
                null
        );
        
        // color the YAccelSeries green
        YAccelFormat = new LineAndPointFormatter(
                Color.GREEN,
                Color.GREEN,
                Color.TRANSPARENT,
                null
        );
        
        // color the ZAccelSeries blue
        ZAccelFormat = new LineAndPointFormatter(
                Color.BLUE,
                Color.BLUE,
                Color.TRANSPARENT,
                null
        );
        
        // color the thresholdSeries Cyan
        thresholdFormat = new LineAndPointFormatter(
                Color.CYAN,
                Color.CYAN,
                Color.TRANSPARENT,
                null
        );
        
        // make the threshold series thransparent if the show_hide button is pressed
        hide = new LineAndPointFormatter(
                Color.TRANSPARENT,
                Color.TRANSPARENT,
                Color.TRANSPARENT,
                null
        );
        */
        
    }
   
    // method for graphing the series on the xyplot
    private void graph(){
        plot.clear(); // clear the plot

        plot.redraw(); // redraw the new data
    }

    // method called once the accelerometer values change
    public void onSensorChanged(SensorEvent event) {
        // Update the accelerometer values in the accel object
        accel.updateAccelVals(event);

        // Add/Remove readings from buffer
        if(bufferedData.size() > bufferSize){
            // Too many readings, delete extra
            int excessReadings = bufferedData.size() - bufferSize + 1; // + 1 because new reading will be added
            for(int i = 0; i < excessReadings; i++){
                bufferedData.remove(0);
            }
        }
        else if(bufferedData.size() == bufferSize){
            // Delete oldest reading to make room for new one
            bufferedData.remove(0);
        }
        // Else, buffer size is less than desired size, so nothing is removed
        bufferedData.add(accel.getAccelFiltered());
        
        // Calculate noise statistics
        float[] means = getMean();
        mean.setText(String.format("X Mean: %.2f\nY Mean: %.2f\nZ Mean: %.2f", means[0], means[1], means[2]));
        mean_outside.setText("Mean Out of Range: ");
        float[] var = getVariance();
        variance.setText(String.format("X Variance: %.2f Y\nVariance: %.2f\nZ Variance: %.2f", var[0], var[1], var[2]));

        //graph(); // graph the new data set
    }

    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        
    }
    

    // Calculate mean of buffer data
    private float[] getMean(){
        float aveX = 0f;
        float aveY = 0f;
        float aveZ = 0f;

        // Sum all acceleration values
        for(float[] accel : bufferedData){
            aveX += accel[0];
            aveY += accel[1];
            aveZ += accel[2];
        }

        // Determine average
        aveX /= bufferedData.size();
        aveY /= bufferedData.size();
        aveZ /= bufferedData.size();
        
        return new float[]{aveX, aveY, aveZ};
    }

    // Calculate variance of data buffer
    private float[] getVariance(){
        // Save average value
        float[] average = getMean();
        
        float tempX = 0;
        float tempY = 0;
        float tempZ = 0;
        float sumX = 0;
        float sumY = 0;
        float sumZ = 0;
        
        // Determine difference from average and square it
        for (float[] accel : bufferedData){
            tempX = average[0] - accel[0];
            tempY = average[1] - accel[1];
            tempZ = average[2] - accel[2];
            sumX += tempX*tempX;
            sumY += tempY*tempY;
            sumZ += tempZ*tempZ;
        }

        // Find average and return results
        float length = (float) bufferedData.size();
        return new float[]{sumX/length, sumY/length, sumZ/length};
    }
}