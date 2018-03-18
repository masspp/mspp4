/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ninja.mspp.model.dataobject;

/**
 *
 * @author murasemasaki
 */
public class Point<T extends Number> implements Comparable<Point<T>>{
    private T x;
    private T y;
    
    /**
     * 
     * @param x The X coordinate of this Point.
     * @param y The Y coordinate of this Point.
     */
    public Point(T x, T y){
        this.x = x;
        this.y = y;
    }
    
    /**
     * 
     * @param other
     * @return 
     */
    @Override
    public int compareTo(Point<T> other){
        if (other.x.doubleValue() > this.x.doubleValue()) return -1;
        return 1;
    }
    
    public T getX(){
        return this.x;
    }
    
    public T getY(){
        return this.y;
    }
    
}
