/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ninja.mspp.model.dataobject;

/**
 *
 * @author masakimu
 */
public class Range<T extends Number> extends Pair<T> implements RangeProvider<T>{

    /**
     * 
     * @param x The X coordinate of this Point.
     * @param y The Y coordinate of this Point.
     */
    public Range(T start, T end){
        super(start, end);
    }
    
    @Override
    public T getStart(){
        return this.getLeft();
    }
    
    @Override
    public T getEnd(){
        return this.getRight();
    }
    
}
