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
 * @author Satoshi Tanaka
 * @since 2019
 *
 * Copyright (c) 2019 Satoshi Tanaka
 * All rights reserved.
 */
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
public abstract class SampleObject { 
    private String sampleid;
    private String filename;
    private String filepath;
    private ArrayList<SpectrumObject> spectra=new ArrayList<>();
    private ArrayList<ChromatogramObject> chromatograms=new ArrayList<>()                                                                                                                                                                                                                                                                                                                                   ;
    private String acquisitionsoftware;
    private String instrumentvendor;
    private String name;
    private int num_spectra=0;
    private boolean has_chromatogram;
    private int num_chromatograms=0;
    private boolean is_opened;
    
    
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
    public String getSampleId() {
        return sampleid;
    }

    /**
     * @param uid the SampleId to set
     */
    public void setSampleId(String uid) {
        this.sampleid = uid;
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
     * @param FileName the FileName to set
     */
    public void setFileName(String FileName) {
        this.filename = FileName;
    }
    
    /**
     * @return the spectra
     */
    public ArrayList<SpectrumObject> getSpectra() {
        return spectra;
    }

    /**
     * @return the chromatograms
     */
    public ArrayList<ChromatogramObject> getChromatograms() {
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
    public int getNumberOfSpectra() {
        return num_spectra;
    }

    /**
     * @return the num_chromatograms
     */
    public int getNumberOfChromatograms() {
        return num_chromatograms;
    }

    /**
     * @return the is_opened
     */
    public boolean isOpened() {
        return is_opened;
    }

    
    /**
     * 
     * @param chr 
     */
    public void addChromatogram(ChromatogramObject chr){
        this.getChromatograms().add(chr);
        this.num_chromatograms++;
    }
    
    public void addSpectrum(SpectrumObject spec){
        this.getSpectra().add(spec);
        this.num_spectra++;
    }
    
    public void insertChromagogram(ChromatogramObject chr, int idx){
        this.chromatograms.add(idx, chr);
    }
    
    public void insertSpectrum(SpectrumObject spec, int idx){
        this.spectra.add(idx, spec);
    }
    
    /**
     * TODO: Not Implemented
     * @param chr 
     */
    public void removeChromatogram(ChromatogramObject chr){
        this.chromatograms.remove(chr);
    }
    
    /**
     * TODO: Not Implemented
     * @param spec 
     */
    public void removeSpectrum(SpectrumObject spec){
        this.spectra.remove(spec);
    }
    
    
    public void clearChromatograms(){
        this.chromatograms = null;
    }
    
    public void clearSpectra(){
        this.spectra = null;
    }
    
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
    
}
