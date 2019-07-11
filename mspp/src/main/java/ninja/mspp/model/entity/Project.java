package ninja.mspp.model.entity;

import java.io.Serializable;
import javax.persistence.*;
import java.sql.Timestamp;
import java.util.List;


/**
 * The persistent class for the PROJECTS database table.
 * 
 */
@Entity
@Table(name="PROJECTS")
@NamedQuery(name="Project.findAll", query="SELECT p FROM Project p")
public class Project implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private int id;

	private String description;

	private String name;

	@Column(name="REGISTRATION_DATE")
	private Timestamp registrationDate;

	//bi-directional many-to-one association to Group
	@OneToMany(mappedBy="project")
	private List<Group> groups;

	public Project() {
	}

	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Timestamp getRegistrationDate() {
		return this.registrationDate;
	}

	public void setRegistrationDate(Timestamp registrationDate) {
		this.registrationDate = registrationDate;
	}

	public List<Group> getGroups() {
		return this.groups;
	}

	public void setGroups(List<Group> groups) {
		this.groups = groups;
	}

	public Group addGroup(Group group) {
		getGroups().add(group);
		group.setProject(this);

		return group;
	}

	public Group removeGroup(Group group) {
		getGroups().remove(group);
		group.setProject(null);

		return group;
	}

}