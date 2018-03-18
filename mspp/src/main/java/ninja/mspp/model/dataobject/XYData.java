/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ninja.mspp.model.dataobject;

import java.util.ArrayList;
import java.util.List;
import java.util.Collections;

/**
 *
 * @author murasemasaki
 */
public abstract class XYData {

    private List< Point<Double> > xydata;
        
    private double minX=0.0;
    private double maxX=0.0;
    private double minY=0.0;
    private double maxY=0.0;
    
    public XYData(Object xydata){
        this( xydata, false, false);
    }
    
    public XYData(Object xydata, boolean do_sort){
        this( xydata, do_sort, false);
    }
    
    public XYData(Object xydata, boolean do_sort, boolean is_reverse){
        this.xydata = (ArrayList<Point<Double>>) xydata;
        if (do_sort){
            if (is_reverse){
                Collections.<Point<Double>>sort( this.xydata,Collections.reverseOrder() );
            }else{
                Collections.<Point<Double>>sort( this.xydata);
            }
        }
        
        this.maxX=this.xydata.get(this.xydata.size()-1).getX();
    }
    
       
    public <A extends Number>XYData(ArrayList<Point<A>> xydata)  {
        
    }     
    
     /**
     * @return the minX
     */
    public double getMinX() {
        return minX;
    }

    /**
     * @param minX the minX to set
     */
    public void setMinX(double minX) {
        this.minX = minX;
    }

    /**
     * @return the maxX
     */
    public double getMaxX() {
        return maxX;
    }

    /**
     * @param maxX the maxX to set
     */
    public void setMaxX(double maxX) {
        this.maxX = maxX;
    }

    /**
     * @return the minY
     */
    public double getMinY() {
        return minY;
    }

    /**
     * @param minY the minY to set
     */
    public void setMinY(double minY) {
        this.minY = minY;
    }

    /**
     * @return the maxY
     */
    public double getMaxY() {
        return maxY;
    }

    /**
     * @param maxY the maxY to set
     */
    public void setMaxY(double maxY) {
        this.maxY = maxY;
    }   
    
    public List<Point<Double>> getPoints( ){
        return this.xydata;
    }
    
}
