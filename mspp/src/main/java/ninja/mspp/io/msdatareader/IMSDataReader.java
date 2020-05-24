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
