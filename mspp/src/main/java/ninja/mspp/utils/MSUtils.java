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
 * @since 2018-05-29 20:23:12+09:00
 *
 * Copyright (c) 2018, Mass++ Users Group
 * All rights reserved.
 */
package ninja.mspp.utils;

/**
 *
 * @author masakimu
 */
public class MSUtils {
    /**
     * 
     * Compares a and b with given relative tolerance(rtol) and absolute tolerance(atol):
     *    partially ported from python's math.isclose() (https://www.python.org/dev/peps/pep-0485/)
     * 
     * @param a
     * @param b
     * @param rtol relative tolerance of relative to the larger absolute value of a or b, rtol must be greater than or equal to zero.
     * @param atol absolute tolerance, must be greater than or equal to zero.
     * @return true if a is nearly equal to b
     */
    public static boolean isclose(double a, double b, double rtol, double atol){
        if (atol < 0 || rtol < 0) throw new IllegalArgumentException("atol must be greater than or equal to zero");
        return Math.abs(a-b) <= Math.max( rtol * Math.max(Math.abs(a), Math.abs(b)), Math.abs(atol));
    }
    
    /**
     * 
     * @param a
     * @param b
     * @param rtol
     * @return true if a is nearly equal to b with relative tolerance: rtol.
     */
    public static boolean isclose_relative(double a, double b, double rtol){
        return isclose(a, b, rtol, 0.0);
    }
    
    /**
     * 
     * @param a
     * @param b
     * @param ppm
     * @return true if a is nearly equal to b with ppm [ppm].
     */
    public static boolean isclose_ppm(double a, double b, double ppm){
        return isclose(a, b, 1.0e-6*ppm, 0.0);
    }
        
    /**
     * 
     * @param a
     * @param b
     * @param atol
     * @return true if a is nearly equal to b with absolute tolerance: atol.
     */
    public static boolean isclose_abs(double a, double b, double atol){
        return isclose(a, b, 0.0, atol);
    }
    
}
