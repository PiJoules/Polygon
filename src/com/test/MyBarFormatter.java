/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.test;

import com.androidplot.ui.SeriesRenderer;
import com.androidplot.xy.BarFormatter;
import com.androidplot.xy.XYPlot;

/**
 *
 * @author Pi_Joules
 */
public class MyBarFormatter extends BarFormatter {
    public MyBarFormatter(int fillColor, int borderColor) {
        super(fillColor, borderColor);
    }

    @Override
    public Class<? extends SeriesRenderer> getRendererClass() {
        return MyBarRenderer.class;
    }

    @Override
    public SeriesRenderer getRendererInstance(XYPlot plot) {
        return new MyBarRenderer(plot);
    }
}
