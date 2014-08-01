package com.test;


import android.graphics.RectF;


// An object representing the shapes that the oval must dodge
public class Polygon{

    // Number of collisions with the edge of the screen before removing the polygon
    private static final int COLLISION_LIMIT = 20;

    // A variable to track how many collisions have occured
    private int collisions;

    // The dimensions of the game screen
    private final float canvasWidth, canvasHeight;

    // The length of an edge of the polygon (only squares right now)
    private float length;

    // The position of the polygon's top left corner
    private float xPos, yPos;

    // The polygon's velocity (will never change)
    private float xVel, yVel;

    // The RectF object representing the polygon that is drawn on the canvas
    public RectF shape; // public so main canvas can access it


    // Constructor. Spawns a square of side length len in a random corner with a velocity (vx, vy)
    public Polygon(float len, int corner, float vx, float vy, float cWidth, float cHeight){
        
        // Save the dimensions of the canvas for use in other methods
        canvasWidth = cWidth;
        canvasHeight = cHeight;

        // Side length
        length = len;

        // Initialize collisions counter
        collisions = 0;

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
            yPos = cHeight - len/2/0f;
            xVel = -vx;
            yVel = -vy;
        }

        // Creates the new polygon in its corner
        shape = new RectF(xPos-len/2.0f, yPos-len/2.0f, xPos+len/2.0f, yPos+len/2.0f);
    }

    // Moves the polygon, checks for collisions with the side of the screen, removes polgyon
    // if it has exceeded edge collision limit
    public boolean move(){
        // Moves the polygon
        shape.offset(xVel, yVel);

        // Check for edge collisions

        if (shape.left < 0){
            // Collision with left side. Place shape at left edge
            shape.offsetTo(0,shape.top);
            // Reverse velocity
            xVel = -xVel;

            // Increment collisions counter
            collisions++;
        }

        if (shape.top < 0) {
            // Collision with top side. Place shape at top edge
            shape.offsetTo(shape.left,0);
            // Reverse velocity
            yVel = -yVel;

            // Increment collisions counter
            collisions++;
        }
        
        if (shape.right > canvasWidth){
            // Collision with right side. Place shape at right edge
            shape.offsetTo(canvasWidth-shape.width(),shape.top);
            // Reverse velocity
            xVel = -xVel;

            // Increment collisions counter
            collisions++;
        }
        
        if (shape.bottom > canvasHeight){
            // Collision with bottom side. Place shape at bottom edge
            shape.offsetTo(shape.left,canvasHeight-shape.height());
            // Reverse velocity 
            yVel = -yVel;

            // Increment collisions counter
            collisions++;
        }            


        // Check if the polygon has exceeded collision limit and should be removed
        if(collisions > Polygon.COLLISION_LIMIT){
            // Signals main loop that polygon should be removed
            return true;
        }

        // Update xPos and yPos
        this.updatePosition();

        // Polygon should not be removed
        return false;
    }

    // Check if the shape has collided with this polygon
    public boolean checkCollisions(float oval_x, float oval_y, float oval_radius){
        // Simplified collision checking for efficiency. Squares modeled as circles with
        // diameters equal to the square's length. Find distance between centers
        float dx = xPos - oval_x;
        float dy = yPos - oval_y;
        float dist = (float) Math.sqrt(dx*dx + dy*dy);
        
        // Check if oval and square intersected (distance between centers < radius oval +radius square)
        if(dist < oval_radius+length/2.0f){
            // Intersection, return true
            return true;
        }

        // No intersection, return false
        return false;
    }

    // Update position method for each time the polygon moves
    public void updatePosition(){
        xPos = shape.centerX();
        yPos = shape.centerY();
    }

    // Getter method for side length
    public float getLength(){
        return length;
    }

    // Getter methods for location variables
    public float getX(){
        return xPos;
    }
    
    public float getY(){
        return yPos;
    }
}

