package ninja.mspp.service;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.querydsl.core.types.dsl.BooleanExpression;

import ninja.mspp.model.dataobject.GroupInfo;
import ninja.mspp.model.entity.Chromatogram;
import ninja.mspp.model.entity.Group;
import ninja.mspp.model.entity.GroupChromatogram;
import ninja.mspp.model.entity.GroupSample;
import ninja.mspp.model.entity.GroupSpectrum;
import ninja.mspp.model.entity.PeakAnnotation;
import ninja.mspp.model.entity.PeakPosition;
import ninja.mspp.model.entity.Project;
import ninja.mspp.model.entity.QChromatogram;
import ninja.mspp.model.entity.QGroup;
import ninja.mspp.model.entity.QPeakPosition;
import ninja.mspp.model.entity.Sample;
import ninja.mspp.model.entity.Spectrum;
import ninja.mspp.repository.ChromatogramRepository;
import ninja.mspp.repository.GroupChromatogramRepository;
import ninja.mspp.repository.GroupRepository;
import ninja.mspp.repository.GroupSampleRepository;
import ninja.mspp.repository.GroupSpectrumRepository;
import ninja.mspp.repository.PeakAnnotationRepository;
import ninja.mspp.repository.PeakPositionRepository;
import ninja.mspp.repository.ProjectRepository;
import umich.ms.fileio.filetypes.pepxml.PepXmlParser;
import umich.ms.fileio.filetypes.pepxml.jaxb.standard.MsmsRunSummary;
import umich.ms.fileio.filetypes.pepxml.jaxb.standard.NameValueType;
import umich.ms.fileio.filetypes.pepxml.jaxb.standard.SearchHit;
import umich.ms.fileio.filetypes.pepxml.jaxb.standard.SearchResult;
import umich.ms.fileio.filetypes.pepxml.jaxb.standard.SpectrumQuery;

@Service
@Transactional
public class ProjectService {
	@Autowired
	private ProjectRepository projectReposiroty;

	@Autowired
	private GroupRepository groupRepository;

	@Autowired
	private GroupSampleRepository groupSampleRepository;

	@Autowired
	private PeakPositionRepository positionRepository;

	@Autowired
	private PeakAnnotationRepository annotationRepository;

	@Autowired
	private ChromatogramRepository chromatogramRepository;

	@Autowired
	private GroupSpectrumRepository groupSpectrumRepository;

	@Autowired
	private GroupChromatogramRepository groupChromatogramRepository;

	/**
	 * constructor
	 */
	public ProjectService() {
	}

	/**
	 * finds all projects
	 * @return
	 */
	public List< Project > findAllProjects() {
		return this.projectReposiroty.findAll();
	}

	/**
	 * registers project
	 * @param name project name
	 * @param description project descritption
	 * @param groups project groups
	 */
	public void registerProject(
		String name,
		String description,
		List< GroupInfo > groups
	) {
		Project project = new Project();
		project.setName( name );
		project.setDescription( description );
		project.setRegistrationDate( new Timestamp( System.currentTimeMillis() ) );
		project = this.projectReposiroty.save( project );

		for( GroupInfo group : groups ) {
			this.registerGroup( project, group );
		}
	}

	/**
	 * registers group
	 * @param project project
	 * @param info group
	 */
	private void registerGroup( Project project, GroupInfo info ) {
		Group group = new Group();
		group.setProject( project );
		group.setName( info.getName() );
		group.setDescription( info.getDescription() );
		group.setColor( info.getColor().toString() );
		group = this.groupRepository.save( group );

		for( Sample sample : info.getSamples() ) {
			this.registerGroupSample( group, sample );
		}
	}

	/**
	 * registers group sample
	 * @param group group
	 * @param sample sample
	 */
	private void registerGroupSample( Group group, Sample sample ) {
		GroupSample groupSample = new GroupSample();
		groupSample.setGroup( group );
		groupSample.setSample( sample );
		groupSample = this.groupSampleRepository.save( groupSample );

		for( Spectrum spectrum : sample.getSpectras() ) {
			GroupSpectrum groupSpectrum = new GroupSpectrum();
			groupSpectrum.setGroupSample( groupSample );
			groupSpectrum.setSpectrum( spectrum );
			groupSpectrum.setPointListId( spectrum.getPointListId() );
			this.groupSpectrumRepository.save( groupSpectrum );
		}

		QChromatogram qChromatogram = QChromatogram.chromatogram;
		BooleanExpression expression = qChromatogram.sample.eq( sample );
		for( Chromatogram chromatogram : this.chromatogramRepository.findAll( expression ) ) {
			GroupChromatogram groupChromatogram = new GroupChromatogram();
			groupChromatogram.setGroupSample( groupSample );
			groupChromatogram.setChromatogram( chromatogram );
			groupChromatogram.setPointListId( chromatogram.getPointListId() );
			this.groupChromatogramRepository.save( groupChromatogram );
		}
	}

	/**
	 * imports pepxml file
	 * @param path file path
	 */
	public void importPepxml( Project project, String path ) throws Exception {
		InputStream stream = new BufferedInputStream( new FileInputStream( new File( path ) ) );
		Iterator< MsmsRunSummary > iterator = PepXmlParser.parse( stream );
		while( iterator.hasNext() ) {
			MsmsRunSummary summary = iterator.next();
			List< SpectrumQuery > queryList = summary.getSpectrumQuery();

			for( SpectrumQuery query : queryList ) {
				double rt = query.getRetentionTimeSec() / 60.0;
				double mz = query.getPrecursorNeutralMass();
				int charge = query.getAssumedCharge();
				mz = mz / ( double )charge;

				PeakPosition position = new PeakPosition();
				position.setRt( rt );
				position.setMz( mz );
				position.setProject( project );

				position = this.positionRepository.save( position );

				List< SearchResult > results = query.getSearchResult();
				PeakAnnotation annotation = null;
				for( SearchResult result : results ) {
					for( SearchHit hit : result.getSearchHit() ) {
						if( annotation == null ) {
							annotation = new PeakAnnotation();
							annotation.setPeakPosition( position );
							Double score = null;
							for( NameValueType scoreValue : hit.getSearchScore() ) {
								if( score == null ) {
									try {
										score = Double.parseDouble( scoreValue.getValueStr() );
									}
									catch( Exception e ) {
									}
								}
							}
							annotation.setScore( score );
							annotation.setProtein( hit.getProtein() );
							annotation.setDescription( hit.getProteinDescr() );
							annotation.setPeptide( hit.getPeptide() );

							annotation = this.annotationRepository.save( annotation );
						}
					}
				}
			}
		}
	}

	/**
	 * finds peak positions
	 * @param project project
	 * @return peak positions
	 */
	public List< PeakPosition > findPeakPositions( Project project ) {
		List< PeakPosition > positions = new ArrayList< PeakPosition >();

		QPeakPosition qPosition = QPeakPosition.peakPosition;
		BooleanExpression expression = qPosition.project.eq( project );

		for( PeakPosition position : this.positionRepository.findAll( expression ) ) {
			positions.add( position );
		}

		return positions;
	}


	/**
	 * finds groups
	 * @param project project
	 * @return groups
	 */
	public List< Group > findGroups( Project project ) {
		List< Group > groups = new ArrayList< Group >();

		QGroup qGroup = QGroup.group;
		BooleanExpression expression = qGroup.project.eq( project );

		for( Group group : this.groupRepository.findAll( expression ) ) {
			groups.add( group );
		}

		return groups;
	}
}
