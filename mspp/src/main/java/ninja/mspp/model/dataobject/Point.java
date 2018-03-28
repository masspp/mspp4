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
public class Point<T extends Number> extends Pair<T> implements XYProvider<T>{

    /**
     * 
     * @param x The X coordinate of this Point.
     * @param y The Y coordinate of this Point.
     */
    public Point(T x, T y){
        super(x,y);
    }
    
    @Override
    public T getX(){
        return this.getLeft();
    }
    
    @Override
    public T getY(){
        return this.getRight();
    }
    
}
