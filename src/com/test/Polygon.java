package com.test;


import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;


// An object representing the shapes that the oval must dodge
// Polgon is a path that can be drawn as n-sided polygons
public class Polygon extends Path{

    // Number of collisions with the edge of the screen before removing the polygon
    private static final int COLLISION_LIMIT = 20;

    // A variable to track how many collisions have occured
    private int collisions;

    // The dimensions of the game screen
    private final float canvasWidth, canvasHeight;

    // The length of an edge of the polygon (only squares right now)
    private final float length;
    
    // The length of the polygon to be displayed on the canvas
    // May not be the same as the actual length
    private float displayLength;

    // The position of the polygon's center
    private float xPos, yPos;

    // The polygon's velocity (will never change)
    private float xVel, yVel;
    
    // The polygon'sto, right, bottom, and left post points
    private float top,right,bottom,left;
    
    // number of sides of polygon
    private final int sides;
    
    private int angle = 0;
    
    // paint of the polygon
    public final Paint p = new Paint();

    // Constructor. Spawns a square of side length len in a random corner with a velocity (vx, vy)
    public Polygon(float len, int corner, float vx, float vy, float cWidth, float cHeight, int sides){
        
        // Save the dimensions of the canvas for use in other methods
        canvasWidth = cWidth;
        canvasHeight = cHeight;

        // Side length
        length = len;
        displayLength = len;

        // Initialize collisions counter
        collisions = 0;
        
        this.sides = sides;

        // Set initial position and velocity based on index of corner provided
        if(corner == 0){
            // Spawn polygon in upper left hand corner
            xPos = len/2.0f;
            yPos = len/2.0f;
            xVel = vx;
            yVel = vy;
        }
        else if(corner == 1){
            // Spawn polygon in upper right hand corner
            xPos = cWidth - len/2.0f;
            yPos = len/2.0f;
            xVel = -vx;
            yVel = vy;
        }
        else if(corner == 2){
            // Spawn polygon in lower left hand corner
            xPos = len/2.0f;
            yPos = cHeight - len/2.0f;
            xVel = vx;
            yVel = -vy;
        }
        else if(corner == 3){
            // Spawn polygon in lower right hand corner
            xPos = cWidth - len/2.0f;
            yPos = cHeight - len/2.0f;
            xVel = -vx;
            yVel = -vy;
        }
        
        /*left = xPos-len/2f;
        top = yPos-len/2f;
        right = xPos+len/2f;
        bottom = yPos+len/2f;*/
        setBounds(len/2f);

        // Creates the new polygon in its corner
        //shape = new RectF(xPos-len/2.0f, yPos-len/2.0f, xPos+len/2.0f, yPos+len/2.0f);
        setPath(sides,0,length);
        setColor(sides);
    }
    
    // depends on number of sides, the current angle,
    // current position of the polygon, and the radius of the polygon
    public void setPath(int n, int angle, float length){
        rewind(); // clear all points from the path
        moveTo(
                (float) (xPos+length*Math.cos(angle*Math.PI/180)),
                (float) (yPos+length*Math.sin(angle*Math.PI/180)));
        for (int i = 1; i < n; i++){
            float nextX = (float) (xPos+length*Math.cos(2*Math.PI*i/n + angle*Math.PI/180));
            float nextY = (float) (yPos+length*Math.sin(2*Math.PI*i/n + angle*Math.PI/180));
            lineTo(nextX, nextY);
        }
        lineTo(
                (float) (xPos+length*Math.cos(angle*Math.PI/180)),
                (float) (yPos+length*Math.sin(angle*Math.PI/180)));
    }
    
    public void setBounds(float radius){
        left = xPos-radius;
        right = xPos+radius;
        top = yPos-radius;
        bottom = yPos+radius;
    }
    
    private void setColor(int n){
        if (n == 3) p.setColor(Color.RED);
        else if (n == 4) p.setARGB(255, 255, 165, 0);
        else if (n == 5) p.setColor(Color.GREEN);
        else if (n == 6) p.setColor(Color.BLUE);
        else p.setARGB(255, 128, 0, 128);
    }
    
    // update the position and boundaries of the polygon after moving
    private void translate(float dx, float dy){
        angle %= 360;
        setPath(sides,angle++,length);
        setColor(sides);
        offset(dx,dy);
        xPos += dx;
        yPos += dy;
        left += dx;
        right += dx;
        top += dy;
        bottom += dy;
    }

    // Moves the polygon, checks for collisions with the side of the screen, removes polgyon
    // if it has exceeded edge collision limit
    public boolean move(){
        // Moves the polygon
        translate(xVel, yVel);

        // Check for edge collisions

        if (left < 0){
            // Collision with left side. Place shape at left edge
            translate(Math.abs(left), 0);
            
            // Reverse velocity
            xVel = -xVel;

            // Increment collisions counter
            collisions++;
        }

        if (top < 0) {
            // Collision with top side. Place shape at top edge
            translate(0, Math.abs(top));
            
            // Reverse velocity
            yVel = -yVel;

            // Increment collisions counter
            collisions++;
        }
        
        if (right > canvasWidth){
            // Collision with right side. Place shape at right edge
            translate(-Math.abs(canvasWidth-right),0);
            
            // Reverse velocity
            xVel = -xVel;

            // Increment collisions counter
            collisions++;
        }
        
        if (bottom > canvasHeight){
            // Collision with bottom side. Place shape at bottom edge
            translate(0, -Math.abs(canvasHeight-bottom));
            
            // Reverse velocity 
            yVel = -yVel;

            // Increment collisions counter
            collisions++;
        }            
        
        // remove the polygon if collision limit is exceeded
        // keep otherwise
        return collisions > Polygon.COLLISION_LIMIT;
    }
    

    // Check if the shape has collided with this polygon
    public boolean checkCollisions(float oval_x, float oval_y, float oval_radius){
        // Simplified collision checking for efficiency. Squares modeled as circles with
        // diameters equal to the square's length. Find distance between centers
        float dx = xPos - oval_x;
        float dy = yPos - oval_y;
        float dist = (float) Math.sqrt(dx*dx + dy*dy);
        
        // should remove if the objects collide
        // keep otherwise
        return dist < oval_radius+length/2.0f;
    }

    // Getter method for side length
    public float getLength(){
        return length;
    }
    
    public float getDisplayLength(){
        return displayLength;
    }
    
    public void setDisplayLength(float nextLength){
        displayLength = nextLength;
    }
    
    public int getAngle(){
        return angle;
    }

    // Getter methods for location variables
    public float getX(){
        return xPos;
    }
    
    public float getY(){
        return yPos;
    }
    
    public int getSides(){
        return sides;
    }
}

