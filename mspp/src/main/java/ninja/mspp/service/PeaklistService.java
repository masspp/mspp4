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
package ninja.mspp.service;

import java.util.ArrayList;
import java.util.List;
import javafx.scene.control.ProgressIndicator;
import ninja.mspp.io.peaklistreader.AbstractPeaklistReader;
import ninja.mspp.model.dataobject.Point;
import ninja.mspp.model.dataobject.XYData;
import ninja.mspp.model.entity.Peak;
import ninja.mspp.model.entity.PeakList;
import ninja.mspp.model.entity.PeakListHeader;
import ninja.mspp.repository.PeakListHeaderRepository;
import ninja.mspp.repository.PeakListRepository;
import ninja.mspp.repository.PeakRepository;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;

/**
 *
 * @author masaki
 */
public class PeaklistService {
    
    @Autowired
    private PeakListHeaderRepository peaklistHeaderRepository;
    
    @Autowired
    private PeakListRepository peaklistRepository;
    
    @Autowired
    private PeakRepository peakRepository;
    
    /**
     * 
     * @param reader
     * @param processingSoftware software name to detect peaks or to generate peaklist file
     * @param progress
     * @param start
     * @param end
     * @throws Exception 
     */
    public void register(AbstractPeaklistReader reader, String processingSoftware, ProgressIndicator progress, double start, double end )throws Exception {
        progress.setProgress( start );
        
        PeakListHeader header = reader.getPeaklistHeader(processingSoftware);
        
        List<PeakList> peaklists = new ArrayList<>();
        for( Pair<PeakList, XYData> peakdata : reader.getPeaklists(header)){

            // convert extracted peaks(xydata) into List<Peak>
            XYData xydata = peakdata.getRight();
            List<Peak> peaks = new ArrayList<>();

            for(Point<Double> p: xydata.getPoints()){
                Peak peak = new Peak();
                peak.setMz(p.getX());
                peak.setIntensity(p.getY());
                peaks.add(peak);
            }
            
            // save PeakList into DB
            PeakList peaklist = peakdata.getKey();
            peaklist.setPeaks(peaks);          
            peaklistRepository.save(peaklist);  // Does it save all Point into DB?
            peaklists.add(peaklist);
        }
        
        header.setPeakLists(peaklists);
        peaklistHeaderRepository.save(header);
        
        reader.close();
        progress.setProgress( end );
        
    }
    
}
