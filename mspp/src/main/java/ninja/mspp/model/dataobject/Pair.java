/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ninja.mspp.model.dataobject;
import org.apache.commons.lang3.builder.CompareToBuilder;

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
     * Compares the pair based on the left element followed by the right element, just like commons.lang3.pair
     * @param other
     * @return 
     */
    @Override
    public int compareTo(Pair<T> other){
        return new CompareToBuilder()
                .append(getLeft(),other.getLeft())
                .append(getRight(), other.getRight())
                .toComparison();
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
    
    public String toString(final String format){
        return String.format(format, getRight(),getLeft());
    }
    
    
}
