/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ninja.mspp.model.dataobject;

import java.util.ArrayList;

/**
 *
 * @author masakimu
 */
public abstract class Chromatogram {

    /**
     * @return the sample
     */
    public Sample getSample() {
        return sample;
    }

    /**
     * @param sample the sample to set
     */
    public void setSample(Sample sample) {
        this.sample = sample;
    }

    /**
     * @return the id
     */
    public int getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * @return the chromatData
     */
    public XYData getChromatogramData() {
        return chromatogramData;
    }

    /**
     * @param chromatData the chromatData to set
     */
    public void setChromatogramData(XYData chromatogramData) {
        this.chromatogramData = chromatogramData;
    }

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
     * @return the ms_stage
     */
    public double getMs_stage() {
        return ms_stage;
    }

    /**
     * @param ms_stage the ms_stage to set
     */
    public void setMs_stage(double ms_stage) {
        this.ms_stage = ms_stage;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the title
     */
    public String getTitle() {
        return title;
    }

    /**
     * @param title the title to set
     */
    public void setTitle(String title) {
        this.title = title;
    }
    
    private Sample sample;
    private int id;
    private XYData chromatogramData;
    private double mz;
    private double ms_stage;
    private String name;
    private String title;
    
    protected enum SearchType{
        SEARCH_PREV,
        SEARCH_NEXT,
        SEARCH_Near
    }
    
    public abstract void onGetChromatogram(XYData chromatogramData);
    
    public abstract ArrayList<Spectrum> onGetSpectra(double startRt, double endRt, SearchType startSearchType, SearchType endSearchType);
    
    public abstract double onGetMass(int idx);
    
    public abstract int onGetMsStage(int idx);
    
    public abstract int onGetPrecursor(int idx);
}
