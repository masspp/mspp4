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
 * @since 2019
 *
 * Copyright (c) 2019 Masayo Kimura
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
public class PeakListObject {

    /**
     * @return the index_positional
     */
    public int getIndex_positional() {
        return index_positional;
    }

    /**
     * @param index_positional the index_positional to set
     */
    public void setIndex_positional(int index_positional) {
        this.index_positional = index_positional;
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
     * @return the peaks
     */
    public XYData getPeaks() {
        return peaks;
    }

    /**
     * @param peaks the peaks to set
     */
    public void setPeaks(XYData peaks) {
        this.peaks = peaks;
    }

    /**
     * @return the index
     */
    public int getIndex() {
        return index;
    }

    /**
     * @param index the index to set
     */
    public void setIndex(int index) {
        this.index = index;
    }

    /**
     * @return the msStage
     */
    public int getMsStage() {
        return msStage;
    }

    /**
     * @param msStage the msStage to set
     */
    public void setMsStage(int msStage) {
        this.msStage = msStage;
    }

    /**
     * @return the rt
     */
    public String getRt() {
        return rt;
    }

    /**
     * @param rt the rt to set
     */
    public void setRt(String rt) {
        this.rt = rt;
    }

    /**
     * @return the precursorMz
     */
    public double getPrecursorMz() {
        return precursorMz;
    }

    /**
     * @param precursorMz the precursorMz to set
     */
    public void setPrecursorMz(double precursorMz) {
        this.precursorMz = precursorMz;
    }

    /**
     * @return the precursorCharge
     */
    public int getPrecursorCharge() {
        return precursorCharge;
    }

    /**
     * @param precursorCharge the precursorCharge to set
     */
    public void setPrecursorCharge(int precursorCharge) {
        this.precursorCharge = precursorCharge;
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

    /**
     * @return the file_path
     */
    public String getFile_path() {
        return file_path;
    }

    /**
     * @param file_path the file_path to set
     */
    public void setFile_path(String file_path) {
        this.file_path = file_path;
    }

    /**
     * @return the is_identified
     */
    public boolean is_identified() {
        return is_identified;
    }

    /**
     * @param is_identified the is_identified to set
     */
    public void set_identified(boolean is_identified) {
        this.is_identified = is_identified;
    }

    private int id;

    private XYData peaks;

    /**
     *  Scan Number of  Spectrum (1,2, ...)
     */
    private int index;
    
    /**
     * Position in Paklist File (1, 2, ...)
     */
    private int index_positional;

    private int msStage;

    private String rt;  //rt or rt range. 

    private double precursorMz;

    private int precursorCharge;

    private String title;

    private String file_path;

    private boolean is_identified;

}
