/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.test;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.text.InputType;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Random;

/**
 *
 * @author Pi_Joules
 */
public class Test2 extends Activity implements SensorEventListener{
    
    // drawable stuff
    CustomDrawableView mCustomDrawableView;
    
    // accelerometer stuff
    public float xAccel, yAccel, zAccel;
    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    
    public TextView tvX, tvY, tvZ, tvXPos, tvYPos, tvXVel, tvYVel, size;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        LinearLayout layout = (LinearLayout) findViewById(R.id.main_layout);
        
        tvX = (TextView)findViewById(R.id.x_axis);
        tvY = (TextView)findViewById(R.id.y_axis);
        tvZ = (TextView)findViewById(R.id.z_axis);
        tvXPos = (TextView) findViewById(R.id.xPos);
        tvYPos = (TextView) findViewById(R.id.yPos);
        tvXVel = (TextView) findViewById(R.id.xVel);
        tvYVel = (TextView) findViewById(R.id.yVel);
        size = (TextView) findViewById(R.id.size);
        
        
        setTitle("Test");
        
        // set screen vertical only
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        
        // accelerometer stuff
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        
        // setup custom canvas
        mCustomDrawableView = new CustomDrawableView(this);
        
        // set parameters
        LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        params.setMargins(20,20,20,20);
        mCustomDrawableView.setLayoutParams(params);
        layout.addView(mCustomDrawableView);
        
        
        System.out.println("done loading");
    }
    
    public void quitGame(float size){
        EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        input.setHint("Enter name here to save score");
        new AlertDialog.Builder(this)
                .setTitle("Max Size: " + round(size,4))
                .setView(input)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener(){
                    public void onClick(DialogInterface dialog, int which){
                        finish();
                    }
                }).show();
    }
    
    // stuff
    @Override
    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
    }
    @Override
    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this);
    }

    
    // sensor methods
    public void onSensorChanged(SensorEvent event) {
        
        // the actual tilt angles
        xAccel = event.values[0];
        yAccel = event.values[1];
        zAccel = event.values[2];
            
        // display the absolute values
        tvX.setText("xAcc: " + round(xAccel,4));
        tvY.setText("yAcc: " + round(yAccel,4));
        tvZ.setText("zAcc: " + round(zAccel,4));
    }
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    
    }
    
    // for rounding float to max of n decimal places
    public float round(float d, int decimalPlace){
        BigDecimal bd = new BigDecimal(Float.toString(d));
        bd = bd.setScale(decimalPlace, BigDecimal.ROUND_HALF_UP);
        return bd.floatValue();
    }

    // the game view
    private class CustomDrawableView extends View {
        
        // canvas setup
        public Paint p = new Paint();
        public float canvasWidth, canvasHeight;
        public boolean initialized = false; // make sure to get canvas width/height
        
        // controlled oval
        public RectF oval = new RectF(0,0,10,10);
        
        // other ovals
        public ArrayList<RectF> ovals = new ArrayList<RectF>();
        public ArrayList<float[]> ovalsVel = new ArrayList<float[]>(); // [xVel,yVel]

        public CustomDrawableView(Context context) {
            super(context);
            this.setBackgroundColor(Color.WHITE);
            
            // add circle every second
            final Handler h = new Handler();
            new Thread(new Runnable(){
                public void run() {
                    while (2<3){
                        try{
                            Thread.sleep(1000);
                            h.post(new Runnable(){
                                public void run(){
                                    if (ovals.size() < 10){ // limit of 10 ovals on canvas
                                        Random r = new Random();
                                        float nextWidth = r.nextFloat()*2*oval.width();
                                        float left = Math.round(r.nextFloat())*(canvasWidth-nextWidth);
                                        float top = Math.round(r.nextFloat())*(canvasHeight-nextWidth);
                                        float[] nextOvalsVel = {r.nextFloat()*2+(float)0.5,r.nextFloat()*2+(float)0.5};
                                        ovals.add(new RectF(left,top,left+nextWidth,top+nextWidth));
                                        ovalsVel.add(nextOvalsVel);
                                    }
                                }
                            });
                        }
                        catch (InterruptedException e){
                            // handle exceptions
                        }
                    }
                }
            }).start();
        }
        
        @Override
        public void onDraw(Canvas canvas) {
            
            if (initialized){
                
                // for xVel, tilting right is negative, so flip it
                // set accel values to velocity to make it easier to control
                oval.offset(-xAccel, yAccel);

                // set bounds for controlled oval
                if (oval.left < 0) oval.offsetTo(0,oval.top);
                if (oval.top < 0) oval.offsetTo(oval.left,0);
                if (oval.right > canvasWidth) oval.offsetTo(canvasWidth-oval.width(),oval.top);
                if (oval.bottom > canvasHeight) oval.offsetTo(oval.left,canvasHeight-oval.height());

                tvXPos.setText("xPos: " + round(oval.left,4));
                tvYPos.setText("yPos: " + round(oval.top,4));
                tvXVel.setText("xVel: " + round(-xAccel,4));
                tvYVel.setText("yVel: " + round(yAccel,4));
                
                p.setColor(Color.BLACK);
                canvas.drawOval(oval, p);
                
                // set values for other ovals
                for (int i = 0; i < ovals.size(); i++){
                    
                    // set bounds for other ovals
                    ovals.get(i).offset(ovalsVel.get(i)[0],ovalsVel.get(i)[1]);
                    if (ovals.get(i).left < 0){
                        ovals.get(i).offsetTo(0,ovals.get(i).top);
                        ovalsVel.get(i)[0] = Math.abs(ovalsVel.get(i)[0]);
                    }
                    if (ovals.get(i).top < 0){
                        ovals.get(i).offsetTo(ovals.get(i).left,0);
                        ovalsVel.get(i)[1] = Math.abs(ovalsVel.get(i)[1]);
                    }
                    if (ovals.get(i).right > canvasWidth){
                        ovals.get(i).offsetTo(canvasWidth-ovals.get(i).width(),ovals.get(i).top);
                        ovalsVel.get(i)[0] = -Math.abs(ovalsVel.get(i)[0]);
                    }
                    if (ovals.get(i).bottom > canvasHeight){
                        ovals.get(i).offsetTo(ovals.get(i).left,canvasHeight-ovals.get(i).height());
                        ovalsVel.get(i)[1] = -Math.abs(ovalsVel.get(i)[1]);
                    }

                    p.setColor(Color.GREEN);
                    canvas.drawOval(ovals.get(i), p);
                    
                    // initially used intersect method, though was not very accurate for ovals
                    double d = Math.sqrt(Math.pow(oval.centerX()-ovals.get(i).centerX(),2)+Math.pow(oval.centerY()-ovals.get(i).centerY(),2));
                    if (d < oval.width()/2 + ovals.get(i).width()/2){
                        if (ovals.get(i).width() <= oval.width()){
                            float area = (float) (Math.PI*Math.pow(ovals.get(i).width()/2,2));
                            float width = oval.width() + (float) (2*Math.sqrt(area/Math.PI))/10;
                            oval.set(oval.left, oval.top, oval.left+width, oval.top+width);
                            ovals.remove(i);
                            ovalsVel.remove(i);
                        }
                        else{
                            quitGame(oval.width());
                            return;
                        }
                    }
                    
                    size.setText("size: " + round(oval.width(),4));
                    
                }
                
            }
            else {
                canvasWidth = this.getWidth();
                canvasHeight = this.getHeight();
                initialized = true;
                
                // setup controlled oval
                float xPos = (canvasWidth-oval.width())/2;
                float yPos = (canvasHeight-oval.height())/2;
                oval.set(
                        xPos,
                        yPos,
                        xPos+oval.width(),
                        yPos+oval.height()
                );
                
                // setup first 2 other ovals
                // will be half size of controlled oval
                ovals.add(new RectF(0,0,oval.width()/2,oval.height()/2));
                ovals.add(new RectF(canvasWidth-oval.width()/2,0,canvasWidth,oval.height()/2));
                
                // setup vels for first 2 ovals
                float[][] initOvalsTraj = {{1,1},{-1,1}};
                ovalsVel.add(initOvalsTraj[0]);
                ovalsVel.add(initOvalsTraj[1]);
                
                
                System.out.println(this.getWidth() + ", " + this.getHeight());
            }
            
            // redraw on canvas
            invalidate();
            
        }

    }
    
}
