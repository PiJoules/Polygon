
package com.test;

// class for setting contraints for the objects on the canvas and the 

import android.graphics.Color;
import java.util.ArrayList;
import java.util.Random;

// layout/design of the canvas
public class CanvasInstructions {
    
    // utility objects
    private final Random r = new Random();
    
    // polygon properties
    private final int[] enemyCount = {5,6,7,8,9,10}; // total number of enemies allowed on screen
    private final int[] enemySideRange = {0,1,2,3,4,5}; // max number of sides from enemy (3 + enemySideRange)
    private final float[] enemyLengthRange = {0.6f,0.7f,0.8f,0.9f,1f,1.2f}; // max enemy width = 0.5*(player width) + enemyLengthRange
    
    // player properties
    private final float initSize;
    
    // canvas properties
    private final int[] colors = {Color.WHITE,Color.rgb(255, 165, 0),Color.GREEN,Color.BLUE,Color.rgb(204,0,102),Color.rgb(128, 0, 128)};
    private final float[] sizeLimits = {0,11,13,16,20,30};
    private int sizeLimitIterator = 0;
    private float scaleRatio = 1f;
    private float lastScaleRatio = scaleRatio;
    private float scaleRatioStep = 0f;
    private float scalingCanvasdx = 0, scalingCanvasdy = 0;
    private final ArrayList<Float> canvasdx = new ArrayList<Float>(), canvasdy = new ArrayList<Float>();
    private float[] canvasBounds = new float[4];
    private final float canvasWidth, canvasHeight;
    private boolean scaling = false;
    
    public CanvasInstructions(float initSize, float canvasLeft, float canvasTop, float canvasRight, float canvasBottom){
        
        this.initSize = initSize;
        
        float[] bounds = {canvasLeft, canvasTop, canvasRight, canvasBottom};
        canvasBounds = bounds;
        canvasWidth = Math.abs(canvasRight-canvasLeft);
        canvasHeight = Math.abs(canvasTop-canvasBottom);
        
        canvasdx.add(0f); canvasdy.add(0f);
    }
    
    public void updateInstructions(float size){
        if (size > sizeLimits[sizeLimitIterator] && sizeLimitIterator < sizeLimits.length-1 && !scaling){
            if (size > sizeLimits[sizeLimitIterator+1]){
                lastScaleRatio = scaleRatio;
                scaleRatio = initSize/size;
                canvasdx.add(0.5f*canvasWidth*(1/scaleRatio-1));
                canvasdy.add(0.5f*canvasHeight*(1/scaleRatio-1));
                canvasBounds[0] = -canvasdx.get(canvasdx.size()-1);
                canvasBounds[1] = -canvasdy.get(canvasdy.size()-1);
                canvasBounds[2] = canvasWidth + canvasdx.get(canvasdx.size()-1);
                canvasBounds[3] = canvasHeight + canvasdy.get(canvasdy.size()-1);
                scaling = true;
                scaleRatioStep = (lastScaleRatio-scaleRatio)/100f;
                
                sizeLimitIterator++;
            }
        }
    }
    
    public boolean shouldScale(){
        if (scaling){
            if (lastScaleRatio - scaleRatio <= 0){
                lastScaleRatio = scaleRatio;
                scaling = false;
            }
            else lastScaleRatio -= scaleRatioStep;
            scalingCanvasdx = 0.5f*canvasWidth*(1/lastScaleRatio-1);
            scalingCanvasdy = 0.5f*canvasHeight*(1/lastScaleRatio-1);
            return true;
        }
        else return false;
    }
    
    public float getLastScaleRatio(){
        return lastScaleRatio;
    }
    
    public float getCanvasWidth(){
        return canvasWidth;
    }
    
    public float getCanvasHeight(){
        return canvasHeight;
    }
    
    public float[] getCanvasBounds(){
        return canvasBounds;
    }
    
    public float getScaleRatio(){
        return scaleRatio;
    }
    
    public ArrayList<Float> getCanvasdx(){
        return canvasdx;
    }
    
    public float getScalingCanvasdx(){
        return scalingCanvasdx;
    }
    
    public float getScalingCanvasdy(){
        return scalingCanvasdy;
    }
    
    public ArrayList<Float> getCanvasdy(){
        return canvasdy;
    }
    
    public int getEnemyCount(){
        return enemyCount[sizeLimitIterator];
    }
    
    public int getEnemySides(){
        return 3 + r.nextInt(enemySideRange[sizeLimitIterator] + 1);
    }
    
    public float getEnemyRadius(float playerRadius){
        return 0.5f*playerRadius + r.nextFloat()*playerRadius*enemyLengthRange[sizeLimitIterator];
    }
    
    public int[] getColors(){
        return colors;
    }
    
}
