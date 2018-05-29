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
public class Precursor {

    /**
     * @return the mz
     */
    public double getMz() {
        return mz;
    }

    /**
     * @param mz the mz to set
     */
    public void setMz(double mz) {
        this.mz = mz;
    }

    /**
     * @return the intensity
     */
    public double getIntensity() {
        return intensity;
    }

    /**
     * @param intensity the intensity to set
     */
    public void setIntensity(double intensity) {
        this.intensity = intensity;
    }

    /**
     * @return the charge
     */
    public int getCharge() {
        return charge;
    }

    /**
     * @param charge the charge to set
     */
    public void setCharge(int charge) {
        this.charge = charge;
    }
    
    private double mz;
    private double intensity;
    private int charge;

    public Precursor(double mz, double intensity, int charge) {
        this.mz = mz;
        this.intensity = intensity;
        this.charge = charge;
    }
    
    public Precursor(double mz, double intensity){
        this(mz, intensity, 1);
    }
    
    public Precursor(double mz){
        this(mz, 0.0, 1);
    }
    
}
