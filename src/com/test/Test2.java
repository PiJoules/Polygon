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
import java.util.Iterator;


// Test2 is the actual game of the app
// Test2 is an activity since it is its own screen in the app
// Test2 will be using methods in the SensorEventListener interface
// since Test2 will be using the accelerometer as a controler
public class Test2 extends Activity{
    
    // Custom view stuff
    // Create a custom view called 'mCustomDrawableView' that will display the game
    // mCustomDrawableView is a subclass created in this class to allow easier
    // management of some variables
    // The boolean variable 'paused' determines whether the game is paused or not
    private CustomDrawableView mCustomDrawableView;
    protected boolean paused = false;
    
    // This is the size of the oval that the user controls
    public float size;

    // The accelerometer object
    private Accelerometer accelSensor;

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
        accelSensor = new Accelerometer(this);
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
        // Reregisters the accelerometer event listener
        accelSensor.resume();
    }
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


    // the game view
    private class CustomDrawableView extends View {
        
        // canvas setup
        public Paint p = new Paint();
        public float canvasWidth, canvasHeight;
        public boolean initialized = false; // make sure to get canvas width/height
        
        // The object representing the player character, an oval
        private Player oval;

        // The squares that the player must avoid or eat. These are represented by the
        // Polygon object and stored in an ArrayList for ease of additions and deletions
        private ArrayList<Polygon> squares;
        // This is the maximum number of enemies that will be inclued on the canvas 
        private final int ENEMY_LIMIT = 10;

        // Debugging variables used for timing
        private long peakFrameRate = 0;
        private long minFrameRate = 1000;


        public CustomDrawableView(Context context) {
            super(context); // Calls the View super class constructor
            this.setBackgroundColor(Color.WHITE);
            
            // add circle every second
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
                
                // An iterator to move and check collisions for each square
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
