/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ninja.mspp.plugin.io.file.jmzreader_wrapper;

import java.io.File;
import java.io.FileInputStream;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.tuple.Pair;

import uk.ac.ebi.pride.tools.jmzreader.JMzReaderException;
import uk.ac.ebi.pride.tools.mgf_parser.MgfFile;
import uk.ac.ebi.pride.tools.mgf_parser.MgfFile.SearchType;

import ninja.mspp.io.msdatareader.AbstractMSDataReader;
import ninja.mspp.model.entity.Chromatogram;
import ninja.mspp.model.entity.PointList;
import ninja.mspp.model.entity.Sample;
import ninja.mspp.model.entity.Spectrum;


/**
 *
 * @author masakimu
 */
public class jmzReaderMGFReader extends AbstractMSDataReader {

    /**
     * @return the mgfFile
     */
    public MgfFile getMgfFile() {
        return mgfFile;
    }

    /**
     * @param mgfFile the mgfFile to set
     */
    public void setMgfFile(MgfFile mgfFile) {
        this.mgfFile = mgfFile;
    }
    
    
    private MgfFile mgfFile;
    private File file;
    private SearchType searchType;
    
    public jmzReaderMGFReader(String path) throws Exception {
        super(path);
        this.file = new File(path);
        this.mgfFile = new MgfFile(this.file);
        this.searchType = this.mgfFile.getSearchType();
        if (this.searchType==null){
            this.searchType=SearchType.MIS; // TODO: need to check whether data is for MS1 or MS2
        }
    }

    @Override
    public Sample getSample() throws Exception {
        String md5 = DigestUtils.md5Hex( new FileInputStream( this.file ) );
        
        Sample sample = new Sample();
        sample.setFilepath(this.file.getAbsolutePath());
        sample.setFilename(this.file.getName());
        sample.setMd5(md5);
        sample.setRegistrationDate(new Timestamp(System.currentTimeMillis()));
        
        if(this.mgfFile.getInstrument() != null){
            //sample.getInstrumentAnalyzer( this.mgfFile.getInstrument());
            sample.setInstrumentAnalyzer( this.mgfFile.getInstrument());
        }
        
        return sample;
        
    }

    @Override
    public List<Integer> getSpectrumIds() throws Exception {
        if (null == searchType){
            return new ArrayList<Integer>(){};
        }else switch (searchType) {
            case MIS:
                List<Integer> ids = new ArrayList<Integer>(this.mgfFile.getMs2QueryCount());
                
                //TODO: fix for PMF peaklist
                for(String sid : this.mgfFile.getSpectraIds()){
                    ids.add(Integer.parseInt(sid));
                }
                
                return ids;
            case PMF:
                return new ArrayList<Integer>(){ {add(1); } };
            default:
                return new ArrayList<Integer>(){};
        }
        
    }


    @Override
    public Spectrum getSpectrumById(Integer index) throws Exception {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Iterable<Pair<Spectrum, PointList>> getSpectraPointList(Sample sample, Map<String, Spectrum> precursorMap) throws Exception {
        if( (searchType==SearchType.MIS) || (searchType==SearchType.PMF) ){
            jmzReaderMGFSpectrumIterator mgfspecs = new jmzReaderMGFSpectrumIterator(this.mgfFile, sample);
            return mgfspecs;
        }else{
            return () -> new Iterator<Pair<Spectrum, PointList>>() {
                @Override
                public boolean hasNext() {
                    return false;
                }
                @Override
                public Pair<Spectrum, PointList> next() {
                    throw new UnsupportedOperationException("Not supported yet."); 
                }
            };
        }
    }

    @Override
    public Integer getNumOfSpectra() throws Exception {
        //TODO: fix for PMF peaklist
        
        if (null == searchType){
            return 0;
        }else switch (searchType) {
            case MIS:
                return this.mgfFile.getMs2QueryCount();
            case PMF:
                return 1;
            default:
                return 0;
        }
    }

    @Override
    public PointList getPointList(Integer index) throws Exception {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<Integer> getChromatogramIds() throws Exception {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Chromatogram getChromatogram(Integer index) throws Exception {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Iterable<Pair<Chromatogram, PointList>> getChromatograms(Sample sample) throws Exception {
        return new Iterable<Pair<Chromatogram, PointList>>() {

            public Iterator<Pair<Chromatogram, PointList>> iterator() {

                return new Iterator<Pair<Chromatogram, PointList>>() {
                    @Override
                    public boolean hasNext() {
                        return false;
                    }

                    @Override
                    public Pair<Chromatogram, PointList> next() {
                        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
                    }
                };
            }
        };
    }

    @Override
    public void close() throws Exception {
         // no operation.
    }
    
    
    
    
    
}
