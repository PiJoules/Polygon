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
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import com.androidplot.ui.SizeLayoutType;
import com.androidplot.ui.SizeMetrics;
import com.androidplot.xy.BarFormatter;
import com.androidplot.xy.BoundaryMode;
import com.androidplot.xy.PointLabelFormatter;
import com.androidplot.xy.SimpleXYSeries;
import com.androidplot.xy.XYPlot;
import com.androidplot.xy.XYStepMode;
import java.util.ArrayList;

// the TimeSeriesActivity is an Activity that uses the SensorEventListener for
// getting accelerometer values
public class NoiseVisualization extends Activity implements SensorEventListener{
    // The plot on which the histogram will be placed
    private XYPlot plot;
    
    // Text boxes
    private TextView mean, mean_outside, variance;
    
    private ArrayList<Number> XAccel = new ArrayList<Number>(),
            YAccel = new ArrayList<Number>(),
            ZAccel = new ArrayList<Number>(),
            xRange = new ArrayList<Number>();
    private SimpleXYSeries XAccelNoise, YAccelNoise, ZAccelNoise;
    private int sensorCount = 1;
    private BarFormatter bf1;

    // The accelerometer object from which we received accelerometer values
    private Accelerometer accel;
    // accelerometer file manager for handling the text file that stores alpha and sma values
    private AccelerometerFileManager afm;

    // Number of readings to save in buffer
    private int bufferSize;
    // List of past readings 
    private ArrayList<float[]> bufferedData;

    // High and low values for expected range
    private float range_min, range_max;

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
        
        // Initiliaze the accelerometer file manager and get settings
        afm = new AccelerometerFileManager(this, "");
        float[] accelSettings = afm.getAccelData();

        // Get buffer size from accelerometer settings
        bufferSize = (int) accelSettings[AccelerometerFileManager.BUFFER_SIZE];
        bufferedData = new ArrayList<float[]>();

        // Get range min and max from accelerometer settings
        range_min = accelSettings[AccelerometerFileManager.RANGE_MIN];
        range_max = accelSettings[AccelerometerFileManager.RANGE_MAX];
        
        // Initialize text boxes
        mean = (TextView) findViewById(R.id.mean);
        mean_outside = (TextView) findViewById(R.id.mean_outside);
        variance = (TextView) findViewById(R.id.variance);
        
        for (int i = 0; i < 100; i++){
            XAccel.add(0);
            YAccel.add(0);
            ZAccel.add(0);
            xRange.add(sensorCount++);
        }

        // setup and decorate the plot
        plot = (XYPlot) findViewById(R.id.histogram);
        plot.getGraphWidget().setSize(new SizeMetrics(0.9f, SizeLayoutType.RELATIVE, 1f, SizeLayoutType.RELATIVE)); // adjust size of plot
        plot.setRangeUpperBoundary(bufferSize, BoundaryMode.FIXED); // set the upper limit for the y axiz
        plot.setRangeLowerBoundary(0, BoundaryMode.FIXED); // set the lower limit for the y axis
        plot.setRangeStep(XYStepMode.INCREMENT_BY_VAL, 0.25); // set the range between each tick on the y axis
        plot.setRangeLabel("Occurences"); // set the y axis label
        plot.setDomainLabel("Acceleration Range"); // set the x axis label
               
        // create XYSeries from xRange and the individual vectors and assign their titles
        XAccelNoise = new SimpleXYSeries(xRange, XAccel, "XAccel");
        YAccelNoise = new SimpleXYSeries(xRange, YAccel, "YAccel");
        ZAccelNoise = new SimpleXYSeries(xRange, ZAccel, "ZAccel");
        
        bf1 = new BarFormatter(Color.RED, Color.RED);
        bf1.setPointLabelFormatter(new PointLabelFormatter(Color.RED));
    }
   
    // method for graphing the series on the xyplot
    private void graph(){
        plot.clear(); // clear the plot
        
        plot.addSeries(XAccelNoise, bf1);
        //plot.addSeries(YAccelNoise, YAccelFormat);
        //plot.addSeries(ZAccelNoise, ZAccelFormat);

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

        /*XAccelNoise.addLast(sensorCount, accel.getAccel()[0]/9.81f);
        YAccelNoise.addLast(sensorCount, accel.getAccel()[1]/9.81f);
        ZAccelNoise.addLast(sensorCount, accel.getAccel()[2]/9.81f);
        XAccelNoise.removeFirst();
        YAccelNoise.removeFirst();
        ZAccelNoise.removeFirst();*/
        XAccelNoise.setY(accel.getAccel()[0]/9.81f, 50);
        
        graph(); // graph the new data set
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