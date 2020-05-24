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
                ArrayList<Point<Double>> xypoints = new ArrayList<>();
                IScan scan = scans.next();
                Spectrum spectrum = new Spectrum();
                Polarity polarity = scan.getPolarity();
                
                ISpectrum points = scan.getSpectrum();
		PointList pointList;
                try {
                    pointList = this.createPointList( points );
                } catch (Exception ex) {
                    Logger.getLogger(MsftbxMzMLSpectrumIterator.class.getName()).log(Level.SEVERE, null, ex);
                    return null;   // OK?
                }
		
                //String id = Integer.toString( );  // from 1?  TODO: check lator.
                String id = indexMap.get( scan.getNum()).getId();
		spectrum.setSpectrumId( id );
		spectrum.setName( id );
		spectrum.setSample( sample );
		spectrum.setStartRt( scan.getRt() );
		spectrum.setEndRt( scan.getRt() );
		spectrum.setBpi( scan.getBasePeakIntensity() );
		spectrum.setBpm( scan.getBasePeakMz() );
		spectrum.setTic( scan.getTic() );
		spectrum.setCentroidMode( scan.isCentroided() ? 1 : 0 );
		spectrum.setMsStage( scan.getMsLevel() );
		spectrum.setLowerMz( scan.getScanMzWindowLower() );
		spectrum.setUpperMz( scan.getScanMzWindowUpper() );
		spectrum.setMaxIntensity( pointList.getMaxY() );
		if( polarity != null ) {
			spectrum.setPolarity( polarity.getSign() );
		}

		PrecursorInfo precursor = scan.getPrecursor();
		if( precursor != null ) {
			Spectrum parent = spectrumMap.get( precursor.getParentScanRefRaw() );
			if( parent != null ) {
				spectrum.setParentSpectrumId( parent.getId() );
				Double precursorMz = precursor.getMzTargetMono();
				if( precursorMz == null ) {
					precursorMz = precursor.getMzTarget();
				}
				spectrum.setPrecursor( precursorMz );
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