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
 * Copyright (c) Mon Sep 23 19:52:14 JST 2019 Satoshi Tanaka
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
public abstract class ChromatogramObject extends DrawObject {

    /**
     * @return the sample
     */
    public String getSamplePath() {
        return sample_path;
    }

    /**
     * @param sample_path the sample to set
     */
    public void setSamplePath(String sample_path) {
        this.sample_path = sample_path;
    }

    /**
     * @return the id
     */
    public String getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * @return the chromatData
     */
    public XYData getChromatogramData() {
        return chromatogramData;
    }

    /**
     * @param chromatogramData the chromatData to set
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
    public void setMs_stage(int ms_stage) {
        this.ms_stage = ms_stage;
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

    private String sample_path;
    private String id;
    private XYData chromatogramData;
    private double mz;
    private Range<Double> mzrange;
    private int ms_stage;
    private String title;

    protected enum SearchType{
        SEARCH_PREV,
        SEARCH_NEXT,
        SEARCH_Near
    }


    /**
     * 
     * @param sample
     * @param mzrange 
     */
    public ChromatogramObject(SampleObject sample, Range< Double > mzrange){
    	super( sample );
        this.sample_path = sample.getFilePath();
        this.mzrange = mzrange;
        this.id = "";
        this.mz = 0.0;
        this.ms_stage = 1;
        this.title = "";
    }

    public ChromatogramObject(SampleObject sample, double mz){
        this(sample,new Range<Double>(mz,mz));
    }

    public ChromatogramObject(SampleObject sample){
        this(sample, 0.0);
    }

    @Override
    public XYData getXYData() {
    	XYData xyData = this.onGetChromatogram();
    	return xyData;
    }

    public abstract XYData onGetChromatogram();

    /***
     *   TODO: still considering specification for viewer GUI.
     * @param startRt
     * @param endRt
     * @param startSearchType
     * @param endSearchType
     * @return
     */
    public abstract ArrayList<SpectrumObject> onGetSpectra(double startRt, double endRt, SearchType startSearchType, SearchType endSearchType);

    /**
     * TODO: still considering specification for viewer GUI.
     * @param idx
     * @return
     */
    public abstract double onGetMass(int idx);

    /**
     * TODO: still considering specification for viewer GUI.
     * @param idx
     * @return
     */
    public abstract int onGetMsStage(int idx);

    /**
     * TODO: still considering specification for viewer GUI.
     * @param idx
     * @return
     */
    public abstract double onGetMz(int idx);
}
