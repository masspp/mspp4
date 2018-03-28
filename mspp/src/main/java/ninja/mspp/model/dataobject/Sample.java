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
public abstract class Sample {

    /**
     * @return the has_chromatogram
     */
    public boolean getHasChromatogram() {
        return has_chromatogram;
    }

    /**
     * @param has_chromatogram the has_chromatogram to set
     */
    public void setHasChromatogram(boolean has_chromatogram) {
        this.has_chromatogram = has_chromatogram;
    }

 
    private int sampleid;
    private String filename;
    private String filepath;
    private ArrayList<Spectrum> spectra;
    private ArrayList<Chromatogram> chromatograms;
    private String acquisitionsoftware;
    private String instrumentvendor;
    private String name;
    private int num_spectra;
    private boolean has_chromatogram;
    private int num_chromatograms;
    private boolean is_opend;
    
    
    /**
     * 
     * @param path
     * @return 
     */
    public abstract boolean onOpenFile(String path);
    
    /**
     * 
     * @return 
     */
    public abstract boolean onCloseFile();
    
        /**
     * @return the SampleId
     */
    public int getSampleId() {
        return sampleid;
    }

    /**
     * @param SampleId the SampleId to set
     */
    public void setSampleId(int SampleId) {
        this.sampleid = SampleId;
    }

    /**
     * @return the FileName
     */
    public String getFileName() {
        return filename;
    }

    /**
     * @return the FilePath
     */
    public String getFilePath() {
        return filepath;
    }

    /**
     * @param FilePath the FilePath to set
     */
    public void setFilePath(String FilePath) {
        this.filepath = FilePath;
    }

    /**
     * @return the spectra
     */
    public ArrayList<Spectrum> getSpectra() {
        return spectra;
    }

    /**
     * @return the chromatograms
     */
    public ArrayList<Chromatogram> getChromatograms() {
        return chromatograms;
    }
    
       /**
     * @return the acquisitionsoftware
     */
    public String getAcquisitionsoftware() {
        return acquisitionsoftware;
    }

    /**
     * @param acquisitionsoftware the acquisitionsoftware to set
     */
    public void setAcquisitionsoftware(String acquisitionsoftware) {
        this.acquisitionsoftware = acquisitionsoftware;
    }

    /**
     * @return the instrumentvendor
     */
    public String getInstrumentvendor() {
        return instrumentvendor;
    }

    /**
     * @param instrumentvendor the instrumentvendor to set
     */
    public void setInstrumentvendor(String instrumentvendor) {
        this.instrumentvendor = instrumentvendor;
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
     * @return the num_spectra
     */
    public int getNum_spectra() {
        return num_spectra;
    }

    /**
     * @return the num_chromatograms
     */
    public int getNum_chromatograms() {
        return num_chromatograms;
    }

    /**
     * @return the is_opend
     */
    public boolean isIs_opend() {
        return is_opend;
    }

    
    /**
     * 
     * @param chr 
     */
    public void addChromatogram(Chromatogram chr){
        this.getChromatograms().add(chr);
    }
    
    public void addSpectrum(Spectrum spec){
        this.getSpectra().add(spec);
    }
    
    public void insertChromagogram(Chromatogram chr, int idx){
        this.chromatograms.add(idx, chr);
    }
    
    public void insertSpectrum(Spectrum spec, int idx){
        this.spectra.add(idx, spec);
    }
    
    /**
     * TODO: Not Implemented
     * @param chr 
     */
    public void removeChromatogram(Chromatogram chr){
        this.chromatograms.remove(chr);
    }
    
    /**
     * TODO: Not Implemented
     * @param spec 
     */
    public void removeSpectrum(Spectrum spec){
        this.spectra.remove(spec);
    }
    
    
    public void clearChromatograms(){
        this.chromatograms = null;
    }
    
    public void clearSpectra(){
        this.spectra = null;
    }
    
    
    
}
