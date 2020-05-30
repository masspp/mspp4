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
        header = this.peaklistHeaderRepository.save(header);
        
        
        
        // TODO: implement here to save PeakList and Point
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
            
            
        }
        
        reader.close();
        progress.setProgress( end );
        
    }
    
}
