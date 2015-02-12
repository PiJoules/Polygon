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
import android.media.MediaPlayer;
import android.media.AudioManager;
import android.os.Bundle;
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


// GameActivity is the object that represents and controls the game the user plays
// GameActivity is an activity since it is its own screen in the app
// It uses methods in the SensorEventListener interface since it uses the
// accelerometer as a controler
public class GameActivity extends Activity implements SensorEventListener{
    
    private boolean pramod = false;
    
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
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE);
                    
        // setup the custom canvas
        mCustomDrawableView = new CustomDrawableView(this); // set the custom view that contains the game
        
        Bundle extras = getIntent().getExtras();
        if (extras != null) pramod = extras.getBoolean("pramod");
        
        // Add an onClickListener to the custom view so that
        // whenever the user taps on the screen, the game will pause
        mCustomDrawableView.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                paused = !paused;
                // If user is pausing game, display pause menu using Alert Dialog gui
                if(paused){
                    new AlertDialog.Builder(getContext())
                        // Set title
                        .setTitle("Paused")
                        // Create a calibration button to set current position as zero position
                        .setPositiveButton("Calibrate", new DialogInterface.OnClickListener(){
                            public void onClick(DialogInterface dialog, int id) {
                                accelSensor.calibrate();
                                paused = false;
                            }
                        })
                        // Create a button to reset to default calibration
                        .setNeutralButton("Reset Calibation", new DialogInterface.OnClickListener(){
                            public void onClick(DialogInterface dialog, int which) {
                                accelSensor.zero();
                                paused = false;
                            }
                        })
                        // Create exit button
                        .setNegativeButton("Resume", new DialogInterface.OnClickListener(){
                            public void onClick(DialogInterface dialog, int which) {
                                paused = false;
                            }
                        })
                        .show();
                }
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
        final ScoreManager sm = new ScoreManager(this);
        
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
                        String name = input.getText().toString().replace('#', '*');
                        if (name.length() > 255) name = name.substring(0, 255);
                        // If the user did not enter a name, save them as "Anonymous"
                        if(name == null || name.equals("")){
                            name = "Anonymous";
                        }
                        // See if the score is high enough to add to the high scores list
                        sm.checkNewScore(name, String.format("%.2f",size));
                        // Exit alert message
                        finish();
                    }
                })
                .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                })
                .show();
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

    public Context getContext(){
        return this;
    }


    // The game view object
    private class CustomDrawableView extends View {
        
        // Canvas objects and variables
        // The paint object colors the objects drawn on the screen
        private final Paint p = new Paint();
        // The dimensions of the game screen
        private float canvasWidth, canvasHeight;
        // Boolean to check that the screen has been initialized
        private boolean initialized = false;
        //private boolean scaling = false;
        
        // The object representing the player character, an oval
        private Player oval;

        // The polygons that the player must avoid or eat. These are represented by the Polygon
        // object and stored in an ArrayList for ease of additions and deletions
        private final ArrayList<Polygon> polygons;
        // This is the maximum number of enemies that will be inclued on the canvas 
        private CanvasInstructions ci;

        // To keep the time between each movement constant, we use a frame rate so that the screen
        // will only redraw itself at set intervals and sleep if it completes drawing before the 
        // time is up in an iteration
        private final long FRAME_PERIOD = 1000/40;
        private long currentTime = System.currentTimeMillis();
        private long nextReDrawTime = currentTime + FRAME_PERIOD;
        private long nextAddition = System.currentTimeMillis() + 1000;
        private final float defaultTextSize;
        private final MediaPlayer mp = MediaPlayer.create(getApplicationContext(), R.raw.blop);

        // The object constructors
        public CustomDrawableView(Context context) {
            super(context); // Calls the View super class constructor
            
            // Set background color
            this.setBackgroundColor(Color.WHITE);

            // Initialize polygons list
            polygons = new ArrayList<Polygon>();
            
            // set color of the oval
            p.setColor(Color.BLACK);
            defaultTextSize = p.getTextSize();
            
            AudioManager audio = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
            mp.setVolume(audio.getStreamVolume(AudioManager.STREAM_MUSIC), audio.getStreamVolume(AudioManager.STREAM_MUSIC));
        }
        
        // This is the method that is repeatedly called to redraw the game screen. It controls the
        // overall flow of the game by moving the objects and removing them as necessary.
        // Overrides the onDraw method of the view class
        @Override
        public void onDraw(Canvas canvas) {

            if (initialized){
                
                ci.updateInstructions(size);
                boolean shouldScale = ci.shouldScale();
                if (!shouldScale){
                    canvas.scale(ci.getScaleRatio(), ci.getScaleRatio(), ci.getCanvasWidth()/2, ci.getCanvasHeight()/2);
                }
                else {
                    canvas.scale(ci.getLastScaleRatio(), ci.getLastScaleRatio(), ci.getCanvasWidth()/2, ci.getCanvasHeight()/2);
                }
                for (int i = ci.getCanvasdx().size()-1; i >= 0; i--){
                    p.setColor(ci.getColors()[i]);
                    canvas.drawRect(-ci.getCanvasdx().get(i),
                                    -ci.getCanvasdy().get(i), 
                                    ci.getCanvasWidth()+ci.getCanvasdx().get(i),
                                    ci.getCanvasHeight()+ci.getCanvasdy().get(i), p);
                }
                
                if (!paused && !shouldScale){
                    
                    // Calculate new speed and position of player and move player
                    // Handled in player class
                    oval.move(accelSensor.getAccel(),
                              ci.getCanvasBounds()[0],
                              ci.getCanvasBounds()[1],
                              ci.getCanvasBounds()[2], 
                              ci.getCanvasBounds()[3],
                              ci.getScaleRatio());
                    
                    // add another polygon
                    if (polygons.size() < ci.getEnemyCount() && System.currentTimeMillis() > nextAddition){
                        nextAddition = System.currentTimeMillis() + 1000;
                        Random r = new Random();
                        // Picks a random corner to spawn the new polygon
                        int nextCorner = r.nextInt(4);
                        // Adds the new polygon to the ArrayList of enemies. Assigns it a random
                        // x and y velocity between .5 and 2.5, and increase speed to keep up with the screen scaling
                        polygons.add(new Polygon(ci.getEnemyRadius(oval.getRadius()),
                                                 nextCorner,
                                                 (r.nextFloat()*2 + 0.5f)/ci.getScaleRatio(),
                                                 (r.nextFloat()*2 + 0.5f)/ci.getScaleRatio(),
                                                 ci.getCanvasBounds(),
                                                 ci.getEnemySides()
                        ));
                    }
                }
                
                // draw the oval on the canvas
                p.setColor(Color.BLACK);
                if (!pramod){
                    canvas.drawCircle(oval.getX(),oval.getY(),oval.getRadius(),p);
                }
                else {
                    canvas.drawBitmap(oval.b, oval.getX(), oval.getY(), p);
                }
                
                // An iterator to go through the polygons to move them and check collisions
                Iterator<Polygon> iter = polygons.iterator();
                while(iter.hasNext()){
                    Polygon polygon = iter.next();
                
                    // move polygon if not paused
                    if (!paused && !shouldScale){
                        // Call each enemy's move method. Returns whether or not polygon should be removed
                        boolean shouldRemove = polygon.move();

                        // Check if the oval is intersecting the polygon. Returns the result of collission checking
                        boolean collided = polygon.checkCollisions(oval.getX(), oval.getY(), oval.getRadius());

                        // Polygon has stayed past collision limiy. Only remove immediately if it
                        // hasn't also collided with the oval
                        if(shouldRemove && !collided){
                            // Polygon has stayed past collision limit, should be removed
                            iter.remove();
                            continue; // Continue to next polygon as this one will not be redrawn
                        }

                        if(collided){
                            
                            mp.start();
                            
                            // Pass polygon to oval's eat method. If polygon is larger, the game ends. Other wise,
                            // a fraction of its area is added to the oval's area
                            boolean dead = oval.eat(polygon);
                            
                            // Calculate new diameter of the oval. This will be the final score if player dies
                            size = 2*round(oval.getRadius(),2);

                            if(dead){
                                // You got eaten, you lose. Sorry about that.
                                quitGame(); // Record score
                                return; // Break out of drawing loop
                            }

                            // Dead was false, so polygon was eaten. Remove it from list
                            iter.remove();
                            continue; // Continue to next polygon as this one will not be redrawn
                        }

                    }

                    // Draw the polygon
                    canvas.drawPath(polygon, polygon.p);
                    
                } // end of Polygon iteration
                
            }
            else {
                // Save dimensions of canvas
                canvasWidth = this.getWidth();
                canvasHeight = this.getHeight();
                
                initialized = true;
                
                // Setup controlled oval
                // Place an oval of radius 5 at center of screen
                size = 10.0f;
                oval = new Player(canvasWidth/2, canvasHeight/2, size/2, canvasWidth, canvasHeight, getContext());
                
                ci = new CanvasInstructions(size, 0, 0, canvasWidth, canvasHeight);
            }
    

            // Sleep until next frame
            currentTime = System.currentTimeMillis();
            try{
                Thread.sleep(Math.max(0,nextReDrawTime - currentTime));
            }
            catch(InterruptedException e){
                return;
            }

            // draw text on canvas
            p.setTextAlign(Paint.Align.RIGHT);
            p.setTextSize(3*defaultTextSize/ci.getLastScaleRatio());
            canvas.drawText(String.format("%.2f", size), canvasWidth-10 + ci.getScalingCanvasdx(), 5+p.getTextSize() - ci.getScalingCanvasdy(), p);
            
            // Calculate end of next redraw period
            nextReDrawTime = currentTime + FRAME_PERIOD;

            // redraw on canvas
            invalidate();
        }

    }

}
