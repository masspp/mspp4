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
