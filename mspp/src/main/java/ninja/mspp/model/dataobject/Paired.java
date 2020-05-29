/*
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
 * @author Mass++ Users Group (https://www.mspp.ninja/)
 * @author Masayo Kimura
 * @since Sun Mar 18 21:02:52 JST 2018
 *
 * Copyright (c) 2018 Masayo Kimura
 * All rights reserved.
 */
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ninja.mspp.model.dataobject;
import java.io.Serializable;

import org.apache.commons.lang3.builder.CompareToBuilder;



/**
 *
 * @author masakimu
 */
public class Paired<T extends Number> implements Comparable<Paired<T>>, Cloneable, Serializable {
    private T left;
    private T right;

    /**
     *
     * @param x The X coordinate of this Point.
     * @param y The Y coordinate of this Point.
     */
    public Paired(T x, T y){
        this.left = x;
        this.right = y;
    }

    /**
     * Compares the pair based on the left element followed by the right element, just like commons.lang3.pair
     * @param other
     * @return
     */
    @Override
    public int compareTo(Paired<T> other){
        return new CompareToBuilder()
                .append(getLeft(), other.getLeft())    //ascending order
                .append(other.getRight(), getRight())  //descending order
                .toComparison();
    }

    public T getLeft(){
        return this.left;
    }

    public T getRight(){
        return this.right;
    }

    @Override
    public Paired<T> clone() throws CloneNotSupportedException{
        Paired<T> p = (Paired<T>) super.clone();
        p.left = left;
        p.right = right;
        return p;
    }

    public String toString(final String format){
        return String.format(format, getRight(),getLeft());
    }


}
