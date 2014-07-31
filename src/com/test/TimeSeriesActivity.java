package com.test;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import com.androidplot.xy.SimpleXYSeries;
import com.androidplot.xy.XYSeries;
import com.androidplot.xy.*;
import java.util.ArrayList;

import java.util.Arrays;

public class TimeSeriesActivity extends Activity{

    private XYPlot plot;
    private LinearLayout screen;
    private ArrayList<Number> series1Numbers;

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
        
        Number[] series = {1, 8, 5, 2, 7, 4};
        series1Numbers = new ArrayList<Number>(Arrays.asList(series));
        
        screen = (LinearLayout) findViewById(R.id.screen);
        screen.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Number lastNum = series1Numbers.get(series1Numbers.size()-1);
                lastNum = lastNum.intValue() + 1;
                series1Numbers.add(lastNum);
                series1Numbers.remove(0);
                plot.clear();
                graph();
                plot.redraw();
            }
        });

        graph();
    }
    
    private void graph(){
        plot = (XYPlot) findViewById(R.id.plot1);

        // Create a couple arrays of y-values to plot:
        //Number[] series1Numbers = {1, 8, 5, 2, 7, 4};
        Number[] series2Numbers = {4, 6, 3, 8, 2, 10};

        // Turn the above arrays into XYSeries':
        XYSeries series1 = new SimpleXYSeries(
                series1Numbers,          // SimpleXYSeries takes a List so turn our array into a List
                SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, // Y_VALS_ONLY means use the element index as the x value
                "Series1");                             // Set the display title of the series

        // same as above
        //XYSeries series2 = new SimpleXYSeries(Arrays.asList(series2Numbers), SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, "Series2");

        // Create a formatter to use for drawing a series using LineAndPointRenderer
        // and configure it from xml:
        LineAndPointFormatter series1Format = new LineAndPointFormatter();
        //series1Format.setPointLabelFormatter(new PointLabelFormatter());
        //series1Format.configure(getApplicationContext(),R.xml.line_point_formatter_with_plf1);

        // add a new series' to the xyplot:
        plot.addSeries(series1, series1Format);

        // same as above:
        //LineAndPointFormatter series2Format = new LineAndPointFormatter();
        //series2Format.setPointLabelFormatter(new PointLabelFormatter());
        //series2Format.configure(getApplicationContext(),R.xml.line_point_formatter_with_plf2);
        //plot.addSeries(series2, series2Format);

        // reduce the number of range labels
        plot.setTicksPerRangeLabel(3);
        //plot.getGraphWidget().setDomainLabelOrientation(-45);
    }
}