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
 * @since 2020
 *
 * Copyright (c) 2020 Masayo Kimura
 * All rights reserved.
 */
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ninja.mspp.io.msdatareader;


import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.tuple.Pair;

import ninja.mspp.model.entity.Sample;
import ninja.mspp.model.entity.Spectrum;
import ninja.mspp.model.entity.Chromatogram;
import ninja.mspp.model.entity.PointList;

/**
 *
 * @author masakimu
 */
public interface IMSDataReader {
    
    /**
     * 
     * @return 
     */
    Sample getSample() throws Exception;
    
    /**
     * 
     * just reserved
     * 
     * @return 
     */
    List<Integer> getSpectrumIds() throws Exception;
    
    /**
     * 
     * just reserved
     * 
     * @param index
     * @return 
     */
    Spectrum getSpectrumById(Integer index) throws Exception;
    
    /**
     * 
     * @param sample
     * @param precursorMap to save relationship of precursor MS spectrum and its childeren 
     * @return 
     */
    Iterable<Pair<Spectrum,PointList>> getSpectraPointList(Sample sample, Map<String, Spectrum> precursorMap) throws Exception;
    
    /**
     * 
     * @return total number of all spectra
     * 
     * TODO??: do you need total number of MS1 or MS2 or whole spectra?
     * 
     */
    Integer getNumOfSpectra() throws Exception ;
    
    
    /**
     * 
     * @param index
     * @return 
     */
    PointList getPointList(Integer index) throws Exception;
    
    /**
     * 
     * just reserved
     * 
     * @return 
     */
    List<Integer> getChromatogramIds() throws Exception;
    
    /**
     * 
     * just reserved
     * 
     * @param index
     * @return 
     */
    Chromatogram getChromatogram(Integer index) throws Exception; 
    
    /**
     * 
     * @return 
     */
    Iterable<Pair<Chromatogram,PointList>> getChromatograms(Sample sample) throws Exception;
    
    /**
     * 
     * @throws Exception 
     */
    void close() throws Exception ;
    
            
}
