
package com.test;

// class for setting contraints for the objects on the canvas and the 
// layout/design of the canvas
public class CanvasInstructions {
    
    private int enemyCount = 0;
    private int enemySideRange = 0;
    private double enemyLengthRange = 0.7;
    
    public CanvasInstructions(int enemyCount, int enemySideRange, double enemyLengthRange){
        this.enemyCount = enemyCount;
        this.enemySideRange = enemySideRange;
        this.enemyLengthRange = enemyLengthRange;
    }
    
    public void setPolygonProperties(Polygon polygon, float length){
        polygon.setDisplayLength(length);
        polygon.setBounds(length/2);
        polygon.setPath(polygon.getSides(), polygon.getAngle(), length/2);
    }
    
    public void setPlayerProperties(Player player, float radius){
        player.setDisplayRadius(radius);
        float left = player.getX()-radius;
        float right = player.getX()+radius;
        float top = player.getY()-radius;
        float bottom = player.getY()+radius;
        player.oval.set(left, top, right, bottom);
    }
    
    public void setEnemyCount(int enemyCount){
        this.enemyCount = enemyCount;
    }
    
    public int getEnemyCount(){
        return enemyCount;
    }
    
    public void setEnemySideRange(int enemySideRange){
        this.enemySideRange = enemySideRange;
    }
    
    public int getEnemySideRange(){
        return enemySideRange;
    }
    
    public void setEnemyLengthRange(double enemyLengthRange){
        this.enemyLengthRange = enemyLengthRange;
    }
    
    public double getEnemyLengthRange(){
        return enemyLengthRange;
    }
    
}
