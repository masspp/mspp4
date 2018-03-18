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
public class XYData {

    private List< Point<Double> > xydata;
        
    private double minX=0.0;
    private double maxX=0.0;
    private double minY=Double.MAX_VALUE;
    private double maxY=Double.MIN_VALUE;
    
    /**
     * 
     * @param xydata should be sorted
     */
    public XYData(Object xydata){
        this( xydata, false, false);
    }
    
    /**
     * 
     * @param xydata
     * @param do_sort if not true, xydata should be sorted
     */
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
        
        if (is_reverse){
            this.minX=this.xydata.get(this.xydata.size()-1).getX();
            this.maxX = this.xydata.get(0).getX();          
        }else{
            this.maxX=this.xydata.get(this.xydata.size()-1).getX();
            this.minX = this.xydata.get(0).getX();
        }
        for(  Point  p: this.xydata){
            double py = p.getY().doubleValue();
            if (this.maxY < py){
                this.maxY = py;
            }
            if (this.minY > py){
                this.minY = py;
            }
        }
        
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
    
    /**
     * 
     * @return List of all points
     */
    public List<Point<Double>> getPoints( ){
        return this.xydata;
    }
    
    /**
     * 
     * @param idx
     * @return X-value at idx
     */
    public double getX(int idx){
        return xydata.get(idx).getX();
    }
    
    /**
     * TODO: Not Implemented
     * Get list of points, x-value of which ranges from min_x to max_x
     * @param min_x
     * @param max_x
     * @return 
     */
    public List<Point<Double>> filter_by_X(double min_x, double max_x){
        return this.xydata;
    }
    
    /**
     * TODO: Not Implemented
     * Get list of points, y-value of which ranges from min_y to max_y
     * @param min_y
     * @param max_y
     * @return 
     */
    public List<Point<Double>> filter_by_Y(double min_y, double max_y)
    {
        return this.xydata;
    }
    
    /**
     * @param desc not yet implemented 
     * @return sorted shallow copy of xydata by Y
     */
    public List<Point<Double>> softed_by_Y(boolean desc){
        List<Point<Double>> sorted_y = new ArrayList<>(this.xydata);
        
        Collections.sort(sorted_y , 
                (p1,p2) -> (int) (p1.getY() - p2.getY())  );
        return sorted_y;
    }
    
    /**
     * TODO: Not Implemented
     * @param idx
     * @return 
     */
    public double getY(int idx){
        return xydata.get(idx).getY();
    }
    
    /**
     * TODO: Not Implemented
     * @param min_x
     * @return 
     */
    public int getFromIndex_byX(Double min_x){
        // TODO: to be implemented
        return 1;
    }
    
    /**
     * TODO: Not Implemented
     * @param max_x
     * @return 
     */
    public int getToIndex_byX(Double max_x){
         // TODO: to be implemented
         return 1;
    }
    
    /**
     * TODO: Not Implemented
     * @param min_y
     * @return 
     */
    public int getFromIndex_byY(Double min_y){
         // TODO: to be implemented
         return 1;
        
    }
    
    /**
     * TODO: Not Implemented
     * @param max_y
     * @return 
     */
    public  int getToIndex_byY(Double max_y){
         // TODO: to be implemented
         return 1;
    }
    
    
}
