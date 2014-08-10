
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
    private final int[] enemyCount = {5,7,10}; // total number of enemies allowed on screen
    private final int[] enemySideRange = {0,1,4}; // max number of sides from enemy (3 + enemySideRange)
    private final float[] enemyLengthRange = {0.7f,0.8f,1f}; // max enemy width = 0.5*(player width) + enemyLengthRange
    
    // player properties
    private final float initSize;
    
    // canvas properties
    private final int[] colors = {Color.WHITE,Color.MAGENTA,Color.YELLOW};
    private final float[] sizeLimits = {0,11,13};
    private int sizeLimitIterator = 0;
    private float scaleRatio = 1f;
    private float lastScaleRatio = scaleRatio;
    private float scaleRatioStep = 0f;
    //private float canvasdx = 0, canvasdy = 0, scalingCanvasdx = 0, scalingCanvasdy = 0;
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
                /*canvasdx = 0.5f*canvasWidth*(1/scaleRatio-1);
                canvasdy = 0.5f*canvasHeight*(1/scaleRatio-1);
                canvasBounds[0] = -canvasdx;
                canvasBounds[1] = -canvasdy;
                canvasBounds[2] = canvasWidth + canvasdx;
                canvasBounds[3] = canvasHeight + canvasdy;*/
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
    
    public void setPolygonProperties(Polygon polygon, float length){
        polygon.setDisplayLength(length);
        polygon.setBounds(length/2);
        polygon.setPath(polygon.getSides(), polygon.getAngle(), length/2);
    }
    
    public void setPlayerProperties(Player player, float radius){
        player.setDisplayRadius(radius);
        float left = player.getX()-radius;
        float right = player.getX()+radius;
        float top = player.getY()-radius;
        float bottom = player.getY()+radius;
        player.oval.set(left, top, right, bottom);
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
    
    /*public float getCanvasdx(){
        return canvasdx;
    }*/
    public ArrayList<Float> getCanvasdx(){
        return canvasdx;
    }
    
    public float getScalingCanvasdx(){
        return scalingCanvasdx;
    }
    
    public float getScalingCanvasdy(){
        return scalingCanvasdy;
    }
    
    /*public float getCanvasdy(){
        return canvasdy;
    }*/
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
