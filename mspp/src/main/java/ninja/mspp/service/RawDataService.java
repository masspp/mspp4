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
 * @author Satoshi Tanaka
 * @since 2019
 *
 * Copyright (c) 2019 Satoshi Tanaka
 * All rights reserved.
 */
package ninja.mspp.service;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.tuple.Pair;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.querydsl.core.types.dsl.BooleanExpression;

import javafx.scene.control.ProgressIndicator;

import ninja.mspp.io.msdatareader.AbstractMSDataReader;
import ninja.mspp.model.dataobject.Point;
import ninja.mspp.model.dataobject.XYData;
import ninja.mspp.model.entity.Chromatogram;
import ninja.mspp.model.entity.GroupChromatogram;
import ninja.mspp.model.entity.GroupSpectrum;
import ninja.mspp.model.entity.PointList;
import ninja.mspp.model.entity.QChromatogram;
import ninja.mspp.model.entity.QGroupChromatogram;
import ninja.mspp.model.entity.QGroupSample;
import ninja.mspp.model.entity.QGroupSpectrum;
import ninja.mspp.model.entity.QSpectrum;
import ninja.mspp.model.entity.Sample;
import ninja.mspp.model.entity.Spectrum;
import ninja.mspp.repository.ChromatogramRepository;
import ninja.mspp.repository.GroupChromatogramRepository;
import ninja.mspp.repository.GroupSampleRepository;
import ninja.mspp.repository.GroupSpectrumRepository;
import ninja.mspp.repository.PointListRepository;
import ninja.mspp.repository.SampleRepository;
import ninja.mspp.repository.SpectrumRepository;

@Service
public class RawDataService {


	@Autowired
	private SampleRepository sampleRepository;

	@Autowired
	private SpectrumRepository spectrumRepository;

	@Autowired
	private ChromatogramRepository chromatogramRepository;

	@Autowired
	private PointListRepository pointRepository;

	@Autowired
	private GroupSampleRepository groupSampleRepository;

	@Autowired
	private GroupSpectrumRepository groupSpectrumRepository;

	@Autowired
	private GroupChromatogramRepository groupChromatogramRepository;

	/**
	 * finds all samples
	 * @return samples
	 */
	public List< Sample > findSamples() {
		List< Sample > list = this.sampleRepository.findAll();
		return list;
	}

	/**
	 * saves sample
	 * @param sample sample
	 */
	public void saveSample( Sample sample ) {
		this.sampleRepository.save( sample );
	}

	/**
	 * finds spectra from sample
	 * @param sample sample
	 * @return spectra
	 */
	public List< Spectrum > findSpectra( Sample sample ) {
		List< Spectrum > spectra = new ArrayList<>();
		if( sample == null ) {
			return spectra;
		}
		QSpectrum qSpectrum = QSpectrum.spectrum;
                // TODO: have to sort spectra here  : Masaki Murase 2020.5.8
                //      since I don't sort scans when loaded scans is saved into database from data file at data input adaptor.
		BooleanExpression expression = qSpectrum.sample.eq( sample );
		for( Spectrum spectrum : this.spectrumRepository.findAll( expression ) ) {
			spectra.add( spectrum );
		}
		return spectra;
	}

	/**
	 * finds chromatograms from sample
	 * @param sample sample
	 * @return chromatograms
	 */
	public List< Chromatogram > findChromatograms( Sample sample ) {
		List< Chromatogram > chromatograms = new ArrayList<>();
		if( sample == null ) {
			return chromatograms;
		}
		QChromatogram qChromatogram = QChromatogram.chromatogram;
		BooleanExpression expression = qChromatogram.sample.eq( sample );
		for( Chromatogram chromatogram : this.chromatogramRepository.findAll( expression ) ) {
			chromatograms.add( chromatogram );
		}
		return chromatograms;
	}

	/**
	 * finds data points
	 * @param pointListId point list ID
	 * @return xy data
	 */
	public XYData findDataPoints( Long pointListId ) {
		Optional< PointList > optional = this.pointRepository.findById( pointListId );
		XYData points = null;
		if( optional.isPresent() ) {
			PointList list = optional.get();
			points = list.getXYData();
		}
		return points;
	}

        /**
         * registers raw data
         * 
         * @param reader object
         * @param progress
         * @param start
         * @param end
         * @throws Exception 
         */
	public void register( AbstractMSDataReader reader, ProgressIndicator progress, double start, double end )  throws Exception {
		progress.setProgress( start );
                if (reader!=null){
                    
                    Sample sample = reader.getSample();
                    sample = this.sampleRepository.save( sample );

                    Map< String, Spectrum > spectrumMap = new HashMap< >();
                    int total = reader.getNumOfSpectra();
                    int count = 0;

                    List< Point< Double > > ticPoints = new ArrayList<>();

                    // TODO: order of  specpiont may not be sorted by scan id. need to check order-dependent GUI behavior.
                    for( Pair<Spectrum, PointList> specpoint: reader.getSpectraPointList(sample, spectrumMap) ) {

                        PointList pointList = this.pointRepository.save(specpoint.getRight());  // save spectrum data points into DB

                        Spectrum spectrum = specpoint.getLeft(); 
                        spectrum.setPointListId( pointList.getId() );
                        spectrum = this.spectrumRepository.save( spectrum );
                        String id = Long.toString( spectrum.getId());                
                        System.out.println( String.format( "    Registering Spectrum [%s].....", id ) );
                        spectrumMap.put( id, spectrum ); 

                        Point< Double > point = new Point<>( spectrum.getStartRt(), spectrum.getTic() );
                        ticPoints.add( point );

                        double t = ( double )count / ( double ) total;
                        double position = ( 1.0 - t ) * start + t * end;
                        progress.setProgress( position );
                        count++;
                    }

                    List<Chromatogram> chromatograms = new ArrayList<>();
                    for( Pair<Chromatogram, PointList> chrpoint : reader.getChromatograms(sample)){
                        chromatograms.add(chrpoint.getLeft());
                        //TODO: implement to save PointList of chromatogram
                    }

                    if(  chromatograms.isEmpty() ){

                        if( ticPoints.size() > 0 ) {
                                Chromatogram chromatogram = this.createChromatogram( sample, ticPoints, "TIC", null );
                                this.chromatogramRepository.save( chromatogram );
                        }
                    }

                    reader.close();
                }
                progress.setProgress( end );
                
	}

	/**
	 * saves chromatogram
	 * @param sample sample
	 * @param points points
	 * @param name name
	 * @param mz m/z
	 * @return chromatogram
	 * @throws Exception
	 */
	public Chromatogram saveChromatogram( Sample sample, List< Point< Double > > points, String name, Double mz ) throws Exception {
		Chromatogram chromatogram = this.createChromatogram( sample, points, name, mz );
		this.chromatogramRepository.save( chromatogram );
		return chromatogram;
	}


	/**
	 * creates point list
	 * @param xArray x array
	 * @param yArray y array
	 * @return point list
	 * @throws Exception
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

	/**
	 * creates chromatogram
	 * @param sample sample
	 * @param points points
	 * @param name name
	 * @param mz m/z
	 * @return chromatogram object
	 */
	private Chromatogram createChromatogram( Sample sample, List< Point< Double > > points, String name, Double mz ) throws Exception {
		Chromatogram chromatogram =  new Chromatogram();

		double[] rts = new double[ points.size() ];
		double[] ints = new double[ points.size() ];
		for( int i = 0; i < points.size(); i++ ) {
			Point< Double > point = points.get( i );
			rts[ i ] = point.getX();
			ints[ i ] = point.getY();
		}

		PointList pointList = this.createPointList( rts, ints );
		pointList = this.pointRepository.save( pointList );

		chromatogram.setSample( sample );
		chromatogram.setName( name );
		chromatogram.setPointListId( pointList.getId() );
		chromatogram.setMz( mz );

		return chromatogram;
	}

	/**
	 * deletes sample
	 * @param sample
	 */
	public void deleteSample( Sample sample ) {
		if( sample == null ) {
			return;
		}

		Set< Long > pointIdSet = new HashSet< Long >();

		QGroupSpectrum qGrouopSpectrum = QGroupSpectrum.groupSpectrum;
		BooleanExpression expression = qGrouopSpectrum.spectrum.sample.eq( sample );
		Iterable< GroupSpectrum > groupSpectra = this.groupSpectrumRepository.findAll( expression );
		for( GroupSpectrum groupSpectrum : groupSpectra ) {
			pointIdSet.add( groupSpectrum.getPointListId() );
		}
		this.groupSpectrumRepository.deleteAll( groupSpectra );

		QGroupChromatogram qGroupChromatogram = QGroupChromatogram.groupChromatogram;
		expression = qGroupChromatogram.chromatogram.sample.eq( sample );
		Iterable< GroupChromatogram > groupChromatograms = this.groupChromatogramRepository.findAll( expression );
		for( GroupChromatogram groupChromatogram : groupChromatograms ) {
			pointIdSet.add( groupChromatogram.getPointListId() );
		}
		this.groupChromatogramRepository.deleteAll( groupChromatograms );

		QGroupSample qGroupSample = QGroupSample.groupSample;
		expression = qGroupSample.sample.eq( sample );
		this.groupSampleRepository.deleteAll( this.groupSampleRepository.findAll( expression ) );

		QSpectrum qSpectrum = QSpectrum.spectrum;
		expression = qSpectrum.sample.eq( sample );
		Iterable< Spectrum > spectra = this.spectrumRepository.findAll( expression );
		for( Spectrum spectrum : spectra ) {
			pointIdSet.add( spectrum.getPointListId() );
		}
		this.spectrumRepository.deleteAll( spectra );

		QChromatogram qChromatogram = QChromatogram.chromatogram;
		expression = qChromatogram.sample.eq( sample );
		Iterable< Chromatogram > chromatograms = this.chromatogramRepository.findAll( expression );
		for( Chromatogram chromatogram : chromatograms ) {
			pointIdSet.add( chromatogram.getPointListId() );
		}
		this.chromatogramRepository.deleteAll( chromatograms );

		for( Long pointListId : pointIdSet ) {
			Optional< PointList > points = this.pointRepository.findById( pointListId );
			if( points.isPresent() ) {
				this.pointRepository.delete( points.get() );
			}
		}

		this.sampleRepository.delete( sample );
	}


}
