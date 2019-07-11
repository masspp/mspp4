package ninja.mspp.model.entity;

import java.io.Serializable;
import javax.persistence.*;


/**
 * The persistent class for the GROUP_CHROMATOGRAMS database table.
 * 
 */
@Entity
@Table(name="GROUP_CHROMATOGRAMS")
@NamedQuery(name="GroupChromatogram.findAll", query="SELECT g FROM GroupChromatogram g")
public class GroupChromatogram implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private int id;

	@Column(name="PEAK_LIST_ID")
	private int peakListId;

	@Column(name="POINT_LIST_ID")
	private int pointListId;

	//uni-directional many-to-one association to Chromatogram
	@ManyToOne
	private Chromatogram chromatogram;

	//bi-directional many-to-one association to GroupSample
	@ManyToOne
	@JoinColumn(name="GROUP_SAMPLE_ID")
	private GroupSample groupSample;

	public GroupChromatogram() {
	}

	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getPeakListId() {
		return this.peakListId;
	}

	public void setPeakListId(int peakListId) {
		this.peakListId = peakListId;
	}

	public int getPointListId() {
		return this.pointListId;
	}

	public void setPointListId(int pointListId) {
		this.pointListId = pointListId;
	}

	public Chromatogram getChromatogram() {
		return this.chromatogram;
	}

	public void setChromatogram(Chromatogram chromatogram) {
		this.chromatogram = chromatogram;
	}

	public GroupSample getGroupSample() {
		return this.groupSample;
	}

	public void setGroupSample(GroupSample groupSample) {
		this.groupSample = groupSample;
	}

}