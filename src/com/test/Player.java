package com.test;

import android.graphics.RectF;


// An object representing the oval that the player controls
public class Player{

    // The object representing the player that is drawn on the screen
    public RectF oval; // Public so that main canvas can access it

    // The size of the oval. Grows as it absorbs other polygons
    private float radius;
    // The current position of the center of the oval
    private float xPos, yPos;
    // The current velocity of the oval
    private float xVel, yVel;
    // The dimensions of the game screen
    private final float canvasWidth, canvasHeight;

    // Mass of oval. Used to calculate change in velocity
    private final float MASS = 10.0f;
    // Oval coefficient of restitution. Used in collisions with edge
    private final float RESTITUTION = 0.4f;
    // A frictional coefficient. Allows more fine control at low speed
    private final float VISCOSITY = 0.20f;    


    // Class constructor. Creates a circle of radius rad at (x,y)
    public Player(float x, float y, float rad, float cWidth, float cHeight){
        xPos = x;
        yPos = y;

        xVel = 0.0f;
        yVel = 0.0f;

        radius = rad;

        // Create the RectF object centered at x,y
        oval = new RectF(x-radius,y-radius,x+radius,y+radius);

        canvasWidth = cWidth;
        canvasHeight = cHeight;
    }


    // Changes the size of the oval according to the area of the polygon it ate
    public boolean eat(Polygon square){
        // Check if polygon is larger. If so, the oval dies
        if(square.getLength() > 2.0f*radius){
            return true; // Oval is dead, return true
        }

        // Calculate area of eaten square
        float area = square.getLength()*square.getLength();
        // Calculate new area by adding one tenth the area of eaten polygon
        float newArea = area/10.0f + (float) Math.PI*radius*radius;
        // Calculate new radius from new area
        float oldRadius = radius;
        radius = (float) Math.sqrt(newArea/Math.PI);

        // Compute new bounds of oval
        float radiusChange = radius - oldRadius; 
        oval.set(oval.left-radiusChange, 
                 oval.top-radiusChange, 
                 oval.right+radiusChange, 
                 oval.bottom+radiusChange);

        return false; // Oval is alive, return false
    }


    // A function to move the oval at each timestep using accelerometer input contained in accel
    public void move(float[] accel){
        // Calculate new velocity and position of oval

        // Calculate current velocity for determination of frictional force
        float netVel = (float) Math.sqrt(xVel*xVel + yVel*yVel);
        
        // Calculate frictional force
        float frictionX, frictionY;
        if(netVel == 0){
            // Prevent divide by zero
            frictionX = 0f;
            frictionY = 0f;
        }
        else{
            // Friction is -velocity^2 scaled by viscosity coefficient
            frictionX = -VISCOSITY*xVel*netVel;
            frictionY = -VISCOSITY*yVel*netVel;
        }

        // Compute new velocity by adding frictional force and accelerometer readings as another force
        xVel = xVel + (accel[0] + frictionX)/MASS;
        yVel = yVel + (accel[1] + frictionY)/MASS;
        

        // Set new position by moving oval in direction of its velocity
        oval.offset(xVel, yVel);

        // Check if oval has hit edge of screen

        if (oval.left < 0){
            // Collision with left side. Place oval at left edge
            oval.offsetTo(0,oval.top);
            // Reverse velocity and scale x velocity by coefficient of restitution
            xVel = -RESTITUTION*xVel;
        }

        if (oval.top < 0) {
            // Collision with top side. Place oval at top edge
            oval.offsetTo(oval.left,0);
            // Reverse velocity and scale y velocity by coefficient of restitution
            yVel = -RESTITUTION*yVel;
        }
        
        if (oval.right > canvasWidth){
            // Collision with right side. Place oval at right edge
            oval.offsetTo(canvasWidth-oval.width(),oval.top);
            // Reverse velocity and scale x velocity by coefficient of restitution
            xVel = -RESTITUTION*yVel;
        }
        
        if (oval.bottom > canvasHeight){
            // Collision with bottom side. Place oval at bottom edge
            oval.offsetTo(oval.left,canvasHeight-oval.height());
            // Reverse velocity and scale y velocity by coefficient of restitution
            yVel = -RESTITUTION*yVel;
        }

        // Update xPos and yPos
        this.updatePosition();

    }


    // Update position method for each time the oval moves
    public void updatePosition(){
        xPos = oval.centerX();
        yPos = oval.centerY();
    }


    // Getter methods for position
    public float getX(){
        return xPos;
    }

    public float getY(){
        return yPos;
    }

    // Getter method for oval radius
    public float getRadius(){
        return radius;
    }

    // Getter and Setter methods for velocity
    public float getXVel(){
        return xVel;
    }

    public float getYVel(){
        return yVel;
    }

    public void setXVel(float vx){
        xVel = vx;
    }

    public void setYVel(float vy){
        yVel = vy;
    }
}