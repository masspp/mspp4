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
package ninja.mspp.plugin.io.file.mzml_msftbx_wrapper;


import java.io.File;
import java.io.FileInputStream;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Map;
import java.util.Iterator;

import org.apache.commons.codec.digest.DigestUtils;

//MSPP4
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.lang3.tuple.Pair;

//MSFTBX
import umich.ms.datatypes.LCMSDataSubset;
import umich.ms.datatypes.lcmsrun.LCMSRunInfo;
import umich.ms.datatypes.lcmsrun.MsSoftware;
import umich.ms.datatypes.scan.StorageStrategy;
import umich.ms.datatypes.scan.props.Instrument;
import umich.ms.datatypes.scancollection.impl.ScanCollectionDefault;
import umich.ms.fileio.exceptions.FileParsingException;
import umich.ms.fileio.filetypes.mzml.MZMLFile;
import umich.ms.fileio.filetypes.mzml.MZMLIndex;
import umich.ms.fileio.filetypes.mzml.MZMLIndexElement;

//mspp
import ninja.mspp.io.msdatareader.AbstractMSDataReader;
import ninja.mspp.model.entity.Chromatogram;
import ninja.mspp.model.entity.PointList;
import ninja.mspp.model.entity.Sample;
import ninja.mspp.model.entity.Spectrum;

/**
 *
 * @author masakimu
 */
public class MsftbxMzMLReader extends AbstractMSDataReader{
    
    

    /**
     * @return the mzml
     */
    public MZMLFile getMzml() {
        return mzml;
    }

    /**
     * @param mzml the mzml to set
     */
    public void setMzml(MZMLFile mzml) {
        this.mzml = mzml;
    }
    
    private MZMLFile mzml;
    
    public MsftbxMzMLReader(String path) throws Exception {
        super(path);
        this.mzml = new MZMLFile( this.getPath() );
    }

    @Override
    public Sample getSample() throws Exception {
        
        mzml.setNumThreadsForParsing( null );
        
        File file = new File( this.getPath() );
        String md5 = DigestUtils.md5Hex( new FileInputStream( file ) );
        LCMSRunInfo run = mzml.fetchRunInfo();
        Instrument instrument = null;
        if( run != null ) {
                instrument = run.getDefaultInstrument();
        }

        Sample sample = new Sample();
        sample.setFilepath( file.getAbsolutePath() );
        sample.setFilename( file.getName() );
        sample.setMd5( md5 );
        sample.setRegistrationDate( new Timestamp( System.currentTimeMillis() ) );
        if( instrument != null ) {
                sample.setInstrumentVendor( instrument.getManufacturer() );
                sample.setInstrumentModel( instrument.getModel() );
                sample.setInstrumentAnalyzer( instrument.getAnalyzer() );
                sample.setIonization( instrument.getIonisation() );;
        }

        MsSoftware software = null;
        if( run != null ) {
                for( MsSoftware currentSoftware : run.getSoftware() ) {
                        if( software == null ) {
                                software = currentSoftware;
                        }
                }
        }

        if( software != null ) {
                sample.setAcquisitionsoftware( software.name );
        }
                
        return sample;
    }

    @Override
    public List<Integer> getSpectrumIds() throws Exception { 
        MZMLIndex index = this.mzml.getIndex();
        Map<Integer, MZMLIndexElement> map= index.getMapByRawNum();
        return new ArrayList<>(map.keySet());
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Spectrum getSpectrumById(Integer index) throws Exception {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
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
    public Iterable<Pair<Spectrum, PointList>> getSpectraPointList(Sample sample, Map<String, Spectrum> spectrumMap) throws Exception {
        ScanCollectionDefault scans = new ScanCollectionDefault();
        scans.setDefaultStorageStrategy(StorageStrategy.SOFT);
        scans.isAutoloadSpectra(true);
        scans.setDataSource(this.mzml);
        mzml.setNumThreadsForParsing(null);
        try {
            scans.loadData(LCMSDataSubset.WHOLE_RUN);
        } catch (FileParsingException ex) {
            Logger.getLogger(MsftbxMzMLReader.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        MZMLIndex index = mzml.fetchIndex();
        
        MsftbxMzMLSpectrumIterator spectrumiterator = new MsftbxMzMLSpectrumIterator(scans.getMapNum2scan(), index.getMapByRawNum(), sample, spectrumMap );
        return spectrumiterator;
    }

    @Override
    public Iterable<Pair<Chromatogram, PointList>> getChromatograms(Sample sample) throws Exception {
        return new Iterable<Pair<Chromatogram, PointList>>(){
            
            public Iterator<Pair<Chromatogram, PointList>>  iterator(){
                
                return new Iterator<Pair<Chromatogram, PointList>>(){
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
    public Integer getNumOfSpectra() throws Exception {
        MZMLIndex index  = this.mzml.fetchIndex();
        Map<Integer, MZMLIndexElement> map= index.getMapByRawNum();
        return map.size();
    }
    
    @Override
    public void close() throws Exception {
        this.mzml.close();
    }
    
    
    
}
