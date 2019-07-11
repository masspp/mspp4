package ninja.mspp.service;

import java.io.File;
import java.io.FileInputStream;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.querydsl.core.types.dsl.BooleanExpression;

import javafx.scene.control.ProgressIndicator;
import ninja.mspp.model.dataobject.ChromatogramObject;
import ninja.mspp.model.dataobject.Point;
import ninja.mspp.model.dataobject.SampleObject;
import ninja.mspp.model.dataobject.SpectrumObject;
import ninja.mspp.model.dataobject.SpectrumObject.Polarity;
import ninja.mspp.model.entity.Chromatogram;
import ninja.mspp.model.entity.PointList;
import ninja.mspp.model.entity.QChromatogram;
import ninja.mspp.model.entity.Sample;
import ninja.mspp.model.entity.Spectrum;
import ninja.mspp.repository.ChromatogramRepository;
import ninja.mspp.repository.SampleRepository;
import ninja.mspp.repository.SpectrumRepository;

@Service
@Transactional
public class RawDataService {


	@Autowired
	private SampleRepository sampleRepository;

	@Autowired
	private SpectrumRepository spectrumRepository;

	@Autowired
	private ChromatogramRepository chromatogramRepository;

	@Autowired
	private PointsService pointsService;

	/**
	 * registers raw data
	 * @param sampleObject sample object
	 */
	@Transactional( propagation = Propagation.REQUIRES_NEW )
	public void register( SampleObject sampleObject, ProgressIndicator progress ) {
		progress.setProgress( -1.0 );
		Sample sample = this.saveSample( sampleObject );
		progress.setProgress( 0.0 );

		int spectrumCount = sampleObject.getNumberOfSpectra();
		int chromatogramCount = 0;
		for( ChromatogramObject chromatogramObject : sampleObject.getChromatograms() ) {
			if( chromatogramObject.getXYData() != null ) {
				chromatogramCount++;
			}
		}
		int total = spectrumCount + chromatogramCount + 1;
		if( chromatogramCount == 0 ) {
			total = total + 1;
		}

		int count = 0;
		List< Point< Double > > points = new ArrayList< Point< Double > >();
		Map< SampleObject, Integer > idMap = new HashMap< SampleObject, Integer >();
		for( SpectrumObject spectrumObject : sampleObject.getSpectra() ) {
			Spectrum spectrum = this.saveSpectrum( spectrumObject, sample, idMap );
			if( spectrum != null ) {
				Point< Double > point = new Point< Double >(
					spectrum.getStartRt(),
					spectrum.getTic()
				);
				points.add( point );
			}
			count++;
			double ratio = (double)count / (double)total;
			progress.setProgress( ratio );
		}

		if( chromatogramCount == 0 ) {
			this.saveTic( points, sample );
			count++;
			double ratio = (double)count / (double)total;
			progress.setProgress( ratio );
		}
		else {
			for( ChromatogramObject object : sampleObject.getChromatograms() ) {
				if( object.getXYData() != null ) {
					this.saveChromatogram( object, sample );
					count++;
					double ratio = (double)count / (double)total;
					progress.setProgress( ratio );
				}
			}
		}

		progress.setProgress( 1.0 );
	}

	/**
	 * saves chromatogram
	 * @param object
	 * @param sample
	 */
	private void saveChromatogram( ChromatogramObject object, Sample sample ) {
		Chromatogram chromatogram = new Chromatogram();
		List< Point< Double > > points = new ArrayList< Point< Double > >();

		for( Point< Double > chromatogramPoint : object.getXYData() ) {
			double x = chromatogramPoint.getX();
			double y = chromatogramPoint.getY();
			Point< Double > point = new Point< Double >( x, y );
			points.add( point );
		}

		PointList list = this.pointsService.savePoints( points, "RT", "Int." );

		chromatogram.setSample( sample );
		chromatogram.setPointListId( list.getId() );
		chromatogram.setMsStage( (int)Math.round( object.getMs_stage() ) );
		chromatogram.setName( object.getName() );
		chromatogram.setTitle( object.getTitle() );

		this.chromatogramRepository.save( chromatogram );
	}

	/**
	 * saves chromatogram
	 * @param points tic points
	 * @param sample sample
	 */
	private void saveTic( List< Point< Double > > points, Sample sample ) {
		PointList list = this.pointsService.savePoints( points,  "RT", "Int." );
		Chromatogram chromatogram = new Chromatogram();
		chromatogram.setName( "TIC" );
		chromatogram.setSample( sample );
		chromatogram.setPointListId( list.getId() );
		chromatogram.setTitle( "TIC" );
		this.chromatogramRepository.save( chromatogram );

	}

	/**
	 * saves sample
	 * @param sampleObject
	 * @return sample
	 */
	private Sample saveSample( SampleObject sampleObject ) {
		Sample sample = new Sample();
		sample.setName( sampleObject.getName() );
		sample.setFilepath( sampleObject.getFilePath() );
		sample.setFilename( sampleObject.getFileName() );
		sample.setInstrumentvendor( sampleObject.getInstrumentvendor() );
		sample.setRegistrationDate( new Timestamp( System.currentTimeMillis() ) );
		sample.setAcquisitionsoftware( sampleObject.getAcquisitionsoftware() );

		try {
			String md5 = DigestUtils.md5Hex( new FileInputStream( new File( sampleObject.getFilePath() ) ) );
			sample.setMd5( md5 );
		}
		catch( Exception e ) {
			e.printStackTrace();
		}

		sample = this.sampleRepository.save( sample );
		return sample;
	}

	/**
	 * saves spectrum
	 * @param spectrumObject spectrum object
	 * @return spectrum
	 */
	@SuppressWarnings("unlikely-arg-type")
	private Spectrum saveSpectrum( SpectrumObject spectrumObject, Sample sample, Map< SampleObject, Integer > idMap ) {
		List< Point< Double > > points = new ArrayList< Point< Double > >();
		long time0 = System.currentTimeMillis();
		for( ninja.mspp.model.dataobject.Point< Double > spectrumPoint : spectrumObject.getXYData().getPoints() ) {
			Point< Double > point = new Point< Double >( spectrumPoint.getX(), spectrumPoint.getY() );
			points.add( point );
		}
		long time1 = System.currentTimeMillis();
		System.out.println( "Getting Spectrum Time: " + ( time1 - time0 ) );
		PointList list = this.pointsService.savePoints( points, "m/z", "Int." );
		long time2 = System.currentTimeMillis();
		System.out.println( "Registering Points Time: " + ( time2 - time1 ) );

		Spectrum spectrum = new Spectrum();
		spectrum.setSample( sample );
		spectrum.setBpm( spectrumObject.getBasepeakMz() );
		spectrum.setBpi( spectrumObject.getMaxY() );
		spectrum.setCentroidMode( spectrumObject.IsCentroid() ? 1 : 0 );
		spectrum.setEndRt( spectrumObject.getRt() );
		spectrum.setStartRt( spectrumObject.getRt() );
		spectrum.setMsStage( spectrumObject.getMsStage() );
		spectrum.setName( spectrumObject.getName() );
		spectrum.setPointListId( list.getId() );
		spectrum.setTic( spectrumObject.getTotalintensity() );
		spectrum.setTitle( spectrumObject.getTitle() );

		if( spectrumObject.getPolarity() == Polarity.Positive ) {
			spectrum.setPolarity( 1 );
		}
		else if( spectrumObject.getPolarity() == Polarity.Negative ) {
			spectrum.setPolarity( -1 );
		}

		SpectrumObject parent = spectrumObject.getParent();
		if( parent != null ) {
			spectrum.setParentSpectrumId( idMap.get( parent ) );
		}

		spectrum = this.spectrumRepository.save( spectrum );
		long time3 = System.currentTimeMillis();
		System.out.println( "Registerging Spectrum Time: " + ( time3 - time2 ) );
		return spectrum;
	}

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
	 * finds chromatograms from sample
	 * @param sample sample
	 * @return chromatograms
	 */
	public List< Chromatogram > findChromatograms( Sample sample ) {
		QChromatogram qChromatogram = QChromatogram.chromatogram;
		BooleanExpression expression = qChromatogram.sample.id.eq( sample.getId() );
		List< Chromatogram > chromatograms = new ArrayList< Chromatogram >();
		for( Chromatogram chromatogram : this.chromatogramRepository.findAll( expression ) ) {
			chromatograms.add( chromatogram );
		}
		return chromatograms;
	}
}
