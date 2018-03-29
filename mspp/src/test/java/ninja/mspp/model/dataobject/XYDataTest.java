/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ninja.mspp.model.dataobject;

import java.util.ArrayList;
import java.util.List;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author masakimu
 */
public class XYDataTest {
    
    private XYData xydata;
    private List<Point<Double>> points;
    
    public XYDataTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
 
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
       points = new ArrayList<Point<Double>>(){
                {
                    add(new Point((double)16.6, (double)10.0));
                    add(new Point((double)1.0, (double)15.1));
                    add(new Point((double)1.2, (double)142.9));
                    add(new Point((double)1.2, (double)18.2));
                    add(new Point((double)23.8, (double)23.3));
                    add(new Point((double)3.3, (double)56.4));
                    add(new Point((double)10.4, (double)84.5));
                    add(new Point((double)15.5, (double)93.6));
                    add(new Point((double)22.7, (double)121.7));
                    add(new Point((double)29.9, (double)130.8));
                }
        };
        //points.add(new Point( (double)1.0, (double)100.2));
        
        xydata = new XYData(points, true);
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test of getMinX method, of class XYData.
     */
    @Test
    public void testGetMinX() {
        System.out.println("getMinX");
        XYData instance = xydata;
        double expResult = 1.0;
        double result = instance.getMinX();
        assertEquals(expResult, result, 0.0);
        // TODO review the generated test code and remove the default call to fail.
        //fail("The test case is a prototype.");
    }

    /**
     * Test of setMinX method, of class XYData.
     */
    @Test
    public void testSetMinX() {
        System.out.println("setMinX");
        double minX = 0.2;
        XYData instance = xydata;
        instance.setMinX(minX);
        assertEquals(instance.getMinX(),minX,0.0);
        // TODO review the generated test code and remove the default call to fail.
        //fail("The test case is a prototype.");
    }

    /**
     * Test of getMaxX method, of class XYData.
     */
    @Test
    public void testGetMaxX() {
        System.out.println("getMaxX");
        XYData instance = xydata;
        double expResult = 29.9;
        double result = instance.getMaxX();
        assertEquals(expResult, result, 0.0);
        // TODO review the generated test code and remove the default call to fail.
        //fail("The test case is a prototype.");
    }

    /**
     * Test of setMaxX method, of class XYData.
     */
    @Test
    public void testSetMaxX() {
        System.out.println("setMaxX");
        double maxX = 64.0;
        XYData instance = xydata;
        instance.setMaxX(maxX);
        assertEquals(instance.getMaxX(),maxX,0.0);
        // TODO review the generated test code and remove the default call to fail.
        //fail("The test case is a prototype.");
    }

    /**
     * Test of getMinY method, of class XYData.
     */
    @Test
    public void testGetMinY() {
        System.out.println("getMinY");
        XYData instance = xydata;
        double expResult = 10.0;
        double result = instance.getMinY();
        assertEquals(expResult, result, 0.0);
        // TODO review the generated test code and remove the default call to fail.
        //fail("The test case is a prototype.");
    }

    /**
     * Test of setMinY method, of class XYData.
     */
    @Test
    public void testSetMinY() {
        System.out.println("setMinY");
        double minY = 0.8;
        XYData instance = xydata;
        instance.setMinY(minY);
        assertEquals(instance.getMinY(),minY,0.0);
        // TODO review the generated test code and remove the default call to fail.
        //fail("The test case is a prototype.");
    }

    /**
     * Test of getMaxY method, of class XYData.
     */
    @Test
    public void testGetMaxY() {
        System.out.println("getMaxY");
        XYData instance = xydata;
        double expResult = 142.9;
        double result = instance.getMaxY();
        assertEquals(expResult, result, 0.0);
        // TODO review the generated test code and remove the default call to fail.
        //fail("The test case is a prototype.");
    }

    /**
     * Test of setMaxY method, of class XYData.
     */
    @Test
    public void testSetMaxY() {
        System.out.println("setMaxY");
        double maxY = 164.999;
        XYData instance = xydata;
        instance.setMaxY(maxY);
        assertEquals(maxY, instance.getMaxY(),0.0);
        // TODO review the generated test code and remove the default call to fail.
        //fail("The test case is a prototype.");
    }

    /**
     * Test of getPoints method, of class XYData.
     */
    @Test
    public void testGetPoints() {
        System.out.println("getPoints");
        XYData instance = xydata;
        List<Point<Double>> points_sorted_by_x=new ArrayList<Point<Double>>(){
                {
                    add(new Point((double)1.0, (double)15.1));
                    add(new Point((double)1.2, (double)142.9));
                    add(new Point((double)1.2, (double)18.2));
                    add(new Point((double)3.3, (double)56.4));
                    add(new Point((double)10.4, (double)84.5));
                    add(new Point((double)15.5, (double)93.6));
                    add(new Point((double)16.6, (double)10.0));
                    add(new Point((double)22.7, (double)121.7));
                    add(new Point((double)23.8, (double)23.3));
                    add(new Point((double)29.9, (double)130.8));                    
                }
        };
        List<Point<Double>> expResult = points_sorted_by_x; 
        List<Point<Double>> result = instance.getPoints();
        int i =0;
        for(Point<Double> p: expResult){
            assertEquals(p.getX(), result.get(i).getX());
            assertEquals(p.getY(), result.get(i).getY()); 
            i++;
        }
        // TODO review the generated test code and remove the default call to fail.
        //fail("The test case is a prototype.");
    }

    /**
     * Test of getX method, of class XYData.
     */
    @Test
    public void testGetX() {
        System.out.println("getX");
        int idx = 3;
        XYData instance = xydata;
        double expResult = 3.3;
        double result = instance.getX(idx);
        assertEquals(expResult, result, 0.0);
        // TODO review the generated test code and remove the default call to fail.
        //fail("The test case is a prototype.");
    }

    /**
     * Test of searchIndicies_by_X method, of class XYData.
     */
    @Test
    public void testSearchIndicies_by_X() {
        System.out.println("searchIndicies_by_X");
        double x = 1.2;
        XYData instance = xydata;
        ArrayList<Integer> expResult = new ArrayList<Integer>(){
            {
                add(1);
                add(2);
            }
        };
        ArrayList<Integer> result = instance.searchIndicies_by_X(x);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        //fail("The test case is a prototype.");
    }

    /**
     * Test of searchIndicies_by_Y method, of class XYData.
     */
    @Test
    public void testSearchIndicies_by_Y() {
        System.out.println("searchIndicies_by_Y");
        double y = 18.2;
        XYData instance = xydata;
        ArrayList<Integer> expResult = new ArrayList<Integer>(){
                {
                    add(2);
                }
        };
        ArrayList<Integer> result = instance.searchIndicies_by_Y(y);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        //fail("The test case is a prototype.");
    }

    /**
     * Test of searchNearestIndex_by_X method, of class XYData.
     */
    @Test
    public void testSearchNearestIndicies_by_X() {
        System.out.println("searchNearestIndicies_by_X");
        double x = 0.0;
        XYData instance = null;
        ArrayList<Integer> expResult = null;
        ArrayList<Integer> result = instance.searchNearestIndex_by_X(x);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of searchNearestIndex_by_Y method, of class XYData.
     */
    @Test
    public void testSearchNearestIndicies_by_Y() {
        System.out.println("searchNearestIndicies_by_Y");
        double y = 0.0;
        XYData instance = null;
        ArrayList<Integer> expResult = null;
        ArrayList<Integer> result = instance.searchNearestIndex_by_Y(y);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of filter_by_X method, of class XYData.
     */
    @Test
    public void testFilter_by_X() {
        System.out.println("filter_by_X");
        double min_x = 1.2;
        double max_x = 22.8;
        XYData instance = xydata;
        List<Point<Double>> expResult = new ArrayList<Point<Double>>(){
                {
                    add(new Point((double)1.2, (double)142.9));
                    add(new Point((double)1.2, (double)18.2));
                    add(new Point((double)3.3, (double)56.4));
                    add(new Point((double)10.4, (double)84.5));
                    add(new Point((double)15.5, (double)93.6));
                    add(new Point((double)16.6, (double)10.0));
                    add(new Point((double)22.7, (double)121.7));
                }
        };
        List<Point<Double>> result = instance.filter_by_X(min_x, max_x);
        int i = 0;
        for(Point<Double> p: expResult){
            assertEquals(p.getX(), result.get(i).getX());
            assertEquals(p.getY(), result.get(i).getY());
            i++;
        }
        
        // TODO review the generated test code and remove the default call to fail.
        //fail("The test case is a prototype.");
    }

    /**
     * Test of filter_by_Y method, of class XYData.
     */
    @Test
    public void testFilter_by_Y() {
        System.out.println("filter_by_Y");
        double min_y = 0.0;
        double max_y = 0.0;
        XYData instance = null;
        List<Point<Double>> expResult = null;
        List<Point<Double>> result = instance.filter_by_Y(min_y, max_y);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of sorted_by_Y method, of class XYData.
     */
    @Test
    public void testSorted_by_Y() {
        System.out.println("sorted_by_Y");
        boolean desc = true;
        XYData instance = xydata;
        List<Point<Double>> points_sorted_by_y = new ArrayList<Point<Double>>(){
                {
                    add(new Point((double)16.6, (double)10.0));
                    add(new Point((double)1.0, (double)15.1));
                    add(new Point((double)1.2, (double)18.2));
                    add(new Point((double)23.8, (double)23.3));
                    add(new Point((double)3.3, (double)56.4));
                    add(new Point((double)10.4, (double)84.5));
                    add(new Point((double)15.5, (double)93.6));
                    add(new Point((double)22.7, (double)121.7));
                    add(new Point((double)29.9, (double)130.8));               
                    add(new Point((double)1.2, (double)142.9));                 
                }
        };
        List<Point<Double>> expResult = points_sorted_by_y;
        List<Point<Double>> result = instance.sorted_by_Y(desc);
        //assertEquals(expResult, result);
        int i =0;
        for(Point<Double> p: expResult){
            assertEquals(p.getX(), result.get(i).getX());
            assertEquals(p.getY(), result.get(i).getY());
            i++;
        }       
        // TODO review the generated test code and remove the default call to fail.
        //fail("The test case is a prototype.");
    }

    /**
     * Test of getY method, of class XYData.
     */
    @Test
    public void testGetY() {
        System.out.println("getY");
        int idx = 3;
        XYData instance = xydata;
        double expResult = 56.4;
        double result = instance.getY(idx);
        assertEquals(expResult, result, 0.0);
        // TODO review the generated test code and remove the default call to fail.
        //fail("The test case is a prototype.");
    }

    /**
     * Test of getFromIndex_byX method, of class XYData.
     */
    @Test
    public void testGetFromIndex_byX() {
        System.out.println("getFromIndex_byX");
        double start_x_1 = 10.3;
        double start_x_2 = 10.4;
        double start_x_3 = 1.2;
        XYData instance = xydata;
        int expResult12 = 4;
        int expResult3 = 1;
        int result1 = instance.getFromIndex_byX(start_x_1);
        assertEquals(expResult12, result1);
        int result2 = instance.getFromIndex_byX(start_x_2);
        assertEquals(expResult12, result2);
        int result3 = instance.getFromIndex_byX(start_x_3);
        assertEquals(expResult3, result3);
        // TODO review the generated test code and remove the default call to fail.
        //fail("The test case is a prototype.");
    }

    /**
     * Test of getToIndex_byX method, of class XYData.
     */
    @Test
    public void testGetToIndex_byX() {
        System.out.println("getToIndex_byX");
        Double end_x_1 = 10.4;
        Double end_x_2 = 10.5;
        Double end_x_3 = 1.2;
        XYData instance = xydata;
        int expResult12 = 5;
        int expResult3 = 3;
        int result1 = instance.getToIndex_byX(end_x_1);
        assertEquals(expResult12, result1);
        int result2 = instance.getToIndex_byX(end_x_2);
        assertEquals(expResult12, result2);
        int result3 = instance.getToIndex_byX(end_x_3);
        assertEquals(expResult3, result3);
        // TODO review the generated test code and remove the default call to fail.
        //fail("The test case is a prototype.");
    }


}
