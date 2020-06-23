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
 * @author Masaki Murase
 * @since 2020
 *
 * Copyright (c) 2020 Masaki Murase
 * All rights reserved.
 */
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ninja.mspp.io.peaklistreader;

import java.util.List;
import ninja.mspp.model.dataobject.XYData;
import ninja.mspp.model.entity.PeakListHeader;
import ninja.mspp.model.entity.PeakList;
import ninja.mspp.model.entity.Peak;
import org.apache.commons.lang3.tuple.Pair;


/**
 *
 * @author masaki
 */
public interface IPeaklistReader {
    
    /**
     * 
     * @param processingSoftware Software name to generate peaklist file or to detecte peaks
     * @return
     * @throws Exception 
     */
    PeakListHeader getPeaklistHeader( String processingSoftware) throws Exception;
    
    /**
     * 
     * @return
     * @throws Exception 
     */
    Iterable<Pair<PeakList,XYData>> getPeaklists(PeakListHeader header) throws Exception;
    
    /**
     * 
     * @return
     * @throws Exception 
     */
    Integer getNumberOfPeaklists() throws Exception;
    
    /**
     * 
     * @return
     * @throws Exception 
     */
    List<Long> getPeaklistIds() throws Exception;
    
    /**
     * 
     * @param index
     * @return
     * @throws Exception 
     */
    PeakList getPeaklistById(Long index) throws Exception;
    
    /**
     * TODO: prepare new data container to treat charge array also with mz and intensity
     * 
     * @param index
     * @return
     * @throws Exception 
     */
    XYData getXYDataById(Long index) throws Exception;  

    
    /**
     * 
     * @throws Exception 
     */
    void close() throws Exception;
    
}
