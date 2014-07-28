package com.test;

// An object representing the shapes that the oval must dodge
public class Polygon{
	// The length of an edge of the polygon (only squares right now)
	private int length;

	// The polygon's position
	private int x,y;

	// The polygon's velocity (will never change)
	private int xVel, yVel;

	// Constructor
	public Polygon(int length, int xPos, int yPos, int vx, int vy){
		// NEED TO ACTUALLY DRAW POLYGON

		x = xPos;  // Initial X Position
		y = yPos;  // Initial Y Position
		xVel = vx; // Initial X Velocity
		yVel = vy; // Initial Y Velocity
	}

	// TODO: In the future this would be a great method to use for moving the polygon
	public void move(){

	}

	// Check if the oval has collided with this polygon
	public boolean detectCollisions(int radius, int xPos, int yPos){
		// TODO: detect collisions. Ideally better than we currently are
	}

	// Getter and setter methods for location variables
	private int getX(){
		return x;
	}
	
	private int getY(){
		return y;
	}

	private void setX(int newXPos){
		x = newXPos;
	}

	private void set(int newYPos){
		y = newYPos;
	}
}

