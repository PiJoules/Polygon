package com.test;

// these are all the classes from other packages necessary for plotting the
// accelerometer data
import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
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
import com.androidplot.xy.BarRenderer;
import com.androidplot.xy.BoundaryMode;
import com.androidplot.xy.PointLabelFormatter;
import com.androidplot.xy.SimpleXYSeries;
import com.androidplot.xy.XYPlot;
import com.androidplot.xy.XYStepMode;
import java.text.DecimalFormat;
import java.util.ArrayList;

// the TimeSeriesActivity is an Activity that uses the SensorEventListener for
// getting accelerometer values
public class NoiseVisualization extends Activity implements SensorEventListener{
    // The plot on which the histogram will be placed
    private XYPlot plot;
    
    // Text boxes
    private TextView mean, variance, std;
    
    // buttons
    private Button switchButton, pause, done;
    
    // int deciding which of the accelerometer components is displayed
    private int show = 2; // 0 -> X, 1 -> Y, 2 -> Z
    private boolean paused = false;
    
    private final ArrayList<Number> XAccelCount = new ArrayList<Number>(), // range vals for x component
            YAccelCount = new ArrayList<Number>(), // range vals for y component
            ZAccelCount = new ArrayList<Number>(), // range vals for z component
            domainAccel = new ArrayList<Number>(); // domain vals
    // ArrayLists containg the order in which to decrement and increment any one of
    // the AccelCounts. Once the number of readings exceeds the bufferSize, the next 
    // value is appended to the end of one of these arrays and the first element is
    // removed
    private final ArrayList<Integer> XAccelOrder = new ArrayList<Integer>(),
            YAccelOrder = new ArrayList<Integer>(),
            ZAccelOrder = new ArrayList<Integer>();
    private SimpleXYSeries XAccelNoise, YAccelNoise, ZAccelNoise;
    private MyBarFormatter formatter1, formatter2, formatter3;

    // The accelerometer object from which we received accelerometer values
    private Accelerometer accel;
    // accelerometer file manager for handling the text file that stores alpha and sma values
    private AccelerometerFileManager afm;

    // Number of readings to save in buffer
    private int bufferSize;
    // List of past readings 
    private ArrayList<float[]> bufferedData; // incoming sensor vals
    private int binSize;

    // High and low values for expected range
    private float range_min, range_max;
    

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
        binSize = (int) accelSettings[AccelerometerFileManager.BINS];

        // Get range min and max from accelerometer settings
        range_min = accelSettings[AccelerometerFileManager.RANGE_MIN];
        range_max = accelSettings[AccelerometerFileManager.RANGE_MAX];
        
        // Initialize text boxes
        mean = (TextView) findViewById(R.id.means);
        variance = (TextView) findViewById(R.id.variance);
        std = (TextView) findViewById(R.id.std);
        
        // initialize butoons
        switchButton = (Button) findViewById(R.id.switchButton);
        switchButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (++show > 2) show = 0;
                if (show == 0){
                    plot.setTitle("Noise Visualization (X Component)");
                    switchButton.setText("Switch to Y");
                }
                else if (show == 1){
                    plot.setTitle("Noise Visualization (Y Component)");
                    switchButton.setText("Switch to Z");
                }
                else if (show == 2){
                    plot.setTitle("Noise Visualization (Z Component)");
                    switchButton.setText("Switch to X");
                }
            }
        });
        pause = (Button) findViewById(R.id.pause);
        pause.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                paused = !paused;
                if (paused) pause.setText("Reusme");
                else pause.setText("Pause");
            }
        });
        done = (Button) findViewById(R.id.done);
        done.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                finish();
            }
        });

        float step = (range_max-range_min)/binSize;
        while (range_min <= range_max){
            domainAccel.add(range_min);
            XAccelCount.add(0);
            YAccelCount.add(0);
            ZAccelCount.add(0);
            range_min += step;
        }

        // setup and decorate the plot
        plot = (XYPlot) findViewById(R.id.histogram);
        plot.setTitle("Noise Visualization (Z Component)");
        plot.getGraphWidget().setSize(new SizeMetrics(0.9f, SizeLayoutType.RELATIVE, 1f, SizeLayoutType.RELATIVE)); // asjust size of plot
        plot.setRangeUpperBoundary(bufferSize*1.1, BoundaryMode.FIXED); // set the upper limit for the y axiz
        plot.setRangeLowerBoundary(0, BoundaryMode.FIXED); // set the lower limit for the y axis
        plot.setRangeStep(XYStepMode.INCREMENT_BY_VAL, bufferSize/10); // set the range between each tick on the y axis
        plot.getLegendWidget().setSize(new SizeMetrics(30, SizeLayoutType.ABSOLUTE, 50, SizeLayoutType.ABSOLUTE)); // resize the legend
        plot.getLegendWidget().position(10, XLayoutStyle.ABSOLUTE_FROM_RIGHT, 50, YLayoutStyle.ABSOLUTE_FROM_BOTTOM); // add margins to the legend
        plot.getLegendWidget().setAnchor(AnchorPosition.RIGHT_BOTTOM); // align the legend to the bottom right corner of the screen
        plot.setRangeLabel("Occurences"); // set the y axis label
        plot.setRangeValueFormat(new DecimalFormat("#"));
        plot.setDomainLabel("Acceleration Range (g)"); // set the x axis label
        plot.setDomainValueFormat(new DecimalFormat("#.##"));
               
        // create XYSeries from xRange and the individual vectors and assign their titles
        XAccelNoise = new SimpleXYSeries(domainAccel, XAccelCount, "X-Accel Count");
        YAccelNoise = new SimpleXYSeries(domainAccel, YAccelCount, "Y-Accel Count");
        ZAccelNoise = new SimpleXYSeries(domainAccel, ZAccelCount, "Z-Accel Count");
        
        // clear the text above each of the bars in the bar graph
        PointLabelFormatter plf = new PointLabelFormatter(Color.BLUE);
        Paint transparentPaint = new Paint();
        transparentPaint.setColor(Color.TRANSPARENT);
        plf.setTextPaint(transparentPaint);
        
        // initiliaze the bar graph formatters (decorations)
        formatter1 = new MyBarFormatter(Color.RED, Color.RED);
        formatter1.setPointLabelFormatter(plf);
        formatter2 = new MyBarFormatter(Color.GREEN, Color.GREEN);
        formatter2.setPointLabelFormatter(plf);
        formatter3 = new MyBarFormatter(Color.BLUE, Color.BLUE);
    }
   
    // method for graphing the series on the xyplot
    private void graph(){
        plot.clear(); // clear the plot
        
        if (show == 0) plot.addSeries(XAccelNoise, formatter1);
        else if (show == 1) plot.addSeries(YAccelNoise, formatter2);
        else if (show == 2) plot.addSeries(ZAccelNoise, formatter3);
        MyBarRenderer renderer = ((MyBarRenderer) plot.getRenderer(MyBarRenderer.class));
        renderer.setBarWidthStyle(BarRenderer.BarWidthStyle.VARIABLE_WIDTH);
        renderer.setBarGap(0f);
        
        plot.redraw(); // redraw the new data
    }

    // method called once the accelerometer values change
    public void onSensorChanged(SensorEvent event) {
        // Update the accelerometer values in the accel object
        accel.updateAccelVals(event);

        float[] accelVals = accel.getAccelUnfiltered();
        
        if (!paused){
            // Add/Remove readings from buffer and the order ArrayLists
            while (bufferedData.size() >= bufferSize){
                // Delete oldest reading to make room for new one
                bufferedData.remove(0);
            }
            while (XAccelOrder.size() >= bufferSize && show == 0){
                int xIndex = XAccelOrder.remove(0);
                float xVal = XAccelNoise.getY(xIndex).floatValue();
                if (xVal > 0) XAccelNoise.setY(xVal-1, xIndex);
            }
            while (YAccelOrder.size() >= bufferSize && show == 1){            
                int yIndex = YAccelOrder.remove(0);
                float yVal = YAccelNoise.getY(yIndex).floatValue();
                if (yVal > 0) YAccelNoise.setY(yVal-1, yIndex);
            }
            while (ZAccelOrder.size() >= bufferSize && show == 2){
                int zIndex = ZAccelOrder.remove(0);
                float zVal = ZAccelNoise.getY(zIndex).floatValue();
                if (zVal > 0) ZAccelNoise.setY(zVal-1, zIndex);
            }
            // Else, buffer size is less than desired size, so nothing is removed
            bufferedData.add(accelVals);
        }
        
        // Calculate noise statistics
        float[] means = getMean();
        float[] var = getVariance();
        if (show == 0){
            mean.setText(String.format("X Mean (g): %.3f", means[0]/9.81f));
            variance.setText(String.format("X Variance (g): %.3f", var[0]/9.81f));
            std.setText(String.format("X std (g): %.3f", Math.sqrt(var[0]/9.81f)));
        }
        else if (show == 1){
            mean.setText(String.format("Y Mean (g): %.3f", means[1]/9.81f));
            variance.setText(String.format("Y Variance (g): %.3f", var[1]/9.81f));
            std.setText(String.format("Y std (g): %.3f", Math.sqrt(var[1]/9.81f)));
        }
        else if (show == 2){
            mean.setText(String.format("Z Mean (g): %.3f", means[2]/9.81f));
            variance.setText(String.format("Z Variance (g): %.3f", var[2]/9.81f));
            std.setText(String.format("Z std (g): %.3f", Math.sqrt(var[2]/9.81f)));
        }
           
        if (!paused){
            for (int i = 1; i < domainAccel.size(); i++){
                if (accel.getAccel()[0]/9.81f < domainAccel.get(i).floatValue() && show == 0){
                    XAccelOrder.add(i-1);
                    float xVal = XAccelNoise.getY(i-1).floatValue();
                    XAccelNoise.setY(xVal+1, i-1);
                    break;
                }
                else if (accel.getAccel()[1]/9.81f < domainAccel.get(i).floatValue() && show == 1){
                    YAccelOrder.add(i-1);
                    float yVal = YAccelNoise.getY(i-1).floatValue();
                    YAccelNoise.setY(yVal+1, i-1);
                    break;
                }
                else if (accel.getAccel()[2]/9.81f < domainAccel.get(i).floatValue() && show == 2){
                    ZAccelOrder.add(i-1);
                    float zVal = ZAccelNoise.getY(i-1).floatValue();
                    ZAccelNoise.setY(zVal+1, i-1);
                    break;
                }
            }
        }
        graph(); // graph the new data set
    }

    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        
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
        
        float sumX = 0;
        float sumY = 0;
        float sumZ = 0;
        
        // Determine difference from average and square it
        for (float[] accel : bufferedData){
            float tempX = average[0] - accel[0];
            float tempY = average[1] - accel[1];
            float tempZ = average[2] - accel[2];
            sumX += tempX*tempX;
            sumY += tempY*tempY;
            sumZ += tempZ*tempZ;
        }

        // Find average and return results
        float length = (float) bufferedData.size();
        return new float[]{sumX/length, sumY/length, sumZ/length};
    }
}