package ninja.mspp.service;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.querydsl.core.types.dsl.BooleanExpression;

import javafx.scene.control.ProgressIndicator;
import ninja.mspp.model.dataobject.DrawPoint;
import ninja.mspp.model.dataobject.FastDrawData;
import ninja.mspp.model.dataobject.Point;
import ninja.mspp.model.dataobject.XYData;
import ninja.mspp.model.entity.Chromatogram;
import ninja.mspp.model.entity.DrawElementList;
import ninja.mspp.model.entity.PointList;
import ninja.mspp.model.entity.QChromatogram;
import ninja.mspp.model.entity.QDrawElementList;
import ninja.mspp.model.entity.QSpectrum;
import ninja.mspp.model.entity.Sample;
import ninja.mspp.model.entity.Spectrum;
import ninja.mspp.repository.ChromatogramRepository;
import ninja.mspp.repository.DrawElementListRepository;
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
	private DrawElementListRepository drawRepository;

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
		QSpectrum qSpectrum = QSpectrum.spectrum;
		BooleanExpression expression = qSpectrum.sample.eq( sample );
		List< Spectrum > spectra = new ArrayList< Spectrum >();
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
		QChromatogram qChromatogram = QChromatogram.chromatogram;
		BooleanExpression expression = qChromatogram.sample.eq( sample );
		List< Chromatogram > chromatograms = new ArrayList< Chromatogram >();
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
	 * finds fast draw data
	 * @param pointListId point list ID
	 * @return fast draw data
	 */
	public FastDrawData findFastDrawdata( Long pointListId ) {
		QDrawElementList qList = QDrawElementList.drawElementList;
		BooleanExpression expression = qList.pointListId.eq( pointListId );

		List< DrawElementList > array = new ArrayList< DrawElementList >();

		for( DrawElementList list : this.drawRepository.findAll( expression ) ) {
			array.add( list );
		}

		FastDrawData data = new FastDrawData( array );
		return data;
	}

	/**
	 * registers raw data
	 * @param sampleObject sample object
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

		List< DrawElementList >  drawList = this.createDrawElementList( pointList );
		for( DrawElementList draw : drawList ) {
			this.drawRepository.save( draw );
		}

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
	 * creates draw element list
	 * @param pointList point list
	 * @return draw element list
	 */
	private List< DrawElementList > createDrawElementList( PointList pointList ) throws Exception {
		List< DrawElementList > array = new ArrayList< DrawElementList >();

		List< DrawPoint > points = this.createFirstDrawPoints( pointList );

		DrawElementList list  = new DrawElementList();
		list.setPointListId( pointList.getId() );
		list.setLevel( 0 );
		this.setDrawDataBlob( list, points );
		array.add( list );

		List< DrawPoint> prevPoints = null;
		double range = FastDrawData.MIN_RANGE;
		for( int level = 1; level <= FastDrawData.MAX_LEVEL; level++ ) {
			prevPoints = points;
			points = new ArrayList< DrawPoint >();

			list = new DrawElementList();
			list.setPointListId( pointList.getId() );
			list.setLevel( level );

			DrawPoint currentPoint = null;
			int currentIndex = -1;

			for( DrawPoint point : prevPoints ) {
				double x = point.getX();
				double maxY = point.getMaxY();
				double minY = point.getMinY();
				double leftY = point.getLeftY();
				double rightY = point.getRightY();

				int index = ( int )Math.round( x / range );

				if( index > currentIndex ) {
					currentPoint = new DrawPoint();
					currentPoint.setX( range * ( double )index );
					currentPoint.setMinY( minY );
					currentPoint.setMaxY( maxY );
					currentPoint.setLeftY( leftY );
					currentPoint.setRightY( rightY );

					points.add( currentPoint );
					currentIndex = index;
				}
				else {
					if( maxY > currentPoint.getMaxY() ) {
						currentPoint.setMaxY( maxY );
					}
					if( minY < currentPoint.getMinY() ) {
						currentPoint.setMinY( minY );
					}
					currentPoint.setRightY( rightY );;
				}
			}

			this.setDrawDataBlob( list, points );
			array.add( list );

			range = range * 2.0;
		}

		return array;
	}

	/**
	 * create
	 * @param pointList
	 * @return
	 */
	private List< DrawPoint > createFirstDrawPoints( PointList pointList ) {
		List< DrawPoint > points = new ArrayList< DrawPoint >();
		XYData xyData = pointList.getXYData();
		for( Point< Double > xy : xyData ) {
			double x = xy.getX();
			double y = xy.getY();

			DrawPoint point = new DrawPoint();
			point.setX( x );
			point.setMinY( y );
			point.setMaxY( y );
			point.setLeftY( y );
			point.setRightY( y );

			points.add( point );
		}
		return points;
	}

	/**
	 * sets blob data to draw element list
	 * @param list draw element list to be set.
	 * @param points draw points
	 */
	private void setDrawDataBlob( DrawElementList list, List< DrawPoint > points ) throws Exception {
		int count = points.size();
		list.setDataLength( count );

		ByteArrayOutputStream xCache = new ByteArrayOutputStream();
		ByteArrayOutputStream maxCache = new ByteArrayOutputStream();
		ByteArrayOutputStream minCache = new ByteArrayOutputStream();
		ByteArrayOutputStream leftCache = new ByteArrayOutputStream();
		ByteArrayOutputStream rightCache = new ByteArrayOutputStream();

		DataOutputStream xOut = new DataOutputStream( xCache );
		DataOutputStream maxOut = new DataOutputStream( maxCache );
		DataOutputStream minOut = new DataOutputStream( minCache );
		DataOutputStream leftOut = new DataOutputStream( leftCache );
		DataOutputStream rightOut = new DataOutputStream( rightCache );

		for( DrawPoint point : points ) {
			xOut.writeDouble( point.getX() );
			minOut.writeDouble( point.getMinY() );
			maxOut.writeDouble( point.getMaxY() );
			leftOut.writeDouble( point.getLeftY() );
			rightOut.writeDouble( point.getRightY() );
		}

		xOut.close();
		minOut.close();
		maxOut.close();
		leftOut.close();
		rightOut.close();

		xCache.close();
		minCache.close();
		maxCache.close();
		leftCache.close();
		rightCache.close();

		list.setxArray( xCache.toByteArray() );
		list.setMinYArray( minCache.toByteArray() );
		list.setMaxYArray( maxCache.toByteArray() );
		list.setLeftYArray( leftCache.toByteArray() );
		list.setRightYArray( rightCache.toByteArray() );
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

		List< DrawElementList >  drawList = this.createDrawElementList( pointList );
		for( DrawElementList draw : drawList ) {
			this.drawRepository.save( draw );
		}

		chromatogram.setSample( sample );
		chromatogram.setName( name );
		chromatogram.setPointListId( pointList.getId() );
		chromatogram.setMz( mz );

		return chromatogram;
	}


}
