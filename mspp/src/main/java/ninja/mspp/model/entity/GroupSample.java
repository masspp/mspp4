package ninja.mspp.model.entity;

import java.io.Serializable;
import javax.persistence.*;
import java.util.List;


/**
 * The persistent class for the GROUP_SAMPLES database table.
 * 
 */
@Entity
@Table(name="GROUP_SAMPLES")
@NamedQuery(name="GroupSample.findAll", query="SELECT g FROM GroupSample g")
public class GroupSample implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private int id;

	//bi-directional many-to-one association to GroupChromatogram
	@OneToMany(mappedBy="groupSample")
	private List<GroupChromatogram> groupChromatograms;

	//bi-directional many-to-one association to Group
	@ManyToOne
	private Group group;

	//uni-directional many-to-one association to Sample
	@ManyToOne
	private Sample sample;

	//bi-directional many-to-one association to GroupSpectrum
	@OneToMany(mappedBy="groupSample")
	private List<GroupSpectrum> groupSpectras;

	public GroupSample() {
	}

	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public List<GroupChromatogram> getGroupChromatograms() {
		return this.groupChromatograms;
	}

	public void setGroupChromatograms(List<GroupChromatogram> groupChromatograms) {
		this.groupChromatograms = groupChromatograms;
	}

	public GroupChromatogram addGroupChromatogram(GroupChromatogram groupChromatogram) {
		getGroupChromatograms().add(groupChromatogram);
		groupChromatogram.setGroupSample(this);

		return groupChromatogram;
	}

	public GroupChromatogram removeGroupChromatogram(GroupChromatogram groupChromatogram) {
		getGroupChromatograms().remove(groupChromatogram);
		groupChromatogram.setGroupSample(null);

		return groupChromatogram;
	}

	public Group getGroup() {
		return this.group;
	}

	public void setGroup(Group group) {
		this.group = group;
	}

	public Sample getSample() {
		return this.sample;
	}

	public void setSample(Sample sample) {
		this.sample = sample;
	}

	public List<GroupSpectrum> getGroupSpectras() {
		return this.groupSpectras;
	}

	public void setGroupSpectras(List<GroupSpectrum> groupSpectras) {
		this.groupSpectras = groupSpectras;
	}

	public GroupSpectrum addGroupSpectra(GroupSpectrum groupSpectra) {
		getGroupSpectras().add(groupSpectra);
		groupSpectra.setGroupSample(this);

		return groupSpectra;
	}

	public GroupSpectrum removeGroupSpectra(GroupSpectrum groupSpectra) {
		getGroupSpectras().remove(groupSpectra);
		groupSpectra.setGroupSample(null);

		return groupSpectra;
	}

}