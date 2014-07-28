package com.test;

// these are various classes that are used
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
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Random;


// Test2 is the actual game of the app
// Test2 is an activity since it is its own screen in the app
// Test2 will be using methods in the SensorEventListener interface
// since Test2 will be using the accelerometer as a controler
public class Test2 extends Activity implements SensorEventListener{
    
    // Custom view stuff
    // Create a custom view called 'mCustomDrawableView' that will display the game
    // mCustomDrawableView is a subclass created in this class to allow easier
    // management of some variables
    // The boolean variable 'paused' determines whether the game is paused or not
    private CustomDrawableView mCustomDrawableView;
    protected boolean paused = false;
    
    // Accelerometer stuff
    // The x,y,and z accelerometer data are saved as 'xAccel','yAccel',and 'zAccel'
    // The accelerometer returns these as floats
    // The SensorManager 'mSensorManager' is required to enable and register sensors in the phone
    // The Sensor 'mAccelerometer' will be the accelerometer in the phone
    public float xAccel, yAccel, zAccel;
    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    
    // This is the size of the oval that the user controls
    public float size;

    // The onCreate method is what's called evertime the game launches
    // The onCreate method is a method inherited from the parent class Activity
    // Overriding it and other methods inherited from Activity will prevent
    // from calling the same method in the parent class
    // This is the same for methods implemented from the SensorEventListener
    // interface also
    // The Bundle allows for data to be transferred between activities
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState); // call the parent class' onCreate method
        
        // hide the title bar
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        
        // hide the notification bar
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        
        // lock the view to a vertical portrait orientation
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        
        // Set the current view that will be displayed on the phone
        // to the layout created in the main.xml file located in the
        // Resources/layout directory
        //setContentView(R.layout.main); // may not need this since the contentview is set to mCustomDrawableView later
        
        // initialize the accelerometer objects
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE); // enable the SensorManager
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER); // set the Sensor to the phone's accelerometer
        mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL); // have the SensorManager register and start gethering accelerometer data
        
        // setup the custom canvas
        mCustomDrawableView = new CustomDrawableView(this); // set the custom view that contains the game
        
        // Add an onClickListener to the custom view so that
        // whenever the user taps on the screen, the game will pause
        mCustomDrawableView.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                paused = !paused;
            }
        });
        setContentView(mCustomDrawableView); // finally set the view of the game as the custom view
        
        // message to print once the onCreate method finishes
        // and the game is done loading
        System.out.println("Loaded Game");
    }
    
    
    // method for quiting the game
    // an alert message is displayed once the player loses
    // and the user is prompted to enter their name and
    // their score will be saved
    public void quitGame(){
        final EditText input = new EditText(this); // create a textfield where the user can enter their name
        input.setInputType(InputType.TYPE_CLASS_TEXT); // allow only for text to be input
        input.setHint("Enter name here to save score"); // set a placeholder that will disappear once the user enters text into the textbox
        final ScoreManager sm = new ScoreManager(this); // the create a score manager object that will update the scores stroed on the phone
        new AlertDialog.Builder(this) // create the alert message
                .setTitle("Max Size: " + String.format("%.2f",size)) // set the title of the alert message to say the user's score
                .setView(input) // add the text field to the alert message dialog box
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener(){
                    public void onClick(DialogInterface dialog, int which){
                        String name = input.getText().toString();
                        sm.checkNewScore(name==null||"".equals(name)?"Anonymous":name, String.format("%.2f",size));
                        finish();
                    }
                }).show();
    }
    
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
        // Calculate net acceleration
        float resultant = (float) Math.sqrt(event.values[0]*event.values[0] +
                                            event.values[1]*event.values[1] + 
                                            event.values[2]*event.values[2]);
        
        // Get accleration components. Scale by 9.81/resultant so net acceleration is 1g
        xAccel = -event.values[0]/resultant*9.81f; // for xAccel, tilting right is negative, so take opposite
        yAccel = event.values[1]/resultant*9.81f;
        zAccel = event.values[2]/resultant*9.81f;    
    }

    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    
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
        
    
        // The object representing the player character, an oval
        public Player oval;

        // other ovals
        public ArrayList<RectF> squares = new ArrayList<RectF>();
        public ArrayList<float[]> squareVels = new ArrayList<float[]>(); // [xVel,yVel]

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
                                    if (squares.size() < 10){ // limit of 10 ovals on canvas
                                        Random r = new Random();
                                        float nextWidth = r.nextFloat()*oval.getSize()*2+oval.getSize(); // between 50% and 150% of current width
                                        float left = Math.round(r.nextFloat())*(canvasWidth-nextWidth);
                                        float top = Math.round(r.nextFloat())*(canvasHeight-nextWidth);
                                        float[] nextOvalsVel = {r.nextFloat()*2+(float)0.5,r.nextFloat()*2+(float)0.5};
                                        squares.add(new RectF(left,top,left+nextWidth,top+nextWidth));
                                        squareVels.add(nextOvalsVel);
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
                
                if (!paused){
                    // Calculate new speed and position of player and move player
                    // Handled in player class
                    oval.move(xAccel, yAccel);                    
                }
                
                p.setColor(Color.BLACK);
                canvas.drawOval(oval.oval, p);
                
                // set values for other ovals
                for (int i = 0; i < squares.size(); i++){
                    
                    if (!paused){
                        // set bounds for other ovals
                        squares.get(i).offset(squareVels.get(i)[0],squareVels.get(i)[1]);
                        if ((squares.get(i).left < 0
                                || squares.get(i).top < 0
                                || squares.get(i).right > canvasWidth
                                || squares.get(i).bottom > canvasHeight)
                                && squares.size() >= 10){
                            squares.remove(i);
                            squareVels.remove(i);
                            continue;
                        }
                        if (squares.get(i).left < 0){
                            squares.get(i).offsetTo(0,squares.get(i).top);
                            squareVels.get(i)[0] = Math.abs(squareVels.get(i)[0]);
                        }
                        if (squares.get(i).top < 0){
                            squares.get(i).offsetTo(squares.get(i).left,0);
                            squareVels.get(i)[1] = Math.abs(squareVels.get(i)[1]);
                        }
                        if (squares.get(i).right > canvasWidth){
                            squares.get(i).offsetTo(canvasWidth-squares.get(i).width(),squares.get(i).top);
                            squareVels.get(i)[0] = -Math.abs(squareVels.get(i)[0]);
                        }
                        if (squares.get(i).bottom > canvasHeight){
                            squares.get(i).offsetTo(squares.get(i).left,canvasHeight-squares.get(i).height());
                            squareVels.get(i)[1] = -Math.abs(squareVels.get(i)[1]);
                        }
                    }

                    p.setColor(Color.GREEN);
                    canvas.drawRect(squares.get(i), p); // the rectangle will be drawn as a rectangle
                    
                    if (!paused){
                        // initially used intersect method, though was not very accurate for ovals
                        double d = Math.sqrt(Math.pow(oval.oval.centerX()-squares.get(i).centerX(),2)+Math.pow(oval.oval.centerY()-squares.get(i).centerY(),2));
                        if (d < oval.getSize() + squares.get(i).width()/2){
                            if (squares.get(i).width() <= oval.getSize()*2){
                                // Calculate area of eaten square. Oval grows by that area
                                oval.eat(squares.get(i).width()*squares.get(i).width());

                                // Remove eaten polygon
                                squares.remove(i);
                                squareVels.remove(i);
                            }
                            else{
                                // You were eaten by a larger polygon. You lose
                                quitGame();
                                return;
                            }
                        }
                    }
                    
                    // Calculate new radius of the oval. This will be the final score if player dies
                    size = round(oval.getSize(),2);
                    
                }
                
            }
            else {
                canvasWidth = this.getWidth();
                canvasHeight = this.getHeight();
                initialized = true;
                
                // Setup controlled oval
                // Places oval of radius 5 at center of screen
                oval = new Player(canvasWidth/2, canvasHeight/2, 5.0f, canvasWidth, canvasHeight);

                
                // setup first 2 enemy polygons
                // will be half size of controlled oval
                squares.add(new RectF(0,0,oval.getSize(), oval.getSize()));
                squares.add(new RectF(canvasWidth-oval.getSize(),0,canvasWidth,oval.getSize()));
                
                // setup vels for first 2 enemies
                float[][] initOvalsTraj = {{1,1},{-1,1}};
                squareVels.add(initOvalsTraj[0]);
                squareVels.add(initOvalsTraj[1]);
            }
            
            // redraw on canvas
            invalidate();
            
        }

    }
    
}
