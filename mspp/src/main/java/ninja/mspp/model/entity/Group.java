package ninja.mspp.model.entity;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;


/**
 * The persistent class for the GROUPS database table.
 *
 */
@Entity
@Table(name="GROUPS")
@NamedQuery(name="Group.findAll", query="SELECT g FROM Group g")
public class Group implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;

	@Column
	private String color;

	@Column
	private String description;

	@Column
	private String name;

	//bi-directional many-to-one association to Project
	@ManyToOne
	private Project project;

	//bi-directional many-to-one association to GroupSample
	@OneToMany(mappedBy="group", fetch = FetchType.EAGER )
	private List<GroupSample> groupSamples;

	public Group() {
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getColor() {
		return this.color;
	}

	public void setColor(String color) {
		this.color = color;
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

	public Project getProject() {
		return this.project;
	}

	public void setProject(Project project) {
		this.project = project;
	}

	public List<GroupSample> getGroupSamples() {
		return this.groupSamples;
	}

	public void setGroupSamples(List<GroupSample> groupSamples) {
		this.groupSamples = groupSamples;
	}

	public GroupSample addGroupSample(GroupSample groupSample) {
		getGroupSamples().add(groupSample);
		groupSample.setGroup(this);

		return groupSample;
	}

	public GroupSample removeGroupSample(GroupSample groupSample) {
		getGroupSamples().remove(groupSample);
		groupSample.setGroup(null);

		return groupSample;
	}

}