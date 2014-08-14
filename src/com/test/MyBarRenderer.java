/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.test;

import com.androidplot.xy.BarRenderer;
import com.androidplot.xy.XYPlot;
import com.androidplot.xy.XYSeries;

/**
 *
 * @author Pi_Joules
 */
public class MyBarRenderer extends BarRenderer<MyBarFormatter> {
    public MyBarRenderer(XYPlot plot) {
        super(plot);
    }

    public MyBarFormatter getFormatter(int index, XYSeries series) {
        return getFormatter(series);
    }
}
