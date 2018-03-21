/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ninja.mspp.math;

/**
 *
 * @author murasemasaki
 */
public class Utils {
    /**
     * 
     * Compares a and b with given relative tolerance(rtol) and absolute tolerance(atol):
     *    ported from python's math.isclose() (https://www.python.org/dev/peps/pep-0485/)
     * 
     * @param a
     * @param b
     * @param rtol relative tolerance of relative to the larger absolute value of a or b, rtol must be greater than zero.
     * @param atol absolute tolerance, must be greater than or equal to zero.
     * @return true if a is nearly equal to b
     */
    public static boolean isclose(double a, double b, double rtol, double atol){
        if (atol <= 0 || rtol < 0) throw new IllegalArgumentException("atol must be greater than or equal to zero");
        return Math.abs(a-b) <= Math.max( rtol * Math.max(Math.abs(a), Math.abs(b)), Math.abs(atol));
    }
    
}
