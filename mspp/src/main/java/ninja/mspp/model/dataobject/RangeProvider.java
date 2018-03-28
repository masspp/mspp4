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
public interface RangeProvider<T> {
    public T getStart();
    public T getEnd();
}
