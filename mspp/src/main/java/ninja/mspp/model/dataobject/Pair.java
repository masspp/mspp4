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
public class Pair<T extends Number> implements Comparable<Pair<T>>, Cloneable{
    private T left;
    private T right;
    
    /**
     * 
     * @param x The X coordinate of this Point.
     * @param y The Y coordinate of this Point.
     */
    public Pair(T x, T y){
        this.left = x;
        this.right = y;
    }
    
    /**
     * 
     * @param other
     * @return 
     */
    @Override
    public int compareTo(Pair<T> other){
        if (other.left.doubleValue() > this.left.doubleValue()) return -1;
        return 1;
    }
    
    public T getLeft(){
        return this.left;
    }
    
    public T getRight(){
        return this.right;
    }
    
    @Override
    public Pair<T> clone() throws CloneNotSupportedException{
        Pair<T> p = (Pair<T>) super.clone();
        p.left = left;
        p.right = right;
        return p;
    }
    
    
}
