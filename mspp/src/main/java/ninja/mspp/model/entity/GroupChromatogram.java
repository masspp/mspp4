package ninja.mspp.model.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQuery;
import javax.persistence.Table;


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
	private Long id;

	@Column
	private Long peakListId;

	@Column(name="POINT_LIST_ID")
	private Long pointListId;

	//uni-directional many-to-one association to Chromatogram
	@ManyToOne
	private Chromatogram chromatogram;

	//bi-directional many-to-one association to GroupSample
	@ManyToOne
	@JoinColumn(name="GROUP_SAMPLE_ID")
	private GroupSample groupSample;

	public GroupChromatogram() {
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getPeakListId() {
		return peakListId;
	}

	public void setPeakListId(Long peakListId) {
		this.peakListId = peakListId;
	}

	public Long getPointListId() {
		return pointListId;
	}

	public void setPointListId(Long pointListId) {
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