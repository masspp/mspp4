/**
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
 * @author Mass++ Users Group
 * @author Satoshi Tanaka
 * @since 2018-06-01 05:51:10+09:00
 *
 * Copyright (c) 2018, Mass++ Users Group
 * All rights reserved.
 */
package ninja.mspp.service;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.querydsl.core.types.dsl.BooleanExpression;

import javafx.scene.control.ProgressIndicator;
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
import umich.ms.datatypes.lcmsrun.LCMSRunInfo;
import umich.ms.datatypes.lcmsrun.MsSoftware;
import umich.ms.datatypes.scan.IScan;
import umich.ms.datatypes.scan.props.Instrument;
import umich.ms.datatypes.scan.props.Polarity;
import umich.ms.datatypes.scan.props.PrecursorInfo;
import umich.ms.datatypes.spectrum.ISpectrum;
import umich.ms.fileio.filetypes.mzml.MZMLFile;
import umich.ms.fileio.filetypes.mzml.MZMLIndex;
import umich.ms.fileio.filetypes.mzml.MZMLIndexElement;

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
		List< Spectrum > spectra = new ArrayList< Spectrum >();
		if( sample == null ) {
			return spectra;
		}
		QSpectrum qSpectrum = QSpectrum.spectrum;
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
		List< Chromatogram > chromatograms = new ArrayList< Chromatogram >();
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
         * @param path
         * @param progress
         * @param start
         * @param end
         * @throws Exception 
         */
	public void register( String path, ProgressIndicator progress, double start, double end )  throws Exception {
		progress.setProgress( start );
		System.out.println( String.format( "Registering Sample [%s].....", path ) );

		MZMLFile mzml = new MZMLFile( path );
		mzml.setNumThreadsForParsing( null );
		Sample sample = this.createSampleObject( mzml, path );
		sample = this.sampleRepository.save( sample );

		MZMLIndex index = mzml.fetchIndex();
		Map< Integer, MZMLIndexElement > map = index.getMapByRawNum();
		Map< String, Spectrum > spectrumMap = new HashMap< String, Spectrum >();
		List< Integer > scanNumberList = new ArrayList< Integer >( map.keySet() );
		scanNumberList.sort(
			( scan1, scan2 ) -> {
				return ( scan1 - scan2 );
			}
		);
		int total = scanNumberList.size();
		int count = 0;

		List< Point< Double > > ticPoints = new ArrayList< Point< Double > >();

		for( Integer scanNumber: scanNumberList ) {
			MZMLIndexElement element = map.get( scanNumber );
			String id = element.getId();
			System.out.println( String.format( "    Registering Spectrum [%s].....", id ) );

			IScan scan = null;
			try {
				scan = mzml.parseScan( element.getNumber(), true );
			}
			catch( Exception e )  {
				scan = mzml.parseScan( element.getNumber(), false );
			}
			Spectrum spectrum = this.createSpectrum( scan, sample, id, spectrumMap  );
			spectrumMap.put( id, spectrum );
			spectrum = this.spectrumRepository.save( spectrum );

			Point< Double > point = new Point< Double >( spectrum.getStartRt(), spectrum.getTic() );
			ticPoints.add( point );

			double t = ( double )count / ( double ) total;
			double position = ( 1.0 - t ) * start + t * end;
			progress.setProgress( position );
			count++;
		}

		if( ticPoints.size() > 0 ) {
			Chromatogram chromatogram = this.createChromatogram( sample, ticPoints, "TIC", null );
			chromatogram = this.chromatogramRepository.save( chromatogram );
		}

		mzml.close();
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
	 * creates sample object
	 * @param mzml mzml object
	 * @param path file path
	 * @return sample object
	 * @throws Exception
	 */
	private Sample createSampleObject( MZMLFile mzml, String path ) throws Exception {
		File file = new File( path );
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

	/**
	 * creates spectrum
	 * @param scan scan
	 * @param sample sample
	 * @return spectrum
	 */
	private Spectrum createSpectrum( IScan scan, Sample sample, String id, Map< String, Spectrum > spectrumMap ) throws Exception {
		Spectrum spectrum = new Spectrum();
		Polarity polarity = scan.getPolarity();

		ISpectrum points = scan.getSpectrum();
		PointList pointList = this.createPointList( points );
		pointList = this.pointRepository.save( pointList );

		spectrum.setSpectrumId( id );
		spectrum.setName( id );
		spectrum.setSample( sample );
		spectrum.setStartRt( scan.getRt() );
		spectrum.setEndRt( scan.getRt() );
		spectrum.setBpi( scan.getBasePeakIntensity() );
		spectrum.setBpm( scan.getBasePeakMz() );
		spectrum.setTic( scan.getTic() );
		spectrum.setCentroidMode( scan.isCentroided() ? 1 : 0 );
		spectrum.setPointListId( pointList.getId() );
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

		return spectrum;
	}

	/**
	 * creates point list
	 * @param points points
	 * @return point list object
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
