/**
 * BSD 3-Clause License
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * * Redistributions of source code must retain the above copyright notice, this
 *   list of conditions and the following disclaimer.
 *
 * * Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 *
 * * Neither the name of the copyright holder nor the names of its
 *   contributors may be used to endorse or promote products derived from
 *   this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 *
 * @author Mass++ Users Group
 * @author Masaki Murase
 * @since 2018-03-18 10:41:24+09:00
 *
 * Copyright (c) 2018, Mass++ Users Group
 * All rights reserved.
 */
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ninja.mspp.model.dataobject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 *
 * @author masakimu
 */
public class XYData implements Iterable< Point< Double > > {

    private List< Point<Double> > xydata;

    private double minX=Double.MAX_VALUE;
    private double maxX=0.0;
    private double minY=Double.MAX_VALUE;
    private double maxY=Double.MIN_VALUE;

    /**
     *
     * @param xydata MUST be sorted in descending order
     */
    public XYData(Object xydata){
        this( xydata, false, false);
    }

    /**
     *
     * @param xydata
     * @param do_sort if not true, xydata MUST be sorted in descending order
     */
    public XYData(Object xydata, boolean do_sort){
        this( xydata, do_sort, false);
    }

    /**
     *
     * @param xydata
     * @param do_sort
     * @param is_reverse: MUST be false (sorry)
     */
    private XYData(Object xydata, boolean do_sort, boolean is_reverse){
        
        this.xydata = (ArrayList<Point<Double>>) xydata;
//        if (  this.xydata.isEmpty() ){
//            this.xydata = new ArrayList<Point<Double>>(){
//                {
//                    add(new Point((double)-1.0, (double)-1.0));
//                }
//            };
//        }
                
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
     * @param x
     * @return "first found" index of xydata where Point has X-value eqaul to x with highest Y-value.
     *                   or -1 if not found.
     */
    public int searchIndex_by_X(double x){
        if (x > xydata.get(xydata.size()-1).getX()) return -1;
        int idx = ~(this.binarySearch_byX_inclusive_least_upperbounds(x));
        if ( xydata.get(idx).getX()==x ) return idx;
        return -1;
    }

    /**
     *
     * @param y
     * @return list of all indicies where P.getY()==y
     */
    public ArrayList<Integer> searchIndicies_by_Y(double y){
       ArrayList<Integer> answer = new ArrayList<>();
       int i=0;
       for(Point<Double>p : this.xydata){
           if (y==p.getY()){
               answer.add(i);
           }
           i++;
       }

       return answer;
    }

    /**
     *
     * @param x
     * @return index of point which has x-value nearest to x.
     *           if multiple points are nearest, the point which has largest Y-value is selected.
     *
     */
    public int searchNearestIndex_by_X(double x){

        int idx = ~(this.binarySearch_byX_inclusive_least_upperbounds(x));
        // x is greater than maximum x-value
        if (idx == xydata.size()) return this.searchIndex_by_X(xydata.get(idx-1).getX());
        if (idx == 0) return idx;   // x is lower than or equal to minimum x-value of points in the xydata
        // the point which has exactly same x-value as x and highest y-value.
        if ( xydata.get(idx).getX()==x ) return idx;

        // find the point with largest y and nearest to x between left and right points
        double x_left = xydata.get(idx-1).getX();
        double x_right = xydata.get(idx).getX();
        double diff_left=x-x_left;
        double diff_right=x_right-x;
        // if differences are same, left point is selected.
        return (diff_left<=diff_right)?this.searchIndex_by_X(x_left):this.searchIndex_by_X(x_right);
    }

    /**
     *
     * @param y
     * @return list of index of points which has nearest y-value to given y
     */
    public ArrayList<Integer> searchNearestIndicies_by_Y(double y){
       double diff = Double.MAX_VALUE;
       double nearest_y = 0.0;
       for(Point<Double> p: xydata){
           if (Math.abs(p.getY()-y) < diff){
               diff = Math.abs(p.getY()-y);
               nearest_y=p.getY();
           }
       }
       return this.searchIndicies_by_Y(nearest_y); //TODO: improve efficacy
    }

    /**
     * Get list of points of which x-value ranges from min_x to max_x
     * @param min_x
     * @param max_x
     * @return
     */
    public List<Point<Double>> filter_by_X(double min_x,double max_x){

        int start = this.getFromIndex_byX(min_x);
        int end = this.getToIndex_byX(max_x);
        if (start <0 || end <0) return  new ArrayList<Point<Double>>(){};
        return this.xydata.subList(start, end);

    }


    /**
     *
     * Get list of points of which y-value ranges from min_y to max_y
     * @param min_y
     * @param max_y
     * @return list of Point in which each element has x-value between min_y and max_y
     */
    public List<Point<Double>> filter_by_Y(double min_y, double max_y)
    {

        List<Point<Double>> result = new ArrayList<Point<Double>>(){};
        for( Point<Double> p:  xydata ){
            if (p.getY()>= min_y && p.getY()<=max_y){
                result.add(p);
            }
        }
        return result;
    }

    /**
     * @param desc not yet implemented
     * @return sorted shallow copy of xydata by Y
     */
    public List<Point<Double>> sorted_by_Y(boolean desc){
        List<Point<Double>> sorted_y = new ArrayList<>(this.xydata);

        Collections.sort(sorted_y ,
                (p1,p2) ->  ( p1.getY() > p2.getY() )?1:
                        (p1.getY()<p2.getY())?-1:0);
        return sorted_y;
    }

    /**
     * @param idx
     * @return
     */
    public double getY(int idx){
        return xydata.get(idx).getY();
    }

    /**
     * @param x
     * @return infemum of set in which element has nearest x-value to x.
     *              otherwise -1 (if list is empty).
     */
    public int getFromIndex_byX(double start_x){
        if (xydata.isEmpty()) return -1;
        //if (start_x > xydata.get( xydata.size()-1 ).getX()) return -1;
        int start = ~(binarySearch_byX_inclusive_least_upperbounds(start_x));
        return start;
    }

    /**
     * @param max_x
     * @return least index in open upper bounds in which elements has x-value greater than x,
     *             otherwise -1 (if list is empty)
     */
    public int getToIndex_byX(Double end_x){
        if (xydata.isEmpty()) return -1;
        //if (end_x >= xydata.get( xydata.size()-1 ).getX()) return -1;
        int end = ~(binarySearch_byX_exclusive_least_upperbounds(end_x));
        return end;
    }


    /**
     * binary searching for least index in closed upper bounds in which elements has
     *    x-value greater than or equal to given x. If multiple points has x-value
     *    equal to x, then index of lower bound is selected.
     * @param x
     * @return -(found index)-1
     */
    protected int binarySearch_byX_inclusive_least_upperbounds(double x){
        int idx = Collections.binarySearch(
        this.xydata,
        new Point(x,0.0),
        (Point p1,Point p2)->(
                p1.getX().doubleValue() >= p2.getX().doubleValue())?(int)1:(int)-1);
        return idx;
    }

    /**
     * binary searching for least index in open upper bounds in which elements has
     *    greater x-value to given x.
     * @param x
     * @return -(found index) -1
     */
    protected int binarySearch_byX_exclusive_least_upperbounds(double x){
        int idx = Collections.binarySearch(
                this.xydata,
                new Point(x,0.0),
                (Point p1,Point p2)->(
                        p1.getX().doubleValue() > p2.getX().doubleValue() )?(int)1:(int)-1);

        return idx;
    }

	@Override
	public Iterator< Point< Double > > iterator() {
		return this.xydata.iterator();
	}

}
