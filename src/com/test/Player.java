package com.test;

import android.graphics.RectF;


// An object representing the oval that the player controls
public class Player{

    // The object representing the player that is drawn on the screen
    public RectF oval; // Public so that main canvas can access it

    // The size of the oval. Grows as it absorbs other polygons
    private float radius;
    // The size of the oval to be displayed. May not be same as the actual radius
    private float displayRadius;
    // The current position of the center of the oval
    private float xPos, yPos;
    // The current velocity of the oval
    private float xVel, yVel;
    private final float initXVel, initYVel;

    // Mass of oval. Used to calculate change in velocity
    private final float MASS = 10.0f;
    // Oval coefficient of restitution. Used in collisions with edge
    private final float RESTITUTION = 0.4f;
    // A frictional coefficient. Allows more fine control at low speed
    private final float VISCOSITY = 0.20f;    


    public Player(float x, float y, float rad, float cWidth, float cHeight){
        xPos = x;
        yPos = y;

        xVel = 0.0f;
        yVel = 0.0f;
        initXVel = xVel;
        initYVel = yVel;

        radius = rad;
        displayRadius = rad;

        // Create the RectF object centered at x,y
        oval = new RectF(x-radius,y-radius,x+radius,y+radius);
    }


    // Changes the size of the oval according to the area of the polygon it ate
    public boolean eat(Polygon square){
        // Check if polygon is larger. If so, the oval dies
        if (square.getArea() > Math.PI*radius*radius){
            return true; // Oval is dead, return true
        }

        // Calculate area of eaten polygon
        float area = square.getArea();
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
    public void move(float[] accel, float cLeft, float cTop, float cRight, float cBottom, float scaleRatio){
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
        oval.offset(xVel/scaleRatio, yVel/scaleRatio);

        // Check if oval has hit edge of screen

        if (oval.left < cLeft){
            // Collision with left side. Place oval at left edge
            oval.offsetTo(cLeft,oval.top);
            // Reverse velocity and scale x velocity by coefficient of restitution
            xVel = -RESTITUTION*xVel;
        }

        if (oval.top < cTop) {
            // Collision with top side. Place oval at top edge
            oval.offsetTo(oval.left,cTop);
            // Reverse velocity and scale y velocity by coefficient of restitution
            yVel = -RESTITUTION*yVel;
        }
        
        if (oval.right > cRight){
            // Collision with right side. Place oval at right edge
            oval.offsetTo(cRight-oval.width(),oval.top);
            // Reverse velocity and scale x velocity by coefficient of restitution
            xVel = -RESTITUTION*yVel;
        }
        
        if (oval.bottom > cBottom){
            // Collision with bottom side. Place oval at bottom edge
            oval.offsetTo(oval.left,cBottom-oval.height());
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
    
    public float getDisplayRadius(){
        return displayRadius;
    }
    
    public void setDisplayRadius(float nextRadius){
        displayRadius = nextRadius;
    }

    // Getter and Setter methods for velocity
    public float getXVel(){
        return xVel;
    }
    
    public float getInitXVel(){
        return initXVel;
    }

    public float getYVel(){
        return yVel;
    }
    
    public float getInitYVel(){
        return initYVel;
    }

    public void setXVel(float vx){
        xVel = vx;
    }

    public void setYVel(float vy){
        yVel = vy;
    }
    
}