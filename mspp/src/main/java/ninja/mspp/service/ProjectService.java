package ninja.mspp.service;

import java.sql.Timestamp;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ninja.mspp.model.dataobject.GroupInfo;
import ninja.mspp.model.entity.Group;
import ninja.mspp.model.entity.GroupSample;
import ninja.mspp.model.entity.Project;
import ninja.mspp.model.entity.Sample;
import ninja.mspp.repository.GroupRepository;
import ninja.mspp.repository.GroupSampleRepository;
import ninja.mspp.repository.ProjectRepository;

@Service
public class ProjectService {
	@Autowired
	private ProjectRepository projectReposiroty;

	@Autowired
	private GroupRepository groupRepository;

	@Autowired
	private GroupSampleRepository groupSampleRepository;

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
			GroupSample groupSample = new GroupSample();
			groupSample.setGroup( group );
			groupSample.setSample( sample );
			groupSample = this.groupSampleRepository.save( groupSample );
		}
	}
}
