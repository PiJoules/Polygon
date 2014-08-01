package com.test;

// These are various libraries that are used

// Android imports
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.os.Bundle;
import android.os.Handler;
import android.text.InputType;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;

// Java imports
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Random;
import java.util.Iterator;


// Test2 is the object that represents and controls the game the user plays
// Test2 is an activity since it is its own screen in the app
// It uses methods in the SensorEventListener interface since it uses the
// accelerometer as a controler
public class Test2 extends Activity implements SensorEventListener{
    
    // Custom view variables
    // Create a custom view called 'mCustomDrawableView' that will display the game
    // mCustomDrawableView is a subclass created in this class to allow easier
    // management of some variables
    private CustomDrawableView mCustomDrawableView;

    // The boolean variable 'paused' determines whether the game is paused or not
    protected boolean paused = false;
    
    // This is the size of the oval that the user controls
    public float size;

    // The accelerometer object. This is used to get new accelerometer readings
    private Accelerometer accelSensor;

    // The onCreate method is what's called evertime the game launches
    // The onCreate method is a method inherited from the parent class Activity
    // Overriding it and other methods inherited from Activity will prevent java
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
        
        // Creates the accelerometer object. Passes this Activity to the constructor
        accelSensor = new Accelerometer(this,this);
    }
    
    
    // method for quiting the game
    // an alert message is displayed once the player loses
    // and the user is prompted to enter their name and
    // their score will be saved
    public void quitGame(){
        // Create a textfield where the user can enter their name
        final EditText input = new EditText(this);
        // Allow only for text to be input
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        // Set placeholder text that will disappear once the user enters something into the textbox
        input.setHint("Enter name here to save score");
        // Create a score manager object that will update the scores stored on the phone
        final ScoreManager sm = new ScoreManager(this,null);
        
        // Create the alert message GUI that the user interacts with
        new AlertDialog.Builder(this)
                // set the title of the alert message to say the user's score
                .setTitle("Max Size: " + String.format("%.2f",size))
                // Add the text field to the alert message dialog box
                .setView(input)
                // Create the save button
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener(){
                    public void onClick(DialogInterface dialog, int which){
                        // Get the text the user entered
                        String name = input.getText().toString();
                        // If the user did not enter a name, save them as "Anonymous"
                        if(name == null || name.equals("")){
                            name = "Anonymous";
                        }
                        // See if the score is high enough to add to the high scores list
                        sm.checkNewScore(name, String.format("%.2f",size));
                        // Exit alert message
                        finish();
                    }
                }).show();
    }
    
    // The method to be called when the user-reopens the app. Overrides the onResume method in
    // Activity
    @Override
    protected void onResume() {
        super.onResume();
        // Reregisters the accelerometer event listener
        accelSensor.resume();
    }

    // The method to be called when the screen is exited. Overrides the onPause method in Activity
    @Override
    protected void onPause() {
        super.onPause();
        // Stops the accelerometer event listener
        accelSensor.pause();
    }

    
    // for rounding float to max of n decimal places
    public float round(float d, int decimalPlace){
        BigDecimal bd = new BigDecimal(Float.toString(d));
        bd = bd.setScale(decimalPlace, BigDecimal.ROUND_HALF_UP);
        return bd.floatValue();
    }

    // The event handler for an accelerometer update. Calls the update method of the accelerometer
    // class.
    public void onSensorChanged(SensorEvent event) {
        accelSensor.updateAccelVals(event);
    }

    // The event handler for a change in the accelerometer settings. For this app, no action is 
    // required in this method
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        
    }


    // The game view object
    private class CustomDrawableView extends View {
        
        // Canvas objects and variables
        // The paint object colors the objects drawn on the screen
        private Paint p = new Paint();
        // The dimensions of the game screen
        private float canvasWidth, canvasHeight;
        // Boolean to check that the screen has been initialized
        private boolean initialized = false;
        
        // The object representing the player character, an oval
        private Player oval;

        // The squares that the player must avoid or eat. These are represented by the Polygon
        // object and stored in an ArrayList for ease of additions and deletions
        private ArrayList<Polygon> squares;
        // This is the maximum number of enemies that will be inclued on the canvas 
        private final int ENEMY_LIMIT = 10;

        // Debugging variables used for timing
        private long peakFrameRate = 0; // Best frame rate attained
        private long minFrameRate = 1000; // Worst frame rate attained

        // The object constructor
        public CustomDrawableView(Context context) {
            super(context); // Calls the View super class constructor
            
            // Set background color
            this.setBackgroundColor(Color.WHITE);
            
            // Spawn a new thread to add a circle every second
            final Handler h = new Handler();
            new Thread(new Runnable(){
                public void run() {
                    while (true){
                        try{
                            Thread.sleep(1000);
                            h.post(new Runnable(){
                                public void run(){
                                    // Spawns a new enemy if there are less than 10 on canvas
                                    if (squares.size() < ENEMY_LIMIT){
                                        Random r = new Random();
                                        // Assigns the new square a width between 50% and 150% of oval's width
                                        float nextWidth = r.nextFloat()*oval.getRadius()*2+oval.getRadius();
                                        // Picks a random corner to spawn the new square
                                        int nextCorner = r.nextInt(4);
                                        // Adds the new square to the ArrayList of enemies. Assigns it a random
                                        // x and y velocity between .5 and 2.5
                                        squares.add(new Polygon(nextWidth, nextCorner, r.nextFloat()*2 + 0.5f, r.nextFloat()*2 + 0.5f, canvasWidth, canvasHeight));
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
        
        // This is the method that is repeatedly called to redraw the game screen. It controls the
        // overall flow of the game by moving the objects and removing them as necessary.
        // Overrides the onDraw method of the view class
        @Override
        public void onDraw(Canvas canvas) {
            // Records the start time of this iteration of the function. For debugging purposes
            long startTime = System.currentTimeMillis();

            if (initialized){
                
                if (!paused){
                    // Calculate new speed and position of player and move player
                    // Handled in player class
                    oval.move(accelSensor.getAccelFiltered());
                }
                
                p.setColor(Color.BLACK);
                canvas.drawOval(oval.oval, p);
                
                // An iterator to go through the squares to move them and check collisions
                Iterator<Polygon> iter = squares.iterator();
                while(iter.hasNext()){
                    Polygon square = iter.next();
                
                    if (!paused){
                        // Call each enemy's move method. Returns whether or not polygon should be removed
                        boolean shouldRemove = square.move();

                        // Check if the oval is intersecting the square. Returns the result of collission checking
                        boolean collided = square.checkCollisions(oval.getX(), oval.getY(), oval.getRadius());

                        // Polygon has stayed past collision limiy. Only remove immediately if it
                        // hasn't also collided with the oval
                        if(shouldRemove && !collided){
                            // Polygon has stayed past collision limit, should be removed
                            iter.remove();
                            continue; // Continue to next square as this one will not be redrawn
                        }

                        if(collided){
                            // Pass polygon to oval's eat method. If square is larger, the game ends. Other wise,
                            // a fraction of its area is added to the oval's area
                            boolean dead = oval.eat(square);
                            
                            // Calculate new diameter of the oval. This will be the final score if player dies
                            size = 2*round(oval.getRadius(),2);

                            if(dead){
                                // You got eaten, you lose. Sorry about that.
                                quitGame(); // Record score
                                return; // Break out of drawing loop
                            }

                            // Dead was false, so square was eaten. Remove it from list
                            iter.remove();
                            continue; // Continue to next square as this one will not be redrawn
                        }

                    }

                    // Set color of polygon to green
                    p.setColor(Color.GREEN);
                    // Draw the polygon
                    canvas.drawRect(square.shape, p);
                    
                }
                
            }
            else {

                // Save dimensions of canvas
                canvasWidth = this.getWidth();
                canvasHeight = this.getHeight();
                initialized = true;
                
                // Setup controlled oval
                // Place an oval of radius 5 at center of screen
                size = 5.0f;
                oval = new Player(canvasWidth/2, canvasHeight/2, size, canvasWidth, canvasHeight);

                // Initialize squares list
                squares = new ArrayList<Polygon>();

            }
            
            // redraw on canvas
            invalidate();
            

            // Debugging code. Calculates current frame rate from execution time of onDraw()
            long currentFrameRate = 1000/Math.max(1,(System.currentTimeMillis() - startTime)); // Prevent divide by zero
            
            // Determine if frame rate exceeds slowest or fastest rates observed
            if(currentFrameRate < minFrameRate){
                minFrameRate = currentFrameRate;
            }
            if(currentFrameRate > peakFrameRate){
                peakFrameRate = currentFrameRate;
            }

            // Output debugging text to screen
            canvas.drawText("Frame Rate: " + Long.toString(currentFrameRate) + "fps", 10, 10, p);
            canvas.drawText("Peak Rate: " + Long.toString(peakFrameRate) + "fps", 10, 20, p);
            canvas.drawText("Min Rate: " + Long.toString(minFrameRate) + "fps", 10, 30, p);
        }

    }
    
}
