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

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.lang3.tuple.Pair;

import umich.ms.datatypes.scan.IScan;
import umich.ms.datatypes.spectrum.ISpectrum;
import umich.ms.datatypes.scan.props.Polarity;
import umich.ms.datatypes.scan.props.PrecursorInfo;

import ninja.mspp.model.dataobject.Point;
import ninja.mspp.model.entity.Spectrum;
import ninja.mspp.model.entity.Sample;
import ninja.mspp.model.entity.PointList;
import umich.ms.fileio.filetypes.mzml.MZMLIndexElement;




/**
 *
 * @author masakimu
 */
public class MsftbxMzMLSpectrumIterator implements Iterable<Pair<Spectrum,PointList>> {
    
    private final  TreeMap<Integer, IScan> scanmap ;
    Sample sample;
    Map< String, Spectrum > spectrumMap;
    Map<Integer, MZMLIndexElement> indexMap;
    
    public MsftbxMzMLSpectrumIterator(TreeMap<Integer, IScan> scanmap, Map<Integer, MZMLIndexElement> indexmap, Sample sample, Map< String, Spectrum > spectrumMap){
        this.scanmap = scanmap;
        this.sample = sample;
        this.spectrumMap = spectrumMap;
        this.indexMap = indexmap;
    }

    @Override
    public Iterator<Pair<Spectrum,PointList>> iterator() {
       
        
        return new Iterator<Pair<Spectrum,PointList>>(){
            
            private final Iterator<IScan> scans = scanmap.values().iterator();
                    
            @Override
            public boolean hasNext() {
                return scans.hasNext();
            }

            @Override
            public Pair<Spectrum, PointList> next() {

                IScan scan = scans.next();
                Spectrum spectrum = new Spectrum();
                Polarity polarity = scan.getPolarity();

                ISpectrum points = scan.getSpectrum();
                PointList pointList;
                try {
                    pointList = this.createPointList(points);
                } catch (Exception ex) {
                    Logger.getLogger(MsftbxMzMLSpectrumIterator.class.getName()).log(Level.SEVERE, null, ex);
                    return null;   // OK?
                }

                String id = indexMap.get(scan.getNum()-1).getId();
                spectrum.setSpectrumId(id);
                spectrum.setName(id);
                spectrum.setSample(sample);
                spectrum.setStartRt(scan.getRt());
                spectrum.setEndRt(scan.getRt());
                spectrum.setBpi(scan.getBasePeakIntensity());
                spectrum.setBpm(scan.getBasePeakMz());
                spectrum.setTic(scan.getTic());
                spectrum.setCentroidMode(scan.isCentroided() ? 1 : 0);
                spectrum.setMsStage(scan.getMsLevel());
                spectrum.setLowerMz(scan.getScanMzWindowLower());
                spectrum.setUpperMz(scan.getScanMzWindowUpper());
                spectrum.setMaxIntensity(pointList.getMaxY());
                if (polarity != null) {
                    spectrum.setPolarity(polarity.getSign());
                    
                }else{
                    spectrum.setPolarity(0);
                }

                // assumed that each precursor spectrum is ahead of child spectra in this iterator.
                // TODO: check this.
                if (spectrum.getMsStage()!=null && spectrum.getMsStage()>1 ){
                    PrecursorInfo precursor = scan.getPrecursor();
                    if (precursor != null) {
                        Spectrum parent = spectrumMap.get(precursor.getParentScanRefRaw());
                        if (parent != null) {
                            spectrum.setParentSpectrumId(parent.getId());
                        }
                        
                        Double precursorMz = precursor.getMzTargetMono();
                        if (precursorMz == null) {
                            precursorMz = precursor.getMzTarget();
                        }
                        spectrum.setPrecursor(precursorMz);
                    }
                }

                return Pair.of(spectrum, pointList);
            }
        
            /**
             * 
             * @param points
             * @return
             * @throws Exception 
             */
            private PointList createPointList( ISpectrum points ) throws Exception {
		if( points != null ) {
			return this.createPointList( points.getMZs(), points.getIntensities() );
		}
		else {
			return this.createPointList( new double[ 0 ], new double[ 0 ] );
		}
            }

            /**
             * 
             * @param xArray
             * @param yArray
             * @return
             * @throws Exception 
             * 
             * integrate with ChromatogramIterator.
             */
            private PointList createPointList( double[] xArray, double[] yArray ) throws Exception {
                PointList pointList = new PointList();

                int count = 0;
                if( xArray != null && yArray != null ) {
                        count = Math.min( xArray.length, yArray.length );
                }
                pointList.setDataLength( count );

                ByteArrayOutputStream xCache = new ByteArrayOutputStream();
                ByteArrayOutputStream yCache = new ByteArrayOutputStream();

                DataOutputStream xOut = new DataOutputStream( xCache );
                DataOutputStream yOut = new DataOutputStream( yCache );

                Double minX = null;
                Double maxX = null;
                Double minY = null;
                Double maxY = null;

                for( int i = 0; i < count; i++ ) {
                        double x = xArray[ i ];
                        double y = yArray[ i ];

                        xOut.writeDouble( x );
                        yOut.writeDouble( y );

                        if( minX == null || x < minX ) {
                                minX = x;
                        }
                        if( maxX == null || x > maxX ) {
                                maxX = x;
                        }
                        if( minY == null || y < minY ) {
                                minY = y;
                        }
                        if( maxY == null || y > maxY ) {
                                maxY = y;
                        }
                }

                xOut.close();
                yOut.close();
                xCache.close();
                yCache.close();

                pointList.setMinX( minX );
                pointList.setMaxX( maxX );
                pointList.setMinY( minY );
                pointList.setMaxY( maxY );
                pointList.setxArray( xCache.toByteArray() );
                pointList.setyArray( yCache.toByteArray() );

                return pointList;
            }
            
        };
    }

    
}